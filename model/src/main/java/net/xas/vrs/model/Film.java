package net.xas.vrs.model;

/**
 * Created by Xavi on 08/02/16.
 */
public class Film {

    public enum Type {
        NEW, REGULAR, OLD
    }

    private final String id;
    private String name;
    private Type type;

    public Film(String id, String name, Type type) {
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

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
