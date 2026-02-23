package com.revshop.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.revshop.dao.CartDAO;
import com.revshop.dao.OrderDAO;
import com.revshop.dao.PaymentDAO;
import com.revshop.util.DBUtil;

public class OrderService {
    private OrderDAO orderDAO = new OrderDAO();
    private CartDAO cartDAO = new CartDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();
    private NotificationService notificationService = new NotificationService();

    public void placeOrder(int buyerId, String deliveryAddress, String paymentMethod) {
        Connection con = null;
        try {
            con = DBUtil.getConnection();
            con.setAutoCommit(false);

            double cartTotal = calculateCartTotal(buyerId);
            if (cartTotal == 0) {
                System.out.println("❌ Your cart is empty!");
                return;
            }

            String normalizedPaymentMethod = normalizePaymentMethod(paymentMethod);
            if (normalizedPaymentMethod == null) {
                System.out.println("❌ Invalid payment method.");
                return;
            }

            int orderId = orderDAO.createOrder(buyerId, cartTotal, deliveryAddress);
            if (orderId == -1) {
                con.rollback();
                System.out.println("❌ Failed to create order.");
                return;
            }

            if (!addOrderItemsFromCart(orderId, buyerId)) {
                con.rollback();
                System.out.println("❌ Failed to add order items.");
                return;
            }

            if (!updateStockFromCart(buyerId)) {
                con.rollback();
                System.out.println("❌ Failed to update stock.");
                return;
            }

            if (!paymentDAO.recordPayment(orderId, normalizedPaymentMethod, cartTotal)) {
                con.rollback();
                System.out.println("❌ Payment failed.");
                return;
            }

            cartDAO.clearCart(buyerId);
            con.commit();

            notificationService.sendOrderNotification(buyerId, orderId, cartTotal);

            System.out.println("✅ ORDER PLACED SUCCESSFULLY!");
            System.out.println("Order ID: " + orderId);
            System.out.println("Total Amount: ₹" + cartTotal);
            System.out.println("Delivery to: " + deliveryAddress);
            System.out.println("Payment Method: " + normalizedPaymentMethod);

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            System.out.println("❌ Order failed: " + e.getMessage());
        } finally {
            try { if (con != null) { con.setAutoCommit(true); con.close(); } } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void trackOrder(int orderId, int userId, String role) {
        String sql;
        if ("BUYER".equalsIgnoreCase(role)) {
            sql = "SELECT o.*, u.name as buyer_name FROM orders o " +
                  "JOIN users u ON o.buyer_id = u.user_id " +
                  "WHERE o.order_id = ? AND o.buyer_id = ?";
        } else if ("SELLER".equalsIgnoreCase(role)) {
            sql = "SELECT DISTINCT o.*, u.name as buyer_name FROM orders o " +
                  "JOIN order_items oi ON o.order_id = oi.order_id " +
                  "JOIN products p ON oi.product_id = p.product_id " +
                  "JOIN users u ON o.buyer_id = u.user_id " +
                  "WHERE o.order_id = ? AND p.seller_id = ?";
        } else {
            System.out.println("❌ Invalid role for order tracking.");
            return;
        }

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n📦 ORDER TRACKING - Order #" + orderId);
                System.out.println("=".repeat(60));
                System.out.println("Order ID: " + rs.getInt("order_id"));
                System.out.println("Buyer: " + rs.getString("buyer_name"));
                System.out.println("Order Date: " + rs.getTimestamp("order_date"));
                System.out.println("Total Amount: ₹" + rs.getDouble("total_amount"));
                System.out.println("Status: " + getStatusIcon(rs.getString("status")));
                System.out.println("Delivery Address: " + rs.getString("delivery_address"));
                System.out.println("=".repeat(60));
            } else {
                System.out.println("❌ Order not found or you don't have permission.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error tracking order: " + e.getMessage());
        }
    }

    public void updateOrderStatus(int orderId, int sellerId, String newStatus) {
        String sql = "UPDATE orders o SET o.status = ? " +
                     "WHERE o.order_id = ? AND EXISTS (" +
                     "  SELECT 1 FROM order_items oi " +
                     "  JOIN products p ON oi.product_id = p.product_id " +
                     "  WHERE oi.order_id = o.order_id AND p.seller_id = ?" +
                     ")";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            ps.setInt(3, sellerId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Order #" + orderId + " status updated to: " + newStatus);
                String buyerSql = "SELECT buyer_id FROM orders WHERE order_id = ?";
                try (PreparedStatement buyerStmt = con.prepareStatement(buyerSql)) {
                    buyerStmt.setInt(1, orderId);
                    ResultSet rs = buyerStmt.executeQuery();
                    if (rs.next()) {
                        int buyerId = rs.getInt("buyer_id");
                        notificationService.addNotification(buyerId, "Order Status Updated",
                                "Order #" + orderId + " is now: " + newStatus);
                    }
                }
            } else {
                System.out.println("❌ Failed to update order status. You may not have permission.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error updating order status: " + e.getMessage());
        }
    }

    public void cancelOrder(int orderId, int userId, String role) {
        String sql;
        if ("BUYER".equalsIgnoreCase(role)) {
            sql = "UPDATE orders SET status = 'CANCELLED' " +
                  "WHERE order_id = ? AND buyer_id = ? " +
                  "AND status IN ('PLACED', 'PROCESSING')";
        } else if ("SELLER".equalsIgnoreCase(role)) {
            sql = "UPDATE orders o SET o.status = 'CANCELLED' " +
                  "WHERE o.order_id = ? AND EXISTS (" +
                  "  SELECT 1 FROM order_items oi " +
                  "  JOIN products p ON oi.product_id = p.product_id " +
                  "  WHERE oi.order_id = o.order_id AND p.seller_id = ?" +
                  ") AND o.status IN ('PLACED', 'PROCESSING')";
        } else {
            System.out.println("❌ Invalid role for order cancellation.");
            return;
        }

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Order #" + orderId + " cancelled successfully!");
                NotificationService ns = new NotificationService();
                if ("BUYER".equalsIgnoreCase(role)) {
                    ns.addNotification(userId, "Order Cancelled", "Order #" + orderId + " has been cancelled.");
                } else {
                    String findBuyerSql = "SELECT buyer_id FROM orders WHERE order_id = ?";
                    try (PreparedStatement buyerStmt = con.prepareStatement(findBuyerSql)) {
                        buyerStmt.setInt(1, orderId);
                        ResultSet rs = buyerStmt.executeQuery();
                        if (rs.next()) {
                            int buyerId = rs.getInt("buyer_id");
                            ns.addNotification(buyerId, "Order Cancelled",
                                    "Order #" + orderId + " has been cancelled by seller.");
                        }
                    }
                }
            } else {
                System.out.println("❌ Cannot cancel order. Order might be shipped, delivered, or already cancelled.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error cancelling order: " + e.getMessage());
        }
    }

