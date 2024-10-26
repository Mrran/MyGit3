package com.matou.smartcar.bean;

public class RsiResBean {

    /**
     * 默认是图片资源  如果是布局文件 置为 false
     */
    private int res;
    private String value;
    private String playDescription;
    private String rsiId;

    private boolean isShowValue = false;

    /**
     * 优先级
     */
    private int priority;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPlayDescription() {
        return playDescription;
    }

    public void setPlayDescription(String playDescription) {
        this.playDescription = playDescription;
    }

    public String getRsiId() {
        return rsiId;
    }

    public void setRsiId(String rsiId) {
        this.rsiId = rsiId;
    }

    public boolean isShowValue() {
        return isShowValue;
    }

    public void setShowValue(boolean showValue) {
        isShowValue = showValue;
    }
}
