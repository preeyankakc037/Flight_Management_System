package bcu.cmp5332.bookingsystem.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a customer who can make flight bookings.
 * This class stores the customer's profile and a list of all their bookings.
 */
public class Customer {

    // Encapsulation: All fields are private, with internal state accessed via
    // controlled methods.
    private int id;
    private String name;
    private String phone;
    private String email;
    // Association: A customer has a list of bookings they have made (One-to-Many).
    private final List<Booking> bookings = new ArrayList<>();
    private boolean isDeleted = false;

    /**
     * Constructs a new Customer with the specified details.
     * 
     * @param id    the unique identifier of the customer
     * @param name  the customer's name
     * @param phone the customer's phone number
     * @param email the customer's email address
     */
    public Customer(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email != null ? email.toLowerCase() : null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }

    /**
     * Validates if a string is a valid email address format.
     * A valid email must follow the general format of local-part@domain.extension.
     * 
     * @param email the email string to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        // Basic email regex that ensures @ and a proper domain extension (at least 2
        // chars after the last dot)
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Validates if a string is a valid phone number format.
     * A valid phone number must contain only digits and may optionally start with a
     * plus sign.
     * 
     * @param phone the phone string to validate
     * @return true if the phone is valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Phone regex: optional + at start, then only digits (no spaces, commas, etc.)
        String phoneRegex = "^\\+?[0-9]+$";
        return phone.matches(phoneRegex);
    }

    /**
     * Checks if the customer is marked as deleted (hidden).
     * 
     * @return true if the customer is deleted, false otherwise
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Marks the customer as deleted or active.
     * 
     * @param isDeleted true to hide the customer, false to show it
     */
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * Retrieves an unmodifiable list of all bookings associated with this customer.
     * Returning an unmodifiable list ensures Encapsulation by preventing
     * external code from bypassing the addBooking(Booking) method.
     * 
     * @return an unmodifiable list of the customer's booking history
     */
    public List<Booking> getBookings() {
        return Collections.unmodifiableList(bookings);
    }

    /**
     * Adds a new booking record to the customer's history.
     * 
     * @param booking the booking object to associate with this customer
     */
    public void addBooking(Booking booking) {
        if (booking != null) {
            bookings.add(booking);
        }
    }

    /**
     * Removes all bookings associated with a specific flight.
     * This is typically used when a flight is entirely removed from the system.
     * 
     * @param flight the flight for which bookings should be removed
     */
    public void cancelBookingForFlight(Flight flight) {
        bookings.removeIf(booking -> booking.getFlight().getId() == flight.getId());
    }

    /**
     * Provides a short summary of the customer's basic contact details.
     * 
     * @return a concise string with the customer's ID, name, and phone
     */
    public String getDetailsShort() {
        return "Customer #" + id + " - " + name + " (Phone: " + phone + ")";
    }

    /**
     * Provides a detailed profile of the customer, including their full contact
     * info and a summary of all their bookings.
     * 
     * @return a multi-line formatted string with the customer's full profile
     */
    public String getDetailsLong() {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer #").append(id).append("\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Phone: ").append(phone).append("\n");
        sb.append("Email: ").append(email).append("\n");
        sb.append("Bookings: \n");
        for (Booking booking : bookings) {
            sb.append("  - ").append(booking.getFlight().getFlightNumber())
                    .append(" on ").append(booking.getBookingDate()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Counts how many future, non-cancelled bookings the customer has.
     * 
     * @param systemDate the current date
     * @return the number of active future bookings
     */
    public int getNumberOfActiveBookings(java.time.LocalDate systemDate) {
        int count = 0;
        for (Booking b : bookings) {
            // A booking is active if it's not cancelled and the flight hasn't departed yet
            if (b.getBookingStatus() == BookingStatus.ACTIVE && b.getFlight().getDepartureDate().isAfter(systemDate)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if the customer has any active (upcoming) bookings.
     * 
     * @param systemDate the current system date
     * @return true if there is at least one upcoming booking, false otherwise
     */
    public boolean hasActiveBookings(java.time.LocalDate systemDate) {
        return getNumberOfActiveBookings(systemDate) > 0;
    }

    /**
     * Gets a list of bookings for flights that haven't departed yet.
     * 
     * @param systemDate the current system date
     * @return list of upcoming active bookings
     */
    public List<Booking> getActiveBookings(java.time.LocalDate systemDate) {
        List<Booking> active = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getBookingStatus() == BookingStatus.ACTIVE
                    && !booking.getFlight().getDepartureDate().isBefore(systemDate)) {
                active.add(booking);
            }
        }
        return Collections.unmodifiableList(active);
    }

    /**
     * Retrieves a list of cancelled bookings for this customer.
     * 
     * @return list of cancelled bookings
     */
    public List<Booking> getCancelledBookings() {
        List<Booking> cancelled = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
                cancelled.add(booking);
            }
        }
        return Collections.unmodifiableList(cancelled);
    }

    /**
     * Gets a list of bookings for flights that have already departed.
     * 
     * @param systemDate the current system date
     * @return list of finished bookings
     */
    public List<Booking> getCompletedBookings(java.time.LocalDate systemDate) {
        List<Booking> completed = new ArrayList<>();
        for (Booking booking : bookings) {
            // A booking is completed if it was active and the flight date is in the past
            if (booking.getBookingStatus() == BookingStatus.ACTIVE
                    && booking.getFlight().getDepartureDate().isBefore(systemDate)) {
                completed.add(booking);
            }
        }
        return Collections.unmodifiableList(completed);
    }
}
