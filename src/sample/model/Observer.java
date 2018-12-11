package sample.model;

import sample.weather.Weather;

public interface Observer {

    void updateWeather(Weather weather);
    String getCity();
    String getUnits();
}
