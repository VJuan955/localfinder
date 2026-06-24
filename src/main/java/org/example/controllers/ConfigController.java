package org.example.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;

import org.example.dao.DirectorioDAO;
import org.example.dao.impl.DirectorioDAOImpl;
import org.example.model.Directorio;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ConfigController implements Initializable {
    @FXML private Button btnMotorBusqueda;
    @FXML private Button btnConfigIndice;
    @FXML private Button btnHistorial;
    @FXML private Button btnEstado;
    @FXML private Circle crawlerDot;
    @FXML private Label  crawlerStatusLabel;
    @FXML private VBox folderListContainer;
    @FXML private Button btnAddFolder;
    @FXML private CheckBox chkPdf;
    @FXML private CheckBox chkTxt;
    @FXML private CheckBox chkDocx;
    @FXML private Button btnGuardar;

    private final List<String> indexedFolders = new ArrayList<>();
    private final DirectorioDAO directorioDAO = new DirectorioDAOImpl();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setCrawlerStatus(true);

        List<Directorio> carpetasGuardadas = directorioDAO.obtenerTodos();
        for (Directorio dir : carpetasGuardadas) {
            indexedFolders.add(dir.getRutaDirectorio());
        }

        refreshFolderList();
    }

    @FXML private void onMotorBusqueda() {
        setActiveNav(btnMotorBusqueda);
        System.out.println("Navegar a: Motor de Búsqueda");
    }

    @FXML private void onConfigIndice() {
        setActiveNav(btnConfigIndice);
    }

    @FXML private void onHistorial() {
        setActiveNav(btnHistorial);
        System.out.println("Navegar a: Historial");
    }

    @FXML private void onEstado() {
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
    private void onAddFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Seleccionar carpeta para indexar");

        File chosen = chooser.showDialog(btnAddFolder.getScene().getWindow());
        if (chosen != null) {
            String path = chosen.getAbsolutePath();
            if (!indexedFolders.contains(path)) {
                indexedFolders.add(path);
                refreshFolderList();
            }
        }
    }

    @FXML
    private void onDeleteFolder(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        String path = (String) btn.getUserData();
        indexedFolders.remove(path);
        refreshFolderList();
    }

    private void refreshFolderList() {
        folderListContainer.getChildren().clear();
        for (String path : indexedFolders) {
            HBox row = buildFolderRow(path);
            folderListContainer.getChildren().add(row);
        }
    }

    private HBox buildFolderRow(String path) {
        Label icon = new Label("📁");
        icon.getStyleClass().add("folder-icon");

        Label label = new Label(path);
        label.getStyleClass().add("folder-path");
        HBox.setHgrow(label, Priority.ALWAYS);

        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().add("delete-btn");
        deleteBtn.setUserData(path);
        deleteBtn.setOnAction(this::onDeleteFolder);

        HBox row = new HBox(10, icon, label, deleteBtn);
        row.getStyleClass().add("folder-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return row;
    }

    @FXML
    private void onSave() {
        List<Directorio> dbDirs = directorioDAO.obtenerTodos();
        List<String> dbPaths = dbDirs.stream()
                .map(Directorio::getRutaDirectorio)
                .collect(Collectors.toList());

        for (Directorio dbDir : dbDirs) {
            if (!indexedFolders.contains(dbDir.getRutaDirectorio())) {
                directorioDAO.eliminar(dbDir.getIdDirectorio());
            }
        }

        for (String uiPath : indexedFolders) {
            if (!dbPaths.contains(uiPath)) {
                Directorio nuevoDir = new Directorio();
                nuevoDir.setRutaDirectorio(uiPath);
                nuevoDir.setEstado("activo");
                nuevoDir.setFechaRegistro(System.currentTimeMillis());
                directorioDAO.insertar(nuevoDir);
            }
        }

        List<String> selectedTypes = new ArrayList<>();
        if (chkPdf.isSelected())  selectedTypes.add(".pdf");
        if (chkTxt.isSelected())  selectedTypes.add(".txt");
        if (chkDocx.isSelected()) selectedTypes.add(".docx");

        System.out.println("Configuración guardada exitosamente en SQLite.");

        btnGuardar.setText("Guardado");
        btnGuardar.setDisable(true);

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
        pause.setOnFinished(e -> {
            btnGuardar.setText("Guardar Cambios");
            btnGuardar.setDisable(false);
        });
        pause.play();
    }

    public void setCrawlerStatus(boolean activo) {
        if (activo) {
            crawlerDot.getStyleClass().setAll("crawler-dot-active");
            crawlerStatusLabel.setText("Activo");
            crawlerStatusLabel.getStyleClass().setAll("crawler-active-text");
        } else {
            crawlerDot.getStyleClass().setAll("crawler-dot-inactive");
            crawlerStatusLabel.setText("Inactivo");
            crawlerStatusLabel.getStyleClass().setAll("label");
        }
    }
    
    public List<String> getIndexedFolders() {
        return new ArrayList<>(indexedFolders);
    }
}
