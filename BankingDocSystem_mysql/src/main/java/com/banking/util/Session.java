package com.banking.util;

import com.banking.model.User;

/**
 * Singleton session manager — holds the currently logged-in user.
 */
public class Session {
    private static Session instance;
    private User currentUser;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) instance = new Session();
        return instance;
    }

    public User getCurrentUser()          { return currentUser; }
    public void setCurrentUser(User u)    { this.currentUser = u; }

    public boolean isAdmin()   { return currentUser != null && "Admin".equals(currentUser.getRole()); }
    public boolean isStaff()   { return currentUser != null && "Staff".equals(currentUser.getRole()); }
    public boolean isCustomer(){ return currentUser != null && "Customer".equals(currentUser.getRole()); }

    public void logout() { currentUser = null; }
}
