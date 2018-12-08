package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sample.weather.Weather;
import sample.weather.WeatherDisplay;
import sample.weather.WeatherThread;

import java.io.FileNotFoundException;

public class Controller {

    @FXML
    private Button start;

    @FXML
    private TextField cityTextField;


    @FXML
    private Label temperatureLabel;


    private final String CELSIUS = "\u00b0C";
    private WeatherThread weatherThread;
    private WeatherDisplay weatherDisplay;
    private ObservableList<Weather> weatherList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        weatherList.addListener((ListChangeListener<Weather>) c -> {
            c.next();
            if (c.wasAdded()) {
                updateLabel();
            }
        });
    }

    @FXML
    void startWeatherThread(ActionEvent event) {
        weatherList.clear();
        weatherThread = new WeatherThread();

        String city = cityTextField.textProperty().getValue();
        System.out.println(city);

        weatherDisplay = new WeatherDisplay(city, weatherList);

        weatherThread.addObserver(weatherDisplay);

        weatherThread.start();


    }

    public void updateLabel() {
        Platform.runLater(() -> {
            Weather weather = weatherList.get(weatherList.size() - 1);
            System.out.println(weather);
            temperatureLabel.setStyle("-fx-background-color:  #bcbbba; -fx-background-radius: 10;");
            temperatureLabel.textProperty().set("Temperature: " + String.valueOf(weather.getTemp()) + CELSIUS);
        });

    }


}
