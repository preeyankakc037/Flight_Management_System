package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

public class AllBookingsWindow extends JFrame {

    private FlightBookingSystem fbs;

    public AllBookingsWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }

        setTitle("All Bookings");
        setSize(800, 400);

        List<Booking> allBookings = new ArrayList<>();
        for (Customer customer : fbs.getCustomers()) {
            for (Booking booking : customer.getBookings()) {
                if (booking.getBookingStatus() == bcu.cmp5332.bookingsystem.model.BookingStatus.ACTIVE) {
                    allBookings.add(booking);
                }
            }
        }

        String[] columns = new String[] { "Customer ID", "Customer Name", "Flight No", "Destination", "Departure Date",
                "Price", "Booking Date" };
        Object[][] data = new Object[allBookings.size()][7];

        for (int i = 0; i < allBookings.size(); i++) {
            Booking b = allBookings.get(i);
            data[i][0] = b.getCustomer().getId();
            data[i][1] = b.getCustomer().getName();
            data[i][2] = b.getFlight().getFlightNumber();
            data[i][3] = b.getFlight().getDestination();
            data[i][4] = b.getFlight().getDepartureDate();
            data[i][5] = String.format("%.2f", b.getFinalPrice());
            data[i][6] = b.getBookingDate();
        }

        JTable table = new JTable(data, columns);
        this.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
