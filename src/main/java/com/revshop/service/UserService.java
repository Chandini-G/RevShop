package com.revshop.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import com.revshop.model.User;
import com.revshop.util.DBUtil;

public class UserService {

    // -------- REGISTER --------
    public void register(Scanner sc) {

        try (Connection con = DBUtil.getConnection()) {

            System.out.print("Name: ");
            String name = sc.nextLine();

            System.out.print("Email: ");
            String email = sc.nextLine();

            System.out.print("Password: ");
            String password = sc.nextLine();

            System.out.print("Role (BUYER/SELLER): ");
            String role = sc.nextLine().toUpperCase();

            System.out.print("Security Question: ");
            String secQ = sc.nextLine();

            System.out.print("Security Answer: ");
            String secA = sc.nextLine();

            String sql = """
                INSERT INTO users
                (name, email, password, role, security_question, security_answer)
                VALUES (?, ?, ?, ?, ?, ?)
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.setString(5, secQ);
            ps.setString(6, secA);

            ps.executeUpdate();
            System.out.println("Registration successful");

        } catch (Exception e) {
            System.out.println("Registration failed (email may already exist)");
        }
    }

    // -------- LOGIN --------
    public User login(String email, String password) {

        String sql = "SELECT * FROM users WHERE email=? AND password=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                );
            }

        } catch (Exception e) {
            System.out.println("Login error");
        }

        return null;
    }
}
