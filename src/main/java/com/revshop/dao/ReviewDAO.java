package com.revshop.dao;

import java.sql.*;
import com.revshop.util.DBUtil;

public class ReviewDAO {

    public void addReview(int productId, int buyerId, int rating, String comment) {

        String sql = """
            INSERT INTO reviews
            (product_id, buyer_id, rating, review_comment)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setInt(2, buyerId);
            ps.setInt(3, rating);
            ps.setString(4, comment);
            ps.executeUpdate();

            System.out.println("Review added successfully");

        } catch (Exception e) {
            System.out.println("Invalid product or user");
        }
    }

    public void viewReviews(int productId) {

        String sql = """
            SELECT u.name, r.rating, r.review_comment, r.review_date
            FROM reviews r
            JOIN users u ON r.buyer_id = u.user_id
            WHERE r.product_id = ?
        """;

        boolean found = false;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- REVIEWS ---");
            while (rs.next()) {
                found = true;
                System.out.println(
                        rs.getString("name") +
                        " | Rating: " + rs.getInt("rating") +
                        " | " + rs.getString("review_comment")
                );
            }

            if (!found) {
                System.out.println("No reviews available");
            }

        } catch (Exception e) {
            System.out.println("Unable to fetch reviews");
        }
    }
}
