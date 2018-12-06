package sample.weather;

import sample.Controller;
import sample.model.Observer;

public class WeatherDisplay implements Observer {

    private String city;
    private Weather weather;

    public WeatherDisplay(String city) {
        this.city = city;
    }

    public Weather getWeather() {
        return weather;
    }

    @Override
    public void updateWeather(Weather weather) {
        this.weather = weather;
        //System.out.println(weather);
    }

    @Override
    public String getCity() {
        return city;
    }
}
