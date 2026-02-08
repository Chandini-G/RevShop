package com.revshop.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.revshop.dao.ReviewDAO;
import com.revshop.util.DBUtil;

public class ReviewService {

    ReviewDAO dao = new ReviewDAO();

    // -------- BUYER ADD REVIEW --------
    public void addReview(int productId, int buyerId, int rating, String comment) {
        dao.addReview(productId, buyerId, rating, comment);
    }

    // -------- BUYER VIEW REVIEWS --------
    public void viewReviews(int productId) {

        String sql = "SELECT rating, review_comment FROM reviews WHERE product_id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            System.out.println("\n--- REVIEWS ---");

            while (rs.next()) {
                found = true;
                System.out.println(
                    "Rating: " + rs.getInt("rating") +
                    " | Comment: " + rs.getString("review_comment")
                );
            }

            if (!found)
                System.out.println("No reviews available");

        } catch (Exception e) {
            System.out.println("Unable to fetch reviews");
        }
    }

    // -------- SELLER VIEW REVIEWS --------
    public void viewSellerReviews(int sellerId) {

        String sql = """
            SELECT r.product_id, r.rating, r.review_comment
            FROM reviews r
            JOIN products p ON r.product_id = p.product_id
            WHERE p.seller_id = ?
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            System.out.println("\n--- PRODUCT REVIEWS ---");

            while (rs.next()) {
                found = true;
                System.out.println(
                    "Product ID: " + rs.getInt("product_id") +
                    " | Rating: " + rs.getInt("rating") +
                    " | Comment: " + rs.getString("review_comment")
                );
            }

            if (!found)
                System.out.println("No reviews yet");

        } catch (Exception e) {
            System.out.println("Unable to fetch seller reviews");
        }
    }
}
