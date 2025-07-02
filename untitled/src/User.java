public abstract class User {
    private String username;
    private String password;
    private String name;
    private String email;
    private String contactNumber;
    private String role; // "admin", "tutor", "receptionist", "student"

    public User(String username, String password, String name, String email, String contactNumber, String role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.role = role;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getRole() {
        return role;
    }

    // Method to update profile
    public void updateProfile(String name, String email, String contactNumber, String password) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        if (email != null && !email.isEmpty()) {
            this.email = email;
        }
        if (contactNumber != null && !contactNumber.isEmpty()) {
            this.contactNumber = contactNumber;
        }
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
    }
} 