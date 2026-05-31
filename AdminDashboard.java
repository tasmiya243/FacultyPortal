package admin;

import db.DatabaseManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;

    public AdminDashboard(String username) {
        setTitle("Admin Dashboard - " + username);
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"User ID", "Username", "Role", "Email"};
        tableModel = new DefaultTableModel(columns, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loadBtn = new JButton("Load All Users");
        JButton addFacultyBtn = new JButton("Add Faculty");
        JButton addStudentBtn = new JButton("Add Student");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(loadBtn);
        buttonPanel.add(addFacultyBtn);
        buttonPanel.add(addStudentBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(logoutBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> loadUsers());
        deleteBtn.addActionListener(e -> deleteUser());
        addFacultyBtn.addActionListener(e -> addFaculty());
        addStudentBtn.addActionListener(e -> addStudent());
        logoutBtn.addActionListener(e -> {
            dispose();
            new auth.LoginPage();
        });

        loadUsers();
        setVisible(true);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT user_id, username, role, email FROM users";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("email")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addFaculty() {
        JTextField unameField = new JTextField();
        JTextField passField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField deptField = new JTextField();
        JTextField cabinField = new JTextField();
        JTextField hoursField = new JTextField();

        Object[] fields = {
            "Username:", unameField,
            "Password:", passField,
            "Full Name:", nameField,
            "Department:", deptField,
            "Cabin Location:", cabinField,
            "Office Hours:", hoursField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add New Faculty", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseManager.getConnection()) {
                // Insert into users
                String userQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, 'faculty')";
                PreparedStatement ps1 = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
                ps1.setString(1, unameField.getText().trim());
                ps1.setString(2, passField.getText().trim());
                ps1.executeUpdate();

                ResultSet keys = ps1.getGeneratedKeys();
                if (keys.next()) {
                    int newUserId = keys.getInt(1);
                    // Insert into faculty
                    String facQuery = "INSERT INTO faculty (user_id, name, department, cabin_location, status, office_hours) VALUES (?, ?, ?, ?, 'Available', ?)";
                    PreparedStatement ps2 = conn.prepareStatement(facQuery);
                    ps2.setInt(1, newUserId);
                    ps2.setString(2, nameField.getText().trim());
                    ps2.setString(3, deptField.getText().trim());
                    ps2.setString(4, cabinField.getText().trim());
                    ps2.setString(5, hoursField.getText().trim());
                    ps2.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Faculty added successfully!");
                loadUsers();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void addStudent() {
        JTextField unameField = new JTextField();
        JTextField passField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField rollField = new JTextField();
        JTextField deptField = new JTextField();
        JTextField semField = new JTextField();

        Object[] fields = {
            "Username:", unameField,
            "Password:", passField,
            "Full Name:", nameField,
            "Roll Number:", rollField,
            "Department:", deptField,
            "Semester:", semField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add New Student", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseManager.getConnection()) {
                String userQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, 'student')";
                PreparedStatement ps1 = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
                ps1.setString(1, unameField.getText().trim());
                ps1.setString(2, passField.getText().trim());
                ps1.executeUpdate();

                ResultSet keys = ps1.getGeneratedKeys();
                if (keys.next()) {
                    int newUserId = keys.getInt(1);
                    String stuQuery = "INSERT INTO students (user_id, name, roll_number, department, semester) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement ps2 = conn.prepareStatement(stuQuery);
                    ps2.setInt(1, newUserId);
                    ps2.setString(2, nameField.getText().trim());
                    ps2.setString(3, rollField.getText().trim());
                    ps2.setString(4, deptField.getText().trim());
                    ps2.setInt(5, Integer.parseInt(semField.getText().trim()));
                    ps2.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Student added successfully!");
                loadUsers();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete!");
            return;
        }
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?");
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseManager.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE user_id=?");
                ps.setInt(1, userId);
                ps.executeUpdate();
                loadUsers();
                JOptionPane.showMessageDialog(this, "User deleted!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}