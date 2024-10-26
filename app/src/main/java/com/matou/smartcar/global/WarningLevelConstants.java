package com.matou.smartcar.global;

import com.matou.smartcar.protocol.NationalStandardOuterClass;
import com.matou.smartcar.protocol.NationalStandardOuterClass.NationalStandard.*;

public class WarningLevelConstants {
    //前向碰撞
    //交叉路口碰撞
    //左转辅助
    //变道预警
    //逆向超车预警
    //非机动车碰撞预警
    public static final int LEVEL_10 = 10;
    //行人横穿预警
    public static final int LEVEL_9 = 9;
    //车道入侵提醒
    public static final int LEVEL_8 = 8;
    //闯红灯预警
    public static final int LEVEL_7 = 7;
    //协作式变道
    //交叉口协作式通行
    //匝道汇入
    //车道偏离预警
    public static final int LEVEL_6 = 6;
    //紧急制动预警
    //异常车辆提醒
    //车辆失控预警
    //紧急车辆提醒
    public static final int LEVEL_5 = 5;
    //礼让行人提醒
    public static final int LEVEL_4 = 4;
    //盲区预警
    public static final int LEVEL_3 = 3;
    //弯道速度提醒
    public static final int LEVEL_2 = 2;
    //前方拥堵提醒
    public static final int LEVEL_1 = 1;


    public static int handleWarningLevel(int eventStr) {
//        if (eventStr == null) return 0;
        int level;
        switch (eventStr) {
            case MsgType.FORWARD_COLLISION_WARNING_VALUE://前向碰撞
            case MsgType.INTERSECTION_WARNING_VALUE://交叉路口碰撞
            case MsgType.TURN_LEFT_AUXILIARY_VALUE://左转辅助
            case MsgType.LANE_CHANGE_WARNING_VALUE://变道预警
            case MsgType.REVERSE_OVERTAKING_WARNING_VALUE://逆向超车预警
            case MsgType.CONGESTION_WARNING_AHEAD_VALUE://弱势交通参与者
                level = WarningLevelConstants.LEVEL_10;
                break;


//            case WarningTypeConstants.Event_CVM://砸到汇入
//                level = WarningLevelConstants.LEVEL_6;
//                break;
            case MsgType.EMERGENCY_BRAKE_WARNING_VALUE://紧急制动预警
            case MsgType.ABNORMAL_VEHICLE_WARNING_VALUE://异常车辆预警
            case MsgType.VEHICLE_LOSS_WARNING_VALUE://车辆失控预警
            case MsgType.EMERGENCY_VEHICLE_REMINDER_VALUE://紧急车辆预警
//            case WarningTypeConstants.Event_CLC://协作变道
//            case WarningTypeConstants.Event_AVWB://异常或者怠速
                level = WarningLevelConstants.LEVEL_5;
                break;
            case MsgType.VULNERABLE_TRAFFIC_PARTICIPANT_COLLISION_WARNING_VALUE://礼让行人
                level = WarningLevelConstants.LEVEL_4;
                break;
            case MsgType.BLIND_SPOT_VALUE://盲区预警
                level = WarningLevelConstants.LEVEL_3;
                break;
            default:
                level = 0;
        }


        return level;
    }
}
