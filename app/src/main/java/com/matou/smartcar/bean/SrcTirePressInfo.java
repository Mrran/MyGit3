package com.matou.smartcar.bean;

public class SrcTirePressInfo {


    /**
     * frontLeft : {"hasFrontLeftBluetoothAddress":true,"bleAddress":"03:B3:EC:C3:DE:A5","hasFrontLeftTpmsData":true,"frontLeftTpmsData":{"bleAddress":"03:B3:EC:C3:DE:A5","bleAddressConfidence":100,"lastUpDateTimestamp_usec":1712113941601609,"dataIdentificationCode":172,"itemNo":0,"voltage_V":3.009999990463257,"pressure_kPa":34.540000915527344,"temperature_C":26,"status":0,"tpmsVersionNo":21,"year":2020,"month":7,"day":31,"bleVersion":10}}
     * frontRight : {"hasFrontRightBluetoothAddress":true,"bleAddress":"03:B3:EC:C3:DE:A6","hasFrontRightTpmsData":true,"frontRightTpmsData":{"bleAddress":"03:B3:EC:C3:DE:A6","bleAddressConfidence":100,"lastUpDateTimestamp_usec":1712113768398073,"dataIdentificationCode":172,"itemNo":0,"voltage_V":2.990000009536743,"pressure_kPa":34.540000915527344,"temperature_C":26,"status":0,"tpmsVersionNo":21,"year":2020,"month":7,"day":31,"bleVersion":10}}
     * rearLeft : {"hasRearLeftBluetoothAddress":true,"bleAddress":"03:B3:EC:C3:DE:A8","hasRearLeftTpmsData":true,"rearLeftTpmsData":{"bleAddress":"03:B3:EC:C3:DE:A8","bleAddressConfidence":100,"lastUpDateTimestamp_usec":1712113944504299,"dataIdentificationCode":172,"itemNo":0,"voltage_V":2.9800000190734863,"pressure_kPa":31.399999618530273,"temperature_C":26,"status":0,"tpmsVersionNo":21,"year":2020,"month":7,"day":31,"bleVersion":10}}
     * rearRight : {"hasRearRightBluetoothAddress":true,"bleAddress":"00:B3:EC:C3:DE:A8","hasRearRightTpmsData":false}
     */

    private FrontLeftBean frontLeft;
    private FrontRightBean frontRight;
    private RearLeftBean rearLeft;
    private RearRightBean rearRight;

    public FrontLeftBean getFrontLeft() {
        return frontLeft;
    }

    public void setFrontLeft(FrontLeftBean frontLeft) {
        this.frontLeft = frontLeft;
    }

    public FrontRightBean getFrontRight() {
        return frontRight;
    }

    public void setFrontRight(FrontRightBean frontRight) {
        this.frontRight = frontRight;
    }

    public RearLeftBean getRearLeft() {
        return rearLeft;
    }

    public void setRearLeft(RearLeftBean rearLeft) {
        this.rearLeft = rearLeft;
    }

    public RearRightBean getRearRight() {
        return rearRight;
    }

    public void setRearRight(RearRightBean rearRight) {
        this.rearRight = rearRight;
    }

    public static class FrontLeftBean {
        /**
         * hasFrontLeftBluetoothAddress : true
         * bleAddress : 03:B3:EC:C3:DE:A5
         * hasFrontLeftTpmsData : true
         * frontLeftTpmsData : {"bleAddress":"03:B3:EC:C3:DE:A5","bleAddressConfidence":100,"lastUpDateTimestamp_usec":1712113941601609,"dataIdentificationCode":172,"itemNo":0,"voltage_V":3.009999990463257,"pressure_kPa":34.540000915527344,"temperature_C":26,"status":0,"tpmsVersionNo":21,"year":2020,"month":7,"day":31,"bleVersion":10}
         */

        private boolean hasFrontLeftBluetoothAddress;
        private String bleAddress;
        private boolean hasFrontLeftTpmsData;
        private TpmsDataBean frontLeftTpmsData;

        public boolean isHasFrontLeftBluetoothAddress() {
            return hasFrontLeftBluetoothAddress;
        }

        public void setHasFrontLeftBluetoothAddress(boolean hasFrontLeftBluetoothAddress) {
            this.hasFrontLeftBluetoothAddress = hasFrontLeftBluetoothAddress;
        }

