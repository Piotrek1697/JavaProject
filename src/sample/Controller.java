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
import java.text.DecimalFormat;
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
    private Button pauseButton;
    @FXML
    private Button resumeButton;
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
    @FXML
    private TextArea weatherArea;
    @FXML
    private Label meanTempLabel;
    @FXML
    private Label meanPressureLabel;
    @FXML
    private Label stdTempLabel;
    @FXML
    private Label stdPressureLabel;
    @FXML
    private Label measureQuantityLabel;
    @FXML
    private TextField freqField;


    private final String CELSIUS = "\u00b0C";
    private final String FAHRENHEIT = "\u00b0F";

    private WeatherThread weatherThread;
    private WeatherDisplay weatherDisplay;
    private TimeThread timeThread;

    private ObservableList<Weather> weatherList = FXCollections.observableArrayList();
    private ArrayList<String> timeList = new ArrayList<>();
    private ObservableList<LocalTime> localTimeListThread = FXCollections.observableArrayList();

    private XYChart.Series<String, Number> weatherSeries = new XYChart.Series<>();

    private String units = "metric";
    private String unitsLabel = CELSIUS;
    private String chartType = "temperature";
    private String yLabelString = "Temp [" + unitsLabel + "]";

    private File propFile;
    private Properties settings;

    /**
     * Initialize app's components (Buttons, chart, labels).
     * Set default values to textFields, set images to buttons.
     */
    @FXML
    public void initialize() {
        weatherList.addListener((ListChangeListener<Weather>) c -> {
            c.next();
            if (c.wasAdded()) {
                updateWeatherLabels();
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
        setDefaultProperties();
    }

    /**
     *Method to loading properties file.
     * If there is no program properties on computer, program creates new properties,
     * if there is some properties file, program load it and put to textFields.
     */
    private void setDefaultProperties(){

        String userDir = System.getProperty("user.home");
        File propertiesDir = new File(userDir, ".corejava");
        if (!propertiesDir.exists())
            propertiesDir.mkdir();

        propFile = new File(propertiesDir, "program.properties");

        Properties defaultSettings = new Properties();
        defaultSettings.put("City","Wroclaw");
        defaultSettings.put("Freq","1");

        settings = new Properties(defaultSettings);

        if (propFile.exists()){
            try(InputStream inputStream = new FileInputStream(propFile)) {
                settings.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        cityTextField.textProperty().set(settings.getProperty("City"));
        freqField.textProperty().set(settings.getProperty("Freq"));

    }

    /**
     * Put images into buttons
     */
    private void initializeButtons() {
        Image startImage = new Image(getClass().getResourceAsStream("/images/startIcon.png"));
        startButton.setGraphic(new ImageView(startImage));

        Image stopImage = new Image(getClass().getResourceAsStream("/images/stopIcon.png"));
        stopButton.setGraphic(new ImageView(stopImage));

        Image pauseImage = new Image(getClass().getResourceAsStream("/images/pauseIcon.png"));
        pauseButton.setGraphic(new ImageView(pauseImage));

        Image resumeImage = new Image(getClass().getResourceAsStream("/images/resumeIcon.png"));
        resumeButton.setGraphic(new ImageView(resumeImage));

        Image fileImage = new Image(getClass().getResourceAsStream("/images/fileIcon.png"));
        menuFile.setGraphic(new ImageView(fileImage));

        chartTypeTemperature.setSelected(true);
    }

    /**
     * Initialize basic chart settings
     */
    private void initializeChart() {
        updateChartLabels();
        weatherChart.setAnimated(false);

        String fontStyle = "-fx-font-weight: bold;-fx-font-size: 11";
        xAxis.setStyle(fontStyle);
        yAxis.setStyle(fontStyle);
        weatherChart.getData().add(weatherSeries);
        weatherChart.setLegendVisible(false);

    }

    /**
     * Set specific chart labels based on temperature units
     */
    private void updateChartLabels() {
        xAxis.setLabel("Time (HH:mm)");
        if (unitsLabel.equals(FAHRENHEIT) && chartType.equals("temperature"))
            yLabelString = "Temp [" + unitsLabel + "]";
        else if (unitsLabel.equals(CELSIUS) && chartType.equals("temperature"))
            yLabelString = "Temp [" + unitsLabel + "]";

        yAxis.setLabel(yLabelString);
    }

    /**
     * Loading newest data from list and visualize it on chart.
     */
    private void updateWeatherChart() {
        updateChartLabels();
        weatherSeries.getData().clear();
        updateChartLabels();
        yAxis.setTickUnit(1);

        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);

        weatherSeries = getChartData(chartType);

    }

    /**
     *
     * @param chartType
     * @return XY series with updated data
     */
    private XYChart.Series<String, Number> getChartData(String chartType) {
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

    /**
     * Method to start weather thread.
     * Clears all weather, time list and chart data.
     * @param event
     */
    @FXML
    void startWeatherThread(ActionEvent event) {
        weatherList.clear();
        timeList.clear();

        celsiusToggle.setDisable(true);
        fahrenheitToggle.setDisable(true);
        if (weatherThread != null)
            weatherThread.stop();

        int intervalThread = 0;
        System.out.println(freqField.getText());
        try {
            intervalThread = Integer.parseInt(freqField.getText());

            if (intervalThread == 0)
                weatherThread = new WeatherThread();
            else if(intervalThread >= 1)
                weatherThread = new WeatherThread(intervalThread * 60000);
            else
                threadError("Frequency hast to be greater than 1 minute","");

        }catch (NumberFormatException ex){
            threadError("Number in frequency field must be integer","");
        }


        weatherSeries.getData().clear();

        String city = cityTextField.textProperty().getValue();
        System.out.println(city);

        weatherDisplay = new WeatherDisplay(city, units, weatherList, timeList);

        if (weatherThread != null) {
            weatherThread.addObserver(weatherDisplay);
            weatherThread.start();
        }
    }

    /**
     * Stops weather thread
     * @param event
     */
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

    /**
     * Temporary pused weather thread
     * @param event
     */
    @FXML
    void pauseWeatherThread(ActionEvent event) {
        try {
            weatherThread.pause();
            System.out.println("Thread paused");
        } catch (NullPointerException ex) {
            threadError("There is no weather thread started",
                    "Firstly start weather thread");
        }
    }

    /**
     * Start weather thread again
     * @param event
     */
    @FXML
    void resumeWeatherThread(ActionEvent event) {
        try {
            weatherThread.resume();
        } catch (NullPointerException ex) {
            threadError("There is no weather thread paused",
                    "You have to pause thread, then resume it");
        }
    }

    /**
     * Method to handle open MenuItem.
     * Open FileChooser window, to select json data file.
     * Data is transfer to data lists (weatherList and timeList).
     * @param event
     */
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

    /**
     * Method to handle save MenuItem.
     * Open FileChooser window, to choose folder where json file will be saved.
     * @param event
     */
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

    /**
     * Put updated data to specific labels and textArea.
     */
    private void updateWeatherLabels() {
        Platform.runLater(() -> {
            Weather weather = weatherList.get(weatherList.size() - 1);
            System.out.println(weather);

            weatherArea.textProperty().set("Weather:\n"
                    + String.valueOf(weather.getTemp()) + unitsLabel + "\n"
                    + String.valueOf(weather.getPressure() + "hPa") + "\n"
                    + String.valueOf(weather.getHumidity() + "%" + " - Humidity"));

            tempArea.textProperty().set("Temp min: " + String.valueOf(weather.getTemp_min()) + unitsLabel + "\n"
                    + "Temp max: " + String.valueOf(weather.getTemp_max() + unitsLabel));

            System.out.println(timeList.get(timeList.size() - 1));
            updateWeatherChart();

            threadProgressBar.setProgress(0);
            threadProgressIndicator.setProgress(0);

            updateWeatherStatistics();
        });
    }

    /**
     * Update statistics about temperature and pressure in city
     */
    private void updateWeatherStatistics() {

        DecimalFormat decimalFormat = new DecimalFormat("###.##");
        double meanTemp = 0;
        double meanPressure = 0;

        double stdTemp = 0;
        double stdPressure = 0;
        for (Weather w : weatherList) {
            meanTemp += w.getTemp();
            meanPressure += w.getPressure();
        }
        meanTemp /= weatherList.size();
        meanPressure /= weatherList.size();

        for (Weather w : weatherList){
            stdTemp += Math.pow(w.getTemp() - meanTemp,2);
            stdPressure += Math.pow(w.getPressure() - meanPressure,2);
        }

        stdTemp = Math.sqrt(stdTemp/weatherList.size());
        stdPressure = Math.sqrt(stdPressure/weatherList.size());

        meanTempLabel.textProperty().set(decimalFormat.format(meanTemp) + unitsLabel);
        meanPressureLabel.textProperty().set(decimalFormat.format(meanPressure) + "hPa");

        stdTempLabel.textProperty().set(decimalFormat.format(stdTemp) + unitsLabel);
        stdPressureLabel.textProperty().set(decimalFormat.format(stdPressure) + "hPa");

        measureQuantityLabel.textProperty().set(String.valueOf(weatherList.size()));


    }

    /**
     * Update time label every second.
     */
    private void updateTimeLabel() {
        Platform.runLater(() -> {
            String time = localTimeListThread.get(localTimeListThread.size() - 1).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            localTimeLabel.textProperty().set("Local time: " + time);

        });
    }

    /**
     * Refresh weatherThread progress bar and progress indicator.
     */
    private void updateThreadProgress() {
        Platform.runLater(() -> {
            double progress = threadProgressBar.getProgress();
            threadProgressBar.setProgress(progress + 1000f/weatherThread.getInterval());
            threadProgressIndicator.setProgress(progress + 1000f/(double)weatherThread.getInterval());
        });
    }

    /**
     * Creates alert window with specific header and content text.
     * @param headerText
     * @param contentText
     */
    public static void threadError(String headerText, String contentText) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);

            alert.showAndWait();
        });
    }

    /**
     * Change units to metric
     * @param event
     */
    @FXML
    void toCelsius(ActionEvent event) {
        units = "metric";
        unitsLabel = CELSIUS;
        updateChartLabels();
    }

    /**
     * Change units to imperial
     * @param event
     */
    @FXML
    void toFahrenheit(ActionEvent event) {
        units = "imperial";
        unitsLabel = FAHRENHEIT;
        updateChartLabels();
    }

    /**
     * Change chart type to Humidity(Time)
     * @param event
     */
    @FXML
    void changeChartTypeHum(ActionEvent event) {
        chartType = "humidity";
        yLabelString = "Humidity [%]";
        updateWeatherChart();
    }

    /**
     * Change chart type to Pressure(Time)
     * @param event
     */
    @FXML
    void changeChartTypePressure(ActionEvent event) {
        chartType = "pressure";
        yLabelString = "Pressure [hPa]";
        updateWeatherChart();
    }

    /**
     * Change chart type to Temperature(Time)
     * @param event
     */
    @FXML
    void changeChartTypeTemp(ActionEvent event) {
        chartType = "temperature";
        yLabelString = "Temp [" + unitsLabel + "]";
        updateWeatherChart();
    }

    /**
     * Method called when application is closing.
     * Saving program properties.
     */
    @FXML
    public void exitApplication(){
        settings.put("City",cityTextField.getText());
        settings.put("Freq",freqField.getText());

        try {
            FileOutputStream outputStream = new FileOutputStream(propFile);
            settings.store(outputStream, "Program Properties");
            System.out.println("Zapisano!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ExitApp");
    }

}
