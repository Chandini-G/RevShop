package com.revshop.dao;

import java.sql.*;
import com.revshop.util.DBUtil;

public class UserDAO {
    
    public boolean checkEmailExists(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // Returns true if email exists
            
        } catch (Exception e) {
            System.out.println("Error checking email: " + e.getMessage());
            return false;
        }
    }
}