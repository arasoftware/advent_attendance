package com.ara.advent.models;

/**
 * Created by User on 09-May-18.
 */

public class RouteAndvehicleModel {

    public RouteAndvehicleModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    String id;
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
