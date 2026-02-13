package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.EditCustomer;
import bcu.cmp5332.bookingsystem.model.Customer;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class EditCustomerWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private Customer customer;

    private JTextField nameText = new JTextField();
    private JTextField phoneText = new JTextField();
    private JTextField emailText = new JTextField();

    private JButton saveBtn = new JButton("Save Changes");
    private JButton cancelBtn = new JButton("Cancel");

    public EditCustomerWindow(MainWindow mw, Customer customer) {
        this.mw = mw;
        this.customer = customer;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }

        setTitle("Edit Customer #" + customer.getId());
        setSize(350, 200);

        try {
            java.net.URL iconUrl = getClass().getClassLoader().getResource("images/nepal_arlines.png");
            if (iconUrl != null) {
                setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(iconUrl));
            }
        } catch (Exception ex) {
        }

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 2, 10, 10));
        topPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        topPanel.add(new JLabel("Name : "));
        nameText.setText(customer.getName());
        topPanel.add(nameText);

        topPanel.add(new JLabel("Phone : "));
        phoneText.setText(customer.getPhone());
        topPanel.add(phoneText);

        topPanel.add(new JLabel("Email : "));
        emailText.setText(customer.getEmail());
        topPanel.add(emailText);

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
            updateCustomer();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }
    }

    private void updateCustomer() {
        try {
            String name = nameText.getText();
            String phone = phoneText.getText();
            String email = emailText.getText();

            Command editCustomer = new EditCustomer(customer.getId(), name, phone, email);
            mw.executeAndSave(editCustomer);

            mw.displayCustomers();
            this.setVisible(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
