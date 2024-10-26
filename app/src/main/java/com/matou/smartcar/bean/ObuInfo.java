package com.matou.smartcar.bean;

import java.io.Serializable;

public class ObuInfo implements Serializable {

    private String name;

    private String ip;

    private String ssidBegin;

    private String ssid;

    private String ssidPwd;


    public ObuInfo(String name, String ip, String ssidBegin, String ssidPwd) {
        this.name = name;
        this.ip = ip;
        this.ssidBegin = ssidBegin;
        this.ssidPwd = ssidPwd;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public String getSsidBegin() {
        return ssidBegin;
    }

    public String getSsidPwd() {
        return ssidPwd;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    @Override
    public String toString() {
        return "ObuInfo{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", ssidBegin='" + ssidBegin + '\'' +
                ", ssid='" + ssid + '\'' +
                ", ssidPwd='" + ssidPwd + '\'' +
                '}';
    }
}