        public String getBleAddress() {
            return bleAddress;
        }

        public void setBleAddress(String bleAddress) {
            this.bleAddress = bleAddress;
        }

        public boolean isHasFrontLeftTpmsData() {
            return hasFrontLeftTpmsData;
        }

        public void setHasFrontLeftTpmsData(boolean hasFrontLeftTpmsData) {
            this.hasFrontLeftTpmsData = hasFrontLeftTpmsData;
        }

        public TpmsDataBean getFrontLeftTpmsData() {
            return frontLeftTpmsData;
        }

        public void setFrontLeftTpmsData(TpmsDataBean frontLeftTpmsData) {
            this.frontLeftTpmsData = frontLeftTpmsData;
        }
    }

    public static class TpmsDataBean {
        /**
         * bleAddress : 03:B3:EC:C3:DE:A5
         * bleAddressConfidence : 100
         * lastUpDateTimestamp_usec : 1712113941601609
         * dataIdentificationCode : 172
         * itemNo : 0
         * voltage_V : 3.009999990463257
         * pressure_kPa : 34.540000915527344
         * temperature_C : 26
         * status : 0
         * tpmsVersionNo : 21
         * year : 2020
         * month : 7
         * day : 31
         * bleVersion : 10
         */

        private String bleAddress;
        private int bleAddressConfidence;
        private long lastUpDateTimestamp_usec;
        private int dataIdentificationCode;
        private int itemNo;
        private double voltage_V;
        private double pressure_kPa;
        private int temperature_C;
        private int status;
        private int tpmsVersionNo;
        private int year;
        private int month;
        private int day;
        private int bleVersion;

        public String getBleAddress() {
            return bleAddress;
        }

        public void setBleAddress(String bleAddress) {
            this.bleAddress = bleAddress;
        }

        public int getBleAddressConfidence() {
            return bleAddressConfidence;
        }

        public void setBleAddressConfidence(int bleAddressConfidence) {
            this.bleAddressConfidence = bleAddressConfidence;
        }

        public long getLastUpDateTimestamp_usec() {
            return lastUpDateTimestamp_usec;
        }

        public void setLastUpDateTimestamp_usec(long lastUpDateTimestamp_usec) {
            this.lastUpDateTimestamp_usec = lastUpDateTimestamp_usec;
        }

        public int getDataIdentificationCode() {
            return dataIdentificationCode;
        }

        public void setDataIdentificationCode(int dataIdentificationCode) {
            this.dataIdentificationCode = dataIdentificationCode;
        }

        public int getItemNo() {
            return itemNo;
        }

        public void setItemNo(int itemNo) {
            this.itemNo = itemNo;
        }

        public double getVoltage_V() {
            return voltage_V;
        }

        public void setVoltage_V(double voltage_V) {
            this.voltage_V = voltage_V;
        }

        public double getPressure_kPa() {
            return pressure_kPa;
        }

        public void setPressure_kPa(double pressure_kPa) {
            this.pressure_kPa = pressure_kPa;
        }

        public int getTemperature_C() {
            return temperature_C;
        }

