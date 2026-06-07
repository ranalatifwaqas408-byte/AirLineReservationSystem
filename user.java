package model;

import java.util.regex.Pattern;

public abstract class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String role;

    public User() {}

    public User(String username, String password, String email, String fullName, String phoneNumber) {
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setFullName(fullName);
        setPhoneNumber(phoneNumber);
        this.role = "passenger";
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.trim().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
        this.username = username;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!Pattern.matches(emailRegex, email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        this.fullName = fullName;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.matches("\\d{11}")) {
            throw new IllegalArgumentException("Phone number must be 11 digits");
        }
        this.phoneNumber = phoneNumber;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public abstract String getDashboardTitle();
