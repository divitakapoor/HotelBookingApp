import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println(" Hotel Booking System v9.0              ");
        System.out.println(" Error Handling & Validation            ");
        System.out.println("========================================");

        // Initialize room inventory
        RoomInventory inventory = new RoomInventory();

        // Add available rooms
        inventory.addRoomType("Standard", 2);
        inventory.addRoomType("Deluxe", 1);
        inventory.addRoomType("Suite", 1);

        // Display initial inventory
        inventory.displayInventory();

        // Booking service
        BookingService bookingService = new BookingService(inventory);

        // --------------------------------------------
        // Test Case 1: Valid booking
        // --------------------------------------------
        System.out.println("\n--- Booking Attempt 1 ---");
        try {
            bookingService.bookRoom("Divita Kapoor", "Standard");
        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
        }

        // --------------------------------------------
        // Test Case 2: Invalid room type (case-sensitive)
        // --------------------------------------------
        System.out.println("\n--- Booking Attempt 2 ---");
        try {
            bookingService.bookRoom("Aarav Sharma", "standard"); // invalid (case-sensitive)
        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
        }

        // --------------------------------------------
        // Test Case 3: Invalid room type
        // --------------------------------------------
        System.out.println("\n--- Booking Attempt 3 ---");
        try {
            bookingService.bookRoom("Meera Singh", "Premium"); // invalid room
        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
        }

        // --------------------------------------------
        // Test Case 4: Booking until room unavailable
        // --------------------------------------------
        System.out.println("\n--- Booking Attempt 4 ---");
        try {
            bookingService.bookRoom("Rohan Das", "Deluxe");
            bookingService.bookRoom("Priya Nair", "Deluxe"); // should fail
        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
        }

        // --------------------------------------------
        // Final inventory check
        // --------------------------------------------
        System.out.println("\n========================================");
        System.out.println(" Final Room Inventory Status            ");
        System.out.println("========================================");
        inventory.displayInventory();

        System.out.println("\nSystem continued running safely after errors.");
    }
}

/**
 * Custom Exception for invalid booking scenarios
 */
class InvalidBookingException extends Exception {

    public InvalidBookingException(String message) {
        super(message);
    }
}

/**
 * Room Inventory class
 * Manages room availability
 */
class RoomInventory {

    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
    }

    // Add room type and count
    public void addRoomType(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }

    // Check if room type exists
    public boolean isValidRoomType(String roomType) {
        return roomAvailability.containsKey(roomType);
    }

    // Check availability
    public boolean isRoomAvailable(String roomType) {
        return roomAvailability.get(roomType) > 0;
    }

    // Reduce room count safely
    public void reserveRoom(String roomType) throws InvalidBookingException {

        if (!isValidRoomType(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }

        int available = roomAvailability.get(roomType);

        if (available <= 0) {
            throw new InvalidBookingException("No rooms available for type: " + roomType);
        }

        roomAvailability.put(roomType, available - 1);
    }

    // Display inventory
    public void displayInventory() {
        System.out.println("\nCurrent Room Inventory:");
        for (Map.Entry<String, Integer> entry : roomAvailability.entrySet()) {
            System.out.println(entry.getKey() + " Rooms Available: " + entry.getValue());
        }
    }
}

/**
 * Booking Service class
 * Validates and processes bookings
 */
class BookingService {

    private RoomInventory inventory;
    private int bookingCounter = 101;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void bookRoom(String guestName, String roomType) throws InvalidBookingException {

        // Validate guest name
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        // Validate room type
        if (roomType == null || roomType.trim().isEmpty()) {
            throw new InvalidBookingException("Room type cannot be empty.");
        }

        if (!inventory.isValidRoomType(roomType)) {
            throw new InvalidBookingException(
                    "Room type '" + roomType + "' is invalid. Please enter a valid room type exactly as defined."
            );
        }

        // Reserve room safely
        inventory.reserveRoom(roomType);

        // Generate booking ID
        String bookingId = "RES-" + bookingCounter++;
        System.out.println("Booking Successful!");
        System.out.println("Reservation ID : " + bookingId);
        System.out.println("Guest Name     : " + guestName);
        System.out.println("Room Type      : " + roomType);
    }
}