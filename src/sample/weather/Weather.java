package sample.weather;

/**
 * This class is made for mapping openWeather json.
 * With this class you can simple get Weather class objects easily from json.
 * It represents main object in json.
 */
public class Weather {

    private double temp;
    private double pressure;
    private double humidity;
    private double temp_min;
    private double temp_max;

    /**
     *
     * @param temp - Temperature
     * @param pressure - Pressure
     * @param humidity - Humidity
     * @param temp_min - Minimal temperature
     * @param temp_max - Maximum temperature
     */
    public Weather(double temp, double pressure, double humidity, double temp_min, double temp_max) {
        this.temp = temp;
        this.pressure = pressure;
        this.humidity = humidity;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
    }

    /**
     *
     * @return Temperature
     */
    public double getTemp() {
        return temp;
    }

    /**
     *
     * @return Pressure
     */
    public double getPressure() {
        return pressure;
    }

    /**
     *
     * @return Humidity
     */
    public double getHumidity() {
        return humidity;
    }

    /**
     *
     * @return Minimum temperature
     */
    public double getTemp_min() {
        return temp_min;
    }

    /**
     *
     * @return Maximum temperature
     */
    public double getTemp_max() {
        return temp_max;
    }

    @Override
    public String toString() {
        return "Temperature: " + temp + "\n" +
                "Pressure: " + pressure + "\n" +
                "Humidity: " + humidity + "\n" +
                "Max temperature: " + temp_max + "\n" +
                "Min temperature: " + temp_min;
    }
}
