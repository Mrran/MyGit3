package com.matou.smartcar.bean;

import java.io.Serializable;
import java.util.List;

public class ReadBean implements Serializable {


    /**
     * timestamp : 1601196762389
     * messageId : 消息ID
     * deviceId : 设备ID
     * properties : ["sn"]
     */

    private long timestamp;
    private String messageId;
    private String deviceId;
    private List<String> properties;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }
}
