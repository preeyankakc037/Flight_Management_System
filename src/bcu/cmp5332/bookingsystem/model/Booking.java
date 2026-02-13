package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a reservation made by a customer.
 * This class stores the details of the booking, including the price at the time
 * of purchase.
 * It also handles the logic for calculating cancellation fees.
 */
public class Booking {

    // Encapsulation: All properties are private to protect the integrity of the
    // booking data.
    private int id;
    // Association: A booking links a single Customer to a single Flight.
    private Customer customer;
    private Flight flight;
    private LocalDate bookingDate;
    private double finalPrice;
    private double cancellationFee;
    private BookingStatus bookingStatus;

    /**
     * Constructs a new Booking.
     * 
     * @param customer    the customer who made the booking
     * @param flight      the flight booked
     * @param bookingDate the date the booking was made
     * @param finalPrice  the price paid for the booking (frozen at booking time)
     */
    public Booking(Customer customer, Flight flight, LocalDate bookingDate, double finalPrice) {
        this.customer = customer;
        this.flight = flight;
        this.bookingDate = bookingDate;
        this.finalPrice = finalPrice;
        this.cancellationFee = 0.0;
        this.bookingStatus = BookingStatus.ACTIVE;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    /**
     * Legacy getter for backward compatibility.
     * 
     * @deprecated Use getFinalPrice() instead
     */
    @Deprecated
    public double getPrice() {
        return finalPrice;
    }

    public double getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(double cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    /**
     * Legacy method for backward compatibility.
     * 
     * @deprecated Use getBookingStatus() == BookingStatus.CANCELLED instead
     */
    @Deprecated
    public boolean isCancelled() {
        return bookingStatus == BookingStatus.CANCELLED;
    }

    /**
     * Legacy setter for backward compatibility.
     * 
     * @deprecated Use setBookingStatus() instead
     */
    @Deprecated
    public void setCancelled(boolean cancelled) {
        this.bookingStatus = cancelled ? BookingStatus.CANCELLED : BookingStatus.ACTIVE;
    }

    /**
     * Cancels the booking and calculates a fee based on how early the cancellation
     * is.
     * 
     * @param systemDate the current system date
     * @throws FlightBookingSystemException if the booking is already cancelled or
     *                                      the flight has departed
     */
    public void cancel(LocalDate systemDate) throws FlightBookingSystemException {
        if (bookingStatus != BookingStatus.ACTIVE) {
            throw new FlightBookingSystemException("Only active bookings can be cancelled.");
        }

        long daysBeforeDeparture = ChronoUnit.DAYS.between(systemDate, flight.getDepartureDate());

        if (daysBeforeDeparture < 0) {
            throw new FlightBookingSystemException(
                    "Cannot cancel booking after flight departure.");
        }

        // Determine the fee percentage based on the number of days left
        double feePercentage;
        if (daysBeforeDeparture >= 30) {
            feePercentage = 0.05; // 5% fee if cancelled a month in advance
        } else if (daysBeforeDeparture >= 14) {
            feePercentage = 0.10; // 10% fee
        } else if (daysBeforeDeparture >= 7) {
            feePercentage = 0.20; // 20% fee
        } else if (daysBeforeDeparture >= 3) {
            feePercentage = 0.40; // 40% fee
        } else {
            feePercentage = 0.70; // 70% fee for very last minute cancellations
        }

        // Apply the fee and update the status
        this.cancellationFee = Math.round(finalPrice * feePercentage * 100.0) / 100.0;
        this.bookingStatus = BookingStatus.CANCELLED;

        flight.removePassenger(customer);
    }

    /**
     * Returns the unique identifier for this booking.
     * 
     * @return the booking ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this booking.
     * 
     * @param id the new booking ID
     */
    public void setId(int id) {
        this.id = id;
    }
}
