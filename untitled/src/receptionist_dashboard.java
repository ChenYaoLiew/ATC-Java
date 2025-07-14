import java.io.*;
import java.nio.file.Paths;
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
    
    private String currentUser;
    private static int nextStudentNumber = 1; // For generating S001, S002, etc.
    private static int nextPaymentNumber = 1; // For generating PAY001, PAY002, etc.
    private Map<String, String> subjectMap = new HashMap<>(); // subjectId -> subjectName
    private Map<String, String> levelSubjectMap = new HashMap<>(); // subjectId -> level
    private Random random = new Random(); // For generating random credentials
    
    // Define base directory for data files
    private static final String DATA_DIR = "data";
    
    public receptionist_dashboard(String userName) {
        this.currentUser = userName;
        
        setContentPane(mainPanel);
        setTitle("Receptionist Dashboard - " + userName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        welcomeLabel.setText("Welcome, " + userName + " (Receptionist)");
        
        loadSubjects(); // Load subjects first
        initializeComponents();
        setupEventListeners();
        loadStudentData();
        loadProfile();
        updateNextPaymentNumber();
    }
    
    private void loadSubjects() {
        subjectMap.clear();
        levelSubjectMap.clear();
        
        String currentDir = System.getProperty("user.dir");
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(currentDir, DATA_DIR, "subject.txt").toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String subjectId = parts[0].trim();
                    String subjectName = parts[1].trim();
                    String level = parts[2].trim();
                    
                    subjectMap.put(subjectId, subjectName);
                    levelSubjectMap.put(subjectId, level);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading subjects: " + e.getMessage());
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
    }
    
    private void updateSubjectsList() {
        String selectedLevel = (String) levelCombo.getSelectedItem();
        List<String> availableSubjects = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : levelSubjectMap.entrySet()) {
            if (entry.getValue().equals(selectedLevel)) {
                String subjectId = entry.getKey();
                String subjectName = subjectMap.get(subjectId);
                availableSubjects.add(subjectName + " (" + subjectId + ")");
            }
        }
        
        subjectsList.setListData(availableSubjects.toArray(new String[0]));
        subjectsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
        String currentDir = System.getProperty("user.dir");
        try (FileWriter writer = new FileWriter(Paths.get(currentDir, DATA_DIR, "students.txt").toString(), true)) {
            // Format: studentId,name,icPassport,email,contact,address,enrollmentDate,level,status
            writer.write(studentId + "," + name + "," + icPassport + "," + email + "," + 
                        contact + "," + address + "," + enrollmentDate + "," + level + "," + status + "\n");
            
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
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving student data: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateNextStudentId() {
        // Find the highest existing student number
        String currentDir = System.getProperty("user.dir");
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(currentDir, DATA_DIR, "students.txt").toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].startsWith("S")) {
                    try {
                        int num = Integer.parseInt(parts[0].substring(1));
                        if (num >= nextStudentNumber) {
                            nextStudentNumber = num + 1;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, start with S001
        }
        
        return String.format("S%03d", nextStudentNumber++);
    }
    
    private void saveStudentSubjects(String studentId, List<String> selectedSubjects) {
        String currentDir = System.getProperty("user.dir");
        try (FileWriter writer = new FileWriter(Paths.get(currentDir, DATA_DIR, "student_subjects.txt").toString(), true)) {
            for (String subjectDisplay : selectedSubjects) {
                // Extract subject ID from display string like "Math (SUB001)"
                String subjectId = extractSubjectId(subjectDisplay);
                if (!subjectId.isEmpty()) {
                    writer.write(studentId + "," + subjectId + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving student subjects: " + e.getMessage());
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
        String currentDir = System.getProperty("user.dir");
        
        // Read all existing student-subject relationships
        List<String> allLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(currentDir, DATA_DIR, "student_subjects.txt").toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && !parts[0].trim().equals(studentId)) {
                    allLines.add(line); // Keep other students' subjects
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading student subjects: " + e.getMessage());
        }
        
        // Add new subjects for this student
        for (String subjectDisplay : selectedSubjects) {
            String subjectId = extractSubjectId(subjectDisplay);
            if (!subjectId.isEmpty()) {
                allLines.add(studentId + "," + subjectId);
            }
        }
        
        // Write back to file
        try (FileWriter writer = new FileWriter(Paths.get(currentDir, DATA_DIR, "student_subjects.txt").toString(), false)) {
            for (String line : allLines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error updating student subjects: " + e.getMessage());
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
        String currentDir = System.getProperty("user.dir");
        List<String> remainingLines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(currentDir, DATA_DIR, "student_subjects.txt").toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && !parts[0].trim().equals(studentId)) {
                    remainingLines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading student subjects: " + e.getMessage());
        }
        
        try (FileWriter writer = new FileWriter(Paths.get(currentDir, DATA_DIR, "student_subjects.txt").toString(), false)) {
            for (String line : remainingLines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error updating student subjects: " + e.getMessage());
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
            String currentDir = System.getProperty("user.dir");
            try (FileWriter writer = new FileWriter(Paths.get(currentDir, DATA_DIR, "payments.txt").toString(), true)) {
                // Format: paymentId,studentId,description,amount,paymentMethod,timestamp,period,receiptId,status
                writer.write(paymentId + "," + studentId + ",," + amount + "," + paymentMethod + "," + 
                           timestamp + "," + period + "," + receiptId + ",Completed\n");
                
                JOptionPane.showMessageDialog(this, "Payment accepted successfully!\nPayment ID: " + paymentId, 
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
    
    private String generateNextPaymentId() {
        return String.format("PAY%03d", nextPaymentNumber++);
    }
    
    private void updateNextPaymentNumber() {
        String currentDir = System.getProperty("user.dir");
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(currentDir, DATA_DIR, "payments.txt").toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].startsWith("PAY")) {
                    try {
                        int num = Integer.parseInt(parts[0].substring(3));
                        if (num >= nextPaymentNumber) {
                            nextPaymentNumber = num + 1;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, start with PAY001
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
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String receiptId = "RCP" + String.format("%03d", nextPaymentNumber);
            
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
        String currentDir = System.getProperty("user.dir");
        
        // Generate random credentials
        String username = generateRandomUsername(studentName);
        String password = generateRandomPassword();
        String role = "student";
        
        try (FileWriter writer = new FileWriter(Paths.get(currentDir, DATA_DIR, "users.txt").toString(), true)) {
            // Format: userId,username,password,role,name
            writer.write(studentId + "," + username + "," + password + "," + role + "," + studentName + "\n");
            
        } catch (IOException e) {
            System.err.println("Error creating user account for student: " + e.getMessage());
        }
        
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
        String currentDir = System.getProperty("user.dir");
        File file = new File(Paths.get(currentDir, DATA_DIR, "students.txt").toString());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
        // Load existing student data
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(currentDir, DATA_DIR, "students.txt").toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
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
                    
                    // Update next student number
                    try {
                        if (studentId.startsWith("S")) {
                            int num = Integer.parseInt(studentId.substring(1));
                            if (num >= nextStudentNumber) {
                                nextStudentNumber = num + 1;
                            }
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
    
    private String loadStudentSubjects(String studentId) {
        List<String> subjects = new ArrayList<>();
        String currentDir = System.getProperty("user.dir");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(currentDir, DATA_DIR, "student_subjects.txt").toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].trim().equals(studentId)) {
                    String subjectId = parts[1].trim();
                    String subjectName = subjectMap.get(subjectId);
                    if (subjectName != null) {
                        subjects.add(subjectName);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading student subjects: " + e.getMessage());
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
        String currentDir = System.getProperty("user.dir");
        try (FileWriter writer = new FileWriter(Paths.get(currentDir, DATA_DIR, "students.txt").toString(), false)) {
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
                             tableModel.getValueAt(i, 8) + "\n";   // Status
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