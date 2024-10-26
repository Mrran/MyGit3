package com.matou.smartcar.bean;

import java.io.Serializable;

public class RsiBean implements Serializable {


    /**
     * eventStr :
     * rsiId :
     * alertType : 1
     * distance : 10.5
     * arrivalTime : 3.3
     * eventValue : 0
     * alertRadius : 5
     * pos : {"lon":0,"lat":0,"elevation":0}
     * description :
     * sourcePlatform :
     */

    private String eventStr;
    private String rsiId;
    private int alertType;
    private double distance;
    private double arrivalTime;
    private double eventValue;
    private int alertRadius;
    private PosBean pos;
    private String description;
    private String sourcePlatform;

    public String getEventStr() {
        return eventStr;
    }

    public void setEventStr(String eventStr) {
        this.eventStr = eventStr;
    }

    public String getRsiId() {
        return rsiId;
    }

    public void setRsiId(String rsiId) {
        this.rsiId = rsiId;
    }

    public int getAlertType() {
        return alertType;
    }

    public void setAlertType(int alertType) {
        this.alertType = alertType;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getEventValue() {
        return eventValue;
    }

    public void setEventValue(double eventValue) {
        this.eventValue = eventValue;
    }

    public int getAlertRadius() {
        return alertRadius;
    }

    public void setAlertRadius(int alertRadius) {
        this.alertRadius = alertRadius;
    }

    public PosBean getPos() {
        return pos;
    }

    public void setPos(PosBean pos) {
        this.pos = pos;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourcePlatform() {
        return sourcePlatform;
    }

    public void setSourcePlatform(String sourcePlatform) {
        this.sourcePlatform = sourcePlatform;
    }

    public static class PosBean {
        /**
         * lon : 0
         * lat : 0
         * elevation : 0
         */

        private double lon;
        private double lat;
        private double elevation;

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getElevation() {
            return elevation;
        }

        public void setElevation(double elevation) {
            this.elevation = elevation;
        }
    }
}
