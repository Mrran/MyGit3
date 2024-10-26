package com.matou.smartcar.event;

import com.matou.smartcar.bean.SrcTirePressInfo;

public class TirePressEvent {
    private SrcTirePressInfo srcTirePressInfo;

    public TirePressEvent(SrcTirePressInfo srcTirePressInfo) {
        this.srcTirePressInfo = srcTirePressInfo;
    }

    public SrcTirePressInfo getTirePressInfo() {
        return srcTirePressInfo;
    }

    public void setTirePressInfo(SrcTirePressInfo srcTirePressInfo) {
        this.srcTirePressInfo = srcTirePressInfo;
    }
}
