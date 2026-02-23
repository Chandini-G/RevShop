package com.revshop.service;

import org.junit.Before;
import org.junit.Test;

public class OrderServiceTest {

    private OrderService orderService;
    private int testBuyerId = 1;
    private int testOrderId = 1;

    @Before
    public void setup() {
        orderService = new OrderService();
    }

    @Test
    public void testCancelOrder() {
        orderService.cancelOrder(testOrderId, testBuyerId); // Added buyerId parameter
    }
}