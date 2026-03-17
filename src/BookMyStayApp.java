import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("   Hotel Booking System v6.1      ");
        System.out.println("==================================");

        // Initialize components
        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();
        BookingService bookingService = new BookingService(inventory);

        // Add booking requests (FIFO)
        queue.addRequest(new Reservation("Alice", "SingleRoom"));
        queue.addRequest(new Reservation("Bob", "SingleRoom"));
        queue.addRequest(new Reservation("Charlie", "DoubleRoom"));
        queue.addRequest(new Reservation("David", "SuiteRoom"));

        // Process all requests
        bookingService.processBookings(queue);
    }
}

/**
 * Reservation (Booking Request)
 */
class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

/**
 * Booking Request Queue (FIFO)
 */
class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.add(r);
    }

    public Reservation getNextRequest() {
        return queue.poll(); // FIFO
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

/**
 * Inventory Service (State Holder)
 */
class RoomInventory {

    private HashMap<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("SingleRoom", 2);
        availability.put("DoubleRoom", 1);
        availability.put("SuiteRoom", 1);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        availability.put(type, getAvailability(type) - 1);
    }
}

/**
 * Booking Service (Core Logic)
 */
class BookingService {

    private RoomInventory inventory;

    // Tracks all allocated room IDs (global uniqueness)
    private Set<String> allocatedRoomIds = new HashSet<>();

    // Tracks room IDs per room type
    private HashMap<String, Set<String>> roomAllocations = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void processBookings(BookingRequestQueue queue) {

        System.out.println("\nProcessing Booking Requests...\n");

        while (!queue.isEmpty()) {

            Reservation request = queue.getNextRequest();

            String roomType = request.getRoomType();

            // Check availability
            if (inventory.getAvailability(roomType) > 0) {

                // Generate unique room ID
                String roomId = generateRoomId(roomType);

                // Allocate room
                allocatedRoomIds.add(roomId);

                roomAllocations
                        .computeIfAbsent(roomType, k -> new HashSet<>())
                        .add(roomId);

                // Update inventory
                inventory.decrement(roomType);

                // Confirm booking
                System.out.println("Booking CONFIRMED for "
                        + request.getGuestName()
                        + " | Room: " + roomType
                        + " | Room ID: " + roomId);

            } else {

                System.out.println("Booking FAILED for "
                        + request.getGuestName()
                        + " | No available " + roomType);
            }
        }
    }

    // Generates unique room ID
    private String generateRoomId(String roomType) {

        String roomId;

        do {
            roomId = roomType + "-" + UUID.randomUUID().toString().substring(0, 5);
        } while (allocatedRoomIds.contains(roomId));

        return roomId;
    }
}