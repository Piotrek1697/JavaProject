package sample.weather;

import sample.Controller;
import sample.model.Observable;
import sample.model.Observer;

import java.io.IOException;
import java.util.ArrayList;

public class WeatherThread implements Runnable, Observable{

    private Thread thread;
    private int interval;
    private volatile boolean isRunning = false;
    private volatile ArrayList<Observer> observerList = new ArrayList<>();


    public WeatherThread() {
        interval = 60001;
    }

    public WeatherThread(int interval) {
        this.interval = interval;

    }

    public int getInterval() {
        return interval;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void addObserver(Observer observer) {
        if (!observerList.contains(observer)) {
            observerList.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        if (observerList.contains(observer)) {
            observerList.remove(observer);
        }
    }

    @Override
    public void updateObservers() throws IOException {
        for (Observer o : observerList) {
            Weather weather = WeatherStation.getWeatherFromCity(o.getCity());
            o.updateWeather(weather);
        }
    }

    public void start() {
        thread = new Thread(this, "Weather Thread");
        thread.start();
    }

    public void stop(){
        isRunning = false;
    }

    public void interrupt() {
        isRunning = false;
        thread.interrupt();
    }

    @Override
    public void run() {
        isRunning = true;

        while (isRunning) {
            try {
                updateObservers();
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Failed");
            } catch (IOException e) {
                Controller.threadError("Wrong city entered or no internet connection",
                        "Check if there is no typo in city name or check your internet connection");
                return;

            }
        }

    }


}
