package com.banking.view;

import com.banking.controller.AccountController;
import com.banking.dao.CustomerDAO;
import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.util.AlertHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;
import java.util.UUID;

public class AccountView {

    private BorderPane root;
    private TableView<Account> table;
    private AccountController controller;

    public AccountView() {
        controller = new AccountController();
        root = new BorderPane();
        root.getStyleClass().add("page-content");
        root.setPadding(new Insets(30));

        VBox header = new VBox(4);
        Label title = new Label("Account Management");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Create and manage bank accounts");
        subtitle.getStyleClass().add("page-subtitle");
        header.getChildren().addAll(title, subtitle);

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(14, 0, 14, 0));

        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search by account number or customer ID...");
        searchField.setPrefWidth(320);
        searchField.getStyleClass().add("search-field");

        Button addBtn = new Button("+ New Account");
        addBtn.getStyleClass().add("primary-btn");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        toolbar.getChildren().addAll(searchField, spacer, addBtn);

        table = buildTable();

        VBox content = new VBox(0, header, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(content);

        addBtn.setOnAction(e -> showAddForm());
        searchField.textProperty().addListener((obs, o, n) -> {
            List<Account> all = controller.getAll();
            if (n == null || n.isBlank()) {
                table.getItems().setAll(all);
            } else {
                table.getItems().setAll(all.stream()
                    .filter(a -> a.getAccountNumber().contains(n)
                        || String.valueOf(a.getCustomerId()).contains(n))
                    .toList());
            }
        });

        refreshTable();
    }

    private TableView<Account> buildTable() {
        TableView<Account> tv = new TableView<>();
        tv.getStyleClass().add("data-table");

        TableColumn<Account, String> accCol    = col("Account Number", "accountNumber", 160);
        TableColumn<Account, Integer> custCol  = col2("Customer ID", "customerId", 100);
        TableColumn<Account, String> typeCol   = col("Type", "accountType", 110);
        TableColumn<Account, Double> balCol    = new TableColumn<>("Balance (Rs.)");
        balCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balCol.setPrefWidth(140);
        balCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f", item));
                setStyle(empty || item == null ? "" : "-fx-font-weight: bold; -fx-text-fill: #27AE60;");
            }
        });

        TableColumn<Account, String> statusCol = col("Status", "status", 90);
        statusCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (!empty && item != null) {
                    setStyle("Active".equals(item)
                        ? "-fx-text-fill: #27AE60; -fx-font-weight: bold;"
                        : "-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Account, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(180);
        actionCol.setCellFactory(c -> new TableCell<>() {
            final Button closeBtn  = new Button("Close");
            final Button activeBtn = new Button("Activate");
            final HBox box = new HBox(6, closeBtn, activeBtn);
            {
                closeBtn.getStyleClass().add("action-btn-delete");
                activeBtn.getStyleClass().add("action-btn-edit");
                box.setAlignment(Pos.CENTER);
                closeBtn.setOnAction(e -> {
                    Account a = getTableView().getItems().get(getIndex());
                    if (AlertHelper.showConfirm("Close Account", "Close account " + a.getAccountNumber() + "?")) {
                        controller.updateStatus(a.getAccountNumber(), "Closed");
                        refreshTable();
                    }
                });
                activeBtn.setOnAction(e -> {
                    Account a = getTableView().getItems().get(getIndex());
                    controller.updateStatus(a.getAccountNumber(), "Active");
                    refreshTable();
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tv.getColumns().addAll(accCol, custCol, typeCol, balCol, statusCol, actionCol);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Account, T> col(String title, String prop, double width) {
        TableColumn<Account, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Account, T> col2(String title, String prop, double width) {
        return col(title, prop, width);
    }

    private void showAddForm() {
        Dialog<Account> dialog = new Dialog<>();
        dialog.setTitle("Create New Account");
        dialog.setHeaderText("Fill in account details");

        ButtonType saveType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20));

        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> customers = customerDAO.getAllCustomers();
        ComboBox<Customer> custCombo = new ComboBox<>();
        custCombo.getItems().addAll(customers);
        custCombo.setPrefWidth(250);
        if (!customers.isEmpty()) custCombo.setValue(customers.get(0));

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Savings", "Current", "Fixed Deposit");
        typeCombo.setValue("Savings");
        typeCombo.setPrefWidth(250);

        TextField balField = new TextField("0.00");
        balField.setPrefWidth(250);

        // Auto-generate account number
        String accNum = "ACC-" + java.time.Year.now().getValue() + "-" + String.format("%04d",
            (int)(Math.random() * 9000) + 1000);
        Label accNumLabel = new Label(accNum);
        accNumLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4A90D9;");

        grid.add(new Label("Account Number:"), 0, 0); grid.add(accNumLabel, 1, 0);
        grid.add(new Label("Customer:"),        0, 1); grid.add(custCombo,   1, 1);
        grid.add(new Label("Account Type:"),    0, 2); grid.add(typeCombo,   1, 2);
        grid.add(new Label("Initial Balance:"), 0, 3); grid.add(balField,    1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/com/banking/css/style.css").toExternalForm());

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                if (custCombo.getValue() == null) { AlertHelper.showError("Validation", "Select a customer."); return null; }
                double bal;
                try { bal = Double.parseDouble(balField.getText()); }
                catch (NumberFormatException e) { AlertHelper.showError("Validation", "Invalid balance."); return null; }
                return new Account(accNum, custCombo.getValue().getCustomerId(), typeCombo.getValue(), bal, "Active");
            }
            return null;
        });

        dialog.showAndWait().ifPresent(a -> {
            if (controller.add(a)) {
                AlertHelper.showInfo("Success", "Account created.");
                refreshTable();
            } else {
                AlertHelper.showError("Error", "Failed to create account.");
            }
        });
    }

    private void refreshTable() {
        table.getItems().setAll(controller.getAll());
    }

    public BorderPane getRoot() { return root; }
}
