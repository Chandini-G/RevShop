package com.revshop.dao;

import java.sql.*;
import com.revshop.util.DBUtil;

public class FavouriteDAO {
    
    public boolean addFavourite(int buyerId, int productId) {
        String sql = "INSERT INTO favourites (buyer_id, product_id) VALUES (?, ?)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, buyerId);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error adding favourite: " + e.getMessage());
            return false;
        }
    }
    
    public boolean removeFavourite(int buyerId, int productId) {
        String sql = "DELETE FROM favourites WHERE buyer_id = ? AND product_id = ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, buyerId);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error removing favourite: " + e.getMessage());
            return false;
        }
    }
    
    public ResultSet getFavourites(int buyerId) throws SQLException {
        String sql = "SELECT f.product_id, p.name, p.price " +
                    "FROM favourites f JOIN products p ON f.product_id = p.product_id " +
                    "WHERE f.buyer_id = ?";
        
        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, buyerId);
        return ps.executeQuery();
    }
}