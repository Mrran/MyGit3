package com.matou.smartcar.bean;

public class PropertyUploadBean {


    /**
     * timestamp : 1601196762389
     * messageId : 9999999999
     */

    private long timestamp;
    private String messageId;

    private PropertyBean properties = new PropertyBean();

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

    public PropertyBean getProperties() {
        return properties;
    }

    public void setProperties(PropertyBean properties) {
        this.properties = properties;
    }
}
