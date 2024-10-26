package com.matou.smartcar.bean;

import java.io.Serializable;

/**
 * @author ranfeng
 */
public class UpdateInfoBean implements Serializable {

    /**
     * 文件签名
     */
    private String sign;

    /**
     * 文件签名方式
     */
    private String signMethod;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 安装包下载地址
     */
    private String url;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignMethod() {
        return signMethod;
    }

    public void setSignMethod(String signMethod) {
        this.signMethod = signMethod;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
