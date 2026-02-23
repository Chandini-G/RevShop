package com.revshop.service;

import java.util.List;
import com.revshop.dao.ReviewDAO;
import com.revshop.model.Review;

public class ReviewService {
    private ReviewDAO reviewDAO = new ReviewDAO();
    
    // Add a review with validation
    public boolean addReview(int productId, int buyerId, int rating, String commentText) {
        // Validation
        if (rating < 1 || rating > 5) {
            System.out.println("❌ Rating must be between 1 and 5.");
            return false;
        }
        
        if (commentText == null || commentText.trim().isEmpty()) {
            System.out.println("❌ Review comment cannot be empty.");
            return false;
        }
        
        // Check if user has already reviewed this product
        if (reviewDAO.hasUserReviewedProduct(productId, buyerId)) {
            System.out.println("❌ You have already reviewed this product.");
            return false;
        }
        
        // Create review object
        Review review = new Review();
        review.setProductId(productId);
        review.setBuyerId(buyerId);
        review.setRating(rating);
        review.setCommentText(commentText.trim());
        
        boolean success = reviewDAO.addReview(review);
        if (success) {
            System.out.println("✅ Review added successfully!");
            return true;
        } else {
            System.out.println("❌ Failed to add review. Please try again.");
            return false;
        }
    }
    
    // View reviews for a product
    public void viewProductReviews(int productId) {
        List<Review> reviews = reviewDAO.getReviewsByProduct(productId);
        
        System.out.println("\n📝 PRODUCT REVIEWS");
        System.out.println("=".repeat(50));
        
        if (reviews.isEmpty()) {
            System.out.println("No reviews yet for this product.");
            return;
        }
        
        double averageRating = reviewDAO.getAverageRating(productId);
        int reviewCount = reviewDAO.getReviewCount(productId);
        
        System.out.printf("⭐ Average Rating: %.1f/5 (%d reviews)\n\n", averageRating, reviewCount);
        
        for (Review review : reviews) {
            System.out.println("Rating: " + "★".repeat(review.getRating()) + 
                             "☆".repeat(5 - review.getRating()));
            System.out.println("Review: " + review.getCommentText());
            System.out.println("Date: " + review.getReviewDate());
            System.out.println("-".repeat(50));
        }
    }
    
    // View all reviews for seller's products
    public void viewSellerReviews(int sellerId) {
        List<Review> reviews = reviewDAO.getReviewsBySeller(sellerId);
        
        System.out.println("\n📝 REVIEWS FOR YOUR PRODUCTS");
        System.out.println("=".repeat(50));
        
        if (reviews.isEmpty()) {
            System.out.println("No reviews for your products yet.");
            return;
        }
        
        for (Review review : reviews) {
            System.out.println("Product ID: " + review.getProductId());
            System.out.println("Rating: " + "★".repeat(review.getRating()));
            System.out.println("Review: " + review.getCommentText());
            System.out.println("Date: " + review.getReviewDate());
            System.out.println("-".repeat(50));
        }
        
        System.out.println("\n📊 Total Reviews: " + reviews.size());
    }
    
    // Get product rating summary (for display in product listings)
    public String getRatingSummary(int productId) {
        double avgRating = reviewDAO.getAverageRating(productId);
        int count = reviewDAO.getReviewCount(productId);
        
        if (count == 0) {
            return "No ratings yet";
        }
        
        return String.format("⭐ %.1f/5 (%d reviews)", avgRating, count);
    }
    
    // Delete a review
    public boolean deleteReview(int reviewId, int userId, String role) {
        boolean success = reviewDAO.deleteReview(reviewId, userId, role);
        
        if (success) {
            System.out.println("✅ Review deleted successfully!");
        } else {
            System.out.println("❌ Failed to delete review. You may not have permission.");
        }
        
        return success;
    }
}