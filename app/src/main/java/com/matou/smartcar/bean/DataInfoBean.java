package com.matou.smartcar.bean;

import java.io.Serializable;
import java.util.List;

public class DataInfoBean implements Serializable {

    /**
     * HVMSG : {"sid":"20024103","timeStamp":1608202324797,"sourceType":0,"vehicleType":10,"plateNo":"京A24103","speed":90,"heading":154,"pos":{"latitude":29.678259,"longitude":106.493644,"elevation":239.300003},"angle":0,"transmission":0,"accelSet":{"lon":0,"lat":0,"vert":0,"yaw":0},"size":{"width":0.01,"height":0.1,"length":0.02},"brakeSystemStatus":{},"safetyExt":{"VehicleEventFlags":{},"PathHistory":{},"PathPrediction":{},"ExteriorLights":4}}
     * RVMSG : {"vinfo":[{"sid":"20024104","timeStamp":1608202355296,"sourceType":0,"vehicleType":5,"plateNo":"京A 123456","speed":0,"heading":209.324997,"pos":{"latitude":29.67288,"longitude":106.474283,"elevation":0},"angle":0,"transmission":0,"accelSet":{"lon":0,"lat":0,"vert":0,"yaw":0},"size":{"width":0.01,"height":0,"length":0.02},"brakeSystemStatus":{},"safetyExt":{},"v2vEvent":{"eventStr":"ICW","dir":1,"dirAngle":88,"distanceV":41.50111,"distanceH":5.767226,"TTC":0.590592}}]}
     * RSIEvent : [{"event":"IVS","rsiId":"CNODE0039","alertType":11,"distance":1.753768,"alertRadius":20,"eventValue":0,"arrivalTime":0.070151,"description":"scho","pos":{"latitude":29.677858,"longitude":106.493959,"elevation":0},"alertPath":[{"latitude":29.677858,"longitude":106.493959,"elevation":0},{"latitude":29.677473,"longitude":106.494208,"elevation":0}]}]
     * TrafficLight : {"eventStr":"GLOSA","timeStamp":1608202355360,"pos":{"latitude":0,"longitude":0,"elevation":0},"light":[{"remoteId":{"region":256,"id":18},"dir":2,"pr":2,"color":3,"time":59,"speedDown":0,"speedUp":0,"rlvwFlag":1},{"remoteId":{"region":256,"id":46},"dir":1,"pr":6,"color":3,"time":14,"speedDown":6,"speedUp":14,"rlvwFlag":1}],"distance":55.674805,"arrivalTime":2.226992}
     * RoadInfo : {"nodeId":{"region":256,"id":13},"upstreamId":{"region":256,"id":14},"speedLimit":70,"currSpeed":90,"GPSStatus":1}
     */
    private int seq;
    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    private HVMSGBean HVMSG;
    private List<RVMSGBean> RVMSG;
    private TrafficLightBean TrafficLight;
    private List<RSIEventBean> RSIEvent;
    private List<RSUInfoBean> RSUInfo;


    public HVMSGBean getHVMSG() {
        return HVMSG;
    }

    public void setHVMSG(HVMSGBean HVMSG) {
        this.HVMSG = HVMSG;
    }

    public List<RVMSGBean> getRVMSG() {
        return RVMSG;
    }

    public void setRVMSG(List<RVMSGBean> RVMSG) {
        this.RVMSG = RVMSG;
    }

    public TrafficLightBean getTrafficLight() {
        return TrafficLight;
    }

    public void setTrafficLight(TrafficLightBean TrafficLight) {
        this.TrafficLight = TrafficLight;
    }


    public List<RSIEventBean> getRSIEvent() {
        return RSIEvent;
    }

    public void setRSIEvent(List<RSIEventBean> RSIEvent) {
        this.RSIEvent = RSIEvent;
    }

    public List<RSUInfoBean> getRSUInfo() {
        return RSUInfo;
    }

    public void setRSUInfo(List<RSUInfoBean> RSUInfo) {
        this.RSUInfo = RSUInfo;
    }

