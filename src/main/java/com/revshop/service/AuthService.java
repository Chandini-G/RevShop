package com.revshop.service;

import com.revshop.dao.AuthDAO;
import com.revshop.model.User;

public class AuthService {
    private AuthDAO authDAO = new AuthDAO();

    public boolean register(User user) {
        return authDAO.register(user);
    }

    public User login(String email, String password) {
        return authDAO.login(email, password);
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) {
        return authDAO.changePassword(email, oldPassword, newPassword);
    }

    public String getSecurityQuestion(String email) {
        return authDAO.getSecurityQuestion(email);
    }

    public String getPasswordHint(String email) {
        return authDAO.getPasswordHint(email);
    }

    public boolean forgotPassword(String email, String securityAnswer, String newPassword) {
        return authDAO.forgotPassword(email, securityAnswer, newPassword);
    }
}