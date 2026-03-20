package com.banking.view;

import com.banking.controller.TransactionController;
import com.banking.dao.AccountDAO;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.util.AlertHelper;
import com.banking.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

public class TransactionView {

    private BorderPane root;
    private TableView<Transaction> table;
    private TransactionController controller;

    public TransactionView() {
        controller = new TransactionController();
        root = new BorderPane();
        root.getStyleClass().add("page-content");
        root.setPadding(new Insets(30));

        VBox header = new VBox(4);
        Label title = new Label("Transaction Management");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Record deposits, withdrawals and transfers");
        subtitle.getStyleClass().add("page-subtitle");
        header.getChildren().addAll(title, subtitle);

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(14, 0, 14, 0));

        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search by account number...");
        searchField.setPrefWidth(280);
        searchField.getStyleClass().add("search-field");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        boolean canAdd = Session.getInstance().isAdmin() || Session.getInstance().isStaff();

        if (canAdd) {
            Button depositBtn = new Button("💰 Deposit");
            depositBtn.getStyleClass().add("primary-btn");
            Button withdrawBtn = new Button("💸 Withdraw");
            withdrawBtn.getStyleClass().addAll("primary-btn", "btn-warning");
            Button transferBtn = new Button("🔄 Transfer");
            transferBtn.getStyleClass().addAll("primary-btn", "btn-info");

            depositBtn.setOnAction(e  -> showTransactionForm("Deposit"));
            withdrawBtn.setOnAction(e -> showTransactionForm("Withdrawal"));
            transferBtn.setOnAction(e -> showTransferForm());
            toolbar.getChildren().addAll(searchField, spacer, depositBtn, withdrawBtn, transferBtn);
        } else {
            toolbar.getChildren().addAll(searchField, spacer);
        }

        table = buildTable();

        searchField.textProperty().addListener((obs, o, n) -> {
            List<Transaction> all = controller.getAll();
            if (n == null || n.isBlank()) table.getItems().setAll(all);
            else table.getItems().setAll(all.stream()
                .filter(t -> t.getAccountNumber().contains(n)).toList());
        });

        VBox content = new VBox(0, header, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(content);

        refreshTable();
    }

