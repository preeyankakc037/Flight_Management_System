package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Manages the loading and storing of flight data from a text file.
 */
public class FlightDataManager implements DataManager {

    private final String RESOURCE = "./resources/data/flights.txt";

    /**
     * Loads flight records from the data file.
     * 
     * @param fbs the flight booking system to populate
     * @throws IOException                  if there is an error reading the file
     * @throws FlightBookingSystemException if the data is corrupted or invalid
     */
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty())
                    continue;
                String[] properties = line.split(SEPARATOR, -1);
                try {
                    int id = Integer.parseInt(properties[0]);
                    String flightNumber = properties[1];
                    String origin = properties[2];
                    String destination = properties[3];
                    LocalDate departureDate = LocalDate.parse(properties[4]);
                    int capacity = Integer.parseInt(properties[5]);
                    double price = Double.parseDouble(properties[6]);
                    boolean isDeleted = properties.length > 7 && "true".equalsIgnoreCase(properties[7]);

                    Flight flight = new Flight(id, flightNumber, origin, destination, departureDate, capacity, price);
                    flight.setDeleted(isDeleted);
                    fbs.addFlight(flight);
                } catch (NumberFormatException ex) {
                    throw new FlightBookingSystemException(
                            "Unable to parse flight id " + properties[0] + " on line " + line_idx
                                    + "\nError: " + ex);
                }
                line_idx++;
            }
        }
    }

    /**
     * Saves all flights to the data file.
     * We store all flights, including those marked as deleted, to preserve
     * their status.
     * 
     * @param fbs the system containing the data to save
     * @throws IOException if there is an error writing the file
     */
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            // We need to store ALL flights (including deleted ones) to persist the
            // status
            for (Flight flight : fbs.getAllFlights()) {
                out.print(flight.getId() + SEPARATOR);
                out.print(flight.getFlightNumber() + SEPARATOR);
                out.print(flight.getOrigin() + SEPARATOR);
                out.print(flight.getDestination() + SEPARATOR);
                out.print(flight.getDepartureDate() + SEPARATOR);
                out.print(flight.getCapacity() + SEPARATOR);
                out.print(flight.getPrice() + SEPARATOR);
                out.print(flight.isDeleted() + SEPARATOR);
                out.println();
            }
        }
    }
}
