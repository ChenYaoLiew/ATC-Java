import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
    
    // Subject Requests Tab
    private JTable requestsTable;
    private JButton approveRequestButton;
    private JButton declineRequestButton;
    private DefaultTableModel requestsTableModel;
    
    private String currentUser;
    private Map<String, String> subjectMap = new HashMap<>();
    private Map<String, String> levelSubjectMap = new HashMap<>();
    private Random random = new Random();
    
    public receptionist_dashboard(String userName) {
        this.currentUser = userName;
        
        setContentPane(mainPanel);
        setTitle("Receptionist Dashboard - " + userName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        welcomeLabel.setText("Welcome, " + userName + " (Receptionist)");
        
        loadSubjects();
        
        initializeComponents();
        
        applyModernStyling();
        setupEventListeners();
        
        loadStudentData();
        
        loadProfile();
        updateNextPaymentNumber();
    }
    
    private void applyModernStyling() {
        // Apply modern styling to existing form components
        
        // Style main panel
        mainPanel.setBackground(new Color(0xF8F9FA));
        
        // Style welcome label
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        
        // Style logout button
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        logoutButton.setFocusPainted(false);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        
        // Style tabbed pane
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(0xF8F9FA));
        
        // Style text fields
        styleTextField(studentNameField);
        styleTextField(icPassportField);
        styleTextField(emailField);
        styleTextField(contactField);
        styleTextField(paymentAmountField);
        styleTextField(profileNameField);
        styleTextField(profileContactField);
        
        // Style text areas
        styleTextArea(addressArea);
        styleTextArea(receiptArea);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Style combo boxes
        styleComboBox(levelCombo);
        styleComboBox(paymentStudentCombo);
        styleComboBox(paymentMethodCombo);
        
        // Style list
        styleList(subjectsList);
        
        // Style buttons
        styleButton(registerStudentButton, new Color(40, 167, 69)); // Green
        styleButton(updateEnrollmentButton, new Color(0, 123, 255)); // Blue
        styleButton(deleteStudentButton, new Color(220, 53, 69)); // Red
        styleButton(acceptPaymentButton, new Color(40, 167, 69)); // Green
        styleButton(generateReceiptButton, new Color(0, 123, 255)); // Blue
        styleButton(updateProfileButton, new Color(0, 123, 255)); // Blue
        styleButton(approveRequestButton, new Color(40, 167, 69)); // Green
        styleButton(declineRequestButton, new Color(220, 53, 69)); // Red
        
        // Style tables
        if (studentsTable != null) {
            studentsTable.setRowHeight(35);
            studentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            studentsTable.getTableHeader().setBackground(new Color(0x007BFF));
            studentsTable.getTableHeader().setForeground(Color.WHITE);
            studentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            studentsTable.setShowGrid(true);
            studentsTable.setGridColor(new Color(0xDEE2E6));
            studentsTable.setSelectionBackground(new Color(0xE7F3FF));
            studentsTable.setBackground(Color.WHITE);
        }
        
        if (requestsTable != null) {
            requestsTable.setRowHeight(35);
            requestsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            requestsTable.getTableHeader().setBackground(new Color(0x007BFF));
            requestsTable.getTableHeader().setForeground(Color.WHITE);
            requestsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            requestsTable.setShowGrid(true);
            requestsTable.setGridColor(new Color(0xDEE2E6));
            requestsTable.setSelectionBackground(new Color(0xE7F3FF));
            requestsTable.setBackground(Color.WHITE);
        }
        

    }
    
    private void initializeComponents() {
        // Initialize Level ComboBox
        String[] levels = {"Secondary 1", "Secondary 2", "Secondary 3", "Secondary 4", "Secondary 5"};
        levelCombo.setModel(new DefaultComboBoxModel<>(levels));
        
        // Initialize Subjects List based on selected level
        updateSubjectsList();
        
        // Add listener to level combo to update subjects
        levelCombo.addActionListener(e -> updateSubjectsList());
        
        // Initialize Payment Method ComboBox
        String[] paymentMethods = {"Cash", "Credit Card", "Debit Card", "Bank Transfer", "Online Banking"};
        paymentMethodCombo.setModel(new DefaultComboBoxModel<>(paymentMethods));
        
        // Initialize Students Table
        String[] columnNames = {"Student ID", "Name", "IC/Passport", "Email", "Contact", "Address", "Level", "Subjects", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        studentsTable.setModel(tableModel);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Initialize Student Selection ComboBox
        paymentStudentCombo.setModel(new DefaultComboBoxModel<>());
        refreshStudentCombo();
        
        // Initialize Subject Requests Table
        String[] requestColumnNames = {"Request ID", "Student ID", "Student Name", "Type", "Subject", "Old Subject", "Reason", "Date", "Status"};
        requestsTableModel = new DefaultTableModel(requestColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        requestsTable.setModel(requestsTableModel);
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Load subject requests
        loadSubjectRequests();
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
        
        // Subject Requests buttons
        approveRequestButton.addActionListener(e -> approveRequest());
        declineRequestButton.addActionListener(e -> declineRequest());
    }
    
    
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDEE2E6)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        field.setBackground(Color.WHITE);
    }
    
    private void styleTextArea(JTextArea area) {
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDEE2E6)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        area.setBackground(Color.WHITE);
    }
    
    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBorder(BorderFactory.createLineBorder(new Color(0xDEE2E6)));
        combo.setBackground(Color.WHITE);
    }
    
    private void styleList(JList<?> list) {
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        list.setSelectionBackground(new Color(0xE7F3FF));
        list.setBackground(Color.WHITE);
        list.setBorder(BorderFactory.createLineBorder(new Color(0xDEE2E6)));
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
    }
    
    private void loadSubjects() {
        subjectMap.clear();
        levelSubjectMap.clear();
        
        List<String> subjectLines = function.readSubjects();
        
        for (String line : subjectLines) {
            String[] parts = line.split(",");
            
            if (parts.length >= 3) {
                String subjectId = parts[0].trim();
                String subjectName = parts[1].trim();
                String level = parts[2].trim();
                
                subjectMap.put(subjectId, subjectName);
                levelSubjectMap.put(subjectId, level);
            }
        }
    }
    
    private void updateSubjectsList() {
        String selectedLevel = (String) levelCombo.getSelectedItem();
        
        List<String> availableSubjects = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : levelSubjectMap.entrySet()) {
            if (entry.getValue().equals(selectedLevel)) {
                String subjectId = entry.getKey();
                String subjectName = subjectMap.get(subjectId);
                
                if (subjectName != null) {
                    String display = subjectName + " (" + subjectId + ")";
                    availableSubjects.add(display);
                }
            }
        }
        
        subjectsList.setListData(availableSubjects.toArray(new String[0]));
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
        
        // Generate new student ID
        String studentId = generateNextStudentId();
        
        String name = studentNameField.getText().trim();
        String icPassport = icPassportField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressArea.getText().trim();
        String level = (String) levelCombo.getSelectedItem();
        String enrollmentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String status = "Active";
        
        // Save student to students.txt
        String studentData = studentId + "," + name + "," + icPassport + "," + email + "," + 
                            contact + "," + address + "," + enrollmentDate + "," + level + "," + status;
        
        if (function.addStudent(studentData)) {
            // Save student subjects
            saveStudentSubjects(studentId, selectedSubjects);
            
            // Create user account for the student and get credentials
            String[] credentials = createStudentUserAccount(name, studentId, email);
            
            // Add to table
            String subjectsDisplay = getSubjectsDisplayString(selectedSubjects);
            tableModel.addRow(new Object[]{studentId, name, icPassport, email, contact, address, level, subjectsDisplay, status});
            
            // Refresh student dropdown
            refreshStudentCombo();
            
            JOptionPane.showMessageDialog(this, 
                "Student registered successfully!\n" +
                "Student ID: " + studentId + "\n" +
                "Username: " + credentials[0] + "\n" +
                "Password: " + credentials[1] + "\n\n" +
                "Please save these credentials for the student.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form
            clearRegistrationForm();
        } else {
            JOptionPane.showMessageDialog(this, "Error saving student data.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateNextStudentId() {
        return function.generateStudentId();
    }
    
    private void saveStudentSubjects(String studentId, List<String> selectedSubjects) {
        for (String subjectDisplay : selectedSubjects) {
            // Extract subject ID from display string like "Math (SUB001)"
            String subjectId = extractSubjectId(subjectDisplay);
            
            if (!subjectId.isEmpty()) {
                String studentSubjectData = studentId + "," + subjectId;
                function.addStudentSubject(studentSubjectData);
            }
        }
    }
    
    private String extractSubjectId(String subjectDisplay) {
        // Extract SUB001 from "Math (SUB001)"
        int start = subjectDisplay.lastIndexOf("(");
        int end = subjectDisplay.lastIndexOf(")");
        if (start != -1 && end != -1 && start < end) {
            return subjectDisplay.substring(start + 1, end);
        }
        return "";
    }
    
    private String getSubjectsDisplayString(List<String> selectedSubjects) {
        List<String> subjectNames = new ArrayList<>();
        for (String subjectDisplay : selectedSubjects) {
            int parenIndex = subjectDisplay.indexOf(" (");
            if (parenIndex != -1) {
                subjectNames.add(subjectDisplay.substring(0, parenIndex));
            } else {
                subjectNames.add(subjectDisplay);
            }
        }
        return String.join(", ", subjectNames);
    }
    
    private void updateEnrollment() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to update.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentSubjects = (String) tableModel.getValueAt(selectedRow, 7);
        
        // Show dialog to select new subjects
        String level = (String) tableModel.getValueAt(selectedRow, 6);
        List<String> availableSubjects = getSubjectsForLevel(level);
        
        JList<String> subjectListDialog = new JList<>(availableSubjects.toArray(new String[0]));
        subjectListDialog.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(subjectListDialog);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        
        int result = JOptionPane.showConfirmDialog(this, scrollPane, 
            "Select new subjects (max 3) for " + tableModel.getValueAt(selectedRow, 1), 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            List<String> selectedSubjects = subjectListDialog.getSelectedValuesList();
            if (selectedSubjects.size() > 3) {
                JOptionPane.showMessageDialog(this, "Maximum 3 subjects allowed.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update student subjects in file
            updateStudentSubjectsFile(studentId, selectedSubjects);
            
            // Update table
            String subjectsDisplay = getSubjectsDisplayString(selectedSubjects);
            tableModel.setValueAt(subjectsDisplay, selectedRow, 7);
            
            JOptionPane.showMessageDialog(this, "Enrollment updated successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private List<String> getSubjectsForLevel(String level) {
        List<String> subjects = new ArrayList<>();
        for (Map.Entry<String, String> entry : levelSubjectMap.entrySet()) {
            if (entry.getValue().equals(level)) {
                String subjectId = entry.getKey();
                String subjectName = subjectMap.get(subjectId);
                subjects.add(subjectName + " (" + subjectId + ")");
            }
        }
        return subjects;
    }
    
    private void updateStudentSubjectsFile(String studentId, List<String> selectedSubjects) {
        // Read all existing student-subject relationships
        List<String> allLines = function.readStudentSubjects();
        List<String> updatedLines = new ArrayList<>();
        
        // Keep other students' subjects
        for (String line : allLines) {
            String[] parts = line.split(",");
            if (parts.length >= 2 && !parts[0].trim().equals(studentId)) {
                updatedLines.add(line);
            }
        }
        
        // Add new subjects for this student
        for (String subjectDisplay : selectedSubjects) {
            String subjectId = extractSubjectId(subjectDisplay);
            if (!subjectId.isEmpty()) {
                updatedLines.add(studentId + "," + subjectId);
            }
        }
        
        // Clear the file and rewrite all data
        // First, delete all existing entries for this student
        for (String line : allLines) {
            String[] parts = line.split(",");
            if (parts.length >= 2 && parts[0].trim().equals(studentId)) {
                function.deleteStudentSubject(line);
            }
        }
        
        // Add new subjects for this student
        for (String subjectDisplay : selectedSubjects) {
            String subjectId = extractSubjectId(subjectDisplay);
            if (!subjectId.isEmpty()) {
                function.addStudentSubject(studentId + "," + subjectId);
            }
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
        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete student: " + studentName + "?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Remove from table
            tableModel.removeRow(selectedRow);
            
            // Update files
            updateStudentFile();
            removeStudentSubjects(studentId);
            
            // Refresh student combo
            refreshStudentCombo();
            
            JOptionPane.showMessageDialog(this, "Student deleted successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void removeStudentSubjects(String studentId) {
        List<String> allLines = function.readStudentSubjects();
        
        // Delete all entries for this student
        for (String line : allLines) {
            String[] parts = line.split(",");
            if (parts.length >= 2 && parts[0].trim().equals(studentId)) {
                function.deleteStudentSubject(line);
            }
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
        
        String studentId = extractStudentId(selectedStudent);
        
        try {
            double amount = Double.parseDouble(amountStr);
            String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String period = new SimpleDateFormat("yyyy-MM").format(new Date());
            
            // Generate payment ID and receipt ID
            String paymentId = generateNextPaymentId();
            String receiptId = "RCP" + paymentId.substring(3); // RCP001 from PAY001
            
            // Save payment record
            String paymentData = paymentId + "," + studentId + ",," + amount + "," + paymentMethod + "," + 
                               timestamp + "," + period + "," + receiptId + ",Completed";
            
            if (function.addPayment(paymentData)) {
                JOptionPane.showMessageDialog(this, "Payment accepted successfully!\nPayment ID: " + paymentId, 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear payment fields
                paymentStudentCombo.setSelectedIndex(0);
                paymentAmountField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error saving payment.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateNextPaymentId() {
        return function.generatePaymentId();
    }
    
    private void updateNextPaymentNumber() {
        // This method is no longer needed since function.generatePaymentId() handles ID generation
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
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String paymentId = generateNextPaymentId();
            String receiptId = "RCP" + paymentId.substring(3); // Extract number from PAY001 -> RCP001
            
            String receipt = generateReceiptText(studentId, selectedStudent, amount, paymentMethod, timestamp, receiptId);
            receiptArea.setText(receipt);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateReceiptText(String studentId, String studentName, double amount, String paymentMethod, String timestamp, String receiptId) {
        return "==========================================\n" +
               "           TUITION CENTER RECEIPT        \n" +
               "==========================================\n" +
               "Receipt ID: " + receiptId + "\n" +
               "Receipt Date: " + timestamp + "\n" +
               "Student: " + extractStudentName(studentName) + "\n" +
               "Student ID: " + studentId + "\n" +
               "Amount Paid: RM " + String.format("%.2f", amount) + "\n" +
               "Payment Method: " + paymentMethod + "\n" +
               "Received by: " + currentUser + "\n" +
               "Status: Completed\n" +
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
            String status = (String) tableModel.getValueAt(i, 8);
            
            // Only add active students
            if ("Active".equals(status)) {
                model.addElement(studentName + " - " + studentId);
            }
        }
        
        paymentStudentCombo.setModel(model);
    }
    
    // Method to generate random username
    private String generateRandomUsername(String studentName) {
        // Remove spaces and take first part of name
        String baseName = studentName.toLowerCase().replaceAll("\\s+", "");
        if (baseName.length() > 6) {
            baseName = baseName.substring(0, 6);
        }
        
        // Add random 3-digit number
        int randomNum = random.nextInt(900) + 100; // Generates 100-999
        return baseName + randomNum;
    }
    
    // Method to generate random password
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        
        // Generate 8-character password
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
    
    // Method to create user account for student
    private String[] createStudentUserAccount(String studentName, String studentId, String email) {
        // Generate random credentials
        String username = generateRandomUsername(studentName);
        String password = generateRandomPassword();
        String role = "student";
        
        // Format: userId,username,password,role,name
        String userData = studentId + "," + username + "," + password + "," + role + "," + studentName;
        
        function.addUser(userData);
        
        // Return the generated credentials
        return new String[]{username, password};
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
        // Ensure file exists
        function.createFileIfNotExists("students.txt");
        
        // Load existing student data
        List<String> studentLines = function.readStudents();
        
        for (String line : studentLines) {
            String[] parts = line.split(",");
            
            if (parts.length >= 9) {
                // Format: studentId,name,icPassport,email,contact,address,enrollmentDate,level,status
                String studentId = parts[0].trim();
                String name = parts[1].trim();
                String icPassport = parts[2].trim();
                String email = parts[3].trim();
                String contact = parts[4].trim();
                String address = parts[5].trim();
                String level = parts[7].trim();
                String status = parts[8].trim();
                
                // Load subjects for this student
                String subjects = loadStudentSubjects(studentId);
                
                tableModel.addRow(new Object[]{
                    studentId, name, icPassport, email, contact, address, level, subjects, status
                });
            }
        }
        
        // Refresh student dropdown after loading data
        refreshStudentCombo();
    }
    
    private String loadStudentSubjects(String studentId) {
        List<String> subjects = new ArrayList<>();
        
        List<String> studentSubjectLines = function.readStudentSubjects();
        
        for (String line : studentSubjectLines) {
            String[] parts = line.split(",");
            if (parts.length >= 2 && parts[0].trim().equals(studentId)) {
                String subjectId = parts[1].trim();
                String subjectName = subjectMap.get(subjectId);
                
                if (subjectName != null) {
                    subjects.add(subjectName);
                }
            }
        }
        
        return String.join(", ", subjects);
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
        // First clear all existing student data
        List<String> allStudents = function.readStudents();
        for (String studentLine : allStudents) {
            function.deleteStudent(studentLine);
        }
        
        // Then add all current table data
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            // Format: studentId,name,icPassport,email,contact,address,enrollmentDate,level,status
            String line = tableModel.getValueAt(i, 0) + "," +  // ID
                         tableModel.getValueAt(i, 1) + "," +   // Name
                         tableModel.getValueAt(i, 2) + "," +   // IC
                         tableModel.getValueAt(i, 3) + "," +   // Email
                         tableModel.getValueAt(i, 4) + "," +   // Contact
                         tableModel.getValueAt(i, 5) + "," +   // Address
                         new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "," + // Date
                         tableModel.getValueAt(i, 6) + "," +   // Level
                         tableModel.getValueAt(i, 8);          // Status
            
            if (!function.addStudent(line)) {
                JOptionPane.showMessageDialog(this, "Error updating student file.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }
    
    private void loadSubjectRequests() {
        // Clear existing data
        requestsTableModel.setRowCount(0);
        
        // Load subject requests from file
        List<String> requestLines = function.readSubjectRequests();
        
        for (String line : requestLines) {
            String[] parts = line.split(",");
            if (parts.length >= 9) {
                String requestId = parts[0].trim();
                String studentId = parts[1].trim();
                String requestType = parts[2].trim();
                String subjectId = parts[3].trim();
                String oldSubjectId = parts[4].trim();
                String reason = parts[5].trim();
                String requestDate = parts[6].trim();
                String status = parts[7].trim();
                
                // Only show pending requests
                if (!"Pending".equals(status)) {
                    continue; // Skip approved, rejected, or other status requests
                }
                
                // Get student name
                String studentName = getStudentName(studentId);
                
                // Get subject name
                String subjectName = getSubjectName(subjectId);
                String oldSubjectName = oldSubjectId.isEmpty() ? "" : getSubjectName(oldSubjectId);
                
                // Add to table
                requestsTableModel.addRow(new Object[]{
                    requestId, studentId, studentName, requestType, subjectName, 
                    oldSubjectName, reason, requestDate, status
                });
            }
        }
    }
    
    private String getStudentName(String studentId) {
        List<String> studentLines = function.readStudents();
        for (String line : studentLines) {
            String[] parts = line.split(",");
            if (parts.length >= 2 && parts[0].trim().equals(studentId)) {
                return parts[1].trim();
            }
        }
        return "Unknown";
    }
    
    private String getSubjectName(String subjectId) {
        return subjectMap.getOrDefault(subjectId, subjectId);
    }
    
    private String[] getRequestData(String requestId) {
        List<String> requestLines = function.readSubjectRequests();
        for (String line : requestLines) {
            String[] parts = line.split(",");
            if (parts.length >= 9 && parts[0].trim().equals(requestId)) {
                return parts;
            }
        }
        return new String[0];
    }
    
    private void approveRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to approve.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String requestId = (String) requestsTableModel.getValueAt(selectedRow, 0);
        String studentId = (String) requestsTableModel.getValueAt(selectedRow, 1);
        String requestType = (String) requestsTableModel.getValueAt(selectedRow, 3);
        String subjectDisplay = (String) requestsTableModel.getValueAt(selectedRow, 4);
        String oldSubjectDisplay = (String) requestsTableModel.getValueAt(selectedRow, 5);
        String status = (String) requestsTableModel.getValueAt(selectedRow, 8);
        
        // Extract subject IDs from the original request data
        String[] requestData = getRequestData(requestId);
        String subjectId = requestData[3];
        String oldSubjectId = requestData[4];
        
        // Check if request is already processed
        if (!"Pending".equals(status)) {
            JOptionPane.showMessageDialog(this, "This request has already been processed.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get comments from user
        String comments = JOptionPane.showInputDialog(this, 
            "Enter approval comments (optional):", 
            "Approve Request", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (comments == null) {
            return; // User cancelled
        }
        
        if (comments.trim().isEmpty()) {
            comments = "Request approved";
        }
        
        // Process the request based on type
        boolean success = false;
        if ("Add".equals(requestType)) {
            success = processAddRequest(studentId, subjectId);
        } else if ("Drop".equals(requestType)) {
            success = processDropRequest(studentId, subjectId);
        } else if ("Change".equals(requestType)) {
            success = processChangeRequest(studentId, oldSubjectId, subjectId);
        }
        
        if (success) {
            // Update request status in file
            updateRequestStatus(requestId, "Approved", comments);
            
            // Refresh tables
            loadSubjectRequests();
            loadStudentData();
            
            JOptionPane.showMessageDialog(this, "Request approved successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to process request.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void declineRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to decline.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String requestId = (String) requestsTableModel.getValueAt(selectedRow, 0);
        String status = (String) requestsTableModel.getValueAt(selectedRow, 8);
        
        // Check if request is already processed
        if (!"Pending".equals(status)) {
            JOptionPane.showMessageDialog(this, "This request has already been processed.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get decline reason from user
        String reason = JOptionPane.showInputDialog(this, 
            "Enter reason for declining:", 
            "Decline Request", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (reason == null) {
            return; // User cancelled
        }
        
        if (reason.trim().isEmpty()) {
            reason = "Request declined";
        }
        
        // Update request status in file
        updateRequestStatus(requestId, "Rejected", reason);
        
        // Refresh table
        loadSubjectRequests();
        
        JOptionPane.showMessageDialog(this, "Request declined successfully!", 
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private boolean processAddRequest(String studentId, String subjectId) {
        // Add subject to student_subjects.txt
        String studentSubjectData = studentId + "," + subjectId;
        return function.addStudentSubject(studentSubjectData);
    }
    
    private boolean processDropRequest(String studentId, String subjectId) {
        // Remove subject from student_subjects.txt
        String studentSubjectData = studentId + "," + subjectId;
        return function.deleteStudentSubject(studentSubjectData);
    }
    
    private boolean processChangeRequest(String studentId, String oldSubjectId, String newSubjectId) {
        // Remove old subject and add new subject
        String oldData = studentId + "," + oldSubjectId;
        String newData = studentId + "," + newSubjectId;
        
        boolean removeSuccess = function.deleteStudentSubject(oldData);
        boolean addSuccess = function.addStudentSubject(newData);
        
        return removeSuccess && addSuccess;
    }
    
    private void updateRequestStatus(String requestId, String status, String comments) {
        // Read all requests
        List<String> requestLines = function.readSubjectRequests();
        List<String> updatedLines = new ArrayList<>();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());
        
        for (String line : requestLines) {
            String[] parts = line.split(",");
            if (parts.length >= 9 && parts[0].trim().equals(requestId)) {
                // Update this request
                // Format: RequestID,StudentID,RequestType,SubjectID,OldSubjectID,Reason,RequestDate,Status,ProcessedDate,Comments
                String updatedLine = parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + 
                                   parts[4] + "," + parts[5] + "," + parts[6] + "," + status + "," + 
                                   currentDate + "," + comments;
                updatedLines.add(updatedLine);
            } else {
                updatedLines.add(line);
            }
        }
        
        // Clear and rewrite the file
        // First delete all existing lines
        for (String line : requestLines) {
            function.deleteSubjectRequest(line);
        }
        
        // Add updated lines
        for (String line : updatedLines) {
            function.addSubjectRequest(line);
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