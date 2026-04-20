package ru.mipt.bit.platformer.observer;

/**
 * Observable interface for the Observer pattern implementation
 */
public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}
