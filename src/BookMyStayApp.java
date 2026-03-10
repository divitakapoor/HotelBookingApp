public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("   Welcome to Hotel Booking App   ");
        System.out.println("      Hotel Booking System v2.1   ");
        System.out.println("==================================");

        // Creating room objects (Polymorphism)
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // Static availability variables
        int singleRoomAvailability = 5;
        int doubleRoomAvailability = 3;
        int suiteRoomAvailability = 2;

        System.out.println("\nRoom Details & Availability\n");

        singleRoom.displayRoomDetails();
        System.out.println("Available Rooms: " + singleRoomAvailability);

        System.out.println();

        doubleRoom.displayRoomDetails();
        System.out.println("Available Rooms: " + doubleRoomAvailability);

        System.out.println();

        suiteRoom.displayRoomDetails();
        System.out.println("Available Rooms: " + suiteRoomAvailability);

        System.out.println("\nApplication terminated.");
    }
}

/**
 * Abstract class representing a generic Room
 */
abstract class Room {

    protected int beds;
    protected int size;
    protected double price;

    public Room(int beds, int size, double price) {
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public void displayRoomDetails() {
        System.out.println("Beds: " + beds);
        System.out.println("Room Size: " + size + " sq.ft");
        System.out.println("Price per night: $" + price);
    }
}

/**
 * Single Room Implementation
 */
class SingleRoom extends Room {

    public SingleRoom() {
        super(1, 200, 100);
    }
}

/**
 * Double Room Implementation
 */
class DoubleRoom extends Room {

    public DoubleRoom() {
        super(2, 350, 180);
    }
}
class SuiteRoom extends Room {
    public SuiteRoom() {
        super(3, 500, 350);
    }
}