package org.example.interfaz.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class HistorialView extends VBox {

    public HistorialView() {

        setSpacing(15);
        setPadding(new Insets(30));

        Label titulo =
                new Label("🕒 Historial");

        ListView<String> historial =
                new ListView<>();

        historial.getItems().addAll(
                "lucene",
                "javafx",
                "sqlite",
                "maven"
        );

        getChildren().addAll(
                titulo,
                historial
        );
    }
}