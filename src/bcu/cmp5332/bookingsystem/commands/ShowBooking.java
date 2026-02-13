package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class ShowBooking implements Command {

    private final int bookingId;

    public ShowBooking(int bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Search for booking across all customers.
        // Ideally FBS should have getBookingByID.
        Booking found = null;
        for (Customer c : flightBookingSystem.getCustomers()) {
            for (Booking b : c.getBookings()) {
                if (b.getId() == bookingId) {
                    found = b;
                    break;
                }
            }
            if (found != null)
                break;
        }

        if (found == null) {
            throw new FlightBookingSystemException("Booking ID " + bookingId + " not found.");
        }

        System.out.println("Booking ID: " + found.getId());
        System.out.println("Customer: " + found.getCustomer().getName());
        System.out.println("Flight: " + found.getFlight().getFlightNumber());
        System.out.println("Departure Date: " + found.getFlight().getDepartureDate());
        System.out.println("Booking Date: " + found.getBookingDate());
        System.out.println("Status: " + found.getBookingStatus());
        System.out.println("Price Paid: " + found.getFinalPrice());
        if (found.getCancellationFee() > 0) {
            System.out.println("Cancellation Fee: " + found.getCancellationFee());
        }
    }
}
