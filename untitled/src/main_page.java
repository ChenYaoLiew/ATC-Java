import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.File;
import java.io.FileWriter;

public class main_page extends JFrame {
    private JPanel main;
    private JTextField username_field;
    private JPasswordField password_field;
    private JButton loginButton;

    // Store login attempts for non-admin users
    private static Map<String, Integer> loginAttempts = new HashMap<>();
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    
    // Define base directory for data files - using current directory
    private static final String DATA_DIR = "data";
    
    public main_page() {
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
        
        User user = authenticateUser(username, password);
        
        if (user != null) {
            // Reset login attempts on successful login
            loginAttempts.remove(username);
            
            JOptionPane.showMessageDialog(this, 
                "Login successful! Welcome " + user.getName() + " (" + user.getRole() + ")", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // TODO: Navigate to appropriate dashboard based on role
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
    
    private User authenticateUser(String username, String password) {
        // Get current directory path for file access
        String currentDir = System.getProperty("user.dir");
        String filePath = Paths.get(currentDir, DATA_DIR, "users.txt").toString();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String fileUsername = parts[0].trim();
                    String filePassword = parts[1].trim();
                    String role = parts[2].trim();
                    String name = parts[3].trim();
                    
                    if (fileUsername.equals(username) && filePassword.equals(password)) {
                        return new User(username, role, name);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading user data: " + e.getMessage() + "\nPath: " + filePath, 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    private void openDashboard(User user) {
        // Hide the login window
        this.setVisible(false);
        
        // Open appropriate dashboard based on user role
        switch (user.getRole()) {
            case "Admin":
                // TODO: Open Admin dashboard
                System.out.println("Opening Admin dashboard for " + user.getName());
                // new AdminDashboard(user.getName()).setVisible(true);
                break;
            case "Receptionist":
                new receptionist_dashboard(user.getName()).setVisible(true);
                break;
            case "Tutor":
                // TODO: Open Tutor dashboard
                System.out.println("Opening Tutor dashboard for " + user.getName());
                // new TutorDashboard(user.getName()).setVisible(true);
                break;
            case "Student":
                new student_dashboard(user.getName()).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown user role: " + user.getRole(), 
                                            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method to ensure data directory and user file exist
    private void ensureDataFilesExist() {
        try {
            // Get current directory path
            String currentDir = System.getProperty("user.dir");
            
            // Create data directory if it doesn't exist
            File dataDir = new File(Paths.get(currentDir, DATA_DIR).toString());
            if (!dataDir.exists()) {
                dataDir.mkdir();
                System.out.println("Created data directory at: " + dataDir.getAbsolutePath());
            }
            
            // Create users.txt if it doesn't exist
            File usersFile = new File(Paths.get(currentDir, DATA_DIR, "users.txt").toString());
            if (!usersFile.exists()) {
                try (FileWriter writer = new FileWriter(usersFile)) {
                    // Add default users
                    writer.write("admin,admin123,Admin,Elson\n");
                    writer.write("receptionist,recep123,Receptionist,ChenYao\n");
                    writer.write("tutor,tutor123,Tutor,Yin Yin\n");
                    writer.write("student,student123,Student,Javion\n");
                    System.out.println("Created users.txt at: " + usersFile.getAbsolutePath());
                }
            }
            
            // Create students.txt if it doesn't exist
            File studentsFile = new File(Paths.get(currentDir, DATA_DIR, "students.txt").toString());
            if (!studentsFile.exists()) {
                studentsFile.createNewFile();
                System.out.println("Created students.txt at: " + studentsFile.getAbsolutePath());
            }
            
            // Create payments.txt if it doesn't exist
            File paymentsFile = new File(Paths.get(currentDir, DATA_DIR, "payments.txt").toString());
            if (!paymentsFile.exists()) {
                paymentsFile.createNewFile();
                System.out.println("Created payments.txt at: " + paymentsFile.getAbsolutePath());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating data files: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new main_page().setVisible(true);
            }
        });
    }
    
    // Inner class to represent a User
    private static class User {
        private String username;
        private String role;
        private String name;
        
        public User(String username, String role, String name) {
            this.username = username;
            this.role = role;
            this.name = name;
        }
        
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public String getName() { return name; }
    }
}