    public static class HVMSGBean {
        /**
         * sid : 20024103
         * timeStamp : 1608202324797
         * sourceType : 0
         * vehicleType : 10
         * plateNo : 京A24103
         * speed : 90
         * heading : 154
         * pos : {"latitude":29.678259,"longitude":106.493644,"elevation":239.300003}
         * angle : 0
         * transmission : 0
         * accelSet : {"lon":0,"lat":0,"vert":0,"yaw":0}
         * size : {"width":0.01,"height":0.1,"length":0.02}
         * brakeSystemStatus : {}
         * safetyExt : {"VehicleEventFlags":{},"PathHistory":{},"PathPrediction":{},"ExteriorLights":4}
         */

        private String sid;
        private long timeStamp;
        private int sourceType;
        private int vehicleType;
        private String plateNo;
        private float speed;
        private float heading;
        private PosBean pos;
        private float angle;
        private int transmission;
        // private AccelSetBean accelSet;
        private SizeBean size;
        private BrakeSystemStatusBean brakeSystemStatus;
        private SafetyExtBean safetyExt;
        private RoadInfoBean roadInfo;

        public RoadInfoBean getRoadInfo() {
            return roadInfo;
        }

        public void setRoadInfo(RoadInfoBean RoadInfo) {
            this.roadInfo = RoadInfo;
        }

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public int getSourceType() {
            return sourceType;
        }

        public void setSourceType(int sourceType) {
            this.sourceType = sourceType;
        }

        public int getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(int vehicleType) {
            this.vehicleType = vehicleType;
        }

        public String getPlateNo() {
            return plateNo;
        }

        public void setPlateNo(String plateNo) {
            this.plateNo = plateNo;
        }

        public float getSpeed() {
            return speed;
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }

        public float getHeading() {
            return heading;
        }

        public void setHeading(float heading) {
            this.heading = heading;
        }

        public PosBean getPos() {
            return pos;
        }

        public void setPos(PosBean pos) {
            this.pos = pos;
        }

        public float getAngle() {
            return angle;
        }

        public void setAngle(float angle) {
            this.angle = angle;
        }

        public int getTransmission() {
            return transmission;
        }

        public void setTransmission(int transmission) {
            this.transmission = transmission;
        }

//        public AccelSetBean getAccelSet() {
//            return accelSet;
//        }
//
//        public void setAccelSet(AccelSetBean accelSet) {
//            this.accelSet = accelSet;
//        }

        public SizeBean getSize() {
            return size;
        }

        public void setSize(SizeBean size) {
            this.size = size;
        }

        public BrakeSystemStatusBean getBrakeSystemStatus() {
            return brakeSystemStatus;
        }

        public void setBrakeSystemStatus(BrakeSystemStatusBean brakeSystemStatus) {
            this.brakeSystemStatus = brakeSystemStatus;
        }

        public SafetyExtBean getSafetyExt() {
            return safetyExt;
        }

        public void setSafetyExt(SafetyExtBean safetyExt) {
            this.safetyExt = safetyExt;
        }

        public static class PosBean {
            /**
             * latitude : 29.678259
             * longitude : 106.493644
             * elevation : 239.300003
             */

            private double latitude;
            private double longitude;
            private double elevation;

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public double getElevation() {
                return elevation;
            }

            public void setElevation(double elevation) {
                this.elevation = elevation;
            }
        }

        public static class AccelSetBean {
            /**
             * lon : 0
             * lat : 0
             * vert : 0
             * yaw : 0
             */

            private double lon;
            private double lat;
            private double vert;
            private double yaw;

            public double getLon() {
                return lon;
            }

            public void setLon(double lon) {
                this.lon = lon;
            }

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getVert() {
                return vert;
            }

            public void setVert(double vert) {
                this.vert = vert;
            }

            public double getYaw() {
                return yaw;
            }

            public void setYaw(double yaw) {
                this.yaw = yaw;
            }
        }

        public static class SizeBean {
        }

        public static class BrakeSystemStatusBean {
        }

        public static class SafetyExtBean {
            /**
             * VehicleEventFlags : {}
             * PathHistory : {}
             * PathPrediction : {}
             * ExteriorLights : 4
             */

