package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.util.List;

public class ShowFlightPassengers implements Command {

    private final int flightId;

    public ShowFlightPassengers(int flightId) {
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        List<Customer> passengers = flightBookingSystem.getPassengersForFlight(flightId);

        System.out.println("Passengers on Flight " + flight.getFlightNumber() + ":");
        for (Customer passenger : passengers) {
            // Find specific booking for this flight to get status if needed
            // But passengers list in Flight are ACTIVE usually.
            System.out.println("- " + passenger.getName() + " | Phone: " + passenger.getPhone());
        }
        System.out.println(passengers.size() + " passenger(s).");
    }
}
