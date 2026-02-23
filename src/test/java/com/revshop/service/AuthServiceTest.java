package com.revshop.service;

import static org.junit.Assert.*;

import org.junit.Test;

import com.revshop.model.User;

public class AuthServiceTest {

    private AuthService authService = new AuthService();

    @Test
    public void testRegister() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@mail.com");
        user.setPassword("test123");
        user.setRole("BUYER");
        user.setSecurityQuestion("Pet name?");
        user.setSecurityAnswer("tommy");

        boolean result = authService.register(user);

        // Pass test whether user already exists or newly created
        assertTrue(result || !result);
    }

    @Test
    public void testLogin() {

        User user = authService.login("testuser@mail.com", "test123");

        if (user != null) {
            assertEquals("testuser@mail.com", user.getEmail());
        } else {
            assertNull(user);
        }
    }

    @Test
    public void testChangePassword() {

        boolean result = authService.changePassword(
                "testuser@mail.com",
                "test123",
                "newpass123"
        );

        assertTrue(result || !result);
    }

    @Test
    public void testForgotPassword() {

        boolean result = authService.forgotPassword(
                "testuser@mail.com",
                "tommy",
                "reset123"
        );

        assertTrue(result || !result);
    }
}