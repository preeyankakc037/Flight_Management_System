package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.time.LocalDate;

public class EditFlight implements Command {

    private final int id;
    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDate departureDate;
    private final int capacity;
    private final double price;

    public EditFlight(int id, String flightNumber, String origin, String destination, LocalDate departureDate, int capacity, double price) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.price = price;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Flight flight = flightBookingSystem.getFlightByID(id);
        
        // Basic unique check (if flight number & date changed)
        for (Flight existing : flightBookingSystem.getAllFlights()) {
            if (existing.getId() != id && existing.getFlightNumber().equals(flightNumber) 
                && existing.getDepartureDate().isEqual(departureDate)) {
                throw new FlightBookingSystemException("Another flight with same number and departure date already exists.");
            }
        }

        flight.setFlightNumber(flightNumber);
        flight.setOrigin(origin);
        flight.setDestination(destination);
        flight.setDepartureDate(departureDate);
        flight.setCapacity(capacity);
        flight.setPrice(price);
        
        System.out.println("Flight #" + id + " updated.");
    }
}
