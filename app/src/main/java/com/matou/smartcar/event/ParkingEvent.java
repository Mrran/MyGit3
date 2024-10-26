package com.matou.smartcar.event;

import com.matou.smartcar.bean.ParkingBean;

import java.util.List;

public class ParkingEvent {

    public ParkingEvent(ParkingBean parkingBean) {
        this.parkingBean = parkingBean;
    }

    private ParkingBean parkingBean;

    public ParkingBean getParkingBean() {
        return parkingBean;
    }

    public void setParkingBean(ParkingBean parkingBean) {
        this.parkingBean = parkingBean;
    }
}
