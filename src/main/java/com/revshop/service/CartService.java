package com.revshop.service;

import java.sql.*;
import com.revshop.util.DBUtil;

public class CartService {

    // Add product → increase quantity if exists
    public void addToCart(int buyerId, int productId, int qty) {

        String checkSql = "SELECT quantity FROM cart WHERE buyer_id=? AND product_id=?";
        String updateSql = "UPDATE cart SET quantity = quantity + ? WHERE buyer_id=? AND product_id=?";
        String insertSql = "INSERT INTO cart (buyer_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection con = DBUtil.getConnection()) {

            PreparedStatement check = con.prepareStatement(checkSql);
            check.setInt(1, buyerId);
            check.setInt(2, productId);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                PreparedStatement update = con.prepareStatement(updateSql);
                update.setInt(1, qty);
                update.setInt(2, buyerId);
                update.setInt(3, productId);
                update.executeUpdate();
            } else {
                PreparedStatement insert = con.prepareStatement(insertSql);
                insert.setInt(1, buyerId);
                insert.setInt(2, productId);
                insert.setInt(3, qty);
                insert.executeUpdate();
            }

            System.out.println("Cart updated successfully");

        } catch (Exception e) {
            System.out.println("Unable to add to cart");
        }
    }

    public void viewCart(int buyerId) {

        String sql = """
            SELECT p.product_id, p.name, p.price, c.quantity,
                   (p.price * c.quantity) total
            FROM cart c
            JOIN products p ON c.product_id = p.product_id
            WHERE c.buyer_id = ?
        """;

        double totalAmount = 0;
        boolean hasItems = false;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- YOUR CART ---");
            while (rs.next()) {
                hasItems = true;
                double itemTotal = rs.getDouble("total");
                totalAmount += itemTotal;

                System.out.println(
                        rs.getInt("product_id") + " | " +
                        rs.getString("name") + " | Qty: " +
                        rs.getInt("quantity") + " | ₹" + itemTotal
                );
            }

            if (!hasItems) {
                System.out.println("Cart is empty");
            } else {
                System.out.println("Total Amount: ₹" + totalAmount);
            }

        } catch (Exception e) {
            System.out.println("Unable to view cart");
        }
    }

    // Remove based on quantity
    public void removeFromCart(int buyerId, int productId, int removeQty) {

        String checkSql = "SELECT quantity FROM cart WHERE buyer_id=? AND product_id=?";
        String updateSql = "UPDATE cart SET quantity = quantity - ? WHERE buyer_id=? AND product_id=?";
        String deleteSql = "DELETE FROM cart WHERE buyer_id=? AND product_id=?";

        try (Connection con = DBUtil.getConnection()) {

            PreparedStatement check = con.prepareStatement(checkSql);
            check.setInt(1, buyerId);
            check.setInt(2, productId);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                int currentQty = rs.getInt("quantity");

                if (removeQty >= currentQty) {
                    PreparedStatement del = con.prepareStatement(deleteSql);
                    del.setInt(1, buyerId);
                    del.setInt(2, productId);
                    del.executeUpdate();
                    System.out.println("Product removed from cart");
                } else {
                    PreparedStatement update = con.prepareStatement(updateSql);
                    update.setInt(1, removeQty);
                    update.setInt(2, buyerId);
                    update.setInt(3, productId);
                    update.executeUpdate();
                    System.out.println("Quantity updated in cart");
                }
            } else {
                System.out.println("Product not found in cart");
            }

        } catch (Exception e) {
            System.out.println("Unable to remove item");
        }
    }
}
