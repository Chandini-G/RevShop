package com.revshop.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.revshop.model.Review;
import com.revshop.util.DBUtil;

public class ReviewDAO {
    
    // Add a new review
    public boolean addReview(Review review) {
        String sql = "INSERT INTO reviews (product_id, buyer_id, rating, comment_text, review_date) " +
                    "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, review.getProductId());
            ps.setInt(2, review.getBuyerId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getCommentText());
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error adding review: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all reviews for a specific product
    public List<Review> getReviewsByProduct(int productId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.name as buyer_name " +
                    "FROM reviews r " +
                    "JOIN users u ON r.buyer_id = u.user_id " +
                    "WHERE r.product_id = ? " +
                    "ORDER BY r.review_date DESC";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Review review = mapResultSetToReview(rs);
                reviews.add(review);
            }
            
        } catch (Exception e) {
            System.out.println("Error getting product reviews: " + e.getMessage());
        }
        return reviews;
    }
    
    // Get all reviews for a seller's products
    public List<Review> getReviewsBySeller(int sellerId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, p.name as product_name, u.name as buyer_name " +
                    "FROM reviews r " +
                    "JOIN products p ON r.product_id = p.product_id " +
                    "JOIN users u ON r.buyer_id = u.user_id " +
                    "WHERE p.seller_id = ? " +
                    "ORDER BY r.review_date DESC";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Review review = mapResultSetToReview(rs);
                review.setCommentText(rs.getString("product_name") + " - " + 
                                   rs.getString("buyer_name") + ": " + 
                                   review.getCommentText());
                reviews.add(review);
            }
            
        } catch (Exception e) {
            System.out.println("Error getting seller reviews: " + e.getMessage());
        }
        return reviews;
    }
    
    // Get average rating for a product
    public double getAverageRating(int productId) {
        String sql = "SELECT AVG(rating) as avg_rating FROM reviews WHERE product_id = ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
            
        } catch (Exception e) {
            System.out.println("Error getting average rating: " + e.getMessage());
        }
        return 0.0;
    }
    
    // Get review count for a product
    public int getReviewCount(int productId) {
        String sql = "SELECT COUNT(*) as review_count FROM reviews WHERE product_id = ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("review_count");
            }
            
        } catch (Exception e) {
            System.out.println("Error getting review count: " + e.getMessage());
        }
        return 0;
    }
    
    // Helper method to map ResultSet to Review object
    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("review_id"));
        review.setProductId(rs.getInt("product_id"));
        review.setBuyerId(rs.getInt("buyer_id"));
        review.setRating(rs.getInt("rating"));
        review.setCommentText(rs.getString("comment_text"));
        review.setHelpfulVotes(rs.getInt("helpful_votes"));
        review.setNotHelpfulVotes(rs.getInt("not_helpful_votes"));
        review.setIsVerifiedPurchase(rs.getString("is_verified_purchase"));
        review.setReviewDate(rs.getTimestamp("review_date").toLocalDateTime());
        return review;
    }
    
    // Check if buyer has already reviewed a product
    public boolean hasUserReviewedProduct(int productId, int buyerId) {
        String sql = "SELECT review_id FROM reviews WHERE product_id = ? AND buyer_id = ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            ps.setInt(2, buyerId);
            ResultSet rs = ps.executeQuery();
            
            return rs.next();
            
        } catch (Exception e) {
            System.out.println("Error checking existing review: " + e.getMessage());
            return false;
        }
    }
    
    // Delete a review (for buyer or admin)
    public boolean deleteReview(int reviewId, int userId, String role) {
        String sql;
        
        if ("BUYER".equalsIgnoreCase(role)) {
            sql = "DELETE FROM reviews WHERE review_id = ? AND buyer_id = ?";
        } else if ("SELLER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            sql = "DELETE FROM reviews r WHERE r.review_id = ? AND " +
                  "EXISTS (SELECT 1 FROM products p WHERE p.product_id = r.product_id AND p.seller_id = ?)";
        } else {
            return false;
        }
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, reviewId);
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error deleting review: " + e.getMessage());
            return false;
        }
    }
}