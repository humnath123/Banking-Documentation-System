package com.banking.model;

/**
 * Base User class demonstrating Inheritance.
 * Admin, Staff, and Customer inherit from this.
 */
public class User {
    private int userId;
    private String username;
    private String password;
    private String role;

    public User() {}

    public User(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters & Setters (Encapsulation)
    public int getUserId()         { return userId; }
    public void setUserId(int id)  { this.userId = id; }

    public String getUsername()            { return username; }
    public void setUsername(String u)      { this.username = u; }

    public String getPassword()            { return password; }
    public void setPassword(String p)      { this.password = p; }

    public String getRole()                { return role; }
    public void setRole(String r)          { this.role = r; }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}
