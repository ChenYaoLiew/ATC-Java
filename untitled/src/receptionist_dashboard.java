import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class receptionist_dashboard extends JFrame {
    private JPanel mainPanel;
    private JLabel welcomeLabel;
    private JButton logoutButton;
    private JTabbedPane tabbedPane;
    
    // Register Student Tab
    private JTextField studentNameField;
    private JTextField icPassportField;
    private JTextField emailField;
    private JTextField contactField;
    private JTextArea addressArea;
    private JComboBox<String> levelCombo;
    private JList<String> subjectsList;
    private JButton registerStudentButton;
    
    // Student Management Tab
    private JTable studentsTable;
    private JButton updateEnrollmentButton;
    private JButton deleteStudentButton;
    private DefaultTableModel tableModel;
    
    // Payment Tab
    private JComboBox<String> paymentStudentCombo;
    private JTextField paymentAmountField;
    private JComboBox<String> paymentMethodCombo;
    private JButton acceptPaymentButton;
    private JButton generateReceiptButton;
    private JTextArea receiptArea;
    
    // Profile Tab
    private JTextField profileNameField;
    private JTextField profileContactField;
    private JButton updateProfileButton;
    
    private String currentUser;
    private static int nextStudentId = 1001; // Starting student ID
    
    public receptionist_dashboard(String userName) {
        this.currentUser = userName;
        
        setContentPane(mainPanel);
        setTitle("Receptionist Dashboard - " + userName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        welcomeLabel.setText("Welcome, " + userName + " (Receptionist)");
        
        initializeComponents();
        setupEventListeners();
        loadStudentData();
        loadProfile();
    }
    
    private void initializeComponents() {
        // Initialize Level ComboBox
        String[] levels = {"Primary 1", "Primary 2", "Primary 3", "Primary 4", "Primary 5", "Primary 6",
                          "Secondary 1", "Secondary 2", "Secondary 3", "Secondary 4", "Secondary 5"};
        levelCombo.setModel(new DefaultComboBoxModel<>(levels));
        
        // Initialize Subjects List (max 3 selections)
        String[] subjects = {"Mathematics", "English", "Science", "Chinese", "Malay", "History", 
                           "Geography", "Physics", "Chemistry", "Biology", "Economics", "Accounting"};
        subjectsList.setListData(subjects);
        subjectsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // Initialize Payment Method ComboBox
        String[] paymentMethods = {"Cash", "Credit Card", "Debit Card", "Bank Transfer", "Online Banking"};
        paymentMethodCombo.setModel(new DefaultComboBoxModel<>(paymentMethods));
        
        // Initialize Students Table FIRST (before refreshStudentCombo)
        String[] columnNames = {"Student ID", "Name", "IC/Passport", "Email", "Contact", "Level", "Subjects", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        studentsTable.setModel(tableModel);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Initialize Student Selection ComboBox AFTER tableModel is created
        paymentStudentCombo.setModel(new DefaultComboBoxModel<>());
        refreshStudentCombo();
    }
    
    private void setupEventListeners() {
        // Logout button
        logoutButton.addActionListener(e -> logout());
        
        // Register Student button
        registerStudentButton.addActionListener(e -> registerStudent());
        
        // Update Enrollment button
        updateEnrollmentButton.addActionListener(e -> updateEnrollment());
        
        // Delete Student button
        deleteStudentButton.addActionListener(e -> deleteStudent());
        
        // Accept Payment button
        acceptPaymentButton.addActionListener(e -> acceptPayment());
        
        // Generate Receipt button
        generateReceiptButton.addActionListener(e -> generateReceipt());
        
        // Update Profile button
        updateProfileButton.addActionListener(e -> updateProfile());
    }
    
    private void registerStudent() {
        // Validate input fields
        if (studentNameField.getText().trim().isEmpty() || 
            icPassportField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            contactField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check subject selection (max 3)
        List<String> selectedSubjects = subjectsList.getSelectedValuesList();
        if (selectedSubjects.isEmpty() || selectedSubjects.size() > 3) {
            JOptionPane.showMessageDialog(this, "Please select 1-3 subjects only.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create student record
        String studentId = String.valueOf(nextStudentId++);
        String name = studentNameField.getText().trim();
        String icPassport = icPassportField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressArea.getText().trim();
        String level = (String) levelCombo.getSelectedItem();
        String subjects = String.join(", ", selectedSubjects);
        String enrollmentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        // Save to file
        try (FileWriter writer = new FileWriter("data/students.txt", true)) {
            writer.write(studentId + "," + name + "," + icPassport + "," + email + "," + 
                        contact + "," + address + "," + level + "," + subjects + "," + 
                        enrollmentDate + ",Active\n");
            
            // Add to table
            tableModel.addRow(new Object[]{studentId, name, icPassport, email, contact, level, subjects, "Active"});
            
            // Refresh student dropdown
            refreshStudentCombo();
            
            JOptionPane.showMessageDialog(this, 
                "Student registered successfully!\nStudent ID: " + studentId, 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form
            clearRegistrationForm();
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving student data: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateEnrollment() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to update.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentSubjects = (String) tableModel.getValueAt(selectedRow, 6);
        
        String newSubjects = JOptionPane.showInputDialog(this, 
            "Current subjects: " + currentSubjects + "\n\nEnter new subjects (comma-separated, max 3):", 
            "Update Enrollment", JOptionPane.QUESTION_MESSAGE);
        
        if (newSubjects != null && !newSubjects.trim().isEmpty()) {
            String[] subjectArray = newSubjects.split(",");
            if (subjectArray.length > 3) {
                JOptionPane.showMessageDialog(this, "Maximum 3 subjects allowed.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update table
            tableModel.setValueAt(newSubjects.trim(), selectedRow, 6);
            
            // Update file (simplified - in production, you'd want proper file updating)
            updateStudentFile();
            
            JOptionPane.showMessageDialog(this, "Enrollment updated successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void deleteStudent() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String studentName = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete student: " + studentName + "?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            updateStudentFile();
            JOptionPane.showMessageDialog(this, "Student deleted successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void acceptPayment() {
        String selectedStudent = (String) paymentStudentCombo.getSelectedItem();
        String amountStr = paymentAmountField.getText().trim();
        
        if (selectedStudent == null || selectedStudent.equals("Select a student...") || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a student and enter amount.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Extract student ID from the combo box selection (format: "Name - ID")
        String studentId = extractStudentId(selectedStudent);
        
        try {
            double amount = Double.parseDouble(amountStr);
            String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            
            // Save payment record
            try (FileWriter writer = new FileWriter("data/payments.txt", true)) {
                writer.write(studentId + "," + amount + "," + paymentMethod + "," + date + "\n");
                
                JOptionPane.showMessageDialog(this, "Payment accepted successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear payment fields
                paymentStudentCombo.setSelectedIndex(0);
                paymentAmountField.setText("");
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving payment: " + e.getMessage(), 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateReceipt() {
        String selectedStudent = (String) paymentStudentCombo.getSelectedItem();
        String amountStr = paymentAmountField.getText().trim();
        
        if (selectedStudent == null || selectedStudent.equals("Select a student...") || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a student and enter amount first.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String studentId = extractStudentId(selectedStudent);
        
        try {
            double amount = Double.parseDouble(amountStr);
            String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            
            String receipt = generateReceiptText(studentId, selectedStudent, amount, paymentMethod, date);
            receiptArea.setText(receipt);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateReceiptText(String studentId, String studentName, double amount, String paymentMethod, String date) {
        return "==========================================\n" +
               "           TUITION CENTER RECEIPT        \n" +
               "==========================================\n" +
               "Receipt Date: " + date + "\n" +
               "Student: " + extractStudentName(studentName) + "\n" +
               "Student ID: " + studentId + "\n" +
               "Amount Paid: RM " + String.format("%.2f", amount) + "\n" +
               "Payment Method: " + paymentMethod + "\n" +
               "Received by: " + currentUser + "\n" +
               "==========================================\n" +
               "Thank you for your payment!\n" +
               "==========================================";
    }
    
    // Helper method to extract student ID from combo box selection (format: "Name - ID")
    private String extractStudentId(String comboSelection) {
        if (comboSelection == null || !comboSelection.contains(" - ")) {
            return "";
        }
        String[] parts = comboSelection.split(" - ");
        return parts.length > 1 ? parts[1] : "";
    }
    
    // Helper method to extract student name from combo box selection (format: "Name - ID")
    private String extractStudentName(String comboSelection) {
        if (comboSelection == null || !comboSelection.contains(" - ")) {
            return comboSelection;
        }
        String[] parts = comboSelection.split(" - ");
        return parts.length > 0 ? parts[0] : "";
    }
    
    // Method to refresh the student dropdown with current student data
    private void refreshStudentCombo() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Select a student...");
        
        // Add students from the table
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String studentName = (String) tableModel.getValueAt(i, 1);
            String studentId = (String) tableModel.getValueAt(i, 0);
            String status = (String) tableModel.getValueAt(i, 7);
            
            // Only add active students
            if ("Active".equals(status)) {
                model.addElement(studentName + " - " + studentId);
            }
        }
        
        paymentStudentCombo.setModel(model);
    }
    
    private void updateProfile() {
        String newName = profileNameField.getText().trim();
        String newContact = profileContactField.getText().trim();
        
        if (newName.isEmpty() || newContact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all profile fields.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update profile (simplified - in production, you'd update the users file)
        JOptionPane.showMessageDialog(this, "Profile updated successfully!", 
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void loadStudentData() {
        // Create students.txt if it doesn't exist
        File file = new File("data/students.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
        // Load existing student data
        try (BufferedReader reader = new BufferedReader(new FileReader("data/students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    tableModel.addRow(new Object[]{
                        parts[0], parts[1], parts[2], parts[3], parts[4], 
                        parts[6], parts[7], parts.length > 9 ? parts[9] : "Active"
                    });
                    
                    // Update next student ID
                    try {
                        int id = Integer.parseInt(parts[0]);
                        if (id >= nextStudentId) {
                            nextStudentId = id + 1;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading student data: " + e.getMessage());
        }
        
        // Refresh student dropdown after loading data
        refreshStudentCombo();
    }
    
    private void loadProfile() {
        // Set current user info in profile tab
        profileNameField.setText(currentUser);
        profileContactField.setText(""); // Would load from user profile file in production
    }
    
    private void clearRegistrationForm() {
        studentNameField.setText("");
        icPassportField.setText("");
        emailField.setText("");
        contactField.setText("");
        addressArea.setText("");
        levelCombo.setSelectedIndex(0);
        subjectsList.clearSelection();
    }
    
    private void updateStudentFile() {
        // Simplified file update - writes all current table data back to file
        try (FileWriter writer = new FileWriter("data/students.txt", false)) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String line = tableModel.getValueAt(i, 0) + "," +  // ID
                             tableModel.getValueAt(i, 1) + "," +   // Name
                             tableModel.getValueAt(i, 2) + "," +   // IC
                             tableModel.getValueAt(i, 3) + "," +   // Email
                             tableModel.getValueAt(i, 4) + "," +   // Contact
                             "," +                                  // Address (simplified)
                             tableModel.getValueAt(i, 5) + "," +   // Level
                             tableModel.getValueAt(i, 6) + "," +   // Subjects
                             new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "," + // Date
                             tableModel.getValueAt(i, 7) + "\n";   // Status
                writer.write(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating student file: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
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
    
    // For testing purposes
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new receptionist_dashboard("Test Receptionist").setVisible(true);
            }
        });
    }
} 