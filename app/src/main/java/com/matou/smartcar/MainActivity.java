package com.matou.smartcar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.amap.api.navi.AmapNaviPage;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.PrinterSet;
import com.flyjingfish.gradienttextviewlib.GradientTextView;
import com.google.zxing.WriterException;
import com.matou.smartcar.adapter.ItemAdapter;
import com.matou.smartcar.base.BaseApplication;
import com.matou.smartcar.bean.BaseResult;
import com.matou.smartcar.bean.CarStatus;
import com.matou.smartcar.bean.DataInfoBean;
import com.matou.smartcar.bean.HandleTirePressInfo;
import com.matou.smartcar.bean.ParkingBean;
import com.matou.smartcar.bean.RsiResBean;
import com.matou.smartcar.bean.RsuInfoBean;
import com.matou.smartcar.bean.SrcTirePressInfo;
import com.matou.smartcar.bean.TokenBean;
import com.matou.smartcar.bean.TokenReq;
import com.matou.smartcar.bean.UpdateStatus;
import com.matou.smartcar.bean.VersionBean;
import com.matou.smartcar.event.BaseDataEvent;
import com.matou.smartcar.event.ControlEvent;
import com.matou.smartcar.event.DeviceInfoEvent;
import com.matou.smartcar.event.MessageEvent;
import com.matou.smartcar.event.StatusEvent;
import com.matou.smartcar.event.ParkingEvent;
import com.matou.smartcar.event.TirePressEvent;
import com.matou.smartcar.event.UpgradeEvent;
import com.matou.smartcar.global.Config;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.global.OptiHandler;
import com.matou.smartcar.manager.WifiPingManager;
import com.matou.smartcar.net.Api;
import com.matou.smartcar.net.AppUpdateTask;
import com.matou.smartcar.net.BaseObserver;
import com.matou.smartcar.net.ModelMQTTClient;
import com.matou.smartcar.net.NetManager;
import com.matou.smartcar.net.RxSchedulers;
import com.matou.smartcar.net.UuMQTTClient;
import com.matou.smartcar.protocol.Icdc2HmiOuterClass;
import com.matou.smartcar.speaker.SpeakerHandler;
import com.matou.smartcar.util.AppUtils;
import com.matou.smartcar.util.CommUtils;
import com.matou.smartcar.util.DataConversionUtil;
import com.matou.smartcar.util.DeviceUtil;
import com.matou.smartcar.util.DisplayUtil;
import com.matou.smartcar.util.GsonUtil;
import com.matou.smartcar.util.MD5Utils;
import com.matou.smartcar.util.SPUtil;
import com.matou.smartcar.view.BaseTrafficLightWnd;
import com.matou.smartcar.view.ParkingInfoDialog;
import com.matou.smartcar.view.MultiClickView;
import com.matou.smartcar.view.NationalView;
import com.matou.smartcar.view.RealTimeView;
import com.matou.smartcar.view.ServerIpSettingDialog;
import com.matou.smartcar.view.SettingsDialog;
import com.matou.smartcar.view.TrafficEventWnd;
import com.matou.smartcar.view.TrafficLightWnd;
import com.matou.smartcar.view.TrafficLightWndNew;

import org.eclipse.paho.android.service.MqttService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

/**
 * GLOSA(绿波)、YPC(礼让行人)、IVS(车内标牌)、BSW(盲区预警)、FCW(前向碰撞)
 *
 * @author ranfeng
 */
public class MainActivity extends FragmentActivity {

    @BindView(R.id.mcv_back_setting)
    MultiClickView backSettingV;

    @BindView(R.id.mcv_back_logflag)
    MultiClickView backLogFlagV;

    @BindView(R.id.mcv_back_reboot)
    MultiClickView backRebootV;

    @BindView(R.id.vNational)
    NationalView vNational;

    @BindView(R.id.vRealTime)
    RealTimeView vRealTime;

    @BindView(R.id.llyt_parking)
    LinearLayout parkingLlyt;

    @BindView(R.id.tv_parking_title)
    GradientTextView parkingTitleTv;

    @BindView(R.id.tv_parking_content)
    GradientTextView parkingContentTv;

    @BindView(R.id.tv_parking_status)
    GradientTextView parkingStatusTv;

    @BindView(R.id.tv_parking_time)
    GradientTextView parkingTimeTv;

    @BindView(R.id.llyt_control)
    LinearLayout controlLlyt;

    @BindView(R.id.tv_control_content)
    TextView controlContentTv;

    @BindView(R.id.tv_control_close)
    TextView controlCloseTv;

    @BindView(R.id.iv_connect_status)
    ImageView connStatusIv;

    @BindView(R.id.llyt_user_feedback)
    LinearLayout userFeedbackLlyt;

    @BindView(R.id.iv_tire_press)
    ImageView tirePressIv;

    @BindView(R.id.llyt_tire_press)
    LinearLayout tirePressLlyt;

    @BindView(R.id.gtv_tire_count)
    GradientTextView tireCountGtv;

    @BindView(R.id.gtv_front_left)
    GradientTextView frontLeftGtv;

    @BindView(R.id.iv_front_left)
    ImageView frontLeftIv;

    @BindView(R.id.gtv_rear_left)
    GradientTextView rearLeftGtv;

    @BindView(R.id.iv_rear_left)
    ImageView rearLeftIv;

    @BindView(R.id.gtv_front_right)
    GradientTextView frontRightGtv;

