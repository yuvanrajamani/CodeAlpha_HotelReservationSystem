import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
public class Booking {
    public enum Status { CONFIRMED, CANCELLED }
    private final String id;
    private final String guestName;
    private final String phone;
    private final int roomNumber;
    private final RoomCategory category;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final double totalAmount;
    private final String paymentMethod;
    private final String transactionId;
    private Status status;
    private double refundAmount;
    public Booking(String id, String guestName, String phone, int roomNumber, RoomCategory category,
                   LocalDate checkIn, LocalDate checkOut, double totalAmount,
                   String paymentMethod, String transactionId, Status status, double refundAmount) {
        this.id = id;
        this.guestName = guestName;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.category = category;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;
        this.refundAmount = refundAmount;
    }
    public String getId() { return id; }
    public String getGuestName() { return guestName; }
    public String getPhone() { return phone; }
    public int getRoomNumber() { return roomNumber; }
    public RoomCategory getCategory() { return category; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public Status getStatus() { return status; }
    public double getRefundAmount() { return refundAmount; }
    public long getNights() { return ChronoUnit.DAYS.between(checkIn, checkOut); }
    public boolean isActive() { return status == Status.CONFIRMED; }

    public void cancel(double refund) {
        this.status = Status.CANCELLED;
        this.refundAmount = refund;
    }
    public boolean overlaps(int room, LocalDate in, LocalDate out) {
        return isActive() && roomNumber == room && in.isBefore(checkOut) && out.isAfter(checkIn);
    }
    public String toFileLine() {
        return String.join(",", id, guestName, phone, String.valueOf(roomNumber), category.name(),
                checkIn.toString(), checkOut.toString(), String.valueOf(totalAmount),
                paymentMethod, transactionId, status.name(), String.valueOf(refundAmount));
    }
    public static Booking fromFileLine(String line) {
        String[] f = line.split(",");
        return new Booking(f[0], f[1], f[2], Integer.parseInt(f[3]), RoomCategory.valueOf(f[4]),
                LocalDate.parse(f[5]), LocalDate.parse(f[6]), Double.parseDouble(f[7]),
                f[8], f[9], Status.valueOf(f[10]), Double.parseDouble(f[11]));
    }

    public void printDetails() {
        System.out.println("\n============ BOOKING DETAILS ============");
        System.out.println("Booking ID   : " + id);
        System.out.println("Status       : " + status);
        System.out.println("Guest        : " + guestName + " (" + phone + ")");
        System.out.println("Room         : " + roomNumber + " (" + category + ")");
        System.out.println("Check-in     : " + checkIn);
        System.out.println("Check-out    : " + checkOut + " (" + getNights() + " night(s))");
        System.out.printf("Total paid   : Rs.%.2f via %s%n", totalAmount, paymentMethod);
        System.out.println("Transaction  : " + transactionId);
        if (status == Status.CANCELLED) System.out.printf("Refunded     : Rs.%.2f%n", refundAmount);
        System.out.println("=========================================");
    }
}
