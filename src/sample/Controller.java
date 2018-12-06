package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import sample.weather.WeatherDisplay;
import sample.weather.WeatherThread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Controller {

    @FXML
    private Label temperatureLabel;

    @FXML
    private Button start;

    @FXML
    private Button checkBUtton;

    private WeatherThread weatherThread;
    private WeatherDisplay weatherDisplay;
    private BlockingQueue<WeatherDisplay> weatherDisplays;

    @FXML
    void startWeatherThread(ActionEvent event) {
        weatherThread = new WeatherThread();
        weatherDisplay = new WeatherDisplay("Koluszki");

        weatherThread.addObserver(weatherDisplay);
        weatherThread.start();
        
    }


    @FXML
    void checkAction(ActionEvent event) {

        temperatureLabel.setText(weatherDisplay.getWeather().toString());
        System.out.println(weatherDisplay.getWeather().getTemp());
    }


}
