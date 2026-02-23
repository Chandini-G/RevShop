package com.revshop.dao;

import java.sql.*;
import com.revshop.util.DBUtil;

public class OrderDAO {
    
    public int createOrder(int buyerId, double totalAmount, String deliveryAddress) {
        String sql = "INSERT INTO orders (buyer_id, total_amount, final_amount, delivery_address, status) " +
                    "VALUES (?, ?, ?, ?, 'PLACED')";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"order_id"})) {
            
            ps.setInt(1, buyerId);
            ps.setDouble(2, totalAmount);
            ps.setDouble(3, totalAmount);
            ps.setString(4, deliveryAddress);
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (Exception e) {
            System.out.println("Error creating order: " + e.getMessage());
        }
        return -1;
    }
    
    public boolean addOrderItem(int orderId, int productId, int quantity, double unitPrice) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            double totalPrice = unitPrice * quantity;
            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setDouble(4, unitPrice);
            ps.setDouble(5, totalPrice);
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error adding order item: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateProductStock(int productId, int quantity) {
        String sql = "UPDATE products SET stock = stock - ? WHERE product_id = ? AND stock >= ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error updating stock: " + e.getMessage());
            return false;
        }
    }
    
    public ResultSet getOrderHistory(int buyerId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE buyer_id = ? ORDER BY order_date DESC";
        
        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, buyerId);
        return ps.executeQuery();
    }
    
    public ResultSet getSellerOrders(int sellerId) throws SQLException {
        String sql = "SELECT o.order_id, o.order_date, o.total_amount, o.status, p.name, oi.quantity " +
                    "FROM orders o " +
                    "JOIN order_items oi ON o.order_id = oi.order_id " +
                    "JOIN products p ON oi.product_id = p.product_id " +
                    "WHERE p.seller_id = ? " +
                    "ORDER BY o.order_date DESC";
        
        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, sellerId);
        return ps.executeQuery();
    }
}