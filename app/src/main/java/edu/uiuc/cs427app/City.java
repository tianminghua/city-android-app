package edu.uiuc.cs427app;

import java.io.Serializable;

@SuppressWarnings("serial")
public class City implements Serializable {
    private String name;
    private String coordX;
    private String coordY;

    public City(String name, String coordX, String coordY) {
        this.name = name;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public String getName() {
        return name;
    }

    public String getCoordX() {
        return coordX;
    }

    public String getCoordY() {
        return coordY;
    }
}
