package com.banking.view;

import com.banking.controller.CustomerController;
import com.banking.model.Customer;
import com.banking.util.AlertHelper;
import com.banking.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

public class CustomerView {

    private BorderPane root;
    private TableView<Customer> table;
    private TextField searchField;
    private CustomerController controller;

    public CustomerView() {
        controller = new CustomerController();
        root = new BorderPane();
        root.getStyleClass().add("page-content");
        root.setPadding(new Insets(30));

        // Header
        VBox header = new VBox(4);
        Label title = new Label("Customer Management");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Add, edit, search and manage customer records");
        subtitle.getStyleClass().add("page-subtitle");
        header.getChildren().addAll(title, subtitle);

        // Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(14, 0, 14, 0));

        searchField = new TextField();
        searchField.setPromptText("🔍  Search by name, phone, email or ID...");
        searchField.setPrefWidth(320);
        searchField.getStyleClass().add("search-field");
        searchField.textProperty().addListener((obs, o, n) -> refreshTable(controller.search(n)));

        Button addBtn = new Button("+ Add Customer");
        addBtn.getStyleClass().add("primary-btn");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(searchField, spacer, addBtn);

        // Table
        table = buildTable();

        VBox content = new VBox(0, header, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(content);

        // Button actions
        addBtn.setOnAction(e -> showForm(null));

        refreshTable(controller.getAll());
    }

    private TableView<Customer> buildTable() {
        TableView<Customer> tv = new TableView<>();
        tv.getStyleClass().add("data-table");

        TableColumn<Customer, Integer> idCol = col("ID", "customerId", 60);
        TableColumn<Customer, String> nameCol = col("Full Name", "name", 160);
        TableColumn<Customer, String> phoneCol = col("Phone", "phone", 120);
        TableColumn<Customer, String> emailCol = col("Email", "email", 180);
        TableColumn<Customer, String> addressCol = col("Address", "address", 160);
        TableColumn<Customer, String> dobCol = col("Date of Birth", "dateOfBirth", 120);

        TableColumn<Customer, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(160);
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button editBtn = new Button("Edit");
            final Button delBtn  = new Button("Delete");
            final HBox box = new HBox(6, editBtn, delBtn);
            {
                editBtn.getStyleClass().add("action-btn-edit");
                delBtn.getStyleClass().add("action-btn-delete");
                box.setAlignment(Pos.CENTER);
                editBtn.setOnAction(e -> showForm(getTableView().getItems().get(getIndex())));
                delBtn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());
                    if (AlertHelper.showConfirm("Delete Customer",
                            "Delete " + c.getName() + "? This cannot be undone.")) {
                        controller.delete(c.getCustomerId());
                        refreshTable(controller.getAll());
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tv.getColumns().addAll(idCol, nameCol, phoneCol, emailCol, addressCol, dobCol, actionCol);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Customer, T> col(String title, String prop, double width) {
        TableColumn<Customer, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private void refreshTable(List<Customer> customers) {
        table.getItems().setAll(customers);
    }

    private void showForm(Customer existing) {
        boolean isEdit = existing != null;
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Customer" : "Add New Customer");
        dialog.setHeaderText(isEdit ? "Update customer details" : "Enter new customer details");

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField nameField    = field(isEdit ? existing.getName() : "");
        TextField phoneField   = field(isEdit ? existing.getPhone() : "");
        TextField emailField   = field(isEdit ? existing.getEmail() : "");
        TextField addressField = field(isEdit ? existing.getAddress() : "");
        TextField dobField     = field(isEdit ? existing.getDateOfBirth() : "");
        dobField.setPromptText("YYYY-MM-DD");

        grid.add(new Label("Full Name:"),    0, 0); grid.add(nameField,    1, 0);
        grid.add(new Label("Phone:"),        0, 1); grid.add(phoneField,   1, 1);
        grid.add(new Label("Email:"),        0, 2); grid.add(emailField,   1, 2);
        grid.add(new Label("Address:"),      0, 3); grid.add(addressField, 1, 3);
        grid.add(new Label("Date of Birth:"),0, 4); grid.add(dobField,     1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/com/banking/css/style.css").toExternalForm());

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                // Validation
                if (nameField.getText().isBlank()) { AlertHelper.showError("Validation", "Name is required."); return null; }
                if (!phoneField.getText().matches("\\d{7,15}")) { AlertHelper.showError("Validation", "Phone must be 7–15 digits."); return null; }
                Customer c = isEdit ? existing : new Customer();
                c.setName(nameField.getText().trim());
                c.setPhone(phoneField.getText().trim());
                c.setEmail(emailField.getText().trim());
                c.setAddress(addressField.getText().trim());
                c.setDateOfBirth(dobField.getText().trim());
                return c;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(c -> {
            boolean ok = isEdit ? controller.update(c) : controller.add(c);
            if (ok) {
                AlertHelper.showInfo("Success", isEdit ? "Customer updated." : "Customer added.");
                refreshTable(controller.getAll());
            } else {
                AlertHelper.showError("Error", "Operation failed.");
            }
        });
    }

    private TextField field(String val) {
        TextField tf = new TextField(val);
        tf.setPrefWidth(250);
        return tf;
    }

    public BorderPane getRoot() { return root; }
}
