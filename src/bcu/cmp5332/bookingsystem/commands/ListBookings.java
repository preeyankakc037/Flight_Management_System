package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.util.List;

public class ListBookings implements Command {

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        List<Customer> customers = flightBookingSystem.getCustomers();
        int count = 0;
        for (Customer customer : customers) {
            for (Booking booking : customer.getBookings()) {
                System.out.println("Customer: " + customer.getName() + " (ID: " + customer.getId() + ") - "
                        + "Flight: " + booking.getFlight().getFlightNumber() + " (ID: " + booking.getFlight().getId()
                        + ") - "
                        + "Date: " + booking.getBookingDate() + " - "
                        + "Price: Rs " + booking.getFinalPrice() + " - "
                        + "Status: " + booking.getBookingStatus());
                count++;
            }
        }
        System.out.println(count + " booking(s)");
    }
}
