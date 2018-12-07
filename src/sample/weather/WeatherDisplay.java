package sample.weather;

import sample.Context;
import sample.Controller;
import sample.model.DisplayWeather;
import sample.model.Observer;

public class WeatherDisplay implements Observer {

    private String city;
    private Weather weather;
    private DisplayWeather displayWeather;

    public WeatherDisplay(String city) {
        this.city = city;
    }

    public Weather getWeather() {
        return weather;
    }

    @Override
    public void updateWeather(Weather weather) {
        this.weather = weather;
        Context.getInstance().setCurrentWeather(weather);
    }

    @Override
    public String getCity() {
        return city;
    }

}
