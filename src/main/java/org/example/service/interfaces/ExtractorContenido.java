package org.example.service.interfaces;
import java.nio.file.Path;

public interface ExtractorContenido {
    String extraerTexto(Path ruta);
    String calcularHash(Path ruta);
}