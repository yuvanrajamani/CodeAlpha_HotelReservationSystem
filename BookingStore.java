import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/** Persists bookings to bookings.csv using file I/O. */
public class BookingStore {
    private static final Path FILE = Paths.get("bookings.csv");

    public static List<Booking> load() {
        List<Booking> list = new ArrayList<>();
        if (!Files.exists(FILE)) return list;
        try {
            for (String line : Files.readAllLines(FILE)) {
                if (!line.isBlank()) list.add(Booking.fromFileLine(line));
            }
        } catch (Exception e) {
            System.out.println("Warning: could not read bookings file: " + e.getMessage());
        }
        return list;
    }

    public static void save(List<Booking> bookings) {
        List<String> lines = new ArrayList<>();
        for (Booking b : bookings) lines.add(b.toFileLine());
        try {
            Files.write(FILE, lines);
        } catch (IOException e) {
            System.out.println("Warning: could not save bookings: " + e.getMessage());
        }
    }
}
