package ru.mipt.bit.platformer.model;

import ru.mipt.bit.platformer.observer.Observable;
import ru.mipt.bit.platformer.observer.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages game objects and implements the Observable pattern
 */
public class GameObjectManager implements Observable {
    private final List<TankModel> tanks;
    private final List<TreeModel> trees;
    private final List<BulletModel> bullets;
    private final List<Observer> observers;
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
    public void addTank(TankModel tank) {
        tanks.add(tank);
        markChanged();
    }

    public void removeTank(TankModel tank) {
        tanks.remove(tank);
        markChanged();
    }

    public void addTree(TreeModel tree) {
        trees.add(tree);
        markChanged();
    }

    public void removeTree(TreeModel tree) {
        trees.remove(tree);
        markChanged();
    }

    public void addBullet(BulletModel bullet) {
        bullets.add(bullet);
        markChanged();
    }

    public void removeBullet(BulletModel bullet) {
        bullets.remove(bullet);
        markChanged();
    }

    // Getters
    public List<TankModel> getTanks() {
        return Collections.unmodifiableList(tanks);
    }

    public List<TreeModel> getTrees() {
        return Collections.unmodifiableList(trees);
    }

    public List<BulletModel> getBullets() {
        return bullets;
    }

    public List<GameObjectModel> getAllObjects() {
        List<GameObjectModel> allObjects = new ArrayList<>();
        allObjects.addAll(tanks);
        allObjects.addAll(trees);
        // Convert bullets to GameObjectModel for consistency
        allObjects.addAll(bullets);
        return allObjects;
    }

    private void markChanged() {
        objectsChanged = true;
        notifyObservers();
    }
}
