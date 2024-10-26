package com.matou.smartcar.event;

import com.matou.smartcar.bean.RsiBean;
import com.matou.smartcar.bean.RsuInfoBean;

public class ControlEvent {

    public ControlEvent(RsiBean rsiBean, RsuInfoBean rsuInfoBean) {
        this.rsiBean = rsiBean;
        this.rsuInfoBean = rsuInfoBean;
    }

    private RsiBean rsiBean;

    private RsuInfoBean rsuInfoBean;

    public RsiBean getRsiBean() {
        return rsiBean;
    }

    public void setRsiBean(RsiBean rsiBean) {
        this.rsiBean = rsiBean;
    }

    public RsuInfoBean getRsuInfoBean() {
        return rsuInfoBean;
    }

    public void setRsuInfoBean(RsuInfoBean rsuInfoBean) {
        this.rsuInfoBean = rsuInfoBean;
    }
}
