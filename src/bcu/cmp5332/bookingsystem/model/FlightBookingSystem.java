package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.time.LocalDate;
import java.util.*;

/**
 * The main coordination class for the booking system.
 * It manages the collections of flights and customers and ensures all
 * business rules (like capacity and uniqueness) are followed.
 */
public class FlightBookingSystem {

    // Encapsulation: All data is private, hidden from external classes.
    private LocalDate systemDate = LocalDate.now();
    private int maxBookingId = 0;

    // Association: The system coordinates the relationship between customers and
    // flights.
    private final Map<Integer, Customer> customers = new TreeMap<>();
    private final Map<Integer, Flight> flights = new TreeMap<>();

    /**
     * Retrieves the current high-water mark for booking IDs.
     * 
     * @return the maximum booking ID seen so far
     */
    public int getMaxBookingId() {
        return maxBookingId;
    }

    /**
     * Sets the maximum booking ID, typically used during data loading.
     * 
     * @param maxBookingId the new maximum booking ID
     */
    public void setMaxBookingId(int maxBookingId) {
        this.maxBookingId = maxBookingId;
    }

    /**
     * Generates a unique booking identifier and increments the internal counter.
     * 
     * @return a new unique booking ID
     */
    public int generateBookingId() {
        return ++maxBookingId;
    }

    public LocalDate getSystemDate() {
        return systemDate;
    }

    /**
     * Updates the current simulation date.
     * 
     * @param newDate the new simulation date
     * @throws IllegalArgumentException if the date is null
     */
    public void setSystemDate(LocalDate newDate) {
        if (newDate == null) {
            throw new IllegalArgumentException("Simulation date cannot be null.");
        }
        this.systemDate = newDate;
    }

    /**
     * Returns a list of all active (not deleted) and future (not departed) flights
     * in the system.
     * 
     * @return an unmodifiable list of active and future flights
     */
    public List<Flight> getFlights() {
        List<Flight> out = new ArrayList<>();
        for (Flight f : flights.values()) {
            if (!f.isDeleted() && !f.getDepartureDate().isBefore(systemDate)) {
                out.add(f);
            }
        }
        return Collections.unmodifiableList(out);
    }

    /**
     * Retrieves a flight by its unique identifier.
     * 
     * @param id the unique identifier of the flight
     * @return the flight object
     * @throws FlightBookingSystemException if no flight with the given ID exists
     */
    public Flight getFlightByID(int id) throws FlightBookingSystemException {
        if (!flights.containsKey(id)) {
            throw new FlightBookingSystemException("There is no flight with that ID.");
        }
        return flights.get(id);
    }

    /**
     * Retrieves a customer by their unique identifier.
     * 
     * @param id the unique identifier of the customer
     * @return the customer object
     * @throws FlightBookingSystemException if no customer with the given ID exists
     */
    public Customer getCustomerByID(int id) throws FlightBookingSystemException {
        return getCustomerByID(id, false);
    }

    /**
     * Retrieves a customer by their unique identifier, with an option to include
     * deleted customers.
     * 
     * @param id             the unique identifier of the customer
     * @param includeDeleted true to return the customer even if they are marked as
     *                       deleted
     * @return the customer object
     * @throws FlightBookingSystemException if no customer with the given ID exists
     *                                      or if includeDeleted is false and the
     *                                      customer is deleted
     */
    public Customer getCustomerByID(int id, boolean includeDeleted) throws FlightBookingSystemException {
        if (!customers.containsKey(id)) {
            throw new FlightBookingSystemException("There is no customer with that ID.");
        }
        Customer customer = customers.get(id);
        if (!includeDeleted && customer.isDeleted()) {
            throw new FlightBookingSystemException("There is no customer with that ID.");
        }
        return customer;
    }

    /**
     * Returns a list of all active (not deleted) customers in the system.
     * 
     * @return an unmodifiable list of active customers
     */
    public List<Customer> getCustomers() {
        List<Customer> out = new ArrayList<>();
        for (Customer c : customers.values()) {
            if (!c.isDeleted()) {
                out.add(c);
            }
        }
        return Collections.unmodifiableList(out);
    }

    /**
     * Returns a list of all customers in the system, including deleted ones.
     * 
     * @return an unmodifiable list of all customers
     */
    public List<Customer> getAllCustomers() {
        return Collections.unmodifiableList(new ArrayList<>(customers.values()));
    }

    /**
     * Returns a list of all flights in the system, including deleted ones.
     * 
     * @return an unmodifiable list of all flights
     */
    public List<Flight> getAllFlights() {
        return Collections.unmodifiableList(new ArrayList<>(flights.values()));
    }

