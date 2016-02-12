package net.xas.vrs.model;

import java.util.Objects;

/**
 * Video rental base resource class.
 */
public abstract class Resource {

    private final String id;

    protected Resource(String id) {
        Objects.requireNonNull(id, "id");
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
