package edu.uiuc.cs427app;

public class CityWeather {
    private final String name;
    private final String locationKey;


    public CityWeather(String name, String locationKey) {
        this.name = name;
        this.locationKey = locationKey;
    }

    public String getName() {
        return name;
    }

    public String getLocationKey() {
        return locationKey;
    }

}
