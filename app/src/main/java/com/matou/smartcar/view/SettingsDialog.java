package com.matou.smartcar.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elvishew.xlog.XLog;
import com.flyjingfish.gradienttextviewlib.GradientTextView;
import com.matou.smartcar.MainActivity;
import com.matou.smartcar.R;
import com.matou.smartcar.bean.BaseResult;
import com.matou.smartcar.bean.VersionBean;
import com.matou.smartcar.event.UpgradeEvent;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.net.Api;
import com.matou.smartcar.net.BaseObserver;
import com.matou.smartcar.net.NetManager;
import com.matou.smartcar.net.RxSchedulers;
import com.matou.smartcar.util.AppUtils;
import com.matou.smartcar.util.DisplayUtil;
import com.matou.smartcar.util.MD5Utils;
import com.matou.smartcar.util.SPUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 *
 * @author ranfeng
 */
public class SettingsDialog extends Dialog implements SettingSwitch.OnCheckedChangeListener {

    private final MainActivity mainAct;
    private LinearLayout tab0Llyt;
    private LinearLayout tab1Llyt;
    private TextView tab0Tv;
    private TextView tab1Tv;

    private ProgressBar loadingView;
    private Button upgradeBtn;

    private GradientTextView versionGtv;

    private LinearLayout sceneSettingsLlyt;
    private LinearLayout paramSettingsLlyt;

    private CapsuleButton tipPosCbtn;

    private ImageView newVerIv;

    private SettingSwitch fcwSs;
    private SettingSwitch bsmSs;
    private SettingSwitch icwSs;
    private SettingSwitch pcrSs;
    private SettingSwitch rlvwSs;
    private SettingSwitch traffControlSs;
    private SettingSwitch greenWaveSs;
    private SettingSwitch traffInfoSs;
    private SettingSwitch limitSpeedSs;
    private SettingSwitch specialVehicleSs;
    private SettingSwitch illegalParkingSs;
    private SettingSwitch mixLightSs;

    public SettingsDialog(MainActivity mainAct) {
        super(mainAct);
        this.mainAct = mainAct;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_dialog);

        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = window.getAttributes();
            // 获取屏幕宽度
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            int screenWidth = displayMetrics.widthPixels;

