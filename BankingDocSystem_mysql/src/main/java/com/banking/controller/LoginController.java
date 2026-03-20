package com.banking.controller;

import com.banking.dao.UserDAO;
import com.banking.model.User;
import com.banking.util.Session;
import com.banking.view.DashboardView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {

    private final UserDAO userDAO = new UserDAO();
    private final Stage stage;

    public LoginController(Stage stage) {
        this.stage = stage;
    }

    /**
     * Authenticate and navigate. Returns error message or null on success.
     */
    public String login(String username, String password) {
        if (username.isBlank() || password.isBlank()) {
            return "Username and password are required.";
        }

        User user = userDAO.authenticate(username, password);
        if (user == null) {
            return "Invalid username or password.";
        }

        Session.getInstance().setCurrentUser(user);

        DashboardView dashboard = new DashboardView(stage);
        Scene scene = new Scene(dashboard.getRoot(), 1100, 700);
        scene.getStylesheets().add(
            getClass().getResource("/com/banking/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setWidth(1100);
        stage.setHeight(700);
        stage.centerOnScreen();

        return null; // no error
    }
}
