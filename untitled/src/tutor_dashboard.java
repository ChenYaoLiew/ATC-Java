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
    private DefaultTableModel attendanceTableModel;
    private DefaultTableModel messagesTableModel;
    private JTable scheduleTable;
    private JTable subjectsTable;
    private JTable attendanceTable;
    private JTable messagesTable;
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
        tabbedPane.addTab("✅ Attendance", createAttendancePanel());
        tabbedPane.addTab("💬 Messages", createMessagingPanel());
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
        scheduleTableModel = new DefaultTableModel(
                new String[]{"Subject", "Level", "Day", "Start Time", "End Time", "Room", "Namelist", "scheduleId", "classId"}, 0
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
        // Hide the last two columns (scheduleId, classId)
        scheduleTable.getColumnModel().getColumn(7).setMinWidth(0);
        scheduleTable.getColumnModel().getColumn(7).setMaxWidth(0);
        scheduleTable.getColumnModel().getColumn(7).setWidth(0);
        scheduleTable.getColumnModel().getColumn(8).setMinWidth(0);
        scheduleTable.getColumnModel().getColumn(8).setMaxWidth(0);
        scheduleTable.getColumnModel().getColumn(8).setWidth(0);

        // Add button renderer for the Namelist column
        scheduleTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
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

                if (row < scheduleTable.getRowCount() && row >= 0 &&
                        column == 6 && e.getClickCount() == 1) {
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

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));
        panel.setOpaque(true);

        JLabel attendanceTitle = new JLabel("✅ Attendance Management");
        attendanceTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        attendanceTitle.setForeground(new Color(0x343A40));

        // Control panel for class selection and date
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(new Color(0xF8F9FA));

        // Class selection combo box
        JComboBox<String> classComboBox = new JComboBox<>();
        classComboBox.setPreferredSize(new Dimension(200, 35));
        loadClassesForAttendance(classComboBox);

        // Date selection
        JTextField dateField = new JTextField(java.time.LocalDate.now().toString(), 10);
        dateField.setPreferredSize(new Dimension(120, 35));

        // Mark Attendance button
        JButton markAttendanceButton = createStyledButton("📝 Mark Attendance", new Color(40, 167, 69));
        markAttendanceButton.addActionListener(e -> {
            String selectedClass = (String) classComboBox.getSelectedItem();
            String selectedDate = dateField.getText();
            if (selectedClass != null && !selectedDate.isEmpty()) {
                showMarkAttendanceDialog(selectedClass, selectedDate);
            } else {
                showError("Error", "Please select a class and date");
            }
        });

        // View Reports button
        JButton viewReportsButton = createStyledButton("📊 View Reports", new Color(0, 123, 255));
        viewReportsButton.addActionListener(e -> showAttendanceReports());

        controlPanel.add(new JLabel("Class:"));
        controlPanel.add(classComboBox);
        controlPanel.add(new JLabel("Date:"));
        controlPanel.add(dateField);
        controlPanel.add(markAttendanceButton);
        controlPanel.add(viewReportsButton);

        // Attendance table
        attendanceTableModel = new DefaultTableModel(
                new String[]{"Date", "Class", "Student Name", "Status", "Time Marked"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attendanceTable = new JTable(attendanceTableModel);
        styleTable(attendanceTable);

        JScrollPane attendanceScrollPane = new JScrollPane(attendanceTable);
        attendanceScrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(attendanceTitle, BorderLayout.NORTH);
        panel.add(controlPanel, BorderLayout.CENTER);
        panel.add(attendanceScrollPane, BorderLayout.SOUTH);

        // Load initial attendance data
        loadAttendanceData();

        return panel;
    }

    private JPanel createMessagingPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(0xF8F9FA));
        panel.setOpaque(true);

        JLabel messagingTitle = new JLabel("💬 Messages & Communication");
        messagingTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        messagingTitle.setForeground(new Color(0x343A40));

        // Create tabbed pane for Inbox, Sent, and Compose
        JTabbedPane messagesTabbedPane = new JTabbedPane();
        messagesTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Inbox Tab
        JPanel inboxPanel = new JPanel(new BorderLayout(10, 10));
        inboxPanel.setBackground(new Color(0xF8F9FA));

        messagesTableModel = new DefaultTableModel(
                new String[]{"From", "Date/Time", "Priority", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        messagesTable = new JTable(messagesTableModel);
        styleTable(messagesTable);
        messagesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = messagesTable.getSelectedRow();
                    if (row >= 0) {
                        showMessageDialog(row);
                    }
                }
            }
        });

        JScrollPane messagesScrollPane = new JScrollPane(messagesTable);
        inboxPanel.add(messagesScrollPane, BorderLayout.CENTER);

        // Buttons for inbox
        JPanel inboxButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inboxButtonPanel.setBackground(new Color(0xF8F9FA));
        JButton refreshButton = createStyledButton("🔄 Refresh", new Color(108, 117, 125));
        refreshButton.addActionListener(e -> loadMessagesData());
        inboxButtonPanel.add(refreshButton);

        inboxPanel.add(inboxButtonPanel, BorderLayout.SOUTH);

        // Compose Tab
        JPanel composePanel = createComposeMessagePanel();

        messagesTabbedPane.addTab("📥 Inbox", inboxPanel);
        messagesTabbedPane.addTab("✉️ Compose", composePanel);

        panel.add(messagingTitle, BorderLayout.NORTH);
        panel.add(messagesTabbedPane, BorderLayout.CENTER);

        // Load initial messages data
        loadMessagesData();

        return panel;
    }

    private JPanel createComposeMessagePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0xF8F9FA));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // To field
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("To:"), gbc);
        JComboBox<String> toComboBox = new JComboBox<>();
        loadRecipientsForCompose(toComboBox);
        toComboBox.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        panel.add(toComboBox, gbc);

        // Priority field
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Priority:"), gbc);
        JComboBox<String> priorityComboBox = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        priorityComboBox.setPreferredSize(new Dimension(300, 35));
        priorityComboBox.setSelectedItem("Medium");
        gbc.gridx = 1;
        panel.add(priorityComboBox, gbc);

        // Message content area
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Message:"), gbc);
        JTextArea messageArea = new JTextArea(10, 25);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setPreferredSize(new Dimension(300, 200));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(messageScrollPane, gbc);

        // Send button
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        JButton sendButton = createStyledButton("📤 Send Message", new Color(40, 167, 69));
        sendButton.addActionListener(e -> {
            String to = (String) toComboBox.getSelectedItem();
            String priority = (String) priorityComboBox.getSelectedItem();
            String content = messageArea.getText().trim();

            if (to == null || content.isEmpty()) {
                showError("Error", "Please fill in all required fields");
                return;
            }

            sendMessage(to, "Message", content, priority);
            messageArea.setText("");
            toComboBox.setSelectedIndex(0);
            priorityComboBox.setSelectedItem("Medium");
        });
        panel.add(sendButton, gbc);

        return panel;
    }

    private void showStudentsDialog(String subject, String level) {
        // Create dialog
        JDialog dialog = new JDialog(this, "Students in " + subject + " (" + level + ")", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setBackground(new Color(0xF8F9FA));

        // Create table model for students
        DefaultTableModel studentsModel = new DefaultTableModel(
                new String[]{"Student ID", "Name", "Level"}, 0
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
                                row.add(parts[0].trim()); // scheduleId (hidden)
                                row.add(classId); // classId (hidden)
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
        if (attendanceTableModel != null) loadAttendanceData();
        if (messagesTableModel != null) loadMessagesData();
        if (classesTable != null) classesTable.revalidate();
        if (scheduleTable != null) scheduleTable.revalidate();
        if (subjectsTable != null) subjectsTable.revalidate();
        if (attendanceTable != null) attendanceTable.revalidate();
        if (messagesTable != null) messagesTable.revalidate();
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

    // Attendance Management Helper Methods
    private void loadClassesForAttendance(JComboBox<String> classComboBox) {
        classComboBox.removeAllItems();
        try {
            List<String> classLines = function.readClasses();
            for (String line : classLines) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[2].trim().equals(currentTutor.getStudentId())) {
                    // Get subject name from subject.txt
                    String subjectId = parts[1].trim();
                    String subjectName = getSubjectName(subjectId);
                    String classId = parts[0].trim();
                    classComboBox.addItem(classId + " - " + subjectName);
                }
            }
        } catch (Exception e) {
            showError("Error", "Failed to load classes: " + e.getMessage());
        }
    }

    private String getSubjectName(String subjectId) {
        try {
            List<String> subjectLines = function.readSubjects();
            for (String line : subjectLines) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].trim().equals(subjectId)) {
                    return parts[1].trim() + " " + parts[2].trim();
                }
            }
        } catch (Exception e) {
            // Return subject ID if can't find name
        }
        return subjectId;
    }

    private void showMarkAttendanceDialog(String selectedClass, String selectedDate) {
        String classId = selectedClass.split(" - ")[0];
        
        // Get students enrolled in this class
        List<String[]> students = getStudentsInClass(classId);
        
        if (students.isEmpty()) {
            showError("Error", "No students found for this class");
            return;
        }

        // Create attendance marking dialog
        JDialog dialog = new JDialog(this, "Mark Attendance - " + selectedDate, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        // Students table for attendance marking
        DefaultTableModel attendanceMarkingModel = new DefaultTableModel(
                new String[]{"Student ID", "Student Name", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only Status is editable
            }
        };

        JTable attendanceMarkingTable = new JTable(attendanceMarkingModel);
        styleTable(attendanceMarkingTable);

        // Add combo box editor for status column
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Present", "Absent", "Late"});
        attendanceMarkingTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(statusComboBox));

        // Populate table with students
        for (String[] student : students) {
            attendanceMarkingModel.addRow(new Object[]{student[0], student[1], "Present"});
        }

        JScrollPane scrollPane = new JScrollPane(attendanceMarkingTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = createStyledButton("💾 Save Attendance", new Color(40, 167, 69));
        JButton cancelButton = createStyledButton("❌ Cancel", new Color(220, 53, 69));

        saveButton.addActionListener(e -> {
            saveAttendanceMarking(attendanceMarkingModel, classId, selectedDate);
            dialog.dispose();
            loadAttendanceData(); // Refresh the main attendance table
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private List<String[]> getStudentsInClass(String classId) {
        List<String[]> students = new ArrayList<>();
        try {
            // Get subject ID from class
            String subjectId = "";
            List<String> classLines = function.readClasses();
            for (String line : classLines) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].trim().equals(classId)) {
                    subjectId = parts[1].trim();
                    break;
                }
            }

            if (!subjectId.isEmpty()) {
                // Get students enrolled in this subject
                List<String> enrolledStudentIds = new ArrayList<>();
                List<String> studentSubjectLines = function.readStudentSubjects();
                for (String line : studentSubjectLines) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2 && parts[1].trim().equals(subjectId)) {
                        enrolledStudentIds.add(parts[0].trim());
                    }
                }

                // Get student details
                List<String> studentLines = function.readStudents();
                for (String line : studentLines) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2 && enrolledStudentIds.contains(parts[0].trim())) {
                        students.add(new String[]{parts[0].trim(), parts[1].trim()});
                    }
                }
            }
        } catch (Exception e) {
            showError("Error", "Failed to load students: " + e.getMessage());
        }
        return students;
    }

    private void saveAttendanceMarking(DefaultTableModel model, String classId, String date) {
        try {
            for (int i = 0; i < model.getRowCount(); i++) {
                String studentId = (String) model.getValueAt(i, 0);
                String status = (String) model.getValueAt(i, 2);
                
                // Create attendance record
                Attendance attendance = new Attendance(
                    function.generateAttendanceId(),
                    studentId,
                    classId,
                    date,
                    status,
                    currentTutor.getStudentId(),
                    java.time.LocalTime.now().toString().substring(0, 5) // HH:MM format
                );

                function.addAttendance(attendance.toCsvString());
            }
            showSuccess("Attendance marked successfully!");
        } catch (Exception e) {
            showError("Error", "Failed to save attendance: " + e.getMessage());
        }
    }

    private void showAttendanceReports() {
        JDialog dialog = new JDialog(this, "Attendance Reports", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        // Summary statistics
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary Statistics"));
        summaryPanel.setBackground(new Color(0xF8F9FA));

        // Calculate statistics
        List<Attendance> allAttendance = function.getAttendanceForTutor(currentTutor.getStudentId());
        int totalRecords = allAttendance.size();
        int presentCount = 0;
        int absentCount = 0;
        int lateCount = 0;

        for (Attendance att : allAttendance) {
            switch (att.getStatus()) {
                case "Present": presentCount++; break;
                case "Absent": absentCount++; break;
                case "Late": lateCount++; break;
            }
        }

        summaryPanel.add(createStatCard("Total Records", String.valueOf(totalRecords), Color.BLUE));
        summaryPanel.add(createStatCard("Present", String.valueOf(presentCount), Color.GREEN));
        summaryPanel.add(createStatCard("Absent", String.valueOf(absentCount), Color.RED));
        summaryPanel.add(createStatCard("Late", String.valueOf(lateCount), Color.ORANGE));

        // Detailed table
        DefaultTableModel reportModel = new DefaultTableModel(
                new String[]{"Date", "Class", "Student", "Status", "Time"}, 0
        );
        JTable reportTable = new JTable(reportModel);
        styleTable(reportTable);

        for (Attendance att : allAttendance) {
            reportModel.addRow(new Object[]{
                att.getDate().toString(),
                att.getClassId(),
                getStudentName(att.getStudentId()),
                att.getStatus(),
                att.getTimeMarked().toString()
            });
        }

        dialog.add(summaryPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        JButton closeButton = createStyledButton("Close", new Color(108, 117, 125));
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private String getStudentName(String studentId) {
        try {
            List<String> studentLines = function.readStudents();
            for (String line : studentLines) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].trim().equals(studentId)) {
                    return parts[1].trim();
                }
            }
        } catch (Exception e) {
            // Return student ID if can't find name
        }
        return studentId;
    }

    private void loadAttendanceData() {
        attendanceTableModel.setRowCount(0);
        try {
            List<Attendance> attendanceList = function.getAttendanceForTutor(currentTutor.getStudentId());
            for (Attendance att : attendanceList) {
                            attendanceTableModel.addRow(new Object[]{
                att.getDate().toString(),
                att.getClassId(),
                getStudentName(att.getStudentId()),
                att.getStatus(),
                att.getTimeMarked().toString()
            });
            }
        } catch (Exception e) {
            showError("Error", "Failed to load attendance data: " + e.getMessage());
        }
    }

    // Messaging System Helper Methods
    private void loadMessagesData() {
        messagesTableModel.setRowCount(0);
        try {
            List<Message> messages = function.getMessagesForUser(currentTutor.getStudentId());
            for (Message msg : messages) {
                String senderName = getUserName(msg.getSenderId());
                messagesTableModel.addRow(new Object[]{
                    senderName,
                    msg.getDateTime().toString(),
                    msg.getPriority(),
                    msg.getStatus()
                });
            }
        } catch (Exception e) {
            showError("Error", "Failed to load messages: " + e.getMessage());
        }
    }

    private void loadRecipientsForCompose(JComboBox<String> toComboBox) {
        toComboBox.removeAllItems();
        try {
            List<String> userLines = function.readUsers();
            for (String line : userLines) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && !parts[0].trim().equals(currentTutor.getStudentId())) {
                    String userId = parts[0].trim();
                    String name = parts[4].trim();
                    String role = parts[3].trim();
                    toComboBox.addItem(userId + " - " + name + " (" + role + ")");
                }
            }
            // Add group options
            toComboBox.addItem("ALL - All Users");
            toComboBox.addItem("STUDENTS - All Students");
            toComboBox.addItem("TUTORS - All Tutors");
            toComboBox.addItem("RECEPTIONISTS - All Receptionists");
        } catch (Exception e) {
            showError("Error", "Failed to load recipients: " + e.getMessage());
        }
    }

    private void sendMessage(String to, String subject, String content, String priority) {
        try {
            String receiverId = to.split(" - ")[0];
            
            Message message = new Message(
                function.generateMessageId(),
                currentTutor.getStudentId(),
                receiverId,
                "Message",
                subject,
                content,
                java.time.LocalDateTime.now(),
                "Sent",
                priority,
                ""
            );

            function.addMessage(message.toCsvString());
            showSuccess("Message sent successfully!");
            loadMessagesData(); // Refresh messages
        } catch (Exception e) {
            showError("Error", "Failed to send message: " + e.getMessage());
        }
    }

    private void showMessageDialog(int row) {
        try {
            // Get message details from the table
            String from = (String) messagesTableModel.getValueAt(row, 0);
            String dateTime = (String) messagesTableModel.getValueAt(row, 1);
            String priority = (String) messagesTableModel.getValueAt(row, 2);

            // Find the full message content
            List<Message> messages = function.getMessagesForUser(currentTutor.getStudentId());
            Message selectedMessage = null;
            for (Message msg : messages) {
                if (msg.getDateTime().toString().equals(dateTime)) {
                    selectedMessage = msg;
                    break;
                }
            }

            if (selectedMessage != null) {
                // Create message view dialog
                JDialog dialog = new JDialog(this, "Message Details", true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.setSize(500, 400);
                dialog.setLocationRelativeTo(this);

                // Header panel
                JPanel headerPanel = new JPanel(new GridLayout(3, 2, 5, 5));
                headerPanel.setBorder(BorderFactory.createTitledBorder("Message Information"));
                headerPanel.add(new JLabel("From:"));
                headerPanel.add(new JLabel(from));
                headerPanel.add(new JLabel("Date/Time:"));
                headerPanel.add(new JLabel(dateTime));
                headerPanel.add(new JLabel("Priority:"));
                headerPanel.add(new JLabel(priority));

                // Content area
                JTextArea contentArea = new JTextArea(selectedMessage.getContent());
                contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                contentArea.setEditable(false);
                contentArea.setLineWrap(true);
                contentArea.setWrapStyleWord(true);
                JScrollPane contentScrollPane = new JScrollPane(contentArea);
                contentScrollPane.setBorder(BorderFactory.createTitledBorder("Message Content"));

                // Buttons
                JPanel buttonPanel = new JPanel(new FlowLayout());
                JButton replyButton = createStyledButton("↩️ Reply", new Color(0, 123, 255));
                JButton closeButton = createStyledButton("Close", new Color(108, 117, 125));

                replyButton.addActionListener(e -> {
                    dialog.dispose();
                    // Switch to compose tab and pre-fill reply
                    // This would require access to the compose panel components
                });

                closeButton.addActionListener(e -> dialog.dispose());

                buttonPanel.add(replyButton);
                buttonPanel.add(closeButton);

                dialog.add(headerPanel, BorderLayout.NORTH);
                dialog.add(contentScrollPane, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);

                dialog.setVisible(true);

                // Mark message as read
                selectedMessage.markAsRead();
                // Update message status in file (this would require updating the CSV)
            }
        } catch (Exception e) {
            showError("Error", "Failed to display message: " + e.getMessage());
        }
    }

    private String getUserName(String userId) {
        try {
            List<String> userLines = function.readUsers();
            for (String line : userLines) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].trim().equals(userId)) {
                    return parts[4].trim();
                }
            }
        } catch (Exception e) {
            // Return user ID if can't find name
        }
        return userId;
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