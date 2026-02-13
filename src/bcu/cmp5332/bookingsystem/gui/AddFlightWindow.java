package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class AddFlightWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField flightNoText = new JTextField();
    private JTextField originText = new JTextField();
    private JTextField destinationText = new JTextField();
    private javax.swing.JSpinner depDateSpinner;
    private JTextField capacityText = new JTextField();
    private JTextField priceText = new JTextField();

    private JButton addBtn = new JButton("Add");
    private JButton cancelBtn = new JButton("Cancel");

    public AddFlightWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {

        }

        setTitle("Add a New Flight");

        setSize(350, 400);

        try {
            java.net.URL iconUrl = getClass().getClassLoader().getResource("images/nepal_arlines.png");
            if (iconUrl != null) {
                setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(iconUrl));
            }
        } catch (Exception ex) {
        }

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(6, 2, 10, 10)); // Reduced rows
        topPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        topPanel.add(new JLabel("Flight No : "));
        topPanel.add(flightNoText);
        topPanel.add(new JLabel("Origin : "));
        topPanel.add(originText);
        topPanel.add(new JLabel("Destination : "));
        topPanel.add(destinationText);

        topPanel.add(new JLabel("Departure Date: "));
        // Use JSpinner for Date Selection
        javax.swing.SpinnerDateModel dateModel = new javax.swing.SpinnerDateModel();
        dateModel.setValue(java.sql.Date.valueOf(mw.getFlightBookingSystem().getSystemDate()));
        depDateSpinner = new javax.swing.JSpinner(dateModel);
        javax.swing.JSpinner.DateEditor dateEditor = new javax.swing.JSpinner.DateEditor(depDateSpinner, "yyyy-MM-dd");
        depDateSpinner.setEditor(dateEditor);
        topPanel.add(depDateSpinner);

        topPanel.add(new JLabel("Capacity : "));
        topPanel.add(capacityText);
        topPanel.add(new JLabel("Price : "));
        topPanel.add(priceText);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));
        bottomPanel.add(new JLabel("     "));
        bottomPanel.add(addBtn);
        bottomPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addBook();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }

    }

    private void addBook() {
        try {
            String flightNumber = flightNoText.getText();
            String origin = originText.getText();
            String destination = destinationText.getText();

            java.util.Date selectedDate = (java.util.Date) depDateSpinner.getValue();
            LocalDate departureDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();

            int capacity = 0;
            try {
                capacity = Integer.parseInt(capacityText.getText());
            } catch (NumberFormatException nfe) {
                throw new FlightBookingSystemException("Capacity must be an integer");
            }

            double price = 0;
            try {
                price = Double.parseDouble(priceText.getText());
            } catch (NumberFormatException nfe) {
                throw new FlightBookingSystemException("Price must be a number");
            }

            // create and execute the AddFlight Command
            Command addFlight = new AddFlight(flightNumber, origin, destination, departureDate, capacity, price);
            mw.executeAndSave(addFlight);

            // refresh the view with the list of flights
            mw.displayFlights();
            // hide (close) the AddFlightWindow
            this.setVisible(false);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
