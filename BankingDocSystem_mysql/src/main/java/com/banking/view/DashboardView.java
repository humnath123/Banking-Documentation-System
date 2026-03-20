package com.banking.view;

import com.banking.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DashboardView {

    private BorderPane root;
    private StackPane contentArea;
    private Stage stage;

    public DashboardView(Stage stage) {
        this.stage = stage;
        root = new BorderPane();
        root.getStyleClass().add("dashboard-root");

        // Sidebar
        VBox sidebar = buildSidebar();
        root.setLeft(sidebar);

        // Top bar
        HBox topBar = buildTopBar();
        root.setTop(topBar);

        // Content area — starts with Dashboard Home
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        root.setCenter(contentArea);

        showDashboardHome();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);
        sidebar.setPadding(new Insets(20, 10, 20, 10));

        // Logo area
        VBox logoArea = new VBox(4);
        logoArea.setAlignment(Pos.CENTER);
        logoArea.setPadding(new Insets(10, 0, 20, 0));
        Text icon = new Text("🏦");
        icon.setStyle("-fx-font-size: 32px;");
        Label logoLabel = new Label("BDS");
        logoLabel.getStyleClass().add("sidebar-logo");
        logoArea.getChildren().addAll(icon, logoLabel);

        sidebar.getChildren().add(logoArea);
        sidebar.getChildren().add(new Separator());
        sidebar.getChildren().add(sideLabel("MAIN MENU"));

        // Navigation buttons
        sidebar.getChildren().add(navBtn("🏠  Dashboard", this::showDashboardHome));
        sidebar.getChildren().add(navBtn("👥  Customers", this::showCustomers));
        sidebar.getChildren().add(navBtn("💳  Accounts", this::showAccounts));
        sidebar.getChildren().add(navBtn("💸  Transactions", this::showTransactions));
        sidebar.getChildren().add(navBtn("📄  Documents", this::showDocuments));

        if (Session.getInstance().isAdmin()) {
            sidebar.getChildren().add(new Separator());
            sidebar.getChildren().add(sideLabel("ADMIN"));
            sidebar.getChildren().add(navBtn("👤  User Management", this::showUserManagement));
        }

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        // Logout
        Button logoutBtn = new Button("🚪  Logout");
        logoutBtn.getStyleClass().add("logout-btn");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> logout());
        sidebar.getChildren().add(logoutBtn);

        return sidebar;
    }

    private HBox buildTopBar() {
        HBox topBar = new HBox();
        topBar.getStyleClass().add("top-bar");
        topBar.setPadding(new Insets(14, 20, 14, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label pageTitle = new Label("Banking Documentation System");
        pageTitle.getStyleClass().add("top-bar-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String userInfo = Session.getInstance().getCurrentUser().getUsername()
            + " · " + Session.getInstance().getCurrentUser().getRole();
        Label userLabel = new Label("👤 " + userInfo);
        userLabel.getStyleClass().add("top-bar-user");

        topBar.getChildren().addAll(pageTitle, spacer, userLabel);
        return topBar;
    }

    private Button navBtn(String text, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private Label sideLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("sidebar-section-label");
        VBox.setMargin(lbl, new Insets(8, 0, 4, 8));
        return lbl;
    }

    private void setContent(javafx.scene.Node node) {
        contentArea.getChildren().setAll(node);
    }

    private void showDashboardHome() {
        setContent(new DashboardHomeView().getRoot());
    }

    private void showCustomers() {
        setContent(new CustomerView().getRoot());
    }

    private void showAccounts() {
        setContent(new AccountView().getRoot());
    }

    private void showTransactions() {
        setContent(new TransactionView().getRoot());
    }

    private void showDocuments() {
        setContent(new DocumentView().getRoot());
    }

    private void showUserManagement() {
        setContent(new UserManagementView().getRoot());
    }

    private void logout() {
        Session.getInstance().logout();
        LoginView loginView = new LoginView(stage);
        Scene scene = new Scene(loginView.getRoot(), 900, 600);
        scene.getStylesheets().add(getClass().getResource("/com/banking/css/style.css").toExternalForm());
        stage.setScene(scene);
    }

    public BorderPane getRoot() { return root; }
}
