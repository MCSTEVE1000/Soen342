package com.project;

public class City {
    private String name;

    public City(String name) {
        this.name = name.trim().toLowerCase();
    }

    public String getName() {
        return name;
    }
}
