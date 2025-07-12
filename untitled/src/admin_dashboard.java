import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class admin_dashboard extends JFrame {
    private JPanel mainPanel;
    private JButton registerTutorButton;
    private JButton deleteTutorButton;
    private JButton assignTutorButton;
    private JButton registerReceptionistButton;
    private JButton deleteReceptionistButton;
    private JButton viewIncomeReportButton;
    private JButton updateProfileButton;
    private JButton logoutButton;
    private JLabel welcomeLabel;

    private String adminName;
    private String adminUsername;
    private String adminEmail;
    private String adminContactNumber;
    private String adminPassword;

    // In-memory storage for users and payments
    private static List<User> users = new ArrayList<>();
    private static List<Subject> subjects = new ArrayList<>();
    private static List<Payment> payments = new ArrayList<>();

    // Load users from file on class initialization
    static {
        loadUsersFromFile();
    }

    public admin_dashboard(String name) {
        this.adminName = name;

        // Initialize UI components
        initComponents();

        // Find the admin user
        for (User user : users) {
            if (user.getRole().equals("admin") && user.getName().equals(name)) {
                this.adminUsername = user.getUsername();
                this.adminEmail = user.getEmail();
                this.adminContactNumber = user.getContactNumber();
                this.adminPassword = user.getPassword();
                break;
            }
        }

        // If admin not found, use default values
        if (adminUsername == null) {
            this.adminUsername = "admin";
            this.adminEmail = "admin@tuition.com";
            this.adminContactNumber = "123456789";
            this.adminPassword = "admin123";
        }

        // Set welcome message
        welcomeLabel.setText("Welcome, " + name + " (Admin)");

        // Set up action listeners
        setupActionListeners();
    }

    // Method to load users from file
    private static void loadUsersFromFile() {
        String currentDir = System.getProperty("user.dir");
        String userFilePath = currentDir + "/data/users.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(userFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    String role = parts[2].trim();
                    String name = parts[3].trim();
                    String studentId = parts.length > 4 ? parts[4].trim() : null;

                    // Create appropriate user object based on role
                    User user = null;
                    switch (role.toLowerCase()) {
                        case "admin":
                            user = new User(username, password, name, "", "", role, studentId) {};
                            break;
                        case "receptionist":
                            user = new Receptionist(username, password, name, "", "", "");
                            if (studentId != null && !studentId.isEmpty()) {
                                user.setStudentId(studentId);
                            }
                            break;
                        case "tutor":
                            user = new Tutor(username, password, name, "", "");
                            if (studentId != null && !studentId.isEmpty()) {
                                user.setStudentId(studentId);
                            }
                            break;
                        case "student":
                            user = new User(username, password, name, "", "", role, studentId) {};
                            break;
                    }

                    if (user != null) {
                        users.add(user);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users from file: " + e.getMessage());
            // Create default admin if file doesn't exist or can't be read
            User defaultAdmin = new User("admin", "admin123", "Elson", "admin@tuition.com", "123456789", "admin") {};
            users.add(defaultAdmin);
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
    public void registerTutor(String username, String password, String name, String email, String contactNumber) {
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose another username.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Tutor newTutor = new Tutor(username, password, name, email, contactNumber);
        users.add(newTutor);
        JOptionPane.showMessageDialog(this, "Tutor registered successfully: " + name);
    }

    public void deleteTutor(String username) {
        User tutorToRemove = null;

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getRole().equals("tutor")) {
                tutorToRemove = user;
                break;
            }
        }

        if (tutorToRemove != null) {
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
            }

            JOptionPane.showMessageDialog(this, "Tutor deleted successfully: " + tutorToRemove.getName());
        } else {
            JOptionPane.showMessageDialog(this, "Tutor not found with username: " + username,
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void assignTutorToSubject(String tutorUsername, String subjectName, String level, double fee) {
        // Find the tutor
        Tutor tutor = null;
        for (User user : users) {
            if (user.getUsername().equals(tutorUsername) && user.getRole().equals("tutor")) {
                tutor = (Tutor) user;
                break;
            }
        }

        if (tutor == null) {
            JOptionPane.showMessageDialog(this, "Tutor not found with username: " + tutorUsername,
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if subject exists
        Subject targetSubject = null;
        for (Subject subject : subjects) {
            if (subject.getName().equals(subjectName) && subject.getLevel().equals(level)) {
                targetSubject = subject;
                break;
            }
        }

        // Create subject if it doesn't exist
        if (targetSubject == null) {
            targetSubject = new Subject(subjectName, level, fee, tutorUsername);
            subjects.add(targetSubject);
        } else {
            targetSubject.setTutorUsername(tutorUsername);
        }

        // Add subject and level to tutor's list
        tutor.addSubject(subjectName);
        tutor.addLevel(level);

        JOptionPane.showMessageDialog(this, "Tutor " + tutor.getName() + " assigned to " + subjectName + " (" + level + ")");
    }

    // Methods for managing receptionists
    public void registerReceptionist(String username, String password, String name, String email, String contactNumber, String employeeId) {
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose another username.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Receptionist newReceptionist = new Receptionist(username, password, name, email, contactNumber, employeeId);
        users.add(newReceptionist);
        JOptionPane.showMessageDialog(this, "Receptionist registered successfully: " + name);
    }

    public void deleteReceptionist(String username) {
        User receptionistToRemove = null;

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getRole().equals("receptionist")) {
                receptionistToRemove = user;
                break;
            }
        }

        if (receptionistToRemove != null) {
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
        Map<String, Double> incomeBySubject = new HashMap<>();
        Map<String, Double> incomeByLevel = new HashMap<>();

        for (Payment payment : payments) {
            if (payment.getMonth().equals(month + " " + year)) {
                totalIncome += payment.getAmount();

                // Aggregate by subject
                String subject = payment.getSubject();
                incomeBySubject.put(subject, incomeBySubject.getOrDefault(subject, 0.0) + payment.getAmount());

                // Aggregate by level
                String level = payment.getLevel();
                incomeByLevel.put(level, incomeByLevel.getOrDefault(level, 0.0) + payment.getAmount());
            }
        }

        // Generate report as string
        StringBuilder report = new StringBuilder();
        report.append("===== MONTHLY INCOME REPORT =====\n");
        report.append("Month: ").append(month).append(" ").append(year).append("\n");
        report.append("Total Income: $").append(totalIncome).append("\n\n");

        report.append("--- Income by Subject ---\n");
        for (Map.Entry<String, Double> entry : incomeBySubject.entrySet()) {
            report.append(entry.getKey()).append(": $").append(entry.getValue()).append("\n");
        }

        report.append("\n--- Income by Level ---\n");
        for (Map.Entry<String, Double> entry : incomeByLevel.entrySet()) {
            report.append(entry.getKey()).append(": $").append(entry.getValue()).append("\n");
        }
        report.append("================================");

        return report.toString();
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

    // Helper method to add a test payment (for demonstration)
    public static void addTestPayment(String studentUsername, String subject, String level,
                                     double amount, String month, int year) {
        String receiptNumber = "R" + payments.size() + 1;
        Payment payment = new Payment(studentUsername, subject, level, amount,
                                     LocalDate.now(), month + " " + year, receiptNumber);
        payments.add(payment);
    }

    // Getter for users list (for testing)
    public static List<User> getUsers() {
        return users;
    }

    // Method to update profile
    public void updateProfile(String name, String email, String contactNumber, String password) {
        // Find the current admin user
        for (User user : users) {
            if (user.getRole().equals("admin") && user.getName().equals(adminName)) {
                if (name != null && !name.isEmpty()) {
                    user.setName(name);
                    this.adminName = name;
                }
                if (email != null && !email.isEmpty()) {
                    user.setEmail(email);
                    this.adminEmail = email;
                }
                if (contactNumber != null && !contactNumber.isEmpty()) {
                    user.setContactNumber(contactNumber);
                    this.adminContactNumber = contactNumber;
                }
                if (password != null && !password.isEmpty()) {
                    user.setPassword(password);
                    this.adminPassword = password;
                }
                break;
            }
        }
    }

    private void initComponents() {
        setTitle("Admin Dashboard");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Create welcome panel
        JPanel welcomePanel = new JPanel();
        welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomePanel.add(welcomeLabel);

        // Create buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(8, 1, 10, 10));

        registerTutorButton = new JButton("Register Tutor");
        deleteTutorButton = new JButton("Delete Tutor");
        assignTutorButton = new JButton("Assign Tutor to Subject");
        registerReceptionistButton = new JButton("Register Receptionist");
        deleteReceptionistButton = new JButton("Delete Receptionist");
        viewIncomeReportButton = new JButton("View Monthly Income Report");
        updateProfileButton = new JButton("Update Profile");
        logoutButton = new JButton("Logout");

        buttonsPanel.add(registerTutorButton);
        buttonsPanel.add(deleteTutorButton);
        buttonsPanel.add(assignTutorButton);
        buttonsPanel.add(registerReceptionistButton);
        buttonsPanel.add(deleteReceptionistButton);
        buttonsPanel.add(viewIncomeReportButton);
        buttonsPanel.add(updateProfileButton);
        buttonsPanel.add(logoutButton);

        // Add panels to main panel
        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(buttonsPanel, BorderLayout.CENTER);

        // Add padding
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        setContentPane(mainPanel);
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
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField contactField = new JTextField();

        Object[] message = {
            "Username:", usernameField,
            "Password:", passwordField,
            "Name:", nameField,
            "Email:", emailField,
            "Contact Number:", contactField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Register Tutor", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText();
            String email = emailField.getText();
            String contact = contactField.getText();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            registerTutor(username, password, name, email, contact);
        }
    }

    private void showDeleteTutorDialog() {
        // Get all tutors
        List<Tutor> tutors = getAllTutors();
        if (tutors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tutors found in the system.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultComboBoxModel<String> tutorModel = new DefaultComboBoxModel<>();
        for (Tutor tutor : tutors) {
            tutorModel.addElement(tutor.getUsername() + " - " + tutor.getName());
        }

        JComboBox<String> tutorComboBox = new JComboBox<>(tutorModel);

        Object[] message = {
            "Select tutor to delete:", tutorComboBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Delete Tutor", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String selected = (String) tutorComboBox.getSelectedItem();
            String username = selected.split(" - ")[0];

            deleteTutor(username);
        }
    }

    private void showAssignTutorDialog() {
        // Get all tutors
        List<Tutor> tutors = getAllTutors();
        if (tutors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tutors found in the system.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultComboBoxModel<String> tutorModel = new DefaultComboBoxModel<>();
        for (Tutor tutor : tutors) {
            tutorModel.addElement(tutor.getUsername() + " - " + tutor.getName());
        }

        JComboBox<String> tutorComboBox = new JComboBox<>(tutorModel);
        JTextField subjectField = new JTextField();
        JTextField levelField = new JTextField();
        JTextField feeField = new JTextField();

        Object[] message = {
            "Select tutor:", tutorComboBox,
            "Subject Name:", subjectField,
            "Level (e.g., Primary 6):", levelField,
            "Monthly Fee:", feeField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Assign Tutor to Subject", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String selected = (String) tutorComboBox.getSelectedItem();
            String username = selected.split(" - ")[0];
            String subject = subjectField.getText();
            String level = levelField.getText();
            String feeStr = feeField.getText();

            if (subject.isEmpty() || level.isEmpty() || feeStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double fee = Double.parseDouble(feeStr);
                assignTutorToSubject(username, subject, level, fee);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid fee amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRegisterReceptionistDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField employeeIdField = new JTextField();

        Object[] message = {
            "Username:", usernameField,
            "Password:", passwordField,
            "Name:", nameField,
            "Email:", emailField,
            "Contact Number:", contactField,
            "Employee ID:", employeeIdField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Register Receptionist", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText();
            String email = emailField.getText();
            String contact = contactField.getText();
            String employeeId = employeeIdField.getText();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty() || contact.isEmpty() || employeeId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            registerReceptionist(username, password, name, email, contact, employeeId);
        }
    }

    private void showDeleteReceptionistDialog() {
        // Get all receptionists
        List<Receptionist> receptionists = getAllReceptionists();
        if (receptionists.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No receptionists found in the system.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultComboBoxModel<String> recModel = new DefaultComboBoxModel<>();
        for (Receptionist receptionist : receptionists) {
            recModel.addElement(receptionist.getUsername() + " - " + receptionist.getName());
        }

        JComboBox<String> recComboBox = new JComboBox<>(recModel);

        Object[] message = {
            "Select receptionist to delete:", recComboBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Delete Receptionist", JOptionPane.OK_CANCEL_OPTION);

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
        JTextField yearField = new JTextField(String.valueOf(LocalDate.now().getYear()));

        Object[] message = {
            "Select month:", monthComboBox,
            "Year:", yearField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "View Income Report", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String month = (String) monthComboBox.getSelectedItem();
            String yearStr = yearField.getText();

            try {
                int year = Integer.parseInt(yearStr);

                // Add some test data if needed
                if (payments.isEmpty()) {
                    addTestPayment("student1", "Mathematics", "Primary 6", 150.0, month, year);
                    addTestPayment("student2", "Science", "Secondary 3", 180.0, month, year);
                    addTestPayment("student3", "English", "Primary 6", 150.0, month, year);
                    addTestPayment("student4", "Mathematics", "Secondary 3", 180.0, month, year);
                }

                // Generate the report
                String reportText = viewMonthlyIncomeReport(month, year);

                // Display report in a dialog
                JTextArea reportArea = new JTextArea(reportText);
                reportArea.setEditable(false);
                reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

                JScrollPane scrollPane = new JScrollPane(reportArea);
                scrollPane.setPreferredSize(new Dimension(500, 400));

                JOptionPane.showMessageDialog(this, scrollPane, "Monthly Income Report", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid year!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateProfileDialog() {
        JTextField nameField = new JTextField(adminName);
        JTextField emailField = new JTextField(adminEmail);
        JTextField contactField = new JTextField(adminContactNumber);
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
            "Name:", nameField,
            "Email:", emailField,
            "Contact Number:", contactField,
            "New Password (leave blank to keep current):", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Profile", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String email = emailField.getText();
            String contact = contactField.getText();
            String password = new String(passwordField.getPassword());

            if (password.isEmpty()) {
                password = null; // Don't change password if empty
            }

            updateProfile(name, email, contact, password);
            JOptionPane.showMessageDialog(this, "Profile updated successfully.");

            // Update welcome label
            welcomeLabel.setText("Welcome, " + adminName + " (Admin)");
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new main_page().setVisible(true);
        }
    }

    // Main method for testing the GUI
    public static void main(String[] args) {
        // Add some sample data
        if (users.size() <= 1) { // Only default admin exists
            User admin = users.get(0);

            // Create a tutor
            Tutor tutor = new Tutor("tutor1", "tutor123", "John Smith", "john@tuition.com", "987654321");
            users.add(tutor);

            // Create a receptionist
            Receptionist receptionist = new Receptionist("rec1", "rec123", "Jane Doe", "jane@tuition.com", "555123456", "REC001");
            users.add(receptionist);
        }

        // Show admin GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new admin_dashboard("Default Admin").setVisible(true);
            }
        });
    }
}