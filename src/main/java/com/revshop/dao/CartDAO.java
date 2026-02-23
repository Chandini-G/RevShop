package com.revshop.dao;

import java.sql.*;
import com.revshop.util.DBUtil;

public class CartDAO {
    
    public boolean addToCart(int buyerId, int productId, int quantity) {
        // Check if already in cart
        String checkSql = "SELECT quantity FROM cart WHERE buyer_id = ? AND product_id = ?";
        String updateSql = "UPDATE cart SET quantity = quantity + ? WHERE buyer_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO cart (buyer_id, product_id, quantity) VALUES (?, ?, ?)";
        
        try (Connection con = DBUtil.getConnection()) {
            
            // Check if item exists
            PreparedStatement check = con.prepareStatement(checkSql);
            check.setInt(1, buyerId);
            check.setInt(2, productId);
            ResultSet rs = check.executeQuery();
            
            if (rs.next()) {
                // Update quantity
                PreparedStatement update = con.prepareStatement(updateSql);
                update.setInt(1, quantity);
                update.setInt(2, buyerId);
                update.setInt(3, productId);
                return update.executeUpdate() > 0;
            } else {
                // Insert new
                PreparedStatement insert = con.prepareStatement(insertSql);
                insert.setInt(1, buyerId);
                insert.setInt(2, productId);
                insert.setInt(3, quantity);
                return insert.executeUpdate() > 0;
            }
            
        } catch (Exception e) {
            System.out.println("Error adding to cart: " + e.getMessage());
            return false;
        }
    }
    
    public boolean removeFromCart(int buyerId, int productId, int removeQuantity) {
        String checkSql = "SELECT quantity FROM cart WHERE buyer_id = ? AND product_id = ?";
        String updateSql = "UPDATE cart SET quantity = quantity - ? WHERE buyer_id = ? AND product_id = ?";
        String deleteSql = "DELETE FROM cart WHERE buyer_id = ? AND product_id = ?";
        
        try (Connection con = DBUtil.getConnection()) {
            
            // Get current quantity
            PreparedStatement check = con.prepareStatement(checkSql);
            check.setInt(1, buyerId);
            check.setInt(2, productId);
            ResultSet rs = check.executeQuery();
            
            if (rs.next()) {
                int currentQty = rs.getInt("quantity");
                
                if (removeQuantity >= currentQty) {
                    // Remove completely
                    PreparedStatement delete = con.prepareStatement(deleteSql);
                    delete.setInt(1, buyerId);
                    delete.setInt(2, productId);
                    return delete.executeUpdate() > 0;
                } else {
                    // Reduce quantity
                    PreparedStatement update = con.prepareStatement(updateSql);
                    update.setInt(1, removeQuantity);
                    update.setInt(2, buyerId);
                    update.setInt(3, productId);
                    return update.executeUpdate() > 0;
                }
            }
            return false;
            
        } catch (Exception e) {
            System.out.println("Error removing from cart: " + e.getMessage());
            return false;
        }
    }
    
    public void clearCart(int buyerId) {
        String sql = "DELETE FROM cart WHERE buyer_id = ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, buyerId);
            ps.executeUpdate();
            
        } catch (Exception e) {
            System.out.println("Error clearing cart: " + e.getMessage());
        }
    }
    
    public ResultSet getCartItems(int buyerId) throws SQLException {
        String sql = "SELECT c.product_id, p.name, p.price, c.quantity, " +
                    "(p.price * c.quantity) as total " +
                    "FROM cart c JOIN products p ON c.product_id = p.product_id " +
                    "WHERE c.buyer_id = ?";
        
        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, buyerId);
        return ps.executeQuery();
    }
}