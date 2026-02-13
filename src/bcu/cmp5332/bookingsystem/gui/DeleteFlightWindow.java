package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.DeleteFlight;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
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

public class DeleteFlightWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JComboBox<String> flightCombo = new JComboBox<>();

    private JButton delBtn = new JButton("Delete");
    private JButton cancelBtn = new JButton("Cancel");

    public DeleteFlightWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // ignore
        }

        setTitle("Delete Flight");

        setSize(350, 150);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));
        topPanel.add(new JLabel("Flight : "));
        topPanel.add(flightCombo);
        
        // Populate
        List<Flight> flights = mw.getFlightBookingSystem().getFlights();
        for (Flight f : flights) {
            flightCombo.addItem(f.getId() + " - " + f.getFlightNumber());
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));
        bottomPanel.add(new JLabel("     "));
        bottomPanel.add(delBtn);
        bottomPanel.add(cancelBtn);

        delBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == delBtn) {
            deleteFlight();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }
    }

    private void deleteFlight() {
        try {
            String flightStr = (String) flightCombo.getSelectedItem();
            if (flightStr == null) {
                throw new FlightBookingSystemException("No flight selected.");
            }
            int id = Integer.parseInt(flightStr.split(" - ")[0]);
            
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure? This will cancel all bookings for this flight.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Command delFlight = new DeleteFlight(id);
                mw.executeAndSave(delFlight);
                mw.displayFlights();
                this.setVisible(false);
            }
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
