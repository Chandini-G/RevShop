package com.revshop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBUtil {

    private static final Logger logger =
            LogManager.getLogger(DBUtil.class);

    private static final String URL =
            "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USERNAME = "revshop";
    private static final String PASSWORD = "revshop";

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            logger.info("Oracle JDBC Driver loaded");
        } catch (ClassNotFoundException e) {
            logger.error("JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        logger.info("Creating database connection");
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}