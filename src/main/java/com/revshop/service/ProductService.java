package com.revshop.service;

import java.util.Scanner;

import com.revshop.dao.ProductDAO;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

    // SELLER: Add product
    public void addProduct(Scanner sc, int sellerId) {

        System.out.print("Product Name: ");
        String name = sc.nextLine();

        System.out.print("Category: ");
        String category = sc.nextLine();

        System.out.print("Price: ");
        double price = sc.nextDouble();

        System.out.print("MRP: ");
        double mrp = sc.nextDouble();

        System.out.print("Stock: ");
        int stock = sc.nextInt();

        System.out.print("Low stock threshold: ");
        int threshold = sc.nextInt();
        sc.nextLine();

        var p = new com.revshop.model.Product();
        p.setSellerId(sellerId);
        p.setName(name);
        p.setCategory(category);
        p.setPrice(price);
        p.setMrp(mrp);
        p.setStock(stock);
        p.setThreshold(threshold);

        boolean added = productDAO.addProduct(p);

        if (added)
            System.out.println("Product added successfully");
        else
            System.out.println("Failed to add product");
    }

    // BUYER: View all products
    public void viewProducts() {
        productDAO.viewAllProducts();
    }

    // BUYER: View products by category
    public void viewProductsByCategory(Scanner sc) {

        System.out.print("Enter category to search: ");
        String category = sc.nextLine();

        productDAO.viewProductsByCategory(category);
    }

    // SELLER: View own products (HORIZONTAL – FIXED IN DAO)
    public void viewSellerProducts(int sellerId) {
        productDAO.viewInventory(sellerId);
    }

    // SELLER: Update product
    public void updateProduct(Scanner sc, int sellerId) {

        System.out.print("Product ID: ");
        int pid = sc.nextInt();

        System.out.print("New Price: ");
        double price = sc.nextDouble();

        System.out.print("New Stock: ");
        int stock = sc.nextInt();
        sc.nextLine();

        String sql = """
            UPDATE products
            SET price = ?, stock = ?
            WHERE product_id = ? AND seller_id = ?
        """;

        try (var con = com.revshop.util.DBUtil.getConnection();
             var ps = con.prepareStatement(sql)) {

            ps.setDouble(1, price);
            ps.setInt(2, stock);
            ps.setInt(3, pid);
            ps.setInt(4, sellerId);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Product updated successfully");
            else
                System.out.println("Product not found");

        } catch (Exception e) {
            System.out.println("Update failed");
        }
    }

    // SELLER: Delete product (FIXED)
    public void deleteProduct(Scanner sc, int sellerId) {

        System.out.print("Product ID to delete: ");
        int pid = sc.nextInt();
        sc.nextLine();

        boolean deleted = productDAO.deleteProduct(pid, sellerId);

        if (deleted)
            System.out.println("Product deleted successfully");
        else
            System.out.println("Product not found or not owned by you");
    }
}