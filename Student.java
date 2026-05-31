package auth;

public class Student extends User {
    private String rollNumber;
    private String department;
    private int semester;

    public Student(int userId, String username, String password, String email,
                   String rollNumber, String department, int semester) {
        super(userId, username, password, "student", email);
        this.rollNumber = rollNumber;
        this.department = department;
        this.semester = semester;
    }

    public String getRollNumber() { return rollNumber; }
    public String getDepartment() { return department; }
    public int getSemester() { return semester; }

    @Override
    public void displayInfo() {
        System.out.println("Student: " + username + " | Roll: " + rollNumber + " | Dept: " + department);
    }
}