package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.time.LocalDate;

public class AddBooking implements Command {

    private final int customerId;
    private final String flightRef;
    private LocalDate bookingDate;

    public AddBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightRef = String.valueOf(flightId);
        this.bookingDate = null;
    }

    public AddBooking(int customerId, String flightRef) {
        this.customerId = customerId;
        this.flightRef = flightRef;
        this.bookingDate = null;
    }

    public AddBooking(int customerId, int flightId, LocalDate bookingDate) {
        this.customerId = customerId;
        this.flightRef = String.valueOf(flightId);
        this.bookingDate = bookingDate;
    }

    /**
     * Executes the booking command to create a new reservation.
     * Checks for customer/flight existence, capacity limits, and duplicate
     * bookings.
     * 
     * @param fbs the flight booking system instance
     * @throws FlightBookingSystemException if any validation rule is violated
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

        LocalDate systemDate = fbs.getSystemDate();
        if (bookingDate == null) {
            bookingDate = systemDate;
        }

        // Use centralized validation
        fbs.validateBooking(customer, flight, bookingDate);

        // Calculate price BEFORE adding the passenger to match the user's preview
        // price.
        double price = fbs.calculateBookingPrice(flight);

        // Now add passenger to reflect the updated seat availability for the NEXT
        // booking.
        flight.addPassenger(customer);

        // Create the booking with the calculated price
        Booking booking = new Booking(customer, flight, bookingDate, price);
        booking.setId(fbs.generateBookingId());
        customer.addBooking(booking);

        System.out.println("Booking issued successfully (ID: " + booking.getId() + ") at price: Rs " + price);
    }
}
