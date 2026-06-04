package org.example.interfaz.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ConfiguracionView extends VBox {

    public ConfiguracionView() {

        setSpacing(15);
        setPadding(new Insets(30));

        Label titulo =
                new Label("⚙ Configuración del Índice");

        ListView<String> rutas =
                new ListView<>();

        rutas.getItems().addAll(
                "C:/Documentos",
                "D:/Proyectos"
        );

        Button agregar =
                new Button("Agregar Ruta");

        Button eliminar =
                new Button("Eliminar Ruta");

        getChildren().addAll(
                titulo,
                rutas,
                agregar,
                eliminar
        );
    }
}