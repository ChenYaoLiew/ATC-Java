import java.util.ArrayList;
import java.util.List;

public class Tutor extends User {
    private List<String> subjects;
    private List<String> levels;
    private List<String> classSchedules;

    public Tutor(String username, String password, String name, String email, String contactNumber) {
        super(username, password, name, email, contactNumber, "tutor");
        this.subjects = new ArrayList<>();
        this.levels = new ArrayList<>();
        this.classSchedules = new ArrayList<>();
    }

    // Getters and setters for tutor-specific fields
    public List<String> getSubjects() {
        return subjects;
    }

    public void addSubject(String subject) {
        if (!subjects.contains(subject)) {
            subjects.add(subject);
        }
    }

    public void removeSubject(String subject) {
        subjects.remove(subject);
    }

    public List<String> getLevels() {
        return levels;
    }

    public void addLevel(String level) {
        if (!levels.contains(level)) {
            levels.add(level);
        }
    }

    public void removeLevel(String level) {
        levels.remove(level);
    }

    public List<String> getClassSchedules() {
        return classSchedules;
    }

    public void addClassSchedule(String schedule) {
        classSchedules.add(schedule);
    }

    public void removeClassSchedule(String schedule) {
        classSchedules.remove(schedule);
    }

    @Override
    public String toString() {
        return "Tutor: " + getName() +
               "\nSubjects: " + String.join(", ", subjects) +
               "\nLevels: " + String.join(", ", levels);
    }
}