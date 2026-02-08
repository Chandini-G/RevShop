package com.revshop.dao;

import java.sql.*;
import com.revshop.util.DBUtil;

public class NotificationDAO {

    // Add notification
    public void addNotification(int userId, String message) {

        String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // View notifications
    public void viewNotifications(int userId) {

        String sql = """
            SELECT message, created_at
            FROM notifications
            WHERE user_id = ?
            ORDER BY created_at DESC
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== NOTIFICATIONS =====");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("----------------------------");
                System.out.println(rs.getTimestamp("created_at"));
                System.out.println(rs.getString("message"));
            }

            if (!found) {
                System.out.println("No notifications found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
