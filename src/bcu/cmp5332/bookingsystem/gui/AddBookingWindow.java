package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddBooking;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class AddBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JComboBox<String> customerCombo = new JComboBox<>();
    private JComboBox<String> flightCombo = new JComboBox<>();
    private JLabel priceLabel = new JLabel("Price: Rs 0.00");

    private JButton bookBtn = new JButton("Book");
    private JButton cancelBtn = new JButton("Cancel");

    public AddBookingWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // ignore
        }

        setTitle("Issue Booking");

        setSize(400, 300);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 2, 10, 10)); // Adjusted to 3 rows
        topPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        topPanel.add(new JLabel("Customer : "));
        topPanel.add(customerCombo);
        topPanel.add(new JLabel("Flight : "));
        topPanel.add(flightCombo);
        topPanel.add(new JLabel("Payable Amount: "));
        topPanel.add(priceLabel);

        // Populate combos
        List<Customer> customers = mw.getFlightBookingSystem().getCustomers();
        for (Customer c : customers) {
            customerCombo.addItem(c.getId() + " - " + c.getName());
        }

        List<Flight> flights = mw.getFlightBookingSystem().getFlights();
        for (Flight f : flights) {
            flightCombo.addItem(
                    f.getId() + " - " + f.getFlightNumber() + " (" + f.getOrigin() + " -> " + f.getDestination() + ")");
        }

        // Live Price Update Logic (ItemListener is more responsive for JComboBox
        // selection)
        flightCombo.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                updatePrice();
            }
        });

        // Ensure initial price is shown correctly
        if (flightCombo.getItemCount() > 0) {
            updatePrice();
        }

        try {
            java.net.URL iconUrl = getClass().getClassLoader().getResource("images/nepal_arlines.png");
            if (iconUrl != null) {
                setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(iconUrl));
            }
        } catch (Exception ex) {
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));
        bottomPanel.add(new JLabel("     "));
        bottomPanel.add(bookBtn);
        bottomPanel.add(cancelBtn);

        // Update action listener
        bookBtn.removeActionListener(this);
        bookBtn.addActionListener(e -> {
            try {
                String customerStr = (String) customerCombo.getSelectedItem();
                String flightStr = (String) flightCombo.getSelectedItem();

                if (customerStr == null || flightStr == null) {
                    throw new FlightBookingSystemException("Please select both customer and flight.");
                }

                int custId = Integer.parseInt(customerStr.split(" - ")[0]);
                int flightId = Integer.parseInt(flightStr.split(" - ")[0]);

                Command addBooking = new AddBooking(custId, flightId);
                mw.executeAndSave(addBooking);

                mw.displayAllBookings(); // Show bookings view to see the new record

                this.setVisible(false);
            } catch (FlightBookingSystemException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        setVisible(true);
    }

    private void updatePrice() {
        try {
            String flightStr = (String) flightCombo.getSelectedItem();
            if (flightStr != null) {
                int flightId = Integer.parseInt(flightStr.split(" - ")[0]);
                Flight flight = mw.getFlightBookingSystem().getFlightByID(flightId);
                double currentPrice = flight.calculatePrice(mw.getFlightBookingSystem().getSystemDate());
                priceLabel.setText("Rs " + String.format("%.2f", currentPrice));
                priceLabel.setForeground(new java.awt.Color(0, 102, 0)); // Professional green
                priceLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14));
            }
        } catch (Exception ex) {
            priceLabel.setText("Price: N/A");
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }
    }
}
