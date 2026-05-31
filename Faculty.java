package auth;

public class Faculty extends User {
    private String department;
    private String cabinLocation;
    private String status;
    private String officeHours;

    public Faculty(int userId, String username, String password, String email,
                   String department, String cabinLocation, String status, String officeHours) {
        super(userId, username, password, "faculty", email);
        this.department = department;
        this.cabinLocation = cabinLocation;
        this.status = status;
        this.officeHours = officeHours;
    }

    public String getDepartment() { return department; }
    public String getCabinLocation() { return cabinLocation; }
    public String getStatus() { return status; }
    public String getOfficeHours() { return officeHours; }

    @Override
    public void displayInfo() {
        System.out.println("Faculty: " + username + " | Dept: " + department + " | Status: " + status);
    }
}