    @BindView(R.id.iv_front_right)
    ImageView frontRightIv;

    @BindView(R.id.gtv_rear_right)
    GradientTextView rearRightGtv;

    @BindView(R.id.iv_rear_right)
    ImageView rearRightIv;

    @BindView(R.id.iv_parking_icon)
    ImageView parkingIconIv;

    @BindView(R.id.iv_settings_icon)
    ImageView settingsIconIv;

    @BindView(R.id.clyt_content)
    ConstraintLayout contentClyt;

    @BindView(R.id.clyt_padding)
    ConstraintLayout paddingLlyt;

    // private GlobalTipsWnd globalTipsWnd;

    private TrafficEventWnd trafficEventWnd;

    private BaseTrafficLightWnd trafficLightWnd;

    private OptiHandler optiHandler;

    private final Handler parkingHandler = new Handler();
    private final Handler controlHandler = new Handler();

    private final Handler tirePressHandler = new Handler();

    public static final int MSG_HANDLE_SPEED = 10000;

    //private final WifiConnector wifiConnector = new WifiConnector(this);

    private ServerIpSettingDialog serverIpSettingDialog;

    private final WifiReceiver wifiReceiver = new WifiReceiver();

    /**
     * 多次胎压提示，只显示一次
     */
    private boolean hasAutoShowTire = false;
    /**
     * 主动点击显示胎压
     */
    private boolean hasManuShowTire = false;

    private final HandleTirePressInfo[] handleTirePressInfoArray = new HandleTirePressInfo[4];

    private ParkingInfoDialog parkingInfoDialog;

    private SettingsDialog settingsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XLog.w("MainActivity onCreate");


        //wifiConnector.openWifi();

