import java.time.LocalDate;

public class Payment {
    private String studentUsername;
    private String subject;
    private String level;
    private double amount;
    private LocalDate paymentDate;
    private String month; // Month for which payment is made
    private String receiptNumber;

    public Payment(String studentUsername, String subject, String level, double amount, 
                  LocalDate paymentDate, String month, String receiptNumber) {
        this.studentUsername = studentUsername;
        this.subject = subject;
        this.level = level;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.month = month;
        this.receiptNumber = receiptNumber;
    }

    // Getters and setters
    public String getStudentUsername() {
        return studentUsername;
    }

    public String getSubject() {
        return subject;
    }

    public String getLevel() {
        return level;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getMonth() {
        return month;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    @Override
    public String toString() {
        return "Receipt #" + receiptNumber + 
               "\nStudent: " + studentUsername + 
               "\nSubject: " + subject + " (" + level + ")" +
               "\nAmount: $" + amount +
               "\nPayment Date: " + paymentDate +
               "\nFor Month: " + month;
    }
} 