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
        reviewService.addReview(testProductId, testBuyerId, 5, "Excellent product!");
        // Prints nothing, just checks method runs
    }

    @Test
    public void testViewReviews() {
        reviewService.viewReviews(testProductId);
        // Should print reviews in console
    }

    @Test
    public void testViewSellerReviews() {
        reviewService.viewSellerReviews(testSellerId);
        // Should print reviews for seller's products
    }
}