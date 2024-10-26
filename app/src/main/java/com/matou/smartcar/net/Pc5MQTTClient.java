package com.matou.smartcar.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.elvishew.xlog.XLog;
import com.google.gson.reflect.TypeToken;
import com.matou.smartcar.bean.BaseBean;
import com.matou.smartcar.bean.BsmReq;
import com.matou.smartcar.bean.DataInfoBean;
import com.matou.smartcar.bean.DeviceInfo;
import com.matou.smartcar.bean.GpsData;
import com.matou.smartcar.bean.ParkingBean;
import com.matou.smartcar.bean.PropertyUploadBean;
import com.matou.smartcar.bean.RTKInfo;
import com.matou.smartcar.bean.RsiBean;
import com.matou.smartcar.bean.RsuInfoBean;
import com.matou.smartcar.bean.SrcTirePressInfo;
import com.matou.smartcar.event.ControlEvent;
import com.matou.smartcar.event.DeviceInfoEvent;
import com.matou.smartcar.event.MessageEvent;
import com.matou.smartcar.event.ParkingEvent;
import com.matou.smartcar.event.RTKInfoEvent;
import com.matou.smartcar.event.StatusEvent;
import com.matou.smartcar.event.TirePressEvent;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.util.AppUtils;
import com.matou.smartcar.util.GsonUtil;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Pc5MQTTClient {
    private static final int MSG_TYPE_RUNNINGDATA = 100000;
    private static final int MSG_TYPE_TPMS = 100001;
    private static final int MSG_TYPE_DEVICEINFO = 100002;
    private static final int MSG_TYPE_RTK = 100003;

    private static final int MSG_TYPE_UU = 100004;

    public final String TAG = Pc5MQTTClient.class.getSimpleName();
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mMqttConnectOptions;
    //    public String mHOST = "tcp://192.168.0.77:61613";//服务器地址（协议+地址+端口号）
//    public String mHOST = "tcp://192.168.8.200:1883";//服务器地址（协议+地址+端口号）
//    public String mHOST = "tcp://192.168.20.202:1883";//服务器地址（协议+地址+端口号）
    public String mHOST = AppUtils.getServerIp();//服务器地址（协议+地址+端口号）
    //    public String mHOST = "tcp://192.168.9.1:1883";//服务器地址（协议+地址+端口号）
    public String USERNAME = AppUtils.getUserName();//用户名‘’
    public String PASSWORD = AppUtils.getPwd();//密码
    //         public String USERNAME = "admin";//用户名
//    public String PASSWORD = "public";//密码
    /*************可能没有反斜杠**************/
    public static String[] SUB_TOPIC = {"/obu/RunningData", "caeri/v1/auto/TPMS", "caeri/v1/devmon/DeviceInfo", "caeri/v1/devmon/RTK", "caeri/v1/bridge/trafficData"};
    //public static String[] SUB_TOPIC = {"/obu/RunningData", "/caeri/v1/auto/TPMS"};
//    public static String[] PUBLISH_TOPIC = {"/obu/BaseInfoData","/obu/RunningData","/obu/BusInfoData"};//订阅主题

    public static String PUBLIC_TOPIC = "/obu/simulink";  //我发布出去的主题
    public String mClientId = AppUtils.getDeviceId();
    private Context context;

    private Handler threadHandler;

    private final List<String> parkingCache = new ArrayList<>();

    private final static Type RsiType = new TypeToken<List<RsiBean>>() {}.getType();

    private final static Type parkingType = new TypeToken<List<ParkingBean>>() {}.getType();

    private final Handler connectPc5Handle = new Handler();

    public Pc5MQTTClient(Context context) {
        this.context = context;
        init();
    }

    public static Pc5MQTTClient getInstance(Context context) {
        if (Pc5MQTTClient.Pc5MQTTClientHolder.INSTANCE == null) {
            Pc5MQTTClient.Pc5MQTTClientHolder.INSTANCE = new Pc5MQTTClient(context);
        }
        return Pc5MQTTClient.Pc5MQTTClientHolder.INSTANCE;
    }

    private static class Pc5MQTTClientHolder {
        private static Pc5MQTTClient INSTANCE;

    }


    public void start() {
        if(!TextUtils.isEmpty(mClientId)){
            XLog.w("uu-mqtt start");
            doClientConnection();
            connectPc5Handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    XLog.w("pc5-mqtt connectPc5Handle doClientConnection");
                    doClientConnection();
                    connectPc5Handle.postDelayed(this, 1000 * 10);
                }
            }, 1000 * 10);

        }

    }

    public void stop() {
        try {
            if (mqttAndroidClient != null) {
                mqttAndroidClient.disconnect(); //断开连接
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //private final BsmReq bsmReq = new BsmReq();

    private final GpsData gpsData = new GpsData();

    private long preModelTime;
    //private long preUuTime;

    private void init() {

        HandlerThread thread = new HandlerThread("pc5-Mqtt");
        thread.start();
        threadHandler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_TYPE_RUNNINGDATA) {
                    MqttMessage message = (MqttMessage) msg.obj;
                    String data = new String(message.getPayload());
                    XLog.w("ps5-mqtt handleMessage = " + System.currentTimeMillis() + ", seq = " + msg.arg1 + ", v2x_msg = " + data);
                    if(data.contains(":\"BUS\"")){
                        return;
                    }

                    try {
                        BaseBean<DataInfoBean> baseBean = GsonUtil.gson.fromJson(data, TYPE);
                        //BaseBean<DataInfoBean> baseBean = JSON.parseObject(data, typeRef);
                        if(baseBean != null && baseBean.getData() != null){
                            baseBean.getData().setSeq(msg.arg1);
                            EventBus.getDefault().post(new MessageEvent(baseBean.getData()));


                            long nowTime = System.currentTimeMillis();

                            /*bsmReq.reset();
                            if(nowTime - preUuTime > 300){
                                if(!TextUtils.isEmpty(GlobalVariables.token)){
                                    DataInfoBean.HVMSGBean hvmsg = baseBean.getData().getHVMSG();
                                    if(hvmsg != null && hvmsg.getPos() != null){
                                        DataInfoBean.HVMSGBean.PosBean pos = hvmsg.getPos();
                                        bsmReq.setDirection(hvmsg.getHeading()).setElevation(pos.getElevation()).setLatitude(pos.getLatitude()).setLongitude(pos.getLongitude()).setSpeed(hvmsg.getSpeed());
                                        UuMQTTClient.getInstance(context).uploadBSM(GsonUtil.beanToJson(bsmReq));
                                    }
                                }
                                preUuTime = nowTime;
                            }*/



                            if(nowTime - preModelTime > 2000){
                                DataInfoBean.HVMSGBean hvmsg = baseBean.getData().getHVMSG();
                                if (hvmsg != null && hvmsg.getPos() != null) {
                                    GlobalVariables.obuId = hvmsg.getSid();
                                    DataInfoBean.HVMSGBean.PosBean pos = hvmsg.getPos();
                                    gpsData.reset();
                                    gpsData.setDirection(hvmsg.getHeading()).setElevation(pos.getElevation()).setLatitude(pos.getLatitude()).setLongitude(pos.getLongitude()).setSpeed(hvmsg.getSpeed());
                                    PropertyUploadBean propertyUploadBean = new PropertyUploadBean();
                                    propertyUploadBean.getProperties().gpsData = gpsData;
                                    ModelMQTTClient.getInstance(context).propertyUpload(propertyUploadBean);
                                }
                                preModelTime = nowTime;
                            }


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (msg.what == MSG_TYPE_TPMS) {
                    MqttMessage message = (MqttMessage) msg.obj;
                    String data = new String(message.getPayload());
                    XLog.w("ps5-mqtt handleMessage = " + System.currentTimeMillis() + ", seq = " + msg.arg1 + ", v2x_msg = " + data);

                    try {
                        SrcTirePressInfo srcTirePressInfo = GsonUtil.gson.fromJson(data, SrcTirePressInfo.class);

                        if(srcTirePressInfo != null) {
                            EventBus.getDefault().post(new TirePressEvent(srcTirePressInfo));
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if (msg.what == MSG_TYPE_DEVICEINFO) {
                    MqttMessage message = (MqttMessage) msg.obj;
                    String data = new String(message.getPayload());
                    XLog.w("ps5-mqtt handleMessage = " + System.currentTimeMillis() + ", seq = " + msg.arg1 + ", v2x_msg = " + data);
                    try {
                        GlobalVariables.deviceInfo = GsonUtil.gson.fromJson(data, DeviceInfo.class);
                        EventBus.getDefault().post(new DeviceInfoEvent());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if (msg.what == MSG_TYPE_RTK) {
                    MqttMessage message = (MqttMessage) msg.obj;
                    String data = new String(message.getPayload());
                    XLog.w("ps5-mqtt handleMessage = " + System.currentTimeMillis() + ", seq = " + msg.arg1 + ", v2x_msg = " + data);
                    try {
                        GlobalVariables.rtkInfo = GsonUtil.gson.fromJson(data, RTKInfo.class);
                        EventBus.getDefault().post(new RTKInfoEvent());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if (msg.what == MSG_TYPE_UU) {
                    MqttMessage message = (MqttMessage) msg.obj;
                    String receiveStr = new String(message.getPayload());
                    XLog.w("ps5-mqtt handleMessage = " + System.currentTimeMillis() + ", seq = " + msg.arg1 + ", v2x_msg = " + receiveStr);

                    // 处理交通管制
                    int rsiIndex = receiveStr.indexOf("RSIEvent");
                    if(GlobalVariables.traffControlEnable && rsiIndex > -1){
                        String substring = receiveStr.substring(rsiIndex);
                        int leftIndex = substring.indexOf('[');
                        int rightIndex = substring.indexOf(']');
                        String rsiStr = substring.substring(leftIndex, rightIndex + 1);
                        List<RsiBean> rsiBeanList = GsonUtil.gson.fromJson(rsiStr, RsiType);

                        if(rsiBeanList != null && rsiBeanList.size() > 0){
                            Optional<RsiBean> first = rsiBeanList.stream().filter(rsiBean -> rsiBean.getSourcePlatform().equals("CloudControlManage") && (rsiBean.getAlertType() == 199 || rsiBean.getAlertType() == 501 || rsiBean.getAlertType() == 699)).findFirst();

                            RsuInfoBean rsuInfoBean = getRsuInfo(receiveStr);
                            if(rsuInfoBean != null && first.isPresent()){
                                RsiBean rsiBean = first.get();
                                boolean isAdd = addToIdCache(rsiBean.getRsiId());
                                if(isAdd){
                                    EventBus.getDefault().post(new ControlEvent(rsiBean, rsuInfoBean));
                                }
                            }

                        }

                    }

                    // 处理禁停
                    if(GlobalVariables.illagelParkingEnable2 &&  GlobalVariables.carStatus.getMoveStatus() == 1 && GlobalVariables.carStatus.getParkingTime() > 0){
                        int parkingIndex = receiveStr.indexOf("parkingViolationEvent");
                        if(parkingIndex > -1){
                            String substring = receiveStr.substring(parkingIndex);
                            int leftIndex = substring.indexOf('[');
                            int rightIndex = substring.indexOf(']');
                            String parkingStr = substring.substring(leftIndex, rightIndex + 1);
                            List<ParkingBean> parkingBeanList = GsonUtil.gson.fromJson(parkingStr, parkingType);

                            if(parkingBeanList != null && parkingBeanList.size() > 0){
                                parkingBeanList.sort((o1, o2) -> o2.getSourceType() < o1.getSourceType() ? 1 : -1);
                                ParkingBean parkingBean = parkingBeanList.get(0);

                                // mock
                                // parkingBean.setParkingMin(0);

                                boolean isAdd = addToIdCache(parkingBean.getUuid());
                                if(isAdd){
                                    XLog.w("uu-mqtt parkingMsg receiveStr = " + receiveStr);
                                    EventBus.getDefault().post(new ParkingEvent(parkingBean));
                                }
                            }
                        }
                    }


                }
            }
        };


        //服务器地址（协议+地址+端口号）
        //serverURI = "tcp://192.168.28.193:8080";
        XLog.e("====> ps5-mqtt ip = " + mHOST);
        mqttAndroidClient = new MqttAndroidClient(context, mHOST, mClientId);

        //设置监听订阅消息的回调
        mqttAndroidClient.setCallback(mqttCallback);
        mMqttConnectOptions = new MqttConnectOptions();

        //设置是否清除缓存
        mMqttConnectOptions.setCleanSession(true);

        //设置超时时间，单位：秒
        mMqttConnectOptions.setConnectionTimeout(10);

        //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(20);

        //设置用户名
        mMqttConnectOptions.setUserName(USERNAME);

        //设置密码
        mMqttConnectOptions.setPassword(PASSWORD.toCharArray());

        // last will message
        String message = "{\"terminal_uid\":\"" + mClientId + "\"}";
        String topic = SUB_TOPIC[0];

        //消息质量
        int qos = 0;
        boolean retained = false;
        // 最后的遗嘱
        try {
            mMqttConnectOptions.setWill(topic, message.getBytes(), qos, retained);
        } catch (Exception e) {
            XLog.w("ps5-mqtt Exception Occured" + e);
            iMqttActionListener.onFailure(null, e);
        }
    }


    private boolean addToIdCache(String id){
        boolean ret;
        Optional<String> first = parkingCache.stream().filter(uuid -> uuid.equals(id)).findFirst();

        if(!first.isPresent()){
            if(parkingCache.size() > 20){
                parkingCache.remove(0);
            }
            parkingCache.add(id);
            ret = true;
        }else {
            ret = false;
        }
        return ret;
    }

    private RsuInfoBean getRsuInfo(String receiveStr) {
        int rsuIndex = receiveStr.indexOf("rsuInfo");
        if(rsuIndex > -1) {
            String substring = receiveStr.substring(rsuIndex);
            int leftIndex = substring.indexOf('{');
            int rightIndex = substring.indexOf('}');
            String rsiStr = substring.substring(leftIndex, rightIndex + 1);
            return GsonUtil.gson.fromJson(rsiStr, RsuInfoBean.class);
        }
        return null;
    }


    /**
     * 连接MQTT服务器
     * 道路违停
     * 声音人性化一些
     */
    private void doClientConnection() {
        if (mqttAndroidClient == null) {
            return;
        }
        if (!mqttAndroidClient.isConnected() && isConnectIsNomarl()) {
            try {
                mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
            } catch (MqttException e) {
                Log.d("启动检测", "init: 链接失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            XLog.w("ps5-mqtt 当前网络名称：" + name);
            return true;
        } else {
            XLog.w("ps5-mqtt 没有可用网络");
            return false;
        }
    }


    private final IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            XLog.w("ps5-mqtt onSuccess ");
            GlobalVariables.pc5ConnStatus = 2;
            EventBus.getDefault().post(new StatusEvent());
            try {
                final int[] qos = {0, 0, 0, 0, 0};
//                handler.post(mRunnable);
                mqttAndroidClient.subscribe(SUB_TOPIC, qos);//订阅主题，参数：主题、服务质量
            } catch (MqttException e) {
                GlobalVariables.pc5DataStatus = 0;
                EventBus.getDefault().post(new StatusEvent());
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            XLog.w("ps5-mqtt onFailure " + arg1.getMessage());
            GlobalVariables.pc5ConnStatus = GlobalVariables.wifiConnect ? 1 : 0;
            GlobalVariables.pc5ConnDesc = arg1.getMessage();
            GlobalVariables.pc5DataStatus = 0;
            EventBus.getDefault().post(new StatusEvent());
        }
    };


    private final static Type TYPE = new TypeToken<BaseBean<DataInfoBean>>() {
    }.getType();

    private int seq;

    private final MqttCallback mqttCallback = new MqttCallback() {

        private long lastTime;

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            long nowTime = System.currentTimeMillis();
            XLog.w("ps5-mqtt messageArrived = " + nowTime + ", seq = " + seq);

//            long l = nowTime - lastTime;
//            if (l < 200) {
//                return;
//            }
//            lastTime = nowTime;

            boolean send = true;
            if (TextUtils.equals(topic, SUB_TOPIC[0]) || TextUtils.equals(topic, SUB_TOPIC[1]) || TextUtils.equals(topic, SUB_TOPIC[2]) || TextUtils.equals(topic, SUB_TOPIC[3]) || TextUtils.equals(topic, SUB_TOPIC[4])){
                Message msg = Message.obtain();
                int what = 0;
                if(TextUtils.equals(topic, SUB_TOPIC[0])){
                    what = MSG_TYPE_RUNNINGDATA;
                }else if(TextUtils.equals(topic, SUB_TOPIC[1])){
                    what = MSG_TYPE_TPMS;
                }else if(TextUtils.equals(topic, SUB_TOPIC[2])){
                    what = MSG_TYPE_DEVICEINFO;
                    // 设备版本信息只处理一次
                    if(GlobalVariables.deviceInfo != null){
                        send = false;
                    }
                }else if(TextUtils.equals(topic, SUB_TOPIC[3])){
                    what = MSG_TYPE_RTK;
                }else if(TextUtils.equals(topic, SUB_TOPIC[4])){
                    what = MSG_TYPE_UU;
                }
                msg.what = what;
                msg.obj = message;
                msg.arg1 = seq;
                if(send){
                    threadHandler.sendMessage(msg);
                }
                seq++;
                if (GlobalVariables.pc5DataStatus != 1) {
                    GlobalVariables.pc5DataStatus = 1;
                    EventBus.getDefault().post(new StatusEvent());
                }
            }


        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            XLog.w("ps5-mqtt connectionLost 连接断开 ");
            GlobalVariables.pc5ConnStatus = GlobalVariables.wifiConnect ? 1 : 0;
            GlobalVariables.pc5ConnDesc = arg0.getMessage();
            GlobalVariables.pc5DataStatus = 0;
            EventBus.getDefault().post(new StatusEvent());
        }
    };

}
