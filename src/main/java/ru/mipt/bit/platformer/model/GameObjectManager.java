package ru.mipt.bit.platformer.model;

import ru.mipt.bit.platformer.observer.Observable;
import ru.mipt.bit.platformer.observer.Observer;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages game objects and implements the Observable pattern
 */
public class GameObjectManager implements Observable {
    private List<GameObjectModel> tanks;
    private List<GameObjectModel> trees;
    private List<BulletModel> bullets;
    private List<Observer> observers;
    private boolean objectsChanged;

    public GameObjectManager() {
        this.tanks = new ArrayList<>();
        this.trees = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.objectsChanged = false;
    }

    // Observable implementation
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        if (objectsChanged) {
            for (Observer observer : observers) {
                observer.update();
            }
            objectsChanged = false;
        }
    }

    // Methods for managing game objects
    public void addTank(GameObjectModel tank) {
        tanks.add(tank);
        objectsChanged = true;
    }

    public void removeTank(GameObjectModel tank) {
        tanks.remove(tank);
        objectsChanged = true;
    }

    public void addTree(GameObjectModel tree) {
        trees.add(tree);
        objectsChanged = true;
    }

    public void removeTree(GameObjectModel tree) {
        trees.remove(tree);
        objectsChanged = true;
    }

    public void addBullet(BulletModel bullet) {
        bullets.add(bullet);
        objectsChanged = true;
    }

    public void removeBullet(BulletModel bullet) {
        bullets.remove(bullet);
        objectsChanged = true;
    }

    // Getters
    public List<GameObjectModel> getTanks() {
        return new ArrayList<>(tanks);
    }

    public List<GameObjectModel> getTrees() {
        return new ArrayList<>(trees);
    }

    public List<BulletModel> getBullets() {
        return new ArrayList<>(bullets);
    }

    public List<GameObjectModel> getAllObjects() {
        List<GameObjectModel> allObjects = new ArrayList<>();
        allObjects.addAll(tanks);
        allObjects.addAll(trees);
        // Convert bullets to GameObjectModel for consistency
        allObjects.addAll(bullets);
        return allObjects;
    }
}
