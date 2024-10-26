package com.matou.smartcar.bean;

/**
 *
 * @author ranfeng
 */
public class AlertTypeBean {

    /**
     * RTE的子类型
     */
    private String subType;

    /**
     * 值
     */
    private String value;

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
