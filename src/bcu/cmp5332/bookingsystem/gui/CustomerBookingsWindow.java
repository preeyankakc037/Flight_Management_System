package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

public class CustomerBookingsWindow extends JFrame {

    private Customer customer;

    public CustomerBookingsWindow(Customer customer) {
        this.customer = customer;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }

        setTitle("Bookings for Customer: " + customer.getName());
        setSize(600, 300);

        List<Booking> bookings = customer.getBookings();
        String[] columns = new String[] { "Flight No", "Origin", "Destination", "Date", "Booking Date", "Price", "Fee",
                "Status" };
        Object[][] data = new Object[bookings.size()][8];

        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            data[i][0] = b.getFlight().getFlightNumber();
            data[i][1] = b.getFlight().getOrigin();
            data[i][2] = b.getFlight().getDestination();
            data[i][3] = b.getFlight().getDepartureDate();
            data[i][4] = b.getBookingDate();
            data[i][5] = b.getFinalPrice();
            data[i][6] = b.getCancellationFee();
            data[i][7] = b.getBookingStatus() == bcu.cmp5332.bookingsystem.model.BookingStatus.CANCELLED ? "Cancelled"
                    : "Active";
        }

        JTable table = new JTable(data, columns);
        this.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
