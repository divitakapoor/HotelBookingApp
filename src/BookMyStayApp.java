
import java.util.HashMap;
import java.util.Map;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("   Welcome to Hotel Booking App   ");
        System.out.println("      Hotel Booking System v3.1   ");
        System.out.println("==================================");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Display current inventory
        inventory.displayInventory();

        // Example update
        System.out.println("\nUpdating inventory (booking 1 Single Room)...");

        inventory.updateAvailability("SingleRoom", -1);

        // Display updated inventory
        inventory.displayInventory();
    }
}

/**
 * RoomInventory class manages centralized availability of rooms
 */
class RoomInventory {

    private HashMap<String, Integer> roomAvailability;

    public RoomInventory() {

        roomAvailability = new HashMap<>();

        // Register room types with initial availability
        roomAvailability.put("SingleRoom", 5);
        roomAvailability.put("DoubleRoom", 3);
        roomAvailability.put("SuiteRoom", 2);
    }

    // Retrieve availability
    public int getAvailability(String roomType) {
        return roomAvailability.getOrDefault(roomType, 0);
    }

    // Update availability
    public void updateAvailability(String roomType, int change) {

        int current = getAvailability(roomType);
        roomAvailability.put(roomType, current + change);
    }

    // Display full inventory
    public void displayInventory() {

        System.out.println("\nCurrent Room Inventory:");

        for (Map.Entry<String, Integer> entry : roomAvailability.entrySet()) {

            System.out.println(entry.getKey() + " : " + entry.getValue() + " rooms available");
        }
    }
}