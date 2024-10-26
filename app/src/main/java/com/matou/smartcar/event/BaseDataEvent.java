package com.matou.smartcar.event;

import com.matou.smartcar.protocol.Icdc2HmiOuterClass;

public class BaseDataEvent {
    private Icdc2HmiOuterClass.Icdc2Hmi data;

    public BaseDataEvent(Icdc2HmiOuterClass.Icdc2Hmi data) {
        this.data = data;
    }

    public Icdc2HmiOuterClass.Icdc2Hmi getData() {
        return data;
    }
}
