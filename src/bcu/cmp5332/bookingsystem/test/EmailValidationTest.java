package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.model.Customer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmailValidationTest {

    @Test
    public void testValidEmails() {
        assertTrue(Customer.isValidEmail("test@example.com"));
        assertTrue(Customer.isValidEmail("user.name@domain.co.uk"));
        assertTrue(Customer.isValidEmail("abc123@gmail.com"));
        assertTrue(Customer.isValidEmail("a+b@c.com"));
    }

    @Test
    public void testInvalidEmails() {
        assertFalse(Customer.isValidEmail("test.con"));
        assertFalse(Customer.isValidEmail("abc@"));
        assertFalse(Customer.isValidEmail("@gmail.com"));
        assertFalse(Customer.isValidEmail("test@gmail")); // Missing .com or similar
        assertFalse(Customer.isValidEmail("test@gmail.c")); // Too short extension
        assertFalse(Customer.isValidEmail(null));
    }

    @Test
    public void testEmailNormalization() {
        Customer c = new Customer(1, "Test", "123", "GMAIL@COM.UK");
        assertEquals("gmail@com.uk", c.getEmail(), "Email should be normalized to lowercase in constructor");

        c.setEmail("XYZ@ABC.CO");
        assertEquals("xyz@abc.co", c.getEmail(), "Email should be normalized to lowercase in setter");
    }
}
