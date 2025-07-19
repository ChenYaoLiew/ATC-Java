import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Attendance {
    private String attendanceId;
    private String studentId;
    private String classId;
    private LocalDate date;
    private String status; // Present, Absent, Late
    private String markedBy;
    private LocalTime timeMarked;
    
    // Date and time formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    // Constructor
    public Attendance(String attendanceId, String studentId, String classId, LocalDate date, 
                     String status, String markedBy, LocalTime timeMarked) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.classId = classId;
        this.date = date;
        this.status = status;
        this.markedBy = markedBy;
        this.timeMarked = timeMarked;
    }
    
    // Constructor with string date and time (for CSV parsing)
    public Attendance(String attendanceId, String studentId, String classId, String dateStr, 
                     String status, String markedBy, String timeStr) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.classId = classId;
        this.date = LocalDate.parse(dateStr, DATE_FORMATTER);
        this.status = status;
        this.markedBy = markedBy;
        this.timeMarked = LocalTime.parse(timeStr, TIME_FORMATTER);
    }
    
    // Getters
    public String getAttendanceId() { return attendanceId; }
    public String getStudentId() { return studentId; }
    public String getClassId() { return classId; }
    public LocalDate getDate() { return date; }
    public String getStatus() { return status; }
    public String getMarkedBy() { return markedBy; }
    public LocalTime getTimeMarked() { return timeMarked; }
    
    // Setters
    public void setAttendanceId(String attendanceId) { this.attendanceId = attendanceId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setClassId(String classId) { this.classId = classId; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setStatus(String status) { this.status = status; }
    public void setMarkedBy(String markedBy) { this.markedBy = markedBy; }
    public void setTimeMarked(LocalTime timeMarked) { this.timeMarked = timeMarked; }
    
    // Convert to CSV format
    public String toCsvString() {
        return String.join(",", 
            attendanceId,
            studentId,
            classId,
            date.format(DATE_FORMATTER),
            status,
            markedBy,
            timeMarked.format(TIME_FORMATTER)
        );
    }
    
    // Parse from CSV string
    public static Attendance fromCsvString(String csvLine) {
        String[] parts = csvLine.split(",", -1); // -1 to preserve empty strings
        if (parts.length >= 7) {
            return new Attendance(
                parts[0].trim(),  // attendanceId
                parts[1].trim(),  // studentId
                parts[2].trim(),  // classId
                parts[3].trim(),  // date
                parts[4].trim(),  // status
                parts[5].trim(),  // markedBy
                parts[6].trim()   // timeMarked
            );
        }
        return null;
    }
    
    // Generate new attendance ID
    public static String generateNewAttendanceId() {
        // Simple ID generation - in real application, should check existing IDs
        return "ATT" + String.format("%03d", (int)(Math.random() * 1000) + 1);
    }
    
    // Utility methods
    public boolean isPresent() {
        return "Present".equalsIgnoreCase(status);
    }
    
    public boolean isAbsent() {
        return "Absent".equalsIgnoreCase(status);
    }
    
    public boolean isLate() {
        return "Late".equalsIgnoreCase(status);
    }
    
    @Override
    public String toString() {
        return String.format("Attendance[%s]: Student %s, Class %s, Date %s, Status %s", 
                           attendanceId, studentId, classId, date, status);
    }
} 