import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private static int nextRequestId = 1001;

    public student_dashboard(String userName) {
        this.currentUser = userName;
        
        // Create menu bar
        createMenuBar();
        
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

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Dashboard Menu
        JMenu dashboardMenu = new JMenu("Dashboard");
        JMenuItem homeItem = new JMenuItem("Home");
        JMenuItem refreshItem = new JMenuItem("Refresh All Data");
        
        homeItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        refreshItem.addActionListener(e -> refreshAllData());
        
        dashboardMenu.add(homeItem);
        dashboardMenu.addSeparator();
        dashboardMenu.add(refreshItem);

        // Schedule Menu
        JMenu scheduleMenu = new JMenu("Schedule");
        JMenuItem viewScheduleItem = new JMenuItem("View My Schedule");
        JMenuItem printScheduleItem = new JMenuItem("Print Schedule");
        
        viewScheduleItem.addActionListener(e -> {
            tabbedPane.setSelectedIndex(0);
            loadSchedule();
        });
        printScheduleItem.addActionListener(e -> printSchedule());
        
        scheduleMenu.add(viewScheduleItem);
        scheduleMenu.add(printScheduleItem);

        // Subject Request Menu
        JMenu subjectMenu = new JMenu("Subject Requests");
        JMenuItem newRequestItem = new JMenuItem("New Subject Request");
        JMenuItem viewRequestsItem = new JMenuItem("View My Requests");
        JMenuItem deleteRequestItem = new JMenuItem("Delete Pending Request");
        
        newRequestItem.addActionListener(e -> {
            tabbedPane.setSelectedIndex(1);
            requestTextArea.requestFocus();
        });
        viewRequestsItem.addActionListener(e -> {
            tabbedPane.setSelectedIndex(1);
            loadRequests();
        });
        deleteRequestItem.addActionListener(e -> deletePendingRequest());
        
        subjectMenu.add(newRequestItem);
        subjectMenu.add(viewRequestsItem);
        subjectMenu.addSeparator();
        subjectMenu.add(deleteRequestItem);

        // Payment Menu
        JMenu paymentMenu = new JMenu("Payments");
        JMenuItem viewPaymentsItem = new JMenuItem("View Payment History");
        JMenuItem checkBalanceItem = new JMenuItem("Check Balance");
        
        viewPaymentsItem.addActionListener(e -> {
            tabbedPane.setSelectedIndex(2);
            loadPayments();
        });
        checkBalanceItem.addActionListener(e -> showBalanceDialog());
        
        paymentMenu.add(viewPaymentsItem);
        paymentMenu.add(checkBalanceItem);

        // Profile Menu
        JMenu profileMenu = new JMenu("Profile");
        JMenuItem viewProfileItem = new JMenuItem("View Profile");
        JMenuItem editProfileItem = new JMenuItem("Edit Profile");
        
        viewProfileItem.addActionListener(e -> {
            tabbedPane.setSelectedIndex(3);
            loadProfile();
        });
        editProfileItem.addActionListener(e -> {
            tabbedPane.setSelectedIndex(3);
            profileNameField.requestFocus();
        });
        
        profileMenu.add(viewProfileItem);
        profileMenu.add(editProfileItem);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        JMenuItem contactItem = new JMenuItem("Contact Support");
        
        aboutItem.addActionListener(e -> showAboutDialog());
        contactItem.addActionListener(e -> showContactDialog());
        
        helpMenu.add(aboutItem);
        helpMenu.add(contactItem);

        // Add menus to menu bar
        menuBar.add(dashboardMenu);
        menuBar.add(scheduleMenu);
        menuBar.add(subjectMenu);
        menuBar.add(paymentMenu);
        menuBar.add(profileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void initializeComponents() {
        // Initialize tables and models for schedule, requests, and payments
        scheduleTableModel = new DefaultTableModel(new String[]{"Day", "Time", "Subject", "Room", "Tutor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        scheduleTable.setModel(scheduleTableModel);

        requestTableModel = new DefaultTableModel(new String[]{"Request ID", "Date", "Subject(s)", "Status", "Response"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        requestTable.setModel(requestTableModel);

        paymentTableModel = new DefaultTableModel(new String[]{"Date", "Amount (RM)", "Method", "Status", "Receipt ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        paymentTable.setModel(paymentTableModel);
    }

    private void setupEventListeners() {
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new main_page().setVisible(true);
            }
        });
        
        sendRequestButton.addActionListener(e -> sendSubjectRequest());
        deleteRequestButton.addActionListener(e -> deletePendingRequest());
        updateProfileButton.addActionListener(e -> updateProfile());
    }

    // Menu Action Methods
    private void refreshAllData() {
        loadSchedule();
        loadRequests();
        loadPayments();
        loadProfile();
        JOptionPane.showMessageDialog(this, "All data refreshed successfully!", "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void printSchedule() {
        // Simple schedule print functionality
        StringBuilder schedule = new StringBuilder();
        schedule.append("=== CLASS SCHEDULE FOR ").append(currentUser.toUpperCase()).append(" ===\n\n");
        
        for (int i = 0; i < scheduleTableModel.getRowCount(); i++) {
            schedule.append(scheduleTableModel.getValueAt(i, 0)).append(" - ")
                   .append(scheduleTableModel.getValueAt(i, 1)).append(" - ")
                   .append(scheduleTableModel.getValueAt(i, 2)).append(" - ")
                   .append(scheduleTableModel.getValueAt(i, 3)).append(" - ")
                   .append(scheduleTableModel.getValueAt(i, 4)).append("\n");
        }
        
        JTextArea textArea = new JTextArea(schedule.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Schedule Preview", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showBalanceDialog() {
        double totalBalance = calculateTotalBalance();
        String message = String.format("Your current balance is: RM %.2f", totalBalance);
        if (totalBalance > 0) {
            message += "\n\nPlease make payment to settle outstanding balance.";
        } else {
            message += "\n\nYour account is up to date!";
        }
        
        JOptionPane.showMessageDialog(this, message, "Balance Information", 
            totalBalance > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        String aboutText = "Student Dashboard v1.0\n\n" +
                          "This dashboard allows students to:\n" +
                          "• View class schedules\n" +
                          "• Send subject change requests\n" +
                          "• Check payment status\n" +
                          "• Update profile information\n\n" +
                          "Developed for Tuition Center Management System";
        
        JOptionPane.showMessageDialog(this, aboutText, "About Student Dashboard", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showContactDialog() {
        String contactText = "For technical support or assistance:\n\n" +
                           "Email: support@tuitioncenter.com\n" +
                           "Phone: +60 3-1234 5678\n" +
                           "Office Hours: Mon-Fri 9AM-5PM\n\n" +
                           "For urgent matters, please contact the receptionist directly.";
        
        JOptionPane.showMessageDialog(this, contactText, "Contact Support", JOptionPane.INFORMATION_MESSAGE);
    }

    // Core Functionality Implementation
    private void loadSchedule() {
        scheduleTableModel.setRowCount(0);
        
        // Add sample schedule data - in production, this would load from a file or database
        scheduleTableModel.addRow(new Object[]{"Monday", "10:00 AM - 12:00 PM", "Mathematics", "Room A1", "Mr. Lim"});
        scheduleTableModel.addRow(new Object[]{"Tuesday", "2:00 PM - 4:00 PM", "English", "Room B2", "Ms. Wong"});
        scheduleTableModel.addRow(new Object[]{"Wednesday", "10:00 AM - 12:00 PM", "Science", "Room C3", "Dr. Kumar"});
        scheduleTableModel.addRow(new Object[]{"Thursday", "3:00 PM - 5:00 PM", "Mathematics", "Room A1", "Mr. Lim"});
        scheduleTableModel.addRow(new Object[]{"Friday", "1:00 PM - 3:00 PM", "English", "Room B2", "Ms. Wong"});
        
        // Update payment status label
        paymentStatusLabel.setText("Schedule loaded for " + currentUser + " - " + scheduleTableModel.getRowCount() + " classes");
    }

    private void sendSubjectRequest() {
        String requestText = requestTextArea.getText().trim();
        
        if (requestText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your subject change request.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create new request
        String requestId = String.valueOf(nextRequestId++);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        String status = "Pending";
        
        // Add to table
        requestTableModel.addRow(new Object[]{requestId, date, requestText, status, "Awaiting review"});
        
        // Save to file (simplified)
        saveRequestToFile(requestId, date, requestText, status);
        
        JOptionPane.showMessageDialog(this, 
            "Request submitted successfully!\nRequest ID: " + requestId + "\nStatus: " + status,
            "Request Sent", JOptionPane.INFORMATION_MESSAGE);
        
        // Clear text area
        requestTextArea.setText("");
    }

    private void deletePendingRequest() {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String status = (String) requestTableModel.getValueAt(selectedRow, 3);
        String requestId = (String) requestTableModel.getValueAt(selectedRow, 0);
        
        if (!"Pending".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only pending requests can be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete request ID: " + requestId + "?",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            requestTableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Request deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadPayments() {
        paymentTableModel.setRowCount(0);
        
        // Add sample payment data
        paymentTableModel.addRow(new Object[]{"2024-01-15", "150.00", "Credit Card", "Paid", "RC001"});
        paymentTableModel.addRow(new Object[]{"2024-02-15", "150.00", "Bank Transfer", "Paid", "RC002"});
        paymentTableModel.addRow(new Object[]{"2024-03-15", "150.00", "Cash", "Pending", "RC003"});
        
        // Update status label with balance
        double balance = calculateTotalBalance();
        paymentStatusLabel.setText(String.format("Payment Status - Outstanding Balance: RM %.2f", balance));
    }

    private double calculateTotalBalance() {
        double balance = 0.0;
        for (int i = 0; i < paymentTableModel.getRowCount(); i++) {
            String status = (String) paymentTableModel.getValueAt(i, 3);
            if ("Pending".equals(status)) {
                String amountStr = (String) paymentTableModel.getValueAt(i, 1);
                balance += Double.parseDouble(amountStr);
            }
        }
        return balance;
    }

    private void updateProfile() {
        String newName = profileNameField.getText().trim();
        String newContact = profileContactField.getText().trim();
        
        if (newName.isEmpty() || newContact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all profile fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // In production, this would update the actual user file
        JOptionPane.showMessageDialog(this, 
            "Profile updated successfully!\nName: " + newName + "\nContact: " + newContact,
            "Profile Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadRequests() {
        requestTableModel.setRowCount(0);
        
        // Add sample request data
        requestTableModel.addRow(new Object[]{"1001", "2024-01-10 14:30", "Change from Physics to Chemistry", "Approved", "Request approved by receptionist"});
        requestTableModel.addRow(new Object[]{"1002", "2024-01-15 10:15", "Add Biology class", "Pending", "Under review"});
    }

    private void loadProfile() {
        // Load current user profile data
        profileNameField.setText(currentUser);
        profileContactField.setText("012-345-6789"); // Sample contact
    }

    private void saveRequestToFile(String requestId, String date, String requestText, String status) {
        try {
            String currentDir = System.getProperty("user.dir");
            File file = new File(Paths.get(currentDir, DATA_DIR, "student_requests.txt").toString());
            
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write(requestId + "," + currentUser + "," + date + "," + requestText + "," + status + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error saving request: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new student_dashboard("TestStudent").setVisible(true);
        });
    }
}
