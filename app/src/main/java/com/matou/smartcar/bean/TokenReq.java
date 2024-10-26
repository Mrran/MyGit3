package com.matou.smartcar.bean;

import com.matou.smartcar.util.MD5Utils;

public class TokenReq {


    /**
     * clientId : OBU001
     * timesnap : 1673225526214
     * secrectCode : 587b2841413dc0ff0e2628de443d5b6d
     */

    private String clientId;
    private String timesnap;
    private String secrectCode;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTimesnap() {
        return timesnap;
    }

    public void setTimesnap(String timesnap) {
        this.timesnap = timesnap;
        this.secrectCode = MD5Utils.encrypt(timesnap + "#caeri12345678");
    }

    public String getSecrectCode() {
        return secrectCode;
    }

}
