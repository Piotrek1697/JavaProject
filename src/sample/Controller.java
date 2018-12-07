package sample;

import com.sun.javafx.event.EventQueue;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import sample.model.DisplayWeather;
import sample.weather.Weather;
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
        weatherDisplay = new WeatherDisplay("Warszawa");

        weatherThread.addObserver(weatherDisplay);
        weatherThread.start();

        updateLabel();
    }

    public void updateLabel() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                    temperatureLabel.textProperty().set(Context.getInstance().getCurrentWeather().toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @FXML
    void checkAction(ActionEvent event) {

        temperatureLabel.setText(weatherDisplay.getWeather().toString());
        System.out.println(Context.getInstance().getCurrentWeather());
    }


}
