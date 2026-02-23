package com.revshop.service;

public class PaymentService {
    
    public void makePayment(double amount, String paymentMethod) {
        System.out.println("\n💳 PROCESSING PAYMENT");
        System.out.println("=====================");
        System.out.println("Amount: ₹" + amount);
        System.out.println("Method: " + paymentMethod);
        System.out.println("Processing...");
        
        // Simulate payment processing
        try {
            Thread.sleep(2000); // 2 second delay
            System.out.println("✅ Payment successful!");
        } catch (InterruptedException e) {
            System.out.println("❌ Payment processing interrupted.");
        }
    }
}