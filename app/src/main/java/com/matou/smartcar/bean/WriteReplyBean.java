package com.matou.smartcar.bean;

import java.io.Serializable;

public class WriteReplyBean implements Serializable {


    /**
     * timestamp : 1601196762389
     * messageId : 与下行消息中的messageId相同
     * properties : {"sn":"test"}
     * deviceId : 设备ID
     * success : true
     */

    private long timestamp;
    private String messageId;
    private PropertyBean properties = new PropertyBean();
    private String deviceId;
    private boolean success;

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

    public PropertyBean getProperties() {
        return properties;
    }

    public void setProperties(PropertyBean properties) {
        this.properties = properties;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private String code;

    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



}
