package com.matou.smartcar.view;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.elvishew.xlog.XLog;
import com.matou.smartcar.MainActivity;
import com.matou.smartcar.R;
import com.matou.smartcar.bean.AlertTypeBean;
import com.matou.smartcar.bean.DataInfoBean;
import com.matou.smartcar.bean.RsiResBean;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.global.WarningTypeConstants;
import com.matou.smartcar.global.ZYWarningLevelConstants;
import com.matou.smartcar.util.AppUtils;
import com.matou.smartcar.util.GsonUtil;
import com.matou.smartcar.util.RteManager;
import com.matou.smartcar.view.base.BaseWnd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import butterknife.BindView;

/**
 * @author ranfeng
 */
public class TrafficEventWnd extends BaseWnd {

    @BindView(R.id.vRealTime)
    RealTimeView vRealTime;
    // private long preTime;

    private final Handler handler = new Handler();

    private final Runnable runnable = () -> {
        // XLog.e("hide event window");
        ((MainActivity) context).updateEvent(false, false, null, 0);
    };


    public TrafficEventWnd(Context context) {
        super(context);
        init();
    }

    private void init() {
        wndType = GlobalVariables.WND_TYPE_1;
        //preTime = System.currentTimeMillis();
    }

    @Override
    public int bindLayout() {
        return R.layout.traffic_event_lyt;
    }

    public void bindData(DataInfoBean data) {
        DataInfoBean.RVMSGBean showVinfoBean = null;
        //todo: 暂时隐藏闯红灯
        DataInfoBean.TrafficLightBean trafficLight = data.getTrafficLight();
        if (trafficLight != null) {
            List<DataInfoBean.TrafficLightBean.LightBean> lightList = trafficLight.getLight();
            if (lightList != null) {
                for (DataInfoBean.TrafficLightBean.LightBean light : lightList) {

                    /**
                     * 处理闯红灯提醒，考虑三种情况： obu做判断
                     * 1、当前道路只有一个车道
                     * 2、车道级地图能够明确知道当前在哪个车道上（非车道地图 右转是否受灯控）—— 待实现
                     * 3、具有导航的情况下明确知道当前要往哪个方向行驶（需考虑同方向多车道相位不一致情况）—— 待实现
                     */

                    if (light != null) {
                        int rlvwFlag = light.getRlvwFlag();
                        int currDir = light.getCurrDir();
                        if (rlvwFlag == 1 && currDir == 1) {
                            DataInfoBean.RVMSGBean rvmsgBean = new DataInfoBean.RVMSGBean();
                            DataInfoBean.RVMSGBean.V2vEventBean v2v = new DataInfoBean.RVMSGBean.V2vEventBean();
                            v2v.setEventStr(WarningTypeConstants.Event_RLVW);
                            rvmsgBean.setV2vEvent(v2v);
                            showVinfoBean = rvmsgBean;
                            showVinfoBean.setOnlyTts(AppUtils.isOnlyTts(v2v.getEventStr()));
                            break;
                        }
                    }
                }
            }
        }


        List<DataInfoBean.RVMSGBean> rvmsg = data.getRVMSG();

        //这里 获取 其他车辆里面有没有v2v信息,如果有 挑选优先级最高的v2v信息
        if (rvmsg != null && rvmsg.size() != 0) {
            int tempLevel = 0;
            double tempTtc = 10000;
            for (DataInfoBean.RVMSGBean vinfoBean : rvmsg) {
                //先判断场景 在判断tts 且要判断当前事件的显示level
                if (vinfoBean != null) {
                    DataInfoBean.RVMSGBean.V2vEventBean v2vEvent = vinfoBean.getV2vEvent();

                    if (v2vEvent != null) {
                        String eventStr = v2vEvent.getEventStr();

                        int level = ZYWarningLevelConstants.handleWarningLevel(eventStr);
                        double ttc = v2vEvent.getTTC();

                        if (level != 0 && level >= tempLevel && ttc < tempTtc) {
                            tempLevel = level;
                            tempTtc = ttc;
                            showVinfoBean = vinfoBean;
                            showVinfoBean.setOnlyTts(AppUtils.isOnlyTts(eventStr));
                        }
                    }

                }

            }
        }
        long nowTime = System.currentTimeMillis();

        XLog.w("show event window showVinfoBean = " + (showVinfoBean == null ? "null" : GsonUtil.beanToJson(showVinfoBean)));

        // 针对场景开关进行拦截
        if(showVinfoBean != null && showVinfoBean.getV2vEvent() != null){
            String eventStr = showVinfoBean.getV2vEvent().getEventStr();
            if(!GlobalVariables.fcwEnable && WarningTypeConstants.Event_FCW.equals(eventStr)){
                showVinfoBean = null;
            }else if(!GlobalVariables.bsmEnable && WarningTypeConstants.Event_BSW.equals(eventStr)){
                showVinfoBean = null;
            }else if(!GlobalVariables.icwEnable && WarningTypeConstants.Event_ICW.equals(eventStr)){
                showVinfoBean = null;
            }else if(!GlobalVariables.pcrEnable && (WarningTypeConstants.Event_PCR.equals(eventStr) || WarningTypeConstants.Event_VRUCW.equals(eventStr) || WarningTypeConstants.Event_YPC.equals(eventStr))){
                showVinfoBean = null;
            }else if(!GlobalVariables.specialVehicleEnable && WarningTypeConstants.Event_EVW.equals(eventStr)){
                showVinfoBean = null;
            }
        }


        if (showVinfoBean == null) {
            //3s都没有新的事件来 那就复原
//            if (nowTime - preTime > GlobalVariables.SHOW_DURATION) {
//                // XLog.e("hide event window");
//                if (AppUtils.isRunningForeground()) {
//                    ((MainActivity) context).updateEvent(false, false, null, 0);
//                } else {
//                    hideFloatWindow();
//                }
//                preTime = nowTime;
//            }
            //有事件 就显示事件
        } else {
            ((MainActivity) context).updateEvent(true, showVinfoBean.isOnlyTts(), showVinfoBean, data.getHVMSG().getHeading());
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(runnable, 3000);
        }
        //场景为空rsi 才播报
        if(GlobalVariables.traffInfoEnable){
            handleRsi(data, showVinfoBean == null);
        }

    }

