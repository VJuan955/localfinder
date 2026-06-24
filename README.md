# LocalFinder

LocalFinder es un motor de búsqueda interno offline diseñado para indexar, organizar y recuperar información almacenada localmente de manera rápida, eficiente y segura.

Este proyecto nace para resolver las deficiencias de los buscadores nativos del sistema operativo (alta latencia, baja precisión y alto consumo de recursos), ofreciendo una herramienta enfocada en la productividad de estudiantes, investigadores y desarrolladores.

---

## Características Principales

* **Búsqueda por Contenido Profundo:** Indexación y recuperación de texto dentro de archivos PDF, DOCX y TXT.
* **Privacidad Total (Offline):** Funcionamiento completamente autónomo sin dependencia de servicios externos ni telemetría; ningún dato sale de tu equipo.
* **Velocidad Extrema:** Arquitectura optimizada para mantener tiempos de respuesta inferiores a 2 segundos en grandes volúmenes de documentos.
* **Indexación Selectiva:** Tú decides qué directorios se escanean, evitando el indexado innecesario de todo el disco duro.
* **Multiplataforma:** Compatible de forma nativa con entornos Windows y Linux.

---

## Stack Tecnológico

El sistema está construido bajo una arquitectura modular de 4 capas lógicas (Presentación, Negocio, Datos y Persistencia) empleando el patrón MVC.

* **Lenguaje:** Java 21 LTS
* **Interfaz Gráfica:** JavaFX (UI moderna sin necesidad de servidor local)
* **Motor de Búsqueda:** Apache Lucene (Índice invertido y algoritmo TF-IDF)
* **Extracción de Texto:** Apache Tika (Soporte uniforme para múltiples formatos)
* **Base de Datos:** SQLite (Embebida, con patrón DAO y JDBC)
* **Gestor de Dependencias:** Maven

---

## Instalación y Compilación (Desarrollo)

Asegúrate de tener instalado **JDK 21** y **Maven** en tu entorno local.

1. Clona este repositorio.
2. Navega al directorio raíz del proyecto donde se encuentra el `pom.xml`.
3. Compila el proyecto y descarga las dependencias ejecutando:
   ```bash
   mvn clean install