package sample.weather;

import javafx.collections.ObservableList;
import sample.model.Observer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WeatherDisplay implements Observer {

    private String city;
    private Weather weather;
    private ObservableList<Weather> weatherList;
    private ArrayList<String> timeList;

    public WeatherDisplay(String city, ObservableList<Weather> weatherList,ArrayList<String> timeList) {
        this.city = city;
        this.weatherList = weatherList;
        this.timeList = timeList;
    }

    public Weather getWeather() {
        return weather;
    }

    @Override
    public void updateWeather(Weather weather) {
        this.weather = weather;
        weatherList.add(weather);
        timeList.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        System.out.println(weatherList.size());
    }

    @Override
    public String getCity() {
        return city;
    }

}
