package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.EditFlight;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class EditFlightWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private Flight flight;
    
    private JTextField flightNoText = new JTextField();
    private JTextField originText = new JTextField();
    private JTextField destinationText = new JTextField();
    private JTextField depDateText = new JTextField();
    private JTextField capacityText = new JTextField();
    private JTextField priceText = new JTextField();

    private JButton saveBtn = new JButton("Save Changes");
    private JButton cancelBtn = new JButton("Cancel");

    public EditFlightWindow(MainWindow mw, Flight flight) {
        this.mw = mw;
        this.flight = flight;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {}

        setTitle("Edit Flight #" + flight.getId());
        setSize(350, 300);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(6, 2));
        
        topPanel.add(new JLabel("Flight No : "));
        flightNoText.setText(flight.getFlightNumber());
        topPanel.add(flightNoText);
        
        topPanel.add(new JLabel("Origin : "));
        originText.setText(flight.getOrigin());
        topPanel.add(originText);
        
        topPanel.add(new JLabel("Destination : "));
        destinationText.setText(flight.getDestination());
        topPanel.add(destinationText);
        
        topPanel.add(new JLabel("Departure Date : "));
        depDateText.setText(flight.getDepartureDate().toString());
        topPanel.add(depDateText);
        
        topPanel.add(new JLabel("Capacity : "));
        capacityText.setText(String.valueOf(flight.getCapacity()));
        topPanel.add(capacityText);
        
        topPanel.add(new JLabel("Price : "));
        priceText.setText(String.valueOf(flight.getPrice()));
        topPanel.add(priceText);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(saveBtn);
        bottomPanel.add(cancelBtn);

        saveBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == saveBtn) {
            updateFlight();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }
    }

    private void updateFlight() {
        try {
            String flightNumber = flightNoText.getText();
            String origin = originText.getText();
            String destination = destinationText.getText();
            LocalDate departureDate;
            try {
                departureDate = LocalDate.parse(depDateText.getText());
            } catch (DateTimeParseException dtpe) {
                throw new FlightBookingSystemException("Date must be in YYYY-MM-DD format");
            }
            
            int capacity = Integer.parseInt(capacityText.getText());
            double price = Double.parseDouble(priceText.getText());

            Command editFlight = new EditFlight(flight.getId(), flightNumber, origin, destination, departureDate, capacity, price);
            mw.executeAndSave(editFlight);
            
            mw.displayFlights();
            this.setVisible(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
