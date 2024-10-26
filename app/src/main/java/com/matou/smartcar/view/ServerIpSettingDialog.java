package com.matou.smartcar.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.matou.smartcar.R;
import com.matou.smartcar.bean.DataInfoBean;
import com.matou.smartcar.bean.IpAddressBean;
import com.matou.smartcar.event.RTKInfoEvent;
import com.matou.smartcar.event.StatusEvent;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.manager.WifiPingManager;
import com.matou.smartcar.speaker.SpeakerHandler;
import com.matou.smartcar.util.AppUtils;
import com.matou.smartcar.util.DeviceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServerIpSettingDialog extends Dialog {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_close)
    TextView tvClose;
    @BindView(R.id.ed)
    EditText ed;
    @BindView(R.id.edName)
    EditText edName;
    @BindView(R.id.edPwd)
    EditText edPwd;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.rv)
    RecyclerView rv;

    @BindView(R.id.tv_ll_speed)
    TextView llSpeedTv;

    @BindView(R.id.tv_pc5_connect_status)
    TextView pc5ConnStatusTv;

    @BindView(R.id.tv_pc5_data_status)
    TextView pc5DataStatusTv;

    @BindView(R.id.tv_uu_connect_status)
    TextView uuConnStatusTv;

    @BindView(R.id.tv_uu_data_status)
    TextView uuDataStatusTv;

    @BindView(R.id.tv_model_connect_status)
    TextView modelConnStatusTv;

    @BindView(R.id.tv_model_data_status)
    TextView modelDataStatusTv;

    @BindView(R.id.tv_parking)
    TextView parkingTv;

    @BindView(R.id.switch_rem_wifi)
    Switch remWifiSw;

    @BindView(R.id.tv_rtk_status)
    TextView rtkStatusTv;


    private Context context;
    private List<IpAddressBean> mIpAddressBeans;
    private BaseQuickAdapter<IpAddressBean, BaseViewHolder> mIpAdapter;

    public ServerIpSettingDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        this.context = context;
        initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window == null) {
            return;
        }

        EventBus.getDefault().register(this);
        WindowManager.LayoutParams wmlp = window.getAttributes();
        wmlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wmlp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        wmlp.gravity = Gravity.CENTER;
        window.setAttributes(wmlp);
    }

    @SuppressLint("SetTextI18n")
    private void initView() {


        View view = LayoutInflater.from(context).inflate(R.layout.dialog_server_ip_setting, null, false);

        ButterKnife.bind(this, view);
        setContentView(view);

        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirmClickListener != null) {
                    String ip = ed.getText().toString().trim();
                    String username = edName.getText().toString().trim();
                    String pwd = edPwd.getText().toString().trim();
                    onConfirmClickListener.onClick(ip, username, pwd);

                }
                dismiss();
            }
        });
        view.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mIpAddressBeans = initIPData();
        rv.setLayoutManager(new LinearLayoutManager(context));
        mIpAdapter = new BaseQuickAdapter<IpAddressBean, BaseViewHolder>(R.layout.item_ip_address, mIpAddressBeans) {
            @Override
            protected void convert(BaseViewHolder helper, final IpAddressBean item) {
                helper.setText(R.id.tvName, item.getName());

                View llRoot = helper.getView(R.id.llRoot);
                TextView tvName = helper.getView(R.id.tvName);
                tvName.setText(item.getName() + item.getAddress());
                if (item.isSelected()) {
                    tvName.setTextColor(0xffDAA710);
                } else {
                    tvName.setTextColor(0xFF99ABD4);
                }
                llRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ed.setText(item.getAddress());
                        for (IpAddressBean mIpAddressBean : mIpAddressBeans) {
                            mIpAddressBean.setSelected(false);
                        }
                        item.setSelected(true);
                        mIpAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        rv.setAdapter(mIpAdapter);

        onPingStatus(GlobalVariables.pingStatus);
        //showTitleInfo("");

        onMqttMsg(null);

        if(GlobalVariables.parkingBean != null){
            parkingTv.setText("违停: " + GlobalVariables.parkingBean);
        }

        //remWifiSw.setOnClickListener(v -> SpeakerHandler.getInstance().speak("您已超速", SpeakerHandler.SPEEDING));

        remWifiSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GlobalVariables.remWifi = isChecked;
            }
        });


    }

    private void showTitleInfo(String pingInfo) {
        String obuVer = GlobalVariables.deviceInfo == null ? "未知" : GlobalVariables.deviceInfo.getSwVer();
        String tyconfig = GlobalVariables.hasTyconfig ? (GlobalVariables.lowTyVal + "-" + GlobalVariables.highTyVal + "bar") : "未知";
        tvTitle.setText("设置(App版本:" + AppUtils.getAppVersionName(getContext()) + ", 后视镜Id:" + DeviceUtil.getDeviceId() + ", Obu版本: " + obuVer
                + ", 胎压配置:" + tyconfig + pingInfo + ")");
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMqttMsg(StatusEvent msg) {
        if(GlobalVariables.pc5ConnStatus == 0){
            pc5ConnStatusTv.setText("wifi和pc5未连接：" + GlobalVariables.pc5ConnDesc);
            pc5ConnStatusTv.setTextColor(Color.parseColor("#aaff0000"));
        }else if(GlobalVariables.pc5ConnStatus == 1){
            pc5ConnStatusTv.setText("wifi已连接，pc5未连接：" + GlobalVariables.pc5ConnDesc);
            pc5ConnStatusTv.setTextColor(Color.parseColor("#aaff0000"));
        }else {
            pc5ConnStatusTv.setText("wifi和pc5都已连接");
            pc5ConnStatusTv.setTextColor(Color.parseColor("#aa00ff00"));
        }
        pc5DataStatusTv.setText(GlobalVariables.pc5DataStatus == 1 ? "正常":"异常");
        pc5DataStatusTv.setTextColor(GlobalVariables.pc5DataStatus == 1 ? Color.parseColor("#aa00ff00") : Color.parseColor("#aaff0000"));

        if(GlobalVariables.uuConnStatus == 0){
            uuConnStatusTv.setText("未连接：" + GlobalVariables.uuConnDesc);
            uuConnStatusTv.setTextColor(Color.parseColor("#aaff0000"));
        }else {
            uuConnStatusTv.setText("已连接");
            uuConnStatusTv.setTextColor(Color.parseColor("#aa00ff00"));
        }
        uuDataStatusTv.setText(GlobalVariables.uuDataStatus == 1 ? "正常":"异常");
        uuDataStatusTv.setTextColor(GlobalVariables.uuDataStatus == 1 ? Color.parseColor("#aa00ff00") : Color.parseColor("#aaff0000"));

        if(GlobalVariables.modelConnStatus == 0){
            modelConnStatusTv.setText("未连接：" + GlobalVariables.modelConnDesc);
            modelConnStatusTv.setTextColor(Color.parseColor("#aaff0000"));
        }else {
            modelConnStatusTv.setText("已连接");
            modelConnStatusTv.setTextColor(Color.parseColor("#aa00ff00"));
        }
        modelDataStatusTv.setText(GlobalVariables.modelDataStatus == 1 ? "正常":"异常");
        modelDataStatusTv.setTextColor(GlobalVariables.modelDataStatus == 1 ? Color.parseColor("#aa00ff00") : Color.parseColor("#aaff0000"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRTKInfo(RTKInfoEvent msg) {
        if(GlobalVariables.rtkInfo == null){
            rtkStatusTv.setText("");
        }else {
            rtkStatusTv.setText(GlobalVariables.rtkInfo.toString());
        }
    }

    private List<IpAddressBean> initIPData() {
//        金溢192.168.110.1:1883
//        星云192.168.10.224:1883
//        华砺192.168.10.1:1883
//        希迪192.168.2.10:1883
//        大椽192.168.9.1:1883
//        联创192.168.1.250:1883
        List<IpAddressBean> ipData = new ArrayList<>();

        IpAddressBean ipAddressBean;

        ipAddressBean = new IpAddressBean();
        ipAddressBean.setName("");
        ipAddressBean.setAddress("192.168.110.1:1883");
        ipData.add(ipAddressBean);

        ipAddressBean = new IpAddressBean();
        ipAddressBean.setName("");
        ipAddressBean.setAddress("192.168.10.224:1883");
        ipData.add(ipAddressBean);

        ipAddressBean = new IpAddressBean();
        ipAddressBean.setName("");
        ipAddressBean.setAddress("192.168.1.10:1883");
        ipData.add(ipAddressBean);

        ipAddressBean = new IpAddressBean();
        ipAddressBean.setName("");
        ipAddressBean.setAddress("192.168.2.10:1883");
        ipData.add(ipAddressBean);

        ipAddressBean = new IpAddressBean();
        ipAddressBean.setName("");
        ipAddressBean.setAddress("192.168.9.1:1883");
        ipData.add(ipAddressBean);

        ipAddressBean = new IpAddressBean();
        ipAddressBean.setName("");
        ipAddressBean.setAddress("192.168.1.250:1883");
        ipData.add(ipAddressBean);

        return ipData;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("SetTextI18n")
    public void showLlSpeed(DataInfoBean.HVMSGBean hvmsg){
        DataInfoBean.HVMSGBean.PosBean pos = hvmsg.getPos();
        float speed = hvmsg.getSpeed();
        if(pos != null){
            llSpeedTv.setText(pos.getLatitude() + " " + pos.getLongitude() + " " + pos.getElevation() + "/ " + speed);
        }else {
            llSpeedTv.setText("---/ " + speed);
        }

    }


    private OnConfirmClickListener onConfirmClickListener;

    public ServerIpSettingDialog setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
        return this;
    }

    public ServerIpSettingDialog setDefaultIP(String defaultIp) {
        ed.setHint(defaultIp);
        return this;
    }

    public interface OnConfirmClickListener {
        void onClick(String ip, String username, String pwd);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPingStatus(WifiPingManager.PingStatus pingStatus) {
        String pingInfo = "";
        switch (pingStatus.status){
            case WifiPingManager.PingStatus.INIT:

                break;

            case WifiPingManager.PingStatus.NOT_JY_WIFI:
                pingInfo = ", 当前未连接金溢Obu";
                break;

            case WifiPingManager.PingStatus.SUCCESS:
                pingInfo = ", ping金溢Obu 成功";
                break;

            case WifiPingManager.PingStatus.FAILED:
                pingInfo = ", ping金溢Obu 失败:" + pingStatus.failCount + "次";
                break;

            default:

                break;
        }

        showTitleInfo(pingInfo);
    }



}
