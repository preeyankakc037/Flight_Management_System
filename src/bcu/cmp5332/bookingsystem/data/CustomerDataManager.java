package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Manages the loading and storing of customer data from a text file.
 */
public class CustomerDataManager implements DataManager {

    private final String RESOURCE = "./resources/data/customers.txt";

    /**
     * Loads customer records from the data file.
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
                    int id = Integer.parseInt(properties[0]);
                    String name = properties[1];
                    String phone = properties[2];
                    String email = properties[3];
                    boolean isDeleted = properties.length > 5 && "true".equalsIgnoreCase(properties[5]);

                    Customer customer = new Customer(id, name, phone, email);
                    customer.setDeleted(isDeleted);
                    fbs.addCustomer(customer, false);
                } catch (NumberFormatException ex) {
                    throw new FlightBookingSystemException(
                            "Unable to parse customer id " + properties[0] + " on line " + line_idx
                                    + "\nError: " + ex);
                }
                line_idx++;
            }
        }
    }

    /**
     * Saves all customers to the data file.
     * We store all customers, including those marked as deleted, to preserve
     * their status.
     * 
     * @param fbs the system containing the data to save
     * @throws IOException if there is an error writing the file
     */
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            //  We need to store ALL customers (including deleted ones) to persist the
            // status
            for (Customer customer : fbs.getAllCustomers()) {
                out.print(customer.getId() + SEPARATOR);
                out.print(customer.getName() + SEPARATOR);
                out.print(customer.getPhone() + SEPARATOR);
                out.print(customer.getEmail() + SEPARATOR);
                out.print(SEPARATOR); // Bookings list placeholder
                out.print(customer.isDeleted() + SEPARATOR);
                out.println();
            }
        }
    }
}
