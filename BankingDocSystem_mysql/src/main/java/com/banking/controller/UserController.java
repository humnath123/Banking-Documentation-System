package com.banking.controller;

import com.banking.dao.UserDAO;
import com.banking.model.User;
import java.util.List;

public class UserController {
    private final UserDAO dao = new UserDAO();

    public List<User> getAll()  { return dao.getAllUsers(); }
    public boolean add(User u)  { return dao.insertUser(u); }
    public boolean update(User u){ return dao.updateUser(u); }
    public boolean delete(int id){ return dao.deleteUser(id); }
}
