package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.time.LocalDate;

/**
 * Command to update a booking by moving a customer from one flight to another.
 * This incurs a rebooking fee.
 */
public class UpdateBooking implements Command {

    private final int customerId;
    private final String oldFlightRef;
    private final String newFlightRef;

    public UpdateBooking(int customerId, int oldFlightId, int newFlightId) {
        this.customerId = customerId;
        this.oldFlightRef = String.valueOf(oldFlightId);
        this.newFlightRef = String.valueOf(newFlightId);
    }

    public UpdateBooking(int customerId, int oldFlightId, int newFlightId, LocalDate bookingDate) {
        this.customerId = customerId;
        this.oldFlightRef = String.valueOf(oldFlightId);
        this.newFlightRef = String.valueOf(newFlightId);
    }

    public UpdateBooking(int customerId, String oldFlightRef, String newFlightRef) {
        this.customerId = customerId;
        this.oldFlightRef = oldFlightRef;
        this.newFlightRef = newFlightRef;
    }

    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        Customer customer = fbs.getCustomerByID(customerId);

        // Find the old booking to preserve its date
        Booking oldBooking = null;
        for (Booking b : customer.getBookings()) {
            if (b.getBookingStatus() == bcu.cmp5332.bookingsystem.model.BookingStatus.ACTIVE
                    && b.getFlight().getId() == Integer.parseInt(oldFlightRef)) {
                oldBooking = b;
                break;
            }
        }

        if (oldBooking == null) {
            throw new FlightBookingSystemException(
                    "Booking for flight " + oldFlightRef + " not found or already cancelled.");
        }

        LocalDate originalBookingDate = oldBooking.getBookingDate();

        // 1. Cancel old booking
        Command cancel = new CancelBooking(customerId, oldFlightRef);
        cancel.execute(fbs);

        // 2. Add new booking using original date
        Command add;
        try {
            int newFlightId = Integer.parseInt(newFlightRef);
            add = new AddBooking(customerId, newFlightId, originalBookingDate);
        } catch (NumberFormatException e) {
            add = new AddBooking(customerId, newFlightRef);
        }
        add.execute(fbs);

        System.out.println("Booking successfully updated from flight " + oldFlightRef + " to flight " + newFlightRef
                + ". Original booking date preserved.");
    }
}
