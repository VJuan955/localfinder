package org.example.interfaz.views;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EstadoView extends VBox {

    public EstadoView() {

        setSpacing(15);
        setPadding(new Insets(30));

        Label titulo =
                new Label("📊 Estado del Sistema");

        Label bd =
                new Label("Base de Datos: Activa");

        Label docs =
                new Label("Documentos Indexados: 125");

        Label ultima =
                new Label("Última Indexación: Hoy");

        getChildren().addAll(
                titulo,
                bd,
                docs,
                ultima
        );
    }
}