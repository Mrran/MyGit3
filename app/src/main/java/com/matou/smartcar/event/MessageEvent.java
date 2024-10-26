package com.matou.smartcar.event;

import com.matou.smartcar.bean.BaseBean;
import com.matou.smartcar.bean.DataInfoBean;

public class MessageEvent {
    private DataInfoBean dataInfoBean;

    public MessageEvent(DataInfoBean dataInfoBean) {
        this.dataInfoBean = dataInfoBean;
    }

    public DataInfoBean getData() {
        return dataInfoBean;
    }
}
