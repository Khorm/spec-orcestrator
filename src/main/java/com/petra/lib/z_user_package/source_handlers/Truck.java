package com.petra.lib.z_user_package.source_handlers;

public class Truck {
    private Long id;
    private String name;

    public Truck(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Truck{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
