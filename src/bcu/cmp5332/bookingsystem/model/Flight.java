package bcu.cmp5332.bookingsystem.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a flight in the booking system.
 * This class tracks flight details, capacity, and the list of passengers.
 * It also handles dynamic pricing based on how close the flight is to
 * departure.
 */
public class Flight {

    // Encapsulation: Fields are private and only accessible via getters/setters.
    private int id;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDate departureDate;
    private int capacity;
    private double price;

    // Association: A flight has a set of passengers (Many-to-Many via Booking).
    // Using a Set ensures that the same customer cannot be added twice to the same
    // flight.
    private final Set<Customer> passengers;
    private boolean isDeleted = false;

    /**
     * Constructs a new Flight with the specified details.
     * 
     * @param id            the unique identifier of the flight
     * @param flightNumber  the flight number (e.g., BA123)
     * @param origin        the departure location
     * @param destination   the arrival location
     * @param departureDate the date of departure
     * @param capacity      the maximum number of passengers
     * @param price         the price of the flight
     */
    public Flight(int id, String flightNumber, String origin, String destination, LocalDate departureDate, int capacity,
            double price) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.price = price;

        passengers = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Checks if the flight is marked as deleted (hidden).
     * 
     * @return true if the flight is deleted, false otherwise
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Marks the flight as deleted or active.
     * 
     * @param isDeleted true to hide the flight, false to show it
     */
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * Retrieves the list of passengers currently booked on this flight.
     * Returning a new ArrayList (Defensive Copy) ensures Encapsulation by
     * preventing external classes from modifying the internal Set.
     * 
     * @return a new list containing all customers who have booked a seat
     */
    public List<Customer> getPassengers() {
        return new ArrayList<>(passengers);
    }

    /**
     * Calculates the price for the flight based on the date and occupancy.
     * Higher prices are charged if the flight is soon or if many seats are already
     * taken.
     * 
     * @param systemDate the current system date
     * @return the rounded final price
     */
    public double calculatePrice(LocalDate systemDate) {
        double currentPrice = this.price;

        // Calculate days until departure to determine if a surcharge applies
        long daysUntilDeparture = java.time.temporal.ChronoUnit.DAYS.between(systemDate, departureDate);

        // Price increases for last-minute bookings
        if (daysUntilDeparture < 3) {
            currentPrice *= 1.5; // 50% increase if departing within 3 days
        } else if (daysUntilDeparture < 7) {
            currentPrice *= 1.2; // 20% increase if departing within a week
        }

        // Price increases based on how full the flight is (occupancy rate)
        double occupancyRate = (double) passengers.size() / capacity;
        if (occupancyRate >= 0.9) {
            currentPrice *= 1.5; // 50% hike for 90%+ occupancy
        } else if (occupancyRate >= 0.75) {
            currentPrice *= 1.25; // 25% hike for 75%+ occupancy
        } else if (occupancyRate >= 0.5) {
            currentPrice *= 1.1; // 10% hike for 50%+ occupancy
        }

        // Round to two decimal places for currency format
        return Math.round(currentPrice * 100.0) / 100.0;
    }

    /**
     * Provides a short summary of the flight details.
     * Useful for listing flights in tables or dropdown menus.
     * 
     * @return a concise string representation of the flight
     */
    public String getDetailsShort() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        return "Flight #" + id + " - " + flightNumber + " - " + origin + " to "
                + destination + " on " + departureDate.format(dtf);
    }

    /**
     * Provides a detailed view of the flight, including the full list of
     * passengers.
     * 
     * @return a multi-line formatted string with all flight and passenger data
     */
    public String getDetailsLong() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        StringBuilder sb = new StringBuilder();
        sb.append("Flight #").append(id).append("\n");
        sb.append("Number: ").append(flightNumber).append("\n");
        sb.append("Origin: ").append(origin).append("\n");
        sb.append("Destination: ").append(destination).append("\n");
        sb.append("Departure Date: ").append(departureDate.format(dtf)).append("\n");
        sb.append("Passengers: \n");
        for (Customer passenger : passengers) {
            sb.append("  - ").append(passenger.getName()).append(" (Phone: ").append(passenger.getPhone())
                    .append(")\n");
        }
        return sb.toString();
    }

    /**
     * Adds a passenger to the flight.
     * This method does not check for capacity; use isFull() before calling.
     * 
     * @param passenger the customer to add to the flight's passenger list
     */
    public void addPassenger(Customer passenger) {
        passengers.add(passenger);
    }

    /**
     * Removes a passenger from the flight, typically due to a cancellation.
     * 
     * @param passenger the customer to remove from the flight
     */
    public void removePassenger(Customer passenger) {
        passengers.remove(passenger);
    }

    /**
     * Checks whether the flight has reached its maximum passenger capacity.
     * 
     * @return true if no more seats are available, false otherwise
     */
    public boolean isFull() {
        return passengers.size() >= capacity;
    }

    /**
     * Determines if the flight has already departed relative to the system date.
     * 
     * @param systemDate the current simulation date
     * @return true if the flight is in the past or departing today, false if it's
     *         in the future
     */
    public boolean hasDeparted(LocalDate systemDate) {
        return systemDate.isAfter(departureDate) || systemDate.isEqual(departureDate);
    }

    /**
     * Returns the current number of passengers booked on this flight.
     * 
     * @return the size of the passenger set
     */
    public int getPassengerCount() {
        return passengers.size();
    }
}
