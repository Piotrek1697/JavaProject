package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import sample.time.TimeThread;
import sample.weather.Weather;
import sample.weather.WeatherDisplay;
import sample.weather.WeatherThread;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Controller {

    @FXML
    private Button startButton;
    @FXML
    private TextField cityTextField;
    @FXML
    private Button stopButton;
    @FXML
    private Label temperatureLabel;
    @FXML
    private Button pauseButton;
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private Label localTimeLabel;
    @FXML
    private LineChart<String, Number> weatherChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;


    private final String CELSIUS = "\u00b0C";
    private WeatherThread weatherThread;
    private WeatherDisplay weatherDisplay;
    private ObservableList<Weather> weatherList = FXCollections.observableArrayList();
    private ArrayList<String> timeList = new ArrayList<>();
    private ObservableList<LocalTime> localTimeListThread = FXCollections.observableArrayList();
    private TimeThread timeThread;
    private XYChart.Series<String, Number> weatherSeries = new XYChart.Series<>();

    @FXML
    public void initialize() {
        weatherList.addListener((ListChangeListener<Weather>) c -> {
            c.next();
            if (c.wasAdded()) {
                updateWeatherLabel();
            }
        });

        localTimeListThread.addListener((ListChangeListener<LocalTime>) c -> {
            c.next();
            if (c.wasAdded()) {
                updateTimeLabel();
            }
        });
        timeThread = new TimeThread(localTimeListThread);
        timeThread.start();

        initializeChart();
    }

    private void initializeChart() {
        weatherSeries.setName("Temperature");
        xAxis.setLabel("Time (HH:mm)");
        yAxis.setLabel("Temp [" + CELSIUS + "]");
        weatherChart.setAnimated(false);

        String fontStyle = "-fx-font-weight: bold;-fx-font-size: 11";
        xAxis.setStyle(fontStyle);
        yAxis.setStyle(fontStyle);
    }

    private void updateWeatherChart() {
        yAxis.setTickUnit(1);

        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);


        for (int i = 0; i < weatherList.size(); i++) {
            weatherSeries.getData().add(new XYChart.Data<>(timeList.get(i), weatherList.get(i).getTemp()));
        }

        weatherChart.getData().addAll(weatherSeries);

    }

    @FXML
    void startWeatherThread(ActionEvent event) {
        weatherList.clear();
        weatherThread = new WeatherThread();

        String city = cityTextField.textProperty().getValue();
        System.out.println(city);

        weatherDisplay = new WeatherDisplay(city, weatherList, timeList);
        weatherThread.addObserver(weatherDisplay);
        weatherThread.start();
    }

    @FXML
    void stopWeatherThread(ActionEvent event) {
        try {
            weatherThread.stop();
            System.out.println("Thread stopped");
            if (!weatherThread.isRunning())
                threadError("There is no weather thread started",
                        "Firstly start weather thread");
        } catch (NullPointerException ex) {
            threadError("There is no weather thread started",
                    "Firstly start weather thread");
        }
    }

    @FXML
    void pauseWeatherThread(ActionEvent event) {
        try {
            weatherThread.interrupt();
            System.out.println("Thread paused");
        } catch (NullPointerException ex) {
            threadError("There is no weather thread started",
                    "Firstly start weather thread");
        }
    }

    @FXML
    void fileOpen(ActionEvent event) {
        System.out.println("File opended");
    }

    public void updateWeatherLabel() {
        Platform.runLater(() -> {
            Weather weather = weatherList.get(weatherList.size() - 1);
            System.out.println(weather);
            temperatureLabel.setStyle("-fx-background-color:  #bcbbba; -fx-background-radius: 10;");
            temperatureLabel.textProperty().set("Temperature: " + String.valueOf(weather.getTemp()) + CELSIUS);
            System.out.println(timeList.get(timeList.size() - 1));
            updateWeatherChart();
        });

    }

    public void updateTimeLabel() {
        Platform.runLater(() -> {
            String time = localTimeListThread.get(localTimeListThread.size() - 1).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            localTimeLabel.textProperty().set("Local time: "+time);

        });
    }

    public static void threadError(String headerText, String contentText) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);

            alert.showAndWait();
        });
    }


}
