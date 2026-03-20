package com.banking.view;

import com.banking.controller.UserController;
import com.banking.model.User;
import com.banking.util.AlertHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

public class UserManagementView {

    private BorderPane root;
    private TableView<User> table;
    private UserController controller;

    public UserManagementView() {
        controller = new UserController();
        root = new BorderPane();
        root.getStyleClass().add("page-content");
        root.setPadding(new Insets(30));

        VBox header = new VBox(4);
        Label title = new Label("User Management");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Manage system users and access roles");
        subtitle.getStyleClass().add("page-subtitle");
        header.getChildren().addAll(title, subtitle);

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(14, 0, 14, 0));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Add User");
        addBtn.getStyleClass().add("primary-btn");
        addBtn.setOnAction(e -> showForm(null));

        toolbar.getChildren().addAll(spacer, addBtn);

        table = buildTable();

        VBox content = new VBox(0, header, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(content);

        refreshTable();
    }

    private TableView<User> buildTable() {
        TableView<User> tv = new TableView<>();
        tv.getStyleClass().add("data-table");

        TableColumn<User, Integer> idCol   = col("ID", "userId", 60);
        TableColumn<User, String> userCol  = col("Username", "username", 160);
        TableColumn<User, String> roleCol  = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(100);
        roleCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (!empty && item != null) {
                    String color = switch (item) {
                        case "Admin"    -> "#8E44AD";
                        case "Staff"    -> "#2980B9";
                        default         -> "#27AE60";
                    };
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                } else setStyle("");
            }
        });

        TableColumn<User, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(160);
        actionCol.setCellFactory(c -> new TableCell<>() {
            final Button editBtn = new Button("Edit");
            final Button delBtn  = new Button("Delete");
            final HBox box = new HBox(6, editBtn, delBtn);
            {
                editBtn.getStyleClass().add("action-btn-edit");
                delBtn.getStyleClass().add("action-btn-delete");
                box.setAlignment(Pos.CENTER);
                editBtn.setOnAction(e -> showForm(getTableView().getItems().get(getIndex())));
                delBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    if (AlertHelper.showConfirm("Delete User", "Delete user '" + u.getUsername() + "'?")) {
                        controller.delete(u.getUserId());
                        refreshTable();
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tv.getColumns().addAll(idCol, userCol, roleCol, actionCol);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<User, T> col(String title, String prop, double width) {
        TableColumn<User, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private void showForm(User existing) {
        boolean isEdit = existing != null;
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit User" : "Add New User");

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField userField = new TextField(isEdit ? existing.getUsername() : "");
        userField.setPrefWidth(220);
        PasswordField passField = new PasswordField();
        passField.setPrefWidth(220);
        if (isEdit) passField.setPromptText("Leave blank to keep current");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Admin", "Staff", "Customer");
        roleCombo.setValue(isEdit ? existing.getRole() : "Staff");
        roleCombo.setPrefWidth(220);

        grid.add(new Label("Username:"), 0, 0); grid.add(userField, 1, 0);
        grid.add(new Label("Password:"), 0, 1); grid.add(passField, 1, 1);
        grid.add(new Label("Role:"),     0, 2); grid.add(roleCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/com/banking/css/style.css").toExternalForm());

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                if (userField.getText().isBlank()) { AlertHelper.showError("Validation", "Username required."); return null; }
                User u = isEdit ? existing : new User();
                u.setUsername(userField.getText().trim());
                if (!passField.getText().isBlank()) u.setPassword(passField.getText());
                else if (!isEdit) { AlertHelper.showError("Validation", "Password required for new user."); return null; }
                u.setRole(roleCombo.getValue());
                return u;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(u -> {
            boolean ok = isEdit ? controller.update(u) : controller.add(u);
            if (ok) { AlertHelper.showInfo("Success", isEdit ? "User updated." : "User added."); refreshTable(); }
            else AlertHelper.showError("Error", "Operation failed.");
        });
    }

    private void refreshTable() {
        table.getItems().setAll(controller.getAll());
    }

    public BorderPane getRoot() { return root; }
}
