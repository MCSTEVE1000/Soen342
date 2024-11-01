package com.project;

public class Availabilities {
    public City[] cities;

    public Availabilities(City[] cities) {
        this.cities = cities;
    }

    public static Availabilities parseAvailabilities(String availabilities) {
        String[] availabilitiesArray = availabilities.split(",");
        City[] cities = new City[availabilitiesArray.length];
        for (int i = 0; i < availabilitiesArray.length; i++) {
            availabilitiesArray[i] = availabilitiesArray[i].trim();
            City city = new City(availabilitiesArray[i]);
            cities[i] = city;
        }
        return new Availabilities(cities);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (City city : cities) {
            sb.append(city.getName()).append(",");
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
