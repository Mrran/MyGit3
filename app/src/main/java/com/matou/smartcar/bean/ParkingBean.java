package com.matou.smartcar.bean;

import java.io.Serializable;

public class ParkingBean implements Serializable {


    /**
     * areaName : 金桂路-玥湖路
     * parkingMin : 5
     * sourceId : 9
     * sourceType : 1
     * startTime : 1691475704873
     * uuid : f3a66621-7b91-4f8e-a1cc-1b59885d1200
     */

    private String areaName;
    private int parkingMin;
    private int sourceId;
    private int sourceType;
    private long startTime;
    private String uuid;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getParkingMin() {
        return parkingMin;
    }

    public void setParkingMin(int parkingMin) {
        this.parkingMin = parkingMin;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        long now = System.currentTimeMillis();
        return  areaName + '\'' + ", 累计" + (now - startTime)/1000 + "秒";
    }
}
