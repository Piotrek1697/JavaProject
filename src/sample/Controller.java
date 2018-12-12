package sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import sample.data.JsonParser;
import sample.time.TimeThread;
import sample.weather.Weather;
import sample.weather.WeatherDisplay;
import sample.weather.WeatherThread;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private Menu menuFile;
    @FXML
    private Label localTimeLabel;
    @FXML
    private LineChart<String, Number> weatherChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private ToggleButton celsiusToggle;
    @FXML
    private ToggleGroup temperatureToggle;
    @FXML
    private ToggleButton fahrenheitToggle;
    @FXML
    private ProgressIndicator huminidityPercents;
    @FXML
    private TextArea tempArea;
    @FXML
    private ProgressBar threadProgressBar;
    @FXML
    private ProgressIndicator threadProgressIndicator;
    @FXML
    private ToggleButton chartTypeTemperature;

    @FXML
    private ToggleGroup chartTypeToggle;

    @FXML
    private ToggleButton chartTypeHumidity;

    @FXML
    private ToggleButton chartTypePressure;



    private final String CELSIUS = "\u00b0C";
    private final String FAHRENHEIT = "\u00b0F";
    private WeatherThread weatherThread;
    private WeatherDisplay weatherDisplay;
    private ObservableList<Weather> weatherList = FXCollections.observableArrayList();
    private ArrayList<String> timeList = new ArrayList<>();
    private ObservableList<LocalTime> localTimeListThread = FXCollections.observableArrayList();
    private TimeThread timeThread;
    private XYChart.Series<String, Number> weatherSeries = new XYChart.Series<>();
    private String units = "metric";
    private String unitsLabel = CELSIUS;
    private String chartType = "temperature";
    private String yLabelString = "Temp [" + unitsLabel + "]";

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

                if (weatherThread != null) {
                    if (weatherThread.isUpdatedObserver()) {
                        updateThreadProgress();
                    } else {
                        Platform.runLater(() -> {
                            threadProgressBar.setProgress(0);
                            threadProgressIndicator.setProgress(0);
                        });
                    }
                }

                updateTimeLabel();
            }
        });
        timeThread = new TimeThread(localTimeListThread);
        timeThread.start();

        initializeChart();
        initializeButtons();
    }

    private void initializeButtons() {
        Image startImage = new Image(getClass().getResourceAsStream("/images/startIcon.png"));
        startButton.setGraphic(new ImageView(startImage));

        Image stopImage = new Image(getClass().getResourceAsStream("/images/stopIcon.png"));
        stopButton.setGraphic(new ImageView(stopImage));

        Image fileImage = new Image(getClass().getResourceAsStream("/images/fileIcon.png"));
        menuFile.setGraphic(new ImageView(fileImage));

        chartTypeTemperature.setSelected(true);

    }

    private void initializeChart() {
        updateChartLabels();
        weatherChart.setAnimated(false);

        String fontStyle = "-fx-font-weight: bold;-fx-font-size: 11";
        xAxis.setStyle(fontStyle);
        yAxis.setStyle(fontStyle);
        weatherChart.getData().add(weatherSeries);
        weatherChart.setLegendVisible(false);

    }

    private void updateChartLabels() {
        xAxis.setLabel("Time (HH:mm)");
        if (unitsLabel.equals(FAHRENHEIT) && chartType.equals("temperature"))
            yLabelString = "Temp [" + unitsLabel + "]";

        yAxis.setLabel(yLabelString);
    }

    private void updateWeatherChart() {
        updateChartLabels();
        weatherSeries.getData().clear();
        updateChartLabels();
        yAxis.setTickUnit(1);

        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);

        weatherSeries = getChartData(chartType);

    }

    public XYChart.Series<String, Number> getChartData(String chartType) {
        XYChart.Series<String, Number> series = weatherSeries;

        if ("humidity".equals(chartType)) {
            for (int i = 0; i < weatherList.size(); i++)
                series.getData().add(new XYChart.Data<>(timeList.get(i), weatherList.get(i).getHumidity()));

        } else if ("pressure".equals(chartType)) {
            for (int i = 0; i < weatherList.size(); i++)
                series.getData().add(new XYChart.Data<>(timeList.get(i), weatherList.get(i).getPressure()));

        } else if ("temperature".equals(chartType)) {
            for (int i = 0; i < weatherList.size(); i++)
                weatherSeries.getData().add(new XYChart.Data<>(timeList.get(i), weatherList.get(i).getTemp()));

        }
        return series;
    }

    @FXML
    void startWeatherThread(ActionEvent event) {
        weatherList.clear();
        timeList.clear();

        celsiusToggle.setDisable(true);
        fahrenheitToggle.setDisable(true);
        if (weatherThread != null)
            weatherThread.stop();

        weatherThread = new WeatherThread();
        weatherSeries.getData().clear();

        String city = cityTextField.textProperty().getValue();
        System.out.println(city);

        weatherDisplay = new WeatherDisplay(city, units, weatherList, timeList);
        weatherThread.addObserver(weatherDisplay);
        weatherThread.start();
    }

    @FXML
    void stopWeatherThread(ActionEvent event) {
        try {
            if (!weatherThread.isRunning())
                threadError("There is no weather thread started",
                        "Firstly start weather thread");
            weatherThread.stop();
            System.out.println("Thread stopped");
            weatherThread.setUpdatedObserver(false);

            celsiusToggle.setDisable(false);
            fahrenheitToggle.setDisable(false);

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
    void resumeWeatherThread(ActionEvent event) {
        try {
            weatherThread.resume();
        } catch (NullPointerException ex) {
            threadError("There is no weather thread paused",
                    "You have to pause thread, then resume it");
        }
    }

    @FXML
    void fileOpen(ActionEvent event) {

        if (weatherThread != null)
            weatherThread.stop();

        ArrayList<Weather> weatherArray;

        Map map = null;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open weather data");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));

        File file = fileChooser.showOpenDialog(localTimeLabel.getScene().getWindow());
        if (file != null) {
            try {
                map = JsonParser.getMap(file);
            } catch (IOException e) {
                threadError("Can't reach json", "Check your file path");
            }

            weatherArray = gson.fromJson(map.get("Weather").toString(), new TypeToken<ArrayList<Weather>>() {
            }.getType());

            String[] time = gson.fromJson(map.get("Time").toString(), String[].class);
            String[] city = gson.fromJson(map.get("City").toString(), String[].class);

            weatherList.clear();
            weatherList.addAll(weatherArray);

            timeList.clear();
            timeList.addAll(Arrays.asList(time));

            cityTextField.setText(city[0]);
            System.out.println("File opended");
        }
    }

    @FXML
    void fileSave(ActionEvent event) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save weather data");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));

        File jsonFile = fileChooser.showSaveDialog(localTimeLabel.getScene().getWindow());

        Map<String, ArrayList<Object>> map = new HashMap<>();
        map.put("Weather", new ArrayList<>(weatherList));
        map.put("Time", new ArrayList<>(timeList));
        map.put("City", new ArrayList<>(Collections.singleton(cityTextField.getText())));

        if (jsonFile != null) {
            try (FileWriter fileWriter = new FileWriter(jsonFile)) {
                gson.toJson(map, fileWriter);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    private void updateWeatherLabel() {
        Platform.runLater(() -> {
            Weather weather = weatherList.get(weatherList.size() - 1);
            System.out.println(weather);

            temperatureLabel.textProperty().set("Temperature: " + String.valueOf(weather.getTemp()) + unitsLabel);
            tempArea.textProperty().set("Temp min: " + String.valueOf(weather.getTemp_min()) + unitsLabel + "\n" + "Temp min: " + String.valueOf(weather.getTemp_max() + unitsLabel));
            System.out.println(timeList.get(timeList.size() - 1));
            updateWeatherChart();

            threadProgressBar.setProgress(0);
            threadProgressIndicator.setProgress(0);
        });
    }

    private void updateTimeLabel() {
        Platform.runLater(() -> {
            String time = localTimeListThread.get(localTimeListThread.size() - 1).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            localTimeLabel.textProperty().set("Local time: " + time);

        });
    }

    private void updateThreadProgress() {
        Platform.runLater(() -> {
            double progress = threadProgressBar.getProgress();
            threadProgressBar.setProgress(progress + 5f / 300f);
            threadProgressIndicator.setProgress(progress + 5f / 300f);

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

    @FXML
    void toCelsius(ActionEvent event) {
        units = "metric";
        unitsLabel = CELSIUS;
    }

    @FXML
    void toFahrenheit(ActionEvent event) {
        units = "imperial";
        unitsLabel = FAHRENHEIT;
    }

    @FXML
    void changeChartTypeHum(ActionEvent event) {
        chartType = "humidity";
        yLabelString = "Humidity [%]";
        updateWeatherChart();
    }

    @FXML
    void changeChartTypePressure(ActionEvent event) {
        chartType = "pressure";
        yLabelString = "Pressure [hPa]";
        updateWeatherChart();
    }

    @FXML
    void changeChartTypeTemp(ActionEvent event) {
        chartType = "temperature";
        yLabelString = "Temp [" + unitsLabel + "]";
        updateWeatherChart();
    }

}
