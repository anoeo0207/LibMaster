package com.example.libmaster.Controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.libmaster.Models.Members;

public class DatabaseController {

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/LibMasterServer";  // Use your database name
    private static final String USER = "root";  // Default XAMPP MySQL username
    private static final String PASSWORD = "";  // Default XAMPP MySQL password is empty

    // Method to fetch data from the database
    public static ObservableList<Members> fetchMembers() {
        ObservableList<Members> membersList = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT * FROM members";  // Select all members
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                int numBookIssue = rs.getInt("numBookIssue");

                membersList.add(new Members(id, name, phone, numBookIssue));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return membersList;
    }

    // Hàm thêm thành viên mới vào cơ sở dữ liệu
    public static boolean addMember(String name, String phone, int numBookIssue) {
        String insertQuery = "INSERT INTO members (name, phone, numBookIssue) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setInt(3, numBookIssue);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
