import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class main_page extends JFrame {
    private JPanel main;
    private JTextField username_field;
    private JPasswordField password_field;
    private JButton loginButton;

    // Store login attempts for non-admin users
    private static Map<String, Integer> loginAttempts = new HashMap<>();
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    
    public main_page() {
        setContentPane(main);
        setTitle("Tuition Center Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        
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
                // TODO: Open Tutor dashboard
                System.out.println("Opening Tutor dashboard for " + user.getName());
                // new TutorDashboard(user.getName()).setVisible(true);
                break;
            case "student":
                // TODO: Open Student dashboard
                System.out.println("Opening Student dashboard for " + user.getName());
                // new StudentDashboard(user.getName()).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown user role: " + user.getRole(), 
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