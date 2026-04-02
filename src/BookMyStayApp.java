import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println(" Hotel Booking System v10.0             ");
        System.out.println(" Booking Cancellation & Rollback        ");
        System.out.println("========================================");

        // Initialize room inventory
        RoomInventory inventory = new RoomInventory();

        inventory.addRoomType("Standard", 2);
        inventory.addRoomType("Deluxe", 1);
        inventory.addRoomType("Suite", 1);

        // Display initial inventory
        inventory.displayInventory();

        // Booking history to store active bookings
        BookingHistory bookingHistory = new BookingHistory();

        // Booking service
        BookingService bookingService = new BookingService(inventory, bookingHistory);

        // Cancellation service
        CancellationService cancellationService = new CancellationService(inventory, bookingHistory);

        // -----------------------------------------
        // Step 1: Make some valid bookings
        // -----------------------------------------
        System.out.println("\n--- Booking Confirmations ---");

        try {
            bookingService.bookRoom("Divita Kapoor", "Standard");
            bookingService.bookRoom("Aarav Sharma", "Deluxe");
        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
        }

        // Show active bookings
        bookingHistory.displayBookings();

        // Show inventory after booking
        System.out.println("\nInventory After Booking:");
        inventory.displayInventory();

        // -----------------------------------------
        // Step 2: Cancel a valid booking
        // -----------------------------------------
        System.out.println("\n--- Cancellation Attempt 1 ---");
        try {
            cancellationService.cancelBooking("RES-101");
        } catch (InvalidBookingException e) {
            System.out.println("Cancellation Failed: " + e.getMessage());
        }

        // Show active bookings after cancellation
        bookingHistory.displayBookings();

        // Show inventory after cancellation
        System.out.println("\nInventory After Cancellation:");
        inventory.displayInventory();

        // -----------------------------------------
        // Step 3: Attempt invalid cancellation
        // -----------------------------------------
        System.out.println("\n--- Cancellation Attempt 2 ---");
        try {
            cancellationService.cancelBooking("RES-999"); // invalid
        } catch (InvalidBookingException e) {
            System.out.println("Cancellation Failed: " + e.getMessage());
        }

        // -----------------------------------------
        // Step 4: Attempt duplicate cancellation
        // -----------------------------------------
        System.out.println("\n--- Cancellation Attempt 3 ---");
        try {
            cancellationService.cancelBooking("RES-101"); // already cancelled
        } catch (InvalidBookingException e) {
            System.out.println("Cancellation Failed: " + e.getMessage());
        }

        System.out.println("\nSystem state remained consistent after rollback operations.");
    }
}

/**
 * Custom Exception for booking errors
 */
class InvalidBookingException extends Exception {

    public InvalidBookingException(String message) {
        super(message);
    }
}

/**
 * Reservation class
 * Represents a confirmed booking
 */
class Reservation {

    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean isCancelled;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isCancelled = false;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void cancel() {
        isCancelled = true;
    }

    public void displayReservation() {
        System.out.println("----------------------------------");
        System.out.println("Reservation ID : " + reservationId);
        System.out.println("Guest Name     : " + guestName);
        System.out.println("Room Type      : " + roomType);
        System.out.println("Allocated Room : " + roomId);
        System.out.println("Status         : " + (isCancelled ? "Cancelled" : "Confirmed"));
    }
}

/**
 * Room Inventory
 * Tracks available rooms and allocated room IDs
 */
class RoomInventory {

    private Map<String, Integer> roomAvailability;
    private Map<String, Stack<String>> availableRoomIds;
    private Map<String, Integer> roomCounters;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        availableRoomIds = new HashMap<>();
        roomCounters = new HashMap<>();
    }

    // Add room type and generate room IDs
    public void addRoomType(String roomType, int count) {
        roomAvailability.put(roomType, count);
        roomCounters.put(roomType, count);

        Stack<String> roomStack = new Stack<>();
        for (int i = count; i >= 1; i--) {
            roomStack.push(roomType.substring(0, 3).toUpperCase() + "-" + i);
        }
        availableRoomIds.put(roomType, roomStack);
    }

    // Check valid room type
    public boolean isValidRoomType(String roomType) {
        return roomAvailability.containsKey(roomType);
    }

    // Allocate room and reduce inventory
    public String allocateRoom(String roomType) throws InvalidBookingException {

        if (!isValidRoomType(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }

        if (roomAvailability.get(roomType) <= 0) {
            throw new InvalidBookingException("No rooms available for type: " + roomType);
        }

        Stack<String> roomStack = availableRoomIds.get(roomType);

        if (roomStack.isEmpty()) {
            throw new InvalidBookingException("No room IDs available for rollback-safe allocation.");
        }

        String allocatedRoomId = roomStack.pop();
        roomAvailability.put(roomType, roomAvailability.get(roomType) - 1);

        return allocatedRoomId;
    }

    // Release room back to inventory (rollback)
    public void releaseRoom(String roomType, String roomId) throws InvalidBookingException {

        if (!isValidRoomType(roomType)) {
            throw new InvalidBookingException("Cannot rollback. Invalid room type: " + roomType);
        }

        availableRoomIds.get(roomType).push(roomId);
        roomAvailability.put(roomType, roomAvailability.get(roomType) + 1);
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
 * Booking History
 * Stores all reservations
 */
class BookingHistory {

    private Map<String, Reservation> reservations;

    public BookingHistory() {
        reservations = new LinkedHashMap<>();
    }

    // Add confirmed reservation
    public void addReservation(Reservation reservation) {
        reservations.put(reservation.getReservationId(), reservation);
        System.out.println("Booking Confirmed: " + reservation.getReservationId());
    }

    // Get reservation by ID
    public Reservation getReservation(String reservationId) {
        return reservations.get(reservationId);
    }

    // Display all bookings
    public void displayBookings() {
        System.out.println("\n==================================");
        System.out.println("        BOOKING HISTORY           ");
        System.out.println("==================================");

        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }

        for (Reservation reservation : reservations.values()) {
            reservation.displayReservation();
        }
    }
}

/**
 * Booking Service
 * Confirms bookings
 */
class BookingService {

    private RoomInventory inventory;
    private BookingHistory history;
    private int bookingCounter = 101;

    public BookingService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void bookRoom(String guestName, String roomType) throws InvalidBookingException {

        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        if (roomType == null || roomType.trim().isEmpty()) {
            throw new InvalidBookingException("Room type cannot be empty.");
        }

        if (!inventory.isValidRoomType(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }

        String roomId = inventory.allocateRoom(roomType);
        String reservationId = "RES-" + bookingCounter++;

        Reservation reservation = new Reservation(reservationId, guestName, roomType, roomId);
        history.addReservation(reservation);
    }
}

/**
 * Cancellation Service
 * Handles booking cancellation and rollback
 */
class CancellationService {

    private RoomInventory inventory;
    private BookingHistory history;

    public CancellationService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void cancelBooking(String reservationId) throws InvalidBookingException {

        Reservation reservation = history.getReservation(reservationId);

        // Validate existence
        if (reservation == null) {
            throw new InvalidBookingException("Reservation ID " + reservationId + " does not exist.");
        }

        // Prevent duplicate cancellation
        if (reservation.isCancelled()) {
            throw new InvalidBookingException("Reservation ID " + reservationId + " is already cancelled.");
        }

        // Rollback room allocation
        inventory.releaseRoom(reservation.getRoomType(), reservation.getRoomId());

        // Update booking status
        reservation.cancel();

        System.out.println("Cancellation Successful for Reservation: " + reservationId);
        System.out.println("Released Room ID: " + reservation.getRoomId());
    }
}