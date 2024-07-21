package weatherApp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class mainApp extends Application {
    private Label weatherInfo;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Weather App");

        Label countryLabel = new Label("Enter country:");
        TextField countryInput = new TextField();

        Label stateLabel = new Label("Enter state (if USA):");
        TextField stateInput = new TextField();

        Label cityLabel = new Label("Enter city name for weather information:");
        TextField cityInput = new TextField();

        Button getWeatherButton = new Button("Get Weather");
        weatherInfo = new Label();

        getWeatherButton.setOnAction(e -> {
            String country = countryInput.getText();
            String state = stateInput.getText();
            String city = cityInput.getText();
            getWeatherData(city, country, state);
        });
        VBox vbox = new VBox(10, countryLabel, countryInput, stateLabel, stateInput, cityLabel, cityInput, getWeatherButton, weatherInfo);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void getWeatherData(String city, String country, String state){
        weatherApp weatherApp = new weatherApp();
        String weatherDetails = weatherApp.getWeatherData(city, country, state);
        weatherInfo.setText(weatherDetails);
    }
}
