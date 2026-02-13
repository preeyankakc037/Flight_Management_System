package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.DeleteCustomer;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
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

public class DeleteCustomerWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JComboBox<String> customerCombo = new JComboBox<>();

    private JButton delBtn = new JButton("Delete");
    private JButton cancelBtn = new JButton("Cancel");

    public DeleteCustomerWindow(MainWindow mw) {
        this.mw = mw;
        initialize(-1);
    }

    public DeleteCustomerWindow(MainWindow mw, int selectedId) {
        this.mw = mw;
        initialize(selectedId);
    }

    private void initialize(int selectedId) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // ignore
        }

        setTitle("Delete Customer");

        setSize(350, 150);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));
        topPanel.add(new JLabel("Customer : "));
        topPanel.add(customerCombo);

        // Populate
        List<Customer> customers = mw.getFlightBookingSystem().getCustomers();
        for (Customer c : customers) {
            String item = c.getId() + " - " + c.getName();
            customerCombo.addItem(item);
            if (c.getId() == selectedId) {
                customerCombo.setSelectedItem(item);
            }
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
            deleteCustomer();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }
    }

    private void deleteCustomer() {
        try {
            String custStr = (String) customerCombo.getSelectedItem();
            if (custStr == null) {
                throw new FlightBookingSystemException("No customer selected.");
            }
            int id = Integer.parseInt(custStr.split(" - ")[0]);

            // Security Check: Ask for ID again
            String inputIdStr = JOptionPane.showInputDialog(this,
                    "Security Check: Please re-enter the Customer ID to confirm deletion:", "Confirm Identity",
                    JOptionPane.WARNING_MESSAGE);

            if (inputIdStr == null) {
                return; // User cancelled input
            }

            try {
                int inputId = Integer.parseInt(inputIdStr.trim());
                if (inputId != id) {
                    throw new FlightBookingSystemException("ID mismatch! Deletion cancelled.");
                }
            } catch (NumberFormatException e) {
                throw new FlightBookingSystemException("Invalid ID format! Deletion cancelled.");
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure? This will cancel all bookings made by this customer.", "Final Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Command delCmd = new DeleteCustomer(id);
                mw.executeAndSave(delCmd);
                mw.displayCustomers();
                this.setVisible(false);
            }
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
