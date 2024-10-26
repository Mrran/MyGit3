package com.matou.smartcar.bean;

public class BsmReq {


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

    public BsmReq setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public BsmReq setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getElevation() {
        return elevation;
    }

    public BsmReq setElevation(double elevation) {
        this.elevation = elevation;
        return this;
    }

    public double getDirection() {
        return direction;
    }

    public BsmReq setDirection(double direction) {
        this.direction = direction;
        return this;
    }

    public double getSpeed() {
        return speed;
    }

    public BsmReq setSpeed(double speed) {
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
