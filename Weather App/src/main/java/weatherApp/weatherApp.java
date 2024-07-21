package weatherApp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;



//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class weatherApp {
    private static final Logger logger = LoggerFactory.getLogger(weatherApp.class);

    public String getWeatherData (String city, String country, String state) {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            // Load properties file
            prop.load(input);
            String apiKey = prop.getProperty("weatherApiKey");

            // Build the location query
            String locationQuery = city;
            if ("USA".equalsIgnoreCase(country) && state != null && !state.isEmpty()) {
                locationQuery = city + "," + state + "," + country;
            } else {
                locationQuery = city + "," + country;
            }

            // URL-encode the location query
            String encodedLocationQuery = URLEncoder.encode(locationQuery, StandardCharsets.UTF_8);

            // Build the URL for the API request
            String url = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + encodedLocationQuery + "&aqi=no";

            // Create an HTTP client
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(url);

                // Execute the request and handle the response
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    int statusCode =  response.getCode();
                    if (statusCode == 200) {
                        String responseBody = EntityUtils.toString(response.getEntity());
                        logger.info("Response: {}", responseBody);
                        return parseWeather(responseBody);
                    } else {
                        logger.error("Error: Failed to retrieve the weather data. Status code: {}", statusCode);
                        return "Error: Failed to retrieve the weather data.";
                    }
                }
            }
        } catch (IOException | ParseException ex) {
            logger.error("Error: Unable to complete the request", ex);
            return "Error: Unable to complete the request.";
        }
    }
    private String parseWeather(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody);
            String location = rootNode.path("location").path("name").asText();
            double temperature = rootNode.path("current").path("temp_f").asDouble();
            String condition = rootNode.path("current").path("condition").path("text").asText();
            double humidity = rootNode.path("current").path("humidity").asDouble();
            double windSpeed = rootNode.path("current").path("wind_mph").asDouble();

            return String.format("Weather in %s: %.1f°F, %s, Humidity: %.1f%%, Wind Speed: %.1f mph",
                    location, temperature, condition, humidity, windSpeed);
        } catch (IOException ex) {
            logger.error("Error parsing the weather data", ex);
            return "Error parsing the weather data.";
        }
    }
}