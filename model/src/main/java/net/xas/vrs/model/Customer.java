package net.xas.vrs.model;

/**
 * Created by Xavi on 08/02/16.
 */
public class Customer {

    private final String id;
    private final String name;

    private int bonusPoints = 0;

    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public void addBonusPoints(int bonusPoints) {
        this.bonusPoints += bonusPoints;
    }
}
