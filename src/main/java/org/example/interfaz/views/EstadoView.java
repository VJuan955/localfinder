package org.example.interfaz.views;

import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.Random;

public class EstadoView extends VBox {

    public EstadoView() {

        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #f1f5f9;");

        // ===== TÍTULO =====
        Label titulo = new Label("📊 Estado del Sistema");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // =====================================================
        // 📊 PANEL GENERAL
        // =====================================================
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);"
        );

        // =====================================================
        // 🧠 CPU USO (GRÁFICO LINEAL SIMULADO)
        // =====================================================
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<Number, Number> cpuChart = new LineChart<>(xAxis, yAxis);
        cpuChart.setTitle("Uso de CPU (%)");

        XYChart.Series<Number, Number> cpu = new XYChart.Series<>();
        cpu.setName("CPU");

        Random r = new Random();

        for (int i = 0; i < 10; i++) {
            cpu.getData().add(new XYChart.Data<>(i, 20 + r.nextInt(60)));
        }

        cpuChart.getData().add(cpu);
        cpuChart.setPrefHeight(200);

        // =====================================================
        // 🧠 RAM USO (BARRA)
        // =====================================================
        CategoryAxis cat = new CategoryAxis();
        NumberAxis val = new NumberAxis();

        BarChart<String, Number> ramChart = new BarChart<>(cat, val);
        ramChart.setTitle("Uso de RAM (%)");

        XYChart.Series<String, Number> ram = new XYChart.Series<>();
        ram.setName("RAM");

        ram.getData().add(new XYChart.Data<>("Usado", 65));
        ram.getData().add(new XYChart.Data<>("Libre", 35));

        ramChart.getData().add(ram);
        ramChart.setPrefHeight(200);

        // =====================================================
        // 📁 ESTADO DEL SISTEMA
        // =====================================================
        VBox info = new VBox(10);

        Label indice = crear("Índice Local", "Activo (98%)");
        Label total = crear("Total de archivos", "125");
        Label sync = crear("Última sincronización", "Hoy - 10:45 AM");
        Label integridad = crear("Integridad del sistema", "OK (Sin errores)");

        info.getChildren().addAll(indice, total, sync, integridad);

        // =====================================================
        // ARMADO FINAL
        // =====================================================
        panel.getChildren().addAll(cpuChart, ramChart, info);

        getChildren().addAll(titulo, panel);
    }

    // =====================================================
    // 🎨 ESTILO TIPO SISTEMA
    // =====================================================
    private Label crear(String titulo, String valor) {

        Label label = new Label(titulo + ": " + valor);

        label.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;" +
                "-fx-background-color: #f8fafc;" +
                "-fx-padding: 10;" +
                "-fx-background-radius: 10;"
        );

        return label;
    }
}