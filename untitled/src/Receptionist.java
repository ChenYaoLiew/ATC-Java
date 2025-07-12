public class Receptionist extends User {
    private String employeeId;

    public Receptionist(String username, String password, String name, String email, String contactNumber, String employeeId) {
        super(username, password, name, email, contactNumber, "receptionist");
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "Receptionist: " + getName() + " (ID: " + employeeId + ")";
    }
}