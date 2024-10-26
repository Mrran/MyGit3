package com.matou.smartcar.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.matou.smartcar.bean.BaseBean;
import com.matou.smartcar.bean.DataInfoBean;
import com.matou.smartcar.global.WarningLevelConstants;
import com.matou.smartcar.global.ZYWarningLevelConstants;
import com.matou.smartcar.protocol.ExtraOuterClass;
import com.matou.smartcar.protocol.GreenWaveGuideOuterClass;
import com.matou.smartcar.protocol.GuidepostOuterClass;
import com.matou.smartcar.protocol.HmiOuterClass;
import com.matou.smartcar.protocol.Icdc2HmiOuterClass;
import com.matou.smartcar.protocol.MetadataOuterClass;
import com.matou.smartcar.protocol.NationalStandardOuterClass;
import com.matou.smartcar.protocol.NationalStandardOuterClass.NationalStandard.MsgType;
import com.matou.smartcar.protocol.PositionOuterClass;
import com.matou.smartcar.protocol.VehicleOuterClass;

import java.util.ArrayList;
import java.util.List;

public class DataConversionUtil {
    private static Gson mGson = new Gson();

    public static DataInfoBean convert(Icdc2HmiOuterClass.Icdc2Hmi icdc2Hmi) {
        DataInfoBean dataInfoBean = new DataInfoBean();

        DataInfoBean.HVMSGBean hvmsg = getHVMSG(icdc2Hmi);
        dataInfoBean.setHVMSG(hvmsg);

        List<DataInfoBean.RSIEventBean> rsi = getRsi(icdc2Hmi);
        dataInfoBean.setRSIEvent(rsi);


        List<DataInfoBean.RVMSGBean> rvmsg = getRVMSG(icdc2Hmi);
        dataInfoBean.setRVMSG(rvmsg);

        DataInfoBean.TrafficLightBean TrafficLight = getTrafficLight(icdc2Hmi);

        dataInfoBean.setTrafficLight(TrafficLight);
        return dataInfoBean;
    }

    private static DataInfoBean.TrafficLightBean getTrafficLight(Icdc2HmiOuterClass.Icdc2Hmi icdc2Hmi) {
        HmiOuterClass.Hmi hmi = icdc2Hmi.getHmi();
        if (hmi == null) return null;

        NationalStandardOuterClass.NationalStandard nationalStandard = hmi.getNationalStandard();
        if (nationalStandard == null) return null;
        GreenWaveGuideOuterClass.GreenWaveGuideList greenWaveGuide = nationalStandard.getGreenWaveGuide();
        if (greenWaveGuide == null) return null;
        List<GreenWaveGuideOuterClass.GreenWaveGuide> greenWaveGuideList = greenWaveGuide.getGreenWaveGuideList();
        if (greenWaveGuideList == null || greenWaveGuideList.size() == 0) return null;
        List<DataInfoBean.TrafficLightBean.LightBean> light = new ArrayList<>();
        for (GreenWaveGuideOuterClass.GreenWaveGuide waveGuide : greenWaveGuideList) {
            DataInfoBean.TrafficLightBean.LightBean lightBean = new DataInfoBean.TrafficLightBean.LightBean();

            int min = waveGuide.getMin();//绿波最大值
            lightBean.setSpeedDown(min);
            int max = waveGuide.getMax();//绿波最小值
            lightBean.setSpeedUp(max);
            int time = waveGuide.getTime();//时间
            lightBean.setTime(time);
            boolean isCurrent = waveGuide.getIsCurrent();//当前所在车道
            lightBean.setCurrDir(isCurrent ? 1 : 0);
            GreenWaveGuideOuterClass.GreenWaveGuide.Direction driection = waveGuide.getDriection();
            int driectionNumber = driection.getNumber();//方向
            if (driectionNumber == GreenWaveGuideOuterClass.GreenWaveGuide.Direction.RIGHT_VALUE) {
                lightBean.setDir(2);
            } else if (driectionNumber == GreenWaveGuideOuterClass.GreenWaveGuide.Direction.LEFT_VALUE) {
                lightBean.setDir(3);
            } else {
                lightBean.setDir(1);
            }

            GreenWaveGuideOuterClass.GreenWaveGuide.TrafficLightStatue statue = waveGuide.getStatue();
            int colorNumber = statue.getNumber();
            if (colorNumber == GreenWaveGuideOuterClass.GreenWaveGuide.TrafficLightStatue.GREEN_VALUE) {
                lightBean.setColor(4);
            } else if (colorNumber == GreenWaveGuideOuterClass.GreenWaveGuide.TrafficLightStatue.YELLOW_VALUE) {
                lightBean.setColor(7);
            } else {
                lightBean.setColor(2);
            }
            light.add(lightBean);
        }

        DataInfoBean.TrafficLightBean trafficLightBean = new DataInfoBean.TrafficLightBean();

        trafficLightBean.setLight(light);
        return trafficLightBean;
    }

