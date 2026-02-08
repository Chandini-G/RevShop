package com.revshop.service;

import com.revshop.dao.AuthDAO;
import com.revshop.model.User;

public class UserService {

    private final AuthDAO dao = new AuthDAO();

    public User login(String email, String password) {
        return dao.login(email, password);
    }
}