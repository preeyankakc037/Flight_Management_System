package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.commands.Help;
import bcu.cmp5332.bookingsystem.commands.ListBookings;
import bcu.cmp5332.bookingsystem.commands.ListCustomers;
import bcu.cmp5332.bookingsystem.commands.ListFlights;
import bcu.cmp5332.bookingsystem.commands.LoadGUI;
import bcu.cmp5332.bookingsystem.commands.ShowCustomer;
import bcu.cmp5332.bookingsystem.commands.ShowFlight;
import bcu.cmp5332.bookingsystem.commands.UpdateBooking;
import bcu.cmp5332.bookingsystem.commands.AddBooking;
import bcu.cmp5332.bookingsystem.commands.AddCustomer;
import bcu.cmp5332.bookingsystem.commands.CancelBooking;
import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.SetSystemDate;
import bcu.cmp5332.bookingsystem.commands.ShowBooking;
import bcu.cmp5332.bookingsystem.commands.ShowFlightPassengers;
import bcu.cmp5332.bookingsystem.commands.DeleteCustomer;
import bcu.cmp5332.bookingsystem.commands.DeleteFlight;
import bcu.cmp5332.bookingsystem.commands.EditFlight;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CommandParser {

    public static Command parse(String line) throws IOException, FlightBookingSystemException {
        line = line.trim();
        if (line.isEmpty()) {
            throw new FlightBookingSystemException("Invalid command.");
        }

        String[] parts = line.split("\\s+");
        String cmd = parts[0].toLowerCase();

        // Handle 2-word commands by combining parts[0] and parts[1] if applicable
        int argOffset = 0;
        if (parts.length > 1) {
            String combined = cmd + parts[1].toLowerCase();
            if (combined.equals("addflight") || combined.equals("addcustomer") ||
                    combined.equals("listofflights") || combined.equals("listofcustomers") ||
                    combined.equals("listofbookings") ||
                    combined.equals("addbooking") || combined.equals("cancelbooking") ||
                    combined.equals("editbooking") || combined.equals("showflightdetails") ||
                    combined.equals("showcustomerdetails") || combined.equals("showbooking") ||
                    combined.equals("setsystemdate") || combined.equals("showflightpassengers") ||
                    combined.equals("deletecustomer") || combined.equals("updateflight") ||
                    combined.equals("deleteflight")) {
                cmd = combined;
                argOffset = 1; // logical arguments shift by 1
            }
        }

        try {
            if (cmd.equals("addflight")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Flight Number: ");
                String flighNumber = reader.readLine();
                System.out.print("Origin: ");
                String origin = reader.readLine();
                System.out.print("Destination: ");
                String destination = reader.readLine();
                System.out.print("Capacity: ");
                int capacity = Integer.parseInt(reader.readLine());
                System.out.print("Price: ");
                double price = Double.parseDouble(reader.readLine());

                LocalDate departureDate = parseDateWithAttempts(reader);

                return new AddFlight(flighNumber, origin, destination, departureDate, capacity, price);
            } else if (cmd.equals("addcustomer")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Customer Name: ");
                String name = reader.readLine();
                System.out.print("Phone Number: ");
                String phone = reader.readLine();
                System.out.print("Email: ");
                String email = reader.readLine();

                return new AddCustomer(name, phone, email);
            } else if (cmd.equals("loadgui")) {
                return new LoadGUI();
            } else if (cmd.equals("listofflights")) {
                return new ListFlights();
            } else if (cmd.equals("listofcustomers")) {
                return new ListCustomers();
            } else if (cmd.equals("listofbookings")) {
                return new ListBookings();
            } else if (cmd.equals("help")) {
                return new Help();
            } else if (cmd.equals("showflightdetails")) {
                int idIndex = 1 + argOffset;
                if (parts.length <= idIndex)
                    throw new FlightBookingSystemException("Usage: showflightdetails [id/number]");
                return new ShowFlight(parts[idIndex]);
            } else if (cmd.equals("showcustomerdetails")) {
                int idIndex = 1 + argOffset;
                if (parts.length <= idIndex)
                    throw new FlightBookingSystemException("Usage: showcustomerdetails [id]");
                return new ShowCustomer(Integer.parseInt(parts[idIndex]));
            } else if (cmd.equals("addbooking")) {
                if (parts.length < 3 + argOffset)
                    throw new FlightBookingSystemException("Usage: addbooking [customer id] [flight id/number]");
                int custId = Integer.parseInt(parts[1 + argOffset]);
                String flightRef = parts[2 + argOffset];
                return new AddBooking(custId, flightRef);
            } else if (cmd.equals("cancelbooking")) {
                if (parts.length < 3 + argOffset)
                    throw new FlightBookingSystemException("Usage: cancelbooking [customer id] [flight id/number]");
                int custId = Integer.parseInt(parts[1 + argOffset]);
                String flightRef = parts[2 + argOffset];
                return new CancelBooking(custId, flightRef);
            } else if (cmd.equals("editbooking")) {
                if (parts.length < 4 + argOffset)
                    throw new FlightBookingSystemException(
                            "Usage: editbooking [customer id] [old flight id/number] [new flight id/number]");
                int custId = Integer.parseInt(parts[1 + argOffset]);
                String oldFlightRef = parts[2 + argOffset];
                String newFlightRef = parts[3 + argOffset];
                return new UpdateBooking(custId, oldFlightRef, newFlightRef);
            } else if (cmd.equals("showbooking")) {
                int idIndex = 1 + argOffset;
                if (parts.length <= idIndex)
                    throw new FlightBookingSystemException("Usage: showbooking [booking id]");
                return new ShowBooking(Integer.parseInt(parts[idIndex]));
            } else if (cmd.equals("showflightpassengers")) {
                int idIndex = 1 + argOffset;
                if (parts.length <= idIndex)
                    throw new FlightBookingSystemException("Usage: showflightpassengers [flight id]");
                return new ShowFlightPassengers(Integer.parseInt(parts[idIndex]));
            } else if (cmd.equals("setsystemdate")) {
                int dateIndex = 1 + argOffset;
                if (parts.length <= dateIndex)
                    throw new FlightBookingSystemException("Usage: setsystemdate [YYYY-MM-DD]");
                LocalDate date = LocalDate.parse(parts[dateIndex]);
                return new SetSystemDate(date);
            } else if (cmd.equals("deletecustomer")) {
                int idIndex = 1 + argOffset;
                if (parts.length <= idIndex)
                    throw new FlightBookingSystemException("Usage: deletecustomer [customer id]");
                return new DeleteCustomer(Integer.parseInt(parts[idIndex]));
            } else if (cmd.equals("deleteflight")) {
                int idIndex = 1 + argOffset;
                if (parts.length <= idIndex)
                    throw new FlightBookingSystemException("Usage: deleteflight [flight id]");
                return new DeleteFlight(Integer.parseInt(parts[idIndex]));
            } else if (cmd.equals("updateflight")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                int idIndex = 1 + argOffset;
                if (parts.length <= idIndex)
                    throw new FlightBookingSystemException("Usage: updateflight [flight id]");
                int flightId = Integer.parseInt(parts[idIndex]);

                System.out.print("Flight Number: ");
                String flightNumber = reader.readLine();
                System.out.print("Origin: ");
                String origin = reader.readLine();
                System.out.print("Destination: ");
                String destination = reader.readLine();
                System.out.print("Capacity: ");
                int capacity = Integer.parseInt(reader.readLine());
                System.out.print("Price: ");
                double price = Double.parseDouble(reader.readLine());
                LocalDate departureDate = parseDateWithAttempts(reader);

                return new EditFlight(flightId, flightNumber, origin, destination, departureDate, capacity, price);
            }
        } catch (NumberFormatException ex) {
            throw new FlightBookingSystemException("Invalid numeric value provided (Ensure Customer IDs are numeric).");
        }

        throw new FlightBookingSystemException("Invalid command.");
    }

    private static LocalDate parseDateWithAttempts(BufferedReader br, int attempts)
            throws IOException, FlightBookingSystemException {
        if (attempts < 1) {
            throw new IllegalArgumentException("Number of attempts should be higher that 0");
        }
        while (attempts > 0) {
            attempts--;
            System.out.print("Departure Date (\"YYYY-MM-DD\" format): ");
            try {
                LocalDate departureDate = LocalDate.parse(br.readLine());
                return departureDate;
            } catch (DateTimeParseException dtpe) {
                System.out.println("Date must be in YYYY-MM-DD format. " + attempts + " attempts remaining...");
            }
        }

        throw new FlightBookingSystemException("Incorrect departure date provided. Cannot create flight.");
    }

    private static LocalDate parseDateWithAttempts(BufferedReader br) throws IOException, FlightBookingSystemException {
        return parseDateWithAttempts(br, 3);
    }
}