    private static List<DataInfoBean.RVMSGBean> getRVMSG(Icdc2HmiOuterClass.Icdc2Hmi icdc2Hmi) {

        HmiOuterClass.Hmi hmi = icdc2Hmi.getHmi();
        NationalStandardOuterClass.NationalStandard nationalStandard = hmi.getNationalStandard();
        if (nationalStandard == null) return null;
        int msgTypeValue = nationalStandard.getMsgTypeValue();

        MetadataOuterClass.MetadataList metadatas = nationalStandard.getMetadatas();
        if (metadatas == null) return null;

        List<MetadataOuterClass.Metadata> metadataList = metadatas.getMetadataList();
        if (metadataList == null || metadataList.size() == 0) return null;

        MetadataOuterClass.Metadata metadata = metadataList.get(0);
        if (metadata == null) return null;

        float heading = metadata.getHeading();//远车方向
        PositionOuterClass.Position position = metadata.getPosition();//远车坐标

        DataInfoBean.RVMSGBean rvmsgBean = new DataInfoBean.RVMSGBean();
        rvmsgBean.setHeading(heading);
        rvmsgBean.setSid(String.valueOf(System.currentTimeMillis()));//大唐没有id 自己构造一个吧
        DataInfoBean.RVMSGBean.PosBeanX pos = new DataInfoBean.RVMSGBean.PosBeanX();
        pos.setLatitude(position.getLatitude());
        pos.setLongitude(position.getLongitude());
        DataInfoBean.RVMSGBean.V2vEventBean v2v = new DataInfoBean.RVMSGBean.V2vEventBean();
        v2v.setTTC(3);//给个ttc 不然进不去
        rvmsgBean.setPos(pos);
        switch (msgTypeValue) {//判断远车事件类型
            case MsgType.EMERGENCY_VEHICLE_REMINDER_VALUE://紧急车辆提醒 EVW
                v2v.setEventStr("EVW");

                break;
            case MsgType.LANE_CHANGE_WARNING_VALUE://变道预警 LCW
                v2v.setEventStr("LCW");
                break;
            case MsgType.FORWARD_COLLISION_WARNING_VALUE://前向碰撞预警 FCW
                v2v.setEventStr("FCW");

                break;
            case MsgType.INTERSECTION_WARNING_VALUE://交叉路口预警 ICW
                v2v.setEventStr("ICW");

                break;
            case MsgType.TURN_LEFT_AUXILIARY_VALUE://左转辅助 LTA

                v2v.setEventStr("LTA");

                break;
            case MsgType.BLIND_SPOT_VALUE://盲区 BSW

                v2v.setEventStr("BSW");

                break;
            case MsgType.REVERSE_OVERTAKING_WARNING_VALUE://逆向超车 DNPW

                v2v.setEventStr("DNPW");

                break;
            case MsgType.EMERGENCY_BRAKE_WARNING_VALUE://紧急制动预警 EBW
                v2v.setEventStr("EBW");

                break;
            case MsgType.ABNORMAL_VEHICLE_WARNING_VALUE://异常车辆提醒 AVW
                v2v.setEventStr("AVW");
                break;
            case MsgType.VEHICLE_LOSS_WARNING_VALUE://车辆失控提醒 CLW
                v2v.setEventStr("CLW");
                break;
            case MsgType.RED_LIGHT_WARNING_VALUE://闯红灯预警 RLVW
                v2v.setEventStr("RLVW");
                break;
            case MsgType.VULNERABLE_TRAFFIC_PARTICIPANT_COLLISION_WARNING_VALUE://弱势交通参与者碰撞预警 YPC
                v2v.setEventStr("YPC");
                break;
//            case MsgType.CONGESTION_WARNING_AHEAD_VALUE://前方拥堵提醒
//                speak = "前方拥堵";
//                break;

        }
        String eventStr = v2v.getEventStr();
        if (TextUtils.isEmpty(eventStr)) return null;
        rvmsgBean.setV2vEvent(v2v);
        List<DataInfoBean.RVMSGBean> rvmsgBeanList = new ArrayList<>();

        rvmsgBeanList.add(rvmsgBean);
        return rvmsgBeanList;
    }

