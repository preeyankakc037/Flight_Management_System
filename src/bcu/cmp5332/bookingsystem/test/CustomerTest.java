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
 * JUnit tests for the Customer class.
 * Focuses on booking management and active booking calculations.
 */
public class CustomerTest {

    private Customer customer;
    private Flight flight;

    @BeforeEach
    public void setUp() {
        customer = new Customer(1, "Alice Smith", "987654321", "alice@test.com");
        flight = new Flight(1, "NY123", "London", "New York", LocalDate.now().plusDays(5), 100, 1000.0);
    }

    @Test
    public void testCustomerCreation() {
        assertEquals("Alice Smith", customer.getName());
        assertEquals("987654321", customer.getPhone());
        assertEquals("alice@test.com", customer.getEmail());
        assertTrue(customer.getBookings().isEmpty());
    }

    @Test
    public void testAddBooking() {
        Booking booking = new Booking(customer, flight, LocalDate.now(), 1000.0);
        customer.addBooking(booking);

        assertEquals(1, customer.getBookings().size());
        assertEquals(booking, customer.getBookings().get(0));
    }

    @Test
    public void testGetNumberOfActiveBookings() {
        LocalDate now = LocalDate.now();
        Booking b1 = new Booking(customer, flight, now, 1000.0);
        customer.addBooking(b1);

        // Flight is in 5 days, status is ACTIVE -> should be 1
        assertEquals(1, customer.getNumberOfActiveBookings(now));

        // Cancel the booking
        b1.setBookingStatus(BookingStatus.CANCELLED);
        assertEquals(0, customer.getNumberOfActiveBookings(now), "Cancelled bookings should not count as active");
    }
}
