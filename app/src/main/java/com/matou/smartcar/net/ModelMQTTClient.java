package com.matou.smartcar.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;

import com.elvishew.xlog.XLog;
import com.matou.smartcar.bean.EnvState;
import com.matou.smartcar.bean.PropertyBean;
import com.matou.smartcar.bean.PropertyUploadBean;
import com.matou.smartcar.bean.TydConfigReq;
import com.matou.smartcar.bean.TydConfigResp;
import com.matou.smartcar.event.StatusEvent;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.util.AppUtils;
import com.matou.smartcar.util.DeviceUtil;
import com.matou.smartcar.util.GsonUtil;
import com.matou.smartcar.util.IdGenerator;
import com.matou.smartcar.util.MD5Utils;
import com.matou.smartcar.util.SPUtil;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ModelMQTTClient {
    /**
     * 测试环境
     */
    //private static final String MODEL_MQTT_URL = "tcp://10.130.212.243:1883";

    /**
     * 正式环境
     */
    private static final String MODEL_MQTT_URL = "tcp://222.180.171.150:1883";

    private final static String SECURE_ID = "test";
    private final static String SECURE_KEY = "test";
    private final static String PRODUCT_ID = "rvm-jlkj-k880";

    public final String TAG = ModelMQTTClient.class.getSimpleName();
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mMqttConnectOptions;
    //public static String[] BSM_TOPIC_REPLY = {"/demoApp/" + DeviceUtil.getDeviceId() +"/bsm/reply"};

    //private final static String BSM_TOPIC = "/demoApp/" + DeviceUtil.getDeviceId() + "/bsm";
    public String mClientId = DeviceUtil.getDeviceId();
    private final Context context;

    private List<String> parkingCache = new ArrayList<>();

    private String willTopic;
    private String readPropertiesTopic;
    private String readPropertiesReplyTopic;

    private String writePropertiesTopic;
    private String writePropertiesReplyTopic;

    private String reportPropertiesTopic;

    private String funcInvokeTopic;
    private String funcInvokeReplyTopic;

    /**
     * 胎压配置
     */
    private String tydConfigTopic;

    private final List<String> subsTopicList = new ArrayList<>();

    private final Handler uploadHandler = new Handler();

    private final Handler connectModelHandle = new Handler();

    public ModelMQTTClient(Context context) {
        this.context = context;
        init();
    }

    public static ModelMQTTClient getInstance(Context context) {
        if (ModelMQTTClientHolder.INSTANCE == null) {
            ModelMQTTClientHolder.INSTANCE = new ModelMQTTClient(context);
        }
        return ModelMQTTClientHolder.INSTANCE;
    }

    /**
     * 属性定时上传
     * @param propertyUploadBean
     */
    public void propertyUpload(PropertyUploadBean propertyUploadBean) {
        propertyUploadBean.setTimestamp(System.currentTimeMillis());
        propertyUploadBean.setMessageId(String.valueOf(IdGenerator.getNextId()));
        sendDataToTopic(reportPropertiesTopic, GsonUtil.gson.toJson(propertyUploadBean));
    }

    private static class ModelMQTTClientHolder {
        private static ModelMQTTClient INSTANCE;

    }

    public void stop() {
        try {
            if (mqttAndroidClient != null) {
                XLog.w("model-mqtt stop 主动断开");

                EventBus.getDefault().unregister(this);

                mqttAndroidClient.disconnect(); //断开连接
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void init() {
        XLog.w("model-mqtt init");

        initTopics();

        XLog.w("model-mqtt connect url = " + MODEL_MQTT_URL + ", mClientId = " + mClientId);
        //服务器地址（协议+地址+端口号）
        mqttAndroidClient = new MqttAndroidClient(context, MODEL_MQTT_URL, mClientId);

        //设置监听订阅消息的回调
        mqttAndroidClient.setCallback(mqttCallback);
        mMqttConnectOptions = new MqttConnectOptions();

        //设置是否清除缓存
        mMqttConnectOptions.setCleanSession(true);

        //设置超时时间，单位：秒
        mMqttConnectOptions.setConnectionTimeout(10);

        //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(20);

        long timestamp = System.currentTimeMillis();
        //设置用户名
        String username = SECURE_ID + "|" + timestamp;
        mMqttConnectOptions.setUserName(username);

        //设置密码
        String password = MD5Utils.encrypt(SECURE_ID + "|" + timestamp + "|" + SECURE_KEY);
        mMqttConnectOptions.setPassword(password.toCharArray());

        try {
            String message = "{\"deviceId\":\"" + DeviceUtil.getDeviceId() + "\"}";
            mMqttConnectOptions.setWill(willTopic, message.getBytes(), 0, false);
        } catch (Exception e) {
            iMqttActionListener.onFailure(null, e);
            e.printStackTrace();
        }

        // mock
        /*try {
            String load = "{\"timestamp\":1601196762389,\"messageId\":\"99999999\",\"deviceId\":\"3874780\",\"function\":\"tydConfig\",\"inputs\":[{\"name\":\"lowVal\",\"value\":200.5},{\"name\":\"topVal\",\"value\":300.5}]}";
            mqttCallback.messageArrived(funcInvokeTopic, new MqttMessage(load.getBytes()));
        }catch (Exception e){
            e.printStackTrace();
        }*/

    }

    /**
     * 设置主题信息
     */
    private void initTopics() {
        willTopic = "/" + PRODUCT_ID + "/" + DeviceUtil.getDeviceId() + "/logout";

        readPropertiesTopic = "/" + PRODUCT_ID + "/" + DeviceUtil.getDeviceId() + "/properties/read";
        readPropertiesReplyTopic = "/" + PRODUCT_ID + "/" + DeviceUtil.getDeviceId() + "/properties/read/reply";

        writePropertiesTopic = "/" + PRODUCT_ID + "/" + DeviceUtil.getDeviceId() + "/properties/write";
        writePropertiesReplyTopic = "/" + PRODUCT_ID + "/" + DeviceUtil.getDeviceId() + "/properties/write/reply";

        reportPropertiesTopic = "/" + PRODUCT_ID + "/" + DeviceUtil.getDeviceId() + "/properties/report";

        funcInvokeTopic = "/" + PRODUCT_ID + "/" + DeviceUtil.getDeviceId() + "/function/invoke";
        funcInvokeReplyTopic = "/" + PRODUCT_ID + "/" + DeviceUtil.getDeviceId() + "/function/invoke/reply";

        tydConfigTopic = "/" + PRODUCT_ID + "/" + DeviceUtil.getDeviceId() + "/event/tydConfig";

        //subsTopicList.add(readPropertiesTopic);
        //subsTopicList.add(writePropertiesTopic);
        //subsTopicList.add(funcInvokeTopic);
        subsTopicList.add(funcInvokeTopic);
    }

    public void start() {
        if (!TextUtils.isEmpty(mClientId)) {
            XLog.w("model-mqtt start");

            EventBus.getDefault().register(this);

            doClientConnection();

            connectModelHandle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    XLog.w("model-mqtt connectModelHandle doClientConnection");
                    doClientConnection();
                    connectModelHandle.postDelayed(this, 1000 * 10);
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
                XLog.w("model-mqtt connect");
                mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
            } catch (MqttException e) {
                XLog.e("model-mqtt init: 链接失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        XLog.w("model-mqtt isConnectIsNomarl");
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            XLog.w("model-mqtt 当前网络名称：" + name);
            return true;
        } else {
            XLog.w("model-mqtt 没有可用网络");
            return false;
        }
    }


    /**
     * 连接状态监听
     */
    private final IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            XLog.e("model-mqtt 连接成功 ");
            GlobalVariables.modelConnStatus = 1;
            EventBus.getDefault().post(new StatusEvent());

            try {
                mqttAndroidClient.subscribe(subsTopicList.toArray(new String[0]), new int[subsTopicList.size()]);//订阅主题，参数：主题、服务质量
                scheduleUpload();
            } catch (MqttException e) {
                e.printStackTrace();
                GlobalVariables.modelDataStatus = 0;
                EventBus.getDefault().post(new StatusEvent());
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            XLog.e("model-mqtt 连接失败 " + arg1.getMessage());

            GlobalVariables.modelConnStatus = 0;
            GlobalVariables.modelConnDesc= arg1.getMessage();
            GlobalVariables.modelDataStatus = 0;
            EventBus.getDefault().post(new StatusEvent());

//            1=无效协议版本
//            2=无效客户机标识
//            3=代理程序不可用
//            4=错误的用户名或密码
//            5=无权连接
//            6=意外错误
//            32000=等待来自服务器的响应时超时
//            32100=已连接客户机
//            32101=已断开客户机连接
//            32102=客户机正在断开连接
//            32103=无法连接至服务器
//            32104=客户机未连接
//            32105=指定的 SocketFactory 类型与代理程序 URI 不匹配
//            32106=SSL 配置错误
//            32107=不允许通过回调方法断开连接
//            32108=不可识别的包
//            32109=已断开连接
//            32110=已在进行连接
//            32111=客户机已关闭
//            32200=持久性已在使用中
//            32201=令牌已在使用中
//            32202=正在进行过多的发布

        }
    };

    /**
     * 获取胎压配置
     */
    private void getTydConfig() {
        XLog.w("========> getTydConfig tydConfigTopic = " + tydConfigTopic);
        sendDataToTopic(tydConfigTopic, GsonUtil.beanToJson(TydConfigReq.build()));
    }

    /**
     * 定时上传属性数据
     */
    private void scheduleUpload() {
        uploadHandler.removeCallbacksAndMessages(null);
        uploadHandler.post(new Runnable() {
            @Override
            public void run() {
                uploadHandler.postDelayed(this, 1000 * 60 * 5);
                uploadStatus();
                getTydConfig();
            }
        });
    }

    private void uploadStatus() {
        PropertyUploadBean propertyUploadBean = new PropertyUploadBean();
        PropertyBean propertyBean = propertyUploadBean.getProperties();
        propertyBean.id = DeviceUtil.getDeviceId();
        propertyBean.name = android.os.Build.MODEL;
        propertyBean.basicVersion = android.os.Build.getRadioVersion();
        // propertyBean.systemVersion = android.os.Build.VERSION.RELEASE;

        propertyBean.systemVersion = AppUtils.getProp("ro.build.display.id");

        propertyBean.appVersion = AppUtils.getVersionName();
        propertyBean.cpuUsage = (int) (AppUtils.getCpuUsage() * 100);
        propertyBean.memUsage = (int) (AppUtils.getMemUsage(context) * 100);
        propertyBean.appStartTime = SystemClock.elapsedRealtime() + "";
        propertyBean.userConfig = SPUtil.getDataList("userConfig");
        EnvState envState = new EnvState();
        envState.obuState = GlobalVariables.pc5ConnStatus;
        envState.obuMsgState = GlobalVariables.pc5DataStatus;
        envState.broker = AppUtils.getServerIp();
        envState.obuId = GlobalVariables.obuId;
        propertyBean.envState = envState;
        propertyUpload(propertyUploadBean);
    }

    private final MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) {

            String dataStr = new String(message.getPayload());
            XLog.w("model-mqtt messageArrived topic = " + topic + ", dataStr = " + dataStr);
            /**
             * 物模型暂时只处理上传接口
             */
            if (topic.equals(funcInvokeTopic)) {
                try {
                    TydConfigResp tydConfigResp = GsonUtil.gsonToBean(dataStr, TydConfigResp.class);
                    if(tydConfigResp != null && tydConfigResp.getInputs() != null && tydConfigResp.getInputs().size() > 0){
                        GlobalVariables.hasTyconfig = true;
                        List<TydConfigResp.InputsBean> inputs = tydConfigResp.getInputs();
                        inputs.forEach(inputsBean -> {
                            if("lowVal".equals(inputsBean.getName())){
                                GlobalVariables.lowTyVal = inputsBean.getValue() / 100;
                            }else if("topVal".equals(inputsBean.getName())){
                                GlobalVariables.highTyVal = inputsBean.getValue() / 100;
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
//
//                FuncInvokeBean funcInvokeBean = GsonUtil.gson.fromJson(dataStr, FuncInvokeBean.class);
//                if(funcInvokeBean == null){
//                    XLog.e("funcInvokeBean == null");
//                    return;
//                }
//
//                if(funcInvokeBean.getInputs() != null
//                    && funcInvokeBean.getInputs().size() > 0
//                    && funcInvokeBean.getInputs().get(0).getValue() != null){
//
//                    List<FuncInvokeBean.InputsBean.ValueBean.ParkingViolationEventBean> parkingBeanList = funcInvokeBean.getInputs().get(0).getValue().getParkingViolationEvent();
//                    if(parkingBeanList != null && parkingBeanList.size() > 0){
//                        parkingBeanList.sort((o1, o2) -> o2.getSourceType() < o1.getSourceType() ? 1 : -1);
//                        FuncInvokeBean.InputsBean.ValueBean.ParkingViolationEventBean parkingBean = parkingBeanList.get(0);
//                        boolean isAdd = addToIdCache(parkingBean.getUuid());
//                        if(isAdd){
//                            EventBus.getDefault().post(new ParkingEvent(parkingBean));
//                        }
//                    }
//
//                    List<FuncInvokeBean.InputsBean.ValueBean.RSIEventBean> rsiBeanList = funcInvokeBean.getInputs().get(0).getValue().getRSIEvent();
//                    if(rsiBeanList != null && rsiBeanList.size() > 0){
//                        Optional<FuncInvokeBean.InputsBean.ValueBean.RSIEventBean> first = rsiBeanList.stream().filter(rsiBean -> rsiBean.getSourcePlatform().equals("CloudControlManage") && (rsiBean.getAlertType() == 199 || rsiBean.getAlertType() == 501 || rsiBean.getAlertType() == 699)).findFirst();
//
//                        FuncInvokeBean.InputsBean.ValueBean.RsuInfoBean rsuInfoBean = funcInvokeBean.getInputs().get(0).getValue().getRsuInfo();
//                        if(rsuInfoBean != null && first.isPresent()){
//                            FuncInvokeBean.InputsBean.ValueBean.RSIEventBean rsiBean = first.get();
//                            boolean isAdd = addToIdCache(rsiBean.getRsiId());
//                            if(isAdd){
//                                EventBus.getDefault().post(new ControlEvent(rsiBean, rsuInfoBean));
//                            }
//                        }
//
//                    }
//                }
//
//                FuncInvokeReplyBean funcInvokeReplyBean = new FuncInvokeReplyBean();
//                funcInvokeReplyBean.setTimestamp(System.currentTimeMillis());
//                funcInvokeReplyBean.setMessageId(funcInvokeBean.getMessageId());
//                funcInvokeReplyBean.setOutput("success");
//                sendDataToTopic(funcInvokeReplyTopic, GsonUtil.gson.toJson(funcInvokeReplyBean));
//
//            }
//            if (topic.equals(readPropertiesTopic)) {
//                ReadBean readBean = GsonUtil.gson.fromJson(dataStr, ReadBean.class);
//                ReadReplyBean readReplyBean = new ReadReplyBean();
//                if (readBean == null) {
//                    XLog.e("readBean == null");
//                    return;
//                }
//
//                readReplyBean.setMessageId(readBean.getMessageId());
//
//                List<String> properties = readBean.getProperties();
//                if (properties == null || properties.size() < 1) {
//                    readReplyBean.setSuccess(false);
//                    readReplyBean.setCode("1000");
//                    readReplyBean.setMessage("下发属性列表为空");
//                    sendDataToTopic(readPropertiesReplyTopic, GsonUtil.gson.toJson(readReplyBean));
//                    return;
//                }
//
//                readReplyBean.setSuccess(true);
//
//                for (int i = 0; i < properties.size(); i++) {
//                    String property = properties.get(i);
//                    PropertyBean propertyBean = readReplyBean.getProperty();
//                    try {
//                        if ("id".equals(property)) {
//                            propertyBean.id = DeviceUtil.getDeviceId();
//                        } else if ("name".equals(property)) {
//                            propertyBean.name = android.os.Build.MODEL;
//                        } else if ("basicVersion".equals(property)) {
//                            propertyBean.basicVersion = android.os.Build.getRadioVersion();
//                        } else if ("systemVersion".equals(property)) {
//                            propertyBean.systemVersion = android.os.Build.VERSION.RELEASE;
//                        } else if ("appVersion".equals(property)) {
//                            propertyBean.appVersion = AppUtils.getVersionName();
//                        } else if ("cpuUsage".equals(property)) {
//                            propertyBean.cpuUsage = (int) (AppUtils.getCpuUsage() * 100);
//                        } else if ("memUsage".equals(property)) {
//                            propertyBean.memUsage = (int) (AppUtils.getMemUsage(context) * 100);
//                        } else if ("appStartTime".equals(property)) {
//                            propertyBean.appStartTime = SystemClock.elapsedRealtime() + "";
//                        } else if ("userConfig".equals(property)) {
//                            propertyBean.userConfig = SPUtil.getDataList("userConfig");
//                        } else if ("envState".equals(property)) {
//                            EnvState envState = new EnvState();
//                            envState.obuState = GlobalVariables.mqttConnStatus;
//                            envState.obuMsgState = GlobalVariables.mqttConnStatus;
//                            envState.broker = AppUtils.getServerIp();
//                            envState.obuId = GlobalVariables.obuId;
//                            propertyBean.envState = envState;
//                        }
////                        else if ("gpsData".equals(property)) { gps实时上传中，本处不再上传
////                            propertyBean.gpsData = ;
////                        }
//                        else {
//
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        readReplyBean.setSuccess(false);
//                        readReplyBean.setCode("1001");
//                        readReplyBean.setMessage("获取属性：" + property + " 失败");
//                        break;
//                    }
//                }
//
//                sendDataToTopic(readPropertiesReplyTopic, GsonUtil.gson.toJson(readReplyBean));
//
//            } else if (topic.equals(writePropertiesTopic)) {
//                WriteBean writeBean = GsonUtil.gson.fromJson(dataStr, WriteBean.class);
//                if (writeBean == null) {
//                    XLog.e("writeBean == null");
//                    return;
//                }
//
//                WriteReplyBean writeReplyBean = new WriteReplyBean();
//                writeReplyBean.setMessageId(writeBean.getMessageId());
//                PropertyBean properties = writeBean.getProperties();
//                if (properties == null) {
//                    writeReplyBean.setTimestamp(System.currentTimeMillis());
//                    writeReplyBean.setSuccess(false);
//                    writeReplyBean.setCode("1000");
//                    writeReplyBean.setMessage("下发属性列表为空");
//                    sendDataToTopic(writePropertiesReplyTopic, GsonUtil.gson.toJson(writeReplyBean));
//                    return;
//                }
//
//                if (properties.userConfig != null) {
//                    SPUtil.set("userConfig", properties.userConfig);
//                }
//
//                if (properties.envState != null) {
//                    // 暂时不支持mqtt的ip和端口配置
//                }
//                writeReplyBean.setTimestamp(System.currentTimeMillis());
//                writeReplyBean.setSuccess(true);
//                sendDataToTopic(writePropertiesReplyTopic, GsonUtil.gson.toJson(writeReplyBean));
//
//            } else if (topic.equals(funcInvokeTopic)) {
//
//                FuncInvokeBean funcInvokeBean = GsonUtil.gson.fromJson(dataStr, FuncInvokeBean.class);
//                if(funcInvokeBean == null){
//                    XLog.e("funcInvokeBean == null");
//                    return;
//                }
//
//                if(funcInvokeBean.getInputs() != null
//                    && funcInvokeBean.getInputs().size() > 0
//                    && funcInvokeBean.getInputs().get(0).getValue() != null){
//
//                    List<FuncInvokeBean.InputsBean.ValueBean.ParkingViolationEventBean> parkingBeanList = funcInvokeBean.getInputs().get(0).getValue().getParkingViolationEvent();
//                    if(parkingBeanList != null && parkingBeanList.size() > 0){
//                        parkingBeanList.sort((o1, o2) -> o2.getSourceType() < o1.getSourceType() ? 1 : -1);
//                        FuncInvokeBean.InputsBean.ValueBean.ParkingViolationEventBean parkingBean = parkingBeanList.get(0);
//                        boolean isAdd = addToIdCache(parkingBean.getUuid());
//                        if(isAdd){
//                            EventBus.getDefault().post(new ParkingEvent(parkingBean));
//                        }
//                    }
//
//                    List<FuncInvokeBean.InputsBean.ValueBean.RSIEventBean> rsiBeanList = funcInvokeBean.getInputs().get(0).getValue().getRSIEvent();
//                    if(rsiBeanList != null && rsiBeanList.size() > 0){
//                        Optional<FuncInvokeBean.InputsBean.ValueBean.RSIEventBean> first = rsiBeanList.stream().filter(rsiBean -> rsiBean.getSourcePlatform().equals("CloudControlManage") && (rsiBean.getAlertType() == 199 || rsiBean.getAlertType() == 501 || rsiBean.getAlertType() == 699)).findFirst();
//
//                        FuncInvokeBean.InputsBean.ValueBean.RsuInfoBean rsuInfoBean = funcInvokeBean.getInputs().get(0).getValue().getRsuInfo();
//                        if(rsuInfoBean != null && first.isPresent()){
//                            FuncInvokeBean.InputsBean.ValueBean.RSIEventBean rsiBean = first.get();
//                            boolean isAdd = addToIdCache(rsiBean.getRsiId());
//                            if(isAdd){
//                                EventBus.getDefault().post(new ControlEvent(rsiBean, rsuInfoBean));
//                            }
//                        }
//
//                    }
//                }
//
//                FuncInvokeReplyBean funcInvokeReplyBean = new FuncInvokeReplyBean();
//                funcInvokeReplyBean.setTimestamp(System.currentTimeMillis());
//                funcInvokeReplyBean.setMessageId(funcInvokeBean.getMessageId());
//                funcInvokeReplyBean.setOutput("success");
//                sendDataToTopic(funcInvokeReplyTopic, GsonUtil.gson.toJson(funcInvokeReplyBean));
//
//            } else {
//
//            }

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            if (GlobalVariables.modelDataStatus != 1) {
                GlobalVariables.modelDataStatus = 1;
                EventBus.getDefault().post(new StatusEvent());
            }
        }

        @Override
        public void connectionLost(Throwable arg0) {
            XLog.e("model-mqtt 连接断开 ");

            GlobalVariables.modelConnStatus = 0;
            GlobalVariables.modelConnDesc= arg0.getMessage();
            GlobalVariables.modelDataStatus = 0;
            EventBus.getDefault().post(new StatusEvent());
        }
    };


    /**
     * 属性上传
     *
     * @param data
     */
    public void sendDataToTopic(String topic, String data) {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                // uu违停
                // data = "{\"longitude\":106.5268837,\"latitude\":29.6459676,\"elevation\":0,\"direction\":226.71,\"speed\":0}";

                // 交通管制
                // data = "{\"longitude\":106.504726,\"latitude\":29.670657,\"elevation\":0,\"direction\":115.76,\"speed\":0}";

                XLog.w("model-mqtt sendDataToTopic topic = " + topic + ", data = " + data);
                mqttAndroidClient.publish(topic, data.getBytes(), 0, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean addToIdCache(String id) {
        boolean ret;
        Optional<String> first = parkingCache.stream().filter(uuid -> uuid.equals(id)).findFirst();

        if (!first.isPresent()) {
            if (parkingCache.size() > 20) {
                parkingCache.remove(0);
            }
            parkingCache.add(id);
            ret = true;
        } else {
            ret = false;
        }
        return ret;
    }

    private int lastObuState = -1;
    private int lastObuMsgState = -1;

    // 状态发生变化后立即向平台上报
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMqttMsg(StatusEvent msg) {
        if(lastObuState != GlobalVariables.pc5ConnStatus || lastObuMsgState != GlobalVariables.pc5DataStatus){
            lastObuState = GlobalVariables.pc5ConnStatus;
            lastObuMsgState = GlobalVariables.pc5DataStatus;
            uploadStatus();
        }

    }

}
