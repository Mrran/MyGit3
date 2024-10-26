package com.matou.smartcar.bean;

import java.io.Serializable;
import java.util.List;

public class ParkBean implements Serializable {

    /**
     * allParkingNum : 5
     * freeSpaceNum : 164
     * parkingList : [{"lat":29.677946,"lng":106.503387,"name":"嘉和路一段","num":29,"price":""},{"lat":29.677946,"lng":106.503387,"name":"嘉和路三段","num":19,"price":""},{"lat":29.67461,"lng":106.52847,"name":"平泰路（延锋安道拓段）","num":0,"price":""},{"lat":29.652544,"lng":106.515006,"name":"九曲河湿地公园C2停车场","num":54,"price":""},{"lat":29.677946,"lng":106.503387,"name":"嘉和路二段","num":62,"price":""}]
     * spaceNum : 296
     */

    private int allParkingNum;
    private int freeSpaceNum;
    private int spaceNum;
    private List<ParkingListBean> parkingList;

    public int getAllParkingNum() {
        return allParkingNum;
    }

    public void setAllParkingNum(int allParkingNum) {
        this.allParkingNum = allParkingNum;
    }

    public int getFreeSpaceNum() {
        return freeSpaceNum;
    }

    public void setFreeSpaceNum(int freeSpaceNum) {
        this.freeSpaceNum = freeSpaceNum;
    }

    public int getSpaceNum() {
        return spaceNum;
    }

    public void setSpaceNum(int spaceNum) {
        this.spaceNum = spaceNum;
    }

    public List<ParkingListBean> getParkingList() {
        return parkingList;
    }

    public void setParkingList(List<ParkingListBean> parkingList) {
        this.parkingList = parkingList;
    }

    public static class ParkingListBean {
        /**
         * lat : 29.677946
         * lng : 106.503387
         * name : 嘉和路一段
         * num : 29
         * price :
         */

        private double lat;
        private double lng;
        private String name;

        /**
         * -1表示未知
         */
        private int num;

        /**
         * 空表示未知
         */
        private String price;

        /**
         * 是否直线距离
         */
        private boolean isLine;

        private double distance;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public boolean isLine() {
            return isLine;
        }

        public void setLine(boolean line) {
            isLine = line;
        }
    }
}
