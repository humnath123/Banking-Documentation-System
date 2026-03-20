module com.banking {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    // MySQL Connector/J is loaded at runtime via DriverManager (no explicit requires needed)

    opens com.banking to javafx.fxml;
    opens com.banking.model to javafx.base;
    opens com.banking.view to javafx.fxml;
    opens com.banking.controller to javafx.fxml;

    exports com.banking;
    exports com.banking.model;
    exports com.banking.view;
    exports com.banking.controller;
    exports com.banking.dao;
    exports com.banking.util;
    exports com.banking.database;
}
