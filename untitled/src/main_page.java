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
        
        // Use admin_dashboard's authentication method
        User user = admin_dashboard.authenticateUser(username, password);
        
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
        try {
            // First get the tutor ID from users.txt based on login username
            String tutorId = "";
            String tutorName = "";
            File usersFile = new File(DATA_DIR + "/users.txt");
            if (usersFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(usersFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    // Match the login username (parts[1]) with user's username
                    if (parts.length >= 5 && parts[1].equals(user.getUsername())) {
                        tutorId = parts[0].trim();    // Get ID (e.g., T001)
                        tutorName = parts[4].trim();  // Get Name (e.g., Yin Yin)
                        break;
                    }
                }
                reader.close();
            }

            // Now read from tutors.txt to get full tutor details using the tutor ID
            File tutorsFile = new File(DATA_DIR + "/tutors.txt");
            if (tutorsFile.exists() && !tutorId.isEmpty()) {
                BufferedReader reader = new BufferedReader(new FileReader(tutorsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6 && parts[0].trim().equals(tutorId)) {
                        // Create tutor with full details from tutors.txt
                        Tutor tutor = new Tutor(
                            tutorId,  // ID from users.txt
                            user.getPassword(),
                            tutorName,  // Name from users.txt
                            parts[3].trim(),  // email
                            parts[4].trim()   // contact
                        );
                        // Load additional tutor-specific data
                        loadTutorData(tutor);
                        reader.close();
                        return tutor;
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Fallback to basic user info if tutor record not found
        return new Tutor(
            user.getUsername(),
            user.getPassword(),
            user.getName(),
            user.getEmail() != null ? user.getEmail() : "",
            user.getContactNumber() != null ? user.getContactNumber() : ""
        );
    }
    
    private void loadTutorData(Tutor tutor) {
        try {
            // Load subjects and levels from a tutor data file if it exists
            File tutorFile = new File(DATA_DIR + "/tutor_data.txt");
            if (tutorFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(tutorFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3 && parts[0].trim().equals(tutor.getUsername())) {
                        // Format: username,subject,level
                        tutor.addSubject(parts[1].trim());
                        tutor.addLevel(parts[2].trim());
                    }
                }
                reader.close();
            } else {
                // Add default subjects and levels if no data file exists
                tutor.addSubject("Mathematics");
                tutor.addSubject("Science");
                tutor.addLevel("Form 1");
                tutor.addLevel("Form 2");
            }
        } catch (IOException e) {
            // If there's an error loading data, add default subjects and levels
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
                    // Add default users with format: username,password,role,name,student_id
                    writer.write("admin,admin123,admin,Elson,\n");
                    writer.write("receptionist,recep123,receptionist,ChenYao,\n");
                    writer.write("tutor,tutor123,tutor,Yin Yin,\n");
                    writer.write("student,student123,student,Javion,\n");
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
}