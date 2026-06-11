package org.example.interfaz.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ConfiguracionView extends VBox {

    private ListView<String> rutas;

    public ConfiguracionView() {

        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #f1f5f9;");

        // ===== TÍTULO =====
        Label titulo = new Label("⚙ Configuración del Sistema");
        titulo.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        // ===== PANEL PRINCIPAL =====
        VBox cardPrincipal = new VBox(15);
        cardPrincipal.setPadding(new Insets(20));
        cardPrincipal.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);"
        );

        // ===== LISTA DE RUTAS =====
        rutas = new ListView<>();
        rutas.getItems().addAll(
                "C:/Documentos",
                "D:/Proyectos",
                "C:/Users/Michell/Desktop"
        );

        rutas.setPrefHeight(160);
        rutas.setStyle(
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );

        // ===== INPUT =====
        TextField inputRuta = new TextField();
        inputRuta.setPromptText("➕ Agregar nueva ruta (ej: C:/Users/...)");
        inputRuta.setStyle(
                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 8;" +
                "-fx-border-color: #cbd5e1;"
        );

        // ===== BOTONES =====
        Button agregar = new Button("➕ Agregar ruta");
        Button eliminar = new Button("🗑 Eliminar seleccionada");

        String btnStyleBase =
                "-fx-background-radius: 12;" +
                "-fx-padding: 8 14;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;";

        agregar.setStyle(
                "-fx-background-color: #16a34a;" + btnStyleBase
        );

        eliminar.setStyle(
                "-fx-background-color: #dc2626;" + btnStyleBase
        );

        HBox botones = new HBox(10, agregar, eliminar);
        botones.setAlignment(Pos.CENTER_LEFT);

        // ===== ACCIONES =====
        agregar.setOnAction(e -> {
            String ruta = inputRuta.getText().trim();
            if (!ruta.isEmpty()) {
                rutas.getItems().add(ruta);
                inputRuta.clear();
            }
        });

        eliminar.setOnAction(e -> {
            String selected = rutas.getSelectionModel().getSelectedItem();
            if (selected != null) {
                rutas.getItems().remove(selected);
            }
        });

        // ===== SECCIÓN RUTAS =====
        Label subtitulo = new Label("📁 Rutas de indexación");
        subtitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox seccionRutas = new VBox(10, subtitulo, rutas, inputRuta, botones);

        // ===== ARMAR CARD =====
        cardPrincipal.getChildren().add(seccionRutas);

        // ===== FINAL =====
        getChildren().addAll(titulo, cardPrincipal);
    }
}