            lp.width = screenWidth / 2 - DisplayUtil.dpToPx(getContext(), 30);
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.START;
            lp.dimAmount = 0.0f;
            lp.x = DisplayUtil.dpToPx(getContext(), 50);
            window.setAttributes(lp);
        }


        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        tab0Llyt = findViewById(R.id.llyt_tab0);
        tab1Llyt = findViewById(R.id.llyt_tab1);
        tab0Tv = findViewById(R.id.tv_tab0);
        tab1Tv = findViewById(R.id.tv_tab1);
        upgradeBtn = findViewById(R.id.btn_upgrade);
        upgradeBtn.setTextSize(DisplayUtil.spToPx(getContext(), 8));
        loadingView = findViewById(R.id.loadingView);
        versionGtv = findViewById(R.id.gtv_version);
        sceneSettingsLlyt = findViewById(R.id.llyt_scene_settings);
        paramSettingsLlyt = findViewById(R.id.llyt_param_settings);
        newVerIv = findViewById(R.id.iv_new_version);
        tipPosCbtn = findViewById(R.id.cbtn_tip_postion);
        fcwSs = findViewById(R.id.ss_fcw);
        bsmSs = findViewById(R.id.ss_bsm);
        icwSs = findViewById(R.id.ss_icw);
        pcrSs = findViewById(R.id.ss_pcr);
        rlvwSs = findViewById(R.id.ss_rlvw);
        traffControlSs = findViewById(R.id.ss_traffic_control);
        greenWaveSs = findViewById(R.id.ss_green_wave);
        traffInfoSs = findViewById(R.id.ss_traffic_info);
        limitSpeedSs = findViewById(R.id.ss_limit_speed);
        specialVehicleSs = findViewById(R.id.ss_special_vehicle);
        illegalParkingSs = findViewById(R.id.ss_illegal_parking);
        mixLightSs = findViewById(R.id.ss_lane_traffic_light);

        versionGtv.setText("V" + AppUtils.getVersionName());

        tab0Llyt.setOnClickListener(v -> selectTab(0));

        tab1Llyt.setOnClickListener(v -> selectTab(1));

        selectTab(0);
    }

    private void selectTab(int tab) {
        if(tab == 0){
            tab0Llyt.setBackgroundResource(R.drawable.bg_settings_tab0_selected);
            tab1Llyt.setBackgroundResource(R.drawable.bg_settings_tab1_normal);
            tab0Tv.setTextColor(Color.parseColor("#ffffff"));
            tab1Tv.setTextColor(Color.parseColor("#6890e0"));

            loadingView.setVisibility(View.GONE);
            sceneSettingsLlyt.setVisibility(View.VISIBLE);
            paramSettingsLlyt.setVisibility(View.GONE);

            fcwSs.setCheck(GlobalVariables.fcwEnable);
            bsmSs.setCheck(GlobalVariables.bsmEnable);
            icwSs.setCheck(GlobalVariables.icwEnable);
            pcrSs.setCheck(GlobalVariables.pcrEnable);
            rlvwSs.setCheck(GlobalVariables.rlvwEnable);
            traffControlSs.setCheck(GlobalVariables.traffControlEnable);
            greenWaveSs.setCheck(GlobalVariables.greenWaveEnable);
            traffInfoSs.setCheck(GlobalVariables.traffInfoEnable);
            limitSpeedSs.setCheck(GlobalVariables.limitSpeedEnable);
            specialVehicleSs.setCheck(GlobalVariables.specialVehicleEnable);
            illegalParkingSs.setCheck(GlobalVariables.illagelParkingEnable2);
            mixLightSs.setCheck(GlobalVariables.mixLightEnable);

            fcwSs.setOnCheckedChangeListener(this);
            bsmSs.setOnCheckedChangeListener(this);
            icwSs.setOnCheckedChangeListener(this);
            pcrSs.setOnCheckedChangeListener(this);
            rlvwSs.setOnCheckedChangeListener(this);
            traffControlSs.setOnCheckedChangeListener(this);
            greenWaveSs.setOnCheckedChangeListener(this);
            traffInfoSs.setOnCheckedChangeListener(this);
            limitSpeedSs.setOnCheckedChangeListener(this);
            specialVehicleSs.setOnCheckedChangeListener(this);
            illegalParkingSs.setOnCheckedChangeListener(this);
            mixLightSs.setOnCheckedChangeListener(this);

        }else {
            tab0Llyt.setBackgroundResource(R.drawable.bg_settings_tab0_normal);
            tab1Llyt.setBackgroundResource(R.drawable.bg_settings_tab1_selected);
            tab0Tv.setTextColor(Color.parseColor("#6890e0"));
            tab1Tv.setTextColor(Color.parseColor("#ffffff"));

            loadingView.setVisibility(View.VISIBLE);
            sceneSettingsLlyt.setVisibility(View.GONE);
            paramSettingsLlyt.setVisibility(View.GONE);

            boolean isRight = (boolean) SPUtil.get("isRight", false);
            tipPosCbtn.setSelect(isRight ? 2 : 0);
            tipPosCbtn.setOnButtonClickListener((view, index) -> {
                if(index == 2){
                    SPUtil.set("isRight", true);
                    mainAct.setContentPostion(true);
                }else {
                    SPUtil.set("isRight", false);
                    mainAct.setContentPostion(false);
                }
            });

            upgradeBtn.setOnClickListener(v -> {
                upgradeBtn.setEnabled(false);
                mainAct.checkVersion(true);
            });

            checkVersion();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpgradeMsg(UpgradeEvent upgradeEvent) {
        XLog.w("---> onUpgradeMsg = " + upgradeEvent);
        /**
         * 升级状态
         * 1：升级中，progress有效
         * 0：升级包下载成功
         * -1：用户取消升级
         * -2：升级失败，网络异常
         * -3：升级失败，其他原因
         */
        if(upgradeEvent.getStatus() == 1){
            upgradeBtn.setEnabled(false);
            upgradeBtn.setTextSize(DisplayUtil.spToPx(getContext(), 8));
            upgradeBtn.setText("下载中("+ upgradeEvent.getProgress() + "%)");
        }else if(upgradeEvent.getStatus() == 2){
            upgradeBtn.setEnabled(true);
            upgradeBtn.setTextSize(DisplayUtil.spToPx(getContext(), 8));
            upgradeBtn.setText("检查更新");
            Toast.makeText(getContext(), "当前已是最新版本！", Toast.LENGTH_SHORT).show();
        }else if(upgradeEvent.getStatus() == 0){
            upgradeBtn.setEnabled(false);
            upgradeBtn.setTextSize(DisplayUtil.spToPx(getContext(), 8));
            upgradeBtn.setText("下载成功");
        }else if(upgradeEvent.getStatus() == -1){
            upgradeBtn.setEnabled(true);
            upgradeBtn.setTextSize(DisplayUtil.spToPx(getContext(), 8));
            upgradeBtn.setText("检查更新");
            Toast.makeText(getContext(), "用户取消升级！", Toast.LENGTH_SHORT).show();
        }else if(upgradeEvent.getStatus() == -2){
            upgradeBtn.setEnabled(true);
            upgradeBtn.setTextSize(DisplayUtil.spToPx(getContext(), 8));
            upgradeBtn.setText("检查更新");
            Toast.makeText(getContext(), "升级失败，请检查网络！", Toast.LENGTH_SHORT).show();
        }else {
            upgradeBtn.setEnabled(true);
            upgradeBtn.setTextSize(DisplayUtil.spToPx(getContext(), 8));
            upgradeBtn.setText("检查更新");
            Toast.makeText(getContext(), "升级失败！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查更新
     */
    private void checkVersion() {
        String time = String.valueOf(System.currentTimeMillis());
        String encrypt = MD5Utils.encrypt(NetManager.getAppId() + "|" + time + "|" + "QAZzaq1!");
        NetManager.getApi(NetManager.mBaseUrl, Api.class)
                .getVersion(NetManager.getAppId(), time, encrypt)
                .compose(RxSchedulers.applySchedulers())
                .subscribe(new BaseObserver<BaseResult<VersionBean>>() {
                    @Override
                    public void onSuccess(BaseResult<VersionBean> baseResult) {

                        loadingView.setVisibility(View.GONE);
                        paramSettingsLlyt.setVisibility(View.VISIBLE);

                        VersionBean data = baseResult.data;
                        boolean software = data.isSoftware();
                        String versionName = AppUtils.getVersionName();
                        int compare = versionName.compareTo(data.getVersion());
                        if (software && compare < 0) {
                            newVerIv.setVisibility(View.VISIBLE);
                        }else {
                            newVerIv.setVisibility(View.INVISIBLE);
                        }
                        XLog.w("checkVersion onSuccess");
                    }

                    @Override
                    public void onErrorMsg(String msg) {
                        XLog.w("checkVersion onErrorMsg: " + msg);

                        if(msg.contains("NetError")){
                            Toast.makeText(getContext(), "获取升级信息失败，请检查网络！", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), "获取升级信息失败！", Toast.LENGTH_SHORT).show();
                        }

                        loadingView.setVisibility(View.GONE);
                        paramSettingsLlyt.setVisibility(View.VISIBLE);
                    }
                });

    }

    @Override
    public void onCheckedChanged(View buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.ss_fcw:
                GlobalVariables.fcwEnable = isChecked;
                SPUtil.set("fcwEnable", isChecked);
                break;

            case R.id.ss_bsm:
                GlobalVariables.bsmEnable = isChecked;
                SPUtil.set("bsmEnable", isChecked);
                break;

            case R.id.ss_icw:
                GlobalVariables.icwEnable = isChecked;
                SPUtil.set("icwEnable", isChecked);
                break;

            case R.id.ss_pcr:
                GlobalVariables.pcrEnable = isChecked;
                SPUtil.set("pcrEnable", isChecked);
                break;

            case R.id.ss_rlvw:
                GlobalVariables.rlvwEnable = isChecked;
                SPUtil.set("rlvwEnable", isChecked);
                break;

            case R.id.ss_traffic_control:
                GlobalVariables.traffControlEnable = isChecked;
                SPUtil.set("traffControlEnable", isChecked);
                break;

            case R.id.ss_green_wave:
                GlobalVariables.greenWaveEnable = isChecked;
                SPUtil.set("greenWaveEnable", isChecked);
                break;

            case R.id.ss_traffic_info:
                GlobalVariables.traffInfoEnable = isChecked;
                SPUtil.set("traffInfoEnable", isChecked);
                break;

            case R.id.ss_limit_speed:
                GlobalVariables.limitSpeedEnable = isChecked;
                SPUtil.set("limitSpeedEnable", isChecked);
                break;

            case R.id.ss_special_vehicle:
                GlobalVariables.specialVehicleEnable = isChecked;
                SPUtil.set("specialVehicleEnable", isChecked);
                break;

            case R.id.ss_illegal_parking:
                GlobalVariables.illagelParkingEnable2 = isChecked;
                SPUtil.set("illagelParkingEnable2", isChecked);
                break;

            case R.id.ss_lane_traffic_light:
                GlobalVariables.mixLightEnable = isChecked;
                SPUtil.set("mixLightEnable", isChecked);
                break;

            default:

                break;
        }
    }
}
