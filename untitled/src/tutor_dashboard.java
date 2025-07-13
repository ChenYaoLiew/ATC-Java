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
    private JTextField nameField;
    private JTextField emailField;
    private JTextField contactField;
    private JList<String> subjectsList;
    private JList<String> levelsList;
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

    public static void main(String[] args) {
        // Create a test tutor for demonstration
        Tutor testTutor = new Tutor("testTutor", "password", "Test Tutor", "test@email.com", "123456789");
        testTutor.addSubject("Mathematics");
        testTutor.addSubject("Physics");
        testTutor.addLevel("Form 1");
        testTutor.addLevel("Form 2");

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

        // Initialize the UI components
        setupUI();

        // Set content pane
        setContentPane(contentPane);

        // Set welcome message
        welcomeLabel.setText("Welcome, " + currentTutor.getName() + " (Tutor)");

        // Initialize tables
        initializeTables();

        // Add action listeners
        addActionListeners();

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupUI() {
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        welcomeLabel = new JLabel("Welcome");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(255, 0, 0));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setPreferredSize(new Dimension(100, 35));
        
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Profile tab
        profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(Color.WHITE);
        setupProfilePanel();
        tabbedPane.addTab("👤 Profile", profilePanel);
        
        // Classes tab
        classesPanel = new JPanel(new BorderLayout());
        classesPanel.setBackground(Color.WHITE);
        setupClassesPanel();
        tabbedPane.addTab("📚 Classes", classesPanel);
        
        // Students tab
        studentsPanel = new JPanel(new BorderLayout());
        studentsPanel.setBackground(Color.WHITE);
        setupStudentsPanel();
        tabbedPane.addTab("👥 Students", studentsPanel);
        
        // Add components to content pane
        contentPane.add(headerPanel, BorderLayout.NORTH);
        contentPane.add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupProfilePanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        profilePanel.add(new JLabel("👤 Name:"), gbc);
        
        gbc.gridx = 1;
        nameField = new JTextField();
        nameField.setEditable(false);
        profilePanel.add(nameField, gbc);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 1;
        profilePanel.add(new JLabel("📧 Email:"), gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField();
        profilePanel.add(emailField, gbc);
        
        // Contact field
        gbc.gridx = 0; gbc.gridy = 2;
        profilePanel.add(new JLabel("📞 Contact:"), gbc);
        
        gbc.gridx = 1;
        contactField = new JTextField();
        profilePanel.add(contactField, gbc);
        
        // Subjects list
        gbc.gridx = 0; gbc.gridy = 3;
        profilePanel.add(new JLabel("📚 Subjects:"), gbc);
        
        gbc.gridx = 1;
        subjectsList = new JList<>();
        JScrollPane subjectsScroll = new JScrollPane(subjectsList);
        subjectsScroll.setPreferredSize(new Dimension(200, 100));
        profilePanel.add(subjectsScroll, gbc);
        
        // Levels list
        gbc.gridx = 0; gbc.gridy = 4;
        profilePanel.add(new JLabel("🎓 Levels:"), gbc);
        
        gbc.gridx = 1;
        levelsList = new JList<>();
        JScrollPane levelsScroll = new JScrollPane(levelsList);
        levelsScroll.setPreferredSize(new Dimension(200, 100));
        profilePanel.add(levelsScroll, gbc);
        
        // Update button
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        updateProfileButton = new JButton("💾 Update Profile");
        updateProfileButton.setBackground(new Color(0, 120, 215));
        updateProfileButton.setForeground(Color.BLACK);
        updateProfileButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        profilePanel.add(updateProfileButton, gbc);
    }

    private void setupClassesPanel() {
        // Header
        JLabel headerLabel = new JLabel("📚 My Classes");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        classesPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Table
        classesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(classesTable);
        classesPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setBackground(Color.WHITE);
        
        addClassButton = new JButton("➕ Add Class");
        updateClassButton = new JButton("✏️ Update Class");
        deleteClassButton = new JButton("🗑️ Delete Class");
        
        // Style buttons
        for (JButton button : new JButton[]{addClassButton, updateClassButton, deleteClassButton}) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setPreferredSize(new Dimension(150, 35));
        }
        
        addClassButton.setBackground(new Color(0, 120, 215));
        updateClassButton.setBackground(new Color(255, 193, 7));
        deleteClassButton.setBackground(new Color(255, 0, 0));
        
        buttonsPanel.add(addClassButton);
        buttonsPanel.add(updateClassButton);
        buttonsPanel.add(deleteClassButton);
        
        classesPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void setupStudentsPanel() {
        // Header
        JLabel headerLabel = new JLabel("👥 My Students");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        studentsPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Table
        studentsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        studentsPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void initializeTables() {
        // Classes table
        classesTableModel = new DefaultTableModel(
            new String[]{"Subject", "Level", "Schedule", "Students Count"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        classesTable.setModel(classesTableModel);
        classesTable.getTableHeader().setBackground(new Color(0x007BFF));
        classesTable.getTableHeader().setForeground(Color.WHITE);
        classesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Students table
        studentsTableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Subject", "Level"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentsTable.setModel(studentsTableModel);
        studentsTable.getTableHeader().setBackground(new Color(0x007BFF));
        studentsTable.getTableHeader().setForeground(Color.WHITE);
        studentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void addActionListeners() {
        logoutButton.addActionListener(e -> onLogout());
        updateProfileButton.addActionListener(e -> onUpdateProfile());
        addClassButton.addActionListener(e -> onAddClass());
        updateClassButton.addActionListener(e -> onUpdateClass());
        deleteClassButton.addActionListener(e -> onDeleteClass());

        // Window listeners
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onLogout();
            }
        });
    }

    private void loadAllData() {
        loadProfileData();
        loadClassesData();
        loadStudentsData();
    }

    private void loadProfileData() {
        nameField.setText(currentTutor.getName());
        emailField.setText(currentTutor.getEmail());
        contactField.setText(currentTutor.getContactNumber());

        // Load subjects
        DefaultListModel<String> subjectsModel = new DefaultListModel<>();
        for (String subject : currentTutor.getSubjects()) {
            subjectsModel.addElement(subject);
        }
        subjectsList.setModel(subjectsModel);

        // Load levels
        DefaultListModel<String> levelsModel = new DefaultListModel<>();
        for (String level : currentTutor.getLevels()) {
            levelsModel.addElement(level);
        }
        levelsList.setModel(levelsModel);
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
            JOptionPane.showMessageDialog(this, "Error loading classes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
        // Show dialog to update profile
        JTextField emailField = new JTextField(currentTutor.getEmail());
        JTextField contactField = new JTextField(currentTutor.getContactNumber());
        JTextField subjectField = new JTextField();
        JTextField levelField = new JTextField();

        Object[] message = {
            "📧 Email:", emailField,
            "📞 Contact Number:", contactField,
            "📚 Add Subject:", subjectField,
            "🎓 Add Level:", levelField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Profile",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            currentTutor.setEmail(emailField.getText().trim());
            currentTutor.setContactNumber(contactField.getText().trim());
            
            String newSubject = subjectField.getText().trim();
            String newLevel = levelField.getText().trim();
            
            if (!newSubject.isEmpty()) {
                currentTutor.addSubject(newSubject);
            }
            if (!newLevel.isEmpty()) {
                currentTutor.addLevel(newLevel);
            }

            // Update the display
            loadProfileData();
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onAddClass() {
        // Show dialog to add class
        JComboBox<String> subjectBox = new JComboBox<>(currentTutor.getSubjects().toArray(new String[0]));
        JComboBox<String> levelBox = new JComboBox<>(currentTutor.getLevels().toArray(new String[0]));
        JTextField scheduleField = new JTextField();

        Object[] message = {
            "📚 Subject:", subjectBox,
            "🎓 Level:", levelBox,
            "📅 Schedule:", scheduleField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Class",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String subject = (String) subjectBox.getSelectedItem();
            String level = (String) levelBox.getSelectedItem();
            String schedule = scheduleField.getText().trim();

            if (subject == null || level == null || schedule.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Add new class to classes.txt
                FileWriter writer = new FileWriter(DATA_DIR + "/classes.txt", true);
                writer.write(String.format("%s,%s,%s,%s,0\n",
                        subject, level, schedule, currentTutor.getUsername()));
                writer.close();

                loadClassesData();
                JOptionPane.showMessageDialog(this, "Class added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error adding class: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onUpdateClass() {
        int selectedRow = classesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a class to update",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String subject = (String) classesTable.getValueAt(selectedRow, 0);
        String level = (String) classesTable.getValueAt(selectedRow, 1);
        String schedule = (String) classesTable.getValueAt(selectedRow, 2);

        JTextField scheduleField = new JTextField(schedule);

        Object[] message = {
            "📚 Subject: " + subject,
            "🎓 Level: " + level,
            "📅 New Schedule:", scheduleField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Class",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newSchedule = scheduleField.getText().trim();

            if (newSchedule.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Schedule cannot be empty!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Update class in classes.txt
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
                JOptionPane.showMessageDialog(this, "Class updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error updating class: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDeleteClass() {
        int selectedRow = classesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a class to delete",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String subject = (String) classesTable.getValueAt(selectedRow, 0);
        String level = (String) classesTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this class?\n" + subject + " (" + level + ")",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Delete class from classes.txt
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
                JOptionPane.showMessageDialog(this, "Class deleted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error deleting class: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onLogout() {
        dispose();
        SwingUtilities.invokeLater(() -> new main_page().setVisible(true));
    }
}
