package com.banking.controller;

import com.banking.dao.CustomerDAO;
import com.banking.model.Customer;
import java.util.List;

public class CustomerController {
    private final CustomerDAO dao = new CustomerDAO();

    public List<Customer> getAll()             { return dao.getAllCustomers(); }
    public List<Customer> search(String kw)    { return kw == null || kw.isBlank() ? dao.getAllCustomers() : dao.searchCustomers(kw); }
    public boolean add(Customer c)             { return dao.insertCustomer(c); }
    public boolean update(Customer c)          { return dao.updateCustomer(c); }
    public boolean delete(int id)              { return dao.deleteCustomer(id); }
}
