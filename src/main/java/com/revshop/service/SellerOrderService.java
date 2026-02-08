package com.revshop.service;

import com.revshop.dao.SellerOrderDAO;

public class SellerOrderService {

    private SellerOrderDAO dao = new SellerOrderDAO();

    public void viewSellerOrders(int sellerId) {
        dao.viewOrdersBySeller(sellerId);
    }
}
