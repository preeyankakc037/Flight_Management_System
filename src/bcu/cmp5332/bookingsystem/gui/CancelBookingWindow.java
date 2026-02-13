package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.CancelBooking;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
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

public class CancelBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JComboBox<String> customerCombo = new JComboBox<>();
    private JComboBox<String> flightCombo = new JComboBox<>();

    private JButton cancelBookingBtn = new JButton("Cancel Booking");
    private JButton closeBtn = new JButton("Close");

    private int preSelectedCustomerId = -1;
    private int preSelectedFlightId = -1;

    public CancelBookingWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    public CancelBookingWindow(MainWindow mw, int customerId, int flightId) {
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
        if (preSelectedFlightId != -1) {
            for (int i = 0; i < flightCombo.getItemCount(); i++) {
                if (flightCombo.getItemAt(i).startsWith(preSelectedFlightId + " - ")) {
                    flightCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // ignore
        }

        setTitle("Cancel Booking");

        setSize(400, 200);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 2));
        topPanel.add(new JLabel("Customer : "));
        topPanel.add(customerCombo);
        topPanel.add(new JLabel("Flight : "));
        topPanel.add(flightCombo);

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

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));
        bottomPanel.add(new JLabel("     "));
        bottomPanel.add(cancelBookingBtn);
        bottomPanel.add(closeBtn);

        cancelBookingBtn.addActionListener(this);
        closeBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == cancelBookingBtn) {
            cancelBooking();
        } else if (ae.getSource() == closeBtn) {
            this.setVisible(false);
        }
    }

    private void cancelBooking() {
        try {
            String customerStr = (String) customerCombo.getSelectedItem();
            String flightStr = (String) flightCombo.getSelectedItem();

            if (customerStr == null || flightStr == null) {
                throw new FlightBookingSystemException("Please select both customer and flight.");
            }

            int custId = Integer.parseInt(customerStr.split(" - ")[0]);
            int flightId = Integer.parseInt(flightStr.split(" - ")[0]);

            // Find the booking to calculate fee preview
            Customer customer = mw.getFlightBookingSystem().getCustomerByID(custId);
            Flight flight = mw.getFlightBookingSystem().getFlightByID(flightId);

            Booking targetBooking = null;
            for (Booking b : customer.getBookings()) {
                if (b.getFlight().getId() == flight.getId() &&
                        b.getBookingStatus() == bcu.cmp5332.bookingsystem.model.BookingStatus.ACTIVE) {
                    targetBooking = b;
                    break;
                }
            }

            if (targetBooking == null) {
                throw new FlightBookingSystemException("No active booking found for this customer and flight.");
            }

            // Calculate fee preview (without actually cancelling)
            long daysBeforeDeparture = java.time.temporal.ChronoUnit.DAYS.between(
                    mw.getFlightBookingSystem().getSystemDate(),
                    flight.getDepartureDate());

            double feePercentage;
            String feeDescription;

            if (daysBeforeDeparture >= 30) {
                feePercentage = 0.05;
                feeDescription = "5% (≥30 days before departure)";
            } else if (daysBeforeDeparture >= 14) {
                feePercentage = 0.10;
                feeDescription = "10% (14-29 days before departure)";
            } else if (daysBeforeDeparture >= 7) {
                feePercentage = 0.20;
                feeDescription = "20% (7-13 days before departure)";
            } else if (daysBeforeDeparture >= 3) {
                feePercentage = 0.40;
                feeDescription = "40% (3-6 days before departure)";
            } else {
                feePercentage = 0.70;
                feeDescription = "70% (<3 days before departure)";
            }

            double estimatedFee = Math.round(targetBooking.getFinalPrice() * feePercentage * 100.0) / 100.0;
            double refundAmount = targetBooking.getFinalPrice() - estimatedFee;

            // Show confirmation dialog with fee details
            String message = String.format(
                    "Cancellation Fee Policy:\n\n" +
                            "Booking Price: Rs %.2f\n" +
                            "Cancellation Fee: Rs %.2f (%s)\n" +
                            "Refund Amount: Rs %.2f\n\n" +
                            "Do you wish to proceed with cancellation?",
                    targetBooking.getFinalPrice(),
                    estimatedFee,
                    feeDescription,
                    refundAmount);

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice != JOptionPane.YES_OPTION) {
                return; // User cancelled
            }

            Command cancelBooking = new CancelBooking(custId, flightId);
            mw.executeAndSave(cancelBooking);

            // Refresh views - stay on bookings page
            mw.displayAllBookings();

            this.setVisible(false);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
