package com.banking.view;

import com.banking.controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginView {

    private BorderPane root;

    public LoginView(Stage stage) {
        root = new BorderPane();
        root.getStyleClass().add("login-root");

        // Left panel — branding
        VBox brandPanel = new VBox(20);
        brandPanel.getStyleClass().add("brand-panel");
        brandPanel.setAlignment(Pos.CENTER);
        brandPanel.setPrefWidth(380);

        Text bankIcon = new Text("🏦");
        bankIcon.setStyle("-fx-font-size: 72px;");

        Label bankName = new Label("Banking\nDocumentation\nSystem");
        bankName.getStyleClass().add("brand-title");
        bankName.setAlignment(Pos.CENTER);

        Label tagline = new Label("Secure · Digital · Reliable");
        tagline.getStyleClass().add("brand-tagline");

        Label courseLabel = new Label("CIS096-1 — Principles of Programming");
        courseLabel.getStyleClass().add("brand-course");

        brandPanel.getChildren().addAll(bankIcon, bankName, tagline, courseLabel);
        root.setLeft(brandPanel);

        // Right panel — login form
        VBox formPanel = new VBox(18);
        formPanel.getStyleClass().add("login-form-panel");
        formPanel.setAlignment(Pos.CENTER);
        formPanel.setPadding(new Insets(60, 60, 60, 60));

        Label loginTitle = new Label("Sign In");
        loginTitle.getStyleClass().add("login-title");

        Label loginSubtitle = new Label("Enter your credentials to continue");
        loginSubtitle.getStyleClass().add("login-subtitle");

        Label userLabel = new Label("Username");
        userLabel.getStyleClass().add("field-label");
        TextField usernameField = new TextField();
        usernameField.setPromptText("e.g. admin, staff1, customer1");
        usernameField.getStyleClass().add("login-field");

        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("field-label");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.getStyleClass().add("login-field");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().add("login-btn");
        loginBtn.setPrefWidth(300);

        // Demo hint box
        VBox hintBox = new VBox(4);
        hintBox.getStyleClass().add("hint-box");
        hintBox.getChildren().addAll(
            new Label("Demo Credentials:"),
            new Label("Admin: admin / admin123"),
            new Label("Staff: staff1 / staff123"),
            new Label("Customer: customer1 / cust123")
        );
        hintBox.getChildren().forEach(n -> {
            if (n instanceof Label l) l.getStyleClass().add("hint-text");
        });

        formPanel.getChildren().addAll(
            loginTitle, loginSubtitle,
            userLabel, usernameField,
            passLabel, passwordField,
            errorLabel, loginBtn, hintBox
        );

        root.setCenter(formPanel);

        // Wire up controller
        LoginController controller = new LoginController(stage);

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String error = controller.login(username, password);
            if (error != null) {
                errorLabel.setText(error);
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
            }
        });

        passwordField.setOnAction(e -> loginBtn.fire());
    }

    public BorderPane getRoot() { return root; }
}
