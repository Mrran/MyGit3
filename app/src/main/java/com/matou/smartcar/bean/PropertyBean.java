package com.matou.smartcar.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author ranfeng
 */
public class PropertyBean implements Serializable {

    public String id;
    public String name;
    public String basicVersion;
    public String systemVersion;
    public String appVersion;
    public Integer cpuUsage;
    public Integer memUsage;
    public String appStartTime;

    public List<UserConfig> userConfig;

    public EnvState envState;

    public GpsData gpsData;
}
