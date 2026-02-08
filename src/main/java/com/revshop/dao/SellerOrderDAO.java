package com.revshop.dao;

import java.sql.*;
import com.revshop.util.DBUtil;

public class SellerOrderDAO {

    public void viewOrdersBySeller(int sellerId) {

        String sql = """
            SELECT o.order_id, o.order_date, o.status, p.name, oi.quantity
            FROM orders o
            JOIN order_items oi ON o.order_id = oi.order_id
            JOIN products p ON oi.product_id = p.product_id
            WHERE p.seller_id = ?
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- ORDERS FOR YOUR PRODUCTS ---");
            while (rs.next()) {
                System.out.println(
                        "Order ID: " + rs.getInt("order_id") +
                        " | Product: " + rs.getString("name") +
                        " | Qty: " + rs.getInt("quantity") +
                        " | Status: " + rs.getString("status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
