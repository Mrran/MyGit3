package com.matou.smartcar.bean;

import java.io.Serializable;
import java.util.List;

public class FuncInvokeBean implements Serializable {


    /**
     * timestamp : 1601196762389
     * messageId : 99999999
     * deviceId : 3874780
     * function : trafficData
     * inputs : [{"name":"trafficSignLocation","value":{"laneMatchData":{"mapLink":{"nodeId":{"region":5000,"id":132},"linkName":"133-132","dhv":10.5,"thv":3.3,"mapLinkDto":{"mapNodeId":199,"mapVersion":"day1","name":"198-199","speedLimit":70,"points":[{"lat":29.729356,"lon":106.543896}]}}},"rsuInfo":{"deviceId":"rsu13055","deviceCode":"rsu-zgqy-cictr61500009","productId":"rsu-zgqy-cictr615","addr":"重庆市渝北区礼环北路","lng":106.5328069847255,"lat":29.714535309796453,"onlineState":1,"crossName":"国博智能汽车体验中心西南门"},"HVMSG":{"sid":"123321","timeStamp":1689847572843,"sourceType":6,"speed":37,"heading":33.15,"pos":{"elevation":232.899994,"lat":29.722667,"lon":106.535269},"roadInfo":{"nodeId":{"region":5000,"id":132},"speedLimit":70,"distance":10.5,"arrivalTime":3.3}},"TrafficLight":{"eventStr":"GLOSA","distance":10.5,"arrivalTime":3.3,"light":[{"remoteId":{"region":5000,"id":134},"dir":1,"pr":1,"color":5,"time":24,"speedDown":15.6,"speedUp":20.6,"rlvwFlag":1,"currDir":1}]},"RSIEvent":[{"eventStr":"IVS","rsiId":"","alertType":1,"distance":10.5,"arrivalTime":3.3,"eventValue":20,"alertRadius":0.1,"pos":{"lon":106.54388,"lat":29.729151},"description":"6e6f2064616e6765726f7573","sourcePlatform":"v2x-information"}],"parkingViolationEvent":[{"sourceType":1,"sourceId":1,"areaName":"xxx大道-xxx大道","parkingMin":5,"uuid":"uuid","startTime":168101010101111}]}}]
     */

    private long timestamp;
    private String messageId;
    private String deviceId;
    private String function;
    private List<InputsBean> inputs;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public List<InputsBean> getInputs() {
        return inputs;
    }

    public void setInputs(List<InputsBean> inputs) {
        this.inputs = inputs;
    }

    public static class InputsBean {
        /**
         * name : trafficSignLocation
         * value : {"laneMatchData":{"mapLink":{"nodeId":{"region":5000,"id":132},"linkName":"133-132","dhv":10.5,"thv":3.3,"mapLinkDto":{"mapNodeId":199,"mapVersion":"day1","name":"198-199","speedLimit":70,"points":[{"lat":29.729356,"lon":106.543896}]}}},"rsuInfo":{"deviceId":"rsu13055","deviceCode":"rsu-zgqy-cictr61500009","productId":"rsu-zgqy-cictr615","addr":"重庆市渝北区礼环北路","lng":106.5328069847255,"lat":29.714535309796453,"onlineState":1,"crossName":"国博智能汽车体验中心西南门"},"HVMSG":{"sid":"123321","timeStamp":1689847572843,"sourceType":6,"speed":37,"heading":33.15,"pos":{"elevation":232.899994,"lat":29.722667,"lon":106.535269},"roadInfo":{"nodeId":{"region":5000,"id":132},"speedLimit":70,"distance":10.5,"arrivalTime":3.3}},"TrafficLight":{"eventStr":"GLOSA","distance":10.5,"arrivalTime":3.3,"light":[{"remoteId":{"region":5000,"id":134},"dir":1,"pr":1,"color":5,"time":24,"speedDown":15.6,"speedUp":20.6,"rlvwFlag":1,"currDir":1}]},"RSIEvent":[{"eventStr":"IVS","rsiId":"","alertType":1,"distance":10.5,"arrivalTime":3.3,"eventValue":20,"alertRadius":0.1,"pos":{"lon":106.54388,"lat":29.729151},"description":"6e6f2064616e6765726f7573","sourcePlatform":"v2x-information"}],"parkingViolationEvent":[{"sourceType":1,"sourceId":1,"areaName":"xxx大道-xxx大道","parkingMin":5,"uuid":"uuid","startTime":168101010101111}]}
         */

