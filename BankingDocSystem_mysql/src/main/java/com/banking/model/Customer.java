package com.banking.model;

public class Customer {
    private int customerId;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String dateOfBirth;
    private int userId;

    public Customer() {}

    public Customer(int customerId, String name, String address, String phone,
                    String email, String dateOfBirth, int userId) {
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.userId = userId;
    }

    public int getCustomerId()              { return customerId; }
    public void setCustomerId(int id)       { this.customerId = id; }

    public String getName()                 { return name; }
    public void setName(String n)           { this.name = n; }

    public String getAddress()              { return address; }
    public void setAddress(String a)        { this.address = a; }

    public String getPhone()                { return phone; }
    public void setPhone(String p)          { this.phone = p; }

    public String getEmail()                { return email; }
    public void setEmail(String e)          { this.email = e; }

    public String getDateOfBirth()          { return dateOfBirth; }
    public void setDateOfBirth(String d)    { this.dateOfBirth = d; }

    public int getUserId()                  { return userId; }
    public void setUserId(int id)           { this.userId = id; }

    @Override
    public String toString() { return name + " (ID: " + customerId + ")"; }
}
