package auth;

import db.DatabaseManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    public LoginPage() {
        setTitle("Faculty Portal - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Faculty Portal Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Role:"), gbc);
        roleBox = new JComboBox<>(new String[]{"student", "faculty", "admin"});
        gbc.gridx = 1;
        add(roleBox, gbc);

        JButton loginBtn = new JButton("Login");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(loginBtn, gbc);

        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridy = 5;
        add(statusLabel, gbc);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = (String) roleBox.getSelectedItem();
        
            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter username and password!");
                return;
            }
        
            boolean valid = DatabaseManager.validateLogin(username, password, role);
            if (valid) {
                dispose();
                switch (role) {
                    case "student":
                        new student.StudentDashboard(username);
                        break;
                    case "faculty":
                        new faculty.FacultyDashboard(username);
                        break;
                    case "admin":
                        new admin.AdminDashboard(username);
                        break;
                }
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Invalid credentials!");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}