package com.matou.smartcar.bean;

import com.elvishew.xlog.XLog;

/**
 * @author ranfeng
 */
public class CarStatus {

    public static final int MAX_CHECK_TIME = 1;

    /**
     * -1 表示速度还没被设置过
     */
    private float speed = -1;

    /**
     * 检测到停车时的时间戳
     */
    private long parkingTime = 0;

    /**
     * 当前运动状态
     * 0：初始
     * 1：速度为0，静止
     * 2：速度非0，运动
     */
    private int moveStatus;

    private boolean hasGps;

    private double lon;

    private double lat;

    public int getMoveStatus() {
        return moveStatus;
    }

    public void setMoveStatus(int moveStatus) {
        XLog.w("---> setMoveStatus = " + moveStatus);
        this.moveStatus = moveStatus;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public long getParkingTime() {
        return parkingTime;
    }

    public void setParkingTime(long parkingTime) {
        this.parkingTime = parkingTime;
    }

    public boolean isHasGps() {
        return hasGps;
    }

    public void setHasGps(boolean hasGps) {
        this.hasGps = hasGps;
    }

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
}
