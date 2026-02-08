package com.revshop.service;

import org.junit.Before;
import org.junit.Test;

public class CartServiceTest {

    private CartService cartService;
    private int testBuyerId = 1;   // Replace with a valid buyer_id from your DB
    private int testProductId = 1; // Replace with a valid product_id from your DB

    @Before
    public void setup() {
        cartService = new CartService();
    }

    @Test
    public void testAddToCart() {
        cartService.addToCart(testBuyerId, testProductId, 2);
    }

    @Test
    public void testViewCart() {
        cartService.viewCart(testBuyerId);
    }

    @Test
    public void testRemoveFromCart() {
        cartService.removeFromCart(testBuyerId, testProductId, 1);
    }
}