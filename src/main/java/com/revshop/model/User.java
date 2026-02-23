package com.revshop.model;

public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String role;
    private String securityQuestion;
    private String securityAnswer;
    private String passwordHint;
    private String isActive;
    private String address;
    private String phone;

    public User() {}

    public User(int userId, String name, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }

    public String getSecurityAnswer() { return securityAnswer; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }

    public String getPasswordHint() { return passwordHint; }
    public void setPasswordHint(String passwordHint) { this.passwordHint = passwordHint; }

    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return String.format("User[ID: %d, Name: %s, Email: %s, Role: %s]",
                userId, name, email, role);
    }
}