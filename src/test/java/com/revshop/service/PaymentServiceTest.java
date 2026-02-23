package com.revshop.service;

import org.junit.Before;
import org.junit.Test;

public class PaymentServiceTest {

    private PaymentService paymentService;

    @Before
    public void setup() {
        paymentService = new PaymentService();
    }

    @Test
    public void testMakePayment() {
        paymentService.makePayment(1000.0, "COD"); // Added parameters
    }
}