package com.matou.smartcar.event;

public class UpdateEvent {
    private String json;

    public UpdateEvent(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }
}
