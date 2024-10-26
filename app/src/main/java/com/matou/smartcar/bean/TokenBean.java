package com.matou.smartcar.bean;

public class TokenBean {


    /**
     * token : uuid
     * expire : 30
     */

    private String token;
    private int expire;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}