        private String name;
        private ValueBean value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ValueBean getValue() {
            return value;
        }

        public void setValue(ValueBean value) {
            this.value = value;
        }

        public static class ValueBean {
            /**
             * laneMatchData : {"mapLink":{"nodeId":{"region":5000,"id":132},"linkName":"133-132","dhv":10.5,"thv":3.3,"mapLinkDto":{"mapNodeId":199,"mapVersion":"day1","name":"198-199","speedLimit":70,"points":[{"lat":29.729356,"lon":106.543896}]}}}
             * rsuInfo : {"deviceId":"rsu13055","deviceCode":"rsu-zgqy-cictr61500009","productId":"rsu-zgqy-cictr615","addr":"重庆市渝北区礼环北路","lng":106.5328069847255,"lat":29.714535309796453,"onlineState":1,"crossName":"国博智能汽车体验中心西南门"}
             * HVMSG : {"sid":"123321","timeStamp":1689847572843,"sourceType":6,"speed":37,"heading":33.15,"pos":{"elevation":232.899994,"lat":29.722667,"lon":106.535269},"roadInfo":{"nodeId":{"region":5000,"id":132},"speedLimit":70,"distance":10.5,"arrivalTime":3.3}}
             * TrafficLight : {"eventStr":"GLOSA","distance":10.5,"arrivalTime":3.3,"light":[{"remoteId":{"region":5000,"id":134},"dir":1,"pr":1,"color":5,"time":24,"speedDown":15.6,"speedUp":20.6,"rlvwFlag":1,"currDir":1}]}
             * RSIEvent : [{"eventStr":"IVS","rsiId":"","alertType":1,"distance":10.5,"arrivalTime":3.3,"eventValue":20,"alertRadius":0.1,"pos":{"lon":106.54388,"lat":29.729151},"description":"6e6f2064616e6765726f7573","sourcePlatform":"v2x-information"}]
             * parkingViolationEvent : [{"sourceType":1,"sourceId":1,"areaName":"xxx大道-xxx大道","parkingMin":5,"uuid":"uuid","startTime":168101010101111}]
             */

            private LaneMatchDataBean laneMatchData;
            private RsuInfoBean rsuInfo;
            private HVMSGBean HVMSG;
            private TrafficLightBean TrafficLight;
            private List<RSIEventBean> RSIEvent;
            private List<ParkingViolationEventBean> parkingViolationEvent;

            public LaneMatchDataBean getLaneMatchData() {
                return laneMatchData;
            }

            public void setLaneMatchData(LaneMatchDataBean laneMatchData) {
                this.laneMatchData = laneMatchData;
            }

            public RsuInfoBean getRsuInfo() {
                return rsuInfo;
            }

            public void setRsuInfo(RsuInfoBean rsuInfo) {
                this.rsuInfo = rsuInfo;
            }

            public HVMSGBean getHVMSG() {
                return HVMSG;
            }

