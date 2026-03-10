import java.util.HashMap;
import java.util.Map;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("   Welcome to Hotel Booking App   ");
        System.out.println("      Hotel Booking System v4.1   ");
        System.out.println("==================================");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Initialize search service
        SearchService searchService = new SearchService(inventory);

        // Guest searches for available rooms
        searchService.searchAvailableRooms();
    }
}

/**
 * Abstract Room class (Domain Model)
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

    public void displayDetails() {
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sq.ft");
        System.out.println("Price per night: $" + price);
    }
}

/**
 * Concrete Room Types
 */
class SingleRoom extends Room {

    public SingleRoom() {
        super(1, 200, 100);
    }
}

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

/**
 * Centralized Inventory (State Holder)
 */
class RoomInventory {

    private HashMap<String, Integer> availability;

    public RoomInventory() {

        availability = new HashMap<>();

        availability.put("SingleRoom", 5);
        availability.put("DoubleRoom", 3);
        availability.put("SuiteRoom", 0); // Example unavailable room
    }

    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    public Map<String, Integer> getAllAvailability() {
        return availability;
    }
}

/**
 * Search Service (Read-Only Operations)
 */
class SearchService {

    private RoomInventory inventory;

    public SearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void searchAvailableRooms() {

        System.out.println("\nAvailable Rooms:\n");

        for (Map.Entry<String, Integer> entry : inventory.getAllAvailability().entrySet()) {

            String roomType = entry.getKey();
            int available = entry.getValue();

            // Defensive check: show only available rooms
            if (available > 0) {

                Room room = createRoom(roomType);

                System.out.println("Room Type: " + roomType);
                room.displayDetails();
                System.out.println("Available Rooms: " + available);
                System.out.println("-------------------------------");
            }
        }
    }

    // Factory method to create room objects
    private Room createRoom(String roomType) {

        switch (roomType) {
            case "SingleRoom":
                return new SingleRoom();

            case "DoubleRoom":
                return new DoubleRoom();

            case "SuiteRoom":
                return new SuiteRoom();

            default:
                return null;
        }
    }
}