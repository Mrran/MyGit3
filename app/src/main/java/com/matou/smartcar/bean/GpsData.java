package com.matou.smartcar.bean;

import java.io.Serializable;

/**
 * @author ranfeng
 */
public class GpsData implements Serializable {


    /**
     * longitude : 106.5268837
     * latitude : 29.6459676
     * elevation : 0
     * direction : 226.71
     * speed : 0
     */

    private double longitude;
    private double latitude;
    private double elevation;
    private double direction;
    private double speed;

    public double getLongitude() {
        return longitude;
    }

    public GpsData setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public GpsData setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getElevation() {
        return elevation;
    }

    public GpsData setElevation(double elevation) {
        this.elevation = elevation;
        return this;
    }

    public double getDirection() {
        return direction;
    }

    public GpsData setDirection(double direction) {
        this.direction = direction;
        return this;
    }

    public double getSpeed() {
        return speed;
    }

    public GpsData setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    public void reset() {
        this.direction = 0;
        this.elevation = 0;
        this.speed = 0;
        this.latitude = 0;
        this.longitude = 0;
    }
}
