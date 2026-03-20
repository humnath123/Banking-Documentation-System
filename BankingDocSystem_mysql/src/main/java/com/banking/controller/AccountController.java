package com.banking.controller;

import com.banking.dao.AccountDAO;
import com.banking.model.Account;
import java.util.List;

public class AccountController {
    private final AccountDAO dao = new AccountDAO();

    public List<Account> getAll()                             { return dao.getAllAccounts(); }
    public boolean add(Account a)                             { return dao.insertAccount(a); }
    public boolean updateStatus(String accNum, String status) { return dao.updateStatus(accNum, status); }
}
