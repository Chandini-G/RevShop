package com.revshop.service;

import java.sql.ResultSet;
import com.revshop.dao.NotificationDAO;

public class NotificationService {
    private NotificationDAO notificationDAO = new NotificationDAO();
    
    public void addNotification(int userId, String title, String message) {
        notificationDAO.addNotification(userId, title, message);
    }
    
    public void viewNotifications(int userId) {
        try {
            ResultSet rs = notificationDAO.getNotifications(userId);
            
            System.out.println("\n🔔 NOTIFICATIONS");
            System.out.println("=================");
            
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("[" + rs.getTimestamp("created_at") + "]");
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Message: " + rs.getString("message"));
                System.out.println("------------------");
            }
            
            if (!found) {
                System.out.println("No notifications.");
            }
            
            rs.close();
            
        } catch (Exception e) {
            System.out.println("Error loading notifications: " + e.getMessage());
        }
    }
    
    public void sendOrderNotification(int buyerId, int orderId, double amount) {
        String title = "Order Confirmation";
        String message = "Order #" + orderId + " placed successfully! Amount: ₹" + amount;
        addNotification(buyerId, title, message);
        System.out.println("📧 Order confirmation sent to your notifications.");
    }
    
    public void sendLowStockNotification(int sellerId, String productName, int stock) {
        String title = "Low Stock Alert";
        String message = "⚠️ " + productName + " has only " + stock + " units left";
        addNotification(sellerId, title, message);
    }
    
    public void sendWelcomeNotification(int userId, String userName) {
        String title = "Welcome to RevShop";
        String message = "Welcome " + userName + "! Start shopping now.";
        addNotification(userId, title, message);
    }
    
    public void sendPromotionNotification(int userId) {
        String title = "Special Offer";
        String message = "🎉 Get 10% off on your first order with code WELCOME10";
        addNotification(userId, title, message);
    }
}