            private VehicleEventFlagsBean VehicleEventFlags;
            private PathHistoryBean PathHistory;
            private PathPredictionBean PathPrediction;
            private int ExteriorLights;

            public VehicleEventFlagsBean getVehicleEventFlags() {
                return VehicleEventFlags;
            }

            public void setVehicleEventFlags(VehicleEventFlagsBean VehicleEventFlags) {
                this.VehicleEventFlags = VehicleEventFlags;
            }

            public PathHistoryBean getPathHistory() {
                return PathHistory;
            }

            public void setPathHistory(PathHistoryBean PathHistory) {
                this.PathHistory = PathHistory;
            }

            public PathPredictionBean getPathPrediction() {
                return PathPrediction;
            }

            public void setPathPrediction(PathPredictionBean PathPrediction) {
                this.PathPrediction = PathPrediction;
            }

            public int getExteriorLights() {
                return ExteriorLights;
            }

            public void setExteriorLights(int ExteriorLights) {
                this.ExteriorLights = ExteriorLights;
            }

            public static class VehicleEventFlagsBean {
            }

            public static class PathHistoryBean {
            }

            public static class PathPredictionBean {
            }
        }
    }

    public static class RVMSGBean {


        /**
         * sid : 20024104
         * timeStamp : 1608202355296
         * sourceType : 0
         * vehicleType : 5
         * plateNo : 京A 123456
         * speed : 0
         * heading : 209.324997
         * pos : {"latitude":29.67288,"longitude":106.474283,"elevation":0}
         * angle : 0
         * transmission : 0
         * accelSet : {"lon":0,"lat":0,"vert":0,"yaw":0}
         * size : {"width":0.01,"height":0,"length":0.02}
         * brakeSystemStatus : {}
         * safetyExt : {}
         * v2vEvent : {"eventStr":"ICW","dir":1,"dirAngle":88,"distanceV":41.50111,"distanceH":5.767226,"TTC":0.590592}
         */

        private String sid;
        private long timeStamp;
        private int sourceType;
        private int vehicleType;
        private String plateNo;
        private int speed;
        private float heading;
        private PosBeanX pos;
        private int angle;
        private int transmission;
        private AccelSetBeanX accelSet;
        private SizeBeanX size;
        private BrakeSystemStatusBeanX brakeSystemStatus;
        private SafetyExtBeanX safetyExt;
        private V2vEventBean v2vEvent;
        private RoadInfoBean roadInfo;

        /**
         * 只播放语音
         */
        private boolean onlyTts = true;

        public boolean isOnlyTts() {
            return onlyTts;
        }

        public void setOnlyTts(boolean onlyTts) {
            this.onlyTts = onlyTts;
        }

        public RoadInfoBean getRoadInfo() {
            return roadInfo;
        }

        public void setRoadInfo(RoadInfoBean RoadInfo) {
            this.roadInfo = RoadInfo;
        }

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public int getSourceType() {
            return sourceType;
        }

        public void setSourceType(int sourceType) {
            this.sourceType = sourceType;
        }

        public int getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(int vehicleType) {
            this.vehicleType = vehicleType;
        }

        public String getPlateNo() {
            return plateNo;
        }