    /**
     * Adds a new flight to the system if it doesn't already exist.
     * 
     * @param flight the flight to add
     * @throws FlightBookingSystemException if a flight with the same number and
     *                                      date exists
     */
    public void addFlight(Flight flight) throws FlightBookingSystemException {
        // First check if the flight ID is unique
        if (flights.containsKey(flight.getId())) {
            throw new IllegalArgumentException("Duplicate flight ID.");
        }
        // Then check if another flight with the same number is already on that date
        for (Flight existing : flights.values()) {
            if (existing.getFlightNumber().equals(flight.getFlightNumber())
                    && existing.getDepartureDate().isEqual(flight.getDepartureDate())) {
                throw new FlightBookingSystemException("There is a flight with same "
                        + "number and departure date in the system");
            }
        }
        flights.put(flight.getId(), flight);
    }

    /**
     * Soft-deletes a flight from the system.
     * 
     * @param id the ID of the flight to remove
     * @throws FlightBookingSystemException if the flight doesn't exist
     */
    public void removeFlight(int id) throws FlightBookingSystemException {
        if (!flights.containsKey(id)) {
            throw new FlightBookingSystemException("Flight does not exist.");
        }

        Flight flight = flights.get(id);
        flight.setDeleted(true); // Mark as deleted rather than removing from map

        // When a flight is removed, all bookings for it should be updated
        for (Customer customer : customers.values()) {
            customer.cancelBookingForFlight(flight);
        }
    }

    /**
     * Adds a new customer to the system.
     * 
     * @param customer the customer object to add
     * @throws FlightBookingSystemException if phone or email uniqueness is violated
     */
    public void addCustomer(Customer customer) throws FlightBookingSystemException {
        addCustomer(customer, true); // Default to checking uniqueness
    }

    /**
     * Adds a new customer to the system with optional uniqueness checks.
     * 
     * @param customer        the customer object to add
     * @param checkUniqueness true to verify phone and email are unique
     * @throws FlightBookingSystemException if uniqueness checks fail
     * @throws IllegalArgumentException     if a customer with the same ID already
     *                                      exists
     */
    public void addCustomer(Customer customer, boolean checkUniqueness) throws FlightBookingSystemException {
        if (customers.containsKey(customer.getId())) {
            throw new IllegalArgumentException("Duplicate customer ID.");
        }
        if (checkUniqueness) {
            for (Customer existing : customers.values()) {
                if (existing.getPhone().equals(customer.getPhone())) {
                    throw new FlightBookingSystemException("A customer with the same phone number already exists.");
                }
                if (existing.getEmail().equalsIgnoreCase(customer.getEmail())) {
                    throw new FlightBookingSystemException("A customer with the same email address already exists.");
                }
            }
        }
        customers.put(customer.getId(), customer);
    }

    /**
     * Soft-deletes a customer from the system.
     * 
     * @param id the ID of the customer to remove
     * @throws FlightBookingSystemException if customer has active bookings or
     *                                      doesn't exist
     */
    public void removeCustomer(int id) throws FlightBookingSystemException {
        if (!customers.containsKey(id)) {
            throw new FlightBookingSystemException("Customer does not exist.");
        }

        Customer customer = customers.get(id);

        // Security check: Don't allow deleting customers who still have future trips
        if (customer.getNumberOfActiveBookings(systemDate) > 0) {
            throw new FlightBookingSystemException("Cannot delete customer with active bookings.");
        }

        customer.setDeleted(true); // Mark as deleted to preserve history
    }

    /**
     * Validates if a booking can be made for the given customer and flight.
     * Enforces rules like capacity limits, departed flights, and duplicate
     * bookings.
     * 
     * @param customer    the customer making the booking
     * @param flight      the target flight
     * @param bookingDate the date the booking is being made
     * @throws FlightBookingSystemException if any validation rule is violated
     */
    public void validateBooking(Customer customer, Flight flight, LocalDate bookingDate)
            throws FlightBookingSystemException {
        if (flight.isDeleted()) {
            throw new FlightBookingSystemException("Cannot book a deleted flight.");
        }
        if (flight.hasDeparted(systemDate)) {
            throw new FlightBookingSystemException("Cannot book: Flight has already departed.");
        }
        if (flight.isFull()) {
            throw new FlightBookingSystemException("Cannot book: Flight is full.");
        }
        // Check for duplicate active booking on same flight
        for (Booking b : customer.getBookings()) {
            if (b.getFlight().getId() == flight.getId() && b.getBookingStatus() == BookingStatus.ACTIVE) {
                throw new FlightBookingSystemException("Customer already has an active booking for this flight.");
            }
        }
    }

    /**
     * Retrieves the list of passengers for a given flight ID.
     * 
     * @param flightId the unique identifier of the flight
     * @return a list of customers who are passengers on the flight
     * @throws FlightBookingSystemException if the flight ID is invalid
     */
    public List<Customer> getPassengersForFlight(int flightId) throws FlightBookingSystemException {
        Flight flight = getFlightByID(flightId);
        return new ArrayList<>(flight.getPassengers());
    }

    /**
     * Calculates the "live" price for a flight based on the current system date.
     * 
     * @param flight the flight to price
     * @return the dynamically calculated price
     */
    public double calculateBookingPrice(Flight flight) {
        return flight.calculatePrice(systemDate);
    }
}
