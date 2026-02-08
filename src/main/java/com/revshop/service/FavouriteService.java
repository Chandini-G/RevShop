package com.revshop.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.revshop.util.DBUtil;

public class FavouriteService {

    // -------- ADD TO FAVOURITES --------
    public void addToFavourites(int buyerId, int productId) {

        String sql = """
            INSERT INTO favourites (buyer_id, product_id)
            VALUES (?, ?)
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ps.setInt(2, productId);
            ps.executeUpdate();

            System.out.println("Added to favourites");

        } catch (Exception e) {
            System.out.println("Already in favourites");
        }
    }

    // -------- VIEW FAVOURITES --------
    public void viewFavourites(int buyerId) {

        String sql = """
            SELECT p.product_id, p.name, p.price
            FROM favourites f
            JOIN products p ON f.product_id = p.product_id
            WHERE f.buyer_id = ?
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- YOUR FAVOURITES ---");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                    rs.getInt("product_id") + " | " +
                    rs.getString("name") + " | ₹" +
                    rs.getDouble("price")
                );
            }

            if (!found)
                System.out.println("No favourites added");

        } catch (Exception e) {
            System.out.println("Unable to fetch favourites");
        }
    }

    // -------- REMOVE FROM FAVOURITES --------
    public void removeFromFavourites(int buyerId, int productId) {

        String sql = """
            DELETE FROM favourites
            WHERE buyer_id = ? AND product_id = ?
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ps.setInt(2, productId);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Removed from favourites");
            else
                System.out.println("Product not in favourites");

        } catch (Exception e) {
            System.out.println("Remove failed");
        }
    }
}
