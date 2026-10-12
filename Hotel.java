import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Holds rooms and bookings; handles search, booking and cancellation. */
public class Hotel {
    private final String name;
    private final List<Room> rooms = new ArrayList<>();
    private final List<Booking> bookings;
    private int nextBookingNumber;

    public Hotel(String name, List<Booking> existingBookings) {
        this.name = name;
        this.bookings = existingBookings;
        for (int i = 101; i <= 104; i++) rooms.add(new Room(i, RoomCategory.STANDARD));
        for (int i = 201; i <= 203; i++) rooms.add(new Room(i, RoomCategory.DELUXE));
        for (int i = 301; i <= 302; i++) rooms.add(new Room(i, RoomCategory.SUITE));
        nextBookingNumber = 1000 + bookings.size() + 1;
    }

    public String getName() { return name; }
    public List<Room> getRooms() { return rooms; }
    public List<Booking> getBookings() { return bookings; }

    /** Finds rooms free for the whole date range; category may be null for all categories. */
    public List<Room> searchAvailable(RoomCategory category, LocalDate in, LocalDate out) {
        List<Room> result = new ArrayList<>();
        for (Room r : rooms) {
            if (category != null && r.getCategory() != category) continue;
            boolean free = true;
            for (Booking b : bookings) {
                if (b.overlaps(r.getNumber(), in, out)) {
                    free = false;
                    break;
                }
            }
            if (free) result.add(r);
        }
        return result;
    }

    public Room findRoom(int number) {
        for (Room r : rooms) if (r.getNumber() == number) return r;
        return null;
    }

    public String nextBookingId() {
        return "BK" + (nextBookingNumber++);
    }

    public void addBooking(Booking b) {
        bookings.add(b);
    }

    public Booking findBooking(String id) {
        for (Booking b : bookings) if (b.getId().equalsIgnoreCase(id)) return b;
        return null;
    }

    public List<Booking> findByGuest(String name) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getGuestName().toLowerCase().contains(name.toLowerCase())) result.add(b);
        }
        return result;
    }

    /**
     * Cancels a booking. Refund policy: 100% if 2+ days before check-in,
     * 50% if less than 2 days, 0% on/after check-in date.
     * Returns the refund amount.
     */
    public double cancel(Booking b) {
        long daysBefore = ChronoUnit.DAYS.between(LocalDate.now(), b.getCheckIn());
        double refund;
        if (daysBefore >= 2) refund = b.getTotalAmount();
        else if (daysBefore >= 1) refund = b.getTotalAmount() * 0.5;
        else refund = 0;
        b.cancel(refund);
        return refund;
    }
}