        public void setPlateNo(String plateNo) {
            this.plateNo = plateNo;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public float getHeading() {
            return heading;
        }

        public void setHeading(float heading) {
            this.heading = heading;
        }

        public PosBeanX getPos() {
            return pos;
        }

        public void setPos(PosBeanX pos) {
            this.pos = pos;
        }

        public int getAngle() {
            return angle;
        }

        public void setAngle(int angle) {
            this.angle = angle;
        }

        public int getTransmission() {
            return transmission;
        }

        public void setTransmission(int transmission) {
            this.transmission = transmission;
        }

        public AccelSetBeanX getAccelSet() {
            return accelSet;
        }

        public void setAccelSet(AccelSetBeanX accelSet) {
            this.accelSet = accelSet;
        }

        public SizeBeanX getSize() {
            return size;
        }

        public void setSize(SizeBeanX size) {
            this.size = size;
        }

        public BrakeSystemStatusBeanX getBrakeSystemStatus() {
            return brakeSystemStatus;
        }

        public void setBrakeSystemStatus(BrakeSystemStatusBeanX brakeSystemStatus) {
            this.brakeSystemStatus = brakeSystemStatus;
        }

        public SafetyExtBeanX getSafetyExt() {
            return safetyExt;
        }

        public void setSafetyExt(SafetyExtBeanX safetyExt) {
            this.safetyExt = safetyExt;
        }

        public V2vEventBean getV2vEvent() {
            return v2vEvent;
        }

        public void setV2vEvent(V2vEventBean v2vEvent) {
            this.v2vEvent = v2vEvent;
        }

        public static class PosBeanX {
            /**
             * latitude : 29.67288
             * longitude : 106.474283
             * elevation : 0
             */

            private double latitude;
            private double longitude;
            private double elevation;

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public double getElevation() {
                return elevation;
            }

            public void setElevation(double elevation) {
                this.elevation = elevation;
            }
        }

        public static class AccelSetBeanX {
            /**
             * lon : 0
             * lat : 0
             * vert : 0
             * yaw : 0
             */

            private int lon;
            private int lat;
            private int vert;
            private int yaw;

            public int getLon() {
                return lon;
            }

            public void setLon(int lon) {
                this.lon = lon;
            }

            public int getLat() {
                return lat;
            }

            public void setLat(int lat) {
                this.lat = lat;
            }

            public int getVert() {
                return vert;
            }

            public void setVert(int vert) {
                this.vert = vert;
            }

            public int getYaw() {
                return yaw;
            }

            public void setYaw(int yaw) {
                this.yaw = yaw;
            }
        }

        public static class SizeBeanX {
        }

        public static class BrakeSystemStatusBeanX {
        }

        public static class SafetyExtBeanX {

            private int ExteriorLights;

            public int getExteriorLights() {
                return ExteriorLights;
            }

            public void setExteriorLights(int exteriorLights) {
                ExteriorLights = exteriorLights;
            }
        }

        public static class V2vEventBean {
            /**
             * eventStr : ICW
             * dir : 1
             * dirAngle : 88
             * distanceV : 41.50111
             * distanceH : 5.767226
             * TTC : 0.590592
             */

            private String eventStr;
            private int dir;
            private int act;//1减速避让  0 优先通行
            private double dirAngle;
            private double distanceV;
            private double distanceH;
            private double TTC;

            public String getEventStr() {
                return eventStr;
            }

            public void setEventStr(String eventStr) {
                this.eventStr = eventStr;
            }

            public int getDir() {
                return dir;
            }

            public void setDir(int dir) {
                this.dir = dir;
            }

            public double getDirAngle() {
                return dirAngle;
            }

            public void setDirAngle(double dirAngle) {
                this.dirAngle = dirAngle;
            }

            public double getDistanceV() {
                return distanceV;
            }

            public void setDistanceV(double distanceV) {
                this.distanceV = distanceV;
            }

            public double getDistanceH() {
                return distanceH;
            }

            public void setDistanceH(double distanceH) {
                this.distanceH = distanceH;
            }

            public double getTTC() {
                return TTC;
            }

            public void setTTC(double TTC) {
                this.TTC = TTC;
            }

            public int getAct() {
                return act;
            }

            public void setAct(int act) {
                this.act = act;
            }
        }
    }


    public static class TrafficLightBean {
        /**
         * eventStr : GLOSA
         * timeStamp : 1608202355360
         * pos : {"latitude":0,"longitude":0,"elevation":0}
         * light : [{"remoteId":{"region":256,"id":18},"dir":2,"pr":2,"color":3,"time":59,"speedDown":0,"speedUp":0,"rlvwFlag":1},{"remoteId":{"region":256,"id":46},"dir":1,"pr":6,"color":3,"time":14,"speedDown":6,"speedUp":14,"rlvwFlag":1}]
         * distance : 55.674805
         * arrivalTime : 2.226992
         */

        private String eventStr;
        private long timeStamp;
        private PosBeanXX pos;
        private double distance;
        private double arrivalTime;
        private List<LightBean> light;

        public String getEventStr() {
            return eventStr;
        }

