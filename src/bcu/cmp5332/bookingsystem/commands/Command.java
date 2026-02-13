package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Inheritance: This interface defines the contract for all executable commands.
 * Polymorphism: Any class implementing Command must provide its own 'execute'
 * logic,
 * allowing the system to run them uniformly without knowing their specific
 * types.
 */
public interface Command {

    public static final String HELP_MESSAGE = "Commands:\n"
            + "\tlistofflights                               print all flights\n"
            + "\tlistofcustomers                             print all customers\n"
            + "\tlistofbookings                              print all bookings\n"
            + "\taddflight                                 add a new flight\n"
            + "\taddcustomer                               add a new customer\n"
            + "\tshowflightdetails [flight id]                    show flight details\n"
            + "\tshowcustomerdetails [customer id]                show customer details\n"
            + "\taddbooking [customer id] [flight id]      add a new booking\n"
            + "\tcancelbooking [customer id] [flight id]   cancel a booking\n"
            + "\teditbooking [booking id] [flight id]      update a booking\n"
            + "\tshowbooking [booking id]                  show booking details\n"
            + "\tshowflightpassengers [flight id]          show passengers on a flight\n"
            + "\tdeletecustomer [customer id]              delete a customer\n"
            + "\tdeleteflight [flight id]                  delete (hide) a flight\n"
            + "\tupdateflight [flight id]                  update flight details\n"
            + "\tsetsystemdate [YYYY-MM-DD]                set system date for testing\n"
            + "\tloadgui                                   loads the GUI version of the app\n"
            + "\thelp                                      prints this help message\n"
            + "\texit                                      exits the program";

    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException;

}
