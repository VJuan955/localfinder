package org.example.interfaz;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.example.interfaz.controllers.MainController;
import org.example.interfaz.views.*;

public class MainView {

    private final BorderPane root;
    private final MainController controller;
    private boolean oscuro = true;

    public MainView(MainController controller) {

        this.controller = controller;
        this.root       = new BorderPane();

        
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(15));
        menu.setPrefWidth(220);
        aplicarTemaOscuro(menu);

        Button btnBusqueda  = new Button("🔎 Motor de Búsqueda");
        Button btnConfig    = new Button("⚙ Configuración");
        Button btnHistorial = new Button("🕒 Historial");
        Button btnEstado    = new Button("📊 Estado");
        Button btnTema      = new Button("🌙 Tema");

        
        String estiloBtn =
                "-fx-background-color:transparent;" +
                "-fx-text-fill:#cbd5e1;" +
                "-fx-font-size:13px;" +
                "-fx-alignment:CENTER_LEFT;" +
                "-fx-padding:10 14;" +
                "-fx-cursor:hand;";

        btnBusqueda .setStyle(estiloBtn);
        btnConfig   .setStyle(estiloBtn);
        btnHistorial.setStyle(estiloBtn);
        btnEstado   .setStyle(estiloBtn);
        btnTema     .setStyle(estiloBtn);

        btnBusqueda .setMaxWidth(Double.MAX_VALUE);
        btnConfig   .setMaxWidth(Double.MAX_VALUE);
        btnHistorial.setMaxWidth(Double.MAX_VALUE);
        btnEstado   .setMaxWidth(Double.MAX_VALUE);
        btnTema     .setMaxWidth(Double.MAX_VALUE);

        menu.getChildren().addAll(
                btnBusqueda,
                btnConfig,
                btnHistorial,
                btnEstado,
                btnTema
        );

        root.setLeft(menu);

        
        root.setCenter(new BusquedaView());

        
        btnBusqueda.setOnAction(e -> openView(new BusquedaView()));

        btnConfig.setOnAction(e -> openView(new ConfiguracionView(controller)));

        btnHistorial.setOnAction(e -> openView(new HistorialView(controller)));

        btnEstado.setOnAction(e -> openView(new EstadoView()));

        
        btnTema.setOnAction(e -> {
            oscuro = !oscuro;
            if (oscuro) {
                aplicarTemaOscuro(menu);
                root.setStyle("-fx-background-color:#0f172a;");
            } else {
                menu.setStyle("-fx-background-color:#f4f4f4;");
                root.setStyle("-fx-background-color:white;");
            }
        });
    }

    private void aplicarTemaOscuro(VBox menu) {
        menu.setStyle("-fx-background-color:#0f172a;");
    }

    private void openView(Parent view) {
        try {
            root.setCenter(view);
        } catch (Exception ex) {
            Label errorLabel = new Label("Error al abrir la vista: " + ex.getMessage());
            errorLabel.setStyle("-fx-text-fill:#b91c1c; -fx-font-size:14px;");

            VBox fallback = new VBox(15, errorLabel);
            fallback.setPadding(new Insets(30));

            root.setCenter(fallback);
        }
    }

    public Parent getRoot() {
        return root;
    }
}