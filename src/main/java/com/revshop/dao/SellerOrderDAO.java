package com.revshop.dao;

import java.sql.*;
import com.revshop.util.DBUtil;

public class SellerOrderDAO {
    
    public ResultSet getOrdersBySeller(int sellerId) throws SQLException {
        String sql = "SELECT DISTINCT o.order_id, o.order_date, o.total_amount, o.status " +
                    "FROM orders o " +
                    "JOIN order_items oi ON o.order_id = oi.order_id " +
                    "JOIN products p ON oi.product_id = p.product_id " +
                    "WHERE p.seller_id = ? " +
                    "ORDER BY o.order_date DESC";
        
        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, sellerId);
        return ps.executeQuery();
    }
}