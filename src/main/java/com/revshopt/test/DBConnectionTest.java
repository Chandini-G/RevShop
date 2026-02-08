package com.revshopt.test;

import com.revshop.util.DBUtil;
import java.sql.Connection;

public class DBConnectionTest {

    public static void main(String[] args) {
        try {
            Connection con = DBUtil.getConnection();
            System.out.println("✅ Database Connected Successfully!");
            System.out.println(con);
        } catch (Exception e) {
            System.out.println("❌ Database Connection Failed");
            e.printStackTrace();
        }
    }
}
