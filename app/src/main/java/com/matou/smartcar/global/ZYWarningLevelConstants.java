package com.matou.smartcar.global;
//汽研自有obu的场景优先判定
public class ZYWarningLevelConstants {
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

    public static int handleWarningLevel(String eventStr) {
        if (eventStr == null) return 0;
        int level;
        switch (eventStr) {
            case WarningTypeConstants.Event_FCW://前向碰撞
            case WarningTypeConstants.Event_FCWB://后向碰撞
            case WarningTypeConstants.Event_LTA://左转辅助
            case WarningTypeConstants.Event_LCW://变道预警
            case WarningTypeConstants.Event_DNPW://逆向超车预警
            case WarningTypeConstants.Event_RLVW://逆向超车预警

                level = WarningLevelConstants.LEVEL_10;
                break;
            case WarningTypeConstants.Event_PCR://行人横穿预警
            case WarningTypeConstants.Event_VRUCW://行人横穿预警
                level = WarningLevelConstants.LEVEL_9;
                break;

            case WarningTypeConstants.Event_ICW://交叉路口碰撞
                level = WarningLevelConstants.LEVEL_7;
                break;

            case WarningTypeConstants.Event_CVM://砸到汇入
                level = WarningLevelConstants.LEVEL_6;
                break;
            case WarningTypeConstants.Event_EBW://紧急制动预警
            case WarningTypeConstants.Event_AVW://异常车辆预警
            case WarningTypeConstants.Event_CLW://车辆失控预警
            case WarningTypeConstants.Event_EVW://紧急车辆预警
            case WarningTypeConstants.Event_CLC://协作变道
            case WarningTypeConstants.Event_AVWB://异常或者怠速


                level = WarningLevelConstants.LEVEL_5;
                break;
            case WarningTypeConstants.Event_YPC://礼让行人
                level = WarningLevelConstants.LEVEL_4;
                break;
            case WarningTypeConstants.Event_BSW://盲区预警
            case WarningTypeConstants.Event_FCS://
                level = WarningLevelConstants.LEVEL_3;
                break;
            default:
                level = 0;
        }


        return level;
    }
}
