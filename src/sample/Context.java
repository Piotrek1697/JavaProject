package sample;

import sample.weather.Weather;

public class Context {

    private final static Context instance = new Context();
    private Weather weather;

    public static Context getInstance() {
        return instance;
    }

    public Weather getCurrentWeather() {
        return weather;
    }

    public void setCurrentWeather(Weather weather){
        this.weather = weather;
    }
}
