package chapter09.designpattern.observer;

public interface Subject {
    void registerObserver(Observer observer);

    void notifyObservers(String tweet);
}
