package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.time.LocalDate;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import bcu.cmp5332.bookingsystem.model.Booking;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JTabbedPane;

/**
 * Main GUI window for the Flight Booking System.
 * This class is launched from Main.java via the LoadGUI command.
 * It shares the same FlightBookingSystem instance as the CLI.
 */
public class MainWindow extends JFrame implements ActionListener {

    private JMenuBar menuBar;
    private JMenu adminMenu;
    private JMenu flightsMenu;
    private JMenu bookingsMenu;
    private JMenu customersMenu;

    private JMenuItem adminExit;
    private JMenuItem adminDashboard;

    private JMenuItem flightsView;
    private JMenuItem flightsAdd;
    private JMenuItem flightsDel;

    private JMenuItem bookingsView;
    private JMenuItem bookingsIssue;
    private JMenuItem bookingsUpdate;
    private JMenuItem bookingsCancel;

    private JMenuItem custView;
    private JMenuItem custAdd;
    private JMenuItem custDel;
    private JMenuItem custBookings;

    private FlightBookingSystem fbs;
    private String currentView = "welcome"; // Track current view for auto-refresh

    public MainWindow(FlightBookingSystem fbs) {
        initialize();
        this.fbs = fbs;
        displayWelcomeScreen();
    }

    public FlightBookingSystem getFlightBookingSystem() {
        return fbs;
    }

    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {

        }

