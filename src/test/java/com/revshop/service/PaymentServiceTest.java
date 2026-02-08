package com.revshop.service;

import org.junit.Before;
import org.junit.Test;

public class PaymentServiceTest {

    private PaymentService paymentService;

    @Before
    public void setup() {
        // Initialize before each test
        paymentService = new PaymentService();
    }

    @Test
    public void testMakePayment() {
        // Call the method inside the test
        paymentService.makePayment();
        // Output: "Payment successful (simulated)"
    }
}