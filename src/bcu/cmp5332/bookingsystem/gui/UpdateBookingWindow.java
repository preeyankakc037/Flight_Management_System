package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.UpdateBooking;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class UpdateBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JComboBox<String> customerCombo = new JComboBox<>();
    private JComboBox<String> oldFlightCombo = new JComboBox<>();
    private JComboBox<String> newFlightCombo = new JComboBox<>();

    private JButton updateBtn = new JButton("Update");
    private JButton cancelBtn = new JButton("Cancel");

    private int preSelectedCustomerId = -1;
    private int preSelectedFlightId = -1;

    public UpdateBookingWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    public UpdateBookingWindow(MainWindow mw, int customerId, int flightId) {
        this.mw = mw;
        this.preSelectedCustomerId = customerId;
        this.preSelectedFlightId = flightId;
        initialize();
        preSelect();
    }

    private void preSelect() {
        if (preSelectedCustomerId != -1) {
            for (int i = 0; i < customerCombo.getItemCount(); i++) {
                if (customerCombo.getItemAt(i).startsWith(preSelectedCustomerId + " - ")) {
                    customerCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        // Selection of oldFlightCombo happens automatically because customerCombo's
        // action listener is triggered by setSelectedIndex above.
        // We need to wait for it or just set it here.
        if (preSelectedFlightId != -1) {
            for (int i = 0; i < oldFlightCombo.getItemCount(); i++) {
                if (oldFlightCombo.getItemAt(i).startsWith(preSelectedFlightId + " - ")) {
                    oldFlightCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }

        setTitle("Update Booking");
        setSize(400, 250);

        try {
            java.net.URL iconUrl = getClass().getClassLoader().getResource("images/nepal_arlines.png");
            if (iconUrl != null) {
                setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(iconUrl));
            }
        } catch (Exception ex) {
        }

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(4, 2, 10, 10));
        topPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        topPanel.add(new JLabel("Customer : "));
        topPanel.add(customerCombo);
        topPanel.add(new JLabel("Current Flight : "));
        topPanel.add(oldFlightCombo);
        topPanel.add(new JLabel("New Flight : "));
        topPanel.add(newFlightCombo);
        topPanel.add(new JLabel("Booking Date : "));
        topPanel.add(new JLabel("Preserved Original Date"));

        // Populate Data
        for (Customer c : mw.getFlightBookingSystem().getCustomers()) {
            customerCombo.addItem(c.getId() + " - " + c.getName());
        }

        for (Flight f : mw.getFlightBookingSystem().getFlights()) {
            newFlightCombo.addItem(f.getId() + " - " + f.getFlightNumber());
        }

        // Action Listener for Customer to populate Old Bookings
        customerCombo.addActionListener(e -> {
            oldFlightCombo.removeAllItems();
            String selected = (String) customerCombo.getSelectedItem();
            if (selected != null) {
                int id = Integer.parseInt(selected.split(" - ")[0]);
                try {
                    Customer c = mw.getFlightBookingSystem().getCustomerByID(id);
                    for (Booking b : c.getBookings()) {
                        if (b.getBookingStatus() == bcu.cmp5332.bookingsystem.model.BookingStatus.ACTIVE) {
                            oldFlightCombo.addItem(b.getFlight().getId() + " - " + b.getFlight().getFlightNumber());
                        }
                    }
                } catch (Exception ex) {
                }
            }
        });

        // Trigger initial load
        if (customerCombo.getItemCount() > 0) {
            customerCombo.setSelectedIndex(0);
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));
        bottomPanel.add(new JLabel("     "));
        bottomPanel.add(updateBtn);
        bottomPanel.add(cancelBtn);

        updateBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == updateBtn) {
            updateBooking();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }
    }

    private void updateBooking() {
        try {
            String custStr = (String) customerCombo.getSelectedItem();
            String oldStr = (String) oldFlightCombo.getSelectedItem();
            String newStr = (String) newFlightCombo.getSelectedItem();

            if (custStr == null || oldStr == null || newStr == null) {
                throw new FlightBookingSystemException("Please select all options.");
            }

            int custId = Integer.parseInt(custStr.split(" - ")[0]);
            int oldId = Integer.parseInt(oldStr.split(" - ")[0]);
            int newId = Integer.parseInt(newStr.split(" - ")[0]);

            if (oldId == newId) {
                throw new FlightBookingSystemException("Select a different flight to update to.");
            }

            Command updateCmd = new UpdateBooking(custId, oldId, newId);
            mw.executeAndSave(updateCmd);

            mw.displayAllBookings();
            this.setVisible(false);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
