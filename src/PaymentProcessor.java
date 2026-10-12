import java.util.Random;
import java.util.Scanner;
public class PaymentProcessor {
    private static final Random random = new Random();
    public static String pay(Scanner scanner, double amount, String[] methodOut) {
        System.out.printf("%nAmount to pay: Rs.%.2f%n", amount);
        System.out.println("Payment method: 1. Credit/Debit Card  2. UPI  3. Cash at hotel");
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.print("Enter 16-digit card number: ");
                String card = scanner.nextLine().replaceAll("\\s", "");
                if (!card.matches("\\d{16}")) {
                    System.out.println("Payment failed: invalid card number.");
                    return null;
                }
                System.out.print("Enter 3-digit CVV: ");
                if (!scanner.nextLine().trim().matches("\\d{3}")) {
                    System.out.println("Payment failed: invalid CVV.");
                    return null;
                }
                methodOut[0] = "CARD(****" + card.substring(12) + ")";
            }
            case "2" -> {
                System.out.print("Enter UPI ID (e.g. name@bank): ");
                String upi = scanner.nextLine().trim();
                if (!upi.matches("[\\w.\\-]+@[\\w]+")) {
                    System.out.println("Payment failed: invalid UPI ID.");
                    return null;
                }
                methodOut[0] = "UPI";
            }
            case "3" -> methodOut[0] = "CASH_AT_HOTEL";
            default -> {
                System.out.println("Invalid payment method.");
                return null;
            }
        }
        System.out.println("Processing payment...");
        String txn = "TXN" + (100000 + random.nextInt(900000));
        System.out.println("Payment successful! Transaction ID: " + txn);
        return txn;
    }
}
