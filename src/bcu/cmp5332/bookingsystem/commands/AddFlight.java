package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.time.LocalDate;

public class AddFlight implements Command {

    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDate departureDate;
    private final int capacity;
    private final double price;

    public AddFlight(String flightNumber, String origin, String destination, LocalDate departureDate, int capacity,
            double price) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.price = price;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        LocalDate systemDate = flightBookingSystem.getSystemDate();
        if (!departureDate.isAfter(systemDate)) {
            throw new FlightBookingSystemException("You cannot add a flight in past");
        }

        int maxId = 0;
        for (Flight f : flightBookingSystem.getAllFlights()) {
            if (f.getId() > maxId) {
                maxId = f.getId();
            }
        }

        Flight flight = new Flight(++maxId, flightNumber, origin, destination, departureDate, capacity, price);
        flightBookingSystem.addFlight(flight);
        System.out.println("Flight #" + flight.getId() + " added.");
    }
}
