package com.matou.smartcar.global;

public class WarningTypeConstants {
    // TODO: 2020/12/18 场景未枚举完全
    public static final String Event_FCW = "FCW";//前向碰撞
    public static final String Event_FCWB = "FCWB";//后向碰撞
    public static final String Event_ICW = "ICW";//交叉路口碰撞
    public static final String Event_LTA = "LTA";//左转辅助
    public static final String Event_BSW = "BSW";//盲区预警
    public static final String Event_LCW = "LCW";//变道预警
    public static final String Event_DNPW = "DNPW";//逆向超车预警
    public static final String Event_EBW = "EBW";//紧急制动预警
    public static final String Event_AVW = "AVW";//异常车辆预警 双闪
    public static final String Event_AVWB = "AVWB";//异常车辆预警 怠速或者停止
    public static final String Event_CLW = "CLW";//车辆失控预警
    public static final String Event_EVW = "EVW";//紧急车辆预警
    public static final String Event_PCR = "PCR";//行人横穿预警
    public static final String Event_VRUCW = "VRUCW";//这个也是行人横穿预警
    public static final String Event_RLVW = "RLVW";//闯红灯预警
    public static final String Event_CLC = "CLC";//协作式变道
    public static final String Event_CVM = "CVM";//砸到汇入
    public static final String Event_YPC = "YPC";//礼让行人

    public static final String Event_FCS = "FCS";//前车起步
}
