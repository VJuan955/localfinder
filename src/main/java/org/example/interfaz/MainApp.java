package org.example.interfaz;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        MainView view = new MainView();

        Scene scene = new Scene(
                view.getRoot(),
                900,
                600
        );

        stage.setTitle("LocalFinder");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}