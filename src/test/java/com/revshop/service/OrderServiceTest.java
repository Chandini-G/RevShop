package com.revshop.service;

import org.junit.Before;
import org.junit.Test;

public class OrderServiceTest {

    private OrderService orderService;
    private int testBuyerId = 1;   // Replace with a valid buyer_id from your DB
    private int testSellerId = 2;  // Replace with a valid seller_id from your DB
    private int testOrderId = 1;   // Replace with a valid order_id from your DB

    @Before
    public void setup() {
        orderService = new OrderService();
    }

    // Test placing an order
    @Test
    public void testPlaceOrder() {
        String address = "123 Main Street";
        String paymentMethod = "COD";  // Cash on Delivery
        orderService.placeOrder(testBuyerId, address, paymentMethod);
        // Check console for "Order placed successfully!"
    }

    // Test viewing buyer's order history
    @Test
    public void testViewOrderHistory() {
        orderService.viewOrderHistory(testBuyerId);
        // Check console for the orders placed by buyer
    }

    // Test viewing seller's received orders
    @Test
    public void testViewSellerOrders() {
        orderService.viewSellerOrders(testSellerId);
        // Check console for orders containing seller's products
    }

    // Test cancelling an order
    @Test
    public void testCancelOrder() {
        orderService.cancelOrder(testOrderId);
        // Check console for "Order cancelled"
    }
}