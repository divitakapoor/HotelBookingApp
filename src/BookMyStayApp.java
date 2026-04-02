import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("   Hotel Booking System v8.0      ");
        System.out.println("==================================");

        // Assume reservation IDs already exist (from previous use cases)
        String reservationId1 = "RES-101";
        String reservationId2 = "RES-102";

        // -----------------------------------------
        // Use Case 7: Add-On Services
        // -----------------------------------------
        AddOnServiceManager manager = new AddOnServiceManager();

        Service breakfast = new Service("Breakfast", 20);
        Service spa = new Service("Spa", 50);
        Service wifi = new Service("WiFi", 10);

        manager.addService(reservationId1, breakfast);
        manager.addService(reservationId1, wifi);
        manager.addService(reservationId2, spa);

        manager.displayServices(reservationId1);
        manager.displayServices(reservationId2);

        // -----------------------------------------
        // Use Case 8: Booking History & Reporting
        // -----------------------------------------
        Reservation booking1 = new Reservation(reservationId1, "Divita Kapoor", "Deluxe Room", 2, 300);
        Reservation booking2 = new Reservation(reservationId2, "Aarav Sharma", "Suite Room", 3, 600);

        BookingHistory bookingHistory = new BookingHistory();

        bookingHistory.addReservation(booking1);
        bookingHistory.addReservation(booking2);

        bookingHistory.displayBookingHistory();

        BookingReportService reportService = new BookingReportService();
        reportService.generateSummaryReport(bookingHistory);
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

    public void addService(String reservationId, Service service) {

        reservationServices
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);

        System.out.println("Added " + service.getName()
                + " to Reservation " + reservationId);
    }

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

/**
 * Reservation class
 */
class Reservation {

    private String reservationId;
    private String guestName;
    private String roomType;
    private int numberOfNights;
    private double totalAmount;

    public Reservation(String reservationId, String guestName, String roomType,
                       int numberOfNights, double totalAmount) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.numberOfNights = numberOfNights;
        this.totalAmount = totalAmount;
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

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void displayReservation() {
        System.out.println("----------------------------------");
        System.out.println("Reservation ID : " + reservationId);
        System.out.println("Guest Name     : " + guestName);
        System.out.println("Room Type      : " + roomType);
        System.out.println("Nights         : " + numberOfNights);
        System.out.println("Total Amount   : $" + totalAmount);
    }
}

/**
 * Booking History class
 */
class BookingHistory {

    private List<Reservation> confirmedBookings;

    public BookingHistory() {
        confirmedBookings = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
        System.out.println("Booking stored in history: " + reservation.getReservationId());
    }

    public List<Reservation> getAllReservations() {
        return confirmedBookings;
    }

    public void displayBookingHistory() {
        System.out.println("\n==================================");
        System.out.println("       BOOKING HISTORY            ");
        System.out.println("==================================");

        if (confirmedBookings.isEmpty()) {
            System.out.println("No confirmed bookings found.");
            return;
        }

        for (Reservation reservation : confirmedBookings) {
            reservation.displayReservation();
        }
    }
}

/**
 * Booking Report Service
 */
class BookingReportService {

    public void generateSummaryReport(BookingHistory history) {

        List<Reservation> bookings = history.getAllReservations();

        System.out.println("\n==================================");
        System.out.println("      BOOKING SUMMARY REPORT      ");
        System.out.println("==================================");

        if (bookings.isEmpty()) {
            System.out.println("No booking data available for report.");
            return;
        }

        int totalBookings = bookings.size();
        double totalRevenue = 0;
        int totalNights = 0;

        for (Reservation reservation : bookings) {
            totalRevenue += reservation.getTotalAmount();
            totalNights += reservation.getNumberOfNights();
        }

        System.out.println("Total Confirmed Bookings : " + totalBookings);
        System.out.println("Total Nights Booked      : " + totalNights);
        System.out.println("Total Revenue Generated  : $" + totalRevenue);
    }
}