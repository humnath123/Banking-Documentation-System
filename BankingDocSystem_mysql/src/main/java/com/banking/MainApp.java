package com.banking;

import com.banking.database.DatabaseConnection;
import com.banking.database.DatabaseInitializer;
import com.banking.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseInitializer.initialize();

        LoginView loginView = new LoginView(primaryStage);
        Scene scene = new Scene(loginView.getRoot(), 900, 600);
        scene.getStylesheets().add(getClass().getResource("/com/banking/css/style.css").toExternalForm());

        primaryStage.setTitle("Banking Documentation System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    @Override
    public void stop() {
        DatabaseConnection.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
