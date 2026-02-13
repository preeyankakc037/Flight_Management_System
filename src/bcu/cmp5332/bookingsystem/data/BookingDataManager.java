package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.BookingStatus;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Manages the loading and storing of booking data from a text file.
 */
public class BookingDataManager implements DataManager {

    public final String RESOURCE = "./resources/data/bookings.txt";

    /**
     * Loads booking records from the data file.
     * This method can handle both legacy (no ID) and new (with ID) formats.
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
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] properties = line.split(SEPARATOR, -1);
                try {
                    int id = -1;
                    int customIdIndex = 0;
                    // Attempting to detect if the data is using the newer format with a Booking ID
                    if (properties.length >= 7) {
                        try {
                            // If field 3 is a valid date, we know the first field is ID
                            LocalDate.parse(properties[3]);
                            id = Integer.parseInt(properties[0]);
                            customIdIndex = 1;
                        } catch (Exception e) {
                            // If field 3 isn't a date, we fall back to the older format
                            customIdIndex = 0;
                        }
                    }

                    int customerId = Integer.parseInt(properties[customIdIndex]);
                    int flightId = Integer.parseInt(properties[customIdIndex + 1]);
                    LocalDate bookingDate = LocalDate.parse(properties[customIdIndex + 2]);
                    double price = Double.parseDouble(properties[customIdIndex + 3]);
                    double fee = Double.parseDouble(properties[customIdIndex + 4]);

                    // Handle status: if customIdIndex is 1, status is at index 1+5 = 6.
                    // If customIdIndex is 0, status is at index 0+5 = 5.
                    int statusIndex = customIdIndex + 5;

                    BookingStatus status;
                    if (properties.length > statusIndex) {
                        try {
                            status = BookingStatus.valueOf(properties[statusIndex]);
                        } catch (IllegalArgumentException e) {
                            boolean isCancelled = Boolean.parseBoolean(properties[statusIndex]);
                            status = isCancelled ? BookingStatus.CANCELLED : BookingStatus.ACTIVE;
                        }
                    } else {
                        status = BookingStatus.ACTIVE;
                    }

                    Customer customer = fbs.getCustomerByID(customerId, true);
                    Flight flight;
                    try {
                        flight = fbs.getFlightByID(flightId);
                    } catch (FlightBookingSystemException e) {
                        System.err.println("Warning: Skipping booking for customer " + customerId
                                + " referencing non-existent flight " + flightId);
                        continue;
                    }

                    Booking booking = new Booking(customer, flight, bookingDate, price);
                    if (id != -1) {
                        booking.setId(id);
                        if (id > fbs.getMaxBookingId()) {
                            fbs.setMaxBookingId(id);
                        }
                    } else {
                        // Assign a new ID for legacy bookings so they have one
                        booking.setId(fbs.generateBookingId());
                    }
                    booking.setCancellationFee(fee);
                    booking.setBookingStatus(status);

                    customer.addBooking(booking);
                    if (status == BookingStatus.ACTIVE) {
                        flight.addPassenger(customer);
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                    throw new FlightBookingSystemException("Unable to parse booking on line " + line_idx
                            + "\nError: " + ex);
                }
                line_idx++;
            }
        }
    }

    /**
     * Saves all current bookings to the data file.
     * 
     * @param fbs the system containing the data to save
     * @throws IOException if there is an error writing the file
     */
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Customer customer : fbs.getAllCustomers()) {
                for (Booking booking : customer.getBookings()) {
                    out.print(booking.getId() + SEPARATOR);
                    out.print(booking.getCustomer().getId() + SEPARATOR + booking.getFlight().getId() + SEPARATOR
                            + booking.getBookingDate() + SEPARATOR + booking.getFinalPrice() + SEPARATOR
                            + booking.getCancellationFee() + SEPARATOR + booking.getBookingStatus() + SEPARATOR);
                    out.println();
                }
            }
        }
    }

}
