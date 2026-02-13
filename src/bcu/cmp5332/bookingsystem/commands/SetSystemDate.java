package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.time.LocalDate;

public class SetSystemDate implements Command {

    private final LocalDate newDate;

    public SetSystemDate(LocalDate newDate) {
        this.newDate = newDate;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        flightBookingSystem.setSystemDate(newDate);
        System.out.println("System date updated to: " + newDate);
    }
}
