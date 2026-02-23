package com.revshop.service;

import java.sql.ResultSet;
import com.revshop.dao.SellerOrderDAO;


public class SellerOrderService {
    private SellerOrderDAO sellerOrderDAO = new SellerOrderDAO();
    
    public void viewSellerOrders(int sellerId) {
        try {
            ResultSet rs = sellerOrderDAO.getOrdersBySeller(sellerId);
            
            System.out.println("\n📦 ORDERS FOR YOUR PRODUCTS");
            System.out.println("==========================================");
            
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Order ID: " + rs.getInt("order_id"));
                System.out.println("Date: " + rs.getTimestamp("order_date"));
                System.out.println("Amount: ₹" + rs.getDouble("total_amount"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("------------------------------------------");
            }
            
            if (!found) {
                System.out.println("No orders received yet.");
            }
            
            rs.close();
            
        } catch (Exception e) {
            System.out.println("Error viewing seller orders: " + e.getMessage());
        }
    }
}