    private TableView<Transaction> buildTable() {
        TableView<Transaction> tv = new TableView<>();
        tv.getStyleClass().add("data-table");

        TableColumn<Transaction, Integer> idCol   = col("TXN ID", "transactionId", 70);
        TableColumn<Transaction, String> accCol   = col("Account Number", "accountNumber", 150);
        TableColumn<Transaction, String> typeCol  = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        typeCol.setPrefWidth(110);
        typeCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (!empty && item != null) {
                    String color = switch (item) {
                        case "Deposit"    -> "#27AE60";
                        case "Withdrawal" -> "#E74C3C";
                        default           -> "#E67E22";
                    };
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                } else setStyle("");
            }
        });

        TableColumn<Transaction, Double> amtCol   = new TableColumn<>("Amount (Rs.)");
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amtCol.setPrefWidth(140);
        amtCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f", item));
            }
        });

        TableColumn<Transaction, String> dateCol  = col("Date & Time", "transactionDate", 180);
        TableColumn<Transaction, Integer> staffCol = col("Staff ID", "staffId", 80);
        TableColumn<Transaction, String> descCol  = col("Description", "description", 200);

        tv.getColumns().addAll(idCol, accCol, typeCol, amtCol, dateCol, staffCol, descCol);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Transaction, T> col(String title, String prop, double width) {
        TableColumn<Transaction, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private void showTransactionForm(String type) {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle(type);
        dialog.setHeaderText("Record a " + type.toLowerCase() + " transaction");

        ButtonType saveType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20));

        AccountDAO accountDAO = new AccountDAO();
        List<Account> accounts = accountDAO.getAllAccounts().stream()
            .filter(a -> "Active".equals(a.getStatus())).toList();

        ComboBox<Account> accCombo = new ComboBox<>();
        accCombo.getItems().addAll(accounts);
        accCombo.setPrefWidth(280);
        if (!accounts.isEmpty()) accCombo.setValue(accounts.get(0));

        Label balanceInfo = new Label();
        balanceInfo.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
        accCombo.setOnAction(e -> {
            Account a = accCombo.getValue();
            if (a != null) balanceInfo.setText("Current Balance: Rs. " + String.format("%.2f", a.getBalance()));
        });
        if (!accounts.isEmpty()) balanceInfo.setText("Current Balance: Rs. " + String.format("%.2f", accounts.get(0).getBalance()));

        TextField amtField = new TextField();
        amtField.setPromptText("Enter amount");
        amtField.setPrefWidth(280);

        TextField descField = new TextField();
        descField.setPromptText("Optional description");
        descField.setPrefWidth(280);

        grid.add(new Label("Account:"),     0, 0); grid.add(accCombo,    1, 0);
        grid.add(new Label(""),             0, 1); grid.add(balanceInfo, 1, 1);
        grid.add(new Label("Amount (Rs.):"),0, 2); grid.add(amtField,    1, 2);
        grid.add(new Label("Description:"), 0, 3); grid.add(descField,   1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/com/banking/css/style.css").toExternalForm());

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                if (accCombo.getValue() == null) { AlertHelper.showError("Validation", "Select an account."); return null; }
                double amt;
                try {
                    amt = Double.parseDouble(amtField.getText().trim());
                    if (amt <= 0) throw new NumberFormatException();
                } catch (NumberFormatException e) { AlertHelper.showError("Validation", "Enter a valid positive amount."); return null; }

                if ("Withdrawal".equals(type) && amt > accCombo.getValue().getBalance()) {
                    AlertHelper.showError("Insufficient Funds",
                        "Withdrawal amount exceeds available balance of Rs. "
                        + String.format("%.2f", accCombo.getValue().getBalance()));
                    return null;
                }
                Transaction t = new Transaction();
                t.setAccountNumber(accCombo.getValue().getAccountNumber());
                t.setTransactionType(type);
                t.setAmount(amt);
                t.setStaffId(Session.getInstance().getCurrentUser().getUserId());
                t.setDescription(descField.getText().trim());
                return t;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(t -> {
            String result = controller.process(t);
            if (result == null) {
                AlertHelper.showInfo("Success", type + " of Rs. " +
                    String.format("%.2f", t.getAmount()) + " recorded.");
                refreshTable();
            } else {
                AlertHelper.showError("Error", result);
            }
        });
    }

    private void showTransferForm() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Transfer Funds");
        dialog.setHeaderText("Transfer between accounts");

        ButtonType saveType = new ButtonType("Transfer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20));

        AccountDAO accountDAO = new AccountDAO();
        List<Account> accounts = accountDAO.getAllAccounts().stream()
            .filter(a -> "Active".equals(a.getStatus())).toList();

        ComboBox<Account> fromCombo = new ComboBox<>(); fromCombo.getItems().addAll(accounts); fromCombo.setPrefWidth(260);
        ComboBox<Account> toCombo   = new ComboBox<>(); toCombo.getItems().addAll(accounts);   toCombo.setPrefWidth(260);
        if (accounts.size() >= 2) { fromCombo.setValue(accounts.get(0)); toCombo.setValue(accounts.get(1)); }

        TextField amtField = new TextField(); amtField.setPromptText("Amount to transfer"); amtField.setPrefWidth(260);

        grid.add(new Label("From Account:"), 0, 0); grid.add(fromCombo, 1, 0);
        grid.add(new Label("To Account:"),   0, 1); grid.add(toCombo,   1, 1);
        grid.add(new Label("Amount (Rs.):"), 0, 2); grid.add(amtField,  1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/com/banking/css/style.css").toExternalForm());

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                if (fromCombo.getValue() == null || toCombo.getValue() == null) {
                    AlertHelper.showError("Validation", "Select both accounts."); return null;
                }
                if (fromCombo.getValue().getAccountNumber().equals(toCombo.getValue().getAccountNumber())) {
                    AlertHelper.showError("Validation", "Cannot transfer to the same account."); return null;
                }
                double amt;
                try {
                    amt = Double.parseDouble(amtField.getText().trim());
                    if (amt <= 0) throw new NumberFormatException();
                } catch (NumberFormatException e) { AlertHelper.showError("Validation", "Enter a valid positive amount."); return null; }

                String result = controller.transfer(
                    fromCombo.getValue().getAccountNumber(),
                    toCombo.getValue().getAccountNumber(), amt,
                    Session.getInstance().getCurrentUser().getUserId());
                if (result == null) {
                    AlertHelper.showInfo("Success", "Transfer of Rs. " + String.format("%.2f", amt) + " completed.");
                    refreshTable();
                } else {
                    AlertHelper.showError("Transfer Failed", result);
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void refreshTable() {
        table.getItems().setAll(controller.getAll());
    }

    public BorderPane getRoot() { return root; }
}
