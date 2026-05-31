package student;

import db.DatabaseManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class StudentDashboard extends JFrame {
    private JTextField searchField;
    private DefaultTableModel facultyModel;
    private DefaultTableModel requestModel;
    private int studentId;

    public StudentDashboard(String username) {
        setTitle("Student Dashboard - " + username);
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        studentId = getStudentId(username);

        JLabel titleLabel = new JLabel("Student Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1 - Search Faculty
        JPanel searchPanel = new JPanel(new BorderLayout());
        JPanel searchBar = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        searchBar.add(new JLabel("Search Faculty:"));
        searchBar.add(searchField);
        searchBar.add(searchBtn);

        String[] facCols = {"Faculty ID", "Name", "Department", "Cabin", "Status", "Office Hours", "Remarks"};
        facultyModel = new DefaultTableModel(facCols, 0);
        JTable facultyTable = new JTable(facultyModel);
        JScrollPane facScroll = new JScrollPane(facultyTable);

        JButton requestBtn = new JButton("Request Appointment");
        searchPanel.add(searchBar, BorderLayout.NORTH);
        searchPanel.add(facScroll, BorderLayout.CENTER);
        searchPanel.add(requestBtn, BorderLayout.SOUTH);
        tabbedPane.addTab("Search Faculty", searchPanel);

        // Tab 2 - My Requests
        JPanel requestPanel = new JPanel(new BorderLayout());
        String[] reqCols = {"Appt ID", "Faculty ID", "Purpose", "Preferred Time", "Status"};
        requestModel = new DefaultTableModel(reqCols, 0);
        JTable requestTable = new JTable(requestModel);
        JScrollPane reqScroll = new JScrollPane(requestTable);
        JButton refreshBtn = new JButton("Refresh Requests");
        requestPanel.add(reqScroll, BorderLayout.CENTER);
        requestPanel.add(refreshBtn, BorderLayout.SOUTH);
        tabbedPane.addTab("My Requests", requestPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Logout");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(logoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> searchFaculty());
        requestBtn.addActionListener(e -> requestAppointment(facultyTable));
        refreshBtn.addActionListener(e -> loadMyRequests());
        logoutBtn.addActionListener(e -> {
            dispose();
            new auth.LoginPage();
        });

        loadAllFaculty();
        loadMyRequests();
        setVisible(true);
    }

    private int getStudentId(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT s.student_id FROM students s JOIN users u ON s.user_id = u.user_id WHERE u.username=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("student_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void loadAllFaculty() {
        facultyModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM faculty";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                facultyModel.addRow(new Object[]{
                    rs.getInt("faculty_id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getString("cabin_location"),
                    rs.getString("status"),
                    rs.getString("office_hours"),
                    rs.getString("remarks")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchFaculty() {
        facultyModel.setRowCount(0);
        String keyword = searchField.getText().trim();
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM faculty WHERE name LIKE ? OR department LIKE ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                facultyModel.addRow(new Object[]{
                    rs.getInt("faculty_id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getString("cabin_location"),
                    rs.getString("status"),
                    rs.getString("office_hours"),
                    rs.getString("remarks")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void requestAppointment(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a faculty first!");
            return;
        }
        int facultyId = (int) facultyModel.getValueAt(selectedRow, 0);
        String purpose = JOptionPane.showInputDialog(this, "Enter purpose of appointment:");
        if (purpose == null || purpose.trim().isEmpty()) return;
        String time = JOptionPane.showInputDialog(this, "Enter preferred time (e.g. 2026-04-28 14:00:00):");
        if (time == null || time.trim().isEmpty()) return;

        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "INSERT INTO appointments (student_id, faculty_id, purpose, preferred_time, status) VALUES (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);
            ps.setInt(2, facultyId);
            ps.setString(3, purpose);
            ps.setString(4, time);
            ps.setString(5, "Pending");
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Appointment request sent successfully!");
            loadMyRequests();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadMyRequests() {
        requestModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM appointments WHERE student_id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                requestModel.addRow(new Object[]{
                    rs.getInt("appt_id"),
                    rs.getInt("faculty_id"),
                    rs.getString("purpose"),
                    rs.getString("preferred_time"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}