    private static List<DataInfoBean.RSIEventBean> getRsi(Icdc2HmiOuterClass.Icdc2Hmi icdc2Hmi) {
        try {
            HmiOuterClass.Hmi hmi = icdc2Hmi.getHmi();
            if (hmi == null) return null;
            NationalStandardOuterClass.NationalStandard nationalStandard = hmi.getNationalStandard();
            if (nationalStandard == null) return null;
            int msgTypeValue = nationalStandard.getMsgTypeValue();
            if (msgTypeValue == 2) {//路牌消息 等于rsi
                List<DataInfoBean.RSIEventBean> rsiEventBeans = new ArrayList<>();
                GuidepostOuterClass.Guidepost guidepost = nationalStandard.getGuidepost();
                List<ExtraOuterClass.Extra> extraList = nationalStandard.getExtraList();
                DataInfoBean.RSIEventBean rsiEventBean = new DataInfoBean.RSIEventBean();
                for (ExtraOuterClass.Extra extra : extraList) {
                    String key = extra.getKey();
                    if (TextUtils.equals("RSI_ID", key)) {
                        rsiEventBean.setRsiId(key);
                        break;
                    }
                }
                List<ExtraOuterClass.Extra> guidepostExtraList = guidepost.getExtraList();
                int typeValue = guidepost.getTypeValue();
                if (typeValue == 0) {//限速
                    ExtraOuterClass.Extra extra = guidepostExtraList.get(0);
                    String value = extra.getValue();
                    rsiEventBean.setAlertType(85);
                    rsiEventBean.setDistance(Double.parseDouble(value));
                } else if (typeValue == 1) {
                    rsiEventBean.setAlertType(38);
                } else if (typeValue == 2) {
                    rsiEventBean.setAlertType(37);
                } else if (typeValue == 3) {
                    rsiEventBean.setAlertType(49);
                } else if (typeValue == 4) {
                    rsiEventBean.setAlertType(11);
                } else if (typeValue == 5) {
                    rsiEventBean.setAlertType(11);
                } else if (typeValue == 6) {//为6的时候 是自定义 直接传值
                    ExtraOuterClass.Extra extra = guidepostExtraList.get(0);
                    String value = extra.getValue();
                    rsiEventBean.setAlertType(Integer.parseInt(value));
                }
                rsiEventBeans.add(rsiEventBean);
                return rsiEventBeans;
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }


    }

    private static DataInfoBean.HVMSGBean getHVMSG(Icdc2HmiOuterClass.Icdc2Hmi icdc2Hmi) {
        try {
            HmiOuterClass.Hmi hmi = icdc2Hmi.getHmi();
            VehicleOuterClass.Vehicle vehicle = hmi.getVehicle();
            if (vehicle == null) return null;
            boolean isSelf = vehicle.getIsSelf();
            if (isSelf) {
                DataInfoBean.HVMSGBean bean = new DataInfoBean.HVMSGBean();
                PositionOuterClass.Position position = vehicle.getPosition();
                float heading = vehicle.getHeading();
                bean.setHeading(heading);
                DataInfoBean.HVMSGBean.PosBean pos = new DataInfoBean.HVMSGBean.PosBean();
                pos.setLatitude(position.getLatitude());
                pos.setLongitude(position.getLongitude());
                bean.setPos(pos);
                return bean;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }


    }

}
