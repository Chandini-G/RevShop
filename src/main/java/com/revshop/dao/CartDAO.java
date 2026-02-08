package com.revshop.dao;

import java.sql.*;
import com.revshop.util.DBUtil;

public class CartDAO {

    public boolean addToCart(int buyerId, int productId, int quantity) {
        String sql = "INSERT INTO cart (buyer_id, product_id, quantity) VALUES (?,?,?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void viewCart(int buyerId) {
        String sql = """
            SELECT c.cart_id, p.name, p.price, c.quantity
            FROM cart c
            JOIN products p ON c.product_id = p.product_id
            WHERE c.buyer_id = ?
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- YOUR CART ---");
            while (rs.next()) {
                System.out.println(
                        rs.getInt("cart_id") + " | " +
                        rs.getString("name") + " | ₹" +
                        rs.getDouble("price") + " | Qty: " +
                        rs.getInt("quantity")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearCart(int buyerId) {
        String sql = "DELETE FROM cart WHERE buyer_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
