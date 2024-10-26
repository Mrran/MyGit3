package com.matou.smartcar.global;

import com.amap.api.navi.AmapNaviParams;
import com.matou.smartcar.bean.CarStatus;
import com.matou.smartcar.bean.DeviceInfo;
import com.matou.smartcar.bean.ObuInfo;
import com.matou.smartcar.bean.ParkingBean;
import com.matou.smartcar.bean.PropertyBean;
import com.matou.smartcar.bean.RTKInfo;
import com.matou.smartcar.manager.WifiPingManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ranfeng
 */
public class GlobalVariables {


    /**
     * 窗口优先级，数值越小优先级越高
     */
    public final static int WND_TYPE_0 = 0; // 全局速度
    public final static int WND_TYPE_1 = 1; // 场景事件
    public final static int WND_TYPE_2 = 2; // APP内事件
    public final static int WND_TYPE_3 = 3; // 红绿灯

    /**
     * 全局窗口显示状态
     */
    public static Map<Integer, Boolean> wndShowMap = new HashMap<>();


    /**
     * 事件语音、动画 持续事件，超过该事件后就重新检测事件
     */
    public static final int SHOW_DURATION = 5000;

    public static final int SHOW_SPEED_DURATION = 15000;

    public static final int SHOW_RSI_DURATION = 5000;


    public static final int RSI_LEVEL_10 = 10;
    public static final int RSI_LEVEL_9 = 9;
    public static final int RSI_LEVEL_8 = 8;
    public static final int RSI_LEVEL_7 = 7;
    public static final int RSI_LEVEL_6 = 6;
    public static final int RSI_LEVEL_5 = 5;
    public static final int RSI_LEVEL_4 = 4;
    public static final int RSI_LEVEL_3 = 3;
    public static final int RSI_LEVEL_2 = 2;
    public static final int RSI_LEVEL_1 = 1;
    public static final int RSI_LEVEL_0 = 0;


    public static CarStatus carStatus = new CarStatus();
    public static ParkingBean parkingBean;
    /**
     * 设备信息
     */
    public static DeviceInfo deviceInfo;
    public static RTKInfo rtkInfo;
    public static boolean startNavi;
    public static AmapNaviParams amapNaviParams;
    public static WifiPingManager.PingStatus pingStatus = new WifiPingManager.PingStatus();
    public static boolean remWifi = true;


    public List<PropertyBean> propertyBeanList = new ArrayList<>();

    public final static List<ObuInfo> OBU_INFO_LIST = new ArrayList<>();
    public static String token;

    public static String swVer;

    static {
        ObuInfo huali = new ObuInfo("huali", "192.168.1.10", "VMAST","hl20170817");
        ObuInfo xingyun = new ObuInfo("xingyun", "192.168.10.224", "CWAVEA","nebulalink");
        ObuInfo xidi = new ObuInfo("xidi", "192.168.2.10", "OBU3","12345678");
        // 金溢密码不统一，暂时先移除
        //ObuInfo jinyi = new ObuInfo("jinyi", "192.168.110.1", "Genvict","cdgvmymm");
        OBU_INFO_LIST.add(huali);
        OBU_INFO_LIST.add(xingyun);
        OBU_INFO_LIST.add(xidi);
        //OBU_INFO_LIST.add(jinyi);
    }

    public final static String LAUNCHER_NAME = "com.zqc.launcher.HomeActivity";

    public static boolean wifiConnect;

    /**
     * pc5连接状态
     * 0：wifi和pc5都没连接
     * 1：wifi已连接，pc5未连接
     * 2：是wifi和pc5都连接
     */
    public static int pc5ConnStatus;

    /**
     * pc5连接状态说明
     */
    public static String pc5ConnDesc;

    /**
     * pc5数据状态
     * 0：异常
     * 1：正常
     */
    public static int pc5DataStatus;

    /**
     * uu连接状态
     * 0：未连接
     * 1：已连接
     */
    public static int uuConnStatus;

    /**
     * uu连接状态说明
     */
    public static String uuConnDesc;

    /**
     * uu数据状态
     * 0：异常
     * 1：正常
     */
    public static int uuDataStatus;

    /**
     * model连接状态
     * 0：未连接
     * 1：已连接
     */
    public static int modelConnStatus;

    /**
     * model连接状态说明
     */
    public static String modelConnDesc;

    /**
     * model数据状态
     * 0：异常
     * 1：正常
     */
    public static int modelDataStatus;

    public static String obuId;

    public final static String URL_FEEDBACK = "http://cq-v2x.ljzhct.com:9000/h5/#/pages/introduction/index?rvmDeviceId=";

    public static boolean fcwEnable;
    public static boolean bsmEnable;
    public static boolean icwEnable;
    public static boolean pcrEnable;
    public static boolean rlvwEnable;
    public static boolean traffControlEnable;
    public static boolean greenWaveEnable;
    public static boolean traffInfoEnable;
    public static boolean limitSpeedEnable;
    public static boolean specialVehicleEnable;
    public static boolean illagelParkingEnable2;
    public static boolean mixLightEnable;


    public static boolean hasTyconfig;
    /**
     * 0表示无效
     */
    public static double lowTyVal = 0;

    public static double highTyVal = 0;

    public final static String OBU_VERSION_SUPPORT_LANE_MAP= "3.0.9";

    /**
     * 显示优先级：手动展示胎压，场景，违停，交管，自动展示胎压
     */
    public final static int SHOW_MANU_TIRE_LEVEL = 12;
    public final static int SHOW_SCENE_LEVEL = 10;
    public final static int SHOW_PARKING_LEVEL = 8;
    public final static int SHOW_CONTROLL_LEVEL = 6;
    public final static int SHOW_AUTO_TIRE_LEVEL = 4;
    public final static int SHOW_NOTHING_LEVEL = 0;

    public static int currSHowLevel = SHOW_NOTHING_LEVEL;


}
