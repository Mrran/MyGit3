package com.matou.smartcar.util;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.elvishew.xlog.XLog;
import com.matou.smartcar.R;
import com.matou.smartcar.bean.DataInfoBean;
import com.matou.smartcar.bean.RsiResBean;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.speaker.SpeakerHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RteManager {

    private static RteManager instance;
    private Context context;

    public static RteManager getInstance(Context context) {
        if (instance == null) {
            synchronized (RteManager.class) {
                if (instance == null) {
                    instance = new RteManager(context);
                }
            }
        }
        return instance;
    }

//    private List<Marker> rsiMarkers = new ArrayList<>();
//    private List<Marker> oldRsiMarkers = new ArrayList<>();

    public List<RsiResBean> handleRte(List<DataInfoBean.RSIEventBean> rsiEvent, boolean isPlay) {

        try {
            List<RsiResBean> resList = new ArrayList<>();
            for (DataInfoBean.RSIEventBean rsiEventBean : rsiEvent) {
                RsiResBean rsiResBean = getRsiRes(rsiEventBean, true);
                resList.add(rsiResBean);
            }
            return resList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private RsiResBean getRsiRes(DataInfoBean.RSIEventBean rsiEventBean, boolean playTTs) {
        int alertType = rsiEventBean.getAlertType();
        int intID = alertType / 100;
        int intCode = alertType % 100;
        String description = rsiEventBean.getDescription();
        String id;
        if (intID < 10) {
            id = "0" + intID;
        } else {
            id = String.valueOf(intID);
        }
        String code;
        if (intCode < 10) {
            code = "0" + intCode;
        } else {
            code = String.valueOf(intCode);
        }
        String subCode = "";
        try {
            String descrp = rsiEventBean.getDescription();
            if(!TextUtils.isEmpty(descrp) && descrp.length() > 2){
                String substring = descrp.substring(0, 3);
                subCode = String.valueOf(Integer.valueOf(substring));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        RsiResBean rsiResBean = new RsiResBean();
        String playDescription = "";
        switch (id) {
            case "01"://交通事故
                rsiResBean.setPriority(GlobalVariables.RSI_LEVEL_8);
                if (TextUtils.equals("01", code)) {
                    playDescription = "前方车辆故障车辆";
                    rsiResBean.setRes(R.mipmap.ic_cheliangshigu);
                } else if (TextUtils.equals("02", code)) {
                    playDescription = "前方事故";
                    rsiResBean.setRes(R.mipmap.ic_rencheshigu);
                } else if (TextUtils.equals("03", code)) {
                    playDescription = "前方事故";
                    rsiResBean.setRes(R.mipmap.ic_checheshigu);

                } else if (TextUtils.equals("04", code)) {
                    playDescription = "";
                    rsiResBean.setRes(R.mipmap.ic_sheshixiangguan);
                }
                break;
            case "02"://交通灾害
                rsiResBean.setPriority(GlobalVariables.RSI_LEVEL_8);
                if (TextUtils.equals("01", code)) {
                    playDescription = "前方失火";
                    rsiResBean.setRes(R.mipmap.ic_chelianghuozai);
                } else if (TextUtils.equals("02", code)) {
                    playDescription = "前方失火";
                    rsiResBean.setRes(R.mipmap.ic_lumianhuozai);
                } else if (TextUtils.equals("03", code)) {
                    playDescription = "前方失火";
                    rsiResBean.setRes(R.mipmap.ic_lubianhuozai);

                } else if (TextUtils.equals("04", code)) {
                    playDescription = "前方失火";
                    rsiResBean.setRes(R.mipmap.ic_suidaohuozai);
                } else if (TextUtils.equals("05", code)) {
                    playDescription = "前方失火";
                    rsiResBean.setRes(R.mipmap.ic_daolusheshihuozai);
                } else if (TextUtils.equals("06", code)) {
                    playDescription = "前方地质灾害";
                    rsiResBean.setRes(R.mipmap.ic_dizhizaihai);
                } else if (TextUtils.equals("07", code)) {
                    playDescription = "前方洪水";
                    rsiResBean.setRes(R.mipmap.ic_shuizai);
                }
                break;
            case "03"://交通气象
                if (TextUtils.equals("01", code)) {
                    if (TextUtils.equals("1", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_yu_0_1);
                    } else if (TextUtils.equals("2", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_yu_2_3);
                    } else if (TextUtils.equals("3", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_yu_2_3);

                    } else if (TextUtils.equals("4", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_yu_4_5);
                    } else if (TextUtils.equals("5", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_yu_4_5);
                    } else if (TextUtils.equals("6", subCode)) {
                        playDescription = "暴雨天气，请谨慎行驶";
                        rsiResBean.setRes(R.mipmap.ic_yu_6_7);
                    } else if (TextUtils.equals("7", subCode)) {
                        playDescription = "暴雨天气，请谨慎行驶";
                        rsiResBean.setRes(R.mipmap.ic_yu_6_7);
                    } else if (TextUtils.equals("8", subCode)) {
                        playDescription = "暴雨天气，请谨慎行驶";
                        rsiResBean.setRes(R.mipmap.ic_yu_8_10);
                    } else if (TextUtils.equals("9", subCode)) {
                        playDescription = "暴雨天气，请谨慎行驶";
                        rsiResBean.setRes(R.mipmap.ic_yu_8_10);
                    } else if (TextUtils.equals("10", subCode)) {
                        playDescription = "暴雨天气，请谨慎行驶";
                        rsiResBean.setRes(R.mipmap.ic_yu_8_10);
                    }
                } else if (TextUtils.equals("02", code)) {//冰雹没有
                    if (TextUtils.equals("1", subCode)) {
                    //    playDescription = "冰雹天气，建议寻找保护";
                        rsiResBean.setRes(R.mipmap.ic_bingbao1);
                    } else if (TextUtils.equals("2", subCode)) {
                   //     playDescription = "冰雹天气，建议寻找保护";
                        rsiResBean.setRes(R.mipmap.ic_bingbao2);
                    } else if (TextUtils.equals("3", subCode)) {
                   //     playDescription = "冰雹天气，建议寻找保护";
                        rsiResBean.setRes(R.mipmap.ic_bingbao3);

                    }

                } else if (TextUtils.equals("03", code)) {//雷电

                    rsiResBean.setRes(R.mipmap.ic_leidian);
                } else if (TextUtils.equals("04", code)) {//风
                    if (TextUtils.equals("0", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_feng_0_1);
                    } else if (TextUtils.equals("1", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_feng_0_1);
                    } else if (TextUtils.equals("2", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_feng_2_3);
                    } else if (TextUtils.equals("3", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_feng_2_3);

                    } else if (TextUtils.equals("4", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_feng_4_5);
                    } else if (TextUtils.equals("5", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_feng_4_5);
                    } else if (TextUtils.equals("6", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_feng_6_7);
                    } else if (TextUtils.equals("7", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_feng_6_7);
                    } else if (TextUtils.equals("8", subCode)) {
                    //    playDescription = "大风天气，谨慎驾驶";
                        rsiResBean.setRes(R.mipmap.ic_feng_8_9);
                    } else if (TextUtils.equals("9", subCode)) {
                    //    playDescription = "大风天气，谨慎驾驶";
                        rsiResBean.setRes(R.mipmap.ic_feng_8_9);
                    } else if (TextUtils.equals("10", subCode)) {
                    //    playDescription = "大风天气，谨慎驾驶";
                        rsiResBean.setRes(R.mipmap.ic_feng_10_11);
                    } else if (TextUtils.equals("11", subCode)) {
                    //    playDescription = "大风天气，谨慎驾驶";
                        rsiResBean.setRes(R.mipmap.ic_feng_10_11);
                    } else if (TextUtils.equals("12", subCode)) {
                    //    playDescription = "大风天气，谨慎驾驶";
                        rsiResBean.setRes(R.mipmap.ic_feng_12);
                    } else {
                   //     playDescription = "飓风天气，请寻找保护";
                        rsiResBean.setRes(R.mipmap.ic_feng_12);
                    }
                } else if (TextUtils.equals("05", code)) {//雾
                    if (TextUtils.equals("0", subCode)) {
                        rsiResBean.setRes(R.mipmap.ic_wu_0_1);
                    } else if (TextUtils.equals("1", subCode)) {
                        playDescription = "大雾天气，请开雾灯，谨慎行驶";
                        rsiResBean.setRes(R.mipmap.ic_wu_0_1);
                    } else if (TextUtils.equals("2", subCode)) {
                        playDescription = "大雾天气，请开雾灯，谨慎行驶";
                        rsiResBean.setRes(R.mipmap.ic_wu_2_3);
                    } else if (TextUtils.equals("3", subCode)) {
                        playDescription = "大雾天气，请开雾灯，谨慎行驶";
                        rsiResBean.setRes(R.mipmap.ic_wu_2_3);
                    }
                } else if (TextUtils.equals("06", code)) {//高温

                    rsiResBean.setRes(R.mipmap.ic_gaowen);
                } else if (TextUtils.equals("07", code)) {//干旱

                    rsiResBean.setRes(R.mipmap.ic_ganhan);
                } else if (TextUtils.equals("08", code)) {//雪
                    if (TextUtils.equals("1", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_xue_0_1);
                    } else if (TextUtils.equals("1", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_xue_0_1);
                    } else if (TextUtils.equals("2", subCode)) {

                        rsiResBean.setRes(R.mipmap.ic_xue_2_3);
                    } else if (TextUtils.equals("3", subCode)) {
                        playDescription = "大雪路滑，请谨慎行驶";
                        rsiResBean.setRes(R.mipmap.ic_xue_2_3);

                    } else if (TextUtils.equals("4", subCode)) {
                        playDescription = "大雪路滑，请谨慎行驶";
                        rsiResBean.setRes(R.mipmap.ic_xue_4_6);
                    } else if (TextUtils.equals("5", subCode)) {
                        playDescription = "大雪路滑，请谨慎行驶";
                        rsiResBean.setRes(R.mipmap.ic_xue_4_6);
                    } else if (TextUtils.equals("6", subCode)) {
                        playDescription = "大雪路滑，请谨慎行驶";
                        rsiResBean.setRes(R.mipmap.ic_xue_4_6);
                    }
                } else if (TextUtils.equals("09", code)) {//寒潮

                    rsiResBean.setRes(R.mipmap.ic_hanchao);
                } else if (TextUtils.equals("10", code)) {//shuangdong

                    rsiResBean.setRes(R.mipmap.ic_shuangdong);
                } else if (TextUtils.equals("11", code)) {//雾霾

                    rsiResBean.setRes(R.mipmap.ic_wumai);
                } else if (TextUtils.equals("99", code)) {//沙尘
                    if (TextUtils.equals("1", subCode)) {
                        //playDescription = "沙尘天气，请开雾灯，谨慎驾驶";

                        rsiResBean.setRes(R.mipmap.ic_shachenbao_0_1);
                    } else if (TextUtils.equals("1", subCode)) {
                        //playDescription = "沙尘天气，请开雾灯，谨慎驾驶";
                        rsiResBean.setRes(R.mipmap.ic_shachenbao_0_1);
                    } else if (TextUtils.equals("2", subCode)) {
                        //playDescription = "沙尘天气，请开雾灯，谨慎驾驶";
                        rsiResBean.setRes(R.mipmap.ic_shachenbao_2_3);
                    } else if (TextUtils.equals("3", subCode)) {
                        //playDescription = "沙尘天气，请开雾灯，谨慎驾驶";
                        rsiResBean.setRes(R.mipmap.ic_shachenbao_2_3);

                    } else if (TextUtils.equals("4", subCode)) {
                        //playDescription = "沙尘天气，请开雾灯，谨慎驾驶";
                        rsiResBean.setRes(R.mipmap.ic_shachenbao4);
                    }
                }

                break;
            case "04"://路面状况
                rsiResBean.setPriority(GlobalVariables.RSI_LEVEL_7);
                if (TextUtils.equals("01", code)) {
                    playDescription = "注意抛洒物";
                    rsiResBean.setRes(R.mipmap.ic_sanwucuoluan);
                } else if (TextUtils.equals("02", code)) {

                    rsiResBean.setRes(R.mipmap.ic_yeti);
                } else if (TextUtils.equals("03", code)) {

                    rsiResBean.setRes(R.mipmap.ic_jiyouxielou);

                } else if (TextUtils.equals("04", code)) {
                    playDescription = "注意障碍";
                    rsiResBean.setRes(R.mipmap.ic_daoluzhangai);
                } else if (TextUtils.equals("05", code)) {

                    rsiResBean.setRes(R.mipmap.ic_ren);
                } else if (TextUtils.equals("06", code)) {

                    rsiResBean.setRes(R.mipmap.ic_dongwu);
                } else if (TextUtils.equals("07", code)) {

                    rsiResBean.setRes(R.mipmap.ic_jishui);
                } else if (TextUtils.equals("08", code)) {//湿滑图标没有
                    playDescription = "道路湿滑，小心慢行";
                } else if (TextUtils.equals("09", code)) {
                    playDescription = "道路结冰，小心慢行";
                    rsiResBean.setRes(R.mipmap.ic_jiebing);
                } else if (TextUtils.equals("12", code)) {

                    rsiResBean.setRes(R.mipmap.ic_jiebing);//这里没图标
                }
                break;
            case "05"://道路施工
                rsiResBean.setPriority(GlobalVariables.RSI_LEVEL_7);
                if (TextUtils.equals("01", code)) {
                    playDescription = "前方占道施工";
                    rsiResBean.setRes(R.mipmap.ic_zhandaoshigong);
                } else if (TextUtils.equals("02", code)) {
                    playDescription = "前方断道施工，请绕行";
                    rsiResBean.setRes(R.mipmap.ic_duandaoshigong);
                } else {

                }

                break;
            case "06"://活动
                if (TextUtils.equals("01", code)) {
                    //playDescription = "前方举行活动，注意安全";
                    rsiResBean.setRes(R.mipmap.ic_wentishangyehuodong);
                } else if (TextUtils.equals("02", code)) {
                    //playDescription = "前方举行活动，请遵从安排";
                    rsiResBean.setRes(R.mipmap.ic_waijiaozhengwuhuodong);
                } else {

                }

                break;
            case "07"://重大事件
                rsiResBean.setPriority(GlobalVariables.RSI_LEVEL_10);
                if (TextUtils.equals("01", code)) {
                    playDescription = "前方事故";
                    rsiResBean.setRes(R.mipmap.ic_ranqi);
                } else if (TextUtils.equals("02", code)) {
                    playDescription = "前方事故";
                    rsiResBean.setRes(R.mipmap.ic_huaxuewuran);
                } else if (TextUtils.equals("03", code)) {
                    playDescription = "前方事故";
                    rsiResBean.setRes(R.mipmap.ic_heshigu);
                } else if (TextUtils.equals("04", code)) {
                    playDescription = "前方事故";
                    rsiResBean.setRes(R.mipmap.ic_baozha);
                } else if (TextUtils.equals("05", code)) {
                    playDescription = "前方事故";
                    rsiResBean.setRes(R.mipmap.ic_dian);
                } else if (TextUtils.equals("06", code)) {
                    playDescription = "";
                    rsiResBean.setRes(R.mipmap.ic_gonggongbaoli);
                } else if (TextUtils.equals("07", code)) {
                    playDescription = "前方拥堵";
                    rsiResBean.setRes(R.mipmap.ic_jiaotongjizhongdusai);
                } else {

                }


                break;
            case "09"://其他
                rsiResBean.setPriority(GlobalVariables.RSI_LEVEL_9);
                if (TextUtils.equals("01", code)) {
                    //playDescription = "附近车辆超速，注意安全";
                    rsiResBean.setRes(R.mipmap.ic_chaosu);
                } else if (TextUtils.equals("02", code)) {
                    playDescription = "前方车辆慢行";
                    rsiResBean.setRes(R.mipmap.ic_manxing);
                } else if (TextUtils.equals("03", code)) {
                    rsiResBean.setRes(R.mipmap.ic_cheliangxingshi);
                } else if (TextUtils.equals("04", code)) {
                    playDescription = "前方车辆逆行";
                    rsiResBean.setRes(R.mipmap.ic_cheliangnixing);
                } else if (TextUtils.equals("05", code)) {
                    playDescription = "请让行紧急车辆";
                    rsiResBean.setRes(R.mipmap.ic_jinjicheliangyouxian);
                } else if (TextUtils.equals("06", code)) {
                    //playDescription = "附近大货车，注意安全";
                    rsiResBean.setRes(R.mipmap.ic_dahuocheshibie);
                } else if (TextUtils.equals("97", code)) {
                    playDescription = "前车实线变道";
                    rsiResBean.setRes(R.mipmap.ic_yashixianbiandao);
                } else if (TextUtils.equals("98", code)) {
                    playDescription = "留意违停车辆";
                    rsiResBean.setRes(R.mipmap.ic_weiting);
                } else {

                }

                break;

            default:

                break;

        }
        rsiResBean.setRsiId(rsiEventBean.getRsiId());
        rsiResBean.setPlayDescription(playDescription);
        return rsiResBean;
    }

    private RteManager(Context context) {
        this.context = context;
    }

    //rsi事件是否正在播报队列的集合
//    private static HashMap<String, Boolean> mRsiTtsMap = new HashMap<>();






}
