package org.example.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import org.example.dao.impl.*;
import org.example.model.Archivo;
import org.example.service.*;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class SearchController implements Initializable {
    @FXML private Button btnMotorBusqueda;
    @FXML private Button btnConfigIndice;
    @FXML private Button btnHistorial;
    @FXML private Button btnEstado;
    @FXML private Circle crawlerDot;
    @FXML private Label crawlerStatusLabel;
    @FXML private TextField searchField;
    @FXML private HBox filterChipsBox;
    @FXML private ToggleButton chip1;
    @FXML private ToggleButton chip2;
    @FXML private VBox resultsContainer;
    @FXML private ScrollPane resultsScrollPane;

    private SistemaBusquedaService searchService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterChipsBox.setVisible(false);
        filterChipsBox.setManaged(false);

        inicializarBackend();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            performSearch(newVal.trim());
        });
    }

    private void inicializarBackend() {
        setCrawlerStatus(false, "Iniciando motor...");

        CompletableFuture.runAsync(() -> {
            try {
                CrawlerService crawler = new CrawlerService();
                ExtractorContenidoService extractor = new ExtractorContenidoService();
                IndexadorLuceneService indexador = new IndexadorLuceneService();

                DirectorioDAOImpl directorioDAO = new DirectorioDAOImpl();
                ArchivoDAOImpl archivoDAO = new ArchivoDAOImpl();
                BusquedaDAOImpl busquedaDAO = new BusquedaDAOImpl();
                ResultadoDAOImpl resultadoDAO = new ResultadoDAOImpl();

                searchService = new SistemaBusquedaService(
                        crawler,
                        extractor,
                        indexador,
                        directorioDAO,
                        archivoDAO,
                        busquedaDAO,
                        resultadoDAO
                );

                Platform.runLater(() -> setCrawlerStatus(true, "Activo (Sincronizando)"));

                searchService.sincronizarIndice();

                Platform.runLater(() -> {
                    setCrawlerStatus(true, "Activo (En espera)");
                    showEmptyState("Motor listo. Escribe para buscar.");
                });
            } catch (IOException e) {
                Platform.runLater(() -> setCrawlerStatus(false, "Error crítico de E/S"));
                System.err.println("Error al inicializar el servicio SOLID: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onMotorBusqueda() { setActiveNav(btnMotorBusqueda); }

    @FXML
    private void onConfigIndice() {
        setActiveNav(btnConfigIndice);
        System.out.println("Navegar a: Configuración Índice");
    }

    @FXML
    private void onHistorial() {
        setActiveNav(btnHistorial);
        System.out.println("Navegar a: Historial");
    }

    @FXML
    private void onEstado() {
        setActiveNav(btnEstado);
        System.out.println("Navegar a: Estado");
    }

    private void setActiveNav(Button selected) {
        Button[] navButtons = {btnMotorBusqueda, btnConfigIndice, btnHistorial, btnEstado};
        for (Button btn : navButtons) {
            btn.getStyleClass().remove("nav-item-active");
        }
        if (!selected.getStyleClass().contains("nav-item-active")) {
            selected.getStyleClass().add("nav-item-active");
        }
    }

    @FXML
    private void onSearch() {
        performSearch(searchField.getText().trim());
    }

    @FXML
    private void onToggleFilters() {
        boolean isVisible = filterChipsBox.isVisible();
        filterChipsBox.setVisible(!isVisible);
        filterChipsBox.setManaged(!isVisible);
    }

    private void performSearch(String query) {
        resultsContainer.getChildren().clear();

        if (query.isEmpty() || searchService == null) {
            showEmptyState("Escribe algo para comenzar a buscar...");
            return;
        }

        CompletableFuture.supplyAsync(() -> searchService.realizarBusqueda(query))
                .thenAccept(resultados -> Platform.runLater(() -> {
                    if (resultados.isEmpty()) {
                        showEmptyState("No se encontraron coincidencias para '" + query + "'");
                    } else {
                        for (Archivo archivo : resultados) {
                            resultsContainer.getChildren().add(buildResultCard(archivo));
                        }
                    }
                }));
    }

    private void showEmptyState(String message) {
        resultsContainer.getChildren().clear();
        Label placeholder = new Label(message);
        placeholder.getStyleClass().add("file-meta");
        resultsContainer.getChildren().add(placeholder);
    }

    private VBox buildResultCard(Archivo archivo) {
        VBox card = new VBox();
        card.getStyleClass().add("result-card");

        HBox mainRow = new HBox(14);
        mainRow.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label("📄");
        if ("pdf".equalsIgnoreCase(archivo.getTipoArchivo())) {
            iconLabel.getStyleClass().add("file-icon-pdf");
        } else if ("docx".equalsIgnoreCase(archivo.getTipoArchivo())) {
            iconLabel.getStyleClass().add("file-icon-doc");
        } else {
            iconLabel.getStyleClass().add("file-icon-generic");
        }

        VBox infoBox = new VBox(3);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label nameLabel = new Label(archivo.getNombreArchivo());
        nameLabel.getStyleClass().add("file-name");

        Label pathLabel = new Label(archivo.getRutaArchivo());
        pathLabel.getStyleClass().add("file-path");

        HBox metaBox = new HBox(6);
        metaBox.setAlignment(Pos.CENTER_LEFT);

        String tamanoFormat = String.format("%.2f MB", archivo.getTamano() / (1024.0 * 1024.0));
        Label sizeLabel = new Label(tamanoFormat);
        sizeLabel.getStyleClass().add("file-meta");

        Label dotLabel = new Label("•");
        dotLabel.getStyleClass().add("file-meta-dot");

        String fechaFormat = dateFormat.format(new Date(archivo.getFechaModificacion()));
        Label dateLabel = new Label("Modificado: " + fechaFormat);
        dateLabel.getStyleClass().add("file-meta");

        metaBox.getChildren().addAll(sizeLabel, dotLabel, dateLabel);

        HBox tagsBox = new HBox(6);
        tagsBox.setAlignment(Pos.CENTER_LEFT);

        String tipo = archivo.getTipoArchivo() != null ? archivo.getTipoArchivo().toUpperCase() : "TXT";
        Label typeTag = new Label(tipo);
        typeTag.getStyleClass().addAll("tag", "tag-blue");
        tagsBox.getChildren().add(typeTag);

        infoBox.getChildren().addAll(nameLabel, pathLabel, metaBox, tagsBox);
        mainRow.getChildren().addAll(iconLabel, infoBox);
        card.getChildren().add(mainRow);

        card.setOnMouseClicked(event -> {
            System.out.println("Abriendo archivo: " + archivo.getRutaArchivo());
        });

        return card;
    }

    public void setCrawlerStatus(boolean activo, String mensaje) {
        Platform.runLater(() -> {
            crawlerStatusLabel.setText(mensaje);
            if (activo) {
                crawlerDot.getStyleClass().setAll("crawler-dot-active");
                crawlerStatusLabel.getStyleClass().setAll("crawler-active-text");
            } else {
                crawlerDot.getStyleClass().setAll("crawler-dot-inactive");
                crawlerStatusLabel.getStyleClass().setAll("label");
            }
        });
    }
}