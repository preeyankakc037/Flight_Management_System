package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

public class FlightPassengersWindow extends JFrame {

    private Flight flight;

    public FlightPassengersWindow(Flight flight) {
        this.flight = flight;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }

        setTitle("Passengers for Flight " + flight.getFlightNumber());
        setSize(500, 300);

        List<Customer> passengers = flight.getPassengers();
        String[] columns = new String[]{"ID", "Name", "Phone", "Email"};
        Object[][] data = new Object[passengers.size()][4];

        for (int i = 0; i < passengers.size(); i++) {
            Customer c = passengers.get(i);
            data[i][0] = c.getId();
            data[i][1] = c.getName();
            data[i][2] = c.getPhone();
            data[i][3] = c.getEmail();
        }

        JTable table = new JTable(data, columns);
        this.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
