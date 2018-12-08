package sample.model;

import java.io.IOException;

public interface Observable {

    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void updateObservers() throws IOException;
}
