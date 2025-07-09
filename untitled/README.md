# Tuition Center Management System

This is a Java-based management system for a tuition center, with different user roles (Admin, Tutor, Receptionist, Student) and their respective functionalities.

## Features

### Admin
- Login with username and password
- Register and delete tutors
- Assign tutors to subjects and levels
- Register and delete receptionists
- View monthly income reports by level and subject
- Update own profile

### Tutor (Not fully implemented yet)
- Login with username and password
- Add class information
- Update and delete class information
- View list of enrolled students
- Update own profile

### Receptionist (Not fully implemented yet)
- Login with username and password
- Register students and enroll them in subjects
- Update subject enrollment
- Accept payments and generate receipts
- Delete completed students
- Update own profile

### Student (Not fully implemented yet)
- Login with username and password
- View class schedules
- Send and delete enrollment change requests
- View payment status
- Update own profile

## How to Run

1. Compile all Java files:
   ```
   javac *.java
   ```

2. Run the main class:
   ```
   java Main
   ```

   Alternatively, you can run the admin_dashboard class directly to test the admin interface:
   ```
   java admin_dashboard
   ```

## Default Login Credentials

- Admin:
  - Username: admin
  - Password: admin123

- Tutor (Sample):
  - Username: tutor1
  - Password: tutor123

- Receptionist (Sample):
  - Username: rec1
  - Password: rec123

## Project Structure

- `User.java`: Base class for all users
- `Admin.java`: Admin functionality
- `Tutor.java`: Tutor functionality
- `Receptionist.java`: Receptionist functionality
- `Subject.java`: Subject information
- `Payment.java`: Payment records
- `Main.java`: Main entry point
- `admin_dashboard.java`: GUI for admin functions

## Implementation Notes

This is a simple in-memory implementation without a database. All data is stored in static lists within the Admin class and will be lost when the program exits.

For a production system, database integration would be required for data persistence.

## Future Enhancements

- Implement database storage
- Complete Tutor, Receptionist, and Student interfaces
- Add data validation and error handling
- Implement file-based storage for reports
- Add search functionality for users and subjects 