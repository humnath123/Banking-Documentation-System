package com.banking.model;

public class Transaction {
    private int transactionId;
    private String accountNumber;
    private String transactionType;
    private double amount;
    private String transactionDate;
    private int staffId;
    private String description;

    public Transaction() {}

    public Transaction(int transactionId, String accountNumber, String transactionType,
                       double amount, String transactionDate, int staffId, String description) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.staffId = staffId;
        this.description = description;
    }

    public int getTransactionId()              { return transactionId; }
    public void setTransactionId(int id)       { this.transactionId = id; }

    public String getAccountNumber()           { return accountNumber; }
    public void setAccountNumber(String a)     { this.accountNumber = a; }

    public String getTransactionType()         { return transactionType; }
    public void setTransactionType(String t)   { this.transactionType = t; }

    public double getAmount()                  { return amount; }
    public void setAmount(double a)            { this.amount = a; }

    public String getTransactionDate()         { return transactionDate; }
    public void setTransactionDate(String d)   { this.transactionDate = d; }

    public int getStaffId()                    { return staffId; }
    public void setStaffId(int id)             { this.staffId = id; }

    public String getDescription()             { return description; }
    public void setDescription(String d)       { this.description = d; }
}
