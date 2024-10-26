package com.matou.smartcar.bean;

import java.util.List;

public class TydConfigResp {


    /**
     * timestamp : 1601196762389
     * messageId : 99999999
     * deviceId : 3874780
     * function : tydConfig
     * inputs : [{"name":"lowVal","value":200.5},{"name":"topVal","value":300.5}]
     */

    private long timestamp;
    private String messageId;
    private String deviceId;
    private String function;
    private List<InputsBean> inputs;

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

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public List<InputsBean> getInputs() {
        return inputs;
    }

    public void setInputs(List<InputsBean> inputs) {
        this.inputs = inputs;
    }

    public static class InputsBean {
        /**
         * name : lowVal
         * value : 200.5
         */

        private String name;
        private double value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}
