package com.banking.controller;

import com.banking.dao.AccountDAO;
import com.banking.dao.TransactionDAO;
import com.banking.model.Account;
import com.banking.model.Transaction;
import java.util.List;

public class TransactionController {
    private final TransactionDAO txnDAO = new TransactionDAO();
    private final AccountDAO accDAO     = new AccountDAO();

    public List<Transaction> getAll() { return txnDAO.getAllTransactions(); }

    /**
     * Process a deposit or withdrawal. Updates account balance.
     * Returns null on success, or an error message string on failure.
     */
    public String process(Transaction t) {
        Account account = accDAO.getAccountByNumber(t.getAccountNumber());
        if (account == null)           return "Account not found.";
        if (!"Active".equals(account.getStatus())) return "Account is not active.";

        boolean ok;
        switch (t.getTransactionType()) {
            case "Deposit"    -> ok = account.deposit(t.getAmount());
            case "Withdrawal" -> ok = account.withdraw(t.getAmount());
            default           -> { return "Unknown transaction type."; }
        }
        if (!ok) return "Transaction validation failed (insufficient funds or invalid amount).";

        accDAO.updateBalance(account.getAccountNumber(), account.getBalance());
        txnDAO.insertTransaction(t);
        return null;
    }

    /**
     * Transfer funds between two accounts atomically.
     */
    public String transfer(String fromAcc, String toAcc, double amount, int staffId) {
        Account from = accDAO.getAccountByNumber(fromAcc);
        Account to   = accDAO.getAccountByNumber(toAcc);

        if (from == null) return "Source account not found.";
        if (to   == null) return "Destination account not found.";
        if (!"Active".equals(from.getStatus())) return "Source account is not active.";
        if (!"Active".equals(to.getStatus()))   return "Destination account is not active.";
        if (!from.withdraw(amount)) return "Insufficient funds in source account.";

        to.deposit(amount);
        accDAO.updateBalance(from.getAccountNumber(), from.getBalance());
        accDAO.updateBalance(to.getAccountNumber(),   to.getBalance());

        Transaction withdrawal = new Transaction();
        withdrawal.setAccountNumber(fromAcc);
        withdrawal.setTransactionType("Withdrawal");
        withdrawal.setAmount(amount);
        withdrawal.setStaffId(staffId);
        withdrawal.setDescription("Transfer to " + toAcc);
        txnDAO.insertTransaction(withdrawal);

        Transaction deposit = new Transaction();
        deposit.setAccountNumber(toAcc);
        deposit.setTransactionType("Deposit");
        deposit.setAmount(amount);
        deposit.setStaffId(staffId);
        deposit.setDescription("Transfer from " + fromAcc);
        txnDAO.insertTransaction(deposit);

        return null;
    }
}
