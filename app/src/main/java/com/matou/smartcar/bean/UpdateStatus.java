package com.matou.smartcar.bean;

import java.io.Serializable;

/**
 * 升级状态保存
 * @author ranfeng
 */
public class UpdateStatus implements Serializable {

    /**
     * 0，无需升级；1、升级中；2、升级成功；3、升级失败
     */
    private int status;

    private String failReason;

    /**
     * 升级版本
     */
    private String version;

    private String upgradeCode;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUpgradeCode() {
        return upgradeCode;
    }

    public void setUpgradeCode(String upgradeCode) {
        this.upgradeCode = upgradeCode;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}
