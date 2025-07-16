import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

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
    private DefaultTableModel scheduleTableModel;
    private DefaultTableModel subjectsTableModel;
    private JTable scheduleTable;
    private JTable subjectsTable;
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(logoutButton);

        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Modern tabbed pane with reordered tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(0xF8F9FA));
        tabbedPane.addTab("📅 Schedule", createSchedulePanel());
        tabbedPane.addTab("📚 Classes", createClassesPanel());
        tabbedPane.addTab("📖 Subjects", createSubjectsPanel());
        tabbedPane.addTab("👤 Profile", createProfilePanel());

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
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(0xF8F9FA));

        // Profile Info Panel (left)
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBorder(BorderFactory.createTitledBorder("Profile Information"));
        profilePanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tutor ID field
        gbc.gridx = 0; gbc.gridy = 0;
        addFormField(profilePanel, "🆔 Tutor ID:", tutorIdField = createTextField(false), gbc);
        // Name field
        gbc.gridx = 0; gbc.gridy = 1;
        addFormField(profilePanel, "👤 Name:", nameField = createTextField(false), gbc);
        // IC/Passport field
        gbc.gridx = 0; gbc.gridy = 2;
        addFormField(profilePanel, "📄 IC/Passport:", icPassportField = createTextField(false), gbc);
        // Email field
        gbc.gridx = 0; gbc.gridy = 3;
        addFormField(profilePanel, "📧 Email:", emailField = createTextField(true), gbc);
        // Contact field
        gbc.gridx = 0; gbc.gridy = 4;
        addFormField(profilePanel, "📞 Contact:", contactField = createTextField(true), gbc);
        // Address field
        gbc.gridx = 0; gbc.gridy = 5;
        addFormField(profilePanel, "🏠 Address:", addressField = createTextField(true), gbc);
        // Update button
        gbc.gridx = 0; gbc.gridy = 6;
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
        profilePanel.add(updateProfileButton, gbc);

        // Change Password Panel (right)
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.setBorder(BorderFactory.createTitledBorder("Change Password"));
        passwordPanel.setBackground(Color.WHITE);
        GridBagConstraints pgbc = new GridBagConstraints();
        pgbc.insets = new Insets(15, 15, 15, 15);
        pgbc.anchor = GridBagConstraints.WEST;
        pgbc.fill = GridBagConstraints.HORIZONTAL;

        // Current Password
        pgbc.gridx = 0; pgbc.gridy = 0;
        passwordPanel.add(new JLabel("Current Password:"), pgbc);
        JPasswordField currentPasswordField = new JPasswordField(20);
        pgbc.gridx = 1;
        passwordPanel.add(currentPasswordField, pgbc);

        // New Password
        pgbc.gridx = 0; pgbc.gridy = 1;
        passwordPanel.add(new JLabel("New Password:"), pgbc);
        JPasswordField newPasswordField = new JPasswordField(20);
        pgbc.gridx = 1;
        passwordPanel.add(newPasswordField, pgbc);

        // Confirm Password
        pgbc.gridx = 0; pgbc.gridy = 2;
        passwordPanel.add(new JLabel("Confirm Password:"), pgbc);
        JPasswordField confirmPasswordField = new JPasswordField(20);
        pgbc.gridx = 1;
        passwordPanel.add(confirmPasswordField, pgbc);

        // Password note
        pgbc.gridx = 0; pgbc.gridy = 3; pgbc.gridwidth = 2;
        JLabel noteLabel = new JLabel("Password must be at least 6 characters long");
        noteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        noteLabel.setForeground(Color.GRAY);
        passwordPanel.add(noteLabel, pgbc);

        // Change Password Button
        pgbc.gridx = 0; pgbc.gridy = 4; pgbc.gridwidth = 2;
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setBackground(new Color(220, 53, 69));
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        changePasswordButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        changePasswordButton.setFocusPainted(false);
        changePasswordButton.setOpaque(true);
        changePasswordButton.setBorderPainted(false);
        passwordPanel.add(changePasswordButton, pgbc);

        // Add action listener for password change
        changePasswordButton.addActionListener(e -> {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            if (!currentPassword.equals(currentTutor.getPassword())) {
                showError("Password Error", "Current password is incorrect.");
                return;
            }
            if (newPassword.length() < 6) {
                showError("Password Error", "New password must be at least 6 characters long.");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                showError("Password Error", "New password and confirm password do not match.");
                return;
            }
            // Update password in tutors.txt
            try {
                List<String> lines = new ArrayList<>();
                File tutorsFile = new File(DATA_DIR + "/tutors.txt");
                BufferedReader reader = new BufferedReader(new FileReader(tutorsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 7 && parts[0].trim().equals(currentTutor.getUsername())) {
                        // Update password (assuming password is at index 2)
                        lines.add(String.format("%s,%s,%s,%s,%s,%s,%s",
                                parts[0].trim(), parts[1].trim(), newPassword,
                                parts[3].trim(), parts[4].trim(), parts[5].trim(), parts[6].trim()));
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
                currentTutor.setPassword(newPassword);
                showSuccess("Password changed successfully!");
                currentPasswordField.setText("");
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            } catch (IOException ex) {
                showError("Error updating password", ex.getMessage());
            }
        });

        mainPanel.add(profilePanel);
        mainPanel.add(passwordPanel);
        return mainPanel;
    }

    private JPanel createClassesPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));
        panel.setOpaque(true);

        JLabel classesTitle = new JLabel("📚 My Classes");
        classesTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        classesTitle.setForeground(new Color(0x343A40));

        // Initialize classes table with updated columns
        classesTableModel = new DefaultTableModel(
                new String[]{"Class ID", "Subject ID", "Subject", "Level", "Fee"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        classesTable = new JTable(classesTableModel);
        styleTable(classesTable);

        // Set preferred column widths
        classesTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Class ID
        classesTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Subject ID
        classesTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Subject
        classesTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Level
        classesTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Fee

        JScrollPane classesScrollPane = new JScrollPane(classesTable);
        classesScrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(classesTitle, BorderLayout.NORTH);
        panel.add(classesScrollPane, BorderLayout.CENTER);
        // No buttons panel
        return panel;
    }

    private JPanel createSubjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));
        panel.setOpaque(true);

        JLabel subjectsTitle = new JLabel("📖 Available Subjects");
        subjectsTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        subjectsTitle.setForeground(new Color(0x343A40));

        // Initialize subjects table with Tutor ID column
        subjectsTableModel = new DefaultTableModel(
                new String[]{"Subject ID", "Subject", "Level", "Tutor ID"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        subjectsTable = new JTable(subjectsTableModel);
        styleTable(subjectsTable);

        // Set preferred column widths
        subjectsTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Subject ID
        subjectsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Subject
        subjectsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Level
        subjectsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Tutor ID

        JScrollPane subjectsScrollPane = new JScrollPane(subjectsTable);
        subjectsScrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(subjectsTitle, BorderLayout.NORTH);
        panel.add(subjectsScrollPane, BorderLayout.CENTER);

        // Load subjects data
        loadSubjectsData(subjectsTableModel);

        return panel;
    }

    private void loadSubjectsData(DefaultTableModel subjectsTableModel) {
        subjectsTableModel.setRowCount(0);
        try {
            // First, load classes data into a map for tutor lookup
            Map<String, String> tutorMap = new HashMap<>();
            File classesFile = new File(DATA_DIR + "/classes.txt");
            if (classesFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(classesFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        // Store tutor ID for each subject ID
                        tutorMap.put(parts[1].trim(), parts[2].trim());
                    }
                }
                reader.close();
            }

            // Now load and display subjects with tutor information
            File subjectsFile = new File(DATA_DIR + "/subject.txt");
            if (subjectsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(subjectsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        Vector<String> row = new Vector<>();
                        String subjectId = parts[0].trim();

                        row.add(subjectId); // Subject ID
                        row.add(parts[1].trim()); // Subject
                        row.add(parts[2].trim()); // Level

                        // Add tutor ID from the map, or "-" if not assigned
                        String tutorId = tutorMap.get(subjectId);
                        row.add(tutorId != null ? tutorId : "-");

                        subjectsTableModel.addRow(row);
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            showError("Error loading subjects", e.getMessage());
        }
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(new Color(0xF8F9FA));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setShowGrid(true);
        table.setGridColor(new Color(0xDEE2E6));
        table.setSelectionBackground(new Color(0xE7F3FF));
        table.setBackground(Color.WHITE);
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));
        panel.setOpaque(true);

        JLabel scheduleTitle = new JLabel("📅 My Class Schedule");
        scheduleTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        scheduleTitle.setForeground(new Color(0x343A40));

        // Initialize schedule table with updated columns
<<<<<<< Updated upstream
        DefaultTableModel scheduleTableModel = new DefaultTableModel(
            new String[]{"Subject", "Level", "Day", "Start Time", "End Time", "Room", "Namelist"}, 0
=======
        scheduleTableModel = new DefaultTableModel(
                new String[]{"Subject", "Level", "Day", "Start Time", "End Time", "Room", "Namelist", "scheduleId", "classId"}, 0
>>>>>>> Stashed changes
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scheduleTable = new JTable(scheduleTableModel);
        styleTable(scheduleTable);

        // Set preferred column widths
        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Subject
        scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Level
        scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Day
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Start Time
        scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(100); // End Time
        scheduleTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Room
        scheduleTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Namelist button
<<<<<<< Updated upstream
=======
        // Hide the last two columns (scheduleId, classId)
        scheduleTable.getColumnModel().getColumn(7).setMinWidth(0);
        scheduleTable.getColumnModel().getColumn(7).setMaxWidth(0);
        scheduleTable.getColumnModel().getColumn(7).setWidth(0);
        scheduleTable.getColumnModel().getColumn(8).setMinWidth(0);
        scheduleTable.getColumnModel().getColumn(8).setMaxWidth(0);
        scheduleTable.getColumnModel().getColumn(8).setWidth(0);
>>>>>>> Stashed changes

        // Add button renderer for the Namelist column
        scheduleTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
<<<<<<< Updated upstream
                    boolean isSelected, boolean hasFocus, int row, int column) {
=======
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
>>>>>>> Stashed changes
                JButton button = new JButton(value != null ? value.toString() : "");
                button.setBackground(new Color(40, 167, 69)); // Green color
                button.setForeground(Color.WHITE);
                button.setFont(new Font("Segoe UI", Font.BOLD, 12));
                button.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                button.setFocusPainted(false);
                return button;
            }
        });

        // Add mouse listener for button clicks
        scheduleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = scheduleTable.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / scheduleTable.getRowHeight();
