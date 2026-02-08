package com.revshop.service;

import com.revshop.dao.AuthDAO;
import com.revshop.model.User;

public class AuthService {

    private final AuthDAO dao = new AuthDAO();

    public boolean register(User user) {
        return dao.register(user);
    }

    public User login(String email, String password) {
        return dao.login(email, password);
    }

    public boolean forgotPassword(String email, String answer, String newPassword) {
        return dao.resetPassword(email, answer, newPassword);
    }
}