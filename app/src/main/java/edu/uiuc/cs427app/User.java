package edu.uiuc.cs427app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class User implements Serializable{

    private String name;
    private String email;
    private String theme;
    private String UID;
    private List<City> cities;

    User(String name, String email, String UID, String theme) {
        this.name = name;
        this.email = email;
        this.theme = theme;
        this.UID = UID;
        cities = new ArrayList<>();
    }

    public List<City> getCities() {
        return cities;
    }

    public void addCity(City city) {
        cities.add(city);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }


}
