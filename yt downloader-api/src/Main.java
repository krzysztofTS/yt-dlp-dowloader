import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api", exchange -> {

            // Pobranie danych JSON z requestu

            String json = new String(exchange.getRequestBody().readAllBytes());

            HashMap<String, String> dane = new HashMap<>();

            // usuwamy nawiasy i dzielimy po przecinkach
            json = json.replace("{", "")
                    .replace("}", "")
                    .replace("\"", "");

            String[] pola = json.split(",");

            for (String pole : pola) {
                String[] wartosc = pole.split(":");
                dane.put(wartosc[0].trim(), wartosc[1].trim());
            }

            // odczyt
            boolean wideo = Boolean.parseBoolean(dane.get("wideo"));
            boolean playlist = Boolean.parseBoolean(dane.get("playlist"));
            String url = dane.get("url");

            try {
                List<String> command = new ArrayList<>();
                command.add("yt-dlp.exe");
                command.add("-f");
                command.add("best");
                if (!wideo) {
                    command.add("-x");
                    command.add("--audio-format");
                    command.add("mp3");
                };
                if (playlist){
                    command.add("--yes-playlist");
                };
                command.add(url);
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.redirectErrorStream(true);

                Process process = pb.start();

                process.waitFor();

                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                String response = "Download complete";
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);

                OutputStream output = exchange.getResponseBody();
                output.write(response.getBytes());
                output.write(responseBytes);
                output.close();

            } catch (Exception e) {

                String error = e.getMessage() != null ? e.getMessage() : e.toString();

                byte[] responseBytes = error.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);

                OutputStream output = exchange.getResponseBody();
                output.write(error.getBytes());
                output.write(responseBytes);
                output.close();
            }

        });
        
        server.start();

    }
}