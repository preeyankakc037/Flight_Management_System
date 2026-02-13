package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class EditCustomer implements Command {

    private final int id;
    private final String name;
    private final String phone;
    private final String email;

    public EditCustomer(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        if (!Customer.isValidPhone(phone)) {
            throw new FlightBookingSystemException(
                    "Invalid phone number format. Phone must contain only digits (optionally starting with +).");
        }

        if (!Customer.isValidEmail(email)) {
            throw new FlightBookingSystemException("Invalid email format. Please use local-part@domain.extension.");
        }

        String normalizedEmail = email.toLowerCase();
        Customer customer = flightBookingSystem.getCustomerByID(id);

        // Check uniqueness for email and phone if they changed
        for (Customer existing : flightBookingSystem.getAllCustomers()) {
            if (existing.getId() != id) {
                if (existing.getPhone().equals(phone)) {
                    throw new FlightBookingSystemException(
                            "Another customer with the same phone number already exists.");
                }
                if (existing.getEmail().equalsIgnoreCase(normalizedEmail)) {
                    throw new FlightBookingSystemException(
                            "Another customer with the same email address already exists.");
                }
            }
        }

        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        System.out.println("Customer #" + id + " updated.");
    }
}
