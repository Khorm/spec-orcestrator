package com.petra.lib.z_user_package.source_handlers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Truck {
    private Long id;
    private String name;
    private Driver driver;

    public Truck(Long id, String name, Driver driver) {
        this.id = id;
        this.name = name;
        this.driver = driver;
    }


    @Override
    public String toString() {
        return "Truck{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", driver=" + (null == getDriver() ? "null" : getDriver().getId()) +
                '}';
    }
}
