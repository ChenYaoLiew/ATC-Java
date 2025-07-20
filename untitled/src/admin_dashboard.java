import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class admin_dashboard extends JFrame {
    // Constants
    private static final Color BACKGROUND_COLOR = new Color(0xF8F9FA);
    private static final Color HEADER_COLOR = new Color(0x343A40);
    private static final Color BUTTON_PRIMARY = new Color(0, 123, 255);
    private static final Color BUTTON_SUCCESS = new Color(40, 167, 69);
    private static final Color BUTTON_DANGER = new Color(220, 53, 69);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    // Static data collections
    private static List<User> users = new ArrayList<>();
    private static List<Subject> subjects = new ArrayList<>();
    private static List<Payment> payments = new ArrayList<>();

    // Main UI Components
    private JPanel mainPanel;
    private JLabel welcomeLabel;
    private JButton logoutButton;
    private JTabbedPane tabbedPane;

    // Staff Management Tab Components
    private JPanel staffPanel;
    private JTable tutorsTable;
    private JTable receptionistsTable;
    private DefaultTableModel tutorsTableModel;
    private DefaultTableModel receptionistsTableModel;
    private JButton registerTutorButton;
    private JButton deleteTutorButton;
    private JButton registerReceptionistButton;
    private JButton deleteReceptionistButton;

    // Subject Management Tab Components
    private JPanel subjectsPanel;
    private JTable subjectsTable;
    private DefaultTableModel subjectsTableModel;
    private JButton assignTutorButton;

    // Reports Tab Components
    private JPanel reportsPanel;
    private JButton viewIncomeReportButton;

    // Profile Tab Components
    private JPanel profilePanel;
    private JTextField profileNameField;
    private JTextField profileUsernameField;
    private JPasswordField profilePasswordField;
    private JButton updateProfileButton;

    // Messaging Tab Components
    private JTable adminMessagesTable;
    private DefaultTableModel adminMessagesTableModel;

    // User Data
    private String adminName;
    private String adminUsername;
    private String adminPassword;

    // ==================== Initialization Methods ====================
    public admin_dashboard(String name) {
        this.adminName = name;
        loadUsersFromFile();
        findAdminCredentials();
        initializeGUI();
        loadAllData();
    }

    private void findAdminCredentials() {
        // Find the admin user
        for (User user : users) {
            if (user.getRole().equals("admin") && user.getName().equals(adminName)) {
                this.adminUsername = user.getUsername();
                this.adminPassword = user.getPassword();
                break;
            }
        }

        // If admin not found, use default values
        if (adminUsername == null) {
            this.adminUsername = "admin";
            this.adminPassword = "admin123";
        }
    }

    private void initializeGUI() {
        setTitle("Admin Dashboard - " + adminName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setupMainPanel();
        setupHeaderPanel();
        initializeTableModels();
        setupTabbedPane();
        setupActionListeners();
        setLookAndFeel();
    }

    private void initializeTableModels() {
        // Initialize tutors table model
        tutorsTableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "IC/Passport", "Email", "Contact", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Initialize receptionists table model
        receptionistsTableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Username", "Contact"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Initialize subjects table model
        subjectsTableModel = new DefaultTableModel(
            new String[]{"Subject ID", "Subject", "Level", "Assigned Tutor"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void setupMainPanel() {
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setOpaque(true);
        setContentPane(mainPanel);
    }

    private void setupHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        headerPanel.setOpaque(true);

        welcomeLabel = new JLabel("Welcome, " + adminName + " (Admin)", SwingConstants.CENTER);
        welcomeLabel.setFont(HEADER_FONT);
        welcomeLabel.setForeground(Color.WHITE);

        logoutButton = createStyledButton("Logout", BUTTON_DANGER);
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void setupTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(NORMAL_FONT);
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.addTab("👥 Staff", createStaffPanel());
        tabbedPane.addTab("📚 Subjects", createSubjectsPanel());
        tabbedPane.addTab("📊 Reports", createReportsPanel());
        tabbedPane.addTab("💬 Messages", createAdminMessagingPanel());
        tabbedPane.addTab("👤 Profile", createProfilePanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== UI Component Creation Methods ====================
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(NORMAL_FONT);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorderPainted(false);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(darken(bgColor));
            }
            
            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private JTextField createStyledTextField(String initialValue) {
        JTextField field = new JTextField(initialValue);
        field.setFont(NORMAL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDEE2E6)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(NORMAL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDEE2E6)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return field;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(NORMAL_FONT);
        table.getTableHeader().setBackground(BACKGROUND_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(LABEL_FONT);
        table.setShowGrid(true);
        table.setGridColor(new Color(0xDEE2E6));
        table.setSelectionBackground(new Color(0xE7F3FF));
        table.setBackground(Color.WHITE);
    }

    // ==================== Data Management Methods ====================
    private void loadAllData() {
        try {
            loadTutorsData();
            loadReceptionistsData();
            loadSubjectsData();
            loadProfileData();
            if (adminMessagesTableModel != null) loadAdminMessagesData();
        } catch (Exception e) {
            showError("Error loading data: " + e.getMessage());
        }
    }

    private void loadUsersFromFile() {
        users.clear();
        List<String> userLines = function.readUsers();
        
        for (String line : userLines) {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                createUserFromData(parts);
            }
        }

        createDefaultAdminIfNeeded();
    }

    private void createUserFromData(String[] parts) {
        String id = parts[0].trim();
        String username = parts[1].trim();
        String password = parts[2].trim();
        String role = parts[3].trim();
        String name = parts[4].trim();

        User user = createUserByRole(username, password, name, role, id);
        if (user != null) {
            users.add(user);
        }
    }

    private User createUserByRole(String username, String password, String name, String role, String id) {
        switch (role.toLowerCase()) {
            case "admin":
                return new User(username, password, name, "", "", role, id) {};
            case "receptionist":
                Receptionist receptionist = new Receptionist(username, password, name, "", "", "");
                receptionist.setStudentId(id);
                return receptionist;
            case "tutor":
                Tutor tutor = new Tutor(username, password, name, "", "");
                tutor.setStudentId(id);
                return tutor;
            case "student":
                return new User(username, password, name, "", "", role, id) {};
            default:
                return null;
        }
    }

    private void createDefaultAdminIfNeeded() {
        if (users.isEmpty()) {
            String adminId = function.generateAdminId();
            User defaultAdmin = new User("admin", "admin123", "Default Admin", "", "", "admin", adminId) {};
            users.add(defaultAdmin);
            saveUser(adminId, "admin", "admin123", "admin", "Default Admin");
        }
    }

    private void saveUser(String id, String username, String password, String role, String name) {
        String userData = String.format("%s,%s,%s,%s,%s", id, username, password, role, name);
        if (!function.addUser(userData)) {
            showError("Error saving user: " + username);
        }
    }

    // User authentication method
    public static User authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null; // Authentication failed
    }

    // Methods for managing tutors
    public void registerTutor(String username, String password, String name, String icpassport, 
                            String email, String contactNumber, String address) {
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose another username.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Generate new tutor ID
        String tutorId = function.generateTutorId();

        // Create new tutor object
        Tutor newTutor = new Tutor(username, password, name, email, contactNumber);
        newTutor.setStudentId(tutorId); // Set the ID
        users.add(newTutor);
        
        // Save to users.txt (id,username,password,role,name)
        String userData = tutorId + "," + username + "," + password + ",tutor," + name;
        boolean userSaved = function.addUser(userData);

        // Save to tutors.txt (id,name,icpassport,email,phone,address,status)
        String tutorData = tutorId + "," + name + "," + icpassport + "," + email + "," + contactNumber + 
                          "," + address + ",Active";
        boolean tutorSaved = function.addTutor(tutorData);

        if (userSaved && tutorSaved) {
            JOptionPane.showMessageDialog(this, "Tutor registered successfully: " + name + "\nTutor ID: " + tutorId);
        } else {
            JOptionPane.showMessageDialog(this, "Error saving tutor data to files.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            // Rollback changes if one file update failed
            if (userSaved) {
                function.deleteUser(userData);
            }
            if (tutorSaved) {
                function.deleteTutor(tutorData);
            }
            users.remove(newTutor);
        }
    }

    public void deleteTutor(String username) {
        User tutorToRemove = null;
        String tutorId = null;
        String userData = null;
        String tutorData = null;

        // Find the tutor and their data
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getRole().equals("tutor")) {
                tutorToRemove = user;
                tutorId = user.getStudentId(); // This contains the tutor ID (e.g., T001)
                // Format for users.txt (id,username,password,role,name)
                userData = tutorId + "," + user.getUsername() + "," + user.getPassword() + 
                          ",tutor," + user.getName();
                
                // Find corresponding tutor data
                List<String> tutorLines = function.readTutors();
                for (String line : tutorLines) {
                    String[] parts = line.split(",");
                    // id,name,icpassport,email,phone,address,status
                    if (parts.length >= 7 && parts[0].equals(tutorId)) {
                        tutorData = line;
                        break;
                    }
                }
                break;
            }
        }

        if (tutorToRemove != null) {
            boolean userDeleted = function.deleteUser(userData);
            boolean tutorDeleted = tutorData != null && function.deleteTutor(tutorData);

            if (userDeleted && (tutorData == null || tutorDeleted)) {
                users.remove(tutorToRemove);

                // Also remove tutor from subjects
                List<Subject> subjectsToUpdate = new ArrayList<>();
                for (Subject subject : subjects) {
                    if (subject.getTutorUsername().equals(username)) {
                        subjectsToUpdate.add(subject);
                    }
                }

                for (Subject subject : subjectsToUpdate) {
                    subject.setTutorUsername(null);
                    // Update subject in file
                    String oldData = subject.getName() + "," + subject.getLevel() + "," + 
                                   subject.getTutorUsername() + "," + subject.getMonthlyFee();
                    String newData = subject.getName() + "," + subject.getLevel() + "," + 
                                   "," + subject.getMonthlyFee();
                    function.updateSubject(oldData, newData);
                }

                JOptionPane.showMessageDialog(this, "Tutor deleted successfully: " + tutorToRemove.getName());
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting tutor data from files.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tutor not found with username: " + username,
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void assignTutorToSubject(String tutorId, String subjectId) {
        // Verify tutor exists
        boolean tutorExists = false;
        String tutorName = "";
        for (User user : users) {
            if (user.getRole().equals("tutor") && user.getStudentId().equals(tutorId)) {
                tutorExists = true;
                tutorName = user.getName();
                break;
            }
        }

        if (!tutorExists && !tutorId.equals("empty")) {
            JOptionPane.showMessageDialog(this, "Tutor not found with ID: " + tutorId,
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Find and update subject
        List<String> subjects = function.readSubjects();
        boolean updated = false;
        
        for (int i = 0; i < subjects.size(); i++) {
            String[] parts = subjects.get(i).split(",");
            if (parts[0].equals(subjectId)) {
                // Format: id,name,level,tutorid
                String newLine = parts[0] + "," + parts[1] + "," + parts[2] + "," + tutorId;
                String oldLine = subjects.get(i);
                
                if (function.updateSubject(oldLine, newLine)) {
                    updated = true;
                    JOptionPane.showMessageDialog(this, 
                        "Successfully " + (tutorId.equals("empty") ? "removed tutor from" : 
                        "assigned tutor " + tutorName + " to") + " " + parts[1] + " (" + parts[2] + ")");
                }
                break;
            }
        }

        if (!updated) {
            JOptionPane.showMessageDialog(this, "Error updating subject assignment.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Methods for managing receptionists
    public void registerReceptionist(String username, String password, String name) {
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose another username.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Generate new receptionist ID
        String receptionistId = function.generateReceptionistId();

        // Create new receptionist object
        Receptionist newReceptionist = new Receptionist(username, password, name, "", "", "");
        newReceptionist.setStudentId(receptionistId);
        users.add(newReceptionist);

        // Save to users.txt (id,username,password,role,name)
        String userData = receptionistId + "," + username + "," + password + ",receptionist," + name;
        
        if (function.addUser(userData)) {
            JOptionPane.showMessageDialog(this, "Receptionist registered successfully: " + name + "\nReceptionist ID: " + receptionistId);
        } else {
            JOptionPane.showMessageDialog(this, "Error saving receptionist data to file.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            users.remove(newReceptionist);
        }
    }

    public void deleteReceptionist(String username) {
        User receptionistToRemove = null;
        String userData = null;

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getRole().equals("receptionist")) {
                receptionistToRemove = user;
                // Format: id,username,password,role,name
                userData = user.getStudentId() + "," + user.getUsername() + "," + 
                          user.getPassword() + ",receptionist," + user.getName();
                break;
            }
        }

        if (receptionistToRemove != null && function.deleteUser(userData)) {
            users.remove(receptionistToRemove);
            JOptionPane.showMessageDialog(this, "Receptionist deleted successfully: " + receptionistToRemove.getName());
        } else {
            JOptionPane.showMessageDialog(this, "Receptionist not found with username: " + username,
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to view monthly income report
    public String viewMonthlyIncomeReport(String month, int year) {
        double totalIncome = 0;
        List<String> paymentLines = function.readPayments();
        payments.clear();

        // Prepare details for the report
        List<String> paymentDetails = new ArrayList<>();
        String billingMonth = String.format("%04d-%02d", year, getMonthNumber(month));

        for (String line : paymentLines) {
            String[] parts = line.split(",");
            if (parts.length >= 9) {
                // payments.txt: PaymentID,StudentID,Field3,Amount,PaymentMethod,DateTime,BillingMonth,ReceptionistID,Status
                String studentId = parts[1].trim();
                String amountStr = parts[3].trim();
                String paymentMethod = parts[4].trim();
                String dateTime = parts[5].trim();
                String payMonth = parts[6].trim();
                String status = parts[8].trim();

                if (payMonth.equals(billingMonth)) {
                    double amount = 0;
                    try { amount = Double.parseDouble(amountStr); } catch (Exception e) { continue; }
                    totalIncome += amount;
                    String studentName = getStudentNameById(studentId);
                    paymentDetails.add(String.format("Student: %s | Date: %s | Amount: $%.2f | Method: %s | Status: %s",
                            studentName, dateTime, amount, paymentMethod, status));
                }
            }
        }

        // Generate report as string
        StringBuilder report = new StringBuilder();
        report.append("===== MONTHLY INCOME REPORT =====\n");
        report.append("Month: ").append(month).append(" ").append(year).append("\n");
        report.append("Total Income: $").append(String.format("%.2f", totalIncome)).append("\n\n");

        report.append("--- Payment Details ---\n");
        if (paymentDetails.isEmpty()) {
            report.append("No payments found for this month.\n");
        } else {
            for (String detail : paymentDetails) {
                report.append(detail).append("\n");
            }
        }
        report.append("================================");
        return report.toString();
    }

    // Helper to convert month name to number (e.g., "March" -> 3)
    private int getMonthNumber(String monthName) {
        switch (monthName.toLowerCase()) {
            case "january": return 1;
            case "february": return 2;
            case "march": return 3;
            case "april": return 4;
            case "may": return 5;
            case "june": return 6;
            case "july": return 7;
            case "august": return 8;
            case "september": return 9;
            case "october": return 10;
            case "november": return 11;
            case "december": return 12;
            default: return 1;
        }
    }

    // Method to display all tutors
    public List<Tutor> getAllTutors() {
        List<Tutor> tutorList = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals("tutor")) {
                tutorList.add((Tutor) user);
            }
        }
        return tutorList;
    }

    // Method to display all receptionists
    public List<Receptionist> getAllReceptionists() {
        List<Receptionist> receptionistList = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals("receptionist")) {
                receptionistList.add((Receptionist) user);
            }
        }
        return receptionistList;
    }

    // Helper method to add a test payment
    public static void addTestPayment(String studentUsername, String subject, String level,
                                     double amount, String month, int year) {
        String receiptNumber = "R" + payments.size() + 1;
        Payment payment = new Payment(studentUsername, subject, level, amount,
                                     LocalDate.now(), month + " " + year, receiptNumber);
        payments.add(payment);

        // Save to file
        String paymentData = studentUsername + "," + subject + "," + level + "," + amount + "," +
                           LocalDate.now() + "," + month + " " + year + "," + receiptNumber;
        function.addPayment(paymentData);
    }

    // Getter for users list
    public static List<User> getUsers() {
        return users;
    }

    // Getter for admin password
    private String getAdminPassword() {
        return adminPassword;
    }

    // Method to update profile
    public void updateProfile(String name, String username, String password) {
        // Find the current admin user
        for (User user : users) {
            if (user.getRole().equals("admin") && user.getName().equals(adminName)) {
                String oldData = user.getStudentId() + "," + user.getUsername() + "," + 
                               getAdminPassword() + ",admin," + user.getName();

                // Update only the provided fields
                if (name != null && !name.isEmpty()) {
                    user.setName(name);
                    this.adminName = name;
                }
                if (username != null && !username.isEmpty()) {
                    user.setUsername(username);
                    this.adminUsername = username;
                }
                if (password != null && !password.isEmpty()) {
                    user.setPassword(password);
                    this.adminPassword = password;
                }

                String newData = user.getStudentId() + "," + user.getUsername() + "," + 
                               user.getPassword() + ",admin," + user.getName();
                
                if (function.updateUser(oldData, newData)) {
                    showSuccess("Profile updated successfully!");
                    welcomeLabel.setText("Welcome, " + user.getName() + " (Admin)");
                } else {
                    showError("Failed to update profile!");
                }
                break;
            }
        }
    }

    private void showUpdateProfileDialog() {
        JTextField nameField = createStyledTextField(adminName);
        JTextField usernameField = createStyledTextField(adminUsername);
        JPasswordField passwordField = createStyledPasswordField();

        Object[] message = {
            "👤 Name:", nameField,
            "👤 Username:", usernameField,
            "🔑 New Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Profile", 
                                                 JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Only update if at least one field is filled
            if (name.isEmpty() && username.isEmpty() && password.isEmpty()) {
                showError("At least one field must be filled!");
                return;
            }

            // Pass empty strings as null to indicate no change
            updateProfile(
                name.isEmpty() ? null : name,
                username.isEmpty() ? null : username,
                password.isEmpty() ? null : password
            );
        }
    }

    private void setupActionListeners() {
        registerTutorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegisterTutorDialog();
            }
        });

        deleteTutorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDeleteTutorDialog();
            }
        });

        assignTutorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAssignTutorDialog();
            }
        });

        registerReceptionistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegisterReceptionistDialog();
            }
        });

        deleteReceptionistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDeleteReceptionistDialog();
            }
        });

        viewIncomeReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showIncomeReportDialog();
            }
        });

        updateProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUpdateProfileDialog();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }

    private void showRegisterTutorDialog() {
        JTextField usernameField = createStyledTextField("");
        JPasswordField passwordField = createStyledPasswordField();
        JTextField nameField = createStyledTextField("");
        JTextField icpassportField = createStyledTextField("");
        JTextField emailField = createStyledTextField("");
        JTextField contactField = createStyledTextField("");
        JTextField addressField = createStyledTextField("");

        Object[] message = {
            "👤 Username:", usernameField,
            "🔑 Password:", passwordField,
            "📝 Name:", nameField,
            "📄 IC/Passport:", icpassportField,
            "📧 Email:", emailField,
            "📞 Contact:", contactField,
            "🏠 Address:", addressField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Register Tutor", 
                                                 JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText();
            String icpassport = icpassportField.getText();
            String email = emailField.getText();
            String contact = contactField.getText();
            String address = addressField.getText();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || 
                icpassport.isEmpty() || email.isEmpty() || contact.isEmpty() || 
                address.isEmpty()) {
                showError("All fields are required!");
                return;
            }

            registerTutor(username, password, name, icpassport, email, contact, address);
        }
    }

    private void showDeleteTutorDialog() {
        List<Tutor> tutors = getAllTutors();
        if (tutors.isEmpty()) {
            showError("No tutors found in the system.");
            return;
        }

        DefaultComboBoxModel<String> tutorModel = new DefaultComboBoxModel<>();
        for (Tutor tutor : tutors) {
            tutorModel.addElement(tutor.getUsername() + " - " + tutor.getName());
        }

        JComboBox<String> tutorComboBox = new JComboBox<>(tutorModel);
        tutorComboBox.setFont(NORMAL_FONT);

        Object[] message = {
            "👨‍🏫 Select tutor to delete:", tutorComboBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Delete Tutor", 
                                                 JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String selected = (String) tutorComboBox.getSelectedItem();
            String username = selected.split(" - ")[0];
            deleteTutor(username);
        }
    }

    private void showAssignTutorDialog() {
        // Get all tutors
        List<String> tutorChoices = new ArrayList<>();
        tutorChoices.add("empty");
        
        for (User user : users) {
            if (user.getRole().equals("tutor")) {
                tutorChoices.add(user.getStudentId() + " - " + user.getName());
            }
        }

        if (tutorChoices.size() <= 1) {
            showError("No tutors found in the system.");
            return;
        }

        // Get all subjects
        List<String> subjects = function.readSubjects();
        List<String> subjectChoices = new ArrayList<>();
        
        for (String subject : subjects) {
            String[] parts = subject.split(",");
            String subjectId = parts[0];
            String subjectName = parts[1];
            String level = parts[2];
            String currentTutor = parts.length > 3 ? parts[3] : "empty";
            
            subjectChoices.add(subjectId + " - " + subjectName + " " + level + 
                             " (Current: " + currentTutor + ")");
        }

        JComboBox<String> tutorComboBox = new JComboBox<>(tutorChoices.toArray(new String[0]));
        JComboBox<String> subjectComboBox = new JComboBox<>(subjectChoices.toArray(new String[0]));

        tutorComboBox.setFont(NORMAL_FONT);
        subjectComboBox.setFont(NORMAL_FONT);

        Object[] message = {
            "📚 Select subject:", subjectComboBox,
            "👨‍🏫 Select tutor:", tutorComboBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Assign Tutor to Subject", 
                                                 JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            String selectedTutor = (String) tutorComboBox.getSelectedItem();
            
            String subjectId = selectedSubject.split(" - ")[0];
            String tutorId = selectedTutor.equals("empty") ? "empty" : 
                           selectedTutor.split(" - ")[0];

            assignTutorToSubject(tutorId, subjectId);
        }
    }

    private void showRegisterReceptionistDialog() {
        JTextField usernameField = createStyledTextField("");
        JPasswordField passwordField = createStyledPasswordField();
        JTextField nameField = createStyledTextField("");

        Object[] message = {
            "👤 Username:", usernameField,
            "🔑 Password:", passwordField,
            "📝 Name:", nameField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Register Receptionist", 
                                                 JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                showError("All fields are required!");
                return;
            }

            registerReceptionist(username, password, name);
        }
    }

    private void showDeleteReceptionistDialog() {
        List<Receptionist> receptionists = getAllReceptionists();
        if (receptionists.isEmpty()) {
            showError("No receptionists found in the system.");
            return;
        }

        DefaultComboBoxModel<String> recModel = new DefaultComboBoxModel<>();
        for (Receptionist receptionist : receptionists) {
            recModel.addElement(receptionist.getUsername() + " - " + receptionist.getName());
        }

        JComboBox<String> recComboBox = new JComboBox<>(recModel);
        recComboBox.setFont(NORMAL_FONT);

        Object[] message = {
            "👩‍💼 Select receptionist to delete:", recComboBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Delete Receptionist", 
                                                 JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String selected = (String) recComboBox.getSelectedItem();
            String username = selected.split(" - ")[0];
            deleteReceptionist(username);
        }
    }

    private void showIncomeReportDialog() {
        String[] months = {"January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};

        JComboBox<String> monthComboBox = new JComboBox<>(months);
        JTextField yearField = createStyledTextField(String.valueOf(LocalDate.now().getYear()));

        monthComboBox.setFont(NORMAL_FONT);

        Object[] message = {
            "📅 Select month:", monthComboBox,
            "📅 Year:", yearField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "View Income Report", 
                                                 JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String month = (String) monthComboBox.getSelectedItem();
            String yearStr = yearField.getText();

            try {
                int year = Integer.parseInt(yearStr);
                String reportText = viewMonthlyIncomeReport(month, year);

                JTextArea reportArea = new JTextArea(reportText);
                reportArea.setEditable(false);
                reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
                reportArea.setBackground(BACKGROUND_COLOR);

                JScrollPane scrollPane = new JScrollPane(reportArea);
                scrollPane.setPreferredSize(new Dimension(500, 400));

                JOptionPane.showMessageDialog(this, scrollPane, "Monthly Income Report", 
                                           JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                showError("Invalid year!");
            }
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new main_page().setVisible(true));
        }
    }

    // ==================== Attendance Report Methods ====================
    
    private void showAttendanceReportDialog() {
        try {
            // Create dialog
            JDialog dialog = new JDialog(this, "Attendance Summary Report", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setSize(800, 600);
            dialog.setLocationRelativeTo(this);

            // Summary statistics panel
            JPanel summaryPanel = new JPanel(new GridLayout(2, 4, 10, 10));
            summaryPanel.setBorder(BorderFactory.createTitledBorder("Overall Statistics"));
            summaryPanel.setBackground(BACKGROUND_COLOR);

            // Calculate overall statistics
            List<Attendance> allAttendance = function.readAttendance().stream()
                .filter(line -> !line.trim().isEmpty())
                .map(Attendance::fromCsvString)
                .filter(att -> att != null)
                .collect(java.util.stream.Collectors.toList());

            int totalRecords = allAttendance.size();
            int presentCount = 0, absentCount = 0, lateCount = 0;

            for (Attendance att : allAttendance) {
                switch (att.getStatus()) {
                    case "Present": presentCount++; break;
                    case "Absent": absentCount++; break;
                    case "Late": lateCount++; break;
                }
            }

            // Create stat cards
            summaryPanel.add(createAdminStatCard("Total Records", String.valueOf(totalRecords), BUTTON_PRIMARY));
            summaryPanel.add(createAdminStatCard("Present", String.valueOf(presentCount), BUTTON_SUCCESS));
            summaryPanel.add(createAdminStatCard("Absent", String.valueOf(absentCount), BUTTON_DANGER));
            summaryPanel.add(createAdminStatCard("Late", String.valueOf(lateCount), new Color(255, 193, 7)));

            // Calculate percentages
            double presentRate = totalRecords > 0 ? (double) presentCount / totalRecords * 100 : 0;
            double absentRate = totalRecords > 0 ? (double) absentCount / totalRecords * 100 : 0;
            double lateRate = totalRecords > 0 ? (double) lateCount / totalRecords * 100 : 0;

            summaryPanel.add(createAdminStatCard("Present Rate", String.format("%.1f%%", presentRate), BUTTON_SUCCESS));
            summaryPanel.add(createAdminStatCard("Absent Rate", String.format("%.1f%%", absentRate), BUTTON_DANGER));
            summaryPanel.add(createAdminStatCard("Late Rate", String.format("%.1f%%", lateRate), new Color(255, 193, 7)));
            summaryPanel.add(createAdminStatCard("Attendance Rate", String.format("%.1f%%", presentRate + lateRate), new Color(108, 117, 125)));

            // Detailed table
            DefaultTableModel reportModel = new DefaultTableModel(
                    new String[]{"Date", "Student", "Class", "Status", "Marked By", "Time"}, 0
            );
            JTable reportTable = new JTable(reportModel);
            styleTable(reportTable);

            for (Attendance att : allAttendance) {
                String studentName = getStudentNameById(att.getStudentId());
                String tutorName = getTutorNameById(att.getMarkedBy());
                reportModel.addRow(new Object[]{
                    att.getDate().toString(),
                    studentName,
                    att.getClassId(),
                    att.getStatus(),
                    tutorName,
                    att.getTimeMarked().toString()
                });
            }

            dialog.add(summaryPanel, BorderLayout.NORTH);
            dialog.add(new JScrollPane(reportTable), BorderLayout.CENTER);

            JButton closeButton = createStyledButton("Close", new Color(108, 117, 125));
            closeButton.addActionListener(e -> dialog.dispose());
            dialog.add(closeButton, BorderLayout.SOUTH);

            dialog.setVisible(true);

        } catch (Exception e) {
            showError("Failed to generate attendance report: " + e.getMessage());
        }
    }

    private void showStudentAttendanceDialog() {
        try {
            // Get list of students
            List<String> studentLines = function.readStudents();
            if (studentLines.isEmpty()) {
                showError("No students found in the system.");
                return;
            }

            DefaultComboBoxModel<String> studentModel = new DefaultComboBoxModel<>();
            for (String line : studentLines) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    studentModel.addElement(parts[0].trim() + " - " + parts[1].trim());
                }
            }

            JComboBox<String> studentComboBox = new JComboBox<>(studentModel);
            studentComboBox.setFont(NORMAL_FONT);

            Object[] message = {
                "👩‍🎓 Select student:", studentComboBox
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Student Attendance Report", 
                                                     JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String selected = (String) studentComboBox.getSelectedItem();
                String studentId = selected.split(" - ")[0];
                showStudentAttendanceDetails(studentId);
            }

        } catch (Exception e) {
            showError("Failed to show student attendance: " + e.getMessage());
        }
    }

    private void showTutorAttendanceDialog() {
        try {
            // Get list of tutors
            List<String> userLines = function.readUsers();
            DefaultComboBoxModel<String> tutorModel = new DefaultComboBoxModel<>();
            
            for (String line : userLines) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && "tutor".equals(parts[3].trim())) {
                    tutorModel.addElement(parts[0].trim() + " - " + parts[4].trim());
                }
            }

            if (tutorModel.getSize() == 0) {
                showError("No tutors found in the system.");
                return;
            }

            JComboBox<String> tutorComboBox = new JComboBox<>(tutorModel);
            tutorComboBox.setFont(NORMAL_FONT);

            Object[] message = {
                "👨‍🏫 Select tutor:", tutorComboBox
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Tutor Attendance Report", 
                                                     JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String selected = (String) tutorComboBox.getSelectedItem();
                String tutorId = selected.split(" - ")[0];
                showTutorAttendanceDetails(tutorId);
            }

        } catch (Exception e) {
            showError("Failed to show tutor attendance: " + e.getMessage());
        }
    }

    private void showAttendanceTrendsDialog() {
        try {
            // Create dialog for attendance trends
            JDialog dialog = new JDialog(this, "Attendance Trends Analysis", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setSize(700, 500);
            dialog.setLocationRelativeTo(this);

            // Create content area
            JTextArea trendsArea = new JTextArea();
            trendsArea.setEditable(false);
            trendsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            trendsArea.setBackground(BACKGROUND_COLOR);

            // Analyze trends
            String trendsReport = generateAttendanceTrendsReport();
            trendsArea.setText(trendsReport);

            JScrollPane scrollPane = new JScrollPane(trendsArea);
            dialog.add(scrollPane, BorderLayout.CENTER);

            JButton closeButton = createStyledButton("Close", new Color(108, 117, 125));
            closeButton.addActionListener(e -> dialog.dispose());
            dialog.add(closeButton, BorderLayout.SOUTH);

            dialog.setVisible(true);

        } catch (Exception e) {
            showError("Failed to generate attendance trends: " + e.getMessage());
        }
    }

    // Helper methods for attendance reports
    private JPanel createAdminStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private String getStudentNameById(String studentId) {
        try {
            List<String> studentLines = function.readStudents();
            for (String line : studentLines) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].trim().equals(studentId)) {
                    return parts[1].trim();
                }
            }
        } catch (Exception e) {
            // Return student ID if can't find name
        }
        return studentId;
    }

    private String getTutorNameById(String tutorId) {
        try {
            List<String> userLines = function.readUsers();
            for (String line : userLines) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].trim().equals(tutorId)) {
                    return parts[4].trim();
                }
            }
        } catch (Exception e) {
            // Return tutor ID if can't find name
        }
        return tutorId;
    }

    private void showStudentAttendanceDetails(String studentId) {
        try {
            List<Attendance> studentAttendance = function.getAttendanceForStudent(studentId);
            String studentName = getStudentNameById(studentId);

            JDialog dialog = new JDialog(this, "Attendance Details - " + studentName, true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);

            // Create table for student attendance
            DefaultTableModel attendanceModel = new DefaultTableModel(
                    new String[]{"Date", "Class", "Status", "Time", "Notes"}, 0
            );

            for (Attendance att : studentAttendance) {
                attendanceModel.addRow(new Object[]{
                    att.getDate().toString(),
                    att.getClassId(),
                    att.getStatus(),
                    att.getTimeMarked().toString()
                });
            }

            JTable attendanceTable = new JTable(attendanceModel);
            styleTable(attendanceTable);

            dialog.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

            JButton closeButton = createStyledButton("Close", new Color(108, 117, 125));
            closeButton.addActionListener(e -> dialog.dispose());
            dialog.add(closeButton, BorderLayout.SOUTH);

            dialog.setVisible(true);

        } catch (Exception e) {
            showError("Failed to show student attendance details: " + e.getMessage());
        }
    }

    private void showTutorAttendanceDetails(String tutorId) {
        try {
            List<Attendance> tutorAttendance = function.getAttendanceForTutor(tutorId);
            String tutorName = getTutorNameById(tutorId);

            JDialog dialog = new JDialog(this, "Classes Marked by " + tutorName, true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setSize(700, 400);
            dialog.setLocationRelativeTo(this);

            // Create table for tutor's attendance marking
            DefaultTableModel attendanceModel = new DefaultTableModel(
                    new String[]{"Date", "Student", "Class", "Status", "Time", "Notes"}, 0
            );

            for (Attendance att : tutorAttendance) {
                String studentName = getStudentNameById(att.getStudentId());
                attendanceModel.addRow(new Object[]{
                    att.getDate().toString(),
                    studentName,
                    att.getClassId(),
                    att.getStatus(),
                    att.getTimeMarked().toString()
                });
            }

            JTable attendanceTable = new JTable(attendanceModel);
            styleTable(attendanceTable);

            dialog.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

            JButton closeButton = createStyledButton("Close", new Color(108, 117, 125));
            closeButton.addActionListener(e -> dialog.dispose());
            dialog.add(closeButton, BorderLayout.SOUTH);

            dialog.setVisible(true);

        } catch (Exception e) {
            showError("Failed to show tutor attendance details: " + e.getMessage());
        }
    }

    private String generateAttendanceTrendsReport() {
        StringBuilder report = new StringBuilder();
        report.append("ATTENDANCE TRENDS ANALYSIS\n");
        report.append("=========================\n\n");

        try {
            List<Attendance> allAttendance = function.readAttendance().stream()
                .filter(line -> !line.trim().isEmpty())
                .map(Attendance::fromCsvString)
                .filter(att -> att != null)
                .collect(java.util.stream.Collectors.toList());

            if (allAttendance.isEmpty()) {
                report.append("No attendance data available for analysis.\n");
                return report.toString();
            }

            // Overall statistics
            int totalRecords = allAttendance.size();
            long presentCount = allAttendance.stream().filter(Attendance::isPresent).count();
            long absentCount = allAttendance.stream().filter(Attendance::isAbsent).count();
            long lateCount = allAttendance.stream().filter(Attendance::isLate).count();

            report.append("OVERALL STATISTICS:\n");
            report.append(String.format("Total Records: %d\n", totalRecords));
            report.append(String.format("Present: %d (%.1f%%)\n", presentCount, (double)presentCount/totalRecords*100));
            report.append(String.format("Absent: %d (%.1f%%)\n", absentCount, (double)absentCount/totalRecords*100));
            report.append(String.format("Late: %d (%.1f%%)\n", lateCount, (double)lateCount/totalRecords*100));
            report.append("\n");

            // Trends by class
            Map<String, Long> classPresentCount = allAttendance.stream()
                .filter(Attendance::isPresent)
                .collect(java.util.stream.Collectors.groupingBy(
                    Attendance::getClassId, 
                    java.util.stream.Collectors.counting()
                ));

            report.append("ATTENDANCE BY CLASS:\n");
            classPresentCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> report.append(String.format("%s: %d present\n", entry.getKey(), entry.getValue())));
            report.append("\n");

            // Recent trends (last 7 days)
            java.time.LocalDate sevenDaysAgo = java.time.LocalDate.now().minusDays(7);
            long recentRecords = allAttendance.stream()
                .filter(att -> att.getDate().isAfter(sevenDaysAgo))
                .count();

            report.append("RECENT TRENDS (Last 7 days):\n");
            report.append(String.format("Records in last 7 days: %d\n", recentRecords));

        } catch (Exception e) {
            report.append("Error generating trends analysis: ").append(e.getMessage());
        }

        return report.toString();
    }

    // ==================== Messaging Panel Creation Methods ====================
    
    private JPanel createAdminMessagingPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setOpaque(true);

        JLabel messagingTitle = new JLabel("💬 Messages & Communication Center");
        messagingTitle.setFont(HEADER_FONT);
        messagingTitle.setForeground(HEADER_COLOR);

        // Create tabbed pane for Inbox, Compose, and Announcements
        JTabbedPane messagesTabbedPane = new JTabbedPane();
        messagesTabbedPane.setFont(NORMAL_FONT);

        // Inbox Tab
        JPanel inboxPanel = new JPanel(new BorderLayout(10, 10));
        inboxPanel.setBackground(BACKGROUND_COLOR);

        adminMessagesTableModel = new DefaultTableModel(
                new String[]{"From", "Date/Time", "Priority", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        adminMessagesTable = new JTable(adminMessagesTableModel);
        styleTable(adminMessagesTable);
        adminMessagesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = adminMessagesTable.getSelectedRow();
                    if (row >= 0) {
                        showAdminMessageDialog(row);
                    }
                }
            }
        });

        JScrollPane messagesScrollPane = new JScrollPane(adminMessagesTable);
        inboxPanel.add(messagesScrollPane, BorderLayout.CENTER);

        // Buttons for inbox
        JPanel inboxButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inboxButtonPanel.setBackground(BACKGROUND_COLOR);
        JButton refreshMessagesButton = createStyledButton("🔄 Refresh", new Color(108, 117, 125));
        refreshMessagesButton.addActionListener(e -> loadAdminMessagesData());
        inboxButtonPanel.add(refreshMessagesButton);

        inboxPanel.add(inboxButtonPanel, BorderLayout.SOUTH);

        // Compose Tab
        JPanel composePanel = createAdminComposeMessagePanel();

        // Announcements Tab
        JPanel announcementsPanel = createAdminAnnouncementsPanel();

        messagesTabbedPane.addTab("📥 Inbox", inboxPanel);
        messagesTabbedPane.addTab("✉️ Compose", composePanel);
        messagesTabbedPane.addTab("📢 Announcements", announcementsPanel);

        panel.add(messagingTitle, BorderLayout.NORTH);
        panel.add(messagesTabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAdminComposeMessagePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // To field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(LABEL_FONT);
        panel.add(toLabel, gbc);
        JComboBox<String> toComboBox = new JComboBox<>();
        loadRecipientsForAdmin(toComboBox);
        toComboBox.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        panel.add(toComboBox, gbc);

        // Priority field
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel priorityLabel = new JLabel("Priority:");
        priorityLabel.setFont(LABEL_FONT);
        panel.add(priorityLabel, gbc);
        JComboBox<String> priorityComboBox = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        priorityComboBox.setPreferredSize(new Dimension(300, 35));
        priorityComboBox.setSelectedItem("Medium");
        gbc.gridx = 1;
        panel.add(priorityComboBox, gbc);

        // Message content area
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setFont(LABEL_FONT);
        panel.add(messageLabel, gbc);
        JTextArea messageArea = new JTextArea(10, 25);
        messageArea.setFont(NORMAL_FONT);
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
        JButton sendButton = createStyledButton("📤 Send Message", BUTTON_SUCCESS);
        sendButton.addActionListener(e -> {
            String to = (String) toComboBox.getSelectedItem();
            String priority = (String) priorityComboBox.getSelectedItem();
            String content = messageArea.getText().trim();

            if (to == null || content.isEmpty()) {
                showError("Please fill in all required fields");
                return;
            }

            sendAdminMessage(to, "Message", content, priority);
            messageArea.setText("");
            toComboBox.setSelectedIndex(0);
            priorityComboBox.setSelectedItem("Medium");
        });
        panel.add(sendButton, gbc);

        return panel;
    }

    private JPanel createAdminAnnouncementsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_COLOR);

        JLabel announcementTitle = new JLabel("📢 Create System Announcement");
        announcementTitle.setFont(LABEL_FONT);

        // Announcement form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Target audience
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel audienceLabel = new JLabel("Target Audience:");
        audienceLabel.setFont(LABEL_FONT);
        formPanel.add(audienceLabel, gbc);
        JComboBox<String> audienceComboBox = new JComboBox<>(new String[]{
            "ALL - All Users", "STUDENTS - All Students", "TUTORS - All Tutors", "RECEPTIONISTS - All Receptionists"
        });
        audienceComboBox.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        formPanel.add(audienceComboBox, gbc);

        // Content
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel contentLabel = new JLabel("Announcement:");
        contentLabel.setFont(LABEL_FONT);
        formPanel.add(contentLabel, gbc);
        JTextArea announcementArea = new JTextArea(8, 25);
        announcementArea.setFont(NORMAL_FONT);
        announcementArea.setLineWrap(true);
        announcementArea.setWrapStyleWord(true);
        JScrollPane announcementScrollPane = new JScrollPane(announcementArea);
        announcementScrollPane.setPreferredSize(new Dimension(300, 150));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(announcementScrollPane, gbc);

        // Send announcement button
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        JButton sendAnnouncementButton = createStyledButton("📢 Send Announcement", BUTTON_PRIMARY);
        sendAnnouncementButton.addActionListener(e -> {
            String audience = ((String) audienceComboBox.getSelectedItem()).split(" - ")[0];
            String content = announcementArea.getText().trim();

            if (content.isEmpty()) {
                showError("Please fill in announcement content");
                return;
            }

            sendAnnouncement(audience, "System Announcement", content);
            announcementArea.setText("");
        });
        formPanel.add(sendAnnouncementButton, gbc);

        panel.add(announcementTitle, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    // Helper methods for admin messaging
    private void loadAdminMessagesData() {
        adminMessagesTableModel.setRowCount(0);
        try {
            String adminId = getAdminUserId();
            List<Message> messages = function.getMessagesForUser(adminId);
            for (Message msg : messages) {
                String senderName = getUserNameFromId(msg.getSenderId());
                adminMessagesTableModel.addRow(new Object[]{
                    senderName,
                    msg.getDateTime().toString(),
                    msg.getPriority(),
                    msg.getStatus()
                });
            }
        } catch (Exception e) {
            showError("Failed to load messages: " + e.getMessage());
        }
    }

    private void loadRecipientsForAdmin(JComboBox<String> toComboBox) {
        toComboBox.removeAllItems();
        try {
            List<String> userLines = function.readUsers();
            String adminId = getAdminUserId();
            for (String line : userLines) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && !parts[0].trim().equals(adminId)) {
                    String userId = parts[0].trim();
                    String name = parts[4].trim();
                    String role = parts[3].trim();
                    toComboBox.addItem(userId + " - " + name + " (" + role + ")");
                }
            }
            // Add group options
            toComboBox.addItem("ALL - All Users");
            toComboBox.addItem("STUDENTS - All Students");
            toComboBox.addItem("TUTORS - All Tutors");
            toComboBox.addItem("RECEPTIONISTS - All Receptionists");
        } catch (Exception e) {
            showError("Failed to load recipients: " + e.getMessage());
        }
    }

    private String getAdminUserId() {
        try {
            List<String> userLines = function.readUsers();
            for (String line : userLines) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && "admin".equals(parts[3].trim()) && 
                    parts[4].trim().equals(adminName)) {
                    return parts[0].trim();
                }
            }
        } catch (Exception e) {
            // Return default if can't find
        }
        return "A001"; // Default admin ID
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

    private void sendAdminMessage(String to, String subject, String content, String priority) {
        try {
            String receiverId = to.split(" - ")[0];
            String adminId = getAdminUserId();
            
            Message message = new Message(
                function.generateMessageId(),
                adminId,
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
            JOptionPane.showMessageDialog(this, "Message sent successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAdminMessagesData(); // Refresh messages
        } catch (Exception e) {
            showError("Failed to send message: " + e.getMessage());
        }
    }

    private void sendAnnouncement(String audience, String subject, String content) {
        try {
            String adminId = getAdminUserId();
            
            Message announcement = new Message(
                function.generateMessageId(),
                adminId,
                audience,
                "Announcement",
                subject,
                content,
                java.time.LocalDateTime.now(),
                "Sent",
                "High",
                ""
            );

            function.addMessage(announcement.toCsvString());
            JOptionPane.showMessageDialog(this, "Announcement sent successfully to " + audience + "!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAdminMessagesData(); // Refresh messages
        } catch (Exception e) {
            showError("Failed to send announcement: " + e.getMessage());
        }
    }

    private void showAdminMessageDialog(int row) {
        try {
            String from = (String) adminMessagesTableModel.getValueAt(row, 0);
            String dateTime = (String) adminMessagesTableModel.getValueAt(row, 1);
            String priority = (String) adminMessagesTableModel.getValueAt(row, 2);

            String adminId = getAdminUserId();
            List<Message> messages = function.getMessagesForUser(adminId);
            Message selectedMessage = null;
            for (Message msg : messages) {
                if (msg.getDateTime().toString().equals(dateTime)) {
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
                headerPanel.add(new JLabel(dateTime));
                headerPanel.add(new JLabel("Priority:"));
                headerPanel.add(new JLabel(priority));

                // Content area
                JTextArea contentArea = new JTextArea(selectedMessage.getContent());
                contentArea.setFont(NORMAL_FONT);
                contentArea.setEditable(false);
                contentArea.setLineWrap(true);
                contentArea.setWrapStyleWord(true);
                JScrollPane contentScrollPane = new JScrollPane(contentArea);
                contentScrollPane.setBorder(BorderFactory.createTitledBorder("Message Content"));

                // Close button
                JButton closeButton = createStyledButton("Close", new Color(108, 117, 125));
                closeButton.addActionListener(e -> dialog.dispose());

                dialog.add(headerPanel, BorderLayout.NORTH);
                dialog.add(contentScrollPane, BorderLayout.CENTER);
                dialog.add(closeButton, BorderLayout.SOUTH);

                dialog.setVisible(true);
            }
        } catch (Exception e) {
            showError("Failed to display message: " + e.getMessage());
        }
    }

    // ==================== Panel Creation Methods ====================
    private JPanel createStaffPanel() {
        staffPanel = new JPanel(new BorderLayout(15, 15));
        staffPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        staffPanel.setBackground(BACKGROUND_COLOR);
        staffPanel.setOpaque(true);

        // Title
        JLabel titleLabel = new JLabel("👥 Staff Management");
        titleLabel.setFont(LABEL_FONT);
        titleLabel.setForeground(HEADER_COLOR);

        // Create panels
        JPanel tutorsPanel = createTutorsPanel();
        JPanel receptionistsPanel = createReceptionistsPanel();

        // Main layout
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.add(tutorsPanel);
        contentPanel.add(receptionistsPanel);

        staffPanel.add(titleLabel, BorderLayout.NORTH);
        staffPanel.add(contentPanel, BorderLayout.CENTER);

        return staffPanel;
    }

    private JPanel createTutorsPanel() {
        JPanel tutorsPanel = new JPanel(new BorderLayout(10, 10));
        tutorsPanel.setBackground(BACKGROUND_COLOR);
        tutorsPanel.setOpaque(true);

        JLabel tutorsLabel = new JLabel("Tutors");
        tutorsLabel.setFont(LABEL_FONT);
        tutorsPanel.add(tutorsLabel, BorderLayout.NORTH);

        tutorsTable = new JTable(tutorsTableModel);
        styleTable(tutorsTable);
        JScrollPane tutorsScrollPane = new JScrollPane(tutorsTable);
        tutorsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tutorsPanel.add(tutorsScrollPane, BorderLayout.CENTER);

        JPanel tutorButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tutorButtonsPanel.setBackground(BACKGROUND_COLOR);
        registerTutorButton = createStyledButton("➕ Register Tutor", BUTTON_SUCCESS);
        deleteTutorButton = createStyledButton("🗑️ Delete Tutor", BUTTON_DANGER);
        tutorButtonsPanel.add(registerTutorButton);
        tutorButtonsPanel.add(deleteTutorButton);
        tutorsPanel.add(tutorButtonsPanel, BorderLayout.SOUTH);

        return tutorsPanel;
    }

    private JPanel createReceptionistsPanel() {
        JPanel receptionistsPanel = new JPanel(new BorderLayout(10, 10));
        receptionistsPanel.setBackground(BACKGROUND_COLOR);
        receptionistsPanel.setOpaque(true);

        JLabel receptionistsLabel = new JLabel("Receptionists");
        receptionistsLabel.setFont(LABEL_FONT);
        receptionistsPanel.add(receptionistsLabel, BorderLayout.NORTH);

        receptionistsTable = new JTable(receptionistsTableModel);
        styleTable(receptionistsTable);
        JScrollPane receptionistsScrollPane = new JScrollPane(receptionistsTable);
        receptionistsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        receptionistsPanel.add(receptionistsScrollPane, BorderLayout.CENTER);

        JPanel receptionistButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        receptionistButtonsPanel.setBackground(BACKGROUND_COLOR);
        registerReceptionistButton = createStyledButton("➕ Register Receptionist", BUTTON_SUCCESS);
        deleteReceptionistButton = createStyledButton("🗑️ Delete Receptionist", BUTTON_DANGER);
        receptionistButtonsPanel.add(registerReceptionistButton);
        receptionistButtonsPanel.add(deleteReceptionistButton);
        receptionistsPanel.add(receptionistButtonsPanel, BorderLayout.SOUTH);

        return receptionistsPanel;
    }

    private JPanel createSubjectsPanel() {
        subjectsPanel = new JPanel(new BorderLayout());
        subjectsPanel.setBorder(BorderFactory.createTitledBorder("Subject Management"));
        subjectsPanel.setBackground(BACKGROUND_COLOR);
        subjectsPanel.setOpaque(true);

        // Subjects Table
        subjectsTable = new JTable(subjectsTableModel);
        styleTable(subjectsTable);
        JScrollPane subjectsScrollPane = new JScrollPane(subjectsTable);
        subjectsScrollPane.setPreferredSize(new Dimension(600, 200));

        // Assign Tutor Button
        assignTutorButton = createStyledButton("Assign Tutor", BUTTON_PRIMARY);
        assignTutorButton.addActionListener(e -> showAssignTutorDialog());

        subjectsPanel.add(subjectsScrollPane, BorderLayout.NORTH);
        subjectsPanel.add(assignTutorButton, BorderLayout.SOUTH);

        return subjectsPanel;
    }

    private JPanel createReportsPanel() {
        reportsPanel = new JPanel(new BorderLayout());
        reportsPanel.setBorder(BorderFactory.createTitledBorder("Reports"));
        reportsPanel.setBackground(BACKGROUND_COLOR);
        reportsPanel.setOpaque(true);

        // Create main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Reports title
        JLabel reportsTitle = new JLabel("📊 System Reports");
        reportsTitle.setFont(HEADER_FONT);
        reportsTitle.setForeground(HEADER_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        contentPanel.add(reportsTitle, gbc);

        // Financial Reports Section
        JLabel financialLabel = new JLabel("💰 Financial Reports");
        financialLabel.setFont(LABEL_FONT);
        financialLabel.setForeground(new Color(0x343A40));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        contentPanel.add(financialLabel, gbc);

        // Income Report Button
        viewIncomeReportButton = createStyledButton("📈 View Monthly Income Report", BUTTON_PRIMARY);
        viewIncomeReportButton.addActionListener(e -> showIncomeReportDialog());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        contentPanel.add(viewIncomeReportButton, gbc);

        // Attendance Reports Section
        JLabel attendanceLabel = new JLabel("✅ Attendance Reports");
        attendanceLabel.setFont(LABEL_FONT);
        attendanceLabel.setForeground(new Color(0x343A40));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        contentPanel.add(attendanceLabel, gbc);

        // Attendance Report Buttons
        JButton viewAttendanceReportButton = createStyledButton("📊 View Attendance Summary", BUTTON_SUCCESS);
        viewAttendanceReportButton.addActionListener(e -> showAttendanceReportDialog());
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        contentPanel.add(viewAttendanceReportButton, gbc);

        JButton studentAttendanceButton = createStyledButton("👥 Student Attendance Details", new Color(108, 117, 125));
        studentAttendanceButton.addActionListener(e -> showStudentAttendanceDialog());
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 1;
        contentPanel.add(studentAttendanceButton, gbc);

        JButton tutorAttendanceButton = createStyledButton("👨‍🏫 Tutor Attendance Report", new Color(255, 193, 7));
        tutorAttendanceButton.addActionListener(e -> showTutorAttendanceDialog());
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        contentPanel.add(tutorAttendanceButton, gbc);

        JButton attendanceTrendsButton = createStyledButton("📈 Attendance Trends", new Color(23, 162, 184));
        attendanceTrendsButton.addActionListener(e -> showAttendanceTrendsDialog());
        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 1;
        contentPanel.add(attendanceTrendsButton, gbc);

        reportsPanel.add(contentPanel, BorderLayout.CENTER);

        return reportsPanel;
    }

    private JPanel createProfilePanel() {
        profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(BACKGROUND_COLOR);
        profilePanel.setOpaque(true);

        // Create a sub-panel for the form with some padding
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Profile Settings");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(HEADER_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        // Name Field
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel nameLabel = new JLabel("👤 Name:");
        nameLabel.setFont(LABEL_FONT);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        profileNameField = createStyledTextField(adminName);
        profileNameField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(profileNameField, gbc);

        // Username Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel usernameLabel = new JLabel("👤 Username:");
        usernameLabel.setFont(LABEL_FONT);
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        profileUsernameField = createStyledTextField(adminUsername);
        profileUsernameField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(profileUsernameField, gbc);

        // Password Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel passwordLabel = new JLabel("🔑 New Password:");
        passwordLabel.setFont(LABEL_FONT);
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        profilePasswordField = createStyledPasswordField();
        profilePasswordField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(profilePasswordField, gbc);

        // Update Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        updateProfileButton = createStyledButton("Update Profile", BUTTON_PRIMARY);
        updateProfileButton.addActionListener(e -> {
            String name = profileNameField.getText();
            String username = profileUsernameField.getText();
            String password = new String(profilePasswordField.getPassword());

            // Only update if at least one field is filled
            if (name.isEmpty() && username.isEmpty() && password.isEmpty()) {
                showError("At least one field must be filled!");
                return;
            }

            // Pass empty strings as null to indicate no change
            updateProfile(
                name.isEmpty() ? null : name,
                username.isEmpty() ? null : username,
                password.isEmpty() ? null : password
            );
        });
        formPanel.add(updateProfileButton, gbc);

        // Add form panel to center of main panel
        profilePanel.add(formPanel);

        return profilePanel;
    }

    // ==================== Utility Methods ====================
    private Color darken(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0, hsb[2] - 0.1f));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== Table Data Loading Methods ====================
    private void loadTutorsData() {
        tutorsTableModel.setRowCount(0);
        List<String> tutorLines = function.readTutors();
        
        for (String line : tutorLines) {
            String[] parts = line.split(",");
            if (parts.length >= 7) {
                Vector<String> row = new Vector<>();
                row.add(parts[0].trim()); // ID
                row.add(parts[1].trim()); // Name
                row.add(parts[2].trim()); // IC/Passport
                row.add(parts[3].trim()); // Email
                row.add(parts[4].trim()); // Contact
                row.add(parts[6].trim()); // Status
                tutorsTableModel.addRow(row);
            }
        }
    }

    private void loadReceptionistsData() {
        receptionistsTableModel.setRowCount(0);
        List<String> userLines = function.readUsers();
        
        for (String line : userLines) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[3].trim().equals("receptionist")) {
                Vector<String> row = new Vector<>();
                row.add(parts[0].trim()); // ID
                row.add(parts[4].trim()); // Name
                row.add(parts[1].trim()); // Username
                row.add("-"); // Contact (placeholder)
                receptionistsTableModel.addRow(row);
            }
        }
    }

    private void loadSubjectsData() {
        subjectsTableModel.setRowCount(0);
        List<String> subjectLines = function.readSubjects();
        
        for (String line : subjectLines) {
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                Vector<String> row = new Vector<>();
                row.add(parts[0].trim()); // Subject ID
                row.add(parts[1].trim()); // Subject name
                row.add(parts[2].trim()); // Level
                
                // Add tutor name if assigned
                String tutorId = parts.length > 3 ? parts[3].trim() : "-";
                String tutorName = findTutorName(tutorId);
                row.add(tutorName);
                
                subjectsTableModel.addRow(row);
            }
        }
    }

    private String findTutorName(String tutorId) {
        if (tutorId.equals("-") || tutorId.isEmpty()) {
            return "Not Assigned";
        }
        
        List<String> tutorLines = function.readTutors();
        for (String line : tutorLines) {
            String[] parts = line.split(",");
            if (parts.length >= 2 && parts[0].trim().equals(tutorId)) {
                return parts[1].trim();
            }
        }
        return "Empty";
    }

    private void loadProfileData() {
        profileNameField.setText(adminName);
        profilePasswordField.setText("");
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                admin_dashboard frame = new admin_dashboard("Default Admin");
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}