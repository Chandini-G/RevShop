package com.revshop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.revshop.model.Product;
import com.revshop.util.DBUtil;

public class ProductDAO {

    // ================= SELLER : ADD PRODUCT =================
    public boolean addProduct(Product p) {

        String sql = """
            INSERT INTO products
            (seller_id, name, category, price, mrp, stock, threshold)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getSellerId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getCategory());
            ps.setDouble(4, p.getPrice());
            ps.setDouble(5, p.getMrp());
            ps.setInt(6, p.getStock());
            ps.setInt(7, p.getThreshold());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= BUYER : VIEW ALL PRODUCTS =================
    public void viewAllProducts() {

        String sql = """
            SELECT product_id, name, category, price, stock
            FROM products
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\nID | NAME | CATEGORY | PRICE | STOCK");
            System.out.println("---------------------------------------");

            while (rs.next()) {
                System.out.println(
                    rs.getInt("product_id") + " | " +
                    rs.getString("name") + " | " +
                    rs.getString("category") + " | ₹" +
                    rs.getDouble("price") + " | " +
                    rs.getInt("stock")
                );
            }

        } catch (Exception e) {
            System.out.println("Unable to fetch products");
        }
    }

    // ================= BUYER : VIEW BY CATEGORY =================
    public void viewProductsByCategory(String category) {

        String sql = """
            SELECT product_id, name, price, stock
            FROM products
            WHERE LOWER(category) = LOWER(?)
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nID | NAME | PRICE | STOCK");
            System.out.println("--------------------------------");

            while (rs.next()) {
                System.out.println(
                    rs.getInt("product_id") + " | " +
                    rs.getString("name") + " | ₹" +
                    rs.getDouble("price") + " | " +
                    rs.getInt("stock")
                );
            }

        } catch (Exception e) {
            System.out.println("Unable to fetch category products");
        }
    }

    // ================= SELLER : VIEW INVENTORY (HORIZONTAL) =================
    public void viewInventory(int sellerId) {

        String sql = """
            SELECT product_id, name, category, mrp, price, stock, threshold
            FROM products
            WHERE seller_id = ?
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nID | NAME | CATEGORY | MRP | PRICE | STOCK | THRESHOLD");
            System.out.println("----------------------------------------------------------------");

            while (rs.next()) {
                System.out.println(
                    rs.getInt("product_id") + " | " +
                    rs.getString("name") + " | " +
                    rs.getString("category") + " | ₹" +
                    rs.getDouble("mrp") + " | ₹" +
                    rs.getDouble("price") + " | " +
                    rs.getInt("stock") + " | " +
                    rs.getInt("threshold")
                );
            }

        } catch (Exception e) {
            System.out.println("Unable to view inventory");
        }
    }

    // ================= SELLER : DELETE PRODUCT (FIXED) =================
    public boolean deleteProduct(int productId, int sellerId) {

        String sql = "DELETE FROM products WHERE product_id=? AND seller_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setInt(2, sellerId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
} 