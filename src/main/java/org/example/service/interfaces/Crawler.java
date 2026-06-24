package org.example.service.interfaces;
import java.util.List;
import org.example.model.Archivo;

public interface Crawler {
    List<Archivo> rastrearDirectorio(String ruta);
}