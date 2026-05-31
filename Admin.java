package auth;

public class Admin extends User {
    private String adminLevel;

    public Admin(int userId, String username, String password, String email, String adminLevel) {
        super(userId, username, password, "admin", email);
        this.adminLevel = adminLevel;
    }

    public String getAdminLevel() { return adminLevel; }

    @Override
    public void displayInfo() {
        System.out.println("Admin: " + username + " | Level: " + adminLevel);
    }
}