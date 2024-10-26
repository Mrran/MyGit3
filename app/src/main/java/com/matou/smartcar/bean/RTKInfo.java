package com.matou.smartcar.bean;

/**
 * RTK实体
 * @author ranfeng
 */
public class RTKInfo {


    /**
     * state : 5
     * rtcm : rtk.i-vista.org:2102
     * expiration : 2024-12-30
     */

    private int state;
    private String rtcm;
    private String expiration;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getRtcm() {
        return rtcm;
    }

    public void setRtcm(String rtcm) {
        this.rtcm = rtcm;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    @Override
    public String toString() {
        return "RTK: " +
                "state=" + state +
                ", rtcm='" + rtcm + '\'' +
                ", expiration='" + expiration + '\'';
    }
}
