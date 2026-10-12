# CodeAlpha_HotelReservationSystem

A console-based hotel reservation system in Java (CodeAlpha Java Internship, Task 4).

## Features
- Room categorization: **Standard**, **Deluxe**, **Suite** (different prices and descriptions)
- Search available rooms by category and date range (overlapping bookings are detected)
- Book a room with guest details and automatic price calculation
- Simulated payment (Card / UPI / Cash at hotel) with input validation and a transaction ID
- Cancel reservations with a refund policy (100% / 50% / 0% depending on how close the check-in date is)
- View booking details by booking ID or guest name, and list all bookings
- File I/O persistence: all bookings are saved to `bookings.csv` and reloaded on startup

## OOP Design
| Class | Responsibility |
|---|---|
| `RoomCategory` (enum) | Category, price per night, description |
| `Room` | Room number + category |
| `Booking` | Reservation data, overlap check, file (de)serialisation |
| `Hotel` | Rooms, bookings, search / book / cancel logic |
| `PaymentProcessor` | Payment simulation |
| `BookingStore` | Reads/writes `bookings.csv` |
| `Main` | Menu-driven console UI |

## How to Run
Requires JDK 17+.
```bash
cd src
javac *.java
java Main
```
Note: a `bookings.csv` file is created in the folder you run the program from.

## Sample Flow
1. Choose **Book a room** -> category `DELUXE`, dates `2026-12-20` to `2026-12-23`
2. Pick room `201`, enter guest details
3. Pay by UPI -> booking confirmed with ID `BK1001`
4. Restart the program: the booking is still there
5. Cancel the booking and see the refund amount
