package com.matou.smartcar.bean;

/**
 * obu的设备信息
 * @author ranfeng
 */
public class DeviceInfo {

    /**
     * id : 20041021
     * sn : 2021541
     * supplier : 中国汽研
     * model : ICTC-V855
     * mfd : 2021-5-1
     * swVer : 1.0.0
     * hwVer : 1.0.0
     * protocolStack : Day1
     * uptime : 10:28:13 up 11:52
     */
    private String id;
    private String sn;
    private String supplier;
    private String model;
    private String mfd;
    private String swVer;
    private String hwVer;
    private String protocolStack;
    private String uptime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMfd() {
        return mfd;
    }

    public void setMfd(String mfd) {
        this.mfd = mfd;
    }

    public String getSwVer() {
        return swVer;
    }

    public void setSwVer(String swVer) {
        this.swVer = swVer;
    }

    public String getHwVer() {
        return hwVer;
    }

    public void setHwVer(String hwVer) {
        this.hwVer = hwVer;
    }

    public String getProtocolStack() {
        return protocolStack;
    }

    public void setProtocolStack(String protocolStack) {
        this.protocolStack = protocolStack;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }
}
