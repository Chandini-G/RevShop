package com.revshop.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.revshop.model.Product;
import com.revshop.util.DBUtil;

public class ProductDAO {
    
    // Get all active products with category name
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE (p.is_active = 'Y' OR p.is_active IS NULL) " +
                    "AND (c.is_active = 'Y' OR c.is_active IS NULL OR c.is_active IS NOT NULL) " +
                    "ORDER BY p.product_id";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Product p = extractProductFromResultSet(rs);
                products.add(p);
            }
            
        } catch (Exception e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
        return products;
    }
    
    // Search active products
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE (p.is_active = 'Y' OR p.is_active IS NULL) " +
                    "AND (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ? OR LOWER(c.name) LIKE ?)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String searchTerm = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            ps.setString(3, searchTerm);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Product p = extractProductFromResultSet(rs);
                products.add(p);
            }
            
        } catch (Exception e) {
            System.out.println("Search error: " + e.getMessage());
        }
        return products;
    }
    
    // Get active products by seller
    public List<Product> getProductsBySeller(int sellerId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.seller_id = ? " +
                    "AND (p.is_active = 'Y' OR p.is_active IS NULL) " +
                    "ORDER BY p.product_id";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Product p = extractProductFromResultSet(rs);
                products.add(p);
            }
            
        } catch (Exception e) {
            System.out.println("Error fetching seller products: " + e.getMessage());
        }
        return products;
    }
    
    // Add product
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (seller_id, name, description, category_id, " +
                    "brand, price, mrp, stock, threshold, discount_percent) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, product.getSellerId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setInt(4, product.getCategoryId());
            ps.setString(5, product.getBrand());
            ps.setDouble(6, product.getPrice());
            ps.setDouble(7, product.getMrp());
            ps.setInt(8, product.getStock());
            ps.setInt(9, product.getThreshold());
            ps.setInt(10, product.getDiscountPercent());
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error adding product: " + e.getMessage());
            return false;
        }
    }
    
    // Update product
    public boolean updateProduct(int productId, int sellerId, double price, int stock, int threshold) {
        String sql = "UPDATE products SET price = ?, stock = ?, threshold = ? " +
                    "WHERE product_id = ? AND seller_id = ? " +
                    "AND (is_active = 'Y' OR is_active IS NULL)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setDouble(1, price);
            ps.setInt(2, stock);
            ps.setInt(3, threshold);
            ps.setInt(4, productId);
            ps.setInt(5, sellerId);
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error updating product: " + e.getMessage());
            return false;
        }
    }
    
    // SOFT DELETE product - set is_active = 'N'
    public boolean deleteProduct(int productId, int sellerId) {
        String sql = "UPDATE products SET is_active = 'N' " +
                    "WHERE product_id = ? AND seller_id = ? " +
                    "AND (is_active = 'Y' OR is_active IS NULL)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            ps.setInt(2, sellerId);
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }
    
    // Get low stock products
    public List<Product> getLowStockProducts(int sellerId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.seller_id = ? AND p.stock <= p.threshold " +
                    "AND (p.is_active = 'Y' OR p.is_active IS NULL)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Product p = extractProductFromResultSet(rs);
                products.add(p);
            }
            
        } catch (Exception e) {
            System.out.println("Error fetching low stock products: " + e.getMessage());
        }
        return products;
    }
    
    // Get products by category name - UPDATED with TRIM and UPPER
    public List<Product> getProductsByCategory(String categoryName) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                    "JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE UPPER(TRIM(c.name)) = UPPER(TRIM(?)) " +
                    "AND (p.is_active = 'Y' OR p.is_active IS NULL) " +
                    "AND (c.is_active = 'Y' OR c.is_active IS NULL)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, categoryName);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Product p = extractProductFromResultSet(rs);
                products.add(p);
            }
            
        } catch (Exception e) {
            System.out.println("Error fetching category products: " + e.getMessage());
        }
        return products;
    }
    
    // Get sales report
    public ResultSet getSalesReport(int sellerId) throws SQLException {
        String sql = "SELECT p.name, c.name as category_name, " +
                    "COALESCE(SUM(oi.quantity), 0) as total_sold, " +
                    "COALESCE(SUM(oi.total_price), 0) as revenue " +
                    "FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "LEFT JOIN order_items oi ON p.product_id = oi.product_id " +
                    "WHERE p.seller_id = ? AND (p.is_active = 'Y' OR p.is_active IS NULL) " +
                    "GROUP BY p.name, c.name";
        
        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, sellerId);
        return ps.executeQuery();
    }
    
    // Get all active categories for selection
    public List<String[]> getAllCategories() {
        List<String[]> categories = new ArrayList<>();
        String sql = "SELECT category_id, name FROM categories " +
                    "WHERE is_active = 'Y' OR is_active IS NULL " +
                    "ORDER BY display_order, name";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String[] category = new String[2];
                category[0] = String.valueOf(rs.getInt("category_id"));
                category[1] = rs.getString("name");
                categories.add(category);
            }
            
        } catch (Exception e) {
            System.out.println("Error fetching categories: " + e.getMessage());
            // Return default categories if table doesn't exist
            categories.add(new String[]{"1", "Electronics"});
            categories.add(new String[]{"2", "Clothing"});
            categories.add(new String[]{"3", "Footwear"});
            categories.add(new String[]{"4", "Home & Kitchen"});
            categories.add(new String[]{"5", "Books"});
            categories.add(new String[]{"6", "Beauty"});
        }
        return categories;
    }
    
    // Helper method to extract product from ResultSet
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setSellerId(rs.getInt("seller_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setCategoryId(rs.getInt("category_id"));
        
        // Get category name - handle case when column doesn't exist
        try {
            String categoryName = rs.getString("category_name");
            p.setCategoryName(categoryName != null ? categoryName : "Unknown");
        } catch (SQLException e) {
            p.setCategoryName("Unknown");
        }
        
        p.setBrand(rs.getString("brand"));
        p.setPrice(rs.getDouble("price"));
        p.setMrp(rs.getDouble("mrp"));
        p.setStock(rs.getInt("stock"));
        p.setThreshold(rs.getInt("threshold"));
        
        // Get discount percent
        try {
            p.setDiscountPercent(rs.getInt("discount_percent"));
        } catch (SQLException e) {
            p.setDiscountPercent(0);
        }
        
        // Get is_active
        try {
            String isActive = rs.getString("is_active");
            p.setIsActive(isActive != null ? isActive : "Y");
        } catch (SQLException e) {
            p.setIsActive("Y");
        }
        
        return p;
    }
}