public class Subject {
    private String name;
    private String level;
    private double monthlyFee;
    private String tutorUsername;

    public Subject(String name, String level, double monthlyFee, String tutorUsername) {
        this.name = name;
        this.level = level;
        this.monthlyFee = monthlyFee;
        this.tutorUsername = tutorUsername;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public double getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(double monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public String getTutorUsername() {
        return tutorUsername;
    }

    public void setTutorUsername(String tutorUsername) {
        this.tutorUsername = tutorUsername;
    }

    @Override
    public String toString() {
        return name + " (" + level + ") - $" + monthlyFee + "/month";
    }
}