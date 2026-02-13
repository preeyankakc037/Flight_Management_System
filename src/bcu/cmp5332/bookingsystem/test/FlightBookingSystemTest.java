package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.BookingStatus;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDate;

/**
 * JUnit tests for the FlightBookingSystem class.
 * Demonstrates high-level validation rules and system integrity.
 */
public class FlightBookingSystemTest {

    private FlightBookingSystem fbs;
    private LocalDate now;

    @BeforeEach
    public void setUp() {
        fbs = new FlightBookingSystem();
        now = LocalDate.now();
        fbs.setSystemDate(now);
    }

    @Test
    public void testAddFlightDuplicatePrevention() throws FlightBookingSystemException {
        Flight f1 = new Flight(1, "BA123", "LDN", "PAR", now.plusDays(1), 100, 500);
        fbs.addFlight(f1);

        // Same number and same date should be blocked
        Flight f2 = new Flight(2, "BA123", "LDN", "PAR", now.plusDays(1), 100, 500);
        assertThrows(FlightBookingSystemException.class, () -> fbs.addFlight(f2));
    }

    @Test
    public void testAddCustomer() throws FlightBookingSystemException {
        Customer c1 = new Customer(1, "Alice", "123", "alice@a.com");
        fbs.addCustomer(c1);
        assertEquals(1, fbs.getCustomers().size());
        assertEquals(c1, fbs.getCustomerByID(1));
    }

    @Test
    public void testValidateBookingCapacity() throws FlightBookingSystemException {
        Flight f1 = new Flight(1, "F1", "A", "B", now.plusDays(1), 1, 500);
        fbs.addFlight(f1);

        Customer c1 = new Customer(1, "A", "1", "a@a.com");
        Customer c2 = new Customer(2, "B", "2", "b@b.com");

        // c1 takes the only seat
        f1.addPassenger(c1);

        // Booking for c2 should be blocked by capacity
        assertThrows(FlightBookingSystemException.class, () -> fbs.validateBooking(c2, f1, now));
    }
}
