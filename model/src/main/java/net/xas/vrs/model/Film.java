package net.xas.vrs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Video rental film.
 */
public class Film {

    public enum Type {
        NEW, REGULAR, OLD
    }

    private final String id;
    private String name;
    private Type type;

    @JsonCreator
    public Film(@JsonProperty("id") String id,
                @JsonProperty("name") String name,
                @JsonProperty("type") Type type) {

        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");

        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

}
