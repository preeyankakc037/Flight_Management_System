package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class ShowCustomer implements Command {

    private final int id;

    public ShowCustomer(int id) {
        this.id = id;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(id);
        java.time.LocalDate systemDate = flightBookingSystem.getSystemDate();

        System.out.println("Customer: " + customer.getName() + " (ID: " + customer.getId() + ")");
        System.out.println("Phone: " + customer.getPhone());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("--------------------------------------------------");

        System.out.println("Active Bookings:");
        if (customer.getActiveBookings(systemDate).isEmpty()) {
            System.out.println("  None");
        } else {
            for (bcu.cmp5332.bookingsystem.model.Booking b : customer.getActiveBookings(systemDate)) {
                System.out.println("  - " + b.getId() + " | Flight " + b.getFlight().getFlightNumber() + " | "
                        + b.getFlight().getDepartureDate() + " | Price: Rs " + b.getFinalPrice());
            }
        }

        System.out.println();
        System.out.println("Cancelled Bookings:");
        if (customer.getCancelledBookings().isEmpty()) {
            System.out.println("  None");
        } else {
            for (bcu.cmp5332.bookingsystem.model.Booking b : customer.getCancelledBookings()) {
                System.out.println("  - " + b.getId() + " | Flight " + b.getFlight().getFlightNumber()
                        + " | Cancelled | Fee: Rs " + b.getCancellationFee());
            }
        }

        System.out.println();
        System.out.println("Completed Bookings:");
        if (customer.getCompletedBookings(systemDate).isEmpty()) {
            System.out.println("  None");
        } else {
            for (bcu.cmp5332.bookingsystem.model.Booking b : customer.getCompletedBookings(systemDate)) {
                System.out
                        .println("  - " + b.getId() + " | Flight " + b.getFlight().getFlightNumber() + " | Completed");
            }
        }
    }
}
