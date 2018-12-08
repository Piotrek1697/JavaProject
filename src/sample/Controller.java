package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sample.weather.Weather;
import sample.weather.WeatherDisplay;
import sample.weather.WeatherThread;

public class Controller {

    @FXML
    private Label temperatureLabel;


    private WeatherThread weatherThread;
    private WeatherDisplay weatherDisplay;
    private ObservableList<Weather> weatherList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        weatherList.addListener((ListChangeListener<Weather>) c -> {
            c.next();
            if (c.wasAdded()){
                updateLabel();
            }
        });
    }
    
    @FXML
    void startWeatherThread(ActionEvent event) {

        weatherThread = new WeatherThread();
        weatherDisplay = new WeatherDisplay("New York",weatherList);

        weatherThread.addObserver(weatherDisplay);
        weatherThread.start();
    }

    public void updateLabel(){
        Platform.runLater(() -> {
            Weather weather = weatherList.get(weatherList.size() - 1);
            System.out.println(weather);
            temperatureLabel.textProperty().set(weather.toString());
        });

    }




}