        public void setEventStr(String eventStr) {
            this.eventStr = eventStr;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public PosBeanXX getPos() {
            return pos;
        }

        public void setPos(PosBeanXX pos) {
            this.pos = pos;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public double getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(double arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public List<LightBean> getLight() {
            return light;
        }

        public void setLight(List<LightBean> light) {
            this.light = light;
        }

        public static class PosBeanXX {
            /**
             * latitude : 0
             * longitude : 0
             * elevation : 0
             */

            private double latitude;
            private double longitude;
            private double elevation;

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public double getElevation() {
                return elevation;
            }

            public void setElevation(double elevation) {
                this.elevation = elevation;
            }
        }

        public static class LightBean {
            /**
             * remoteId : {"region":256,"id":18}
             * dir : 2
             * pr : 2
             * color : 3
             * time : 59
             * speedDown : 0
             * speedUp : 0
             * rlvwFlag : 1
             */

            private RemoteIdBean remoteId;
            private int dir;
            private int pr;
            private int color;
            private int time;
            private int speedDown;
            private int speedUp;
            private int rlvwFlag;
            private int laneID;
            private int currDir;
            private boolean animIng;//动画是否在执行中

            public RemoteIdBean getRemoteId() {
                return remoteId;
            }

            public void setRemoteId(RemoteIdBean remoteId) {
                this.remoteId = remoteId;
            }

            public int getDir() {
                return dir;
            }

            public void setDir(int dir) {
                this.dir = dir;
            }

            public int getPr() {
                return pr;
            }

            public void setPr(int pr) {
                this.pr = pr;
            }

            public int getColor() {
                return color;
            }

            public void setColor(int color) {
                this.color = color;
            }

            public int getTime() {
                return time;
            }

            public void setTime(int time) {
                this.time = time;
            }

            public int getSpeedDown() {
                return speedDown;
            }

            public void setSpeedDown(int speedDown) {
                this.speedDown = speedDown;
            }

            public int getSpeedUp() {
                return speedUp;
            }

            public void setSpeedUp(int speedUp) {
                this.speedUp = speedUp;
            }

            public int getRlvwFlag() {
                return rlvwFlag;
            }

            public void setRlvwFlag(int rlvwFlag) {
                this.rlvwFlag = rlvwFlag;
            }

            public int getCurrDir() {
                return currDir;
            }

            public void setCurrDir(int currDir) {
                this.currDir = currDir;
            }

            public boolean isAnimIng() {
                return animIng;
            }

            public void setAnimIng(boolean animIng) {
                this.animIng = animIng;
            }

            public int getLaneID() {
                return laneID;
            }

            public void setLaneID(int laneID) {
                this.laneID = laneID;
            }

            public static class RemoteIdBean {
                /**
                 * region : 256
                 * id : 18
                 */

                private int region;
                private int id;

                public int getRegion() {
                    return region;
                }

                public void setRegion(int region) {
                    this.region = region;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }
            }
        }
    }

    public static class RoadInfoBean {
        /**
         * nodeId : {"region":256,"id":13}
         * upstreamId : {"region":256,"id":14}
         * speedLimit : 70
         * currSpeed : 90
         * GPSStatus : 1
         */

        private NodeIdBean nodeId;
        private UpstreamIdBean upstreamId;
        private float speedLimit;
        private float currSpeed;
        private int GPSStatus;
        private int roadType;//1 主路  0辅道

        public NodeIdBean getNodeId() {
            return nodeId;
        }

        public void setNodeId(NodeIdBean nodeId) {
            this.nodeId = nodeId;
        }

        public UpstreamIdBean getUpstreamId() {
            return upstreamId;
        }

        public void setUpstreamId(UpstreamIdBean upstreamId) {
            this.upstreamId = upstreamId;
        }

        public float getSpeedLimit() {
            return speedLimit;
        }

        public void setSpeedLimit(float speedLimit) {
            this.speedLimit = speedLimit;
        }

        public float getCurrSpeed() {
            return currSpeed;
        }

        public void setCurrSpeed(float currSpeed) {
            this.currSpeed = currSpeed;
        }

        public int getGPSStatus() {
            return GPSStatus;
        }

        public void setGPSStatus(int GPSStatus) {
            this.GPSStatus = GPSStatus;
        }

        public int getRoadType() {
            return roadType;
        }

        public void setRoadType(int roadType) {
            this.roadType = roadType;
        }

        public static class NodeIdBean {
            /**
             * region : 256
             * id : 13
             */

            private int region;
            private int id;

            public int getRegion() {
                return region;
            }

            public void setRegion(int region) {
                this.region = region;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }

        public static class UpstreamIdBean {
            /**
             * region : 256
             * id : 14
             */

            private int region;
            private int id;

            public int getRegion() {
                return region;
            }

            public void setRegion(int region) {
                this.region = region;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }
    }

    public static class RSIEventBean {
        /**
         * event : IVS
         * rsiId : CNODE0039
         * alertType : 11
         * distance : 1.753768
         * alertRadius : 20
         * eventValue : 0
         * arrivalTime : 0.070151
         * description : scho
         * pos : {"latitude":29.677858,"longitude":106.493959,"elevation":0}
         * alertPath : [{"latitude":29.677858,"longitude":106.493959,"elevation":0},{"latitude":29.677473,"longitude":106.494208,"elevation":0}]
         */

        private String eventStr;
        private String rsiId;
        private int alertType;
        private double distance;
        private int alertRadius;
        private int eventValue;
        private double arrivalTime;
        private String description;
        private PosBeanXXX pos;
        private List<AlertPathBean> alertPath;

        public String getEvent() {
            return eventStr;
        }

        public void setEvent(String event) {
            this.eventStr = event;
        }

        public String getRsiId() {
            return rsiId;
        }

        public void setRsiId(String rsiId) {
            this.rsiId = rsiId;
        }

        public int getAlertType() {
            return alertType;
        }

        public void setAlertType(int alertType) {
            this.alertType = alertType;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public int getAlertRadius() {
            return alertRadius;
        }

        public void setAlertRadius(int alertRadius) {
            this.alertRadius = alertRadius;
        }

        public int getEventValue() {
            return eventValue;
        }

        public void setEventValue(int eventValue) {
            this.eventValue = eventValue;
        }

        public double getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(double arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public PosBeanXXX getPos() {
            return pos;
        }

        public void setPos(PosBeanXXX pos) {
            this.pos = pos;
        }

        public List<AlertPathBean> getAlertPath() {
            return alertPath;
        }

        public void setAlertPath(List<AlertPathBean> alertPath) {
            this.alertPath = alertPath;
        }

        public static class PosBeanXXX {
            /**
             * latitude : 29.677858
             * longitude : 106.493959
             * elevation : 0
             */

            private double latitude;
            private double longitude;
            private double elevation;

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public double getElevation() {
                return elevation;
            }

            public void setElevation(double elevation) {
                this.elevation = elevation;
            }
        }

        public static class AlertPathBean {
            /**
             * latitude : 29.677858
             * longitude : 106.493959
             * elevation : 0
             */

            private double latitude;
            private double longitude;
            private double elevation;

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public double getElevation() {
                return elevation;
            }

            public void setElevation(double elevation) {
                this.elevation = elevation;
            }
        }
    }


    public static class RSUInfoBean {

        /**
         * rsuid : NODE000e
         * pos : {"latitude":" 296792971","longitude":" 1064930329","elevation":0}
         */

        private String rsuid;
        private PosBean pos;

        public String getRsuid() {
            return rsuid;
        }

        public void setRsuid(String rsuid) {
            this.rsuid = rsuid;
        }

        public PosBean getPos() {
            return pos;
        }

        public void setPos(PosBean pos) {
            this.pos = pos;
        }

        public static class PosBean {
            /**
             * latitude :  296792971
             * longitude :  1064930329
             * elevation : 0
             */

            private String latitude;
            private String longitude;
            private double elevation;

            public String getLatitude() {
                return latitude;
            }

            public void setLatitude(String latitude) {
                this.latitude = latitude;
            }

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }

            public double getElevation() {
                return elevation;
            }

            public void setElevation(int elevation) {
                this.elevation = elevation;
            }
        }
    }
}
