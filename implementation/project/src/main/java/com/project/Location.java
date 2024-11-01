package com.project;

public class Location {
    private String address;
    private City city;
    private String room;
    private String organization;

    public Location(String address, String city, String organization, String room) {
        this.address = address;
        this.city = new City(city);
        this.organization = organization;
        this.room = room;
    }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    @Override
    public String toString() {
        return address + ";" + city.getName() + ";" + organization + ";" + room;
    }

    public static Location fromString(String locationStr) {
        String[] parts = locationStr.split(";");
        if (parts.length == 4) {
            String address = parts[0];
            String city = parts[1];
            String organization = parts[2];
            String room = parts[3];
            return new Location(address, city, organization, room);
        } else {
            System.err.println("Error parsing location string.");
            return null;
        }
    }
}
