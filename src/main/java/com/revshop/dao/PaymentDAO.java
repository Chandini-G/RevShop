package com.revshop.dao;

import java.sql.*;
import com.revshop.util.DBUtil;

public class PaymentDAO {
    
    public boolean recordPayment(int orderId, String paymentMethod, double amount) {
        String sql = "INSERT INTO payments (order_id, payment_method, amount, payment_status) " +
                    "VALUES (?, UPPER(?), ?, 'SUCCESS')";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            ps.setString(2, paymentMethod);
            ps.setDouble(3, amount);
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error recording payment: " + e.getMessage());
            return false;
        }
    }
}