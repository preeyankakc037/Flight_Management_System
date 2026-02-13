package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class DeleteFlight implements Command {

    private final int flightId;

    public DeleteFlight(int flightId) {
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        fbs.removeFlight(flightId);
        System.out.println("Flight #" + flightId + " removed.");
    }
}
