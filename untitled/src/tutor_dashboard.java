import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class tutor_dashboard extends JFrame {
    private JPanel contentPane;
    private JLabel welcomeLabel;
    private JButton logoutButton;
    private JTabbedPane tabbedPane;
    
    // Profile Tab
    private JPanel profilePanel;
    private JTextField tutorIdField;
    private JTextField nameField;
    private JTextField icPassportField;
    private JTextField emailField;
    private JTextField contactField;
    private JTextField addressField;
    private JButton updateProfileButton;
    
    // Classes Tab
    private JPanel classesPanel;
    private JTable classesTable;
    private JButton addClassButton;
    private JButton updateClassButton;
    private JButton deleteClassButton;
    
    // Students Tab
    private JPanel studentsPanel;
    private JTable studentsTable;

    private Tutor currentTutor;
    private DefaultTableModel classesTableModel;
    private DefaultTableModel studentsTableModel;
    private static final String DATA_DIR = "data";

    public tutor_dashboard(Tutor tutor) {
        this.currentTutor = tutor;
        initializeGUI();
        loadAllData();
    }

    private void initializeGUI() {
        setTitle("Tutor Dashboard - " + currentTutor.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Main panel with modern background
        contentPane = new JPanel(new BorderLayout(15, 15));
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.setBackground(new Color(0xF8F9FA));
        contentPane.setOpaque(true);

        // Modern header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0x343A40));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        headerPanel.setOpaque(true);

        welcomeLabel = new JLabel("Welcome, " + currentTutor.getName() + " (Tutor)", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        logoutButton.setFocusPainted(false);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new main_page().setVisible(true));
        });

        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Modern tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(0xF8F9FA));
        tabbedPane.addTab("👤 Profile", createProfilePanel());
        tabbedPane.addTab("📚 Classes", createClassesPanel());
        tabbedPane.addTab("👥 Students", createStudentsPanel());

        contentPane.add(headerPanel, BorderLayout.NORTH);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(contentPane);

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));
        panel.setOpaque(true);

        JLabel titleLabel = new JLabel("👤 My Profile Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x343A40));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(0xF8F9FA));
        formPanel.setOpaque(true);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tutor ID field
        gbc.gridx = 0; gbc.gridy = 0;
        addFormField(formPanel, "🆔 Tutor ID:", tutorIdField = createTextField(false), gbc);

        // Name field
        gbc.gridx = 0; gbc.gridy = 1;
        addFormField(formPanel, "👤 Name:", nameField = createTextField(false), gbc);

        // IC/Passport field
        gbc.gridx = 0; gbc.gridy = 2;
        addFormField(formPanel, "📄 IC/Passport:", icPassportField = createTextField(false), gbc);

        // Email field
        gbc.gridx = 0; gbc.gridy = 3;
        addFormField(formPanel, "📧 Email:", emailField = createTextField(true), gbc);

        // Contact field
        gbc.gridx = 0; gbc.gridy = 4;
        addFormField(formPanel, "📞 Contact:", contactField = createTextField(true), gbc);

        // Address field
        gbc.gridx = 0; gbc.gridy = 5;
        addFormField(formPanel, "🏠 Address:", addressField = createTextField(true), gbc);

        // Update button
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(25, 15, 15, 15);
        updateProfileButton = new JButton("💾 Update Profile");
        updateProfileButton.setBackground(new Color(0, 123, 255));
        updateProfileButton.setForeground(Color.WHITE);
        updateProfileButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateProfileButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        updateProfileButton.setFocusPainted(false);
        updateProfileButton.setOpaque(true);
        updateProfileButton.setBorderPainted(false);
        updateProfileButton.addActionListener(e -> onUpdateProfile());
        formPanel.add(updateProfileButton, gbc);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createClassesPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));
        panel.setOpaque(true);

        JLabel titleLabel = new JLabel("📚 My Classes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x343A40));

        // Initialize table
        classesTableModel = new DefaultTableModel(
            new String[]{"Subject", "Level", "Schedule", "Students Count"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        classesTable = new JTable(classesTableModel);
        classesTable.setRowHeight(35);
        classesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        classesTable.getTableHeader().setBackground(new Color(0xF8F9FA));
        classesTable.getTableHeader().setForeground(Color.BLACK);
        classesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        classesTable.setShowGrid(true);
        classesTable.setGridColor(new Color(0xDEE2E6));
        classesTable.setSelectionBackground(new Color(0xE7F3FF));
        classesTable.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(classesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonsPanel.setBackground(new Color(0xF8F9FA));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        addClassButton = createStyledButton("➕ Add Class", new Color(40, 167, 69));
        updateClassButton = createStyledButton("✏️ Update Class", new Color(255, 193, 7));
        deleteClassButton = createStyledButton("🗑️ Delete Class", new Color(220, 53, 69));

        addClassButton.addActionListener(e -> onAddClass());
        updateClassButton.addActionListener(e -> onUpdateClass());
        deleteClassButton.addActionListener(e -> onDeleteClass());

        buttonsPanel.add(addClassButton);
        buttonsPanel.add(updateClassButton);
        buttonsPanel.add(deleteClassButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));
        panel.setOpaque(true);

        JLabel titleLabel = new JLabel("👥 My Students");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x343A40));

        // Initialize table
        studentsTableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Subject", "Level"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentsTable = new JTable(studentsTableModel);
        studentsTable.setRowHeight(35);
        studentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentsTable.getTableHeader().setBackground(new Color(0xF8F9FA));
        studentsTable.getTableHeader().setForeground(Color.BLACK);
        studentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        studentsTable.setShowGrid(true);
        studentsTable.setGridColor(new Color(0xDEE2E6));
        studentsTable.setSelectionBackground(new Color(0xE7F3FF));
        studentsTable.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }

    private void loadAllData() {
        loadProfileData();
        loadClassesData();
        loadStudentsData();
    }

    private void loadProfileData() {
        try {
            File tutorsFile = new File(DATA_DIR + "/tutors.txt");
            if (tutorsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(tutorsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 7 && parts[0].trim().equals("T001")) { // Using T001 as the tutor ID
                        // Format the fields with proper labels
                        tutorIdField.setText(parts[0].trim());
                        nameField.setText(parts[1].trim());
                        icPassportField.setText(parts[2].trim());
                        emailField.setText(parts[3].trim());
                        contactField.setText(parts[4].trim());
                        addressField.setText(parts[5].trim());

                        // Update labels with icons and better formatting
                        updateFormLabels();

                        // Update the welcome label with the tutor's ID and name
                        welcomeLabel.setText(String.format("Welcome, %s (%s)", parts[1].trim(), parts[0].trim()));
                        
                        // Update current tutor object with latest data
                        currentTutor.setName(parts[1].trim());
                        currentTutor.setEmail(parts[3].trim());
                        currentTutor.setContactNumber(parts[4].trim());

                        // Set window title with tutor ID and name
                        setTitle(String.format("Tutor Dashboard - %s (%s)", parts[1].trim(), parts[0].trim()));
                        break;
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            showError("Error loading profile", e.getMessage());
        }
    }

    private void updateFormLabels() {
        // Update the form field labels with better formatting
        for (Component comp : profilePanel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                String text = label.getText();
                if (text.contains("Tutor ID:")) {
                    label.setText("🆔 Tutor ID & Name:");
                } else if (text.contains("Name:")) {
                    label.setText("👤 Full Name:");
                } else if (text.contains("IC/Passport:")) {
                    label.setText("📄 IC/Passport No:");
                } else if (text.contains("Email:")) {
                    label.setText("📧 Email Address:");
                } else if (text.contains("Contact:")) {
                    label.setText("📞 Contact Number:");
                } else if (text.contains("Address:")) {
                    label.setText("🏠 Home Address:");
                }
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setForeground(new Color(0x343A40));
            }
        }
    }

    private void loadClassesData() {
        classesTableModel.setRowCount(0);
        try {
            File classesFile = new File(DATA_DIR + "/classes.txt");
            if (classesFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(classesFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4 && parts[3].trim().equals(currentTutor.getUsername())) {
                        Vector<String> row = new Vector<>();
                        row.add(parts[0].trim()); // Subject
                        row.add(parts[1].trim()); // Level
                        row.add(parts[2].trim()); // Schedule
                        row.add(parts[4].trim()); // Number of students
                        classesTableModel.addRow(row);
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            showError("Error loading classes", e.getMessage());
        }
    }

    private void loadStudentsData() {
        studentsTableModel.setRowCount(0);
        try {
            File enrollmentFile = new File(DATA_DIR + "/student_subjects.txt");
            if (enrollmentFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(enrollmentFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4 && parts[3].trim().equals(currentTutor.getUsername())) {
                        Vector<String> row = new Vector<>();
                        row.add(parts[0].trim()); // Student ID
                        row.add(getStudentName(parts[0].trim())); // Student Name
                        row.add(parts[1].trim()); // Subject
                        row.add(parts[2].trim()); // Level
                        studentsTableModel.addRow(row);
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            showError("Error loading students", e.getMessage());
        }
    }

    private String getStudentName(String studentId) {
        try {
            File usersFile = new File(DATA_DIR + "/users.txt");
            if (usersFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(usersFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5 && parts[0].trim().equals(studentId)) {
                        reader.close();
                        return parts[4].trim(); // Return student name
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private void onUpdateProfile() {
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressField.getText().trim();
        
        if (email.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            showError("Validation Error", "Please fill in all fields.");
            return;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Validation Error", "Please enter a valid email address.");
            return;
        }

        // Validate contact format (allow +, -, spaces, parentheses, and numbers)
        if (!contact.matches("^[0-9+\\-\\s()]+$")) {
            showError("Validation Error", "Please enter a valid contact number.");
            return;
        }

        try {
            List<String> lines = new ArrayList<>();
            File tutorsFile = new File(DATA_DIR + "/tutors.txt");
            BufferedReader reader = new BufferedReader(new FileReader(tutorsFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7 && parts[0].trim().equals("T001")) {
                    // Preserve ID, name, IC/passport, and status while updating email, contact, and address
                    lines.add(String.format("%s,%s,%s,%s,%s,%s,%s",
                            parts[0].trim(), parts[1].trim(), parts[2].trim(),
                            email, contact, address, parts[6].trim()));
                } else {
                    lines.add(line);
                }
            }
            reader.close();

            FileWriter writer = new FileWriter(tutorsFile);
            for (String updatedLine : lines) {
                writer.write(updatedLine + "\n");
            }
            writer.close();

            // Update current tutor object
            currentTutor.setEmail(email);
            currentTutor.setContactNumber(contact);
            
            showSuccess("Profile updated successfully!");
        } catch (IOException e) {
            showError("Error updating profile", e.getMessage());
        }
    }

    private void onAddClass() {
        JComboBox<String> subjectBox = new JComboBox<>(currentTutor.getSubjects().toArray(new String[0]));
        JComboBox<String> levelBox = new JComboBox<>(currentTutor.getLevels().toArray(new String[0]));
        JTextField scheduleField = new JTextField();
        scheduleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        Object[] message = {
            "📚 Subject:", subjectBox,
            "🎓 Level:", levelBox,
            "📅 Schedule:", scheduleField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Class",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String subject = (String) subjectBox.getSelectedItem();
            String level = (String) levelBox.getSelectedItem();
            String schedule = scheduleField.getText().trim();

            if (subject == null || level == null || schedule.isEmpty()) {
                showError("Validation Error", "All fields are required!");
                return;
            }

            try {
                FileWriter writer = new FileWriter(DATA_DIR + "/classes.txt", true);
                writer.write(String.format("%s,%s,%s,%s,0\n",
                        subject, level, schedule, currentTutor.getUsername()));
                writer.close();

                loadClassesData();
                showSuccess("Class added successfully!");
            } catch (IOException e) {
                showError("Error adding class", e.getMessage());
            }
        }
    }

    private void onUpdateClass() {
        int selectedRow = classesTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Selection Error", "Please select a class to update");
            return;
        }

        String subject = (String) classesTable.getValueAt(selectedRow, 0);
        String level = (String) classesTable.getValueAt(selectedRow, 1);
        String schedule = (String) classesTable.getValueAt(selectedRow, 2);

        JTextField scheduleField = new JTextField(schedule);
        scheduleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        Object[] message = {
            "📚 Subject: " + subject,
            "🎓 Level: " + level,
            "📅 New Schedule:", scheduleField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Class",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String newSchedule = scheduleField.getText().trim();

            if (newSchedule.isEmpty()) {
                showError("Validation Error", "Schedule cannot be empty!");
                return;
            }

            try {
                List<String> lines = new ArrayList<>();
                File classesFile = new File(DATA_DIR + "/classes.txt");
                BufferedReader reader = new BufferedReader(new FileReader(classesFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5 && parts[0].trim().equals(subject) &&
                            parts[1].trim().equals(level) &&
                            parts[3].trim().equals(currentTutor.getUsername())) {
                        lines.add(String.format("%s,%s,%s,%s,%s\n",
                                parts[0].trim(), parts[1].trim(), newSchedule,
                                parts[3].trim(), parts[4].trim()));
                    } else {
                        lines.add(line + "\n");
                    }
                }
                reader.close();

                FileWriter writer = new FileWriter(classesFile);
                for (String updatedLine : lines) {
                    writer.write(updatedLine);
                }
                writer.close();

                loadClassesData();
                showSuccess("Class updated successfully!");
            } catch (IOException e) {
                showError("Error updating class", e.getMessage());
            }
        }
    }

    private void onDeleteClass() {
        int selectedRow = classesTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Selection Error", "Please select a class to delete");
            return;
        }

        String subject = (String) classesTable.getValueAt(selectedRow, 0);
        String level = (String) classesTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this class?\n" + subject + " (" + level + ")",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                List<String> lines = new ArrayList<>();
                File classesFile = new File(DATA_DIR + "/classes.txt");
                BufferedReader reader = new BufferedReader(new FileReader(classesFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (!(parts.length >= 4 && parts[0].trim().equals(subject) &&
                            parts[1].trim().equals(level) &&
                            parts[3].trim().equals(currentTutor.getUsername()))) {
                        lines.add(line + "\n");
                    }
                }
                reader.close();

                FileWriter writer = new FileWriter(classesFile);
                for (String remainingLine : lines) {
                    writer.write(remainingLine);
                }
                writer.close();

                loadClassesData();
                showSuccess("Class deleted successfully!");
            } catch (IOException e) {
                showError("Error deleting class", e.getMessage());
            }
        }
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private JTextField createTextField(boolean editable) {
        JTextField field = new JTextField(25);
        field.setEditable(editable);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDEE2E6)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return field;
    }

    private void addFormField(JPanel panel, String labelText, JTextField field, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(0x343A40));
        
        gbc.gridx = 0;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        field.setPreferredSize(new Dimension(300, 35)); // Set a fixed width for better alignment
        panel.add(field, gbc);
    }

    public static void main(String[] args) {
        // Create a test tutor for demonstration
        Tutor testTutor = new Tutor("testTutor", "password", "Test Tutor", "test@email.com", "123456789");

        // Run the application
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            tutor_dashboard frame = new tutor_dashboard(testTutor);
            frame.setVisible(true);
        });
    }
}   
