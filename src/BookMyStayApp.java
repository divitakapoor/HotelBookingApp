import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("   Hotel Booking System v7.1      ");
        System.out.println("==================================");

        // Assume reservation IDs already exist (from Use Case 6)
        String reservationId1 = "RES-101";
        String reservationId2 = "RES-102";

        // Initialize Add-On Service Manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Create services
        Service breakfast = new Service("Breakfast", 20);
        Service spa = new Service("Spa", 50);
        Service wifi = new Service("WiFi", 10);

        // Guest selects services
        manager.addService(reservationId1, breakfast);
        manager.addService(reservationId1, wifi);

        manager.addService(reservationId2, spa);

        // Display services and cost
        manager.displayServices(reservationId1);
        manager.displayServices(reservationId2);
    }
}

/**
 * Service class (Add-On)
 */
class Service {

    private String name;
    private double cost;

    public Service(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }
}

/**
 * Add-On Service Manager
 * Maps reservation → list of services
 */
class AddOnServiceManager {

    private Map<String, List<Service>> reservationServices;

    public AddOnServiceManager() {
        reservationServices = new HashMap<>();
    }

    // Add service to reservation
    public void addService(String reservationId, Service service) {

        reservationServices
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);

        System.out.println("Added " + service.getName()
                + " to Reservation " + reservationId);
    }

    // Display services and total cost
    public void displayServices(String reservationId) {

        System.out.println("\nServices for Reservation: " + reservationId);

        List<Service> services = reservationServices.get(reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("No services selected.");
            return;
        }

        double totalCost = 0;

        for (Service s : services) {
            System.out.println("- " + s.getName() + " ($" + s.getCost() + ")");
            totalCost += s.getCost();
        }

        System.out.println("Total Add-On Cost: $" + totalCost);
    }
}