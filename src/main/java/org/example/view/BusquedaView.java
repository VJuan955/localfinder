package org.example.interfaz.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.interfaz.controllers.MainController;
import org.example.model.Archivo;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BusquedaView extends VBox {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    .withZone(ZoneId.systemDefault());

    private final MainController controller;
    private final VBox contenedorResultados;
    private final Label lblEstado;
    private String filtroTipoActivo = null;

    public BusquedaView(MainController controller) {
        this.controller = controller;

        setSpacing(20);
        setPadding(new Insets(40));
        setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("🔎 LocalFinder");
        titulo.setStyle(
                "-fx-font-size: 38px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e293b;"
        );

        Label subtitulo = new Label(
                "Busca documentos locales de forma rápida y segura"
        );
        subtitulo.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #64748b;"
        );

        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar archivos, documentos o palabras clave...");
        txtBuscar.setMaxWidth(650);
        txtBuscar.setStyle(
                "-fx-background-radius: 20;" +
                "-fx-border-radius: 20;" +
                "-fx-padding: 12;" +
                "-fx-font-size: 14px;" +
                "-fx-border-color: #cbd5e1;"
        );

        Button btnBuscar = new Button("Buscar");
        btnBuscar.setStyle(
                "-fx-background-color:#1e40af;" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:20;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:12 24;"
        );

        HBox barraBusqueda = new HBox(10, txtBuscar, btnBuscar);
        barraBusqueda.setAlignment(Pos.CENTER);

        Button filtroPdf = crearBotonFiltro("PDF", "pdf");
        Button filtroDocx = crearBotonFiltro("DOCX", "docx");
        Button filtroTxt = crearBotonFiltro("TXT", "txt");
        Button filtroTodos = crearBotonFiltro("Todos", null);
        aplicarEstiloFiltro(filtroTodos, true);

        HBox filtros = new HBox(10, filtroPdf, filtroDocx, filtroTxt, filtroTodos);
        filtros.setAlignment(Pos.CENTER);

        lblEstado = new Label("Escribe un término y presiona Enter para buscar.");
        lblEstado.setStyle("-fx-text-fill:#64748b; -fx-font-size:13px;");

        contenedorResultados = new VBox(12);
        contenedorResultados.setAlignment(Pos.TOP_CENTER);
        contenedorResultados.setMaxWidth(700);

        ScrollPane scroll = new ScrollPane(contenedorResultados);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-border-color:transparent;");
        scroll.setPrefViewportHeight(320);

        Runnable ejecutarBusqueda = () -> {
            String consulta = txtBuscar.getText().trim();
            if (consulta.isEmpty()) {
                mostrarEstado("⚠ Escribe un término de búsqueda.", "#b45309");
                return;
            }
            mostrarEstado("Buscando...", "#64748b");
            try {
                List<Archivo> resultados = controller.buscar(consulta, filtroTipoActivo);
                mostrarResultados(resultados);
                mostrarEstado(
                        resultados.isEmpty()
                                ? "No se encontraron resultados para \"" + consulta + "\"."
                                : "Se encontraron " + resultados.size() + " resultado(s).",
                        resultados.isEmpty() ? "#b45309" : "#15803d"
                );
            } catch (Exception ex) {
                mostrarEstado("✘ Error en la búsqueda: " + ex.getMessage(), "#b91c1c");
            }
        };

        btnBuscar.setOnAction(e -> ejecutarBusqueda.run());
        txtBuscar.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                ejecutarBusqueda.run();
            }
        });

        getChildren().addAll(titulo, subtitulo, barraBusqueda, filtros, lblEstado, scroll);
    }

    private final java.util.List<Button> botonesFiltro = new java.util.ArrayList<>();

    private Button crearBotonFiltro(String etiqueta, String tipo) {
        Button btn = new Button(etiqueta);
        aplicarEstiloFiltro(btn, false);
        botonesFiltro.add(btn);

        btn.setOnAction(e -> {
            filtroTipoActivo = tipo;
            for (Button b : botonesFiltro) {
                aplicarEstiloFiltro(b, b == btn);
            }
        });

        return btn;
    }

    private void aplicarEstiloFiltro(Button btn, boolean activo) {
        if (activo) {
            btn.setStyle(
                    "-fx-background-color:#1e40af;" +
                    "-fx-background-radius:20;" +
                    "-fx-text-fill:white;" +
                    "-fx-font-weight:bold;"
            );
        } else {
            btn.setStyle(
                    "-fx-background-color:#e2e8f0;" +
                    "-fx-background-radius:20;" +
                    "-fx-text-fill:#334155;" +
                    "-fx-font-weight:bold;"
            );
        }
    }

    private void mostrarResultados(List<Archivo> resultados) {
        contenedorResultados.getChildren().clear();

        if (resultados.isEmpty()) {
            contenedorResultados.getChildren().add(
                    new Label("Sin coincidencias. Verifica que hayas sincronizado el índice en Configuración.")
            );
            return;
        }

        for (Archivo archivo : resultados) {
            contenedorResultados.getChildren().add(crearTarjeta(archivo));
        }
    }

    private VBox crearTarjeta(Archivo archivo) {
        String icono = switch (archivo.getTipoArchivo().toLowerCase()) {
            case "pdf" -> "📄";
            case "docx" -> "📝";
            case "txt" -> "📃";
            default -> "📁";
        };

        String detalle = formatearTamano(archivo.getTamano())
                + " • Modificado "
                + FORMATO_FECHA.format(Instant.ofEpochMilli(archivo.getFechaModificacion()));

        VBox card = new VBox(6);
        card.setPadding(new Insets(18));
        card.setMaxWidth(700);
        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-background-radius:15;" +
                "-fx-border-radius:15;" +
                "-fx-border-color:#e2e8f0;"
        );

        Label nombre = new Label(icono + " " + archivo.getNombreArchivo());
        nombre.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#0f172a;");

        Label ruta = new Label(archivo.getRutaArchivo());
        ruta.setStyle("-fx-text-fill:#475569;");
        ruta.setWrapText(true);

        Label info = new Label(detalle);
        info.setStyle("-fx-text-fill:#64748b;");

        Button btnAbrir = new Button("📂 Abrir archivo");
        btnAbrir.setStyle(
                "-fx-background-color:#1e40af;" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:8;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:6 14;" +
                "-fx-cursor:hand;"
        );
        btnAbrir.setOnAction(e -> {
            try {
                controller.abrirArchivo(archivo.getRutaArchivo());
            } catch (Exception ex) {
                mostrarEstado("✘ No se pudo abrir: " + ex.getMessage(), "#b91c1c");
            }
        });

        card.getChildren().addAll(nombre, ruta, info, btnAbrir);
        return card;
    }

    private String formatearTamano(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    private void mostrarEstado(String mensaje, String color) {
        lblEstado.setText(mensaje);
        lblEstado.setStyle("-fx-text-fill:" + color + "; -fx-font-size:13px;");
    }
}
