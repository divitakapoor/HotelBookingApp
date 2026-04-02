import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println(" Hotel Booking System v11.0                   ");
        System.out.println(" Concurrent Booking Simulation (Thread Safety)");
        System.out.println("==============================================");

        // Shared inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Standard", 2);
        inventory.addRoomType("Deluxe", 1);

        // Shared booking queue
        BookingQueue bookingQueue = new BookingQueue();

        // Add concurrent booking requests
        bookingQueue.addRequest(new BookingRequest("Divita Kapoor", "Standard"));
        bookingQueue.addRequest(new BookingRequest("Aarav Sharma", "Standard"));
        bookingQueue.addRequest(new BookingRequest("Meera Singh", "Standard")); // should fail if rooms finish
        bookingQueue.addRequest(new BookingRequest("Rohan Das", "Deluxe"));
        bookingQueue.addRequest(new BookingRequest("Priya Nair", "Deluxe"));    // should fail if already booked

        // Create processor threads
        Thread processor1 = new Thread(new ConcurrentBookingProcessor("Processor-1", bookingQueue, inventory));
        Thread processor2 = new Thread(new ConcurrentBookingProcessor("Processor-2", bookingQueue, inventory));
        Thread processor3 = new Thread(new ConcurrentBookingProcessor("Processor-3", bookingQueue, inventory));

        // Start threads
        processor1.start();
        processor2.start();
        processor3.start();

        // Wait for all threads to complete
        try {
            processor1.join();
            processor2.join();
            processor3.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted.");
        }

        // Final inventory state
        System.out.println("\n==============================================");
        System.out.println(" Final Inventory State                        ");
        System.out.println("==============================================");
        inventory.displayInventory();

        System.out.println("\nSimulation completed safely without double allocation.");
    }
}

/**
 * Booking Request
 * Represents a guest booking request
 */
class BookingRequest {

    private String guestName;
    private String roomType;

    public BookingRequest(String guestName, String roomType) {
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
 * Shared Booking Queue
 * Thread-safe request retrieval
 */
class BookingQueue {

    private Queue<BookingRequest> requestQueue;

    public BookingQueue() {
        requestQueue = new LinkedList<>();
    }

    // Add request to queue
    public synchronized void addRequest(BookingRequest request) {
        requestQueue.offer(request);
        System.out.println("Request Added: " + request.getGuestName()
                + " -> " + request.getRoomType());
    }

    // Retrieve next request safely
    public synchronized BookingRequest getNextRequest() {
        return requestQueue.poll();
    }

    // Check if queue is empty
    public synchronized boolean isEmpty() {
        return requestQueue.isEmpty();
    }
}

/**
 * Room Inventory
 * Shared mutable state protected using synchronization
 */
class RoomInventory {

    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
    }

    public void addRoomType(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }

    /**
     * Critical Section
     * Only one thread can allocate at a time
     */
    public synchronized boolean allocateRoom(String guestName, String roomType, String processorName) {

        Integer available = roomAvailability.get(roomType);

        if (available == null) {
            System.out.println(processorName + " -> Booking Failed for " + guestName
                    + " | Invalid Room Type: " + roomType);
            return false;
        }

        if (available > 0) {
            System.out.println(processorName + " processing booking for " + guestName
                    + " (" + roomType + ")");

            // Simulate processing delay (to expose race condition risk)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(processorName + " interrupted during processing.");
            }

            roomAvailability.put(roomType, available - 1);

            System.out.println(processorName + " -> Booking Confirmed for "
                    + guestName + " | Room Type: " + roomType);
            return true;
        } else {
            System.out.println(processorName + " -> Booking Failed for "
                    + guestName + " | No " + roomType + " rooms available.");
            return false;
        }
    }

    public synchronized void displayInventory() {
        System.out.println("\nCurrent Room Inventory:");
        for (Map.Entry<String, Integer> entry : roomAvailability.entrySet()) {
            System.out.println(entry.getKey() + " Rooms Available: " + entry.getValue());
        }
    }
}

/**
 * Concurrent Booking Processor
 * Multiple threads process shared booking requests
 */
class ConcurrentBookingProcessor implements Runnable {

    private String processorName;
    private BookingQueue bookingQueue;
    private RoomInventory inventory;

    public ConcurrentBookingProcessor(String processorName, BookingQueue bookingQueue, RoomInventory inventory) {
        this.processorName = processorName;
        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
    }

    @Override
    public void run() {

        while (true) {

            BookingRequest request;

            // Safely retrieve request from shared queue
            synchronized (bookingQueue) {
                if (bookingQueue.isEmpty()) {
                    break;
                }
                request = bookingQueue.getNextRequest();
            }

            if (request != null) {
                inventory.allocateRoom(request.getGuestName(), request.getRoomType(), processorName);
            }
        }

        System.out.println(processorName + " finished processing.");
    }
}