package com.petra.lib.block.model;


import java.util.Objects;

public class Identifier {
    private final long id;

    private final String version;

    public Identifier(Long id, String version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }

        Identifier id = (Identifier) o;
        return id.id == this.id && id.version.equals(this.version);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id, version);
    }

    public long getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }
}
