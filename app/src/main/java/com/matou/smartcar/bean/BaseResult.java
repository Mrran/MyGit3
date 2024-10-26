package com.matou.smartcar.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseResult<T> implements Serializable {

    /**
     * code : app_00_0000
     * data : 数据
     * msg : 请求成功
     * showMsg : false
     */

    @SerializedName(value = "code", alternate = {"resultCode"})
    private String code;
    public T data;
    @SerializedName(value = "msg", alternate = {"message"})
    private String msg;
    private boolean showMsg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isShowMsg() {
        return showMsg;
    }

    public void setShowMsg(boolean showMsg) {
        this.showMsg = showMsg;
    }


}