        setTitle("Flight Booking Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        try {
            java.net.URL iconUrl = getClass().getClassLoader().getResource("images/nepal_arlines.png");
            if (iconUrl != null) {
                setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(iconUrl));
            }
        } catch (Exception ex) {
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    FlightBookingSystemData.store(fbs);
                    System.out.println("Data saved successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "Error saving data: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                System.exit(0); // Ensure application exits after saving
            }
        });

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        adminMenu = new JMenu("Admin");
        menuBar.add(adminMenu);

        adminDashboard = new JMenuItem("Dashboard");
        adminMenu.add(adminDashboard);
        adminDashboard.addActionListener(this);

        adminExit = new JMenuItem("Exit");
        adminMenu.add(adminExit);
        adminExit.addActionListener(this);

        JMenu simulationMenu = new JMenu("Simulation");
        menuBar.add(simulationMenu);

        JMenuItem setDateItem = new JMenuItem("Set Simulation Date");
        simulationMenu.add(setDateItem);
        setDateItem.addActionListener(e -> {
            javax.swing.SpinnerDateModel dateModel = new javax.swing.SpinnerDateModel();
            dateModel.setValue(java.sql.Date.valueOf(fbs.getSystemDate()));
            javax.swing.JSpinner dateSpinner = new javax.swing.JSpinner(dateModel);
            javax.swing.JSpinner.DateEditor dateEditor = new javax.swing.JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(dateEditor);

            int result = JOptionPane.showConfirmDialog(this, dateSpinner, "Set Simulation Date",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
                LocalDate newDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();
                fbs.setSystemDate(newDate);
                refreshCurrentView();
                JOptionPane.showMessageDialog(this, "Simulation date updated to: " + newDate);
            }
        });

        flightsMenu = new JMenu("Flights");
        menuBar.add(flightsMenu);

        flightsView = new JMenuItem("View");
        flightsAdd = new JMenuItem("Add");
        flightsDel = new JMenuItem("Delete");
        flightsMenu.add(flightsView);
        flightsMenu.add(flightsAdd);
        flightsMenu.add(flightsDel);

        for (int i = 0; i < flightsMenu.getItemCount(); i++) {
            flightsMenu.getItem(i).addActionListener(this);
        }

        bookingsMenu = new JMenu("Bookings");
        menuBar.add(bookingsMenu);

        bookingsView = new JMenuItem("View");
        bookingsIssue = new JMenuItem("Issue");
        bookingsUpdate = new JMenuItem("Update");
        bookingsCancel = new JMenuItem("Cancel");
        bookingsMenu.add(bookingsView);
        bookingsMenu.add(bookingsIssue);
        bookingsMenu.add(bookingsUpdate);
        bookingsMenu.add(bookingsCancel);

        for (int i = 0; i < bookingsMenu.getItemCount(); i++) {
            bookingsMenu.getItem(i).addActionListener(this);
        }

        customersMenu = new JMenu("Customers");
        menuBar.add(customersMenu);

        custView = new JMenuItem("View");
        custAdd = new JMenuItem("Add");
        custDel = new JMenuItem("Delete");

        customersMenu.add(custView);
        customersMenu.add(custAdd);
        customersMenu.add(custDel);

        custView.addActionListener(this);
        custAdd.addActionListener(this);
        custDel.addActionListener(this);
        custBookings = new JMenuItem("View Bookings");
        customersMenu.add(custBookings);
        custBookings.addActionListener(this);

        setSize(800, 500);

        setVisible(true);
        setAutoRequestFocus(true);
        toFront();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    FlightBookingSystemData.store(fbs);
                    System.out.println("Data saved successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "Error saving data: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    /**
     * Refreshes the current view based on the current simulation date.
     */
    private void refreshCurrentView() {
        if (this.getContentPane().getComponentCount() > 0) {
            Component c = this.getContentPane().getComponent(0);
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                for (Component comp : p.getComponents()) {
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        String text = label.getText();
                        if (text.contains("All Flights")) {
                            displayFlights();
                            return;
                        } else if (text.contains("All Customers")) {
                            displayCustomers();
                            return;
                        } else if (text.contains("Bookings Overview")) {
                            displayAllBookings();
                            return;
                        }
                    }
                }
            }
        }
        displayWelcomeScreen(); // Default fallback
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == adminExit) {
            try {
                FlightBookingSystemData.store(fbs);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        } else if (ae.getSource() == adminDashboard) {
            displayDashboard();
        } else if (ae.getSource() == flightsView) {
            displayFlights();

        } else if (ae.getSource() == flightsAdd) {
            new AddFlightWindow(this);

        } else if (ae.getSource() == flightsDel) {
            new DeleteFlightWindow(this);
        } else if (ae.getSource() == bookingsView) {
            displayAllBookings();
        } else if (ae.getSource() == bookingsIssue) {
            new AddBookingWindow(this);
        } else if (ae.getSource() == bookingsUpdate) {
            JTable table = findSelectedTable();
            if (table != null) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    try {
                        if (table.getColumnCount() >= 3 && "Cust ID".equals(table.getColumnName(0))) {
                            int custId = Integer.parseInt(table.getValueAt(row, 0).toString());
                            int flightId = Integer.parseInt(table.getValueAt(row, 2).toString());
                            new UpdateBookingWindow(this, custId, flightId);
                            return;
                        }
                    } catch (Exception e) {
                    }
                }
            }
            new UpdateBookingWindow(this);
        } else if (ae.getSource() == bookingsCancel) {
            JTable table = findSelectedTable();
            if (table != null) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    try {
                        if (table.getColumnCount() >= 3 && "Cust ID".equals(table.getColumnName(0))) {
                            int custId = Integer.parseInt(table.getValueAt(row, 0).toString());
                            int flightId = Integer.parseInt(table.getValueAt(row, 2).toString());
                            new CancelBookingWindow(this, custId, flightId);
                            return;
                        }
                    } catch (Exception e) {
                    }
                }
            }
            new CancelBookingWindow(this);
        } else if (ae.getSource() == custView) {
            displayCustomers();

        } else if (ae.getSource() == custAdd) {
            new AddCustomerWindow(this);

        } else if (ae.getSource() == custDel) {
            // Check if we are currently viewing the customer table and a row is selected
            if (this.getContentPane().getComponentCount() > 0
                    && this.getContentPane().getComponent(0) instanceof javax.swing.JScrollPane) {

                javax.swing.JScrollPane scrollPane = (javax.swing.JScrollPane) this.getContentPane().getComponent(0);
                if (scrollPane.getViewport().getView() instanceof javax.swing.JTable) {
                    javax.swing.JTable table = (javax.swing.JTable) scrollPane.getViewport().getView();
                    // Basic check to see if this is likely the customer table (check column count
                    // or name)
                    // Customer table has 8 columns
                    if (table.getModel().getColumnCount() == 8) {
                        int row = table.getSelectedRow();
                        if (row != -1) {
                            try {
                                int id = Integer.parseInt(table.getValueAt(row, 0).toString());
                                new DeleteCustomerWindow(this, id);
                                return;
                            } catch (Exception e) {
                                // Ignore parsing errors, fall through to default
                            }
                        }
                    }
                }
            }
            // Default behavior if no specific row selected
            new DeleteCustomerWindow(this);
        } else if (ae.getSource() == custBookings) {
            String idStr = JOptionPane.showInputDialog(this, "Enter Customer ID:");
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    Customer customer = fbs.getCustomerByID(id);
                    displayCustomerBookings(customer);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid ID format.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Displays the list of all customers in the system.
     * Shows customer details and allows viewing bookings or editing.
     */
    public void displayCustomers() {
        List<Customer> customersList = fbs.getCustomers();
        // headers for the table
        String[] columns = new String[] { "ID", "Name", "Phone", "Email", "Bookings Count", "Active Bookings",
                "View Booking", "Edit" };

        Object[][] data = new Object[customersList.size()][8];
        for (int i = 0; i < customersList.size(); i++) {
            Customer customer = customersList.get(i);
            data[i][0] = customer.getId();
            data[i][1] = customer.getName();
            data[i][2] = customer.getPhone();
            data[i][3] = customer.getEmail();
            data[i][4] = customer.getBookings().size();
            data[i][5] = customer.getNumberOfActiveBookings(fbs.getSystemDate());
            data[i][6] = "View";
            data[i][7] = "Edit";
        }

        JTable table = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        // Make the "View" column look like a clickable box/button
        DefaultTableCellRenderer boxRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (isSelected) {
                    setBackground(new Color(200, 200, 200)); // Darker on click
                } else {
                    setBackground(new Color(230, 230, 230));
                }
                setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                setForeground(Color.BLACK);
                return this;
            }
        };
        table.getColumnModel().getColumn(6).setCellRenderer(boxRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(boxRenderer);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row != -1 && col == 6) {
                    int id = (int) table.getValueAt(row, 0);
                    try {
                        Customer customer = fbs.getCustomerByID(id);
                        displayCustomerBookings(customer);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Error: " + ex.getMessage());
                    }
                } else if (row != -1 && col == 7) {
                    int id = (int) table.getValueAt(row, 0);
                    try {
                        Customer customer = fbs.getCustomerByID(id);
                        new EditCustomerWindow(MainWindow.this, customer);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        this.getContentPane().removeAll();
        this.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    /**
     * Displays the bookings for a specific customer.
     * 
     * @param customer the customer whose bookings are to be displayed
     */
    public void displayCustomerBookings(Customer customer) {
        List<bcu.cmp5332.bookingsystem.model.Booking> bookings = customer.getBookings();
        String[] columns = new String[] { "Flight No", "Origin", "Destination", "Date", "Booking Date", "Price", "Fee",
                "Status" };
        Object[][] data = new Object[bookings.size()][8];

        for (int i = 0; i < bookings.size(); i++) {
            bcu.cmp5332.bookingsystem.model.Booking b = bookings.get(i);
            data[i][0] = b.getFlight().getFlightNumber();
            data[i][1] = b.getFlight().getOrigin();
            data[i][2] = b.getFlight().getDestination();
            data[i][3] = b.getFlight().getDepartureDate();
            data[i][4] = b.getBookingDate();
            data[i][5] = String.format("Rs %.2f", b.getFinalPrice());
            data[i][6] = String.format("Rs %.2f", b.getCancellationFee());
            if (b.getBookingStatus() == bcu.cmp5332.bookingsystem.model.BookingStatus.CANCELLED) {
                data[i][7] = "Cancelled";
            } else if (b.getFlight().getDepartureDate().isBefore(fbs.getSystemDate())) {
                data[i][7] = "Completed";
            } else {
                data[i][7] = "Upcoming";
            }
        }

        JTable table = new JTable(data, columns);

        JPanel panel = new JPanel(new BorderLayout());
        JButton backBtn = new JButton("<- Back to Customers");
        backBtn.addActionListener(e -> displayCustomers());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.add(new JLabel("  Bookings for: " + customer.getName(), JLabel.CENTER), BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        this.getContentPane().removeAll();
        this.getContentPane().add(panel);
        this.revalidate();
        this.repaint();
    }

    /**
     * Displays the list of all active flights in the system.
     * Shows flight details, occupancy, and allows viewing passengers or editing.
     */
    public void displayFlights() {
        List<Flight> flightsList = fbs.getFlights();

        String[] columns = new String[] { "ID", "Flight No", "Origin", "Destination", "Date", "Cap", "Pass", "Avail",
                "Base Price", "View", "Edit" };

        Object[][] data = new Object[flightsList.size()][11];
        for (int i = 0; i < flightsList.size(); i++) {
            Flight flight = flightsList.get(i);
            int capacity = flight.getCapacity();
            int passengers = flight.getPassengers().size();
            int available = Math.max(0, capacity - passengers); // Ensure it's never negative

            data[i][0] = flight.getId();
            data[i][1] = flight.getFlightNumber();
            data[i][2] = flight.getOrigin();
            data[i][3] = flight.getDestination();
            data[i][4] = flight.getDepartureDate();
            data[i][5] = capacity;
            data[i][6] = passengers;
            data[i][7] = available;
            double dynamicPrice = fbs.calculateBookingPrice(flight);
            data[i][8] = String.format("Rs %.2f", dynamicPrice);
            data[i][9] = "Passengers";
            data[i][10] = "Edit";
        }

        JTable table = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer boxRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (isSelected) {
                    setBackground(new Color(200, 200, 200));
                } else {
                    setBackground(new Color(230, 230, 230));
                }
                setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                setForeground(Color.BLACK);
                return this;
            }
        };
        table.getColumnModel().getColumn(9).setCellRenderer(boxRenderer);
        table.getColumnModel().getColumn(10).setCellRenderer(boxRenderer);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row != -1 && col == 9) {
                    int id = (int) table.getValueAt(row, 0);
                    try {
                        Flight flight = fbs.getFlightByID(id);
                        displayFlightPassengers(flight);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Error: " + ex.getMessage());
                    }
                } else if (row != -1 && col == 10) {
                    int id = (int) table.getValueAt(row, 0);
                    try {
                        Flight flight = fbs.getFlightByID(id);
                        new EditFlightWindow(MainWindow.this, flight);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        this.getContentPane().removeAll();
        this.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    /**
     * Displays the admin dashboard with system statistics and flight utilization
     * overview.
     */
    public void displayDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        int totalFlights = fbs.getFlights().size();
        int totalCustomers = fbs.getCustomers().size();

        statsPanel.add(createStatCard("Total Flights", String.valueOf(totalFlights), new Color(100, 149, 237)));
        statsPanel.add(createStatCard("Total Customers", String.valueOf(totalCustomers), new Color(60, 179, 113)));

        String[] columns = { "Flight No", "Origin", "Destination", "Date", "Capacity", "Passengers", "Occupancy %" };
        List<Flight> flights = fbs.getFlights();
        Object[][] data = new Object[flights.size()][7];

        for (int i = 0; i < flights.size(); i++) {
            Flight f = flights.get(i);
            int passengers = f.getPassengers().size();
            int capacity = f.getCapacity();
            double pct = (double) passengers / capacity * 100;

            data[i][0] = f.getFlightNumber();
            data[i][1] = f.getOrigin();
            data[i][2] = f.getDestination();
            data[i][3] = f.getDepartureDate();
            data[i][4] = capacity;
            data[i][5] = passengers;
            data[i][6] = String.format("%.1f%%", pct);
        }

        JTable table = new JTable(data, columns);
        table.setRowHeight(25);
        table.setEnabled(false);

        JPanel tablePanel = new JPanel(new BorderLayout());
        JLabel tableLabel = new JLabel("Flight Utilization Overview", JLabel.LEFT);
        tableLabel.setFont(tableLabel.getFont().deriveFont(java.awt.Font.BOLD, 14f));
        tablePanel.add(tableLabel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        mainPanel.add(statsPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        this.getContentPane().removeAll();
        this.getContentPane().add(mainPanel);
        this.currentView = "dashboard"; // Track view
        this.revalidate();
        this.repaint();
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(color);
        card.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleLabel.getFont().deriveFont(java.awt.Font.BOLD, 12f));

        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(valueLabel.getFont().deriveFont(java.awt.Font.BOLD, 24f));

        card.add(titleLabel);
        card.add(valueLabel);
        return card;
    }

    /**
     * Displays the list of passengers for a specific flight.
     * 
     * @param flight the flight whose passengers are to be displayed
     */
    public void displayFlightPassengers(Flight flight) {
        List<Customer> passengers = flight.getPassengers();
        String[] columns = new String[] { "ID", "Name", "Phone", "Email" };
        Object[][] data = new Object[passengers.size()][4];

        for (int i = 0; i < passengers.size(); i++) {
            Customer c = passengers.get(i);
            data[i][0] = c.getId();
            data[i][1] = c.getName();
            data[i][2] = c.getPhone();
            data[i][3] = c.getEmail();
        }

        JTable table = new JTable(data, columns);

        JPanel panel = new JPanel(new BorderLayout());
        JButton backBtn = new JButton("<- Back to Flights");
        backBtn.addActionListener(e -> displayFlights());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.add(new JLabel("  Passengers for Flight: " + flight.getFlightNumber(), JLabel.CENTER),
                BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        this.getContentPane().removeAll();
        this.getContentPane().add(panel);
        this.revalidate();
        this.repaint();
    }

    /**
     * Displays all bookings in the system organized by status using a JTabbedPane.
     */
    public void displayAllBookings() {
        List<Booking> allBookings = new ArrayList<>();
        for (Customer customer : fbs.getAllCustomers()) {
            allBookings.addAll(customer.getBookings());
        }

        List<Booking> upcoming = new ArrayList<>();
        List<Booking> completed = new ArrayList<>();
        List<Booking> cancelled = new ArrayList<>();

        LocalDate systemDate = fbs.getSystemDate();
        for (Booking b : allBookings) {
            if (b.getBookingStatus() == bcu.cmp5332.bookingsystem.model.BookingStatus.CANCELLED) {
                cancelled.add(b);
            } else if (b.getFlight().getDepartureDate().isBefore(systemDate)) {
                completed.add(b);
            } else {
                upcoming.add(b);
            }
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Upcoming (" + upcoming.size() + ")", createBookingTable(upcoming));
        tabbedPane.addTab("Completed (" + completed.size() + ")", createBookingTable(completed));
        tabbedPane.addTab("Cancelled (" + cancelled.size() + ")", createCancelledBookingTable(cancelled));

        JPanel panel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("All Bookings Overview", JLabel.CENTER);
        header.setFont(header.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        header.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 10, 0));

        panel.add(header, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);

        this.getContentPane().removeAll();
        this.getContentPane().add(panel);
        this.revalidate();
        this.repaint();
    }

    private JScrollPane createCancelledBookingTable(List<Booking> bookings) {
        String[] columns = new String[] { "Cust ID", "Customer", "Flight ID", "Flight No", "Flight Date",
                "Initial price paid", "Cancellation Fee", "Net Refund Amount" };
        Object[][] data = new Object[bookings.size()][8];

        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            data[i][0] = b.getCustomer().getId();
            data[i][1] = b.getCustomer().getName();
            data[i][2] = b.getFlight().getId();
            data[i][3] = b.getFlight().getFlightNumber();
            data[i][4] = b.getFlight().getDepartureDate();
            data[i][5] = String.format("Rs %.2f", b.getFinalPrice());
            data[i][6] = String.format("Rs %.2f", b.getCancellationFee());
            data[i][7] = String.format("Rs %.2f", b.getFinalPrice() - b.getCancellationFee());
        }

        JTable table = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        return new JScrollPane(table);
    }

    private JScrollPane createBookingTable(List<Booking> bookings) {
        String[] columns = new String[] { "Cust ID", "Customer", "Flight ID", "Flight No", "Origin", "Destination",
                "Flight Date",
                "Price", "Booking Date" };
        Object[][] data = new Object[bookings.size()][9];

        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            data[i][0] = b.getCustomer().getId();
            data[i][1] = b.getCustomer().getName();
            data[i][2] = b.getFlight().getId();
            data[i][3] = b.getFlight().getFlightNumber();
            data[i][4] = b.getFlight().getOrigin();
            data[i][5] = b.getFlight().getDestination();
            data[i][6] = b.getFlight().getDepartureDate();
            data[i][7] = String.format("Rs %.2f", b.getFinalPrice());
            data[i][8] = b.getBookingDate();
        }

        JTable table = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        return new JScrollPane(table);
    }

    /**
     * Opens the UpdateBookingWindow popup.
     */
    public void displayUpdateBooking() {
        new UpdateBookingWindow(this);
    }

    /**
     * Helper method to execute a command and handle rollback if saving fails.
     */
    public void executeAndSave(bcu.cmp5332.bookingsystem.commands.Command command) {
        try {
            command.execute(fbs);
            try {
                FlightBookingSystemData.store(fbs);
            } catch (IOException e) {
                // Rollback: reload the system from files
                fbs = FlightBookingSystemData.load();
                throw new Exception(
                        "Critical Error: Failed to save to disk. Changes have been rolled back.\n" + e.getMessage());
            }

            // Auto-refresh dashboard if currently displayed
            if ("dashboard".equals(currentView)) {
                displayDashboard();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void displayAndSave(bcu.cmp5332.bookingsystem.commands.Command command) {
        executeAndSave(command);
    }

    public void displayWelcomeScreen() {
        // Create a custom panel to draw the background image
        JPanel welcomePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                try {
                    // Try to load the image from resources
                    java.net.URL imgUrl = getClass().getClassLoader().getResource("images/nepal_arlines.png");

                    // Fallback to file system if resource stream is null (e.g. IDE setup)
                    java.awt.image.BufferedImage image = null;
                    if (imgUrl != null) {
                        image = javax.imageio.ImageIO.read(imgUrl);
                    } else {
                        String filePath = "resources/images/nepal_arlines.png";
                        java.io.File imgFile = new java.io.File(filePath);
                        if (imgFile.exists()) {
                            image = javax.imageio.ImageIO.read(imgFile);
                        } else {
                            File rootFile = new File("resources/nepal_arlines.png");
                            if (rootFile.exists()) {
                                image = javax.imageio.ImageIO.read(rootFile);
                            }
                        }
                    }

                    if (image != null) {
                        // Draw image in the lower part to avoid text collision
                        // Offset y by 150 pixels
                        int yOffset = 180;
                        g.drawImage(image, 0, yOffset, getWidth(), getHeight() - yOffset, this);
                    } else {
                        g.setColor(new Color(240, 248, 255)); // AliceBlue
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // Header Panel for Text
        JPanel headerPanel = new JPanel(new java.awt.GridBagLayout());
        headerPanel.setOpaque(false); // Transparent

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new java.awt.Insets(40, 10, 10, 10); // Top padding

        JLabel titleLabel = new JLabel("FlyHigh Airlines");
        titleLabel.setFont(new java.awt.Font("Serif", java.awt.Font.BOLD, 48));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);
        JLabel subLabel = new JLabel("Flight Booking & Management System");
        subLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 18));
        subLabel.setForeground(new Color(60, 60, 60));
        headerPanel.add(subLabel, gbc);

        welcomePanel.add(headerPanel, BorderLayout.NORTH); // Put text at TOP

        // Footer
        JLabel footerLabel = new JLabel("Manage your flights and customers with ease.", JLabel.CENTER);
        footerLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 20, 0));
        footerLabel.setForeground(new Color(100, 100, 100));
        welcomePanel.add(footerLabel, BorderLayout.SOUTH);

        this.getContentPane().removeAll();
        this.getContentPane().add(welcomePanel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private JTable findSelectedTable() {
        if (this.getContentPane().getComponentCount() > 0) {
            Component centerComp = this.getContentPane().getComponent(0);
            if (centerComp instanceof JPanel) {
                JPanel p = (JPanel) centerComp;
                for (Component comp : p.getComponents()) {
                    if (comp instanceof JTabbedPane) {
                        JTabbedPane tabbed = (JTabbedPane) comp;
                        Component tab = tabbed.getSelectedComponent();
                        if (tab instanceof JScrollPane) {
                            Component view = ((JScrollPane) tab).getViewport().getView();
                            if (view instanceof JTable) {
                                return (JTable) view;
                            }
                        }
                    } else if (comp instanceof JScrollPane) {
                        Component view = ((JScrollPane) comp).getViewport().getView();
                        if (view instanceof JTable) {
                            return (JTable) view;
                        }
                    }
                }
            }
        }
        return null;
    }
}
