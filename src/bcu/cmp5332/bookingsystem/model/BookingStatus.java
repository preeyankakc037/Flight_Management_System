package bcu.cmp5332.bookingsystem.model;

/**
 * Represents the status of a booking in the flight booking system.
 * 
 * @author Student
 */
public enum BookingStatus {
    /**
     * Booking is active and the customer is scheduled to fly.
     */
    ACTIVE,

    /**
     * Booking has been cancelled by the customer.
     */
    CANCELLED,

    /**
     * Flight has departed and the booking is complete.
     */
    COMPLETED
}
