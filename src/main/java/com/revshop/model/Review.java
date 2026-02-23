package com.revshop.model;

import java.time.LocalDateTime;

public class Review {
    private int reviewId;
    private int productId;
    private int buyerId;
    private int orderId;
    private int rating;
    private String title;
    private String commentText;  // Changed from 'comment'
    private int helpfulVotes;
    private int notHelpfulVotes;
    private String isVerifiedPurchase;
    private LocalDateTime reviewDate;

    // Getters and Setters
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }
    
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public int getRating() { return rating; }
    public void setRating(int rating) { 
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating; 
    }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
    
    public int getHelpfulVotes() { return helpfulVotes; }
    public void setHelpfulVotes(int helpfulVotes) { this.helpfulVotes = helpfulVotes; }
    
    public int getNotHelpfulVotes() { return notHelpfulVotes; }
    public void setNotHelpfulVotes(int notHelpfulVotes) { this.notHelpfulVotes = notHelpfulVotes; }
    
    public String getIsVerifiedPurchase() { return isVerifiedPurchase; }
    public void setIsVerifiedPurchase(String isVerifiedPurchase) { this.isVerifiedPurchase = isVerifiedPurchase; }
    
    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }
    
    @Override
    public String toString() {
        return String.format("Review[ID: %d, Product: %d, Buyer: %d, Rating: %d/5]", 
            reviewId, productId, buyerId, rating);
    }
}