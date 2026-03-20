package com.banking.model;

/**
 * Account model with Encapsulation — balance is private,
 * modified only through deposit/withdraw methods with validation.
 */
public class Account {
    private String accountNumber;
    private int customerId;
    private String accountType;
    private double balance;
    private String status;

    public Account() {}

    public Account(String accountNumber, int customerId, String accountType,
                   double balance, String status) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
    }

    /** Deposit funds — validates positive amount */
    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        this.balance += amount;
        return true;
    }

    /** Withdraw funds — validates balance sufficiency */
    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) return false;
        this.balance -= amount;
        return true;
    }

    public String getAccountNumber()           { return accountNumber; }
    public void setAccountNumber(String a)     { this.accountNumber = a; }

    public int getCustomerId()                 { return customerId; }
    public void setCustomerId(int id)          { this.customerId = id; }

    public String getAccountType()             { return accountType; }
    public void setAccountType(String t)       { this.accountType = t; }

    public double getBalance()                 { return balance; }
    public void setBalance(double b)           { this.balance = b; }

    public String getStatus()                  { return status; }
    public void setStatus(String s)            { this.status = s; }

    @Override
    public String toString() {
        return accountNumber + " (" + accountType + ") - Rs." + String.format("%.2f", balance);
    }
}
