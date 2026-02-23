package com.revshop.dao;

import java.sql.*;
import com.revshop.util.DBUtil;

public class NotificationDAO {
    
    public void addNotification(int userId, String title, String message) {
        String sql = "INSERT INTO notifications (user_id, title, message) VALUES (?, ?, ?)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, message);
            ps.executeUpdate();
            
        } catch (Exception e) {
            System.out.println("Error adding notification: " + e.getMessage());
        }
    }
    
    public ResultSet getNotifications(int userId) throws SQLException {
        String sql = "SELECT title, message, created_at FROM notifications " +
                    "WHERE user_id = ? ORDER BY created_at DESC";
        
        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, userId);
        return ps.executeQuery();
    }
}