# Tuition Center Management System - Data Structure Documentation

## Overview
This document describes the data structure and file formats used in the Tuition Center Management System. All data files are stored in CSV format with comma-separated values.

## File Structures

### 1. users.txt
**Purpose**: Stores user authentication and basic profile information for all system users.

**Format**: `UserID,Username,Password,Role,Name`

**Fields**:
- `UserID` (String): Unique identifier for the user (e.g., A001, R001, T001, S001)
- `Username` (String): Login username
- `Password` (String): Login password (plain text)
- `Role` (String): User role (admin, receptionist, tutor, student)
- `Name` (String): Full name of the user

**Example**:
```
A001,admin,admin123,admin,Elson
R001,receptionist,recep123,receptionist,ChenYao
T001,tutor,tutor123,tutor,Yin Yin
S001,student,student123,student,Javion
```

---

### 2. students.txt
**Purpose**: Stores detailed student information and enrollment data.

**Format**: `StudentID,Name,IC,Email,Phone,Address,RegistrationDate,Level,Status`

**Fields**:
- `StudentID` (String): Unique identifier matching users.txt UserID
- `Name` (String): Full name of the student
- `IC` (String): Identity card number (Malaysian format)
- `Email` (String): Email address
- `Phone` (String): Contact phone number
- `Address` (String): Full residential address
- `RegistrationDate` (Date): Date of registration (YYYY-MM-DD)
- `Level` (String): Current academic level (e.g., Secondary 3, Secondary 4)
- `Status` (String): Enrollment status (Active, Inactive, Graduated)

**Example**:
```
S001,Javion,990123-14-5678,javion@email.com,012-345-6789,123 Jalan Utama Kuala Lumpur,2024-01-15,Secondary 4,Active
```

---

### 3. subject.txt
**Purpose**: Defines available subjects and their corresponding academic levels.

**Format**: `SubjectID,SubjectName,Level,TutorID`

**Fields**:
- `SubjectID` (String): Unique identifier for the subject (SUB001, SUB002, etc.)
- `SubjectName` (String): Name of the subject (Math, English, Malay, Science)
- `Level` (String): Academic level (Secondary 1-5)
- `TutorID` (String): Unique identifier for the user (e.g., A001, R001, T001, S001)

**Example**:
```
SUB001,Math,Secondary 1
SUB004,Math,Secondary 4
SUB008,English,Secondary 3
```

---

### 4. classes.txt
**Purpose**: Stores class information including assigned tutors and fees.

**Format**: `ClassID,SubjectID,TutorID,Fee`

**Fields**:
- `ClassID` (String): Unique identifier for the class (CLS001, CLS002, etc.)
- `SubjectID` (String): Reference to subject.txt SubjectID
- `TutorID` (String): Reference to users.txt UserID for tutor
- `Fee` (Decimal): Class fee amount in currency units

**Example**:
```
CLS001,SUB001,T001,180.00
CLS004,SUB004,T002,180.00
```

**Relationships**:
- `SubjectID` → subject.txt
- `TutorID` → users.txt (where Role = "tutor")

---

### 5. schedule.txt
**Purpose**: Defines class schedules including timing and room assignments.

**Format**: `ScheduleID,ClassID,Day,StartTime,EndTime,Room`

**Fields**:
- `ScheduleID` (String): Unique identifier for the schedule entry
- `ClassID` (String): Reference to classes.txt ClassID
- `Day` (String): Day of the week (Monday, Tuesday, etc.)
- `StartTime` (Time): Class start time (HH:MM format)
- `EndTime` (Time): Class end time (HH:MM format)
- `Room` (String): Room assignment (Room A1, Room B2, etc.)

**Example**:
```
SCH001,CLS001,Monday,09:00,10:30,Room A1
SCH004,CLS004,Monday,16:00,17:30,Room A2
```

**Relationships**:
- `ClassID` → classes.txt

---

### 6. student_subjects.txt
**Purpose**: Records student enrollment in specific subjects (many-to-many relationship).

**Format**: `StudentID,SubjectID`

**Fields**:
- `StudentID` (String): Reference to students.txt StudentID
- `SubjectID` (String): Reference to subject.txt SubjectID

**Example**:
```
S001,SUB004
S001,SUB009
S002,SUB003
```

**Relationships**:
- `StudentID` → students.txt
- `SubjectID` → subject.txt

---

### 7. student_balances.txt
**Purpose**: Tracks student financial information including fees, payments, and outstanding balances.

**Format**: `StudentID,TotalFees,PaidAmount,BalanceAmount,LastPaymentDate,NextDueDate,Status`

