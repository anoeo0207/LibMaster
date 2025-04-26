package com.example.libmaster;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TerminalDatabaseApp {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/LibMasterServer?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = ""; // XAMPP default

        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to DB
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to database!");

            // Prepare SQL
            String sql = "SELECT * FROM books";
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Execute
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n📋 Books:");
            while (rs.next()) {
                int id = rs.getInt("isbn");
                String name = rs.getString("title");
                String email = rs.getString("author");

                System.out.printf("%d | %s | %s\n", id, name, email);
            }

            // Close connection
            rs.close();
            stmt.close();
            conn.close();
            System.out.println("\n✅ Done!");

        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Database error!");
            e.printStackTrace();
        }
    }
}
