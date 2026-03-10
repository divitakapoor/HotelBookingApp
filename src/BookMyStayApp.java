import java.util.LinkedList;
import java.util.Queue;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("   Welcome to Hotel Booking App   ");
        System.out.println("      Hotel Booking System v5.1   ");
        System.out.println("==================================");

        // Initialize booking queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Guests submit booking requests
        bookingQueue.addRequest(new Reservation("Alice", "SingleRoom"));
        bookingQueue.addRequest(new Reservation("Bob", "DoubleRoom"));
        bookingQueue.addRequest(new Reservation("Charlie", "SuiteRoom"));

        // Display queue state
        bookingQueue.displayQueue();
    }
}

/**
 * Reservation class
 * Represents a guest booking request
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
 * BookingRequestQueue manages incoming booking requests
 */
class BookingRequestQueue {

    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    // Add booking request to queue
    public void addRequest(Reservation reservation) {

        requestQueue.add(reservation);

        System.out.println("Booking request added for "
                + reservation.getGuestName()
                + " (" + reservation.getRoomType() + ")");
    }

    // Display queued requests
    public void displayQueue() {

        System.out.println("\nCurrent Booking Request Queue:\n");

        for (Reservation r : requestQueue) {

            System.out.println("Guest: " + r.getGuestName()
                    + " | Room Type: " + r.getRoomType());
        }
    }
}