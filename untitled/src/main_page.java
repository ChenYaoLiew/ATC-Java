import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Vector;

public class main_page extends JFrame {
    private JPanel main;
    private JTextField username_field;
    private JPasswordField password_field;
    private JButton loginButton;

    // Store login attempts for non-admin users
    private static Map<String, Integer> loginAttempts = new HashMap<>();
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    
    // Define base directory for data files - using current directory
    private static final String DATA_DIR = System.getProperty("user.dir") + "/untitled/data";

    private void initializeComponents() {
        main = new JPanel();
        main.setLayout(new java.awt.GridBagLayout());
        
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);
        
        // Username label and field
        gbc.gridx = 0; gbc.gridy = 0;
        main.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        username_field = new JTextField(15);
        main.add(username_field, gbc);
        
        // Password label and field
        gbc.gridx = 0; gbc.gridy = 1;
        main.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        password_field = new JPasswordField(15);
        main.add(password_field, gbc);
        
        // Login button
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        loginButton = new JButton("Login");
        main.add(loginButton, gbc);
    }
    
    public main_page() {
        initializeComponents();
        setContentPane(main);
        setTitle("Tuition Center Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        
        // Ensure data directory and user file exist
        ensureDataFilesExist();
        
        // Add action listener to login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Allow Enter key to trigger login
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void performLogin() {
        String username = username_field.getText().trim();
        String password = new String(password_field.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if user is blocked (non-admin users only)
        if (!username.equals("admin") && isUserBlocked(username)) {
            JOptionPane.showMessageDialog(this, 
                "Account locked due to too many failed login attempts. Please contact administrator.", 
                "Account Locked", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Read users from file
        List<String> userLines = function.readUsers();
        User user = null;
        
        for (String line : userLines) {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                String id = parts[0].trim();
                String storedUsername = parts[1].trim();
                String storedPassword = parts[2].trim();
                String role = parts[3].trim();
                String name = parts[4].trim();
                
                if (storedUsername.equals(username) && storedPassword.equals(password)) {
                    switch (role.toLowerCase()) {
                        case "admin":
                            user = new User(username, password, name, "", "", role, id) {};
                            break;
                        case "receptionist":
                            user = new Receptionist(username, password, name, "", "", "");
                            user.setStudentId(id);
                            break;
                        case "tutor":
                            user = new Tutor(username, password, name, "", "");
                            user.setStudentId(id);
                            break;
                        case "student":
                            user = new User(username, password, name, "", "", role, id) {};
                            break;
                    }
                    break;
                }
            }
        }
        
        if (user != null) {
            // Reset login attempts on successful login
            loginAttempts.remove(username);
            
            JOptionPane.showMessageDialog(this, 
                "Login successful! Welcome " + user.getName() + " (" + user.getRole() + ")", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Navigate to appropriate dashboard based on role
            openDashboard(user);
            
        } else {
            // Increment login attempts for non-admin users
            if (!username.equals("admin")) {
                int attempts = loginAttempts.getOrDefault(username, 0) + 1;
                loginAttempts.put(username, attempts);
                
                int remainingAttempts = MAX_LOGIN_ATTEMPTS - attempts;
                if (remainingAttempts > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid username or password. " + remainingAttempts + " attempts remaining.", 
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Account locked due to too many failed login attempts. Please contact administrator.", 
                        "Account Locked", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", 
                                            "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
            
            // Clear password field
            password_field.setText("");
        }
    }
    
    private boolean isUserBlocked(String username) {
        return loginAttempts.getOrDefault(username, 0) >= MAX_LOGIN_ATTEMPTS;
    }
    
    private Tutor createTutorFromUser(User user) {
        String tutorId = user.getStudentId();
        String tutorName = user.getName();
        
        // Read tutor details from tutors.txt
        List<String> tutorLines = function.readTutors();
        for (String line : tutorLines) {
            String[] parts = line.split(",");
            if (parts.length >= 6 && parts[0].trim().equals(tutorId)) {
                // Create tutor with full details from tutors.txt
                Tutor tutor = new Tutor(
                    tutorId, // Use tutorId as username for filtering
                    user.getPassword(),
                    tutorName,
                    parts[3].trim(),  // email
                    parts[4].trim()   // contact
                );
                tutor.setStudentId(tutorId);
                // Load additional tutor data
                loadTutorData(tutor);
                return tutor;
            }
        }

        // Fallback to basic user info if tutor record not found
        Tutor tutor = new Tutor(
            tutorId, // Use tutorId as username for filtering
            user.getPassword(),
            user.getName(),
            user.getEmail() != null ? user.getEmail() : "",
            user.getContactNumber() != null ? user.getContactNumber() : ""
        );
        tutor.setStudentId(tutorId);
        loadTutorData(tutor);
        return tutor;
    }
    
    private void loadTutorData(Tutor tutor) {
        // Load subjects and levels from subject.txt
        List<String> subjectLines = function.readSubjects();
        boolean hasSubjects = false;
        
        for (String line : subjectLines) {
            String[] parts = line.split(",");
            if (parts.length >= 4 && parts[3].trim().equals(tutor.getStudentId())) {
                tutor.addSubject(parts[1].trim()); // Subject name
                tutor.addLevel(parts[2].trim());   // Level
                hasSubjects = true;
            }
        }
        
        // Add default subjects and levels if none found
        if (!hasSubjects) {
            tutor.addSubject("Mathematics");
            tutor.addSubject("Science");
            tutor.addLevel("Form 1");
            tutor.addLevel("Form 2");
        }
    }
    
    private void openDashboard(User user) {
        // Hide the login window
        this.setVisible(false);
        
        // Open appropriate dashboard based on user role
        switch (user.getRole()) {
            case "admin":
               new admin_dashboard(user.getName()).setVisible(true);
                break;
            case "receptionist":
                new receptionist_dashboard(user.getName()).setVisible(true);
                break;
            case "tutor":
                Tutor tutor = createTutorFromUser(user);
                new tutor_dashboard(tutor).setVisible(true);
                break;
            case "student":
                new student_dashboard(user.getUsername()).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this, 
                    "Invalid user role", "Error", JOptionPane.ERROR_MESSAGE);
                this.setVisible(true);
        }
        
        // Dispose of the login window
        this.dispose();
    }
    
    // Method to ensure data directory and user file exist
    private void ensureDataFilesExist() {
        // Check if users.txt exists
        if (!function.fileExists("users.txt")) {
            // Add default users
            function.addUser("A001,admin,admin123,admin,Elson");
            function.addUser("R001,receptionist,recep123,receptionist,ChenYao");
            function.addUser("T001,tutor,tutor123,tutor,Yin Yin");
            function.addUser("S001,student,student123,student,Javion");
            System.out.println("Created default users in users.txt");
        }

        // Create other required files
        function.createFileIfNotExists("students.txt");
        function.createFileIfNotExists("payments.txt");
        function.createFileIfNotExists("subject.txt");
        function.createFileIfNotExists("tutors.txt");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new main_page().setVisible(true);
            }
        });
    }
}