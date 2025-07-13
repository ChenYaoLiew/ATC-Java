import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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

    private final String currentUser;
    private final String currentStudentId;
    private final String displayName;
    private static final String DATA_DIR = "data";
    private static int nextRequestId = 1009; // Start after existing requests

    public student_dashboard(String userName) {
        this.currentUser = userName;
        this.currentStudentId = findStudentId(userName);
        this.displayName = findDisplayName(userName);
        
        initializeGUI();
        loadAllData();
    }

    private void initializeGUI() {
        setTitle("Student Dashboard - " + currentUser);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Main panel with modern background
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(0xF8F9FA));
        mainPanel.setOpaque(true);

        // Modern header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0x343A40));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        headerPanel.setOpaque(true);

        welcomeLabel = new JLabel("Welcome, " + displayName + " (Student)", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        logoutButton.setFocusPainted(false);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new main_page().setVisible(true));
        });

        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Modern tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(0xF8F9FA));
        tabbedPane.addTab("Schedule", createSchedulePanel());
        tabbedPane.addTab("Requests", createRequestPanel());
        tabbedPane.addTab("Payments", createPaymentPanel());
        tabbedPane.addTab("Profile", createProfilePanel());

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));
        panel.setOpaque(true);

        JLabel titleLabel = new JLabel("Weekly Class Schedule");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x343A40));

        scheduleTableModel = new DefaultTableModel(
            new String[]{"Day", "Time", "Subject", "Room", "Tutor"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scheduleTable = new JTable(scheduleTableModel);
        scheduleTable.setRowHeight(35);
        scheduleTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scheduleTable.getTableHeader().setBackground(new Color(0x007BFF));
        scheduleTable.getTableHeader().setForeground(Color.WHITE);
        scheduleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        scheduleTable.setShowGrid(true);
        scheduleTable.setGridColor(new Color(0xDEE2E6));
        scheduleTable.setSelectionBackground(new Color(0xE7F3FF));
        scheduleTable.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));
        panel.setOpaque(true);

        // Top section for new requests
        JPanel topPanel = new JPanel(new BorderLayout(15, 15));
        topPanel.setBackground(new Color(0xF8F9FA));
        topPanel.setOpaque(true);
        
        JLabel titleLabel = new JLabel("Submit New Subject Request");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x343A40));

        requestTextArea = new JTextArea(3, 0);
        requestTextArea.setLineWrap(true);
        requestTextArea.setWrapStyleWord(true);
        requestTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        requestTextArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDEE2E6)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(0xF8F9FA));
        buttonPanel.setOpaque(true);
        sendRequestButton = new JButton("Send Request");
        sendRequestButton.setBackground(new Color(40, 167, 69));
        sendRequestButton.setForeground(Color.WHITE);
        sendRequestButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendRequestButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        sendRequestButton.setFocusPainted(false);
        sendRequestButton.setOpaque(true);
        sendRequestButton.setBorderPainted(false);
        sendRequestButton.addActionListener(e -> sendRequest());

        deleteRequestButton = new JButton("Delete Selected");
        deleteRequestButton.setBackground(new Color(220, 53, 69));
        deleteRequestButton.setForeground(Color.WHITE);
        deleteRequestButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteRequestButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        deleteRequestButton.setFocusPainted(false);
        deleteRequestButton.setOpaque(true);
        deleteRequestButton.setBorderPainted(false);
        deleteRequestButton.addActionListener(e -> deleteRequest());

        buttonPanel.add(sendRequestButton);
        buttonPanel.add(deleteRequestButton);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(new JScrollPane(requestTextArea), BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Bottom section for request history
        JPanel bottomPanel = new JPanel(new BorderLayout(15, 15));
        bottomPanel.setBackground(new Color(0xF8F9FA));
        bottomPanel.setOpaque(true);
        
        JLabel historyLabel = new JLabel("Request History");
        historyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        historyLabel.setForeground(new Color(0x343A40));

        requestTableModel = new DefaultTableModel(
            new String[]{"Request ID", "Date", "Description", "Status", "Response"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        requestTable = new JTable(requestTableModel);
        requestTable.setRowHeight(35);
        requestTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        requestTable.getTableHeader().setBackground(new Color(0x007BFF));
        requestTable.getTableHeader().setForeground(Color.WHITE);
        requestTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        requestTable.setShowGrid(true);
        requestTable.setGridColor(new Color(0xDEE2E6));
        requestTable.setSelectionBackground(new Color(0xE7F3FF));
        requestTable.setBackground(Color.WHITE);

        JScrollPane requestScrollPane = new JScrollPane(requestTable);
        requestScrollPane.setPreferredSize(new Dimension(0, 200));
        requestScrollPane.setBorder(BorderFactory.createEmptyBorder());

        bottomPanel.add(historyLabel, BorderLayout.NORTH);
        bottomPanel.add(requestScrollPane, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));
        panel.setOpaque(true);

        JLabel titleLabel = new JLabel("Payment History & Balance");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x343A40));

        paymentStatusLabel = new JLabel("Loading payment information...");
        paymentStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        paymentStatusLabel.setForeground(new Color(0x28A745));

        paymentTableModel = new DefaultTableModel(
            new String[]{"Date", "Amount (RM)", "Method", "Status", "Receipt ID"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentTable = new JTable(paymentTableModel);
        paymentTable.setRowHeight(35);
        paymentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        paymentTable.getTableHeader().setBackground(new Color(0x007BFF));
        paymentTable.getTableHeader().setForeground(Color.WHITE);
        paymentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        paymentTable.setShowGrid(true);
        paymentTable.setGridColor(new Color(0xDEE2E6));
        paymentTable.setSelectionBackground(new Color(0xE7F3FF));
        paymentTable.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(paymentStatusLabel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));
        panel.setOpaque(true);

        JLabel titleLabel = new JLabel("Profile Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x343A40));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(0xF8F9FA));
        formPanel.setOpaque(true);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(new Color(0x343A40));
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        profileNameField = new JTextField(25);
        profileNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        profileNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDEE2E6)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formPanel.add(profileNameField, gbc);

        // Contact field
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        contactLabel.setForeground(new Color(0x343A40));
        formPanel.add(contactLabel, gbc);
        gbc.gridx = 1;
        profileContactField = new JTextField(25);
        profileContactField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        profileContactField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDEE2E6)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formPanel.add(profileContactField, gbc);

        // Update button
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.insets = new Insets(25, 15, 15, 15);
        updateProfileButton = new JButton("Update Profile");
        updateProfileButton.setBackground(new Color(0, 123, 255));
        updateProfileButton.setForeground(Color.WHITE);
        updateProfileButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateProfileButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        updateProfileButton.setFocusPainted(false);
        updateProfileButton.setOpaque(true);
        updateProfileButton.setBorderPainted(false);
        updateProfileButton.addActionListener(e -> updateProfile());
        formPanel.add(updateProfileButton, gbc);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadAllData() {
        if (currentStudentId == null) {
            JOptionPane.showMessageDialog(this, 
                "Student ID not found for user: " + currentUser, 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadScheduleData();
        loadRequestData();
        loadPaymentData();
        loadProfileData();
    }

    private String findStudentId(String username) {
        String filePath = DATA_DIR + "/users.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[1].trim().equals(username)) {
                    return parts[0].trim();
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
        }
        return null;
    }

    private String findDisplayName(String username) {
        String filePath = DATA_DIR + "/users.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[1].trim().equals(username)) {
                    return parts[4].trim(); // Return the display name (5th field)
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
        }
        return username; // Fallback to username if display name not found
    }

    private void loadScheduleData() {
        scheduleTableModel.setRowCount(0);
        
        try {
            // Load student's enrolled subjects
            List<String> enrolledSubjects = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "/student_subjects.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2 && parts[0].trim().equals(currentStudentId)) {
                        enrolledSubjects.add(parts[1].trim());
                    }
                }
            }

            // Load subject details
            Map<String, String[]> subjectMap = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "/subject.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        subjectMap.put(parts[0].trim(), parts);
                    }
                }
            }

            // Load class details
            Map<String, String[]> classMap = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "/classes.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        classMap.put(parts[0].trim(), parts);
                    }
                }
            }

            // Load schedule details
            Map<String, String[]> scheduleMap = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "/schedule.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        scheduleMap.put(parts[1].trim(), parts); // Key by class_id
                    }
                }
            }

            // Load tutor details
            Map<String, String> tutorMap = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "/users.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        tutorMap.put(parts[0].trim(), parts[4].trim());
                    }
                }
            }

            // Build schedule table
            for (String subjectId : enrolledSubjects) {
                String[] subjectData = subjectMap.get(subjectId);
                if (subjectData != null) {
                    String subjectName = subjectData[1].trim();
                    String level = subjectData[2].trim();
                    
                    // Find corresponding class
                    for (String[] classData : classMap.values()) {
                        if (classData[1].trim().equals(subjectId)) {
                            String classId = classData[0].trim();
                            String tutorId = classData[2].trim();
                            
                            // Find schedule for this class
                            String[] scheduleData = scheduleMap.get(classId);
                            if (scheduleData != null) {
                                String day = scheduleData[2].trim();
                                String startTime = scheduleData[3].trim();
                                String endTime = scheduleData[4].trim();
                                String room = scheduleData[5].trim();
                                
                                String tutorName = tutorMap.getOrDefault(tutorId, "Unknown");
                                String timeSlot = formatTime(startTime) + " - " + formatTime(endTime);
                                
                                scheduleTableModel.addRow(new Object[]{
                                    day, timeSlot, subjectName + " (" + level + ")", room, tutorName
                                });
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error loading schedule data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading schedule data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRequestData() {
        requestTableModel.setRowCount(0);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "/subject_requests.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9 && parts[1].trim().equals(currentStudentId)) {
                    String requestId = parts[0].trim();
                    String reason = parts[5].trim().replace("\"", "");
                    String date = parts[6].trim();
                    String status = parts[7].trim();
                    String response = parts.length > 9 ? parts[9].trim() : "Under review";
                    
                    requestTableModel.addRow(new Object[]{
                        requestId, date, reason, status, response
                    });
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading request data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading request data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPaymentData() {
        paymentTableModel.setRowCount(0);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "/payments.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9 && parts[1].trim().equals(currentStudentId)) {
                    String dateTime = parts[5].trim();
                    String date = dateTime.split(" ")[0];
                    String amount = parts[3].trim();
                    String method = parts[4].trim();
                    String status = parts[8].trim();
                    String receiptId = parts[7].trim();
                    
                    paymentTableModel.addRow(new Object[]{
                        date, amount, method, status, receiptId
                    });
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading payment data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading payment data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Update balance
        updateBalanceStatus();
    }

    private void loadProfileData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "/students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].trim().equals(currentStudentId)) {
                    profileNameField.setText(parts[1].trim());
                    profileContactField.setText(parts[4].trim());
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading profile data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading profile data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBalanceStatus() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "/student_balances.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].trim().equals(currentStudentId)) {
                    String outstanding = parts[3].trim();
                    paymentStatusLabel.setText("Outstanding Balance: RM " + outstanding);
                    return;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading balance data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading balance data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        paymentStatusLabel.setText("Balance information not available");
    }

    private void sendRequest() {
        String requestText = requestTextArea.getText().trim();
        if (requestText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your request.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String requestId = "REQ" + String.format("%03d", nextRequestId++);
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String status = "Pending";

            // Save to file
            try (FileWriter writer = new FileWriter(DATA_DIR + "/subject_requests.txt", true)) {
                writer.write(requestId + "," + currentStudentId + ",Add,,,\"" + requestText + "\"," + 
                           date + "," + status + ",,Under review\n");
            }

            // Add to table
            requestTableModel.addRow(new Object[]{
                requestId, date, requestText, status, "Under review"
            });

            requestTextArea.setText("");
            JOptionPane.showMessageDialog(this, "Request submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRequest() {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String status = (String) requestTableModel.getValueAt(selectedRow, 3);
        if (!"Pending".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only pending requests can be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        requestTableModel.removeRow(selectedRow);
        JOptionPane.showMessageDialog(this, "Request deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateProfile() {
        String name = profileNameField.getText().trim();
        String contact = profileContactField.getText().trim();
        
        if (name.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate contact format (basic validation)
        if (!contact.matches("^[0-9+\\-\\s()]+$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid contact number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Here you would typically update the students.txt file with the new information
            // For now, we'll just show a success message
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatTime(String time24) {
        try {
            String[] parts = time24.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            
            String ampm = hour >= 12 ? "PM" : "AM";
            if (hour > 12) hour -= 12;
            if (hour == 0) hour = 12;
            
            return String.format("%d:%02d %s", hour, minute, ampm);
        } catch (Exception e) {
            return time24;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new student_dashboard("student").setVisible(true);
        });
    }
}