    public void cancelOrder(int orderId, int buyerId) {
        cancelOrder(orderId, buyerId, "BUYER");
    }

    public void viewOrderHistory(int buyerId) {
        try {
            ResultSet rs = orderDAO.getOrderHistory(buyerId);
            System.out.println("\n📦 ORDER HISTORY");
            System.out.println("==========================================");
            boolean found = false;
            while (rs != null && rs.next()) {
                found = true;
                System.out.println("Order ID: " + rs.getInt("order_id"));
                System.out.println("Date: " + rs.getTimestamp("order_date"));
                System.out.println("Amount: ₹" + rs.getDouble("total_amount"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Address: " + rs.getString("delivery_address"));
                System.out.println("------------------------------------------");
            }
            if (!found) System.out.println("No orders found.");
            rs.close();
        } catch (Exception e) {
            System.out.println("❌ Error viewing order history: " + e.getMessage());
        }
    }

    public void viewSellerOrders(int sellerId) {
        try {
            ResultSet rs = orderDAO.getSellerOrders(sellerId);
            System.out.println("\n📊 SELLER ORDERS");
            System.out.println("==========================================");
            boolean found = false;
            while (rs != null && rs.next()) {
                found = true;
                System.out.println("Order ID: " + rs.getInt("order_id"));
                System.out.println("Date: " + rs.getTimestamp("order_date"));
                System.out.println("Product: " + rs.getString("name"));
                System.out.println("Quantity: " + rs.getInt("quantity"));
                System.out.println("Amount: ₹" + rs.getDouble("total_amount"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("------------------------------------------");
            }
            if (!found) System.out.println("No orders received yet.");
            rs.close();
        } catch (Exception e) {
            System.out.println("❌ Error viewing seller orders: " + e.getMessage());
        }
    }

    private String getStatusIcon(String status) {
        switch (status) {
            case "PLACED": return "📝 PLACED";
            case "PROCESSING": return "⚙️ PROCESSING";
            case "SHIPPED": return "🚚 SHIPPED";
            case "DELIVERED": return "✅ DELIVERED";
            case "CANCELLED": return "❌ CANCELLED";
            case "RETURNED": return "↩️ RETURNED";
            default: return status;
        }
    }

    private String normalizePaymentMethod(String method) {
        if (method == null) return null;
        method = method.trim().toUpperCase();
        if (method.contains("COD") || method.contains("CASH")) return "COD";
        if (method.contains("UPI")) return "UPI";
        if (method.contains("CREDIT")) return "CREDIT_CARD";
        if (method.contains("DEBIT")) return "DEBIT_CARD";
        if (method.contains("NET") || method.contains("BANK")) return "NET_BANKING";
        if (method.contains("WALLET")) return "WALLET";
        return null;
    }

    private double calculateCartTotal(int buyerId) {
        double total = 0;
        try {
            ResultSet rs = cartDAO.getCartItems(buyerId);
            if (rs != null) {
                while (rs.next()) total += rs.getDouble("total");
                rs.close();
            }
        } catch (Exception e) {
            System.out.println("❌ Error calculating total: " + e.getMessage());
        }
        return total;
    }

    private boolean addOrderItemsFromCart(int orderId, int buyerId) {
        try {
            ResultSet rs = cartDAO.getCartItems(buyerId);
            if (rs == null) return false;
            boolean success = true;
            while (rs.next()) {
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                if (!orderDAO.addOrderItem(orderId, productId, quantity, price)) {
                    success = false;
                    break;
                }
            }
            rs.close();
            return success;
        } catch (Exception e) {
            System.out.println("❌ Error adding order items: " + e.getMessage());
            return false;
        }
    }

    private boolean updateStockFromCart(int buyerId) {
        try {
            ResultSet rs = cartDAO.getCartItems(buyerId);
            if (rs == null) return false;
            boolean success = true;
            while (rs.next()) {
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");
                if (!orderDAO.updateProductStock(productId, quantity)) {
                    success = false;
                    break;
                }
            }
            rs.close();
            return success;
        } catch (Exception e) {
            System.out.println("❌ Error updating stock: " + e.getMessage());
            return false;
        }
    }
}