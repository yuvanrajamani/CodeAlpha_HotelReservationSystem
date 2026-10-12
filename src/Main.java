import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static Hotel hotel;
    public static void main(String[] args) {
        hotel = new Hotel("CodeAlpha Grand Hotel", BookingStore.load());
        System.out.println("===== Welcome to " + hotel.getName() + " =====");
        boolean running = true;
        while (running) {
            System.out.println("\n1. View room categories & prices");
            System.out.println("2. Search available rooms");
            System.out.println("3. Book a room");
            System.out.println("4. View booking details");
            System.out.println("5. Cancel a booking");
            System.out.println("6. View all bookings");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> showCategories();
                case "2" -> searchRooms();
                case "3" -> bookRoom();
                case "4" -> viewBooking();
                case "5" -> cancelBooking();
                case "6" -> listAllBookings();
                case "0" -> {
                    BookingStore.save(hotel.getBookings());
                    System.out.println("Bookings saved. Thank you!");
                    running = false;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
    private static void showCategories() {
        System.out.println("\n--- Room Categories ---");
        for (RoomCategory c : RoomCategory.values()) {
            System.out.printf("%-9s Rs.%-6.0f/night  %s%n", c, c.getPricePerNight(), c.getDescription());
        }
    }
    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (yyyy-mm-dd): ");
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format.");
            }
        }
    }
    private static RoomCategory readCategory(boolean allowAll) {
        while (true) {
            System.out.print("Category (STANDARD / DELUXE / SUITE" + (allowAll ? " / ALL" : "") + "): ");
            String s = scanner.nextLine().trim().toUpperCase();
            if (allowAll && s.equals("ALL")) return null;
            try {
                return RoomCategory.valueOf(s);
            } catch (IllegalArgumentException e) {
                System.out.println("Unknown category, please try again.");
            }
        }
    }
    private static LocalDate[] readDates() {
        LocalDate in = readDate("Check-in date");
        LocalDate out = readDate("Check-out date");
        if (in.isBefore(LocalDate.now())) {
            System.out.println("Check-in cannot be in the past.");
            return null;
        }
        if (!out.isAfter(in)) {
            System.out.println("Check-out must be after check-in.");
            return null;
        }
        return new LocalDate[]{in, out};
    }
    private static void searchRooms() {
        RoomCategory cat = readCategory(true);
        LocalDate[] d = readDates();
        if (d == null) return;
        List<Room> available = hotel.searchAvailable(cat, d[0], d[1]);
        if (available.isEmpty()) {
            System.out.println("No rooms available for those dates.");
            return;
        }
        System.out.println("\nAvailable rooms:");
        for (Room r : available) System.out.println("  " + r);
    }
    private static void bookRoom() {
        RoomCategory cat = readCategory(false);
        LocalDate[] d = readDates();
        if (d == null) return;
        List<Room> available = hotel.searchAvailable(cat, d[0], d[1]);
        if (available.isEmpty()) {
            System.out.println("Sorry, no " + cat + " rooms are available for those dates.");
            return;
        }
        System.out.println("\nAvailable " + cat + " rooms:");
        for (Room r : available) System.out.println("  " + r);
        System.out.print("Enter room number to book: ");
        int roomNo;
        try {
            roomNo = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid room number.");
            return;
        }
        Room room = null;
        for (Room r : available) if (r.getNumber() == roomNo) room = r;
        if (room == null) {
            System.out.println("That room is not in the available list.");
            return;
        }
        System.out.print("Guest name: ");
        String name = scanner.nextLine().trim().replace(",", " ");
        System.out.print("Phone number: ");
        String phone = scanner.nextLine().trim().replace(",", "");
        if (name.isEmpty() || phone.isEmpty()) {
            System.out.println("Name and phone are required.");
            return;
        }
        long nights = java.time.temporal.ChronoUnit.DAYS.between(d[0], d[1]);
        double total = nights * room.getPrice();
        System.out.printf("%n%d night(s) x Rs.%.0f = Rs.%.2f%n", nights, room.getPrice(), total);

        String[] method = new String[1];
        String txn = PaymentProcessor.pay(scanner, total, method);
        if (txn == null) {
            System.out.println("Booking not completed.");
            return;
        }
        Booking b = new Booking(hotel.nextBookingId(), name, phone, room.getNumber(), cat,
                d[0], d[1], total, method[0], txn, Booking.Status.CONFIRMED, 0);
        hotel.addBooking(b);
        BookingStore.save(hotel.getBookings());
        System.out.println("\nBooking confirmed!");
        b.printDetails();
    }
    private static void viewBooking() {
        System.out.print("Enter booking ID (or guest name): ");
        String q = scanner.nextLine().trim();
        Booking b = hotel.findBooking(q);
        if (b != null) {
            b.printDetails();
            return;
        }
        List<Booking> found = hotel.findByGuest(q);
        if (found.isEmpty()) System.out.println("No booking found.");
        for (Booking x : found) x.printDetails();
    }
    private static void cancelBooking() {
        System.out.print("Enter booking ID to cancel: ");
        Booking b = hotel.findBooking(scanner.nextLine().trim());
        if (b == null) {
            System.out.println("Booking not found.");
            return;
        }
        if (!b.isActive()) {
            System.out.println("That booking is already cancelled.");
            return;
        }
        System.out.println("Refund policy: 100% if 2+ days before check-in, 50% if 1 day before, 0% otherwise.");
        System.out.print("Confirm cancellation of " + b.getId() + "? (y/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("Cancellation aborted.");
            return;
        }
        double refund = hotel.cancel(b);
        BookingStore.save(hotel.getBookings());
        System.out.printf("Booking %s cancelled. Refund: Rs.%.2f%n", b.getId(), refund);
    }
    private static void listAllBookings() {
        if (hotel.getBookings().isEmpty()) {
            System.out.println("No bookings yet.");
            return;
        }
        System.out.println("\n--- All Bookings ---");
        System.out.printf("%-8s %-14s %-5s %-9s %-11s %-11s %-10s%n",
                "ID", "Guest", "Room", "Category", "Check-in", "Check-out", "Status");
        for (Booking b : hotel.getBookings()) {
            System.out.printf("%-8s %-14s %-5d %-9s %-11s %-11s %-10s%n", b.getId(), b.getGuestName(),
                    b.getRoomNumber(), b.getCategory(), b.getCheckIn(), b.getCheckOut(), b.getStatus());
        }
    }
}
