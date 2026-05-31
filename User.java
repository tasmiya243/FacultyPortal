package auth;

public class User {
    protected int userId;
    protected String username;
    protected String password;
    protected String role;
    protected String email;

    public User(int userId, String username, String password, String role, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getEmail() { return email; }

    public void displayInfo() {
        System.out.println("User: " + username + " | Role: " + role);
    }
}