            public void setHVMSG(HVMSGBean HVMSG) {
                this.HVMSG = HVMSG;
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

            public List<ParkingViolationEventBean> getParkingViolationEvent() {
                return parkingViolationEvent;
            }

            public void setParkingViolationEvent(List<ParkingViolationEventBean> parkingViolationEvent) {
                this.parkingViolationEvent = parkingViolationEvent;
            }

            public static class LaneMatchDataBean {
                /**
                 * mapLink : {"nodeId":{"region":5000,"id":132},"linkName":"133-132","dhv":10.5,"thv":3.3,"mapLinkDto":{"mapNodeId":199,"mapVersion":"day1","name":"198-199","speedLimit":70,"points":[{"lat":29.729356,"lon":106.543896}]}}
                 */

                private MapLinkBean mapLink;

                public MapLinkBean getMapLink() {
                    return mapLink;
                }

                public void setMapLink(MapLinkBean mapLink) {
                    this.mapLink = mapLink;
                }

                public static class MapLinkBean {
                    /**
                     * nodeId : {"region":5000,"id":132}
                     * linkName : 133-132
                     * dhv : 10.5
                     * thv : 3.3
                     * mapLinkDto : {"mapNodeId":199,"mapVersion":"day1","name":"198-199","speedLimit":70,"points":[{"lat":29.729356,"lon":106.543896}]}
                     */

                    private NodeIdBean nodeId;
                    private String linkName;
                    private double dhv;
                    private double thv;
                    private MapLinkDtoBean mapLinkDto;

                    public NodeIdBean getNodeId() {
                        return nodeId;
                    }

                    public void setNodeId(NodeIdBean nodeId) {
                        this.nodeId = nodeId;
                    }

                    public String getLinkName() {
                        return linkName;
                    }

                    public void setLinkName(String linkName) {
                        this.linkName = linkName;
                    }

                    public double getDhv() {
                        return dhv;
                    }

                    public void setDhv(double dhv) {
                        this.dhv = dhv;
                    }

                    public double getThv() {
                        return thv;
                    }

                    public void setThv(double thv) {
                        this.thv = thv;
                    }

                    public MapLinkDtoBean getMapLinkDto() {
                        return mapLinkDto;
                    }

                    public void setMapLinkDto(MapLinkDtoBean mapLinkDto) {
                        this.mapLinkDto = mapLinkDto;
                    }

                    public static class NodeIdBean {
                        /**
                         * region : 5000
                         * id : 132
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

                    public static class MapLinkDtoBean {
                        /**
                         * mapNodeId : 199
                         * mapVersion : day1
                         * name : 198-199
                         * speedLimit : 70
                         * points : [{"lat":29.729356,"lon":106.543896}]
                         */

                        private int mapNodeId;
                        private String mapVersion;
                        private String name;
                        private int speedLimit;
                        private List<PointsBean> points;

                        public int getMapNodeId() {
                            return mapNodeId;
                        }

                        public void setMapNodeId(int mapNodeId) {
                            this.mapNodeId = mapNodeId;
                        }

                        public String getMapVersion() {
                            return mapVersion;
                        }

                        public void setMapVersion(String mapVersion) {
                            this.mapVersion = mapVersion;
                        }

                        public String getName() {
                            return name;
                        }

                        public void setName(String name) {
                            this.name = name;
                        }

                        public int getSpeedLimit() {
                            return speedLimit;
                        }

                        public void setSpeedLimit(int speedLimit) {
                            this.speedLimit = speedLimit;
                        }

                        public List<PointsBean> getPoints() {
                            return points;
                        }

                        public void setPoints(List<PointsBean> points) {
                            this.points = points;
                        }

                        public static class PointsBean {
                            /**
                             * lat : 29.729356
                             * lon : 106.543896
                             */

                            private double lat;
                            private double lon;

                            public double getLat() {
                                return lat;
                            }

                            public void setLat(double lat) {
                                this.lat = lat;
                            }

                            public double getLon() {
                                return lon;
                            }

                            public void setLon(double lon) {
                                this.lon = lon;
                            }
                        }
                    }
                }
            }

            public static class RsuInfoBean {
                /**
                 * deviceId : rsu13055
                 * deviceCode : rsu-zgqy-cictr61500009
                 * productId : rsu-zgqy-cictr615
                 * addr : 重庆市渝北区礼环北路
                 * lng : 106.5328069847255
                 * lat : 29.714535309796453
                 * onlineState : 1
                 * crossName : 国博智能汽车体验中心西南门
                 */

                private String deviceId;
                private String deviceCode;
                private String productId;
                private String addr;
                private double lng;
                private double lat;
                private int onlineState;
                private String crossName;

                public String getDeviceId() {
                    return deviceId;
                }

                public void setDeviceId(String deviceId) {
                    this.deviceId = deviceId;
                }

                public String getDeviceCode() {
                    return deviceCode;
                }

                public void setDeviceCode(String deviceCode) {
                    this.deviceCode = deviceCode;
                }

                public String getProductId() {
                    return productId;
                }

                public void setProductId(String productId) {
                    this.productId = productId;
                }

                public String getAddr() {
                    return addr;
                }

                public void setAddr(String addr) {
                    this.addr = addr;
                }

                public double getLng() {
                    return lng;
                }

                public void setLng(double lng) {
                    this.lng = lng;
                }

                public double getLat() {
                    return lat;
                }

                public void setLat(double lat) {
                    this.lat = lat;
                }

                public int getOnlineState() {
                    return onlineState;
                }

                public void setOnlineState(int onlineState) {
                    this.onlineState = onlineState;
                }

                public String getCrossName() {
                    return crossName;
                }

                public void setCrossName(String crossName) {
                    this.crossName = crossName;
                }
            }

            public static class HVMSGBean {
                /**
                 * sid : 123321
                 * timeStamp : 1689847572843
                 * sourceType : 6
                 * speed : 37
                 * heading : 33.15
                 * pos : {"elevation":232.899994,"lat":29.722667,"lon":106.535269}
                 * roadInfo : {"nodeId":{"region":5000,"id":132},"speedLimit":70,"distance":10.5,"arrivalTime":3.3}
                 */

                private String sid;
                private long timeStamp;
                private int sourceType;
                private int speed;
                private double heading;
                private PosBean pos;
                private RoadInfoBean roadInfo;

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

                public int getSpeed() {
                    return speed;
                }

                public void setSpeed(int speed) {
                    this.speed = speed;
                }

                public double getHeading() {
                    return heading;
                }

                public void setHeading(double heading) {
                    this.heading = heading;
                }

                public PosBean getPos() {
                    return pos;
                }

                public void setPos(PosBean pos) {
                    this.pos = pos;
                }

                public RoadInfoBean getRoadInfo() {
                    return roadInfo;
                }

                public void setRoadInfo(RoadInfoBean roadInfo) {
                    this.roadInfo = roadInfo;
                }

                public static class PosBean {
                    /**
                     * elevation : 232.899994
                     * lat : 29.722667
                     * lon : 106.535269
                     */

                    private double elevation;
                    private double lat;
                    private double lon;

                    public double getElevation() {
                        return elevation;
                    }

                    public void setElevation(double elevation) {
                        this.elevation = elevation;
                    }

                    public double getLat() {
                        return lat;
                    }

                    public void setLat(double lat) {
                        this.lat = lat;
                    }

                    public double getLon() {
                        return lon;
                    }

                    public void setLon(double lon) {
                        this.lon = lon;
                    }
                }

                public static class RoadInfoBean {
                    /**
                     * nodeId : {"region":5000,"id":132}
                     * speedLimit : 70
                     * distance : 10.5
                     * arrivalTime : 3.3
                     */

                    private NodeIdBeanX nodeId;
                    private int speedLimit;
                    private double distance;
                    private double arrivalTime;

                    public NodeIdBeanX getNodeId() {
                        return nodeId;
                    }

                    public void setNodeId(NodeIdBeanX nodeId) {
                        this.nodeId = nodeId;
                    }

                    public int getSpeedLimit() {
                        return speedLimit;
                    }

                    public void setSpeedLimit(int speedLimit) {
                        this.speedLimit = speedLimit;
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

                    public static class NodeIdBeanX {
                        /**
                         * region : 5000
                         * id : 132
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

            public static class TrafficLightBean {
                /**
                 * eventStr : GLOSA
                 * distance : 10.5
                 * arrivalTime : 3.3
                 * light : [{"remoteId":{"region":5000,"id":134},"dir":1,"pr":1,"color":5,"time":24,"speedDown":15.6,"speedUp":20.6,"rlvwFlag":1,"currDir":1}]
                 */

                private String eventStr;
                private double distance;
                private double arrivalTime;
                private List<LightBean> light;

                public String getEventStr() {
                    return eventStr;
                }

                public void setEventStr(String eventStr) {
                    this.eventStr = eventStr;
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

                public static class LightBean {
                    /**
                     * remoteId : {"region":5000,"id":134}
                     * dir : 1
                     * pr : 1
                     * color : 5
                     * time : 24
                     * speedDown : 15.6
                     * speedUp : 20.6
                     * rlvwFlag : 1
                     * currDir : 1
                     */

                    private RemoteIdBean remoteId;
                    private int dir;
                    private int pr;
                    private int color;
                    private int time;
                    private double speedDown;
                    private double speedUp;
                    private int rlvwFlag;
                    private int currDir;

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

                    public double getSpeedDown() {
                        return speedDown;
                    }

                    public void setSpeedDown(double speedDown) {
                        this.speedDown = speedDown;
                    }

                    public double getSpeedUp() {
                        return speedUp;
                    }

                    public void setSpeedUp(double speedUp) {
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

                    public static class RemoteIdBean {
                        /**
                         * region : 5000
                         * id : 134
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

            public static class RSIEventBean {
                /**
                 * eventStr : IVS
                 * rsiId :
                 * alertType : 1
                 * distance : 10.5
                 * arrivalTime : 3.3
                 * eventValue : 20
                 * alertRadius : 0.1
                 * pos : {"lon":106.54388,"lat":29.729151}
                 * description : 6e6f2064616e6765726f7573
                 * sourcePlatform : v2x-information
                 */

                private String eventStr;
                private String rsiId;
                private int alertType;
                private double distance;
                private double arrivalTime;
                private int eventValue;
                private double alertRadius;
                private PosBeanX pos;
                private String description;
                private String sourcePlatform;

                public String getEventStr() {
                    return eventStr;
                }

                public void setEventStr(String eventStr) {
                    this.eventStr = eventStr;
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

                public double getArrivalTime() {
                    return arrivalTime;
                }

                public void setArrivalTime(double arrivalTime) {
                    this.arrivalTime = arrivalTime;
                }

                public int getEventValue() {
                    return eventValue;
                }

                public void setEventValue(int eventValue) {
                    this.eventValue = eventValue;
                }

                public double getAlertRadius() {
                    return alertRadius;
                }

                public void setAlertRadius(double alertRadius) {
                    this.alertRadius = alertRadius;
                }

                public PosBeanX getPos() {
                    return pos;
                }

                public void setPos(PosBeanX pos) {
                    this.pos = pos;
                }

                public String getDescription() {
                    return description;
                }

                public void setDescription(String description) {
                    this.description = description;
                }

                public String getSourcePlatform() {
                    return sourcePlatform;
                }

                public void setSourcePlatform(String sourcePlatform) {
                    this.sourcePlatform = sourcePlatform;
                }

                public static class PosBeanX {
                    /**
                     * lon : 106.54388
                     * lat : 29.729151
                     */

                    private double lon;
                    private double lat;

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
                }
            }

            public static class ParkingViolationEventBean {
                /**
                 * sourceType : 1
                 * sourceId : 1
                 * areaName : xxx大道-xxx大道
                 * parkingMin : 5
                 * uuid : uuid
                 * startTime : 168101010101111
                 */

                private int sourceType;
                private int sourceId;
                private String areaName;
                private int parkingMin;
                private String uuid;
                private long startTime;

                public int getSourceType() {
                    return sourceType;
                }

                public void setSourceType(int sourceType) {
                    this.sourceType = sourceType;
                }

                public int getSourceId() {
                    return sourceId;
                }

                public void setSourceId(int sourceId) {
                    this.sourceId = sourceId;
                }

                public String getAreaName() {
                    return areaName;
                }

                public void setAreaName(String areaName) {
                    this.areaName = areaName;
                }

                public int getParkingMin() {
                    return parkingMin;
                }

                public void setParkingMin(int parkingMin) {
                    this.parkingMin = parkingMin;
                }

                public String getUuid() {
                    return uuid;
                }

                public void setUuid(String uuid) {
                    this.uuid = uuid;
                }

                public long getStartTime() {
                    return startTime;
                }

                public void setStartTime(long startTime) {
                    this.startTime = startTime;
                }
            }
        }
    }
}
