package com.revshop.dao;

import java.sql.*;
import com.revshop.util.DBUtil;

public class OrderDAO {

    // Place order (already used earlier)
    public boolean placeOrder(int buyerId, double totalAmount) {

        String sql = "INSERT INTO orders (buyer_id, total_amount, status) VALUES (?,?,?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ps.setDouble(2, totalAmount);
            ps.setString(3, "PLACED");

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 🔥 NEW: View Order History
    public void viewOrderHistory(int buyerId) {

        String sql = """
            SELECT order_id, order_date, total_amount, status
            FROM orders
            WHERE buyer_id = ?
            ORDER BY order_date DESC
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== YOUR ORDER HISTORY =====");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("-------------------------------");
                System.out.println("Order ID     : " + rs.getInt("order_id"));
                System.out.println("Order Date   : " + rs.getTimestamp("order_date"));
                System.out.println("Total Amount : ₹" + rs.getDouble("total_amount"));
                System.out.println("Status       : " + rs.getString("status"));
            }

            if (!found) {
                System.out.println("No orders found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
