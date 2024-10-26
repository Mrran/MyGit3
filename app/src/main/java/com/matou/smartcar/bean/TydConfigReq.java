package com.matou.smartcar.bean;

import com.matou.smartcar.util.CommUtils;

public class TydConfigReq {

    public long timestamp;

    public String messageId;

    public Data data = new Data();

    public class Data {
        public String tydConfig;
    }

    public static TydConfigReq build(){
        TydConfigReq req = new TydConfigReq();
        req.timestamp = System.currentTimeMillis();
        req.messageId = CommUtils.geneMsgId();
        req.data.tydConfig = "ask";
        return req;
    }

}
