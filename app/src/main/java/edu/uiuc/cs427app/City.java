package edu.uiuc.cs427app;

import java.io.Serializable;

@SuppressWarnings("serial")
public class City implements Serializable {
    private String name;
    private Double coordX;
    private Double coordY;

    public City(String name, Double coordX, Double coordY) {
        this.name = name;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public String getName() {
        return name;
    }

    public Double getCoordX() {
        return coordX;
    }

    public Double getCoordY() {
        return coordY;
    }
}
