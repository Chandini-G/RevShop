package com.revshop.dao;

import java.sql.*;
import com.revshop.model.User;
import com.revshop.util.DBUtil;

public class AuthDAO {

    // REGISTER
    public boolean register(User user) {

        String sql = """
            INSERT INTO users
            (name, email, password, role, security_question, security_answer)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getSecurityQuestion());
            ps.setString(6, user.getSecurityAnswer());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            return false;
        }
    }

    // LOGIN
    public User login(String email, String password) {

        String sql = """
            SELECT user_id, name, email, role
            FROM users
            WHERE email = ? AND password = ?
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                return u;
            }
        } catch (Exception ignored) {}
        return null;
    }

    // FORGOT PASSWORD
    public boolean resetPassword(String email, String answer, String newPassword) {

        String sql = """
            UPDATE users
            SET password = ?
            WHERE email = ? AND security_answer = ?
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, email);
            ps.setString(3, answer);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            return false;
        }
    }
}