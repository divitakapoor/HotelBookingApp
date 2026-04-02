import java.io.*;
import java.util.*;

public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println(" Hotel Booking System v12.0                   ");
        System.out.println(" Data Persistence & System Recovery           ");
        System.out.println("==============================================");

        String fileName = "hotel_system_data.ser";

        // -----------------------------------------
        // Step 1: Try to recover saved system state
        // -----------------------------------------
        HotelSystemState systemState = PersistenceService.loadState(fileName);

        if (systemState == null) {
            System.out.println("\nNo previous saved state found. Starting fresh...");

            // Create fresh state
            systemState = new HotelSystemState();

            systemState.getInventory().addRoomType("Standard", 2);
            systemState.getInventory().addRoomType("Deluxe", 1);
            systemState.getInventory().addRoomType("Suite", 1);
        } else {
            System.out.println("\nSystem state restored successfully from file.");
        }

        // -----------------------------------------
        // Step 2: Display recovered/current state
        // -----------------------------------------
        System.out.println("\nRecovered Inventory:");
        systemState.getInventory().displayInventory();

        System.out.println("\nRecovered Booking History:");
        systemState.getBookingHistory().displayBookings();

        // -----------------------------------------
        // Step 3: Simulate new bookings after recovery
        // -----------------------------------------
        BookingService bookingService = new BookingService(
                systemState.getInventory(),
                systemState.getBookingHistory()
        );

        System.out.println("\n--- New Booking Operations ---");

        try {
            bookingService.bookRoom("Divita Kapoor", "Standard");
            bookingService.bookRoom("Aarav Sharma", "Deluxe");
        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
        }

        // -----------------------------------------
        // Step 4: Display updated state
        // -----------------------------------------
        System.out.println("\nUpdated Inventory:");
        systemState.getInventory().displayInventory();

        System.out.println("\nUpdated Booking History:");
        systemState.getBookingHistory().displayBookings();

        // -----------------------------------------
        // Step 5: Save state before shutdown
        // -----------------------------------------
        PersistenceService.saveState(systemState, fileName);

        System.out.println("\nSystem shutdown complete. State saved for recovery.");
    }
}

/**
 * Custom Exception
 */
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

/**
 * Reservation class
 * Serializable so it can be saved to file
 */
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
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

    public void displayReservation() {
        System.out.println("----------------------------------");
        System.out.println("Reservation ID : " + reservationId);
        System.out.println("Guest Name     : " + guestName);
        System.out.println("Room Type      : " + roomType);
    }
}

/**
 * Room Inventory
 * Serializable for persistence
 */
class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
    }

    public void addRoomType(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }

    public boolean isValidRoomType(String roomType) {
        return roomAvailability.containsKey(roomType);
    }

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

    public void displayInventory() {
        if (roomAvailability.isEmpty()) {
            System.out.println("No inventory data available.");
            return;
        }

        for (Map.Entry<String, Integer> entry : roomAvailability.entrySet()) {
            System.out.println(entry.getKey() + " Rooms Available: " + entry.getValue());
        }
    }
}

/**
 * Booking History
 * Serializable for persistence
 */
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> reservations;

    public BookingHistory() {
        reservations = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        System.out.println("Booking Confirmed: " + reservation.getReservationId());
    }

    public void displayBookings() {
        if (reservations.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Reservation reservation : reservations) {
            reservation.displayReservation();
        }
    }

    public int getBookingCount() {
        return reservations.size();
    }
}

/**
 * Combined system state
 * Holds all important persistent data
 */
class HotelSystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    private RoomInventory inventory;
    private BookingHistory bookingHistory;

    public HotelSystemState() {
        inventory = new RoomInventory();
        bookingHistory = new BookingHistory();
    }

    public RoomInventory getInventory() {
        return inventory;
    }

    public BookingHistory getBookingHistory() {
        return bookingHistory;
    }
}

/**
 * Booking Service
 * Works on recovered or fresh state
 */
class BookingService {

    private RoomInventory inventory;
    private BookingHistory history;
    private int bookingCounter;

    public BookingService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
        this.bookingCounter = history.getBookingCount() + 101;
    }

    public void bookRoom(String guestName, String roomType) throws InvalidBookingException {

        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        if (roomType == null || roomType.trim().isEmpty()) {
            throw new InvalidBookingException("Room type cannot be empty.");
        }

        inventory.reserveRoom(roomType);

        String reservationId = "RES-" + bookingCounter++;
        Reservation reservation = new Reservation(reservationId, guestName, roomType);
        history.addReservation(reservation);
    }
}

/**
 * Persistence Service
 * Handles save and load operations
 */
class PersistenceService {

    public static void saveState(HotelSystemState state, String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(state);
            System.out.println("\nSystem state saved successfully to file: " + fileName);
        } catch (IOException e) {
            System.out.println("\nError saving system state: " + e.getMessage());
        }
    }

    public static HotelSystemState loadState(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (HotelSystemState) in.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("\nPersistence file not found. Fresh system startup.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\nError loading saved state. Starting with safe empty state.");
        }
        return null;
    }
}