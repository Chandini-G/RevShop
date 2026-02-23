package com.revshop.service;

import org.junit.Before;
import org.junit.Test;

public class ReviewServiceTest {

    private ReviewService reviewService;
    private int testProductId = 21; // Replace with an existing product ID
    private int testBuyerId = 1;    // Replace with an existing buyer ID
    private int testSellerId = 1;   // Replace with an existing seller ID

    @Before
    public void setup() {
        reviewService = new ReviewService();
    }

    @Test
    public void testAddReview() {
        boolean result = reviewService.addReview(testProductId, testBuyerId, 5, "Excellent product!");
        System.out.println("Add review result: " + (result ? "Success" : "Failed"));
    }

    @Test
    public void testViewProductReviews() {
        reviewService.viewProductReviews(testProductId);
        // Should print reviews in console
    }

    @Test
    public void testViewSellerReviews() {
        reviewService.viewSellerReviews(testSellerId);
        // Should print reviews for seller's products
    }
    
    @Test
    public void testGetRatingSummary() {
        String summary = reviewService.getRatingSummary(testProductId);
        System.out.println("Rating Summary: " + summary);
    }
    
    @Test
    public void testDeleteReview() {
        // Note: This will only work if you know an existing review ID
        // int existingReviewId = 1; // Replace with actual review ID
        // boolean result = reviewService.deleteReview(existingReviewId, testBuyerId, "BUYER");
        // System.out.println("Delete review result: " + (result ? "Success" : "Failed"));
        
        System.out.println("Delete review test skipped - requires existing review ID");
    }
}