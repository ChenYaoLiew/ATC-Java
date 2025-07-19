import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class function {
    // Cross-platform data path construction
    private static final String DATA_PATH;
    
    static {
        // Get current working directory and build cross-platform path
        String userDir = System.getProperty("user.dir");
        DATA_PATH = userDir + File.separator + "data" + File.separator;
        
        // Ensure data directory exists
        ensureDataDirectoryExists();
    }
    
    // Ensure the data directory exists, create if it doesn't
    private static void ensureDataDirectoryExists() {
        File dataDir = new File(DATA_PATH);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
    
    // Get full file path
    private static String getFullPath(String fileName) {
        return DATA_PATH + fileName;
    }
    
    // Generic method to read lines from any file
    private static List<String> readFile(String fileName) {
        List<String> lines = new ArrayList<>();
        String fullPath = getFullPath(fileName);
        
        File file = new File(fullPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Failed to create file " + fullPath + ": " + e.getMessage());
            }
            return lines; // Return empty list for new file
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fullPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading from " + fullPath + ": " + e.getMessage());
        }
        return lines;
    }

    // Generic method to write lines to any file
    private static boolean writeFile(String fileName, List<String> lines) {
        String fullPath = getFullPath(fileName);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to " + fullPath + ": " + e.getMessage());
            return false;
        }
    }

    // Generic method to append a line to any file
    private static boolean appendToFile(String fileName, String line) {
        String fullPath = getFullPath(fileName);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath, true))) {
            writer.write(line);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error appending to " + fullPath + ": " + e.getMessage());
            return false;
        }
    }

    // Generic method to delete a line from any file
    private static boolean deleteLine(String fileName, String lineToDelete) {
        List<String> lines = readFile(fileName);
        boolean found = lines.remove(lineToDelete);
        
        if (found) {
            return writeFile(fileName, lines);
        }
        return false;
    }

    // Users.txt operations
    public static List<String> readUsers() {
        return readFile("users.txt");
    }

    public static boolean addUser(String userData) {
        return appendToFile("users.txt", userData);
    }

    public static boolean updateUser(String oldData, String newData) {
        List<String> lines = readFile("users.txt");
        int index = lines.indexOf(oldData);
        if (index != -1) {
            lines.set(index, newData);
            return writeFile("users.txt", lines);
        }
        return false;
    }

    public static boolean deleteUser(String userData) {
        return deleteLine("users.txt", userData);
    }

    // Students.txt operations
    public static List<String> readStudents() {
        return readFile("students.txt");
    }

    public static boolean addStudent(String studentData) {
        return appendToFile("students.txt", studentData);
    }

    public static boolean updateStudent(String oldData, String newData) {
        List<String> lines = readFile("students.txt");
        int index = lines.indexOf(oldData);
        if (index != -1) {
            lines.set(index, newData);
            return writeFile("students.txt", lines);
        }
        return false;
    }

    public static boolean deleteStudent(String studentData) {
        return deleteLine("students.txt", studentData);
    }

    // Tutors.txt operations
    public static List<String> readTutors() {
        return readFile("tutors.txt");
    }

    public static boolean addTutor(String tutorData) {
        return appendToFile("tutors.txt", tutorData);
    }

    public static boolean updateTutor(String oldData, String newData) {
        List<String> lines = readFile("tutors.txt");
        int index = lines.indexOf(oldData);
        if (index != -1) {
            lines.set(index, newData);
            return writeFile("tutors.txt", lines);
        }
        return false;
    }

    public static boolean deleteTutor(String tutorData) {
        return deleteLine("tutors.txt", tutorData);
    }

    // Subjects.txt operations
    public static List<String> readSubjects() {
        return readFile("subject.txt");
    }

    public static boolean addSubject(String subjectData) {
        return appendToFile("subject.txt", subjectData);
    }

    public static boolean updateSubject(String oldData, String newData) {
        List<String> lines = readFile("subject.txt");
        int index = lines.indexOf(oldData);
        if (index != -1) {
            lines.set(index, newData);
            return writeFile("subject.txt", lines);
        }
        return false;
    }

    public static boolean deleteSubject(String subjectData) {
        return deleteLine("subject.txt", subjectData);
    }

    // Classes.txt operations
    public static List<String> readClasses() {
        return readFile("classes.txt");
    }

    public static boolean addClass(String classData) {
        return appendToFile("classes.txt", classData);
    }

    public static boolean updateClass(String oldData, String newData) {
        List<String> lines = readFile("classes.txt");
        int index = lines.indexOf(oldData);
        if (index != -1) {
            lines.set(index, newData);
            return writeFile("classes.txt", lines);
        }
        return false;
    }

    public static boolean deleteClass(String classData) {
        return deleteLine("classes.txt", classData);
    }

    // Payments.txt operations
    public static List<String> readPayments() {
        return readFile("payments.txt");
    }

    public static boolean addPayment(String paymentData) {
        return appendToFile("payments.txt", paymentData);
    }

    public static boolean updatePayment(String oldData, String newData) {
        List<String> lines = readFile("payments.txt");
        int index = lines.indexOf(oldData);
        if (index != -1) {
            lines.set(index, newData);
            return writeFile("payments.txt", lines);
        }
        return false;
    }

    public static boolean deletePayment(String paymentData) {
        return deleteLine("payments.txt", paymentData);
    }

    // Schedule.txt operations
    public static List<String> readSchedules() {
        return readFile("schedule.txt");
    }

    public static boolean addSchedule(String scheduleData) {
        return appendToFile("schedule.txt", scheduleData);
    }

    public static boolean updateSchedule(String oldData, String newData) {
        List<String> lines = readFile("schedule.txt");
        int index = lines.indexOf(oldData);
        if (index != -1) {
            lines.set(index, newData);
            return writeFile("schedule.txt", lines);
        }
        return false;
    }

    public static boolean deleteSchedule(String scheduleData) {
        return deleteLine("schedule.txt", scheduleData);
    }

    // Student_subjects.txt operations
    public static List<String> readStudentSubjects() {
        return readFile("student_subjects.txt");
    }

    public static boolean addStudentSubject(String data) {
        return appendToFile("student_subjects.txt", data);
    }

    public static boolean updateStudentSubject(String oldData, String newData) {
        List<String> lines = readFile("student_subjects.txt");
        int index = lines.indexOf(oldData);
        if (index != -1) {
            lines.set(index, newData);
            return writeFile("student_subjects.txt", lines);
        }
        return false;
    }

    public static boolean deleteStudentSubject(String data) {
        return deleteLine("student_subjects.txt", data);
    }

    // Student_balances.txt operations
    public static List<String> readStudentBalances() {
        return readFile("student_balances.txt");
    }

    public static boolean addStudentBalance(String data) {
        return appendToFile("student_balances.txt", data);
    }

    public static boolean updateStudentBalance(String oldData, String newData) {
        List<String> lines = readFile("student_balances.txt");
        int index = lines.indexOf(oldData);
        if (index != -1) {
            lines.set(index, newData);
            return writeFile("student_balances.txt", lines);
        }
        return false;
    }

    public static boolean deleteStudentBalance(String data) {
        return deleteLine("student_balances.txt", data);
    }

    // Subject_requests.txt operations
    public static List<String> readSubjectRequests() {
        return readFile("subject_requests.txt");
    }

    public static boolean addSubjectRequest(String data) {
        return appendToFile("subject_requests.txt", data);
    }

    public static boolean updateSubjectRequest(String oldData, String newData) {
        List<String> lines = readFile("subject_requests.txt");
        int index = lines.indexOf(oldData);
        if (index != -1) {
            lines.set(index, newData);
            return writeFile("subject_requests.txt", lines);
        }
        return false;
    }

    public static boolean deleteSubjectRequest(String data) {
        return deleteLine("subject_requests.txt", data);
    }

    // Utility method to check if a file exists
    public static boolean fileExists(String fileName) {
        File file = new File(getFullPath(fileName));
        return file.exists() && file.isFile();
    }

    // Utility method to create a file if it doesn't exist
    public static boolean createFileIfNotExists(String fileName) {
        File file = new File(getFullPath(fileName));
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file " + getFullPath(fileName) + ": " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    // ID Generation Methods
    private static String generateId(String prefix, List<String> existingIds, int idLength) {
        int maxNumber = 0;
        
        // Find the highest number in existing IDs
        for (String id : existingIds) {
            if (id.startsWith(prefix)) {
                try {
                    int number = Integer.parseInt(id.substring(prefix.length()));
                    maxNumber = Math.max(maxNumber, number);
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                    continue;
                }
            }
        }
        
        // Generate new number
        int newNumber = maxNumber + 1;
        
        // Format the number part with leading zeros
        String numberPart = String.format("%0" + (idLength - prefix.length()) + "d", newNumber);
        return prefix + numberPart;
    }

    public static String generateAdminId() {
        List<String> userLines = readUsers();
        List<String> existingIds = new ArrayList<>();
        
        for (String line : userLines) {
            String[] parts = line.split(",");
            if (parts.length > 0 && !parts[0].trim().isEmpty()) {
                existingIds.add(parts[0].trim());
            }
        }
        
        return generateId("A", existingIds, 4); // A001 format
    }

    public static String generateTutorId() {
        List<String> userLines = readUsers();
        List<String> existingIds = new ArrayList<>();
        
        for (String line : userLines) {
            String[] parts = line.split(",");
            if (parts.length > 0 && !parts[0].trim().isEmpty()) {
                existingIds.add(parts[0].trim());
            }
        }
        
        return generateId("T", existingIds, 4); // T001 format
    }

    public static String generateStudentId() {
        List<String> userLines = readUsers();
        List<String> existingIds = new ArrayList<>();
        
        for (String line : userLines) {
            String[] parts = line.split(",");
            if (parts.length > 0 && !parts[0].trim().isEmpty()) {
                existingIds.add(parts[0].trim());
            }
        }
        
        return generateId("S", existingIds, 4); // S001 format
    }

    public static String generateReceptionistId() {
        List<String> userLines = readUsers();
        List<String> existingIds = new ArrayList<>();
        
        for (String line : userLines) {
            String[] parts = line.split(",");
            if (parts.length > 0 && !parts[0].trim().isEmpty()) {
                existingIds.add(parts[0].trim());
            }
        }
        
        return generateId("R", existingIds, 4); // R001 format
    }

    public static String generateSubjectId() {
        List<String> subjectLines = readSubjects();
        List<String> existingIds = new ArrayList<>();
        
        for (String line : subjectLines) {
            String[] parts = line.split(",");
            if (parts.length > 0 && !parts[0].trim().isEmpty()) {
                existingIds.add(parts[0].trim());
            }
        }
        
        return generateId("SUB", existingIds, 6); // SUB001 format
    }

    public static String generateClassId() {
        List<String> classLines = readClasses();
        List<String> existingIds = new ArrayList<>();
        
        for (String line : classLines) {
            String[] parts = line.split(",");
            if (parts.length > 0 && !parts[0].trim().isEmpty()) {
                existingIds.add(parts[0].trim());
            }
        }
        
        return generateId("CLS", existingIds, 6); // CLS001 format
    }

    public static String generatePaymentId() {
        List<String> paymentLines = readPayments();
        List<String> existingIds = new ArrayList<>();
        
        for (String line : paymentLines) {
            String[] parts = line.split(",");
            if (parts.length > 0 && !parts[0].trim().isEmpty()) {
                existingIds.add(parts[0].trim());
            }
        }
        
        return generateId("PAY", existingIds, 6); // PAY001 format
    }

    public static String generateRequestId() {
        List<String> requestLines = readSubjectRequests();
        List<String> existingIds = new ArrayList<>();
        
        for (String line : requestLines) {
            String[] parts = line.split(",");
            if (parts.length > 0 && !parts[0].trim().isEmpty()) {
                existingIds.add(parts[0].trim());
            }
        }
        
        return generateId("REQ", existingIds, 6); // REQ001 format
    }

    // Attendance.txt operations
    public static List<String> readAttendance() {
        return readFile("attendance.txt");
    }

    public static boolean addAttendance(String attendanceData) {
        return appendToFile("attendance.txt", attendanceData);
    }

    public static boolean updateAttendance(String oldData, String newData) {
        List<String> lines = readFile("attendance.txt");
        int index = lines.indexOf(oldData);
        if (index != -1) {
            lines.set(index, newData);
            return writeFile("attendance.txt", lines);
        }
        return false;
    }

    public static boolean deleteAttendance(String attendanceData) {
        return deleteLine("attendance.txt", attendanceData);
    }

    public static String generateAttendanceId() {
        List<String> attendanceLines = readAttendance();
        List<String> existingIds = new ArrayList<>();
        
        for (String line : attendanceLines) {
            String[] parts = line.split(",");
            if (parts.length > 0 && !parts[0].trim().isEmpty()) {
                existingIds.add(parts[0].trim());
            }
        }
        
        return generateId("ATT", existingIds, 6); // ATT001 format
    }

    // Messages.txt operations
    public static List<String> readMessages() {
        return readFile("messages.txt");
    }

    public static boolean addMessage(String messageData) {
        return appendToFile("messages.txt", messageData);
    }

    public static boolean updateMessage(String oldData, String newData) {
        List<String> lines = readFile("messages.txt");
        int index = lines.indexOf(oldData);
        if (index != -1) {
            lines.set(index, newData);
            return writeFile("messages.txt", lines);
        }
        return false;
    }

    public static boolean deleteMessage(String messageData) {
        return deleteLine("messages.txt", messageData);
    }

    public static String generateMessageId() {
        List<String> messageLines = readMessages();
        List<String> existingIds = new ArrayList<>();
        
        for (String line : messageLines) {
            String[] parts = line.split(",");
            if (parts.length > 0 && !parts[0].trim().isEmpty()) {
                existingIds.add(parts[0].trim());
            }
        }
        
        return generateId("MSG", existingIds, 6); // MSG001 format
    }

    // Helper methods for attendance system
    public static List<Attendance> getAttendanceForStudent(String studentId) {
        List<Attendance> attendanceList = new ArrayList<>();
        List<String> lines = readAttendance();
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            Attendance attendance = Attendance.fromCsvString(line);
            if (attendance != null && attendance.getStudentId().equals(studentId)) {
                attendanceList.add(attendance);
            }
        }
        return attendanceList;
    }

    public static List<Attendance> getAttendanceForClass(String classId) {
        List<Attendance> attendanceList = new ArrayList<>();
        List<String> lines = readAttendance();
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            Attendance attendance = Attendance.fromCsvString(line);
            if (attendance != null && attendance.getClassId().equals(classId)) {
                attendanceList.add(attendance);
            }
        }
        return attendanceList;
    }

    public static List<Attendance> getAttendanceForTutor(String tutorId) {
        List<Attendance> attendanceList = new ArrayList<>();
        List<String> lines = readAttendance();
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            Attendance attendance = Attendance.fromCsvString(line);
            if (attendance != null && attendance.getMarkedBy().equals(tutorId)) {
                attendanceList.add(attendance);
            }
        }
        return attendanceList;
    }

    // Helper methods for messaging system
    public static List<Message> getMessagesForUser(String userId) {
        List<Message> messageList = new ArrayList<>();
        List<String> lines = readMessages();
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            Message message = Message.fromCsvString(line);
            if (message != null) {
                // Include messages sent to user, broadcast messages, or role-specific messages
                if (message.getReceiverId().equals(userId) || 
                    message.getReceiverId().equals("ALL") ||
                    isUserInGroup(userId, message.getReceiverId())) {
                    messageList.add(message);
                }
            }
        }
        return messageList;
    }

    public static List<Message> getSentMessages(String userId) {
        List<Message> messageList = new ArrayList<>();
        List<String> lines = readMessages();
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            Message message = Message.fromCsvString(line);
            if (message != null && message.getSenderId().equals(userId)) {
                messageList.add(message);
            }
        }
        return messageList;
    }

    public static List<Message> getAnnouncements() {
        List<Message> announcements = new ArrayList<>();
        List<String> lines = readMessages();
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            Message message = Message.fromCsvString(line);
            if (message != null && message.isAnnouncement()) {
                announcements.add(message);
            }
        }
        return announcements;
    }

    private static boolean isUserInGroup(String userId, String groupId) {
        if (groupId.equals("ALL")) return true;
        
        // Get user role from users.txt
        List<String> userLines = readUsers();
        for (String line : userLines) {
            String[] parts = line.split(",");
            if (parts.length >= 4 && parts[0].trim().equals(userId)) {
                String role = parts[3].trim().toLowerCase();
                switch (groupId) {
                    case "TUTORS": return "tutor".equals(role);
                    case "STUDENTS": return "student".equals(role);
                    case "RECEPTIONISTS": return "receptionist".equals(role);
                    case "ADMINS": return "admin".equals(role);
                }
            }
        }
        return false;
    }
}
