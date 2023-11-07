package edu.uiuc.cs427app;

public class CityWeather {
    private final String name;
    private final String locationKey;
    private String areaName;
    private String countryName;


    public CityWeather(String name, String locationKey) {
        this.name = name;
        this.locationKey = locationKey;
    }

    public String getName() {
        return name;
    }

    public void addAreaAndCountry(String area, String country) {
        this.areaName = area;
        this.countryName = country;
    }

    public String getFullName() {
        return name + ", " + areaName + ", " + countryName;
    }

    public String getLocationKey() {
        return locationKey;
    }

}
