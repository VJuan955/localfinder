package org.example.interfaz.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BusquedaView extends VBox {

    private List<Archivo> archivos = new ArrayList<>();
    private VBox contenedorResultados = new VBox(12);

    public BusquedaView() {

        setSpacing(20);
        setPadding(new Insets(40));
        setStyle("-fx-background-color: #f8fafc;");
        setAlignment(javafx.geometry.Pos.TOP_CENTER);

        // ===== TÍTULO =====
        Label titulo = new Label("🔍 LocalFinder");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        // ===== BUSCADOR =====
        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar archivos...");
        txtBuscar.setPrefWidth(420);

        Button btnAvanzado = new Button("🔧 Búsqueda avanzada");

        btnAvanzado.setStyle(
                "-fx-background-color: #1d4ed8;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 18;" +
                "-fx-padding: 8 14;"
        );

        btnAvanzado.setOnAction(e -> abrirVentanaAvanzada(txtBuscar));

        HBox barra = new HBox(10, txtBuscar, btnAvanzado);
        barra.setAlignment(javafx.geometry.Pos.CENTER);

        contenedorResultados.setAlignment(javafx.geometry.Pos.CENTER);
        contenedorResultados.setPadding(new Insets(10));

        // ===== DATOS DE EJEMPLO =====
        archivos.add(new Archivo("Reporte_Anual.pdf",
                "C:/Users/Michell/Documents/Reporte_Anual.pdf",
                "PDF",
                "Michell",
                "12/03/2026",
                "2 MB",
                "finanzas"));

        archivos.add(new Archivo("Tesis_Final.docx",
                "C:/Users/Jose/Desktop/Tesis_Final.docx",
                "DOCX",
                "Jose",
                "01/06/2026",
                "1.5 MB",
                "investigacion"));

        archivos.add(new Archivo("Notas_Clase.txt",
                "D:/Cursos/Java/Notas_Clase.txt",
                "TXT",
                "Juan",
                "03/06/2026",
                "300 KB",
                "java"));

        archivos.add(new Archivo("Proyecto_LocalFinder.pdf",
                "C:/Users/Johan/Desktop/Proyecto_LocalFinder.pdf",
                "PDF",
                "Johan",
                "10/02/2026",
                "4 MB",
                "software"));

        archivos.add(new Archivo("Resumen_Algoritmos.docx",
                "C:/Users/Michell/Documents/Resumen_Algoritmos.docx",
                "DOCX",
                "Michell",
                "20/01/2026",
                "900 KB",
                "algoritmos"));

        archivos.add(new Archivo("BD_SQL.txt",
                "D:/BasesDatos/BD_SQL.txt",
                "TXT",
                "Jose",
                "15/05/2026",
                "600 KB",
                "base de datos"));

        archivos.add(new Archivo("Manual_Sistema.pdf",
                "C:/Docs/Manual_Sistema.pdf",
                "PDF",
                "Juan",
                "08/04/2026",
                "3 MB",
                "manual"));

        // ===== BÚSQUEDA SIMPLE =====
        txtBuscar.setOnKeyReleased(e ->
                aplicarFiltro(txtBuscar.getText(), "Todos", "", "", "", "", "")
        );

        getChildren().addAll(
                titulo,
                barra,
                contenedorResultados
        );

        aplicarFiltro("", "Todos", "", "", "", "", "");
    }

    // =========================================================
    // 🔎 MOTOR DE BÚSQUEDA
    // =========================================================
    private void aplicarFiltro(String texto,
                               String tipo,
                               String palabraClave,
                               String propietario,
                               String etiquetas,
                               String fecha,
                               String tamaño) {

        String q = texto.toLowerCase();

        List<Archivo> filtrados = archivos.stream()
                .filter(a ->
                        a.nombre.toLowerCase().contains(q) ||
                        a.ruta.toLowerCase().contains(q)
                )
                .filter(a ->
                        tipo.equals("Todos") ||
                        a.tipo.equalsIgnoreCase(tipo)
                )
                .filter(a ->
                        palabraClave.isEmpty() ||
                        a.nombre.toLowerCase().contains(palabraClave.toLowerCase())
                )
                .filter(a ->
                        propietario.isEmpty() ||
                        a.propietario.toLowerCase().contains(propietario.toLowerCase())
                )
                .filter(a ->
                        etiquetas.isEmpty() ||
                        a.etiquetas.toLowerCase().contains(etiquetas.toLowerCase())
                )
                .filter(a ->
                        fecha.isEmpty() ||
                        a.fechaModificacion.contains(fecha)
                )
                .filter(a ->
                        tamaño.isEmpty() ||
                        a.tamaño.toLowerCase().contains(tamaño.toLowerCase())
                )
                .collect(Collectors.toList());

        contenedorResultados.getChildren().clear();

        for (Archivo a : filtrados) {
            contenedorResultados.getChildren().add(crearCard(a));
        }
    }

    // =========================================================
    // 📄 CARD VISUAL
    // =========================================================
    private VBox crearCard(Archivo a) {

        VBox card = new VBox(6);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);"
        );

        Label nombre = new Label(a.nombre);
        nombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label ruta = new Label("📁 " + a.ruta);
        ruta.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        Label fecha = new Label("📅 Modificado: " + a.fechaModificacion);
        fecha.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");

        Label extra = new Label(
                "Tipo: " + a.tipo +
                " | Tamaño: " + a.tamaño +
                " | Propietario: " + a.propietario
        );

        extra.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");

        card.getChildren().addAll(nombre, ruta, fecha, extra);

        return card;
    }

    // =========================================================
    // 🪟 VENTANA AVANZADA (MODAL)
    // =========================================================
    private void abrirVentanaAvanzada(TextField txtBuscarPrincipal) {

        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle("🔧 Búsqueda avanzada");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8fafc;");

        ComboBox<String> tipo = new ComboBox<>();
        tipo.getItems().addAll("Todos", "PDF", "DOCX", "TXT");
        tipo.setValue("Todos");

        TextField palabras = new TextField();
        palabras.setPromptText("Palabras clave");

        TextField propietario = new TextField();
        propietario.setPromptText("Propietario");

        TextField etiquetas = new TextField();
        etiquetas.setPromptText("Etiquetas");

        TextField fecha = new TextField();
        fecha.setPromptText("Fecha (ej: 12/03/2026)");

        TextField tamaño = new TextField();
        tamaño.setPromptText("Tamaño (ej: 2 MB)");

        Button buscar = new Button("🔍 Buscar");

        buscar.setStyle(
                "-fx-background-color: #16a34a;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 18;" +
                "-fx-padding: 8 16;"
        );

        buscar.setOnAction(e -> {

            aplicarFiltro(
                    txtBuscarPrincipal.getText(),
                    tipo.getValue(),
                    palabras.getText(),
                    propietario.getText(),
                    etiquetas.getText(),
                    fecha.getText(),
                    tamaño.getText()
            );

            ventana.close();
        });

        root.getChildren().addAll(
                new Label("Tipo"), tipo,
                new Label("Palabras clave"), palabras,
                new Label("Fecha"), fecha,
                new Label("Tamaño"), tamaño,
                new Label("Propietario"), propietario,
                new Label("Etiquetas"), etiquetas,
                buscar
        );

        Scene scene = new Scene(root, 420, 420);
        ventana.setScene(scene);
        ventana.setResizable(false);
        ventana.centerOnScreen();
        ventana.showAndWait();
    }

    // =========================================================
    // 📦 MODELO
    // =========================================================
    class Archivo {

        String nombre;
        String ruta;
        String tipo;
        String propietario;
        String fechaModificacion;
        String tamaño;
        String etiquetas;

        public Archivo(String nombre, String ruta, String tipo,
                       String propietario, String fechaModificacion,
                       String tamaño, String etiquetas) {

            this.nombre = nombre;
            this.ruta = ruta;
            this.tipo = tipo;
            this.propietario = propietario;
            this.fechaModificacion = fechaModificacion;
            this.tamaño = tamaño;
            this.etiquetas = etiquetas;
        }
    }
}