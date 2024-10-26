package com.matou.smartcar.bean;

import java.io.Serializable;

public class BaseBean<T> implements Serializable {

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 消息体
     */
    private T data;



    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
