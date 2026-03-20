package com.banking.view;

import com.banking.dao.*;
import com.banking.model.Transaction;
import com.banking.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.List;

public class DashboardHomeView {

    private ScrollPane root;

    public DashboardHomeView() {
        VBox content = new VBox(24);
        content.setPadding(new Insets(30));
        content.getStyleClass().add("page-content");

        // Header
        Label title = new Label("Dashboard Overview");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Welcome back, " + Session.getInstance().getCurrentUser().getUsername()
            + " · " + Session.getInstance().getCurrentUser().getRole());
        subtitle.getStyleClass().add("page-subtitle");

        // Stats cards
        HBox statsRow = buildStatsRow();

        // Recent transactions
        VBox recentBox = buildRecentTransactions();

        content.getChildren().addAll(title, subtitle, statsRow, recentBox);

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.getStyleClass().add("scroll-pane");
    }

    private HBox buildStatsRow() {
        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();
        TransactionDAO transactionDAO = new TransactionDAO();
        DocumentDAO documentDAO = new DocumentDAO();

        HBox row = new HBox(16);
        row.getChildren().addAll(
            statCard("👥", "Total Customers", String.valueOf(customerDAO.getTotalCount()), "#4A90D9"),
            statCard("💳", "Total Accounts", String.valueOf(accountDAO.getTotalCount()), "#27AE60"),
            statCard("💸", "Transactions", String.valueOf(transactionDAO.getTotalCount()), "#E67E22"),
            statCard("📄", "Documents", String.valueOf(documentDAO.getTotalCount()), "#8E44AD")
        );
        return row;
    }

    private VBox statCard(String icon, String label, String value, String color) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox iconRow = new HBox(10);
        iconRow.setAlignment(Pos.CENTER_LEFT);
        Text iconText = new Text(icon);
        iconText.setStyle("-fx-font-size: 28px;");

        VBox textBox = new VBox(2);
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label nameLabel = new Label(label);
        nameLabel.getStyleClass().add("stat-label");
        textBox.getChildren().addAll(valueLabel, nameLabel);

        iconRow.getChildren().addAll(iconText, textBox);
        card.getChildren().add(iconRow);

        // Color bar at bottom
        Region bar = new Region();
        bar.setPrefHeight(4);
        bar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 2;");
        card.getChildren().add(bar);

        return card;
    }

    private VBox buildRecentTransactions() {
        VBox box = new VBox(12);
        box.getStyleClass().add("section-box");
        box.setPadding(new Insets(20));

        Label sectionTitle = new Label("Recent Transactions");
        sectionTitle.getStyleClass().add("section-title");

        TransactionDAO dao = new TransactionDAO();
        List<Transaction> recent = dao.getRecentTransactions(8);

        TableView<Transaction> table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setPrefHeight(280);

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        idCol.setPrefWidth(60);

        TableColumn<Transaction, String> accCol = new TableColumn<>("Account No.");
        accCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        accCol.setPrefWidth(160);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        typeCol.setPrefWidth(120);
        typeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    String color = switch (item) {
                        case "Deposit"    -> "#27AE60";
                        case "Withdrawal" -> "#E74C3C";
                        default           -> "#E67E22";
                    };
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Transaction, Double> amtCol = new TableColumn<>("Amount (Rs.)");
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amtCol.setPrefWidth(140);
        amtCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f", item));
            }
        });

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        dateCol.setPrefWidth(180);

        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(200);

        table.getColumns().addAll(idCol, accCol, typeCol, amtCol, dateCol, descCol);
        table.getItems().addAll(recent);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        box.getChildren().addAll(sectionTitle, table);
        return box;
    }

    public ScrollPane getRoot() { return root; }
}
