package com.revshop.service;

import org.junit.Before;
import org.junit.Test;
import java.util.Scanner;

public class ProductServiceTest {

    private ProductService productService;
    private Scanner sc = new Scanner(System.in);
    private int testSellerId = 1; // Replace with a valid seller_id from your DB

    @Before
    public void setup() {
        productService = new ProductService();
    }

    // Test adding a product
    @Test
    public void testAddProduct() {
        // Manually input product details in console
        productService.addProduct(sc, testSellerId);
    }

    // Test viewing seller's products
    @Test
    public void testViewSellerProducts() {
        productService.viewSellerProducts(testSellerId);
    }

    // Test updating a product
    @Test
    public void testUpdateProduct() {
        // Enter product ID and new details in console
        productService.updateProduct(sc, testSellerId);
    }

    // Test deleting a product
    @Test
    public void testDeleteProduct() {
        // Enter product ID to delete in console
        productService.deleteProduct(sc, testSellerId);
    }
}