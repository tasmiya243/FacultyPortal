package faculty;

import db.DatabaseManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class FacultyDashboard extends JFrame {
    private JLabel statusLabel;
    private JComboBox<String> statusBox;
    private JTextField officeHoursField;
    private JTextField remarksField;
    private DefaultTableModel requestModel;
    private int facultyId;

    public FacultyDashboard(String username) {
        setTitle("Faculty Dashboard - " + username);
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        facultyId = getFacultyId(username);

        JLabel titleLabel = new JLabel("Faculty Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Update Status:"), gbc);
        statusBox = new JComboBox<>(new String[]{
            "Available", "Busy", "In Class", "In Meeting", "On Leave"
        });
        gbc.gridx = 1;
        topPanel.add(statusBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Office Hours:"), gbc);
        officeHoursField = new JTextField(15);
        gbc.gridx = 1;
        topPanel.add(officeHoursField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("Remarks:"), gbc);
        remarksField = new JTextField(15);
        gbc.gridx = 1;
        topPanel.add(remarksField, gbc);

        JButton updateBtn = new JButton("Update Profile");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        topPanel.add(updateBtn, gbc);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.GREEN);
        gbc.gridy = 4;
        topPanel.add(statusLabel, gbc);

        add(topPanel, BorderLayout.NORTH);

        JLabel reqLabel = new JLabel("Appointment Requests", SwingConstants.CENTER);
        reqLabel.setFont(new Font("Arial", Font.BOLD, 16));

        String[] columns = {"Appt ID", "Student ID", "Purpose", "Preferred Time", "Status"};
        requestModel = new DefaultTableModel(columns, 0);
        JTable requestTable = new JTable(requestModel);
        JScrollPane scrollPane = new JScrollPane(requestTable);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(reqLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton approveBtn = new JButton("Approve Selected");
        JButton rejectBtn = new JButton("Reject Selected");
        JButton logoutBtn = new JButton("Logout");

        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(logoutBtn);
        add(btnPanel, BorderLayout.SOUTH);

        updateBtn.addActionListener(e -> updateProfile());
        approveBtn.addActionListener(e -> updateRequest(requestTable, "Approved"));
        rejectBtn.addActionListener(e -> updateRequest(requestTable, "Rejected"));
        logoutBtn.addActionListener(e -> {
            dispose();
            new auth.LoginPage();
        });

        loadRequests();
        setVisible(true);
    }

    private int getFacultyId(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT f.faculty_id FROM faculty f JOIN users u ON f.user_id = u.user_id WHERE u.username=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("faculty_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void updateProfile() {
        String status = (String) statusBox.getSelectedItem();
        String officeHours = officeHoursField.getText().trim();
        String remarks = remarksField.getText().trim();

        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "UPDATE faculty SET status=?, office_hours=?, remarks=? WHERE faculty_id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, status);
            ps.setString(2, officeHours);
            ps.setString(3, remarks);
            ps.setInt(4, facultyId);
            ps.executeUpdate();
            statusLabel.setText("Profile updated successfully!");
        } catch (SQLException e) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void loadRequests() {
        requestModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM appointments WHERE faculty_id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                requestModel.addRow(new Object[]{
                    rs.getInt("appt_id"),
                    rs.getInt("student_id"),
                    rs.getString("purpose"),
                    rs.getString("preferred_time"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRequest(JTable table, String status) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request first!");
            return;
        }
        int apptId = (int) requestModel.getValueAt(selectedRow, 0);
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "UPDATE appointments SET status=? WHERE appt_id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, status);
            ps.setInt(2, apptId);
            ps.executeUpdate();
            loadRequests();
            JOptionPane.showMessageDialog(this, "Request " + status + " successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}