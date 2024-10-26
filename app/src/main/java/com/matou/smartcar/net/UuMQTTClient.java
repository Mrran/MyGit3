package com.matou.smartcar.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;

import com.elvishew.xlog.XLog;
import com.google.gson.reflect.TypeToken;
import com.matou.smartcar.bean.ParkingBean;
import com.matou.smartcar.bean.RsiBean;
import com.matou.smartcar.bean.RsuInfoBean;
import com.matou.smartcar.event.ControlEvent;
import com.matou.smartcar.event.ParkingEvent;
import com.matou.smartcar.event.StatusEvent;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.util.DeviceUtil;
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

public class UuMQTTClient {
    private static final String UU_MQTT_URL = "tcp://222.180.171.150:21883";
    public final String TAG = UuMQTTClient.class.getSimpleName();
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mMqttConnectOptions;
    public static String[] BSM_TOPIC_REPLY = {"/demoApp/" + DeviceUtil.getDeviceId() +"/bsm/reply"};

    private final static String BSM_TOPIC = "/demoApp/" + DeviceUtil.getDeviceId() + "/bsm";
    public String mClientId = GlobalVariables.token;
    private final Context context;

    private final List<String> parkingCache = new ArrayList<>();

    private final Handler connectUuHandle = new Handler();

    public UuMQTTClient(Context context) {
        this.context = context;
        init();
    }

    public static UuMQTTClient getInstance(Context context) {
        if (UuMQTTClientHolder.INSTANCE == null) {
            UuMQTTClientHolder.INSTANCE = new UuMQTTClient(context);
        }
        return UuMQTTClientHolder.INSTANCE;
    }

    private static class UuMQTTClientHolder {
        private static UuMQTTClient INSTANCE;

    }

    public void stop() {
        try {
            if (mqttAndroidClient != null) {
                XLog.w("uu-mqtt stop 主动断开");
                mqttAndroidClient.disconnect(); //断开连接
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void init() {
        XLog.w("uu-mqtt init");

        //服务器地址（协议+地址+端口号）
        mqttAndroidClient = new MqttAndroidClient(context, UU_MQTT_URL, mClientId);

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
//        mMqttConnectOptions.setUserName(USERNAME);
//
//        //设置密码
//        mMqttConnectOptions.setPassword(PASSWORD.toCharArray());

        String message = "{\"terminal_uid\":\"" + mClientId + "\"}";
        String topic = BSM_TOPIC_REPLY[0];

        try {
            mMqttConnectOptions.setWill(topic, message.getBytes(), 0, false);
        } catch (Exception e) {
            iMqttActionListener.onFailure(null, e);
            e.printStackTrace();
        }
    }

    public void start(){
        if(!TextUtils.isEmpty(mClientId)){
            XLog.w("uu-mqtt start");
            doClientConnection();

            connectUuHandle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    XLog.w("uu-mqtt connectUuHandle doClientConnection");
                    doClientConnection();
                    connectUuHandle.postDelayed(this, 1000 * 10);
                }
            }, 1000 * 10);

        }

    }


    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        if (mqttAndroidClient == null) {
            return;
        }
        if (!mqttAndroidClient.isConnected() && isConnectIsNomarl()) {
            try {
                XLog.w("uu-mqtt doClientConnection");
                mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
            } catch (MqttException e) {
                XLog.e("uu-mqtt init: 链接失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        XLog.w("uu-mqtt isConnectIsNomarl");
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            XLog.i("uu-mqtt 当前网络名称：" + name);
            return true;
        } else {
            XLog.i("uu-mqtt 没有可用网络");
            return false;
        }
    }


    /**
     * 连接状态监听
     */
    private final IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            XLog.e("uu-mqtt 连接成功 ");
            GlobalVariables.uuConnStatus = 1;
            EventBus.getDefault().post(new StatusEvent());
            try {
                final int[] qos = {0};
                mqttAndroidClient.subscribe(BSM_TOPIC_REPLY, qos);//订阅主题，参数：主题、服务质量
            } catch (MqttException e) {
                e.printStackTrace();
                GlobalVariables.uuDataStatus = 0;
                EventBus.getDefault().post(new StatusEvent());
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            XLog.e("uu-mqtt 连接失败 " + arg1.getMessage());
            GlobalVariables.uuConnStatus = 0;
            GlobalVariables.uuConnDesc= arg1.getMessage();
            GlobalVariables.uuDataStatus = 0;
            EventBus.getDefault().post(new StatusEvent());
        }
    };

    private final static Type parkingType = new TypeToken<List<ParkingBean>>() {}.getType();
    private final static Type RsiType = new TypeToken<List<RsiBean>>() {}.getType();

    private final MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            if (TextUtils.equals(topic, BSM_TOPIC_REPLY[0])) {
                String receiveStr = new String(message.getPayload());
                XLog.w("uu-mqtt messageArrived : " + receiveStr);

                if (GlobalVariables.uuDataStatus != 1) {
                    GlobalVariables.uuDataStatus = 1;
                    EventBus.getDefault().post(new StatusEvent());
                }

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

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
        }

        @Override
        public void connectionLost(Throwable arg0) {
            XLog.e("uu-mqtt 连接断开 ");
            GlobalVariables.uuConnStatus = 0;
            GlobalVariables.uuConnDesc= arg0.getMessage();
            GlobalVariables.uuDataStatus = 0;
            EventBus.getDefault().post(new StatusEvent());
        }
    };

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
     * bsm消息上传
     * @param data
     */
    private long preSendTime;

    public void uploadBSM(String data) {
        long nowSendTime = System.currentTimeMillis();
        if(nowSendTime - preSendTime < 500){
            return;
        }
        preSendTime = nowSendTime;
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                // uu违停
                // data = "{\"longitude\":106.5268837,\"latitude\":29.6459676,\"elevation\":0,\"direction\":226.71,\"speed\":0}";

                // 交通管制
                // data = "{\"longitude\":106.504726,\"latitude\":29.670657,\"elevation\":0,\"direction\":115.76,\"speed\":0}";
                XLog.w("uu-mqtt uploadBSM : " + data);
                mqttAndroidClient.publish(BSM_TOPIC, data.getBytes(), 0, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            XLog.e("uu-mqtt mqtt not connected");
            start();
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

}
