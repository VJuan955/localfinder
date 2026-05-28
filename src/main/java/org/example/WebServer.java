package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.model.Archivo;
import org.example.service.CrawlerService;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class WebServer {
    private static final int PUERTO = 8080;
    private final CrawlerService crawlerService = new CrawlerService();
    private HttpServer server;

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PUERTO), 0);
        server.createContext("/", new StaticHandler());
        server.createContext("/api/rastrear", new RastrearHandler());
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        String url = "http://localhost:" + PUERTO + "/";
        System.out.println("Interfaz web disponible en: " + url);
        abrirNavegador(url);
    }

    private void abrirNavegador(String url) {
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.BROWSE)) {
            return;
        }
        try {
            desktop.browse(new URI(url));
        } catch (IOException | URISyntaxException ignored) {
        }
    }

    private class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                serveResource(exchange, "/web/index.html", "text/html; charset=utf-8");
                return;
            }

            if (path.endsWith(".css")) {
                serveResource(exchange, "/web" + path, "text/css; charset=utf-8");
                return;
            }

            if (path.endsWith(".js")) {
                serveResource(exchange, "/web" + path, "application/javascript; charset=utf-8");
                return;
            }

            exchange.sendResponseHeaders(404, -1);
        }

        private void serveResource(HttpExchange exchange, String resourcePath, String contentType) throws IOException {
            InputStream resource = WebServer.class.getResourceAsStream(resourcePath);
            if (resource == null) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int bytesRead;
            while ((bytesRead = resource.read(chunk)) != -1) {
                buffer.write(chunk, 0, bytesRead);
            }
            resource.close();
            byte[] bytes = buffer.toByteArray();
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private class RastrearHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String query = exchange.getRequestURI().getQuery();
            String ruta = obtenerParametro(query, "path");
            if (ruta == null || ruta.trim().isEmpty()) {
                enviarTexto(exchange, 400, "Parámetro 'path' es obligatorio.");
                return;
            }
            List<Archivo> archivos = crawlerService.rastrearDirectorio(ruta);
            String json = archivos.stream().map(WebServer::archivoToJson).collect(Collectors.joining(",", "[", "]"));
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            enviarTexto(exchange, 200, json);
        }

        private String obtenerParametro(String query, String clave) {
            if (query == null) {
                return null;
            }
            for (String param : query.split("&")) {
                String[] partes = param.split("=", 2);
                if (partes.length == 2 && partes[0].equals(clave)) {
                    try {
                        return java.net.URLDecoder.decode(partes[1], "UTF-8");
                    } catch (Exception e) {
                        return partes[1];
                    }
                }
            }
            return null;
        }

        private void enviarTexto(HttpExchange exchange, int status, String contenido) throws IOException {
            byte[] bytes = contenido.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private static String archivoToJson(Archivo archivo) {
        return "{" +
                "\"rutaArchivo\":\"" + escapeJson(archivo.getRutaArchivo()) + "\"," +
                "\"nombreArchivo\":\"" + escapeJson(archivo.getNombreArchivo()) + "\"," +
                "\"tipoArchivo\":\"" + escapeJson(archivo.getTipoArchivo()) + "\"," +
                "\"tamano\":" + archivo.getTamano() + "," +
                "\"fechaModificacion\":" + archivo.getFechaModificacion() +
                "}";
    }

    private static String escapeJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
