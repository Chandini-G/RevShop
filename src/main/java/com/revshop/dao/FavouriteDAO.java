package com.revshop.dao;

import java.sql.*;
import com.revshop.util.DBUtil;

public class FavouriteDAO {

    public void addFavourite(int buyerId, int productId) {

        String sql = "INSERT INTO favourites (buyer_id, product_id) VALUES (?,?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ps.setInt(2, productId);
            ps.executeUpdate();

            System.out.println("Added to favourites");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
