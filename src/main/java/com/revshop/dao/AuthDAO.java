package com.revshop.dao;

import java.sql.*;
import com.revshop.model.User;
import com.revshop.util.DBUtil;

public class AuthDAO {

    public boolean register(User user) {
        String sql = "INSERT INTO users (name, email, password, role, security_question, security_answer, password_hint) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getSecurityQuestion());
            ps.setString(6, user.getSecurityAnswer());
            ps.setString(7, user.getPasswordHint());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Registration error: " + e.getMessage());
            return false;
        }
    }

    public User login(String email, String password) {
        String sql = "SELECT user_id, name, email, role FROM users WHERE email = ? AND password = ?";

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

        } catch (SQLException e) {
            System.out.println("❌ Login error: " + e.getMessage());
        }
        return null;
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) {
        String checkSql = "SELECT user_id FROM users WHERE email = ? AND password = ?";
        String updateSql = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement check = con.prepareStatement(checkSql)) {

            check.setString(1, email);
            check.setString(2, oldPassword);
            ResultSet rs = check.executeQuery();

            if (!rs.next()) {
                return false;
            }

            try (PreparedStatement update = con.prepareStatement(updateSql)) {
                update.setString(1, newPassword);
                update.setString(2, email);
                return update.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("❌ Change password error: " + e.getMessage());
            return false;
        }
    }

    public String getSecurityQuestion(String email) {
        String sql = "SELECT security_question FROM users WHERE email = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("security_question");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error getting security question: " + e.getMessage());
        }
        return null;
    }

    public String getPasswordHint(String email) {
        String sql = "SELECT password_hint FROM users WHERE email = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("password_hint");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error getting password hint: " + e.getMessage());
        }
        return null;
    }

    public boolean forgotPassword(String email, String securityAnswer, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ? " +
                     "AND UPPER(TRIM(security_answer)) = UPPER(TRIM(?))";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, email);
            ps.setString(3, securityAnswer);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Forgot password error: " + e.getMessage());
            return false;
        }
    }
}