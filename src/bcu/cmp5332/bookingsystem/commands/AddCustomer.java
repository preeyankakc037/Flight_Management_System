package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Inheritance: Implements the Command interface to provide customer addition
 * logic.
 */
public class AddCustomer implements Command {

    private final String name;
    private final String phone;
    private final String email;

    public AddCustomer(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        if (!bcu.cmp5332.bookingsystem.model.Customer.isValidPhone(phone)) {
            throw new FlightBookingSystemException(
                    "Invalid phone number format. Phone must contain only digits (optionally starting with +).");
        }

        if (!bcu.cmp5332.bookingsystem.model.Customer.isValidEmail(email)) {
            throw new FlightBookingSystemException("Invalid email format. Please use local-part@domain.extension.");
        }

        int maxId = 0;
        for (bcu.cmp5332.bookingsystem.model.Customer c : flightBookingSystem.getAllCustomers()) {
            if (c.getId() > maxId) {
                maxId = c.getId();
            }
        }
        bcu.cmp5332.bookingsystem.model.Customer customer = new bcu.cmp5332.bookingsystem.model.Customer(maxId + 1,
                name, phone, email.toLowerCase());
        flightBookingSystem.addCustomer(customer);
        System.out.println("Customer #" + customer.getId() + " added.");
    }
}
