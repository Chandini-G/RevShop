package com.revshop.service;

import java.sql.ResultSet;
import com.revshop.dao.FavouriteDAO;
import com.revshop.dao.CartDAO;

public class FavouriteService {
    private FavouriteDAO favouriteDAO = new FavouriteDAO();
    private CartDAO cartDAO = new CartDAO();
    
    public void addToFavourites(int buyerId, int productId) {
        if (favouriteDAO.addFavourite(buyerId, productId)) {
            System.out.println("✅ Added to favourites!");
        } else {
            System.out.println("❌ Failed to add to favourites.");
        }
    }
    
    public void removeFromFavourites(int buyerId, int productId) {
        if (favouriteDAO.removeFavourite(buyerId, productId)) {
            System.out.println("✅ Removed from favourites!");
        } else {
            System.out.println("❌ Failed to remove from favourites.");
        }
    }
    
    public void viewFavourites(int buyerId) {
        try {
            ResultSet rs = favouriteDAO.getFavourites(buyerId);
            
            System.out.println("\n⭐ YOUR FAVOURITES");
            System.out.println("====================================");
            
            boolean found = false;
            while (rs != null && rs.next()) {
                found = true;
                System.out.println("Product ID: " + rs.getInt("product_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Price: ₹" + rs.getDouble("price"));
                System.out.println("------------------------");
            }
            
            if (!found) {
                System.out.println("No favourites added yet.");
            }
            
            if (rs != null) {
                rs.close();
            }
            
        } catch (Exception e) {
            System.out.println("Error viewing favourites: " + e.getMessage());
        }
    }
    
    // NEW: Move favourite to cart
    public void moveToCart(int buyerId, int productId, int quantity) {
        if (quantity <= 0) {
            System.out.println("❌ Quantity must be greater than 0.");
            return;
        }
        
        // First remove from favourites
        if (favouriteDAO.removeFavourite(buyerId, productId)) {
            // Then add to cart
            if (cartDAO.addToCart(buyerId, productId, quantity)) {
                System.out.println("✅ Moved from favourites to cart!");
            } else {
                System.out.println("❌ Added to cart but couldn't remove from favourites.");
            }
        } else {
            System.out.println("❌ Product not found in favourites.");
        }
    }
}