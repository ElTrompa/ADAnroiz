package org.example.fichajes.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

public class WeatherService {
    // API gratuita de Open-Meteo (no requiere API key)
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast";

    // Coordenadas por defecto (Alzira, Valencia, España)
    private static final double DEFAULT_LATITUDE = 39.1508;
    private static final double DEFAULT_LONGITUDE = -0.4365;

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static class WeatherData {
        private double temperature;
        private String description;

        public WeatherData(double temperature, String description) {
            this.temperature = temperature;
            this.description = description;
        }

        public double getTemperature() {
            return temperature;
        }

        public String getDescription() {
            return description;
        }
    }

    public static WeatherData getCurrentWeather() {
        return getCurrentWeather(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
    }

    public static WeatherData getCurrentWeather(double latitude, double longitude) {
        try {
            String url = String.format(Locale.US, "%s?latitude=%.4f&longitude=%.4f&current=temperature_2m,weather_code&timezone=auto",
                    API_URL, latitude, longitude);

            System.out.println("Consultando clima: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();
            
            System.out.println("Respuesta clima: " + jsonResponse);
            
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

            if (jsonObject.has("current")) {
                JsonObject current = jsonObject.getAsJsonObject("current");
                double temperature = current.get("temperature_2m").getAsDouble();
                int weatherCode = current.get("weather_code").getAsInt();
                String description = getWeatherDescription(weatherCode);

                return new WeatherData(temperature, description);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener datos del clima: " + e.getMessage());
            e.printStackTrace();
        }

        // Valores por defecto si falla la API
        return new WeatherData(20.0, "No disponible");
    }

    private static String getWeatherDescription(int code) {
        return switch (code) {
            case 0 -> "Despejado";
            case 1, 2, 3 -> "Parcialmente nublado";
            case 45, 48 -> "Niebla";
            case 51, 53, 55 -> "Llovizna";
            case 61, 63, 65 -> "Lluvia";
            case 71, 73, 75 -> "Nieve";
            case 77 -> "Granizo";
            case 80, 81, 82 -> "Chubascos";
            case 85, 86 -> "Chubascos de nieve";
            case 95 -> "Tormenta";
            case 96, 99 -> "Tormenta con granizo";
            default -> "Nublado";
        };
    }
}

