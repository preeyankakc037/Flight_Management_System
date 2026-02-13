package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class ShowFlight implements Command {

    private final String flightRef;

    public ShowFlight(int id) {
        this.flightRef = String.valueOf(id);
    }

    public ShowFlight(String flightRef) {
        this.flightRef = flightRef;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Flight flight;
        try {
            int id = Integer.parseInt(flightRef);
            flight = flightBookingSystem.getFlightByID(id);
        } catch (NumberFormatException e) {
            flight = null;
            for (Flight f : flightBookingSystem.getFlights()) {
                if (f.getFlightNumber().equalsIgnoreCase(flightRef)) {
                    flight = f;
                    break;
                }
            }
            if (flight == null) {
                throw new FlightBookingSystemException("Flight " + flightRef + " not found.");
            }
        }
        double currentPrice = flightBookingSystem.calculateBookingPrice(flight);
        System.out.println(flight.getDetailsLong());
        System.out.println("Current Booking Price: Rs " + String.format("%.2f", currentPrice));
    }
}
