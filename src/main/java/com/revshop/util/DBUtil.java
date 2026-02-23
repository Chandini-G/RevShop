package com.revshop.util;

import java.sql.*;

public class DBUtil {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USERNAME = "revshop";
    private static final String PASSWORD = "revshop";

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            System.out.println("✅ Oracle JDBC Driver loaded.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Oracle JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void closeResources(ResultSet rs, Statement stmt) {
        closeResources(rs, stmt, null);
    }
}