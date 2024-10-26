package com.matou.smartcar.bean;

import java.io.Serializable;

public class RsuInfoBean implements Serializable {


    /**
     * addr : 重庆市渝北区金渝大道
     * crossName : 欢乐谷
     * deviceCode : rsu-zgqy-cictr61500115
     * deviceId : 20024222
     * lat : 29.66975424387482
     * lng : 106.50754671085384
     * onlineState : 1
     * productId : rsu-zgqy-cictr615
     */

    private String addr;
    private String crossName;
    private String deviceCode;
    private String deviceId;
    private double lat;
    private double lng;
    private int onlineState;
    private String productId;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCrossName() {
        return crossName;
    }

    public void setCrossName(String crossName) {
        this.crossName = crossName;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(int onlineState) {
        this.onlineState = onlineState;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