        //wifiConnector.registerReceiver();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        GlobalVariables.wifiConnect = networkInfo.isConnected();
        XLog.w("===> onCreate wifiConnect = " + GlobalVariables.wifiConnect);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, intentFilter);


        checkFloatPermission();

        backSettingV.setOnMultiClickLitener(v -> {
            String defaultIp = (String) SPUtil.get(SPUtil.SERVER_PORT, "192.168.9.1:1883");
            serverIpSettingDialog = new ServerIpSettingDialog(MainActivity.this)
                    .setDefaultIP(defaultIp)
                    .setOnConfirmClickListener((ip, username, pwd) -> {
                        if (!TextUtils.isEmpty(ip)) {
                            SPUtil.set(SPUtil.SERVER_PORT, ip);
                            SPUtil.set(SPUtil.SERVER_USENAME, username);
                            SPUtil.set(SPUtil.SERVER_PWD, pwd);

                            Toast.makeText(MainActivity.this, "请重启 V2X云镜APP 以使您的设置生效", Toast.LENGTH_LONG).show();
                            new Handler().postDelayed(() -> {
                                stopService(new Intent(MainActivity.this, MqttService.class));
                                System.exit(0);
                            }, 3000);
                        }
                    });
            serverIpSettingDialog.show();
        });

        backLogFlagV.setOnMultiClickLitener(v -> {
            PrinterSet.logFlag = !PrinterSet.logFlag;
            Toast.makeText(MainActivity.this, PrinterSet.logFlag ? "已打开日志(仅限调试使用)" : "已关闭日志", Toast.LENGTH_LONG).show();
        });

        backRebootV.setOnMultiClickLitener(v -> {
            finish();
            stopService(new Intent(MainActivity.this, MqttService.class));
            System.exit(0);
        });

        userFeedbackLlyt.setOnClickListener(v -> showFeedbackDialog());


        //getUuToken();
        ModelMQTTClient.getInstance(this).start();

        showCar();

        // 先保证上传状态ok
        optiHandler.postDelayed(this::uploadUpdateStatus, 5000);

        optiHandler.postDelayed(() -> checkVersion(false), 10000);

        // globalTipsWnd = new GlobalTipsWnd(this);
        trafficEventWnd = new TrafficEventWnd(this);

        tirePressIv.setOnClickListener(v -> {
            if(tirePressLlyt.getVisibility() != View.VISIBLE){
                if(!hasManuShowTire){
                    if(!checkShowLevel(GlobalVariables.SHOW_MANU_TIRE_LEVEL)){
                        return;
                    }
                    showTirePressContent(true, handleTirePressInfoArray);
                    hasManuShowTire = true;
                }
            }
        });

        parkingIconIv.setOnClickListener(v -> {
            // 有车辆位置才显示停车场
            if(GlobalVariables.carStatus.isHasGps()){
                parkingInfoDialog = new ParkingInfoDialog(MainActivity.this, GlobalVariables.carStatus.getLon(), GlobalVariables.carStatus.getLat());
                parkingInfoDialog.show();
            }else {
                Toast.makeText(MainActivity.this, "暂无车辆位置信息，请检查!", Toast.LENGTH_SHORT).show();
            }

        });

        settingsIconIv.setOnClickListener(v -> {
            settingsDialog = new SettingsDialog(MainActivity.this);
            settingsDialog.show();
        });

        // 设置相关初始化
        boolean isRight = (boolean) SPUtil.get("isRight", false);
        setContentPostion(isRight);

        GlobalVariables.fcwEnable = (boolean) SPUtil.get("fcwEnable", true);
        GlobalVariables.bsmEnable = (boolean) SPUtil.get("bsmEnable", true);
        GlobalVariables.icwEnable = (boolean) SPUtil.get("icwEnable", true);
        GlobalVariables.pcrEnable = (boolean) SPUtil.get("pcrEnable", true);
        GlobalVariables.rlvwEnable = (boolean) SPUtil.get("rlvwEnable", true);
        GlobalVariables.traffControlEnable = (boolean) SPUtil.get("traffControlEnable", true);
        GlobalVariables.greenWaveEnable = (boolean) SPUtil.get("greenWaveEnable", true);
        GlobalVariables.traffInfoEnable = (boolean) SPUtil.get("traffInfoEnable", true);
        GlobalVariables.limitSpeedEnable = (boolean) SPUtil.get("limitSpeedEnable", true);
        GlobalVariables.specialVehicleEnable = (boolean) SPUtil.get("specialVehicleEnable", true);
        GlobalVariables.illagelParkingEnable2 = (boolean) SPUtil.get("illagelParkingEnable2", false);
        GlobalVariables.mixLightEnable = (boolean) SPUtil.get("mixLightEnable", false);


        // mock
        //trafficLightWnd = new TrafficLightWndNew(this);

        WifiPingManager.getInstance(BaseApplication.getInstance()).startPinging();

    }


    public void showFeedbackDialog() {
        // 创建对话框构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        // 自定义的布局文件
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_feedback, null);
        // 将布局设置给Dialog
        builder.setView(dialogView);
        AlertDialog feedbackDialog = builder.create();
        dialogView.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackDialog.cancel();
            }
        });

        // 设置二维码图片
        ImageView qrCodeImage = dialogView.findViewById(R.id.image_qr_code);
        try {
            Bitmap qrCodeBitmap = AppUtils.generateQRCode(GlobalVariables.URL_FEEDBACK + AppUtils.getDeviceId());
            qrCodeImage.setImageBitmap(qrCodeBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            // 处理异常，例如显示一个错误消息
        }

        // 创建对话框

        feedbackDialog.show();
        Window window = feedbackDialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        layoutParams.width = screenWidth * 2 / 5;

        window.setAttributes(layoutParams);

    }


    /**
     * 防止可以联网后多次请求token
     */
    private boolean isGettingToken;

    /**
     * 获取uu的token
     */
    private void getUuToken() {
        XLog.w("getUuToken invoke");
        if (isGettingToken) {
            XLog.w("getUuToken gettingToken return");
            return;
        }

        if (!TextUtils.isEmpty(GlobalVariables.token)) {
            XLog.w("getUuToken has getted");
            return;
        }


        isGettingToken = true;
        TokenReq req = new TokenReq();
        req.setClientId(DeviceUtil.getDeviceId());
        req.setTimesnap(String.valueOf(System.currentTimeMillis()));
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), GsonUtil.beanToJson(req));
        NetManager.getApi(NetManager.mBaseUrl, Api.class)
                .getToken(body)
                .compose(RxSchedulers.applySchedulers())
                .subscribe(new BaseObserver<BaseResult<TokenBean>>() {
                    @Override
                    public void onSuccess(BaseResult<TokenBean> baseResult) {
                        XLog.w("uumqtt getToken onSuccess");
                        isGettingToken = false;
                        GlobalVariables.token = baseResult.data.getToken();
                        if (!TextUtils.isEmpty(GlobalVariables.token)) {
                            UuMQTTClient.getInstance(MainActivity.this).start();
                        }
                    }

                    @Override
                    public void onErrorMsg(String msg) {
                        isGettingToken = false;
                        XLog.w("uumqtt getToken onErrorMsg: " + msg);

                        // 非入库情况下则轮询获取
                        if(!msg.contains("未入库")){
                            if (TextUtils.isEmpty(GlobalVariables.token)) {
                                optiHandler.postDelayed(MainActivity.this::getUuToken, 10000);
                            }
                        }

                        GlobalVariables.uuConnStatus = 0;
                        GlobalVariables.uuConnDesc= msg;
                        GlobalVariables.uuDataStatus = 0;
                        EventBus.getDefault().post(new StatusEvent());

                    }
                });
    }

    /**
     * 检查是否上传升级状态
     */
    private void uploadUpdateStatus() {
        String versionName = AppUtils.getVersionName();
        if (TextUtils.isEmpty(versionName)) {
            XLog.e("获取版本名称为空");
            return;
        }

        String updateStatusStr = (String) SPUtil.get("UpdateStatus", "");
        if (TextUtils.isEmpty(updateStatusStr)) {
            XLog.e("未获取到升级状态信息，忽略");
            return;
        }
        UpdateStatus updateStatus = GsonUtil.gsonToBean(updateStatusStr, UpdateStatus.class);
        if (updateStatus.getStatus() == 1) {
            int compare = updateStatus.getVersion().compareTo(versionName);
            int status = compare < 1 ? 1 : 0;
            String message = compare < 1 ? "升级成功" : ("升级失败" + updateStatus.getFailReason());
            AppUtils.uploadUpdateStatus(updateStatus.getUpgradeCode(), status, message);
            // 升级成功，移除安装包
            if (compare < 1) {
                File folder = new File(Config.BASE_PATH);
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        boolean delete = file.delete();
                        XLog.w("uploadUpdateStatus success, " + file.getName() + " delete result = " + delete);
                    }
                }
            }
        }
    }

    /**
     * 检查悬浮窗权限
     */
    private void checkFloatPermission() {

        //悬浮窗权限判断
        if (!Settings.canDrawOverlays(MainActivity.this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示")
                    .setMessage("请允许显示在其他应用的上层，否则将影响本程序的正常使用")
                    .setPositiveButton("确定", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQUEST_CODE);
                    }).setCancelable(false);
            builder.show();
        }
    }

    private void delayExitApp() {
        Toast.makeText(this, "未授权，程序即将退出", Toast.LENGTH_SHORT).show();
        optiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                stopService(new Intent(MainActivity.this, MqttService.class));
                System.exit(0);
            }
        }, 2000);
    }

    private static final int REQUEST_CODE = 1000;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                delayExitApp();
            }
        }
    }

    private void showCar() {

        optiHandler = new OptiHandler(this);
        optiHandler.setListener(msg -> {
            if (msg.what == MSG_HANDLE_SPEED) {
                optiHandler.sendEmptyMessageDelayed(MSG_HANDLE_SPEED, 500);
                CarStatus carStatus = GlobalVariables.carStatus;
                try {
                    float speed = carStatus.getSpeed();
                    if (speed < 0) {
                        return;
                    }

                    XLog.w("---> speed = " + speed);

                    int moveStatus = speed > 3.6f ? 2 : 1;
                    if (moveStatus == carStatus.getMoveStatus()) {
                        //XLog.w("===> isMove == carStatus.isLashMove()");
                        return;
                    }

                    // 记录停车时的时间戳
                    if (moveStatus == 1) {
                        carStatus.setParkingTime(System.currentTimeMillis());
                    } else {
                        hideParking();
                        carStatus.setParkingTime(0);
                    }

                    carStatus.setMoveStatus(moveStatus);

                    XLog.w("===> switch move pic");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        optiHandler.sendEmptyMessage(MSG_HANDLE_SPEED);


    }


    private int checkCount;

    /**
     * 检查更新
     * @param manu 手动更新
     */
    public void checkVersion(boolean manu) {
        XLog.w("---> checkVersion manu = " + manu);
        String time = String.valueOf(System.currentTimeMillis());
        String encrypt = MD5Utils.encrypt(NetManager.getAppId() + "|" + time + "|" + "QAZzaq1!");
        NetManager.getApi(NetManager.mBaseUrl, Api.class)
                .getVersion(NetManager.getAppId(), time, encrypt)
                .compose(RxSchedulers.applySchedulers())
                .subscribe(new BaseObserver<BaseResult<VersionBean>>() {
                    @Override
                    public void onSuccess(BaseResult<VersionBean> baseResult) {
                        VersionBean data = baseResult.data;

                        String versionName = AppUtils.getVersionName();
                        String md5 = data.getMd5();
                        SPUtil.set(SPUtil.MD5_KEY, md5);

                        boolean software = data.isSoftware();
                        int contentLength = data.getContentLength();
                        int compare = versionName.compareTo(data.getVersion());
                        if (software && compare < 0) {
                            UpdateStatus updateStatus = new UpdateStatus();
                            updateStatus.setStatus(1);
                            updateStatus.setVersion(data.getVersion());
                            updateStatus.setUpgradeCode(data.getUpgradeCode());
                            SPUtil.set("UpdateStatus", GsonUtil.beanToJson(updateStatus));
                            XLog.w("save UpdateStatus = " + GsonUtil.beanToJson(updateStatus) + ", upgrading = " + AppUpdateTask.upgrading);
                            if(!AppUpdateTask.upgrading){
                                AppUpdateTask.upgrading = true;
                                new AppUpdateTask(MainActivity.this, contentLength, data.getVersion(), data.getUpgradeCode()).execute(data.getUrl());
                            }
                        }else { // 无升级包
                            if(manu){
                                EventBus.getDefault().post(new UpgradeEvent(2, 0));
                            }
                        }
                        XLog.w("checkVersion onSuccess");
                    }

                    @Override
                    public void onErrorMsg(String msg) {
                        XLog.w("checkVersion onErrorMsg: " + msg);
                        if(manu){
                            if(msg.contains("NetError")){
                                EventBus.getDefault().post(new UpgradeEvent(-2, 0));
                            }else {
                                EventBus.getDefault().post(new UpgradeEvent(-3, 0));
                            }
                        }else {
                            if (checkCount < 10) {
                                optiHandler.postDelayed(() -> checkVersion(manu), 10 * 1000);
                            }

                            checkCount++;
                        }
                    }
                });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsg(BaseDataEvent event) {
        Icdc2HmiOuterClass.Icdc2Hmi data = event.getData();
        DataInfoBean dataInfo = DataConversionUtil.convert(data);

        //大唐数据转换 让后执行
        handleData(dataInfo);
    }

    /**
     * 接收来着mqtt 的数据
     *
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMqttMsg(MessageEvent msg) {
        XLog.w("===========> nowTime444 = " + System.currentTimeMillis() + ", seq = " + msg.getData().getSeq());
        handleData(msg.getData());
    }

    /**
     * 处理来自mqtt或者udp的数据
     *
     * @param data
     */
    private void handleData(DataInfoBean data) {
        if (data == null) {
            return;
        }

        CarStatus carStatus = GlobalVariables.carStatus;
        if (data.getHVMSG() != null) {
            carStatus.setSpeed(data.getHVMSG().getSpeed());
            DataInfoBean.HVMSGBean.PosBean pos = data.getHVMSG().getPos();
            if (pos != null && pos.getLatitude() > 0 && pos.getLongitude() > 0) {
                carStatus.setHasGps(true);
                carStatus.setLon(pos.getLongitude());
                carStatus.setLat(pos.getLatitude());
            } else {
                carStatus.setHasGps(false);
                carStatus.setLon(0);
                carStatus.setLat(0);
            }
            if (serverIpSettingDialog != null && serverIpSettingDialog.isShowing()) {
                serverIpSettingDialog.showLlSpeed(data.getHVMSG());
            }
        }

        //globalTipsWnd.bindData(data);
        trafficEventWnd.bindData(data);
        if(trafficLightWnd != null){
            trafficLightWnd.bindData(data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XLog.w("MainActivity onDestroy");
        //wifiConnector.unRegisterReceiver();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(wifiReceiver);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WifiPingManager.getInstance(BaseApplication.getInstance()).stopPinging();
    }

    public void updateNational(List<RsiResBean> rsiIcons, boolean canPlay) {
        //XLog.w("updateNational rsiIcons = " + (rsiIcons == null ? "null" : GsonUtil.beanToJson(rsiIcons)));
        vNational.bindData(rsiIcons, canPlay);
    }

    public void updateEvent(boolean isResponse, boolean onlyTts, DataInfoBean.RVMSGBean data, float heading) {

        // 表示事件展示，型号灯不能中断
        GlobalVariables.wndShowMap.put(GlobalVariables.WND_TYPE_2, isResponse);

        XLog.w("========> isResponse = %b, onlyTts= %b", isResponse, onlyTts);

        if (isResponse) {

            if(!checkShowLevel(GlobalVariables.SHOW_SCENE_LEVEL)){
                return;
            }

            vRealTime.bindData(data, heading, onlyTts);
        } else {
            GlobalVariables.currSHowLevel = GlobalVariables.SHOW_NOTHING_LEVEL;
            vRealTime.reset();
            vRealTime.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private int parkingCount;

    /**
     * 提示停车延时
     */
    private final static int SHOW_PARKING_DELAY = 3 * 60;

    // mock
    //private final static int SHOW_PARKING_DELAY = 2;

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onParkingMsg(ParkingEvent event) {
        XLog.w("onParkingMsg");

        if(!checkShowLevel(GlobalVariables.SHOW_PARKING_LEVEL)){
            return;
        }

        ParkingBean parkingBean = event.getParkingBean();
        if(parkingBean != null){
            parkingBean.setStartTime(System.currentTimeMillis());
            GlobalVariables.parkingBean = parkingBean;
        }
        parkingHandler.removeCallbacksAndMessages(null);
        parkingHandler.postDelayed(() -> showParking(event), SHOW_PARKING_DELAY * 1000);

    }

    private void showParking(ParkingEvent event) {
        if (GlobalVariables.pc5ConnStatus != 2) {
            hideParking();
            XLog.w("GlobalVariables.pc5ConnStatus != 2");
            return;
        }

        if (GlobalVariables.carStatus.getMoveStatus() != 1) {
            hideParking();
            XLog.w("showParking GlobalVariables.carStatus.getMoveStatus() != 1 return");
            return;
        }

        ParkingBean parkingBean = event.getParkingBean();


        /*******************mock****************/
        //parkingBean.setParkingMin(1);


        int parkingMin = parkingBean.getParkingMin();
        String parkingContent;
        if (parkingMin > 0) {
            parkingContent = parkingBean.getAreaName() + " 限时停车，请尽快出发";
            if (!TextUtils.isEmpty(parkingContent)) {
                SpeakerHandler.getInstance().speak(parkingContent, SpeakerHandler.ILL_PARKING);
            }
            new Handler().postDelayed(() -> showLimitParking(parkingBean, parkingMin), 1500);
        } else {
            parkingContent = parkingBean.getAreaName() + " 禁止停车，请尽快出发";
            if (!TextUtils.isEmpty(parkingContent)) {
                SpeakerHandler.getInstance().speak(parkingContent, SpeakerHandler.ILL_PARKING);
            }
            new Handler().postDelayed(() -> showNoParking(parkingBean), 1500);
        }
    }

    private void showNoParking(ParkingBean parkingBean) {
        parkingLlyt.setVisibility(View.VISIBLE);
        parkingTitleTv.setText("禁停路段");
        setTvStatus(parkingTitleTv, "error");

        parkingContentTv.setText(parkingBean.getAreaName() + " 禁止停车，请尽快出发");
        setTvStatus(parkingContentTv, "normal");

        setTvStatus(parkingStatusTv, "warning");
        setTvStatus(parkingTimeTv, "error");

        parkingCount = SHOW_PARKING_DELAY;
        parkingHandler.post(new Runnable() {
            @Override
            public void run() {

                parkingTimeTv.setText(AppUtils.convertDuration(parkingCount));
                parkingCount++;

                if (GlobalVariables.carStatus.getMoveStatus() == 1) {
                    parkingHandler.postDelayed(this, 1000);
                } else {
                    hideParking();
                }
            }
        });
    }

    private void showLimitParking(ParkingBean parkingBean, int parkingMin) {
        parkingLlyt.setVisibility(View.VISIBLE);
        parkingTitleTv.setText("临停路段");
        setTvStatus(parkingTitleTv, "warning");
        parkingContentTv.setText(parkingBean.getAreaName() + " 限时停车，请尽快出发");
        setTvStatus(parkingContentTv, "normal");

        long remainTime = parkingMin * 60L - SHOW_PARKING_DELAY;

        XLog.w("remainTime = " + remainTime);

        if (remainTime > 0) {
            parkingStatusTv.setText("限时剩余");
            setTvStatus(parkingStatusTv, "normal");

            setTvStatus(parkingTimeTv, "warning");

            parkingCount = (int) remainTime;
            parkingHandler.post(new Runnable() {
                @Override
                public void run() {

                    parkingTimeTv.setText(AppUtils.convertDuration(parkingCount));
                    if (parkingCount < 1) {
                        setTvStatus(parkingTitleTv, "error");
                        parkingStatusTv.setText("已超时");
                        setTvStatus(parkingStatusTv, "warning");

                        setTvStatus(parkingTimeTv, "error");

                        parkingCount = 1;
                        parkingHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                parkingCount++;
                                parkingTimeTv.setText(AppUtils.convertDuration(parkingCount));

                                if (GlobalVariables.carStatus.getMoveStatus() == 1) {
                                    parkingHandler.postDelayed(this, 1000);
                                } else {
                                    hideParking();
                                }
                            }
                        });
                    } else {
                        if (GlobalVariables.carStatus.getMoveStatus() == 1) {
                            parkingHandler.postDelayed(this, 1000);
                        } else {
                            hideParking();
                        }
                    }
                    parkingCount--;
                }
            });
        } else {
            setTvStatus(parkingTitleTv, "error");
            parkingStatusTv.setText("已超时");
            setTvStatus(parkingStatusTv, "warning");

            setTvStatus(parkingTimeTv, "error");

            parkingCount = 1;
            parkingHandler.post(new Runnable() {
                @Override
                public void run() {

                    parkingTimeTv.setText(AppUtils.convertDuration(parkingCount));
                    parkingCount++;

                    if (GlobalVariables.carStatus.getMoveStatus() == 1) {
                        parkingHandler.postDelayed(this, 1000);
                    } else {
                        hideParking();
                    }
                }
            });
        }
    }

    /**
     * 设置字体渐变色
     * @param tv
     * @param status
     */
    private void setTvStatus(GradientTextView tv, String status){
        if("error".equals(status)){
            tv.setGradientColors(new int[]{Color.parseColor("#f0361c"),Color.parseColor("#cb240c"),Color.parseColor("#b71a04")});
        }else if("warning".equals(status)){
            tv.setGradientColors(new int[]{Color.parseColor("#fd932b"),Color.parseColor("#fd7a2b")});
        }else {
            tv.setGradientColors(new int[]{Color.parseColor("#ffffff"),Color.parseColor("#a7bbe9")});
        }
    }


    private void hideParking() {
        GlobalVariables.currSHowLevel = GlobalVariables.SHOW_NOTHING_LEVEL;
        GlobalVariables.parkingBean = null;
        parkingLlyt.setVisibility(View.GONE);
        parkingHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(() -> {
            if(GlobalVariables.startNavi && GlobalVariables.amapNaviParams != null){
                AmapNaviPage.getInstance().showRouteActivity(BaseApplication.getInstance(), GlobalVariables.amapNaviParams, ItemAdapter.iNaviInfoCallback);
            }
        }, 2000);

        // mock
/*        new Handler().postDelayed(() -> {
            RsiBean rsiBean = new RsiBean();
            RsuInfoBean rsuInfoBean = new RsuInfoBean();
            rsuInfoBean.setAddr("渝北区");
            rsuInfoBean.setCrossName("中国汽研1号门");
            onControlMsg(new ControlEvent(rsiBean, rsuInfoBean));
        }, 3000);*/



    }

    private int controlCount;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onControlMsg(ControlEvent event) {
        controlHandler.removeCallbacksAndMessages(null);

        if(!checkShowLevel(GlobalVariables.SHOW_CONTROLL_LEVEL)){
            return;
        }


        RsuInfoBean rsuInfoBean = event.getRsuInfoBean();
        String controlStr = rsuInfoBean.getAddr() + rsuInfoBean.getCrossName() + "临时管控，请绕行";

        if (!TextUtils.isEmpty(controlStr)) {
            SpeakerHandler.getInstance().speak(controlStr, SpeakerHandler.TRAFFIC_CONTROL);
        }
        new Handler().postDelayed(() -> showTrafficControl(controlStr), 1500);
    }

    private void showTrafficControl(String controlStr) {
        controlLlyt.setVisibility(View.VISIBLE);
        controlContentTv.setText(controlStr);
        controlCount = 15;
        controlHandler.post(new Runnable() {
            @Override
            public void run() {
                controlCloseTv.setText(controlCount + "");
                if (controlCount > 0) {
                    controlHandler.postDelayed(this, 1000);
                } else {
                    GlobalVariables.currSHowLevel = GlobalVariables.SHOW_NOTHING_LEVEL;
                    controlLlyt.setVisibility(View.GONE);
                }
                controlCount--;
            }
        });
    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            XLog.w("WifiReceiver onReceive wifiInfo = " + wifiInfo);
            if (wifiInfo != null && wifiInfo.isConnected()) {
                GlobalVariables.wifiConnect = true;
            } else {
                GlobalVariables.pc5ConnStatus = 0;
                GlobalVariables.pc5ConnDesc = "wifi未连接";

                GlobalVariables.uuConnStatus = 0;
                GlobalVariables.uuConnDesc = "wifi未连接";

                GlobalVariables.modelConnStatus = 0;
                GlobalVariables.modelConnDesc = "wifi未连接";

                GlobalVariables.wifiConnect = false;
                EventBus.getDefault().post(new StatusEvent());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMqttMsg(StatusEvent msg) {
        if (GlobalVariables.pc5ConnStatus == 0) {
            connStatusIv.setVisibility(View.INVISIBLE);
        } else if (GlobalVariables.pc5ConnStatus == 1) {
            connStatusIv.setVisibility(View.VISIBLE);
            connStatusIv.setBackgroundResource(R.mipmap.icon_wifi_connected);
        } else {
            connStatusIv.setVisibility(View.VISIBLE);
            connStatusIv.setBackgroundResource(R.mipmap.icon_mqtt_connected);
        }
    }

    /**
     * 处理胎压异常
     * @param tirePressEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleTirePressInfo(TirePressEvent tirePressEvent){
        XLog.w("---> handleTirePressInfo");

        SrcTirePressInfo srcTirePressInfo = tirePressEvent.getTirePressInfo();
        boolean hasFrontLeft = srcTirePressInfo.getFrontLeft().isHasFrontLeftTpmsData();
        double frontLeft = 0;
        if(hasFrontLeft){
            frontLeft = CommUtils.remainOneDouble(srcTirePressInfo.getFrontLeft().getFrontLeftTpmsData().getPressure_kPa() / 100);
        }
        handleTirePressInfoArray[0] = new HandleTirePressInfo(hasFrontLeft, frontLeft);

        boolean hasRearLeft = srcTirePressInfo.getRearLeft().isHasRearLeftTpmsData();
        double rearLeft = 0;
        if(hasRearLeft){
            rearLeft = CommUtils.remainOneDouble(srcTirePressInfo.getRearLeft().getRearLeftTpmsData().getPressure_kPa() / 100);
        }
        handleTirePressInfoArray[1] = new HandleTirePressInfo(hasRearLeft, rearLeft);

        boolean hasFrontRight = srcTirePressInfo.getFrontRight().isHasFrontRightTpmsData();
        double frontRight = 0;
        if(hasFrontRight){
            frontRight = CommUtils.remainOneDouble(srcTirePressInfo.getFrontRight().getFrontRightTpmsData().getPressure_kPa() / 100);
        }
        handleTirePressInfoArray[2] = new HandleTirePressInfo(hasFrontRight, frontRight);

        boolean hasRearRight = srcTirePressInfo.getRearRight().isHasRearRightTpmsData();
        double rearRight = 0;
        if(hasRearRight){
            rearRight = CommUtils.remainOneDouble(srcTirePressInfo.getRearRight().getRearRightTpmsData().getPressure_kPa() / 100);
        }
        handleTirePressInfoArray[3] = new HandleTirePressInfo(hasRearRight, rearRight);

        boolean isAbnormal = false;
        for(int i=0; i<handleTirePressInfoArray.length; i++){
            if(handleTirePressInfoArray[i].isAbnormal()){
                isAbnormal = true;
                break;
            }
        }

        if(isAbnormal){
            showTirePressIcon(true);
            if(!hasAutoShowTire){
                // 判断显示优先级
                if(!checkShowLevel(GlobalVariables.SHOW_AUTO_TIRE_LEVEL)){
                    return;
                }
                showTirePressContent(true, handleTirePressInfoArray);
            }
            hasAutoShowTire = true;
        }else {
            GlobalVariables.currSHowLevel = GlobalVariables.SHOW_NOTHING_LEVEL;
            hasAutoShowTire = false;
            showTirePressIcon(false);
            showTirePressContent(false, null);
        }
    }

    /**
     * 显示胎压图标
     * @param show
     */
    public void showTirePressIcon(boolean show){
        tirePressIv.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private int tireCount = 20;

    /**
     * 显示胎压信息
     *
     * @param show
     * @param handleTirePressInfoArray
     */
    public void showTirePressContent(boolean show, HandleTirePressInfo[] handleTirePressInfoArray){
        if(show){
            if(tirePressLlyt.getVisibility() == View.VISIBLE){
                return;
            }
            String tts = "";

            if(handleTirePressInfoArray[0] != null){
                if(handleTirePressInfoArray[0].isHasValue()){
                    if(handleTirePressInfoArray[0].isAbnormal()){
                        setTirePressTvIvStatus(frontLeftGtv, frontLeftIv, "warning", String.valueOf(handleTirePressInfoArray[0].getValue()));
                        tts += "左前轮，";
                    }else {
                        setTirePressTvIvStatus(frontLeftGtv, frontLeftIv, "normal", String.valueOf(handleTirePressInfoArray[0].getValue()));
                    }
                }else {
                    setTirePressTvIvStatus(frontLeftGtv, frontLeftIv, "normal", "--");
                }
            }

            if(handleTirePressInfoArray[1] != null){
                if(handleTirePressInfoArray[1].isHasValue()){
                    if(handleTirePressInfoArray[1].isAbnormal()){
                        setTirePressTvIvStatus(rearLeftGtv, rearLeftIv, "warning", String.valueOf(handleTirePressInfoArray[1].getValue()));
                        tts += "左后轮，";
                    }else {
                        setTirePressTvIvStatus(rearLeftGtv, rearLeftIv, "normal", String.valueOf(handleTirePressInfoArray[1].getValue()));
                    }
                }else {
                    setTirePressTvIvStatus(rearLeftGtv, rearLeftIv, "normal", "--");
                }
            }

            if(handleTirePressInfoArray[2] != null){
                if(handleTirePressInfoArray[2].isHasValue()){
                    if(handleTirePressInfoArray[2].isAbnormal()){
                        setTirePressTvIvStatus(frontRightGtv, frontRightIv, "warning", String.valueOf(handleTirePressInfoArray[2].getValue()));
                        tts += "右前轮，";
                    }else {
                        setTirePressTvIvStatus(frontRightGtv, frontRightIv, "normal", String.valueOf(handleTirePressInfoArray[2].getValue()));
                    }
                }else {
                    setTirePressTvIvStatus(frontRightGtv, frontRightIv, "normal", "--");
                }
            }

            if(handleTirePressInfoArray[3] != null){
                if(handleTirePressInfoArray[3].isHasValue()){
                    if(handleTirePressInfoArray[3].isAbnormal()){
                        setTirePressTvIvStatus(rearRightGtv, rearRightIv, "warning", String.valueOf(handleTirePressInfoArray[3].getValue()));
                        tts += "右前轮，";
                    }else {
                        setTirePressTvIvStatus(rearRightGtv, rearRightIv, "normal", String.valueOf(handleTirePressInfoArray[3].getValue()));
                    }
                }else {
                    setTirePressTvIvStatus(rearRightGtv, rearRightIv, "normal", "--");
                }
            }

            SpeakerHandler.getInstance().speak(tts + " 胎压异常", SpeakerHandler.TIRE_PRESS);

            tireCount = 20;
            tireCountGtv.setText("(20s)");
            tirePressHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tireCount--;
                    if(tireCount < 1){
                        hasManuShowTire = false;
                        showTirePressContent(false, null);
                    }else {
                        tireCountGtv.setText("(" + CommUtils.formatNum(tireCount) + "s)");
                        tirePressHandler.postDelayed(this, 1000);
                    }
                }
            }, 1000);

            /**
             * 为保持显示和声音同步，此处延时
             */
            tirePressHandler.postDelayed(() -> tirePressLlyt.setVisibility(View.VISIBLE), 500);
        }else {
            GlobalVariables.currSHowLevel = GlobalVariables.SHOW_NOTHING_LEVEL;
            tirePressHandler.removeCallbacksAndMessages(null);
            tirePressLlyt.setVisibility(View.GONE);
        }

    }

    /**
     * 设置胎压字体显示状态
     * @param tv
     * @param status
     */
    private void setTirePressTvIvStatus(GradientTextView tv, ImageView iv, String status, String value){
        if("warning".equals(status)){
            tv.setGradientColors(new int[]{Color.parseColor("#fd932b"),Color.parseColor("#fd7a2b")});
            tv.setTextSize(DisplayUtil.spToPx(this, 9.5f));
            if(tv == frontLeftGtv || tv == rearLeftGtv){
                iv.setBackgroundResource(R.drawable.tire_press_right_select);
            }else {
                iv.setBackgroundResource(R.drawable.tire_press_left_select);
            }
        }else {
            tv.setGradientColors(new int[]{Color.parseColor("#b1bfce"),Color.parseColor("#b1bfce")});
            tv.setTextSize(DisplayUtil.spToPx(this, 9));
            if(tv == frontLeftGtv || tv == rearLeftGtv){
                iv.setBackgroundResource(R.drawable.tire_press_right_normal);
            }else {
                iv.setBackgroundResource(R.drawable.tire_press_left_normal);
            }
        }
        tv.setText(value);
    }

    /**
     * 设置内容的位置，左或右
     */
    public void setContentPostion(boolean isRight) {
        LinearLayout parent = (LinearLayout) contentClyt.getParent();

        if(isRight){
            parent.removeView(contentClyt);
            parent.removeView(paddingLlyt);

            parent.addView(paddingLlyt);
            parent.addView(contentClyt);
        }else {
            parent.removeView(contentClyt);
            parent.removeView(paddingLlyt);

            parent.addView(contentClyt);
            parent.addView(paddingLlyt);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceInfo(DeviceInfoEvent deviceInfoEvent){
        if(trafficLightWnd == null){
            if(CommUtils.isVersionGreaterThan(GlobalVariables.deviceInfo.getSwVer(), GlobalVariables.OBU_VERSION_SUPPORT_LANE_MAP)){
                trafficLightWnd = new TrafficLightWndNew(this);
            }else {
                trafficLightWnd = new TrafficLightWnd(this);
            }
        }
    }


    private boolean checkShowLevel(int nextShowLevel){
        boolean pass = false;
        if(nextShowLevel > GlobalVariables.currSHowLevel){
            pass = true;

            vRealTime.reset();
            vRealTime.setVisibility(View.GONE);

            hideParking();

            controlHandler.removeCallbacks(null);
            controlLlyt.setVisibility(View.GONE);

            hasManuShowTire = false;
            showTirePressContent(false, null);

            GlobalVariables.currSHowLevel = nextShowLevel;
        }else if(nextShowLevel == GlobalVariables.currSHowLevel){
            pass = true;
        }
        return pass;
    }

}