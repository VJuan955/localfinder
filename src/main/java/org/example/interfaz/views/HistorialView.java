package org.example.interfaz.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistorialView extends VBox {

    private ListView<HistorialItem> lista;

    public HistorialView() {

        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #f1f5f9;");

        // ===== TÍTULO =====
        Label titulo = new Label("🕒 Historial de Búsquedas");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // ===== LISTA =====
        lista = new ListView<>();
        lista.setPrefHeight(300);

        // DATOS DE EJEMPLO (SIMULANDO BÚSQUEDAS REALES)
        lista.getItems().addAll(
                new HistorialItem("Reporte_Anual.pdf", 5),
                new HistorialItem("Tesis_Final.docx", 0),
                new HistorialItem("Notas_Clase.txt", 12),
                new HistorialItem("Proyecto_LocalFinder.pdf", 3)
        );

        lista.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(HistorialItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                VBox card = new VBox(5);
                card.setPadding(new Insets(10));

                String color = item.resultados > 0 ? "#16a34a" : "#dc2626";

                Label nombre = new Label("📄 " + item.nombreArchivo);
                nombre.setStyle("-fx-font-weight: bold;");

                Label fecha = new Label("📅 " + item.fecha);
                Label resultados = new Label("Resultados: " + item.resultados);

                resultados.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");

                Label estado = new Label(item.resultados > 0 ? "✔ Con resultados" : "❌ Sin resultados");
                estado.setStyle("-fx-text-fill: " + color + ";");

                card.getChildren().addAll(nombre, fecha, resultados, estado);

                setGraphic(card);
            }
        });

        // ===== BOTÓN LIMPIAR =====
        Button limpiar = new Button("🧹 Limpiar historial");

        limpiar.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 8 14;"
        );

        limpiar.setOnAction(e -> lista.getItems().clear());

        // ===== PANEL =====
        VBox card = new VBox(15, lista, limpiar);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);"
        );

        getChildren().addAll(titulo, card);
    }

    // ===== MODELO =====
    static class HistorialItem {

        String nombreArchivo;
        String fecha;
        int resultados;

        public HistorialItem(String nombreArchivo, int resultados) {
            this.nombreArchivo = nombreArchivo;
            this.resultados = resultados;
            this.fecha = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    }
}