**Fields**:
- `StudentID` (String): Reference to students.txt StudentID
- `TotalFees` (Decimal): Total fees charged to student
- `PaidAmount` (Decimal): Total amount paid by student
- `BalanceAmount` (Decimal): Outstanding balance (TotalFees - PaidAmount)
- `LastPaymentDate` (Date): Date of last payment (YYYY-MM-DD)
- `NextDueDate` (Date): Next payment due date (YYYY-MM-DD)
- `Status` (String): Payment status (Current, Overdue, Paid)

**Example**:
```
S001,1050.00,700.00,350.00,2024-02-15,2024-03-15,Current
```

**Relationships**:
- `StudentID` → students.txt

---

### 8. payments.txt
**Purpose**: Records all payment transactions made by students.

**Format**: `PaymentID,StudentID,Field3,Amount,PaymentMethod,DateTime,BillingMonth,ReceptionistID,Status`

**Fields**:
- `PaymentID` (String): Unique identifier for the payment
- `StudentID` (String): Reference to students.txt StudentID
- `Field3` (String): Currently unused/empty field
- `Amount` (Decimal): Payment amount
- `PaymentMethod` (String): Method of payment (Credit Card, Bank Transfer, Cash)
- `DateTime` (DateTime): Payment date and time (YYYY-MM-DD HH:MM:SS)
- `BillingMonth` (String): Month for which payment is made (YYYY-MM)
- `ReceptionistID` (String): ID of receptionist who processed payment
- `Status` (String): Payment status (Completed, Pending, Failed)

**Example**:
```
PAY001,S001,,350.00,Credit Card,2024-01-25 14:30:00,2024-01,RCP001,Completed
```

**Relationships**:
- `StudentID` → students.txt
- `ReceptionistID` → users.txt (where Role = "receptionist")

---

### 9. subject_requests.txt
**Purpose**: Manages student requests for subject enrollment changes (add, drop, change).

**Format**: `RequestID,StudentID,RequestType,ClassID,AlternativeClassID,Reason,RequestDate,Status,ProcessedDate,Comments`

**Fields**:
- `RequestID` (String): Unique identifier for the request
- `StudentID` (String): Reference to students.txt StudentID
- `RequestType` (String): Type of request (Add, Drop, Change)
- `ClassID` (String): Primary class involved in the request
- `AlternativeClassID` (String): Alternative class (for Change requests, may be empty)
- `Reason` (String): Student's reason for the request
- `RequestDate` (Date): Date request was submitted (YYYY-MM-DD)
- `Status` (String): Request status (Pending, Approved, Rejected)
- `ProcessedDate` (Date): Date request was processed (YYYY-MM-DD, empty if pending)
- `Comments` (String): Admin/receptionist comments on the request

**Example**:
```
REQ001,S001,Add,CLS004,,Want to improve math skills,2024-01-20,Approved,2024-01-21,Request approved - Math Secondary 4 added
REQ007,S001,Change,CLS014,CLS009,Schedule conflict,2024-02-20,Pending,,Checking schedule availability
```

**Relationships**:
- `StudentID` → students.txt
- `ClassID` → classes.txt
- `AlternativeClassID` → classes.txt

---

## Data Relationships

### Entity Relationship Overview

```
Users (1:N) → Students
Users (1:N) → Tutors → (1:N) Classes
Subjects (1:N) → Classes
Classes (1:N) → Schedules
Students (M:N) → Subjects (via student_subjects.txt)
Students (1:N) → Payments
Students (1:N) → Subject_Requests
Students (1:1) → Student_Balances
```

### Key Business Rules

1. **Student Enrollment**: Students can enroll in multiple subjects, and each subject can have multiple students enrolled.

2. **Class Assignment**: Each class is assigned to exactly one tutor and covers exactly one subject at a specific level.

3. **Payment Tracking**: Each student has one balance record that summarizes their financial status, with detailed transaction history in payments.txt.

4. **Request Management**: Students can submit requests to add, drop, or change subjects, which must be processed by administrative staff.

5. **Scheduling**: Each class has scheduled time slots that include day, time, and room assignments.

### Data Integrity Considerations

- All ID fields should be unique within their respective files
- Foreign key relationships should be maintained between files
- Date formats should be consistent (YYYY-MM-DD for dates, YYYY-MM-DD HH:MM:SS for timestamps)
- Numeric fields (fees, amounts) should use decimal format with 2 decimal places
- Status fields should use predefined values to ensure consistency

### File Dependencies

The files have the following dependency order for data integrity:
1. `users.txt` (base user data)
2. `students.txt` (extends user data)
3. `subject.txt` (subject definitions)
4. `classes.txt` (requires users and subjects)
5. `schedule.txt` (requires classes)
6. `student_subjects.txt` (requires students and subjects)
7. `student_balances.txt` (requires students)
8. `payments.txt` (requires students and users)
9. `subject_requests.txt` (requires students and classes) 