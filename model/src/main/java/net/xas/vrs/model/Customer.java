package net.xas.vrs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Video rental customer.
 */
public class Customer {

    private final String id;
    private final String name;

    private int bonusPoints = 0;

    @JsonCreator
    public Customer(@JsonProperty("id") String id,
                    @JsonProperty("name") String name) {

        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");

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
