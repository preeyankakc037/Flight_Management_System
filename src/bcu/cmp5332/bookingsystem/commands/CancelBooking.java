package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class CancelBooking implements Command {

    private final int customerId;
    private final String flightRef;

    public CancelBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightRef = String.valueOf(flightId);
    }

    public CancelBooking(int customerId, String flightRef) {
        this.customerId = customerId;
        this.flightRef = flightRef;
    }

    /**
     * Executes the cancellation command.
     * Finds the passenger's booking and calculates the tiered cancellation fee.
     * 
     * @param fbs the flight booking system instance
     * @throws FlightBookingSystemException if the booking cannot be found or
     *                                      cancelled
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        Customer customer = fbs.getCustomerByID(customerId);

        Flight flight;
        try {
            int id = Integer.parseInt(flightRef);
            flight = fbs.getFlightByID(id);
        } catch (NumberFormatException e) {
            flight = null;
            for (Flight f : fbs.getFlights()) {
                if (f.getFlightNumber().equalsIgnoreCase(flightRef)) {
                    flight = f;
                    break;
                }
            }
            if (flight == null) {
                throw new FlightBookingSystemException("Flight " + flightRef + " not found.");
            }
        }

        // Find the booking
        Booking targetBooking = null;
        for (Booking b : customer.getBookings()) {
            if (b.getFlight().getId() == flight.getId()
                    && b.getBookingStatus() == bcu.cmp5332.bookingsystem.model.BookingStatus.ACTIVE) {
                targetBooking = b;
                break;
            }
        }

        if (targetBooking == null) {
            throw new FlightBookingSystemException("Booking not found for this customer and flight.");
        }

        // Use domain logic for cancellation with tiered fees
        targetBooking.cancel(fbs.getSystemDate());

        System.out.println(
                "Booking cancelled successfully. Cancellation fee of Rs " +
                        String.format("%.2f", targetBooking.getCancellationFee()) + " applied.");
    }
}