<<<<<<< Updated upstream
                
                if (row < scheduleTable.getRowCount() && row >= 0 && 
                    column == 6 && e.getClickCount() == 1) {
=======

                if (row < scheduleTable.getRowCount() && row >= 0 &&
                        column == 6 && e.getClickCount() == 1) {
>>>>>>> Stashed changes
                    String subject = (String) scheduleTable.getValueAt(row, 0);
                    String level = (String) scheduleTable.getValueAt(row, 1);
                    showStudentsDialog(subject, level);
                }
            }
        });

        JScrollPane scheduleScrollPane = new JScrollPane(scheduleTable);
        scheduleScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Add update button
        JButton updateScheduleButton = createStyledButton("✏️ Update Schedule", new Color(255, 193, 7));
        updateScheduleButton.addActionListener(e -> onUpdateSchedule(scheduleTable));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(0xF8F9FA));
        buttonPanel.add(updateScheduleButton);

        panel.add(scheduleTitle, BorderLayout.NORTH);
        panel.add(scheduleScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Load schedule data
        loadScheduleData(scheduleTableModel);

        return panel;
    }

    private void showStudentsDialog(String subject, String level) {
        // Create dialog
        JDialog dialog = new JDialog(this, "Students in " + subject + " (" + level + ")", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setBackground(new Color(0xF8F9FA));

        // Create table model for students
        DefaultTableModel studentsModel = new DefaultTableModel(
<<<<<<< Updated upstream
            new String[]{"Student ID", "Name", "Level"}, 0
=======
                new String[]{"Student ID", "Name", "Level"}, 0
>>>>>>> Stashed changes
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable studentsTable = new JTable(studentsModel);
        styleTable(studentsTable);

        // Create header panel with title and total count
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(new Color(0xF8F9FA));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add title
        JLabel titleLabel = new JLabel("👥 Student Namelist");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add total count label (will be updated after loading data)
        JLabel totalCountLabel = new JLabel("");
        totalCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalCountLabel.setForeground(new Color(40, 167, 69)); // Match the button color
        headerPanel.add(totalCountLabel, BorderLayout.EAST);

        try {
            // First get the subject ID for the given subject name and level
            String subjectId = "";
            File subjectsFile = new File(DATA_DIR + "/subject.txt");
            if (subjectsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(subjectsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3 && parts[1].trim().equals(subject) && parts[2].trim().equals(level)) {
                        subjectId = parts[0].trim();
                        break;
                    }
                }
                reader.close();
            }

            if (!subjectId.isEmpty()) {
                // Get all student IDs enrolled in this subject
                Set<String> enrolledStudentIds = new HashSet<>();
                File studentSubjectsFile = new File(DATA_DIR + "/student_subjects.txt");
                if (studentSubjectsFile.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(studentSubjectsFile));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 2 && parts[1].trim().equals(subjectId)) {
                            enrolledStudentIds.add(parts[0].trim());
                        }
                    }
                    reader.close();
                }

                // Get student details and add to table
                File studentsFile = new File(DATA_DIR + "/students.txt");
                if (studentsFile.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(studentsFile));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 8 && enrolledStudentIds.contains(parts[0].trim())) {
                            Vector<String> row = new Vector<>();
                            row.add(parts[0].trim()); // Student ID
                            row.add(parts[1].trim()); // Name
                            row.add(parts[7].trim()); // Level
                            studentsModel.addRow(row);
                        }
                    }
                    reader.close();
                }

                // Update total count label
                totalCountLabel.setText("Total Students: " + studentsModel.getRowCount());
            }
        } catch (IOException e) {
            showError("Error", "Failed to load student data: " + e.getMessage());
        }

        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(0, 123, 255));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.addActionListener(e -> dialog.dispose());

        // Layout
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(studentsTable), BorderLayout.CENTER);
        dialog.add(closeButton, BorderLayout.SOUTH);

        // Set size and location
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void loadScheduleData(DefaultTableModel scheduleTableModel) {
        scheduleTableModel.setRowCount(0);
        try {
            // First, load subjects data into a map for quick lookup
            Map<String, String[]> subjectMap = new HashMap<>();
            File subjectsFile = new File(DATA_DIR + "/subject.txt");
            if (subjectsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(subjectsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        // Store subject name and level for each subject ID
                        subjectMap.put(parts[0].trim(), new String[]{parts[1].trim(), parts[2].trim()});
                    }
                }
                reader.close();
            }

            // Get all class IDs for the current tutor and map them to subject IDs
            Map<String, String> classToSubjectMap = new HashMap<>();
            File classesFile = new File(DATA_DIR + "/classes.txt");
            if (classesFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(classesFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3 && parts[2].trim().equals(currentTutor.getUsername())) {
                        classToSubjectMap.put(parts[0].trim(), parts[1].trim());
                    }
                }
                reader.close();
            }

            // Now load schedule data and join with subjects info
            File scheduleFile = new File(DATA_DIR + "/schedule.txt");
            if (scheduleFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(scheduleFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        String classId = parts[1].trim();
                        String subjectId = classToSubjectMap.get(classId);
<<<<<<< Updated upstream
                        
=======

>>>>>>> Stashed changes
                        if (subjectId != null) {
                            String[] subjectInfo = subjectMap.get(subjectId);
                            if (subjectInfo != null) {
                                // Count students for this subject
                                int studentCount = 0;
                                BufferedReader studentReader = new BufferedReader(new FileReader(DATA_DIR + "/student_subjects.txt"));
                                String studentLine;
                                while ((studentLine = studentReader.readLine()) != null) {
                                    String[] studentParts = studentLine.split(",");
                                    if (studentParts.length >= 2 && studentParts[1].trim().equals(subjectId)) {
                                        studentCount++;
                                    }
                                }
                                studentReader.close();

                                Vector<String> row = new Vector<>();
                                row.add(subjectInfo[0]); // Subject name
                                row.add(subjectInfo[1]); // Level
                                row.add(parts[2].trim()); // Day
                                row.add(parts[3].trim()); // Start Time
                                row.add(parts[4].trim()); // End Time
                                row.add(parts[5].trim()); // Room
                                row.add("👥 Namelist (" + studentCount + ")"); // Namelist button
<<<<<<< Updated upstream
=======
                                row.add(parts[0].trim()); // scheduleId (hidden)
                                row.add(classId); // classId (hidden)
>>>>>>> Stashed changes
                                scheduleTableModel.addRow(row);
                            }
                        }
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            showError("Error loading schedule", e.getMessage());
        }
    }

    private void loadAllData() {
        loadProfileData();
        loadClassesData();
        loadScheduleData(scheduleTableModel);
        loadSubjectsData(subjectsTableModel);
        if (classesTable != null) classesTable.revalidate();
        if (scheduleTable != null) scheduleTable.revalidate();
        if (subjectsTable != null) subjectsTable.revalidate();
    }

    private void loadProfileData() {
        try {
            for (String line : function.readTutors()) {
                String[] parts = line.split(",");
                if (parts.length >= 7 && parts[0].trim().equals(currentTutor.getUsername())) {
                    tutorIdField.setText(parts[0].trim());
                    nameField.setText(parts[1].trim());
                    icPassportField.setText(parts[2].trim());
                    emailField.setText(parts[3].trim());
                    contactField.setText(parts[4].trim());
                    addressField.setText(parts[5].trim());
                    updateFormLabels();
                    welcomeLabel.setText(String.format("Welcome, %s (%s)", parts[1].trim(), parts[0].trim()));
                    currentTutor.setName(parts[1].trim());
                    currentTutor.setEmail(parts[3].trim());
                    currentTutor.setContactNumber(parts[4].trim());
                    setTitle(String.format("Tutor Dashboard - %s (%s)", parts[1].trim(), parts[0].trim()));
                    break;
                }
            }
        } catch (Exception e) {
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
                    label.setText("🆔 Tutor ID:");
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
            // First, load subjects data into a map for quick lookup
            Map<String, String[]> subjectMap = new HashMap<>();
            File subjectsFile = new File(DATA_DIR + "/subject.txt");
            if (subjectsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(subjectsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        // Store subject name and level for each subject ID
                        subjectMap.put(parts[0].trim(), new String[]{parts[1].trim(), parts[2].trim()});
                    }
                }
                reader.close();
            }

            // Now load classes data and join with subjects info
            File classesFile = new File(DATA_DIR + "/classes.txt");
            if (classesFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(classesFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4 && parts[2].trim().equals(currentTutor.getUsername())) {
                        Vector<String> row = new Vector<>();
                        String subjectId = parts[1].trim();

                        row.add(parts[0].trim()); // Class ID
                        row.add(subjectId); // Subject ID

                        // Add subject name and level from the subject map
                        String[] subjectInfo = subjectMap.get(subjectId);
                        if (subjectInfo != null) {
                            row.add(subjectInfo[0]); // Subject
                            row.add(subjectInfo[1]); // Level
                        } else {
                            row.add("Unknown"); // Subject
                            row.add("Unknown"); // Level
                        }

                        row.add("RM " + parts[3].trim()); // Fee with RM prefix
                        classesTableModel.addRow(row);
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            showError("Error loading classes", e.getMessage());
        }
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
            String oldData = null, newData = null;
            for (String line : function.readTutors()) {
                String[] parts = line.split(",");
                if (parts.length >= 7 && parts[0].trim().equals(currentTutor.getUsername())) {
                    oldData = line;
                    newData = String.format("%s,%s,%s,%s,%s,%s,%s",
                            parts[0].trim(), parts[1].trim(), parts[2].trim(),
                            email, contact, address, parts[6].trim());
                    break;
                }
            }
            if (oldData != null && newData != null && function.updateTutor(oldData, newData)) {
                currentTutor.setEmail(email);
                currentTutor.setContactNumber(contact);
                showSuccess("Profile updated successfully!");
            } else {
                showError("Error updating profile", "Could not update tutor data.");
            }
        } catch (Exception e) {
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

    private void onUpdateSchedule(JTable scheduleTable) {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Selection Error", "Please select a schedule to update");
            return;
        }

        String scheduleId = (String) scheduleTable.getValueAt(selectedRow, 7);
        String classId = (String) scheduleTable.getValueAt(selectedRow, 8);
        String currentDay = (String) scheduleTable.getValueAt(selectedRow, 2);
        String currentStartTime = (String) scheduleTable.getValueAt(selectedRow, 3);
        String currentEndTime = (String) scheduleTable.getValueAt(selectedRow, 4);
        String currentRoom = (String) scheduleTable.getValueAt(selectedRow, 5);

        // Create day selection combo box
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        JComboBox<String> dayBox = new JComboBox<>(days);
        dayBox.setSelectedItem(currentDay);

        // Create time input fields with current values
        JTextField startTimeField = new JTextField(currentStartTime);
        JTextField endTimeField = new JTextField(currentEndTime);
        JTextField roomField = new JTextField(currentRoom);

        // Style the components
        dayBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startTimeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        endTimeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roomField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Create the dialog components
        Object[] message = {
                "📅 Day:", dayBox,
                "🕒 Start Time (HH:mm):", startTimeField,
                "🕒 End Time (HH:mm):", endTimeField,
                "🏫 Room:", roomField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Schedule",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String newDay = (String) dayBox.getSelectedItem();
            String newStartTime = startTimeField.getText().trim();
            String newEndTime = endTimeField.getText().trim();
            String newRoom = roomField.getText().trim();

            // Validate inputs
            if (newStartTime.isEmpty() || newEndTime.isEmpty() || newRoom.isEmpty()) {
                showError("Validation Error", "All fields are required!");
                return;
            }

            // Validate time format (HH:mm)
            if (!newStartTime.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$") ||
                    !newEndTime.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                showError("Validation Error", "Please enter time in HH:mm format (e.g., 09:00)");
                return;
            }

            try {
                String oldData = null, newData = null;
                for (String line : function.readSchedules()) {
                    String[] parts = line.split(",");
                    if (parts[0].trim().equals(scheduleId)) {
                        oldData = line;
                        newData = String.format("%s,%s,%s,%s,%s,%s",
                                scheduleId, classId, newDay, newStartTime, newEndTime, newRoom);
                        break;
                    }
                }
                if (oldData != null && newData != null && function.updateSchedule(oldData, newData)) {
                    loadScheduleData((DefaultTableModel) scheduleTable.getModel());
                    showSuccess("Schedule updated successfully!");
                } else {
                    showError("Error updating schedule", "Could not update schedule data.");
                }
            } catch (Exception e) {
                showError("Error updating schedule", e.getMessage());
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Create a test tutor for demonstration
                Tutor testTutor = new Tutor("T001", "tutor123", "Yin Yin", "yinyin@email.com", "+60123456789");

                tutor_dashboard frame = new tutor_dashboard(testTutor);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}