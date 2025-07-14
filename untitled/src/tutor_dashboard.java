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

        // Modern tabbed pane with reordered tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(0xF8F9FA));
        tabbedPane.addTab("📅 Schedule", createSchedulePanel());
        tabbedPane.addTab("👤 Profile", createProfilePanel());
        tabbedPane.addTab("📚 Classes", createClassesPanel());
        tabbedPane.addTab("📖 Subjects", createSubjectsPanel());

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

        panel.add(classesTitle, BorderLayout.NORTH);
        panel.add(classesScrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

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
        DefaultTableModel subjectsTableModel = new DefaultTableModel(
            new String[]{"Subject ID", "Subject", "Level", "Tutor ID"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable subjectsTable = new JTable(subjectsTableModel);
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
        DefaultTableModel scheduleTableModel = new DefaultTableModel(
            new String[]{"Subject", "Level", "Day", "Start Time", "End Time", "Room", "Namelist"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable scheduleTable = new JTable(scheduleTableModel);
        styleTable(scheduleTable);

        // Set preferred column widths
        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Subject
        scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Level
        scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Day
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Start Time
        scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(100); // End Time
        scheduleTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Room
        scheduleTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Namelist button

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
        // Remove studentsTableModel since we replaced it with schedule
    }

    private void loadProfileData() {
        try {
            File tutorsFile = new File(DATA_DIR + "/tutors.txt");
            if (tutorsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(tutorsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 7 && parts[0].trim().equals(currentTutor.getUsername())) {
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
            List<String> lines = new ArrayList<>();
            File tutorsFile = new File(DATA_DIR + "/tutors.txt");
            BufferedReader reader = new BufferedReader(new FileReader(tutorsFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7 && parts[0].trim().equals(currentTutor.getUsername())) {
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

    private void onUpdateSchedule(JTable scheduleTable) {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Selection Error", "Please select a schedule to update");
            return;
        }

        String scheduleId = (String) scheduleTable.getValueAt(selectedRow, 0);
        String classId = (String) scheduleTable.getValueAt(selectedRow, 1);
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
                // Read all lines from the file
                List<String> lines = new ArrayList<>();
                File scheduleFile = new File(DATA_DIR + "/schedule.txt");
                BufferedReader reader = new BufferedReader(new FileReader(scheduleFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].trim().equals(scheduleId)) {
                        // Update the schedule line
                        lines.add(String.format("%s,%s,%s,%s,%s,%s",
                                scheduleId, classId, newDay, newStartTime, newEndTime, newRoom));
                    } else {
                        lines.add(line);
                    }
                }
                reader.close();

                // Write back to file
                FileWriter writer = new FileWriter(scheduleFile);
                for (String updatedLine : lines) {
                    writer.write(updatedLine + "\n");
                }
                writer.close();

                // Refresh the table
                DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
                loadScheduleData(model);
                
                showSuccess("Schedule updated successfully!");
            } catch (IOException e) {
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
