package sample.time;

import javafx.collections.ObservableList;
import java.time.LocalTime;

public class TimeThread implements Runnable{

    private Thread thread;
    private String city;
    private ObservableList<LocalTime> localTimeList;

    public TimeThread(ObservableList<LocalTime> localTimeList) {
        this.localTimeList = localTimeList;
    }

    public void start(){
        thread = new Thread(this,"Time Thread");
        thread.start();
    }

    private void updateTime(){
        localTimeList.clear();
        LocalTime localTime = LocalTime.now();
        localTimeList.addAll(localTime);
    }

    @Override
    public void run() {
        while (true){
            try {
                updateTime();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
