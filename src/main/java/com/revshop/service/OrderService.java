package com.revshop.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.revshop.util.DBUtil;

public class OrderService {

    // -------- PLACE ORDER (BUYER – already working) --------
    public void placeOrder(int buyerId, String address, String paymentMethod) {

        String cartSql = """
            SELECT p.product_id, p.price, c.quantity
            FROM cart c
            JOIN products p ON c.product_id = p.product_id
            WHERE c.buyer_id = ?
        """;

        String orderSql =
            "INSERT INTO orders (buyer_id, total_amount, status) VALUES (?, ?, 'PLACED')";

        double total = 0;

        try (Connection con = DBUtil.getConnection()) {

            PreparedStatement cartPs = con.prepareStatement(cartSql);
            cartPs.setInt(1, buyerId);
            ResultSet rs = cartPs.executeQuery();

            while (rs.next()) {
                total += rs.getDouble("price") * rs.getInt("quantity");
            }

            if (total == 0) {
                System.out.println("Cart is empty");
                return;
            }

            PreparedStatement orderPs =
                con.prepareStatement(orderSql, new String[] { "order_id" });
            orderPs.setInt(1, buyerId);
            orderPs.setDouble(2, total);
            orderPs.executeUpdate();

            ResultSet orderRs = orderPs.getGeneratedKeys();
            orderRs.next();
            int orderId = orderRs.getInt(1);

            cartPs = con.prepareStatement(cartSql);
            cartPs.setInt(1, buyerId);
            rs = cartPs.executeQuery();

            while (rs.next()) {
                PreparedStatement itemPs = con.prepareStatement("""
                    INSERT INTO order_items
                    (order_id, product_id, quantity, price)
                    VALUES (?, ?, ?, ?)
                """);
                itemPs.setInt(1, orderId);
                itemPs.setInt(2, rs.getInt("product_id"));
                itemPs.setInt(3, rs.getInt("quantity"));
                itemPs.setDouble(4, rs.getDouble("price"));
                itemPs.executeUpdate();
            }

            PreparedStatement clear =
                con.prepareStatement("DELETE FROM cart WHERE buyer_id=?");
            clear.setInt(1, buyerId);
            clear.executeUpdate();

            System.out.println("Order placed successfully!");
            System.out.println("Payment Method: " + paymentMethod);
            System.out.println("Total Paid: ₹" + total);

        } catch (Exception e) {
            System.out.println("Order failed");
        }
    }

    // -------- VIEW BUYER ORDERS --------
    public void viewOrderHistory(int buyerId) {

        String sql = "SELECT * FROM orders WHERE buyer_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- ORDER HISTORY ---");
            while (rs.next()) {
                System.out.println(
                    "Order ID: " + rs.getInt("order_id") +
                    " | Amount: ₹" + rs.getDouble("total_amount") +
                    " | Status: " + rs.getString("status")
                );
            }

        } catch (Exception e) {
            System.out.println("Unable to fetch orders");
        }
    }

    // -------- VIEW SELLER ORDERS --------
    public void viewSellerOrders(int sellerId) {

        String sql = """
            SELECT DISTINCT o.order_id, o.total_amount, o.status
            FROM orders o
            JOIN order_items oi ON o.order_id = oi.order_id
            JOIN products p ON oi.product_id = p.product_id
            WHERE p.seller_id = ?
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- ORDERS RECEIVED ---");
            while (rs.next()) {
                System.out.println(
                    "Order ID: " + rs.getInt("order_id") +
                    " | Amount: ₹" + rs.getDouble("total_amount") +
                    " | Status: " + rs.getString("status")
                );
            }

        } catch (Exception e) {
            System.out.println("Unable to fetch seller orders");
        }
    }

    // -------- CANCEL ORDER --------
    public void cancelOrder(int orderId) {

        String sql = "UPDATE orders SET status='CANCELLED' WHERE order_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.executeUpdate();
            System.out.println("Order cancelled");

        } catch (Exception e) {
            System.out.println("Cancel failed");
        }
    }
}
