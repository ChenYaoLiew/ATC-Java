# Student Dashboard Data Integration Summary

## Overview
The student dashboard has been successfully integrated with all relevant data files in the `untitled/data/` directory. All hardcoded sample data has been replaced with dynamic data reading from actual text files.

## Data Files Linked to Student Dashboard

### 1. **users.txt** - User Authentication
- **Format**: `ID,username,password,role,name`
- **Purpose**: Used to find student ID based on username during login
- **Integration**: `findStudentId()` method maps username to student ID

### 2. **students.txt** - Student Profile Information
- **Format**: `ID,name,ic,email,phone,address,registration_date,level,status`
- **Purpose**: Loads student profile information (name, contact details)
- **Integration**: `loadProfile()` method reads student data for profile tab

### 3. **schedule.txt** - Class Schedule Times
- **Format**: `schedule_id,class_id,day,start_time,end_time,room`
- **Purpose**: Provides schedule timings for classes
- **Integration**: `loadSchedule()` method combines with other files to build complete schedule

### 4. **classes.txt** - Class Information
- **Format**: `class_id,subject_id,tutor_id,fee`
- **Purpose**: Links subjects to tutors and provides fee information
- **Integration**: Used by `loadSchedule()` to get tutor and subject information

### 5. **subject.txt** - Subject Details
- **Format**: `subject_id,subject_name,level`
- **Purpose**: Provides subject names and levels
- **Integration**: Used by `loadSchedule()` and `loadRequests()` to display subject information

### 6. **student_subjects.txt** - Student Enrollments
- **Format**: `student_id,subject_id`
- **Purpose**: Links students to their enrolled subjects
- **Integration**: `loadSchedule()` uses this to show only student's enrolled subjects

### 7. **subject_requests.txt** - Subject Change Requests
- **Format**: `req_id,student_id,action,new_class_id,old_class_id,reason,request_date,status,response_date,response`
- **Purpose**: Stores student subject change requests
- **Integration**: `loadRequests()` loads student's requests, `saveRequestToFile()` saves new requests

### 8. **payments.txt** - Payment Records
- **Format**: `payment_id,student_id,subject_id,amount,method,datetime,month,receipt_id,status`
- **Purpose**: Tracks student payments
- **Integration**: `loadPayments()` displays payment history for current student

### 9. **student_balances.txt** - Outstanding Balances
- **Format**: `student_id,total_due,total_paid,outstanding,last_payment_date,next_due_date,status`
- **Purpose**: Tracks student outstanding balances
- **Integration**: `calculateTotalBalance()` shows current outstanding amount

## Key Integration Features

### Dynamic Data Loading
- All data is loaded dynamically based on the current student's ID
- Student ID is determined from username during login
- Data is filtered to show only information relevant to the logged-in student

### Schedule Integration
The schedule tab combines multiple data sources:
1. **student_subjects.txt** - Find enrolled subjects
2. **subject.txt** - Get subject names and levels
3. **classes.txt** - Get tutor assignments
4. **schedule.txt** - Get class timings and rooms
5. **users.txt** - Get tutor names

### Request System
- New requests are saved to `subject_requests.txt`
- Request IDs are auto-generated based on existing requests
- Requests are displayed with proper subject information

### Payment Tracking
- Payment history from `payments.txt`
- Outstanding balance from `student_balances.txt`
- Status indicators for pending/completed payments

## Methods for Data Access

### Core Data Reading Methods
- `readDataFile(filename)` - Generic file reader
- `createDataMap(data, keyIndex)` - Creates lookup maps
- `findStudentId(username)` - Maps username to student ID

### Tab-Specific Load Methods
- `loadSchedule()` - Loads class schedule
- `loadRequests()` - Loads subject requests
- `loadPayments()` - Loads payment history
- `loadProfile()` - Loads student profile

### Utility Methods
- `formatTime(time24)` - Converts 24-hour to 12-hour format
- `formatDateTime(dateTime)` - Formats date strings
- `calculateTotalBalance()` - Calculates outstanding balance
- `getNextRequestId()` - Generates unique request IDs

## Data Flow Example

When a student logs in as "student":
1. `findStudentId("student")` returns "S001"
2. `loadSchedule()` finds enrolled subjects for S001
3. Schedule is built by joining multiple data files
4. Similar process for requests, payments, and profile

## File Structure
```
untitled/data/
├── users.txt          - Authentication data
├── students.txt       - Student profiles
├── schedule.txt       - Class schedules
├── classes.txt        - Class information
├── subject.txt        - Subject details
├── student_subjects.txt - Student enrollments
├── subject_requests.txt - Subject requests
├── payments.txt       - Payment records
└── student_balances.txt - Outstanding balances
```

## Benefits of This Integration
1. **Real-time Data**: Dashboard shows current data from files
2. **Scalability**: Easy to add new students, subjects, or classes
3. **Maintainability**: Data is centralized in text files
4. **Flexibility**: Easy to modify data structure if needed
5. **Consistency**: All parts of the application use the same data source

## Sample Data Usage
- Student "Javion" (ID: S001) can see their Math and English classes
- Their schedule shows proper timings, tutors, and rooms
- Payment history shows actual transactions
- Outstanding balance reflects real amounts

All data files are now fully integrated and the student dashboard displays real, dynamic data instead of hardcoded samples. 