        public void setTemperature_C(int temperature_C) {
            this.temperature_C = temperature_C;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getTpmsVersionNo() {
            return tpmsVersionNo;
        }

        public void setTpmsVersionNo(int tpmsVersionNo) {
            this.tpmsVersionNo = tpmsVersionNo;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getBleVersion() {
            return bleVersion;
        }

        public void setBleVersion(int bleVersion) {
            this.bleVersion = bleVersion;
        }
    }

    public static class FrontRightBean {
        /**
         * hasFrontRightBluetoothAddress : true
         * bleAddress : 03:B3:EC:C3:DE:A6
         * hasFrontRightTpmsData : true
         * frontRightTpmsData : {"bleAddress":"03:B3:EC:C3:DE:A6","bleAddressConfidence":100,"lastUpDateTimestamp_usec":1712113768398073,"dataIdentificationCode":172,"itemNo":0,"voltage_V":2.990000009536743,"pressure_kPa":34.540000915527344,"temperature_C":26,"status":0,"tpmsVersionNo":21,"year":2020,"month":7,"day":31,"bleVersion":10}
         */

        private boolean hasFrontRightBluetoothAddress;
        private String bleAddress;
        private boolean hasFrontRightTpmsData;
        private TpmsDataBean frontRightTpmsData;

        public boolean isHasFrontRightBluetoothAddress() {
            return hasFrontRightBluetoothAddress;
        }

        public void setHasFrontRightBluetoothAddress(boolean hasFrontRightBluetoothAddress) {
            this.hasFrontRightBluetoothAddress = hasFrontRightBluetoothAddress;
        }

        public String getBleAddress() {
            return bleAddress;
        }

        public void setBleAddress(String bleAddress) {
            this.bleAddress = bleAddress;
        }

        public boolean isHasFrontRightTpmsData() {
            return hasFrontRightTpmsData;
        }

        public void setHasFrontRightTpmsData(boolean hasFrontRightTpmsData) {
            this.hasFrontRightTpmsData = hasFrontRightTpmsData;
        }

        public TpmsDataBean getFrontRightTpmsData() {
            return frontRightTpmsData;
        }

        public void setFrontRightTpmsData(TpmsDataBean frontRightTpmsData) {
            this.frontRightTpmsData = frontRightTpmsData;
        }

    }

    public static class RearLeftBean {
        /**
         * hasRearLeftBluetoothAddress : true
         * bleAddress : 03:B3:EC:C3:DE:A8
         * hasRearLeftTpmsData : true
         * rearLeftTpmsData : {"bleAddress":"03:B3:EC:C3:DE:A8","bleAddressConfidence":100,"lastUpDateTimestamp_usec":1712113944504299,"dataIdentificationCode":172,"itemNo":0,"voltage_V":2.9800000190734863,"pressure_kPa":31.399999618530273,"temperature_C":26,"status":0,"tpmsVersionNo":21,"year":2020,"month":7,"day":31,"bleVersion":10}
         */

        private boolean hasRearLeftBluetoothAddress;
        private String bleAddress;
        private boolean hasRearLeftTpmsData;
        private TpmsDataBean rearLeftTpmsData;

        public boolean isHasRearLeftBluetoothAddress() {
            return hasRearLeftBluetoothAddress;
        }

        public void setHasRearLeftBluetoothAddress(boolean hasRearLeftBluetoothAddress) {
            this.hasRearLeftBluetoothAddress = hasRearLeftBluetoothAddress;
        }

        public String getBleAddress() {
            return bleAddress;
        }

        public void setBleAddress(String bleAddress) {
            this.bleAddress = bleAddress;
        }

        public boolean isHasRearLeftTpmsData() {
            return hasRearLeftTpmsData;
        }

        public void setHasRearLeftTpmsData(boolean hasRearLeftTpmsData) {
            this.hasRearLeftTpmsData = hasRearLeftTpmsData;
        }

        public TpmsDataBean getRearLeftTpmsData() {
            return rearLeftTpmsData;
        }

        public void setRearLeftTpmsData(TpmsDataBean rearLeftTpmsData) {
            this.rearLeftTpmsData = rearLeftTpmsData;
        }
    }

    public static class RearRightBean {
        /**
         * hasRearRightBluetoothAddress : true
         * bleAddress : 00:B3:EC:C3:DE:A8
         * hasRearRightTpmsData : false
         */

        private boolean hasRearRightBluetoothAddress;
        private String bleAddress;
        private boolean hasRearRightTpmsData;

        private TpmsDataBean rearRightTpmsData;

        public boolean isHasRearRightBluetoothAddress() {
            return hasRearRightBluetoothAddress;
        }

        public void setHasRearRightBluetoothAddress(boolean hasRearRightBluetoothAddress) {
            this.hasRearRightBluetoothAddress = hasRearRightBluetoothAddress;
        }

        public String getBleAddress() {
            return bleAddress;
        }

        public void setBleAddress(String bleAddress) {
            this.bleAddress = bleAddress;
        }

        public boolean isHasRearRightTpmsData() {
            return hasRearRightTpmsData;
        }

        public void setHasRearRightTpmsData(boolean hasRearRightTpmsData) {
            this.hasRearRightTpmsData = hasRearRightTpmsData;
        }

        public TpmsDataBean getRearRightTpmsData() {
            return rearRightTpmsData;
        }

        public void setRearRightTpmsData(TpmsDataBean rearRightTpmsData) {
            this.rearRightTpmsData = rearRightTpmsData;
        }
    }
}