    @Override
    public boolean hideFloatWindow() {
        //XLog.e("========================> event hideFloatWindow");
        vRealTime.reset();
        return super.hideFloatWindow();
    }

    private void handleRsi(DataInfoBean data, boolean canPlay) {

        MainActivity activity = (MainActivity) context;

        List<DataInfoBean.RSIEventBean> allEvent = data.getRSIEvent();
        if (allEvent == null || allEvent.size() == 0) {
            activity.updateNational(null, false);//传null直接隐藏
            return;
        }

        Collections.reverse(allEvent);
        allEvent = allEvent.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(
                Comparator.comparing(DataInfoBean.RSIEventBean::getAlertType)
        )), ArrayList::new));

        // 排序
        allEvent.sort((o1, o2) -> o2.getDistance() < o1.getDistance() ? 1 : -1);

        List<DataInfoBean.RSIEventBean> rsiEvent = new ArrayList<>();
        List<DataInfoBean.RSIEventBean> rteEvent = new ArrayList<>();
        for (DataInfoBean.RSIEventBean rsiEventBean : allEvent) {
            if (TextUtils.equals(rsiEventBean.getEvent(), "HLW")) {
                rteEvent.add(rsiEventBean);
            } else {
                rsiEvent.add(rsiEventBean);
            }
        }
        List<RsiResBean> rsiResBeans = RteManager.getInstance(context).handleRte(rteEvent, true);
        //如果没有rsi信息 那么就给他隐藏起来


        List<RsiResBean> rsiIcons = new ArrayList<>();
        for (DataInfoBean.RSIEventBean rsiEventBean : rsiEvent) {
            int distance = (int) rsiEventBean.getDistance();

            double speed = 0;
            if(data.getHVMSG() != null){
                speed = data.getHVMSG().getSpeed();
            }

            RsiResBean rsiResBean = getRsiRes(rsiEventBean.getAlertType(), rsiEventBean.getDescription(), rsiEventBean.getRsiId(), true, distance, speed);
            if (rsiResBean != null) {
                rsiIcons.add(rsiResBean);
            }

        }
        if (rsiResBeans != null) {
            rsiIcons.addAll(rsiResBeans);
        }

        //坐标显示rsi图标
        activity.updateNational(AppUtils.getShowRsiList(rsiIcons), canPlay);
    }


    private RsiResBean getRsiRes(int alertType, String descriptions, String rsiId, boolean playTTs, int distance, double speed) {
        RsiResBean rsiResBean = new RsiResBean();
        int res = 0;
        String description = null;
        String value = null;
        int priority = 0;

        AlertTypeBean alertTypeBean = AppUtils.parseAlertBean(descriptions);
        description = alertTypeBean.getSubType();
        value = alertTypeBean.getValue();

        String playDescription = "";
        switch (alertType) {
            case 1:
                if (TextUtils.equals(description, "a")) {
                    playDescription = "前方交叉路口";
                    res = R.mipmap.ic_1a;
                } else if (TextUtils.equals(description, "b")) {
                    playDescription = "前方连续路口";
                    res = R.mipmap.ic_1b;
                } else if (TextUtils.equals(description, "B")) {
                    playDescription = "前方连续路口";
                    res = R.mipmap.ic_1double_b;
                } else if (TextUtils.equals(description, "c")) {
                    playDescription = "注意前方左侧汇入车辆";
                    res = R.mipmap.ic_1c;
                } else if (TextUtils.equals(description, "d")) {
                    playDescription = "注意前方右侧汇入车辆";
                    res = R.mipmap.ic_1d;
                } else if (TextUtils.equals(description, "e")) {
                    playDescription = "前方左侧岔路";
                    res = R.mipmap.ic_1e;
                } else if (TextUtils.equals(description, "f")) {
                    playDescription = "前方右侧岔路";
                    res = R.mipmap.ic_1f;
                } else if (TextUtils.equals(description, "g")) {
                    playDescription = "前方T字路口";
                    res = R.mipmap.ic_1g;
                } else if (TextUtils.equals(description, "h")) {
                    playDescription = "前方T字路口";
                    res = R.mipmap.ic_1h;
                } else if (TextUtils.equals(description, "i")) {
                    playDescription = "前方T字路口";
                    res = R.mipmap.ic_1i;
                } else if (TextUtils.equals(description, "j")) {
                    playDescription = "前方环岛";
                    res = R.mipmap.ic_1j;
                }
                playDescription = "";
                priority = GlobalVariables.RSI_LEVEL_1;
                break;
            case 2:
                priority = GlobalVariables.RSI_LEVEL_2;
                if (TextUtils.equals(description, "a")) {
                    playDescription = "前方左转急弯";
                    res = R.mipmap.ic_2a;
                    break;
                } else if (TextUtils.equals(description, "b")) {
                    playDescription = "前方右转急弯";
                    res = R.mipmap.ic_2b;
                    break;
                } else {
                    playDescription = "前方急转弯";
                    res = R.mipmap.ic_2a;
                    break;
                }
            case 3:
                priority = GlobalVariables.RSI_LEVEL_2;
                if (TextUtils.equals(description, "a")) {
                    playDescription = "前方反向弯路";
                    res = R.mipmap.ic_3a;
                    break;
                } else {
                    playDescription = "前方反向弯路";
                    res = R.mipmap.ic_3b;
                    break;
                }
            case 4:
                priority = GlobalVariables.RSI_LEVEL_2;
                playDescription = "前方连续转弯";
                res = R.mipmap.ic_4;
                break;

            case 5:
                priority = GlobalVariables.RSI_LEVEL_2;
                if (TextUtils.equals(description, "a")) {
                    playDescription = "前方上陡坡";
                    res = R.mipmap.ic_5a;
                    break;
                } else if (TextUtils.equals(description, "b")) {
                    playDescription = "前方下陡坡";
                    res = R.mipmap.ic_5b;
                    break;
                } else {
                    playDescription = "前方陡坡";
                    res = R.mipmap.ic_5b;
                    break;
                }
            case 6:
                priority = GlobalVariables.RSI_LEVEL_2;
                playDescription = "前方连续下坡";
                res = R.mipmap.ic_6;
                break;
            case 7:
                priority = GlobalVariables.RSI_LEVEL_1;
                if (TextUtils.equals(description, "a")) {
                    playDescription = String.format("前方%d米两侧变窄", distance);
                    res = R.mipmap.ic_7a;

                } else if (TextUtils.equals(description, "b")) {
                    playDescription = String.format("前方%d米右侧变窄", distance);
                    res = R.mipmap.ic_7b;
                } else if (TextUtils.equals(description, "b")) {
                    playDescription = String.format("前方%d米左侧变窄", distance);
                    res = R.mipmap.ic_7c;
                } else {
                    playDescription = String.format("前方%d米侧变窄", distance);
                    res = R.mipmap.ic_7a;
                }
                playDescription = "";
                break;
            case 8:
                //playDescription = String.format("即将%d米进入窄桥", distance);
                res = R.mipmap.ic_8;
                priority = GlobalVariables.RSI_LEVEL_1;
                break;
            case 9:
                priority = GlobalVariables.RSI_LEVEL_1;
                res = R.mipmap.ic_9;
                break;
            case 10:
                playDescription = "注意人行横道";
                res = R.mipmap.ic_10;
                priority = GlobalVariables.RSI_LEVEL_3;
                break;

            case 11:
                playDescription = "前方学校";
                res = R.mipmap.ic_11;
                priority = GlobalVariables.RSI_LEVEL_3;
                break;
            case 12:
                //playDescription = "注意牲畜";
                res = R.mipmap.ic_12;
                priority = GlobalVariables.RSI_LEVEL_1;
                break;

            case 13:
                //playDescription = "注意野生动物";
                res = R.mipmap.ic_13;
                priority = GlobalVariables.RSI_LEVEL_1;
                break;
//            case 14:
//                playTts("注意信号灯", rsiId, playTTs);
//
//                return R.mipmap.ic_14;

            case 15:
                priority = GlobalVariables.RSI_LEVEL_1;
                if (TextUtils.equals(description, "a")) {
                    playDescription = "即将经过落石区域";
                    res = R.mipmap.ic_15a;
                    break;
                } else {
                    playDescription = "即将经过落石区域";
                    res = R.mipmap.ic_15b;
                    break;
                }
            case 16:
                playDescription = "注意横风";
                res = R.mipmap.ic_16;
                priority = GlobalVariables.RSI_LEVEL_1;
                break;
            case 17:
                playDescription = "路面易滑，请小心行驶";
                res = R.mipmap.ic_17;
                priority = GlobalVariables.RSI_LEVEL_3;
                break;
            case 18:
                priority = GlobalVariables.RSI_LEVEL_1;
                if (TextUtils.equals(description, "b")) {
                    playDescription = "傍山险路，请小心行驶";
                    res = R.mipmap.ic_18b;
                    break;
                } else {
                    playDescription = "傍山险路，请小心行驶";
                    res = R.mipmap.ic_18a;
                    break;
                }
            case 19:
                priority = GlobalVariables.RSI_LEVEL_1;
                if (TextUtils.equals(description, "b")) {
                    playDescription = "即将经过堤坝路段";
                    res = R.mipmap.ic_19b;
                } else {
                    playDescription = "即将经过堤坝路段";
                    res = R.mipmap.ic_19a;
                }
                playDescription = "";
                break;
            case 20:
                priority = GlobalVariables.RSI_LEVEL_1;
                //playDescription = "前方村庄，谨慎驾驶";
                res = R.mipmap.ic_20;
                break;
            case 21:
                priority = GlobalVariables.RSI_LEVEL_1;
                playDescription = "前方隧道";
                res = R.mipmap.ic_21;
                break;
            case 22:
                priority = GlobalVariables.RSI_LEVEL_1;
                //playDescription = String.format("前方%d米到达渡口", distance);
                res = R.mipmap.ic_22;
                break;
            case 23:
                //playDescription = String.format("前方%d米驶入驼峰桥", distance);
                priority = GlobalVariables.RSI_LEVEL_1;
                res = R.mipmap.ic_23;
                break;
            case 24:
                // playDescription = "路面不平，减速慢行";
                priority = GlobalVariables.RSI_LEVEL_1;
                res = R.mipmap.ic_24;
                break;
            case 25:
                // playDescription = "前方路面高突";
                res = R.mipmap.ic_25;
                priority = GlobalVariables.RSI_LEVEL_1;
                break;
            case 26:
                //  playDescription = "前方路面低洼";
                priority = GlobalVariables.RSI_LEVEL_1;
                res = R.mipmap.ic_26;
                break;
            case 27:
                playDescription = "前方积水，请绕行";
                res = R.mipmap.ic_27;
                priority = GlobalVariables.RSI_LEVEL_3;
                break;
            case 28:
                playDescription = "前方铁道路口";
                priority = GlobalVariables.RSI_LEVEL_1;
                res = R.mipmap.ic_28;
                break;
            case 29:
                playDescription = "前方铁道路口";
                priority = GlobalVariables.RSI_LEVEL_1;
                res = R.mipmap.ic_29;
                break;
            case 30:
                //playDescription = "前方多股铁路相交，请遵循交通指示";
                res = R.mipmap.ic_30;
                priority = GlobalVariables.RSI_LEVEL_1;
                break;
            case 31:
                priority = GlobalVariables.RSI_LEVEL_1;
                if (TextUtils.equals(description, "a")) {
                    playDescription = "前方铁路道口";
                    res = R.mipmap.ic_31a;
                    break;
                } else if (TextUtils.equals(description, "b")) {
                    playDescription = "前方铁路道口";
                    res = R.mipmap.ic_31b;
                    break;
                } else if (TextUtils.equals(description, "c")) {
                    playDescription = "前方铁路道口";
                    res = R.mipmap.ic_31c;
                    break;
                } else {
                    playDescription = "前方铁路道口";
                    res = R.mipmap.ic_31a;
                }
                break;
            case 32:
                //playDescription = "注意非机动车辆";
                res = R.mipmap.ic_32;
                priority = GlobalVariables.RSI_LEVEL_1;
                break;
            case 33:
                //playDescription = "注意残疾人";
                res = R.mipmap.ic_33;
                priority = GlobalVariables.RSI_LEVEL_1;
                break;
            case 34:
                playDescription = "事故易发路段, 请小心行驶";
                res = R.mipmap.ic_34;
                priority = GlobalVariables.RSI_LEVEL_2;
                break;
            case 36:
                priority = GlobalVariables.RSI_LEVEL_2;
                if (TextUtils.equals(description, "b")) {
                    playDescription = "前方障碍，请左侧通行";
                    res = R.mipmap.ic_36b;
                } else if (TextUtils.equals(description, "c")) {
                    playDescription = "前方障碍，请右侧通行";
                    res = R.mipmap.ic_36c;
                } else if (TextUtils.equals(description, "a")) {
                    playDescription = "前方障碍，请两侧通行";
                    res = R.mipmap.ic_36a;
                } else {
                    playDescription = "前方障碍，请绕行";
                    res = R.mipmap.ic_36a;
                }
                playDescription = "";
                break;
            case 37:
                playDescription = "注意危险";
                res = R.mipmap.ic_37;
                priority = GlobalVariables.RSI_LEVEL_2;
                break;

            case 38:
                playDescription = "前方施工，请按实际道路行驶";
                res = R.mipmap.ic_38;
                priority = GlobalVariables.RSI_LEVEL_3;
                break;

            case 40:
                playDescription = "前方进入隧道，请开车灯";
                res = R.mipmap.ic_40;
                priority = GlobalVariables.RSI_LEVEL_1;
                break;
            case 41:
                //playDescription = "注意潮汐车道";
                res = R.mipmap.ic_41;
                priority = GlobalVariables.RSI_LEVEL_1;
                break;
            case 42:
                //playDescription = "注意保持车距";
                res = R.mipmap.ic_42;
                priority = GlobalVariables.RSI_LEVEL_1;
                break;
            case 43:
                priority = GlobalVariables.RSI_LEVEL_1;
                if (TextUtils.equals(description, "b")) {
                    playDescription = "注意左侧汇入车辆";
                    res = R.mipmap.ic_43b;
                } else {
                    playDescription = "注意右侧汇入车辆";
                    res = R.mipmap.ic_43a;
                }
                playDescription = "";
                break;

            case 44:
                priority = GlobalVariables.RSI_LEVEL_1;
                if (TextUtils.equals(description, "a")) {
                    playDescription = "注意左侧汇入车辆";
                    res = R.mipmap.ic_44;
                } else if (TextUtils.equals(description, "b")) {
                    playDescription = "注意右侧汇入车辆";
                    res = R.mipmap.ic_44b;
                } else {
                    playDescription = "注意汇入车辆";
                    res = R.mipmap.ic_44b;
                }
                playDescription = "";
                break;
            case 45:
                priority = GlobalVariables.RSI_LEVEL_1;
                //playDescription = "前方有避险车道";
                res = R.mipmap.ic_45;
                break;
            case 46:
                priority = GlobalVariables.RSI_LEVEL_1;
                if (TextUtils.equals(description, "a")) {
                    playDescription = "路面结冰，小心慢行";
                    res = R.mipmap.ic_46a;
                } else if (TextUtils.equals(description, "b")) {
                    playDescription = "注意雨雪天气";
                    res = R.mipmap.ic_46b;
                } else if (TextUtils.equals(description, "c")) {
                    //playDescription = "注意雾天，请开雾灯，小心慢行";
                    playDescription = "";
                    res = R.mipmap.ic_46c;
                } else if (TextUtils.equals(description, "d")) {
                    playDescription = "注意不利气象条件";

                    res = R.mipmap.ic_46d;

                } else {
                    playDescription = "注意不利气象条件";
                    res = R.mipmap.ic_46d;
                }
                playDescription = "";
                break;
            case 47:
                priority = GlobalVariables.RSI_LEVEL_1;
                //playDescription = String.format("前方%d米车辆排队", distance);
                res = R.mipmap.ic_47;
                break;
            case 49:
                //playDescription = "减速让行";
                res = R.mipmap.ic_49;
                priority = GlobalVariables.RSI_LEVEL_4;
                break;
            case 51:
                //playDescription = "前方禁止通行";
                res = R.mipmap.ic_51;
                priority = GlobalVariables.RSI_LEVEL_6;
                break;
            case 52:
                //playDescription = "前方禁止驶入";
                res = R.mipmap.ic_52;
                priority = GlobalVariables.RSI_LEVEL_6;
                break;
            case 76:
                //playDescription = "路段禁止超车";
                res = R.mipmap.ic_76;
                priority = GlobalVariables.RSI_LEVEL_4;
                break;
            case 77:
                //playDescription = "解除禁止超车";
                res = R.mipmap.ic_77;
                priority = GlobalVariables.RSI_LEVEL_4;
                break;
            case 78:
                //playDescription = "此处禁止停车";
                res = R.mipmap.ic_78;
                priority = GlobalVariables.RSI_LEVEL_5;
                break;
            case 79:
                //playDescription = "此处禁止长时停车";
                res = R.mipmap.ic_79;
                priority = GlobalVariables.RSI_LEVEL_5;
                break;
            case 80:
                //playDescription = "禁止鸣笛";
                res = R.mipmap.ic_80;
                priority = GlobalVariables.RSI_LEVEL_4;
                break;
            case 81: {
                rsiResBean.setShowValue(true);
                if (TextUtils.equals(description, "z")) {
                    playDescription = String.format("前方限宽%s米,请绕行", value);
                } else {
                    playDescription = String.format("前方限宽%s米,请绕行", value);
                }
                res = R.mipmap.ic_81;
                priority = GlobalVariables.RSI_LEVEL_4;
            }
            break;
            case 82: {
                rsiResBean.setShowValue(true);
                if (TextUtils.equals(description, "z")) {
                    playDescription = String.format("前方限高%s米,请绕行", value);
                } else {
                    playDescription = String.format("前方限高%s米,请绕行", value);
                }
                res = R.mipmap.ic_82;
                priority = GlobalVariables.RSI_LEVEL_4;
            }
            break;
            case 85: {
                rsiResBean.setShowValue(true);
                playDescription = "";
                try {
                    double limitSpeed = Double.parseDouble(value);
                    if(GlobalVariables.limitSpeedEnable && speed > limitSpeed){
                        // 超速需语音提示
                        playDescription = "道路限速" + value;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                res = R.mipmap.ic_85;
                priority = GlobalVariables.RSI_LEVEL_5;
            }
            break;

            case 88:
                // playDescription = "前方禁止运输危险物品车辆驶入";
                res = R.mipmap.ic_88;
                priority = GlobalVariables.RSI_LEVEL_5;
                break;

            case 111: {
                rsiResBean.setShowValue(true);
                if (TextUtils.equals(description, "z")) {
                    //      playDescription = String.format("路段最低限速%s,请加速行驶", value);
                } else {
                    //      playDescription = String.format("路段最低限速%s", value);
                }

                res = R.mipmap.ic_111;
            }

            break;

            case 163:
                if (TextUtils.equals(description, "a")) {
                    //     playDescription = "前方右侧车道变少";
                    res = R.mipmap.ic_163a;
                    break;
                } else if (TextUtils.equals(description, "b")) {
                    //    playDescription = "前方车道变少";
                    res = R.mipmap.ic_163b;
                    break;
                }
                break;

            //111  76 163a
            default:
                break;
        }
        if (res == 0) {
            return null;
        }
        rsiResBean.setPriority(priority);
        rsiResBean.setRes(res);
        if (!TextUtils.isEmpty(value)) {
            rsiResBean.setValue(value);
        }

        // 标志标牌所有提醒改为"叮"
        playDescription = "";

        rsiResBean.setPlayDescription(playDescription);
        rsiResBean.setRsiId(rsiId);

        return rsiResBean;
    }


}
