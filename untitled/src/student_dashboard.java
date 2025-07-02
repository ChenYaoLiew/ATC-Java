import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Paths;

public class student_dashboard extends JFrame {
    private JPanel mainPanel;
    private JLabel welcomeLabel;
    private JButton logoutButton;
    private JTabbedPane tabbedPane;

    // Schedule Tab
    private JTable scheduleTable;
    private DefaultTableModel scheduleTableModel;

    // Subject Request Tab
    private JTextArea requestTextArea;
    private JButton sendRequestButton;
    private JTable requestTable;
    private JButton deleteRequestButton;
    private DefaultTableModel requestTableModel;

    // Payment Tab
    private JLabel paymentStatusLabel;
    private JTable paymentTable;
    private DefaultTableModel paymentTableModel;

    // Profile Tab
    private JTextField profileNameField;
    private JTextField profileContactField;
    private JButton updateProfileButton;

    private String currentUser;
    private static final String DATA_DIR = "data";

    public student_dashboard(String userName) {
        this.currentUser = userName;
        setContentPane(mainPanel);
        setTitle("Student Dashboard - " + userName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        welcomeLabel.setText("Welcome, " + userName + " (Student)");

        initializeComponents();
        setupEventListeners();
        loadSchedule();
        loadRequests();
        loadPayments();
        loadProfile();
    }

    private void initializeComponents() {
        // Initialize tables and models for schedule, requests, and payments
        scheduleTableModel = new DefaultTableModel(new String[]{"Day", "Time", "Subject", "Room"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        scheduleTable.setModel(scheduleTableModel);

        requestTableModel = new DefaultTableModel(new String[]{"Request ID", "Subject(s)", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        requestTable.setModel(requestTableModel);

        paymentTableModel = new DefaultTableModel(new String[]{"Date", "Amount", "Method", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        paymentTable.setModel(paymentTableModel);
    }

    private void setupEventListeners() {
        logoutButton.addActionListener(e -> {
            dispose();
            new main_page().setVisible(true);
        });
        sendRequestButton.addActionListener(e -> sendSubjectRequest());
        deleteRequestButton.addActionListener(e -> deletePendingRequest());
        updateProfileButton.addActionListener(e -> updateProfile());
    }

    // 1. View class schedule
    private void loadSchedule() {
        // TODO: Load schedule from file or database for currentUser
    }

    // 2. Send request to change subject enrollment
    private void sendSubjectRequest() {
        // TODO: Implement sending subject change request
    }

    // 3. Delete pending request
    private void deletePendingRequest() {
        // TODO: Implement deleting a pending request
    }

    // 4. View payment status and balance
    private void loadPayments() {
        // TODO: Load payment status and history for currentUser
    }

    // 5. Update own profile
    private void updateProfile() {
        // TODO: Implement profile update logic
    }

    private void loadRequests() {
        // TODO: Load subject change requests for currentUser
    }

    private void loadProfile() {
        // TODO: Load profile info for currentUser
    }
}
