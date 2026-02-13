package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Customer;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDate;

/**
 * JUnit tests for the Flight class.
 * Demonstrates basic flight logic including capacity limits and dynamic
 * pricing.
 */
public class FlightTest {

    private Flight flight;
    private LocalDate departureDate;

    @BeforeEach
    public void setUp() {
        departureDate = LocalDate.now().plusDays(10);
        flight = new Flight(1, "LH100", "London", "Paris", departureDate, 2, 500.0);
    }

    @Test
    public void testFlightCreation() {
        assertEquals("LH100", flight.getFlightNumber());
        assertEquals("London", flight.getOrigin());
        assertEquals("Paris", flight.getDestination());
        assertEquals(departureDate, flight.getDepartureDate());
        assertEquals(2, flight.getCapacity());
        assertEquals(500.0, flight.getPrice());
    }

    @Test
    public void testAddPassenger() {
        Customer customer = new Customer(1, "John Doe", "123456", "john@example.com");
        flight.addPassenger(customer);
        assertEquals(1, flight.getPassengerCount());
        assertTrue(flight.getPassengers().contains(customer));
    }

    @Test
    public void testIsFull() {
        Customer c1 = new Customer(1, "A", "1", "a@a.com");
        Customer c2 = new Customer(2, "B", "2", "b@b.com");

        assertFalse(flight.isFull());
        flight.addPassenger(c1);
        assertFalse(flight.isFull());
        flight.addPassenger(c2);
        assertTrue(flight.isFull(), "Flight should be full after reaches capacity of 2");
    }

    @Test
    public void testDynamicPricingTimeBased() {
        // 10 days out (no surcharge)
        assertEquals(500.0, flight.calculatePrice(LocalDate.now()));

        // 5 days out (+20% hike)
        LocalDate fiveDaysBefore = departureDate.minusDays(5);
        assertEquals(600.0, flight.calculatePrice(fiveDaysBefore), "Should have 20% surcharge within 7 days");

        // 2 days out (+50% hike)
        LocalDate twoDaysBefore = departureDate.minusDays(2);
        assertEquals(750.0, flight.calculatePrice(twoDaysBefore), "Should have 50% surcharge within 3 days");
    }
}
