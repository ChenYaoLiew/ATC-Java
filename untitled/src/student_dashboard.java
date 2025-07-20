import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private JComboBox<String> fromSubjectComboBox;
    private JComboBox<String> toSubjectComboBox;
    private JComboBox<String> requestTypeComboBox;
    private JButton sendRequestButton;
    private JTable requestTable;
    private JButton deleteRequestButton;
    private DefaultTableModel requestTableModel;

    // Payment Tab
    private JLabel paymentStatusLabel;
    private JTable paymentTable;
    private DefaultTableModel paymentTableModel;

    // Attendance Tab
    private JTable studentAttendanceTable;
    private DefaultTableModel studentAttendanceTableModel;
    private JLabel presentCountLabel;
    private JLabel absentCountLabel;
    private JLabel lateCountLabel;
    private JLabel totalCountLabel;

    // Messaging Tab
    private JTable studentMessagesTable;
    private DefaultTableModel studentMessagesTableModel;

    // Profile Tab
    private JTextField profileNameField;
    private JTextField profileEmailField;
    private JTextField profilePhoneField;
    private JTextField profileAddressField;
    private JTextField profileContactField; // For compatibility
    private JTextField profileUsernameField; // Read-only username display
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton updateProfileButton;
    private JButton changePasswordButton;
    private JButton refreshDataButton;

    // Student Data
    private final String currentUser;
    private String currentStudentId;
    private String displayName;
    private Map<String, String> studentProfile;
    private List<String> enrolledSubjects;
    private double totalFees;
    private double paidAmount;
    private double balanceAmount;

    public student_dashboard(String userName) {
        this.currentUser = userName;
        
        // Initialize student data using function.java with comprehensive error handling
        if (!initializeStudentData()) {
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize student data. Please contact administrator.", 
                "Initialization Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Initialize GUI
        initializeGUI();
        
        // Show the window
        setVisible(true);
        
        // Load all data after GUI is visible with error handling
        SwingUtilities.invokeLater(() -> {
            try {
                loadAllData();
                refreshAllTables();
                System.out.println("Student dashboard initialized successfully for: " + displayName);
            } catch (Exception e) {
                System.err.println("Error during data loading: " + e.getMessage());
                showErrorDialog("Data Loading Error", "Some data may not be displayed correctly.");
            }
        });
    }

    /**
     * Initialize student data using function.java methods with comprehensive validation
     */
    private boolean initializeStudentData() {
        try {
            // Ensure all required files exist using function.java utilities
            if (!validateRequiredFiles()) {
                return false;
            }

            // Find student ID from users.txt using function.java
            currentStudentId = findStudentId(currentUser);
            if (currentStudentId == null) {
                showErrorDialog("Student Not Found", 
                    "Student not found for username: " + currentUser);
                return false;
            }

            // Load student data components
            if (!loadStudentProfile() || !loadEnrolledSubjects() || !loadFinancialData()) {
                return false;
            }
            
            // Set display name with fallback
            displayName = studentProfile.getOrDefault("name", currentUser);
            System.out.println("DEBUG: Student data initialized - ID: " + currentStudentId + ", Name: " + displayName);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error initializing student data: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validate that all required files exist using function.java utilities
     */
    private boolean validateRequiredFiles() {
        String[] requiredFiles = {
            "users.txt", "students.txt", "subject.txt", "classes.txt", 
            "schedule.txt", "student_subjects.txt", "student_balances.txt", 
            "subject_requests.txt", "payments.txt"
        };
        
        boolean allFilesValid = true;
        for (String fileName : requiredFiles) {
            if (!function.fileExists(fileName)) {
                System.out.println("Creating missing file: " + fileName);
                if (!function.createFileIfNotExists(fileName)) {
                    System.err.println("Failed to create required file: " + fileName);
                    allFilesValid = false;
                } else {
                    System.out.println("Successfully created file: " + fileName);
                }
            }
        }
        
        return allFilesValid;
    }

    /**
     * Find student ID using function.java with enhanced validation
     */
    private String findStudentId(String username) {
        try {
            List<String> users = function.readUsers();
            if (users.isEmpty()) {
                System.err.println("No users found in users.txt");
                return null;
            }
            
            for (String line : users) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String userId = parts[0].trim();
                    String userName = parts[1].trim();
                    String userRole = parts[3].trim();
                    
                    if (userName.equals(username) && userRole.equals("student")) {
                        System.out.println("Found student ID: " + userId + " for username: " + username);
                        return userId;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding student ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Load student profile using function.java with enhanced error handling
     */
    private boolean loadStudentProfile() {
        try {
            studentProfile = new HashMap<>();
            List<String> students = function.readStudents();
            
            if (students.isEmpty()) {
                System.err.println("No students found in students.txt");
                return false;
            }
            
            System.out.println("DEBUG: Loading profile for student ID: " + currentStudentId);
            
            for (String line : students) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 9 && parts[0].trim().equals(currentStudentId)) {
                    studentProfile.put("id", parts[0].trim());
                    studentProfile.put("name", parts[1].trim());
                    studentProfile.put("ic", parts[2].trim());
                    studentProfile.put("email", parts[3].trim());
                    studentProfile.put("phone", parts[4].trim());
                    studentProfile.put("address", parts[5].trim());
                    studentProfile.put("regDate", parts[6].trim());
                    studentProfile.put("level", parts[7].trim());
                    studentProfile.put("status", parts[8].trim());
                    
                    System.out.println("DEBUG: Student profile loaded successfully");
                    return true;
                }
            }
            
            System.err.println("Student profile not found for ID: " + currentStudentId);
            return false;
            
        } catch (Exception e) {
            System.err.println("Error loading student profile: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load enrolled subjects using function.java with validation
     */
    private boolean loadEnrolledSubjects() {
        try {
            enrolledSubjects = new ArrayList<>();
            List<String> studentSubjects = function.readStudentSubjects();
            
            System.out.println("DEBUG: Loading enrolled subjects for student ID: " + currentStudentId);
            
            for (String line : studentSubjects) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].trim().equals(currentStudentId)) {
                    String subjectId = parts[1].trim();
                    if (!subjectId.isEmpty()) {
                        enrolledSubjects.add(subjectId);
                        System.out.println("DEBUG: Added subject: " + subjectId);
                    }
                }
            }
            
            System.out.println("DEBUG: Total enrolled subjects: " + enrolledSubjects.size());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error loading enrolled subjects: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load financial data using function.java with enhanced error handling
     */
    private boolean loadFinancialData() {
        try {
            totalFees = 0.0;
            paidAmount = 0.0;
            balanceAmount = 0.0;
            
            List<String> balances = function.readStudentBalances();
            
            for (String line : balances) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].trim().equals(currentStudentId)) {
                    try {
                        totalFees = Double.parseDouble(parts[1].trim());
                        paidAmount = Double.parseDouble(parts[2].trim());
                        balanceAmount = Double.parseDouble(parts[3].trim());
                        System.out.println("DEBUG: Financial data loaded - Total: " + totalFees + 
                                         ", Paid: " + paidAmount + ", Balance: " + balanceAmount);
                        return true;
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing financial data: " + e.getMessage());
                        return false;
                    }
                }
            }
            
            // If no balance record exists, create one using function.java
            String balanceData = currentStudentId + ",0.0,0.0,0.0";
            if (function.addStudentBalance(balanceData)) {
                System.out.println("Created new balance record for student: " + currentStudentId);
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error loading financial data: " + e.getMessage());
            return false;
        }
    }

    /**
     * Initialize the GUI components
     */
    private void initializeGUI() {
        setTitle("Student Dashboard - " + currentUser);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Main panel with modern background
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(0xF8F9FA));

        // Create header
        createHeader();
        
        // Create tabbed pane
        createTabbedPane();

        // Add components to main panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    /**
     * Create the header panel with refresh functionality
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0x343A40));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        welcomeLabel = new JLabel("Welcome, " + displayName + " (Student)", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        // Button panel for logout and refresh
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(0x343A40));
        
        refreshDataButton = new JButton("Refresh Data");
        styleButton(refreshDataButton, new Color(23, 162, 184));
        refreshDataButton.addActionListener(e -> refreshAllData());
        
        logoutButton = new JButton("Logout");
        styleButton(logoutButton, new Color(220, 53, 69));
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(refreshDataButton);
        buttonPanel.add(logoutButton);

        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Create header components
     */
    private void createHeader() {
        // Header components are created in createHeaderPanel()
    }

    /**
     * Create the tabbed pane with all tabs
     */
    private void createTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(0xF8F9FA));
        
        // Add all tabs
        tabbedPane.addTab("📅 Schedule", createSchedulePanel());
        tabbedPane.addTab("📝 Requests", createRequestPanel());
        tabbedPane.addTab("💰 Payments", createPaymentPanel());
        tabbedPane.addTab("✅ Attendance", createStudentAttendancePanel());
        tabbedPane.addTab("💬 Messages", createStudentMessagingPanel());
        tabbedPane.addTab("👤 Profile", createProfilePanel());
    }

    /**
     * Create the schedule panel
     */
    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));

        JLabel titleLabel = new JLabel("Weekly Class Schedule");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x343A40));

        // Create schedule table
        scheduleTableModel = new DefaultTableModel(
            new String[]{"Day", "Time", "Subject", "Room", "Tutor"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        scheduleTable = new JTable(scheduleTableModel);
        setupTable(scheduleTable);

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create the request panel
     */
    private JPanel createRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));

        // Top section for new requests
        JPanel topPanel = createNewRequestPanel();
        
        // Bottom section for request history
        JPanel bottomPanel = createRequestHistoryPanel();

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create new request panel
     */
    private JPanel createNewRequestPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(15, 15));
        topPanel.setBackground(new Color(0xF8F9FA));
        
        JLabel titleLabel = new JLabel("Submit New Subject Request");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x343A40));

        // Create form panel for dropdowns and text area
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(0xF8F9FA));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Request Type dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel requestTypeLabel = new JLabel("Request Type:");
        requestTypeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(requestTypeLabel, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        requestTypeComboBox = new JComboBox<>(new String[]{"Add", "Drop", "Change"});
        requestTypeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        requestTypeComboBox.addActionListener(e -> updateSubjectDropdowns());
        formPanel.add(requestTypeComboBox, gbc);

        // From Subject dropdown (for Drop and Change requests)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel fromSubjectLabel = new JLabel("From Subject:");
        fromSubjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(fromSubjectLabel, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        fromSubjectComboBox = new JComboBox<>();
        fromSubjectComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(fromSubjectComboBox, gbc);

        // To Subject dropdown (for Add and Change requests)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel toSubjectLabel = new JLabel("To Subject:");
        toSubjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(toSubjectLabel, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        toSubjectComboBox = new JComboBox<>();
        toSubjectComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(toSubjectComboBox, gbc);

        // Reason text area
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel reasonLabel = new JLabel("Reason:");
        reasonLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(reasonLabel, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        requestTextArea = new JTextArea(3, 30);
        requestTextArea.setLineWrap(true);
        requestTextArea.setWrapStyleWord(true);
        requestTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        requestTextArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDEE2E6)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane textScrollPane = new JScrollPane(requestTextArea);
        textScrollPane.setPreferredSize(new Dimension(400, 80));
        formPanel.add(textScrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(0xF8F9FA));
        
        sendRequestButton = new JButton("Send Request");
        styleButton(sendRequestButton, new Color(40, 167, 69));
        sendRequestButton.addActionListener(e -> sendRequest());

        deleteRequestButton = new JButton("Delete Selected");
        styleButton(deleteRequestButton, new Color(220, 53, 69));
        deleteRequestButton.addActionListener(e -> deleteRequest());

        buttonPanel.add(sendRequestButton);
        buttonPanel.add(deleteRequestButton);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        return topPanel;
    }

    /**
     * Create request history panel
     */
    private JPanel createRequestHistoryPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(15, 15));
        bottomPanel.setBackground(new Color(0xF8F9FA));
        
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
        setupTable(requestTable);

        JScrollPane requestScrollPane = new JScrollPane(requestTable);
        requestScrollPane.setPreferredSize(new Dimension(0, 200));
        requestScrollPane.setBorder(BorderFactory.createEmptyBorder());

        bottomPanel.add(historyLabel, BorderLayout.NORTH);
        bottomPanel.add(requestScrollPane, BorderLayout.CENTER);

        return bottomPanel;
    }

    /**
     * Create the payment panel
     */
    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));

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
        setupTable(paymentTable);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(paymentStatusLabel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create the profile panel
     */
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));

        JLabel titleLabel = new JLabel("Profile Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x343A40));

        JPanel formPanel = createProfileForm();

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create profile form
     */
    private JPanel createProfileForm() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(0xF8F9FA));

        // Create profile information panel
        JPanel profilePanel = createBasicProfilePanel();
        
        // Create password change panel
        JPanel passwordPanel = createPasswordChangePanel();

        mainPanel.add(profilePanel, BorderLayout.WEST);
        mainPanel.add(passwordPanel, BorderLayout.EAST);

        return mainPanel;
    }

    /**
     * Create basic profile information panel
     */
    private JPanel createBasicProfilePanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(0xF8F9FA));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0xDEE2E6), 2),
            "Profile Information",
            0, 0, new Font("Segoe UI", Font.BOLD, 16),
            new Color(0x343A40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Username field (read-only)
        addFormField(formPanel, gbc, "Username:", profileUsernameField = new JTextField(25), 0);
        profileUsernameField.setEditable(false);
        profileUsernameField.setBackground(new Color(0xE9ECEF));
        profileUsernameField.setForeground(new Color(0x6C757D));

        // Name field
        addFormField(formPanel, gbc, "Name:", profileNameField = new JTextField(25), 1);
        
        // Email field
        addFormField(formPanel, gbc, "Email:", profileEmailField = new JTextField(25), 2);
        
        // Phone field
        addFormField(formPanel, gbc, "Phone:", profilePhoneField = new JTextField(25), 3);
        
        // Address field
        addFormField(formPanel, gbc, "Address:", profileAddressField = new JTextField(25), 4);

        // Initialize compatibility field (not displayed)
        profileContactField = new JTextField(25);

        // Update profile button
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.insets = new Insets(25, 15, 15, 15);
        updateProfileButton = new JButton("Update Profile");
        styleButton(updateProfileButton, new Color(0, 123, 255));
        updateProfileButton.addActionListener(e -> updateProfile());
        formPanel.add(updateProfileButton, gbc);

        return formPanel;
    }

    /**
     * Create password change panel
     */
    private JPanel createPasswordChangePanel() {
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.setBackground(new Color(0xF8F9FA));
        passwordPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0xDEE2E6), 2),
            "Change Password",
            0, 0, new Font("Segoe UI", Font.BOLD, 16),
            new Color(0x343A40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Current password field
        addPasswordField(passwordPanel, gbc, "Current Password:", currentPasswordField = new JPasswordField(25), 0);
        
        // New password field
        addPasswordField(passwordPanel, gbc, "New Password:", newPasswordField = new JPasswordField(25), 1);
        
        // Confirm password field
        addPasswordField(passwordPanel, gbc, "Confirm Password:", confirmPasswordField = new JPasswordField(25), 2);

        // Password requirements label
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 15, 15, 15);
        JLabel requirementsLabel = new JLabel("<html><small>Password must be at least 6 characters long</small></html>");
        requirementsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        requirementsLabel.setForeground(new Color(0x6C757D));
        passwordPanel.add(requirementsLabel, gbc);

        // Change password button
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(25, 15, 15, 15);
        changePasswordButton = new JButton("Change Password");
        styleButton(changePasswordButton, new Color(220, 53, 69));
        changePasswordButton.addActionListener(e -> changePassword());
        passwordPanel.add(changePasswordButton, gbc);

        return passwordPanel;
    }

    /**
     * Add form field helper method
     */
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int row) {
        // Label
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(0x343A40));
        panel.add(label, gbc);
        
        // Text Field
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Allow field to expand horizontally
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill available horizontal space
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDEE2E6)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.add(field, gbc);
    }

    /**
     * Add password field helper method
     */
    private void addPasswordField(JPanel panel, GridBagConstraints gbc, String labelText, JPasswordField field, int row) {
        // Label
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(0x343A40));
        panel.add(label, gbc);
        
        // Password Field
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Allow field to expand horizontally
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill available horizontal space
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDEE2E6)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.add(field, gbc);
    }

    /**
     * Setup table appearance
     */
    private void setupTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Enhanced header styling
        table.getTableHeader().setBackground(new Color(0x007BFF));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x0056B3), 2),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        // Enhanced grid and borders
        table.setShowGrid(true);
        table.setGridColor(new Color(0x6C757D)); // Darker grid lines for better visibility
        table.setIntercellSpacing(new Dimension(1, 1)); // More prominent cell spacing
        
        // Table border
        table.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x495057), 2), // Dark outer border
            BorderFactory.createLineBorder(new Color(0xADB5BD), 1)  // Light inner border
        ));
        
        // Row styling
        table.setSelectionBackground(new Color(0xE7F3FF));
        table.setSelectionForeground(new Color(0x212529));
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(0x212529));
        
        // Row striping for better readability
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(0xF8F9FA)); // Light gray for alternate rows
                    }
                    c.setForeground(new Color(0x212529));
                } else {
                    c.setBackground(new Color(0xE7F3FF));
                    c.setForeground(new Color(0x212529));
                }
                
                // Add padding to cells
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                return c;
            }
        });
    }

    /**
     * Style button helper method
     */
    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    /**
     * Load all data for all tabs using function.java with enhanced error handling
     */
    private void loadAllData() {
        try {
            // Validate files before loading
            if (!validateRequiredFiles()) {
                showErrorDialog("File Validation Error", "Some required files are missing or corrupted.");
                return;
            }
            
            loadScheduleData();
            loadRequestData();
            loadPaymentData();
            loadProfileData();
            if (studentAttendanceTableModel != null) loadStudentAttendanceData();
            if (studentMessagesTableModel != null) loadStudentMessagesData();
            
            // Initialize subject dropdowns
            initializeSubjectDropdowns();
            
            System.out.println("All data loaded successfully for student: " + currentStudentId);
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            showErrorDialog("Data Loading Error", 
                "Error loading some data: " + e.getMessage() + "\nPlease try refreshing or contact administrator.");
        }
    }

    /**
     * Initialize subject dropdowns on first load
     */
    private void initializeSubjectDropdowns() {
        try {
            // Set default request type to Add
            requestTypeComboBox.setSelectedItem("Add");
            updateSubjectDropdowns();
        } catch (Exception e) {
            System.err.println("Error initializing subject dropdowns: " + e.getMessage());
        }
    }

    /**
     * Enhanced schedule data loading using function.java with better error handling
     */
    private void loadScheduleData() {
        scheduleTableModel.setRowCount(0);
        
        try {
            if (enrolledSubjects.isEmpty()) {
                System.out.println("No enrolled subjects found for student: " + currentStudentId);
                return;
            }
            
            // Get all required data using function.java
            Map<String, String[]> subjectMap = getSubjectMapFromFunction();
            Map<String, String[]> classMap = getClassMapFromFunction();
            Map<String, String[]> scheduleMap = getScheduleMapFromFunction();
            Map<String, String> tutorMap = getTutorMapFromFunction();

            // Build schedule table with enhanced validation
            for (String subjectId : enrolledSubjects) {
                String[] subjectData = subjectMap.get(subjectId);
                if (subjectData != null && subjectData.length >= 3) {
                    String subjectName = subjectData[1].trim();
                    String level = subjectData[2].trim();
                    
                    // Find corresponding class
                    for (String[] classData : classMap.values()) {
                        if (classData.length >= 3 && classData[1].trim().equals(subjectId)) {
                            String classId = classData[0].trim();
                            String tutorId = classData[2].trim();
                            
                            // Find schedule for this class
                            String[] scheduleData = scheduleMap.get(classId);
                            if (scheduleData != null && scheduleData.length >= 6) {
                                String day = scheduleData[2].trim();
                                String startTime = scheduleData[3].trim();
                                String endTime = scheduleData[4].trim();
                                String room = scheduleData[5].trim();
                                
                                String tutorName = tutorMap.getOrDefault(tutorId, "Unknown");
                                String timeSlot = formatTimeWithValidation(startTime) + " - " + formatTimeWithValidation(endTime);
                                
                                scheduleTableModel.addRow(new Object[]{
                                    day, timeSlot, subjectName + " (" + level + ")", room, tutorName
                                });
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading schedule data: " + e.getMessage());
            showErrorDialog("Schedule Error", "Error loading schedule data: " + e.getMessage());
        }
    }

    /**
     * Enhanced subject mapping using function.java
     */
    private Map<String, String[]> getSubjectMapFromFunction() {
        Map<String, String[]> subjectMap = new HashMap<>();
        try {
            List<String> subjects = function.readSubjects();
            
            for (String line : subjects) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    subjectMap.put(parts[0].trim(), parts);
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating subject map: " + e.getMessage());
        }
        return subjectMap;
    }

    /**
     * Enhanced class mapping using function.java
     */
    private Map<String, String[]> getClassMapFromFunction() {
        Map<String, String[]> classMap = new HashMap<>();
        try {
            List<String> classes = function.readClasses();
            
            for (String line : classes) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    classMap.put(parts[0].trim(), parts);
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating class map: " + e.getMessage());
        }
        return classMap;
    }

    /**
     * Enhanced schedule mapping using function.java
     */
    private Map<String, String[]> getScheduleMapFromFunction() {
        Map<String, String[]> scheduleMap = new HashMap<>();
        try {
            List<String> schedules = function.readSchedules();
            
            for (String line : schedules) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    scheduleMap.put(parts[1].trim(), parts); // Key by class_id
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating schedule map: " + e.getMessage());
        }
        return scheduleMap;
    }

    /**
     * Enhanced tutor mapping using function.java
     */
    private Map<String, String> getTutorMapFromFunction() {
        Map<String, String> tutorMap = new HashMap<>();
        try {
            List<String> users = function.readUsers();
            
            for (String line : users) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    tutorMap.put(parts[0].trim(), parts[4].trim());
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating tutor map: " + e.getMessage());
        }
        return tutorMap;
    }

    /**
     * Update subject dropdowns based on request type selection
     */
    private void updateSubjectDropdowns() {
        String requestType = (String) requestTypeComboBox.getSelectedItem();
        
        // Clear existing items
        fromSubjectComboBox.removeAllItems();
        toSubjectComboBox.removeAllItems();
        
        if ("Add".equals(requestType)) {
            // For Add requests, show available subjects not enrolled
            fromSubjectComboBox.setEnabled(false);
            toSubjectComboBox.setEnabled(true);
            populateAvailableSubjects();
        } else if ("Drop".equals(requestType)) {
            // For Drop requests, show enrolled subjects
            fromSubjectComboBox.setEnabled(true);
            toSubjectComboBox.setEnabled(false);
            populateEnrolledSubjects();
        } else if ("Change".equals(requestType)) {
            // For Change requests, show both dropdowns
            fromSubjectComboBox.setEnabled(true);
            toSubjectComboBox.setEnabled(true);
            populateEnrolledSubjects();
            populateAvailableSubjects();
        }
    }

    /**
     * Populate dropdown with student's enrolled subjects
     */
    private void populateEnrolledSubjects() {
        try {
            fromSubjectComboBox.removeAllItems();
            fromSubjectComboBox.addItem("-- Select Subject --");
            
            Map<String, String[]> subjectMap = getSubjectMapFromFunction();
            
            for (String subjectId : enrolledSubjects) {
                String[] subjectData = subjectMap.get(subjectId);
                if (subjectData != null && subjectData.length >= 3) {
                    String displayText = subjectData[1] + " (" + subjectData[2] + ")";
                    fromSubjectComboBox.addItem(subjectId + "|" + displayText);
                }
            }
        } catch (Exception e) {
            System.err.println("Error populating enrolled subjects: " + e.getMessage());
        }
    }

    /**
     * Populate dropdown with available subjects (not enrolled)
     */
    private void populateAvailableSubjects() {
        try {
            toSubjectComboBox.removeAllItems();
            toSubjectComboBox.addItem("-- Select Subject --");
            
            List<String> subjects = function.readSubjects();
            
            for (String line : subjects) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String subjectId = parts[0].trim();
                    String subjectName = parts[1].trim();
                    String level = parts[2].trim();
                    
                    // Only add subjects not currently enrolled
                    if (!enrolledSubjects.contains(subjectId)) {
                        String displayText = subjectName + " (" + level + ")";
                        toSubjectComboBox.addItem(subjectId + "|" + displayText);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error populating available subjects: " + e.getMessage());
        }
    }

    /**
     * Enhanced request data loading using function.java
     */
    private void loadRequestData() {
        requestTableModel.setRowCount(0);
        
        try {
            List<String> requests = function.readSubjectRequests();
            
            for (String line : requests) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 8 && parts[1].trim().equals(currentStudentId)) {
                    String requestId = parts[0].trim();
                    String reason = parts.length > 5 ? parts[5].trim().replace("\"", "") : "No description";
                    String date = parts.length > 6 ? parts[6].trim() : "Unknown";
                    String status = parts.length > 7 ? parts[7].trim() : "Pending";
                    String response = parts.length > 8 ? parts[8].trim() : "Under review";
                    
                    requestTableModel.addRow(new Object[]{
                        requestId, date, reason, status, response
                    });
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading request data: " + e.getMessage());
            showErrorDialog("Request Error", "Error loading request data: " + e.getMessage());
        }
    }

    /**
     * Enhanced payment data loading using function.java
     */
    private void loadPaymentData() {
        paymentTableModel.setRowCount(0);
        
        try {
            List<String> payments = function.readPayments();
            
            for (String line : payments) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 9 && parts[1].trim().equals(currentStudentId)) {
                    String dateTime = parts.length > 5 ? parts[5].trim() : "Unknown";
                    String date = dateTime.contains(" ") ? dateTime.split(" ")[0] : dateTime;
                    String amount = parts.length > 3 ? parts[3].trim() : "0.00";
                    String method = parts.length > 4 ? parts[4].trim() : "Unknown";
                    String status = parts.length > 8 ? parts[8].trim() : "Unknown";
                    String receiptId = parts.length > 7 ? parts[7].trim() : "N/A";
                    
                    paymentTableModel.addRow(new Object[]{
                        date, amount, method, status, receiptId
                    });
                }
            }
            
            updatePaymentStatus();
            
        } catch (Exception e) {
            System.err.println("Error loading payment data: " + e.getMessage());
            showErrorDialog("Payment Error", "Error loading payment data: " + e.getMessage());
        }
    }

    /**
     * Load profile data using function.java
     */
    private void loadProfileData() {
        try {
            profileUsernameField.setText(currentUser); // Set username as read-only
            profileNameField.setText(studentProfile.getOrDefault("name", ""));
            profileEmailField.setText(studentProfile.getOrDefault("email", ""));
            profilePhoneField.setText(studentProfile.getOrDefault("phone", ""));
            profileAddressField.setText(studentProfile.getOrDefault("address", ""));
            profileContactField.setText(studentProfile.getOrDefault("phone", "")); // For form compatibility
        } catch (Exception e) {
            System.err.println("Error loading profile data: " + e.getMessage());
        }
    }

    /**
     * Update payment status display
     */
    private void updatePaymentStatus() {
        try {
            if (balanceAmount > 0) {
                paymentStatusLabel.setText(String.format(
                    "Total Fees: RM%.2f | Paid: RM%.2f | Outstanding: RM%.2f", 
                    totalFees, paidAmount, balanceAmount));
                paymentStatusLabel.setForeground(new Color(0xDC3545)); // Red for outstanding
            } else {
                paymentStatusLabel.setText(String.format(
                    "Total Fees: RM%.2f | Paid: RM%.2f | Status: PAID", 
                    totalFees, paidAmount));
                paymentStatusLabel.setForeground(new Color(0x28A745)); // Green for paid
            }
        } catch (Exception e) {
            paymentStatusLabel.setText("Balance information not available");
            paymentStatusLabel.setForeground(new Color(0x6C757D)); // Gray for unknown
        }
    }

    /**
     * Refresh table display
     */
    private void refreshTable(JTable table, DefaultTableModel model) {
        SwingUtilities.invokeLater(() -> {
            model.fireTableDataChanged();
            table.repaint();
            table.revalidate();
        });
    }

    /**
     * Refresh all tables
     */
    private void refreshAllTables() {
        refreshTable(scheduleTable, scheduleTableModel);
        refreshTable(requestTable, requestTableModel);
        refreshTable(paymentTable, paymentTableModel);
    }

    /**
     * Enhanced time formatting with validation
     */
    private String formatTimeWithValidation(String time24) {
        try {
            if (time24 == null || time24.trim().isEmpty()) {
                return "N/A";
            }
            
            String[] parts = time24.split(":");
            if (parts.length < 2) {
                return time24; // Return as-is if format is unexpected
            }
            
            int hour = Integer.parseInt(parts[0].trim());
            int minute = Integer.parseInt(parts[1].trim());
            
            // Validate time ranges
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                return time24; // Return as-is if invalid
            }
            
            String ampm = hour >= 12 ? "PM" : "AM";
            if (hour > 12) hour -= 12;
            if (hour == 0) hour = 12;
            
            return String.format("%d:%02d %s", hour, minute, ampm);
        } catch (Exception e) {
            System.err.println("Error formatting time '" + time24 + "': " + e.getMessage());
            return time24; // Return original if parsing fails
        }
    }

    /**
     * Enhanced request sending using function.java with comprehensive validation
     */
    private void sendRequest() {
        try {
            String requestType = (String) requestTypeComboBox.getSelectedItem();
            String requestText = requestTextArea.getText().trim();
            
            if (requestText.isEmpty()) {
                showErrorDialog("Input Error", "Please enter a reason for your request.");
                return;
            }

            if (requestText.length() > 500) {
                showErrorDialog("Input Error", "Reason text is too long. Please limit to 500 characters.");
                return;
            }

            String fromSubjectId = "";
            String toSubjectId = "";
            String classId = "";
            String alternativeClassId = "";

            // Validate selections based on request type
            if ("Add".equals(requestType)) {
                String toSelection = (String) toSubjectComboBox.getSelectedItem();
                if (toSelection == null || toSelection.startsWith("--")) {
                    showErrorDialog("Selection Error", "Please select a subject to add.");
                    return;
                }
                toSubjectId = toSelection.split("\\|")[0];
                classId = findClassIdForSubject(toSubjectId);
                
            } else if ("Drop".equals(requestType)) {
                String fromSelection = (String) fromSubjectComboBox.getSelectedItem();
                if (fromSelection == null || fromSelection.startsWith("--")) {
                    showErrorDialog("Selection Error", "Please select a subject to drop.");
                    return;
                }
                fromSubjectId = fromSelection.split("\\|")[0];
                classId = findClassIdForSubject(fromSubjectId);
                
            } else if ("Change".equals(requestType)) {
                String fromSelection = (String) fromSubjectComboBox.getSelectedItem();
                String toSelection = (String) toSubjectComboBox.getSelectedItem();
                
                if (fromSelection == null || fromSelection.startsWith("--")) {
                    showErrorDialog("Selection Error", "Please select a subject to change from.");
                    return;
                }
                if (toSelection == null || toSelection.startsWith("--")) {
                    showErrorDialog("Selection Error", "Please select a subject to change to.");
                    return;
                }
                
                fromSubjectId = fromSelection.split("\\|")[0];
                toSubjectId = toSelection.split("\\|")[0];
                
                if (fromSubjectId.equals(toSubjectId)) {
                    showErrorDialog("Selection Error", "Cannot change from and to the same subject.");
                    return;
                }
                
                classId = findClassIdForSubject(fromSubjectId);
                alternativeClassId = findClassIdForSubject(toSubjectId);
            }

            // Generate unique request ID using function.java
            String requestId = function.generateRequestId();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String status = "Pending";

            // Create request data based on the format in subject_requests.txt
            // Format: RequestID,StudentID,RequestType,ClassID,AlternativeClassID,Reason,RequestDate,Status,ProcessedDate,Comments
            String requestData = String.format("%s,%s,%s,%s,%s,\"%s\",%s,%s,,Under review",
                                             requestId, currentStudentId, requestType, 
                                             classId, alternativeClassId, requestText, date, status);

            boolean success = function.addSubjectRequest(requestData);
            
            if (success) {
                // Add to table
                String subjectDescription = createSubjectDescription(requestType, fromSubjectId, toSubjectId);
                requestTableModel.addRow(new Object[]{
                    requestId, date, subjectDescription + " - " + requestText, status, "Under review"
                });
                requestTableModel.fireTableDataChanged();

                // Clear form
                requestTextArea.setText("");
                requestTypeComboBox.setSelectedIndex(0);
                updateSubjectDropdowns();
                
                showSuccessDialog("Request Submitted", "Request submitted successfully with ID: " + requestId);
                
                System.out.println("Request submitted successfully: " + requestId);
            } else {
                showErrorDialog("Submission Error", "Error saving request. Please try again.");
            }
        } catch (Exception e) {
            System.err.println("Error sending request: " + e.getMessage());
            showErrorDialog("Request Error", "Error processing request: " + e.getMessage());
        }
    }

    /**
     * Create subject description for display in table
     */
    private String createSubjectDescription(String requestType, String fromSubjectId, String toSubjectId) {
        try {
            Map<String, String[]> subjectMap = getSubjectMapFromFunction();
            
            if ("Add".equals(requestType)) {
                String[] subjectData = subjectMap.get(toSubjectId);
                if (subjectData != null && subjectData.length >= 3) {
                    return "Add " + subjectData[1] + " (" + subjectData[2] + ")";
                }
            } else if ("Drop".equals(requestType)) {
                String[] subjectData = subjectMap.get(fromSubjectId);
                if (subjectData != null && subjectData.length >= 3) {
                    return "Drop " + subjectData[1] + " (" + subjectData[2] + ")";
                }
            } else if ("Change".equals(requestType)) {
                String[] fromSubjectData = subjectMap.get(fromSubjectId);
                String[] toSubjectData = subjectMap.get(toSubjectId);
                if (fromSubjectData != null && toSubjectData != null && 
                    fromSubjectData.length >= 3 && toSubjectData.length >= 3) {
                    return "Change from " + fromSubjectData[1] + " (" + fromSubjectData[2] + ")" +
                           " to " + toSubjectData[1] + " (" + toSubjectData[2] + ")";
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating subject description: " + e.getMessage());
        }
        return requestType + " request";
    }

    /**
     * Find class ID for a given subject ID
     */
    private String findClassIdForSubject(String subjectId) {
        try {
            List<String> classes = function.readClasses();
            for (String line : classes) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].trim().equals(subjectId)) {
                    return parts[0].trim();
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding class ID for subject " + subjectId + ": " + e.getMessage());
        }
        return "";
    }

    /**
     * Enhanced request deletion using function.java with validation
     */
    private void deleteRequest() {
        try {
            int selectedRow = requestTable.getSelectedRow();
            if (selectedRow == -1) {
                showErrorDialog("Selection Error", "Please select a request to delete.");
                return;
            }

            String status = (String) requestTableModel.getValueAt(selectedRow, 3);
            if (!"Pending".equals(status)) {
                showErrorDialog("Action Not Allowed", "Only pending requests can be deleted.");
                return;
            }

            String requestId = (String) requestTableModel.getValueAt(selectedRow, 0);
            
            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete request " + requestId + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Find and delete the request from file using function.java
            List<String> requests = function.readSubjectRequests();
            
            for (String line : requests) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].trim().equals(requestId)) {
                    boolean success = function.deleteSubjectRequest(line);
                    if (success) {
                        requestTableModel.removeRow(selectedRow);
                        requestTableModel.fireTableDataChanged();
                        showSuccessDialog("Request Deleted", "Request deleted successfully!");
                        System.out.println("Request deleted successfully: " + requestId);
                    } else {
                        showErrorDialog("Deletion Error", "Error deleting request. Please try again.");
                    }
                    return;
                }
            }
            
            showErrorDialog("Request Not Found", "Request not found in file.");
                
        } catch (Exception e) {
            System.err.println("Error deleting request: " + e.getMessage());
            showErrorDialog("Deletion Error", "Error deleting request: " + e.getMessage());
        }
    }

    /**
     * Enhanced profile update using function.java with comprehensive validation
     */
    private void updateProfile() {
        try {
            String name = profileNameField.getText().trim();
            String email = profileEmailField.getText().trim();
            String phone = profilePhoneField.getText().trim();
            String address = profileAddressField.getText().trim();
            
            // Comprehensive validation
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                showErrorDialog("Validation Error", "Please fill in all fields.");
                return;
            }

            // Validate name (only letters and spaces, reasonable length)
            if (!name.matches("^[a-zA-Z\\s]{2,50}$")) {
                showErrorDialog("Validation Error", "Name should contain only letters and spaces (2-50 characters).");
                return;
            }

            // Validate email format
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                showErrorDialog("Validation Error", "Please enter a valid email address.");
                return;
            }

            // Validate phone format (allow various international formats)
            if (!phone.matches("^[0-9+\\-\\s()]{8,20}$")) {
                showErrorDialog("Validation Error", "Please enter a valid phone number (8-20 characters).");
                return;
            }

            // Validate address length
            if (address.length() < 10 || address.length() > 200) {
                showErrorDialog("Validation Error", "Address should be between 10-200 characters.");
                return;
            }

            // Update student profile using function.java
            List<String> students = function.readStudents();
            
            for (String line : students) {
                String[] parts = line.split(",");
                if (parts.length >= 9 && parts[0].trim().equals(currentStudentId)) {
                    // Create updated line: StudentID,Name,IC,Email,Phone,Address,RegDate,Level,Status
                    String updatedLine = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                                                     parts[0], name, parts[2], email, phone, 
                                                     address, parts[6], parts[7], parts[8]);
                    
                    boolean success = function.updateStudent(line, updatedLine);
                    if (success) {
                        // Update local data
                        studentProfile.put("name", name);
                        studentProfile.put("email", email);
                        studentProfile.put("phone", phone);
                        studentProfile.put("address", address);
                        
                        // Update display name
                        displayName = name;
                        welcomeLabel.setText("Welcome, " + displayName + " (Student)");
                        
                        showSuccessDialog("Profile Updated", "Profile updated successfully!");
                        System.out.println("Profile updated successfully for student: " + currentStudentId);
                    } else {
                        showErrorDialog("Update Error", "Error updating profile. Please try again.");
                    }
                    return;
                }
            }
            
            showErrorDialog("Student Not Found", "Student record not found.");
                
        } catch (Exception e) {
            System.err.println("Error updating profile: " + e.getMessage());
            showErrorDialog("Update Error", "Error updating profile: " + e.getMessage());
        }
    }

    /**
     * Enhanced password change using function.java with comprehensive validation
     */
    private void changePassword() {
        try {
            String currentPassword = new String(currentPasswordField.getPassword()).trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
            
            // Validate input fields
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showErrorDialog("Validation Error", "Please fill in all password fields.");
                return;
            }

            // Validate new password matches confirmation
            if (!newPassword.equals(confirmPassword)) {
                showErrorDialog("Validation Error", "New password and confirmation do not match.");
                clearPasswordFields();
                return;
            }

            // Validate password length and complexity
            if (newPassword.length() < 6) {
                showErrorDialog("Validation Error", "New password must be at least 6 characters long.");
                clearPasswordFields();
                return;
            }

            // Verify current password by checking users.txt
            List<String> users = function.readUsers();
            String currentUserData = null;
            boolean currentPasswordValid = false;
            
            for (String line : users) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].trim().equals(currentStudentId)) {
                    currentUserData = line;
                    String storedPassword = parts[2].trim();
                    if (storedPassword.equals(currentPassword)) {
                        currentPasswordValid = true;
                    }
                    break;
                }
            }

            if (!currentPasswordValid) {
                showErrorDialog("Authentication Error", "Current password is incorrect.");
                clearPasswordFields();
                return;
            }

            if (currentUserData == null) {
                showErrorDialog("User Error", "User account not found.");
                clearPasswordFields();
                return;
            }

            // Update password in users.txt using function.java
            String[] parts = currentUserData.split(",");
            String updatedUserData = String.format("%s,%s,%s,%s,%s",
                                                 parts[0], parts[1], newPassword, parts[3], parts[4]);

            boolean success = function.updateUser(currentUserData, updatedUserData);
            
            if (success) {
                clearPasswordFields();
                showSuccessDialog("Password Changed", "Password changed successfully!");
                System.out.println("Password updated successfully for student: " + currentStudentId);
            } else {
                showErrorDialog("Update Error", "Error updating password. Please try again.");
                clearPasswordFields();
            }
                
        } catch (Exception e) {
            System.err.println("Error changing password: " + e.getMessage());
            showErrorDialog("Password Change Error", "Error changing password: " + e.getMessage());
            clearPasswordFields();
        }
    }

    /**
     * Clear all password fields for security
     */
    private void clearPasswordFields() {
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    /**
     * Refresh all data using function.java
     */
    private void refreshAllData() {
        try {
            // Show loading indicator
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            // Re-initialize and reload all data
            if (initializeStudentData()) {
                loadAllData();
                refreshAllTables();
                
                // Refresh subject dropdowns with updated data
                updateSubjectDropdowns();
                
                // Update welcome label
                welcomeLabel.setText("Welcome, " + displayName + " (Student)");
                
                showSuccessDialog("Data Refreshed", "All data has been refreshed successfully!");
                System.out.println("Data refreshed successfully for student: " + currentStudentId);
            } else {
                showErrorDialog("Refresh Error", "Failed to refresh data. Please try again.");
            }
            
        } catch (Exception e) {
            System.err.println("Error refreshing data: " + e.getMessage());
            showErrorDialog("Refresh Error", "Error refreshing data: " + e.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Enhanced error dialog
     */
    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Enhanced success dialog
     */
    private void showSuccessDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Create the student attendance panel
     */
    private JPanel createStudentAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));

        JLabel titleLabel = new JLabel("📊 My Attendance Records");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x343A40));

        // Create attendance table
        studentAttendanceTableModel = new DefaultTableModel(
                new String[]{"Date", "Subject", "Class", "Status", "Time Marked"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentAttendanceTable = new JTable(studentAttendanceTableModel);
        setupTable(studentAttendanceTable);

        // Create summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Attendance Summary"));
        summaryPanel.setBackground(new Color(0xF8F9FA));

        // Create cards and store label references
        JPanel presentCard = createAttendanceStatCard("Present", "0", new Color(40, 167, 69));
        presentCountLabel = getValueLabelFromCard(presentCard);
        
        JPanel absentCard = createAttendanceStatCard("Absent", "0", new Color(220, 53, 69));
        absentCountLabel = getValueLabelFromCard(absentCard);
        
        JPanel lateCard = createAttendanceStatCard("Late", "0", new Color(255, 193, 7));
        lateCountLabel = getValueLabelFromCard(lateCard);
        
        JPanel totalCard = createAttendanceStatCard("Total", "0", new Color(0, 123, 255));
        totalCountLabel = getValueLabelFromCard(totalCard);

        summaryPanel.add(presentCard);
        summaryPanel.add(absentCard);
        summaryPanel.add(lateCard);
        summaryPanel.add(totalCard);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(new Color(0xF8F9FA));

        JButton viewDetailsButton = new JButton("📋 View Details");
        styleButton(viewDetailsButton, new Color(0, 123, 255));
        viewDetailsButton.addActionListener(e -> showAttendanceDetails());

        controlPanel.add(viewDetailsButton);

        JScrollPane scrollPane = new JScrollPane(studentAttendanceTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(0xF8F9FA));
        centerPanel.add(summaryPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create the student messaging panel
     */
    private JPanel createStudentMessagingPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));

        JLabel titleLabel = new JLabel("💬 Messages & Communication");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x343A40));

        // Create tabbed pane for Inbox and Compose
        JTabbedPane messagesTabbedPane = new JTabbedPane();
        messagesTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Inbox Tab
        JPanel inboxPanel = new JPanel(new BorderLayout(10, 10));
        inboxPanel.setBackground(new Color(0xF8F9FA));

        studentMessagesTableModel = new DefaultTableModel(
                new String[]{"From", "Date", "Time", "Priority", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentMessagesTable = new JTable(studentMessagesTableModel);
        setupTable(studentMessagesTable);
        studentMessagesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = studentMessagesTable.getSelectedRow();
                    if (row >= 0) {
                        showStudentMessageDialog(row);
                    }
                }
            }
        });

        JScrollPane messagesScrollPane = new JScrollPane(studentMessagesTable);
        inboxPanel.add(messagesScrollPane, BorderLayout.CENTER);

        // Buttons for inbox
        JPanel inboxButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inboxButtonPanel.setBackground(new Color(0xF8F9FA));

        inboxPanel.add(inboxButtonPanel, BorderLayout.SOUTH);

        // Compose Tab
        JPanel composePanel = createStudentComposeMessagePanel();

        messagesTabbedPane.addTab("📥 Inbox", inboxPanel);
        messagesTabbedPane.addTab("✉️ Compose", composePanel);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(messagesTabbedPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create compose message panel for students
     */
    private JPanel createStudentComposeMessagePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0xF8F9FA));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // To field (tutors, admin, receptionist)
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("To:"), gbc);
        JComboBox<String> toComboBox = new JComboBox<>();
        loadRecipientsForStudent(toComboBox);
        toComboBox.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        panel.add(toComboBox, gbc);

        // Priority field
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Priority:"), gbc);
        JComboBox<String> priorityComboBox = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        priorityComboBox.setPreferredSize(new Dimension(300, 35));
        priorityComboBox.setSelectedItem("Medium");
        gbc.gridx = 1;
        panel.add(priorityComboBox, gbc);

        // Message content area
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Message:"), gbc);
        JTextArea messageArea = new JTextArea(10, 25);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setPreferredSize(new Dimension(300, 200));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(messageScrollPane, gbc);

        // Send button
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        JButton sendButton = new JButton("📤 Send Message");
        styleButton(sendButton, new Color(40, 167, 69));
        sendButton.addActionListener(e -> {
            String to = (String) toComboBox.getSelectedItem();
            String priority = (String) priorityComboBox.getSelectedItem();
            String content = messageArea.getText().trim();

            if (to == null || content.isEmpty()) {
                showErrorDialog("Error", "Please fill in all required fields");
                return;
            }

            sendStudentMessage(to, "Message", content, priority);
            messageArea.setText("");
            toComboBox.setSelectedIndex(0);
            priorityComboBox.setSelectedItem("Medium");
        });
        panel.add(sendButton, gbc);

        return panel;
    }

    /**
     * Create attendance stat card
     */
    private JPanel createAttendanceStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setName("valueLabel"); // Add name to identify this label

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Get the value label from an attendance stat card
     */
    private JLabel getValueLabelFromCard(JPanel card) {
        Component[] components = card.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel && "valueLabel".equals(comp.getName())) {
                return (JLabel) comp;
            }
        }
        return null;
    }

    /**
     * Load student attendance data
     */
    private void loadStudentAttendanceData() {
        studentAttendanceTableModel.setRowCount(0);
        try {
            List<Attendance> attendanceList = function.getAttendanceForStudent(currentStudentId);
            for (Attendance att : attendanceList) {
                String subjectName = getSubjectNameFromClass(att.getClassId());
                studentAttendanceTableModel.addRow(new Object[]{
                    att.getDate().toString(),
                    subjectName,
                    att.getClassId(),
                    att.getStatus(),
                    att.getTimeMarked().toString()
                });
            }
            updateAttendanceSummary(attendanceList);
        } catch (Exception e) {
            showErrorDialog("Error", "Failed to load attendance data: " + e.getMessage());
        }
    }

    /**
     * Load student messages data
     */
    private void loadStudentMessagesData() {
        studentMessagesTableModel.setRowCount(0);
        try {
            List<Message> messages = function.getMessagesForUser(currentStudentId);
            for (Message msg : messages) {
                String senderName = getUserNameFromId(msg.getSenderId());
                String fullDateTime = msg.getDateTime().toString();
                String date = fullDateTime.contains("T") ? fullDateTime.split("T")[0] : fullDateTime.split(" ")[0];
                String time = fullDateTime.contains("T") ? fullDateTime.split("T")[1] : (fullDateTime.split(" ").length > 1 ? fullDateTime.split(" ")[1] : "");
                studentMessagesTableModel.addRow(new Object[]{
                    senderName,
                    date,
                    time,
                    msg.getPriority(),
                    msg.getStatus()
                });
            }
        } catch (Exception e) {
            showErrorDialog("Error", "Failed to load messages: " + e.getMessage());
        }
    }

    /**
     * Helper methods for the new functionality
     */
    private String getSubjectNameFromClass(String classId) {
        try {
            List<String> classLines = function.readClasses();
            for (String line : classLines) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].trim().equals(classId)) {
                    String subjectId = parts[1].trim();
                    List<String> subjectLines = function.readSubjects();
                    for (String subjectLine : subjectLines) {
                        String[] subjectParts = subjectLine.split(",");
                        if (subjectParts.length >= 3 && subjectParts[0].trim().equals(subjectId)) {
                            return subjectParts[1].trim() + " " + subjectParts[2].trim();
                        }
                    }
                    return subjectId;
                }
            }
        } catch (Exception e) {
            // Return class ID if can't find subject name
        }
        return classId;
    }

    private String getUserNameFromId(String userId) {
        try {
            List<String> userLines = function.readUsers();
            for (String line : userLines) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].trim().equals(userId)) {
                    return parts[4].trim();
                }
            }
        } catch (Exception e) {
            // Return user ID if can't find name
        }
        return userId;
    }

    private void updateAttendanceSummary(List<Attendance> attendanceList) {
        int totalRecords = attendanceList.size();
        int presentCount = 0;
        int absentCount = 0;
        int lateCount = 0;

        for (Attendance att : attendanceList) {
            switch (att.getStatus()) {
                case "Present": presentCount++; break;
                case "Absent": absentCount++; break;
                case "Late": lateCount++; break;
            }
        }

        // Update summary cards
        if (presentCountLabel != null) presentCountLabel.setText(String.valueOf(presentCount));
        if (absentCountLabel != null) absentCountLabel.setText(String.valueOf(absentCount));
        if (lateCountLabel != null) lateCountLabel.setText(String.valueOf(lateCount));
        if (totalCountLabel != null) totalCountLabel.setText(String.valueOf(totalRecords));

        System.out.println("Attendance Summary - Total: " + totalRecords + 
                          ", Present: " + presentCount + 
                          ", Absent: " + absentCount + 
                          ", Late: " + lateCount);
    }

    private void showAttendanceDetails() {
        // Create a detailed attendance report dialog
        JDialog dialog = new JDialog(this, "Detailed Attendance Report", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        try {
            // Get attendance data
            List<Attendance> attendanceList = function.getAttendanceForStudent(currentStudentId);
            
            // Create main panel
            JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            mainPanel.setBackground(new Color(0xF8F9FA));
            
            // Title
            JLabel titleLabel = new JLabel("📊 Detailed Attendance Report", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(new Color(0x343A40));
            
            // Create tabbed pane for different views
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            // Summary tab
            tabbedPane.addTab("📈 Summary", createAttendanceSummaryPanel(attendanceList));
            
            // By Subject tab
            tabbedPane.addTab("📚 By Subject", createAttendanceBySubjectPanel(attendanceList));
            
            // Timeline tab
            tabbedPane.addTab("📅 Timeline", createAttendanceTimelinePanel(attendanceList));
            
            // Close button
            JButton closeButton = new JButton("Close");
            styleButton(closeButton, new Color(108, 117, 125));
            closeButton.addActionListener(e -> dialog.dispose());
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.setBackground(new Color(0xF8F9FA));
            buttonPanel.add(closeButton);
            
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(tabbedPane, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.add(mainPanel);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            showErrorDialog("Error", "Failed to generate attendance report: " + e.getMessage());
        }
    }
    
    private JPanel createAttendanceSummaryPanel(List<Attendance> attendanceList) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(0xF8F9FA));
        
        // Calculate statistics
        int total = attendanceList.size();
        int present = 0, absent = 0, late = 0;
        
        for (Attendance att : attendanceList) {
            switch (att.getStatus()) {
                case "Present": present++; break;
                case "Absent": absent++; break;
                case "Late": late++; break;
            }
        }
        
        double attendanceRate = total > 0 ? (double)(present + late) / total * 100 : 0;
        
        // Create summary cards
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBackground(new Color(0xF8F9FA));
        
        statsPanel.add(createDetailStatCard("Total Sessions", String.valueOf(total), new Color(0, 123, 255)));
        statsPanel.add(createDetailStatCard("Present", String.valueOf(present), new Color(40, 167, 69)));
        statsPanel.add(createDetailStatCard("Absent", String.valueOf(absent), new Color(220, 53, 69)));
        statsPanel.add(createDetailStatCard("Late", String.valueOf(late), new Color(255, 193, 7)));
        statsPanel.add(createDetailStatCard("Attendance Rate", String.format("%.1f%%", attendanceRate), 
                      attendanceRate >= 80 ? new Color(40, 167, 69) : attendanceRate >= 60 ? new Color(255, 193, 7) : new Color(220, 53, 69)));
        statsPanel.add(createDetailStatCard("Sessions This Month", String.valueOf(getSessionsThisMonth(attendanceList)), new Color(108, 117, 125)));
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAttendanceBySubjectPanel(List<Attendance> attendanceList) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(0xF8F9FA));
        
        // Group attendance by subject
        Map<String, Integer[]> subjectStats = new HashMap<>(); // [present, absent, late]
        
        for (Attendance att : attendanceList) {
            String subject = getSubjectNameFromClass(att.getClassId());
            subjectStats.putIfAbsent(subject, new Integer[]{0, 0, 0});
            
            switch (att.getStatus()) {
                case "Present": subjectStats.get(subject)[0]++; break;
                case "Absent": subjectStats.get(subject)[1]++; break;
                case "Late": subjectStats.get(subject)[2]++; break;
            }
        }
        
        // Create table
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Subject", "Present", "Absent", "Late", "Total", "Attendance Rate"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Map.Entry<String, Integer[]> entry : subjectStats.entrySet()) {
            String subject = entry.getKey();
            Integer[] stats = entry.getValue();
            int total = stats[0] + stats[1] + stats[2];
            double rate = total > 0 ? (double)(stats[0] + stats[2]) / total * 100 : 0;
            
            model.addRow(new Object[]{
                subject,
                stats[0],
                stats[1], 
                stats[2],
                total,
                String.format("%.1f%%", rate)
            });
        }
        
        JTable table = new JTable(model);
        setupTable(table);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAttendanceTimelinePanel(List<Attendance> attendanceList) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(0xF8F9FA));
        
        // Sort attendance by date
        attendanceList.sort((a1, a2) -> a1.getDate().compareTo(a2.getDate()));
        
        // Create detailed table
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Date", "Day", "Subject", "Class", "Status", "Time Marked"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Attendance att : attendanceList) {
            String dayOfWeek = att.getDate().getDayOfWeek().toString();
            // Capitalize first letter and make rest lowercase for better display
            dayOfWeek = dayOfWeek.substring(0, 1) + dayOfWeek.substring(1).toLowerCase();
            
            String subject = getSubjectNameFromClass(att.getClassId());
            
            model.addRow(new Object[]{
                att.getDate().toString(),
                dayOfWeek,
                subject,
                att.getClassId(),
                att.getStatus(),
                att.getTimeMarked().toString()
            });
        }
        
        JTable table = new JTable(model);
        setupTable(table);
        
        // Color code rows based on status
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 4);
                    switch (status) {
                        case "Present":
                            c.setBackground(new Color(212, 237, 218));
                            break;
                        case "Absent":
                            c.setBackground(new Color(248, 215, 218));
                            break;
                        case "Late":
                            c.setBackground(new Color(255, 243, 205));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDetailStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(Color.GRAY);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private int getSessionsThisMonth(List<Attendance> attendanceList) {
        java.time.LocalDate now = java.time.LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        
        int count = 0;
        for (Attendance att : attendanceList) {
            java.time.LocalDate attendanceDate = att.getDate();
            if (attendanceDate.getMonthValue() == currentMonth && 
                attendanceDate.getYear() == currentYear) {
                count++;
            }
        }
        return count;
    }
    

    


    private void showStudentMessageDialog(int row) {
        // Similar to tutor's showMessageDialog but adapted for students
        try {
            String from = (String) studentMessagesTableModel.getValueAt(row, 0);
            String date = (String) studentMessagesTableModel.getValueAt(row, 1);
            String time = (String) studentMessagesTableModel.getValueAt(row, 2);
            String priority = (String) studentMessagesTableModel.getValueAt(row, 3);
            String reconstructedDateTime = date + (time.isEmpty() ? "" : "T" + time);

            List<Message> messages = function.getMessagesForUser(currentStudentId);
            Message selectedMessage = null;
            for (Message msg : messages) {
                if (msg.getDateTime().toString().equals(reconstructedDateTime)) {
                    selectedMessage = msg;
                    break;
                }
            }

            if (selectedMessage != null) {
                JDialog dialog = new JDialog(this, "Message Details", true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.setSize(500, 400);
                dialog.setLocationRelativeTo(this);

                // Header panel
                JPanel headerPanel = new JPanel(new GridLayout(3, 2, 5, 5));
                headerPanel.setBorder(BorderFactory.createTitledBorder("Message Information"));
                headerPanel.add(new JLabel("From:"));
                headerPanel.add(new JLabel(from));
                headerPanel.add(new JLabel("Date/Time:"));
                headerPanel.add(new JLabel(reconstructedDateTime));
                headerPanel.add(new JLabel("Priority:"));
                headerPanel.add(new JLabel(priority));

                // Content area
                JTextArea contentArea = new JTextArea(selectedMessage.getContent());
                contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                contentArea.setEditable(false);
                contentArea.setLineWrap(true);
                contentArea.setWrapStyleWord(true);
                JScrollPane contentScrollPane = new JScrollPane(contentArea);
                contentScrollPane.setBorder(BorderFactory.createTitledBorder("Message Content"));

                // Close button
                JButton closeButton = new JButton("Close");
                styleButton(closeButton, new Color(108, 117, 125));
                closeButton.addActionListener(e -> dialog.dispose());

                dialog.add(headerPanel, BorderLayout.NORTH);
                dialog.add(contentScrollPane, BorderLayout.CENTER);
                dialog.add(closeButton, BorderLayout.SOUTH);

                dialog.setVisible(true);
            }
        } catch (Exception e) {
            showErrorDialog("Error", "Failed to display message: " + e.getMessage());
        }
    }

    private void loadRecipientsForStudent(JComboBox<String> toComboBox) {
        toComboBox.removeAllItems();
        try {
            List<String> userLines = function.readUsers();
            for (String line : userLines) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && !parts[0].trim().equals(currentStudentId)) {
                    String userId = parts[0].trim();
                    String name = parts[4].trim();
                    String role = parts[3].trim();
                    
                    // Students can only message tutors, admin, and receptionists
                    if (role.equals("tutor") || role.equals("admin") || role.equals("receptionist")) {
                        toComboBox.addItem(userId + " - " + name + " (" + role + ")");
                    }
                }
            }
        } catch (Exception e) {
            showErrorDialog("Error", "Failed to load recipients: " + e.getMessage());
        }
    }

    private void sendStudentMessage(String to, String subject, String content, String priority) {
        try {
            String receiverId = to.split(" - ")[0];
            
            Message message = new Message(
                function.generateMessageId(),
                currentStudentId,
                receiverId,
                "Message",
                subject,
                content,
                java.time.LocalDateTime.now(),
                "Sent",
                priority,
                ""
            );

            function.addMessage(message.toCsvString());
            showSuccessDialog("Success", "Message sent successfully!");
            loadStudentMessagesData(); // Refresh messages
        } catch (Exception e) {
            showErrorDialog("Error", "Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Logout and return to main page
     */
    private void logout() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            try {
                new main_page().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error returning to main page: " + e.getMessage());
                System.exit(0);
            }
        });
    }

    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new student_dashboard("student");
        });
    }
}
