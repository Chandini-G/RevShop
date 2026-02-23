package com.revshop.service;

import java.sql.ResultSet;
import com.revshop.dao.CartDAO;

public class CartService {
    private CartDAO cartDAO = new CartDAO();
    
    public void addToCart(int buyerId, int productId, int quantity) {
        if (quantity <= 0) {
            System.out.println("❌ Quantity must be greater than 0.");
            return;
        }
        
        if (cartDAO.addToCart(buyerId, productId, quantity)) {
            System.out.println("✅ Added to cart successfully!");
        } else {
            System.out.println("❌ Failed to add to cart.");
        }
    }
    
    public void removeFromCart(int buyerId, int productId, int quantity) {
        if (quantity <= 0) {
            System.out.println("❌ Quantity must be greater than 0.");
            return;
        }
        
        if (cartDAO.removeFromCart(buyerId, productId, quantity)) {
            System.out.println("✅ Removed from cart successfully!");
        } else {
            System.out.println("❌ Failed to remove from cart.");
        }
    }
    
    public void viewCart(int buyerId) {
        try {
            ResultSet rs = cartDAO.getCartItems(buyerId);
            
            System.out.println("\n🛒 YOUR CART");
            System.out.println("=".repeat(80));
            
            System.out.printf("%-5s %-25s %-15s %-10s %-15s%n",
                "ID", "PRODUCT NAME", "PRICE", "QUANTITY", "TOTAL");
            System.out.println("-".repeat(80));
            
            double grandTotal = 0;
            boolean hasItems = false;
            
            while (rs.next()) {
                hasItems = true;
                double itemTotal = rs.getDouble("total");
                grandTotal += itemTotal;
                
                String productName = rs.getString("name");
                if (productName.length() > 25) {
                    productName = productName.substring(0, 22) + "...";
                }
                
                System.out.printf("%-5d %-25s ₹%-13.2f %-9d ₹%-13.2f%n",
                    rs.getInt("product_id"),
                    productName,
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    itemTotal);
            }
            
            if (!hasItems) {
                System.out.println("Your cart is empty.");
            } else {
                System.out.println("-".repeat(80));
                System.out.printf("%-45s ₹%.2f%n", "CART TOTAL:", grandTotal);
            }
            
            rs.close();
            
        } catch (Exception e) {
            System.out.println("Error viewing cart: " + e.getMessage());
        }
    }
}