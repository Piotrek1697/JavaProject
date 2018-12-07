package sample.weather;

import javafx.collections.ObservableList;
import sample.model.Observer;

public class WeatherDisplay implements Observer {

    private String city;
    private Weather weather;
    private ObservableList<Weather> weatherList;

    public WeatherDisplay(String city, ObservableList<Weather> weatherList) {
        this.city = city;
        this.weatherList = weatherList;
    }

    public Weather getWeather() {
        return weather;
    }

    @Override
    public void updateWeather(Weather weather) {
        this.weather = weather;
        weatherList.add(weather);
        System.out.println(weatherList.size());
    }

    @Override
    public String getCity() {
        return city;
    }

}
