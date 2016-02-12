package net.xas.vrs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Video rental film.
 */
public class Film extends Resource {

    public enum Type {
        NEW, REGULAR, OLD
    }

    private String name;
    private Type type;

    @JsonCreator
    public Film(@JsonProperty("id") String id,
                @JsonProperty("name") String name,
                @JsonProperty("type") Type type) {

        super(id);

        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");

        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

}
