package com.matou.smartcar.bean;

import java.io.Serializable;

/**
 * @author ranfeng
 */
public class IpAddressBean implements Serializable {
    private String name;
    private String address;
    private boolean selected;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
