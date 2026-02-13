package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.BookingStatus;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDate;

/**
 * JUnit tests for the Booking class.
 * Demonstrates cancellation fee logic based on tiered days.
 */
public class BookingTest {

    private Customer customer;
    private Flight flight;
    private LocalDate now;

    @BeforeEach
    public void setUp() {
        now = LocalDate.now();
        customer = new Customer(1, "Bob", "111", "bob@test.com");
        // Flight departs in 30 days
        flight = new Flight(1, "F1", "A", "B", now.plusDays(30), 10, 1000.0);
    }

    @Test
    public void testBookingCreation() {
        Booking booking = new Booking(customer, flight, now, 1000.0);
        assertEquals(customer, booking.getCustomer());
        assertEquals(flight, booking.getFlight());
        assertEquals(1000.0, booking.getFinalPrice());
        assertEquals(BookingStatus.ACTIVE, booking.getBookingStatus());
    }

    @Test
    public void testCancellationFeeTier30Days() throws Exception {
        Booking booking = new Booking(customer, flight, now, 1000.0);
        flight.addPassenger(customer);

        // Cancel 30 days before -> 5% fee
        booking.cancel(now);
        assertEquals(50.0, booking.getCancellationFee(), "Should be 5% (50.0) for 30 days advance");
        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
    }

    @Test
    public void testCancellationFeeTier2Days() throws Exception {
        // Flight departs in 2 days
        Flight soonFlight = new Flight(2, "F2", "A", "B", now.plusDays(2), 10, 1000.0);
        Booking booking = new Booking(customer, soonFlight, now, 1000.0);
        soonFlight.addPassenger(customer);

        // Cancel 2 days before -> 70% fee
        booking.cancel(now);
        assertEquals(700.0, booking.getCancellationFee(), "Should be 70% (700.0) for 2 days advance");
    }
}
