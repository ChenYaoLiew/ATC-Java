import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String messageType; // Message, Announcement, Request, Reply
    private String subject;
    private String content;
    private LocalDateTime dateTime;
    private String status; // Sent, Read, Deleted
    private String priority; // Low, Medium, High
    private String parentMessageId; // For threaded conversations
    
    // DateTime formatter
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Constructor
    public Message(String messageId, String senderId, String receiverId, String messageType,
                  String subject, String content, LocalDateTime dateTime, String status, 
                  String priority, String parentMessageId) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.subject = subject;
        this.content = content;
        this.dateTime = dateTime;
        this.status = status;
        this.priority = priority;
        this.parentMessageId = parentMessageId;
    }
    
    // Constructor with string datetime (for CSV parsing)
    public Message(String messageId, String senderId, String receiverId, String messageType,
                  String subject, String content, String dateTimeStr, String status, 
                  String priority, String parentMessageId) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.subject = subject;
        this.content = content;
        this.dateTime = LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        this.status = status;
        this.priority = priority;
        this.parentMessageId = parentMessageId;
    }
    
    // Getters
    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getMessageType() { return messageType; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getParentMessageId() { return parentMessageId; }
    
    // Setters
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setContent(String content) { this.content = content; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public void setStatus(String status) { this.status = status; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setParentMessageId(String parentMessageId) { this.parentMessageId = parentMessageId; }
    
    // Convert to CSV format
    public String toCsvString() {
        return String.join(",", 
            messageId,
            senderId,
            receiverId,
            messageType,
            subject,
            content.replace(",", "\\,"), // Escape commas in content
            dateTime.format(DATETIME_FORMATTER),
            status,
            priority,
            parentMessageId != null ? parentMessageId : ""
        );
    }
    
    // Parse from CSV string
    public static Message fromCsvString(String csvLine) {
        String[] parts = csvLine.split(",", -1); // -1 to preserve empty strings
        if (parts.length >= 10) {
            return new Message(
                parts[0].trim(),  // messageId
                parts[1].trim(),  // senderId
                parts[2].trim(),  // receiverId
                parts[3].trim(),  // messageType
                parts[4].trim(),  // subject
                parts[5].trim().replace("\\,", ","), // Unescape commas
                parts[6].trim(),  // dateTime
                parts[7].trim(),  // status
                parts[8].trim(),  // priority
                parts[9].trim()   // parentMessageId
            );
        }
        return null;
    }
    
    // Generate new message ID
    public static String generateNewMessageId() {
        // Simple ID generation - in real application, should check existing IDs
        return "MSG" + String.format("%03d", (int)(Math.random() * 1000) + 1);
    }
    
    // Utility methods
    public boolean isAnnouncement() {
        return "Announcement".equalsIgnoreCase(messageType);
    }
    
    public boolean isRequest() {
        return "Request".equalsIgnoreCase(messageType);
    }
    
    public boolean isReply() {
        return "Reply".equalsIgnoreCase(messageType);
    }
    
    public boolean isRead() {
        return "Read".equalsIgnoreCase(status);
    }
    
    public boolean isHighPriority() {
        return "High".equalsIgnoreCase(priority);
    }
    
    public boolean isBroadcast() {
        return "ALL".equals(receiverId) || "TUTORS".equals(receiverId) || 
               "STUDENTS".equals(receiverId) || "RECEPTIONISTS".equals(receiverId);
    }
    
    public void markAsRead() {
        this.status = "Read";
    }
    
    @Override
    public String toString() {
        return String.format("Message[%s]: From %s to %s - %s (%s)", 
                           messageId, senderId, receiverId, subject, priority);
    }
} 