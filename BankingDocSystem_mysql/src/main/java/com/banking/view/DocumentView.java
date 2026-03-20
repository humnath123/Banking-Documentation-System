package com.banking.view;

import com.banking.controller.DocumentController;
import com.banking.dao.CustomerDAO;
import com.banking.model.Customer;
import com.banking.model.Document;
import com.banking.util.AlertHelper;
import com.banking.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class DocumentView {

    private BorderPane root;
    private TableView<Document> table;
    private DocumentController controller;

    public DocumentView() {
        controller = new DocumentController();
        root = new BorderPane();
        root.getStyleClass().add("page-content");
        root.setPadding(new Insets(30));

        VBox header = new VBox(4);
        Label title = new Label("Document Management");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Upload, verify and manage customer documents");
        subtitle.getStyleClass().add("page-subtitle");
        header.getChildren().addAll(title, subtitle);

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(14, 0, 14, 0));

        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search by customer ID...");
        searchField.setPrefWidth(280);
        searchField.getStyleClass().add("search-field");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        boolean canUpload = Session.getInstance().isAdmin() || Session.getInstance().isStaff();
        toolbar.getChildren().add(searchField);
        toolbar.getChildren().add(spacer);

        if (canUpload) {
            Button uploadBtn = new Button("📤 Upload Document");
            uploadBtn.getStyleClass().add("primary-btn");
            uploadBtn.setOnAction(e -> showUploadForm());
            toolbar.getChildren().add(uploadBtn);
        }

        table = buildTable(canUpload);

        searchField.textProperty().addListener((obs, o, n) -> {
            List<Document> all = controller.getAll();
            if (n == null || n.isBlank()) table.getItems().setAll(all);
            else table.getItems().setAll(all.stream()
                .filter(d -> String.valueOf(d.getCustomerId()).contains(n)
                    || d.getDocumentType().toLowerCase().contains(n.toLowerCase()))
                .toList());
        });

        VBox content = new VBox(0, header, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(content);

        refreshTable();
    }

    private TableView<Document> buildTable(boolean canModify) {
        TableView<Document> tv = new TableView<>();
        tv.getStyleClass().add("data-table");

        TableColumn<Document, Integer> idCol     = col("Doc ID", "documentId", 70);
        TableColumn<Document, Integer> custCol   = col("Customer ID", "customerId", 100);
        TableColumn<Document, String> typeCol    = col("Document Type", "documentType", 160);
        TableColumn<Document, String> pathCol    = col("File Path", "filePath", 200);
        TableColumn<Document, String> dateCol    = col("Upload Date", "uploadDate", 160);
        TableColumn<Document, String> statusCol  = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("verifiedLabel"));
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (!empty && item != null) {
                    setStyle("Verified".equals(item)
                        ? "-fx-text-fill: #27AE60; -fx-font-weight: bold;"
                        : "-fx-text-fill: #E67E22; -fx-font-weight: bold;");
                } else setStyle("");
            }
        });

        tv.getColumns().addAll(idCol, custCol, typeCol, pathCol, dateCol, statusCol);

        if (canModify) {
            TableColumn<Document, Void> actionCol = new TableColumn<>("Actions");
            actionCol.setPrefWidth(180);
            actionCol.setCellFactory(c -> new TableCell<>() {
                final Button verifyBtn = new Button("Verify");
                final Button delBtn    = new Button("Delete");
                final HBox box = new HBox(6, verifyBtn, delBtn);
                {
                    verifyBtn.getStyleClass().add("action-btn-edit");
                    delBtn.getStyleClass().add("action-btn-delete");
                    box.setAlignment(Pos.CENTER);
                    verifyBtn.setOnAction(e -> {
                        Document d = getTableView().getItems().get(getIndex());
                        controller.verify(d.getDocumentId());
                        refreshTable();
                    });
                    delBtn.setOnAction(e -> {
                        Document d = getTableView().getItems().get(getIndex());
                        if (AlertHelper.showConfirm("Delete Document", "Delete this document record?")) {
                            controller.delete(d.getDocumentId());
                            refreshTable();
                        }
                    });
                }
                @Override protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : box);
                }
            });
            tv.getColumns().add(actionCol);
        }

        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Document, T> col(String title, String prop, double width) {
        TableColumn<Document, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private void showUploadForm() {
        Dialog<Document> dialog = new Dialog<>();
        dialog.setTitle("Upload Document");
        dialog.setHeaderText("Upload a customer document");

        ButtonType saveType = new ButtonType("Upload", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20));

        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> customers = customerDAO.getAllCustomers();
        ComboBox<Customer> custCombo = new ComboBox<>();
        custCombo.getItems().addAll(customers);
        custCombo.setPrefWidth(260);
        if (!customers.isEmpty()) custCombo.setValue(customers.get(0));

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("ID Proof", "Passport Photo", "Address Verification",
            "Birth Certificate", "Income Proof", "Other");
        typeCombo.setValue("ID Proof");
        typeCombo.setPrefWidth(260);

        Label fileLabel = new Label("No file selected");
        fileLabel.setStyle("-fx-text-fill: #888;");
        Button browseBtn = new Button("Browse...");
        browseBtn.getStyleClass().add("action-btn-edit");
        final String[] selectedPath = {""};

        browseBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Document");
            fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images & PDFs", "*.png", "*.jpg", "*.jpeg", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File file = fc.showOpenDialog(null);
            if (file != null) {
                selectedPath[0] = file.getAbsolutePath();
                fileLabel.setText(file.getName());
                fileLabel.setStyle("-fx-text-fill: #333;");
            }
        });

        HBox fileBox = new HBox(8, browseBtn, fileLabel);
        fileBox.setAlignment(Pos.CENTER_LEFT);

        grid.add(new Label("Customer:"),       0, 0); grid.add(custCombo, 1, 0);
        grid.add(new Label("Document Type:"), 0, 1);  grid.add(typeCombo, 1, 1);
        grid.add(new Label("File:"),          0, 2);  grid.add(fileBox,   1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/com/banking/css/style.css").toExternalForm());

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                if (custCombo.getValue() == null) { AlertHelper.showError("Validation", "Select a customer."); return null; }
                Document doc = new Document();
                doc.setCustomerId(custCombo.getValue().getCustomerId());
                doc.setDocumentType(typeCombo.getValue());
                doc.setFilePath(selectedPath[0].isEmpty() ? "[No file - demo record]" : selectedPath[0]);
                doc.setVerifiedStatus(false);
                return doc;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(doc -> {
            if (controller.add(doc)) {
                AlertHelper.showInfo("Success", "Document uploaded.");
                refreshTable();
            } else {
                AlertHelper.showError("Error", "Failed to upload document.");
            }
        });
    }

    private void refreshTable() {
        table.getItems().setAll(controller.getAll());
    }

    public BorderPane getRoot() { return root; }
}
