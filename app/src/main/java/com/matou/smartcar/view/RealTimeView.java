package com.matou.smartcar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.elvishew.xlog.XLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.animation.drawable.AnimatedDrawable2;
import com.facebook.imagepipeline.image.ImageInfo;
import com.matou.smartcar.R;
import com.matou.smartcar.base.BaseApplication;
import com.matou.smartcar.bean.DataInfoBean;
import com.matou.smartcar.global.WarningTypeConstants;
import com.matou.smartcar.global.ZYWarningLevelConstants;
import com.matou.smartcar.util.SceneMediaPlayerManager;
import com.opensource.svgaplayer.SVGAParser;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ranfeng
 */
public class RealTimeView extends FrameLayout {


//    @BindView(R.id.giv_event)
//    GifImageView eventGiv;

    @BindView(R.id.siv_event)
    SimpleDraweeView eventSiv;
    @BindView(R.id.tvSceneName)
    TextView sceneNameTv;

    @BindView(R.id.llyt_warning)
    LinearLayout warningLlyt;

    private final Context mContext;

    private SVGAParser mParser;

    private final static int SPEAK_DURATION = 10 * 1000;
    /**
     * 相同语音15s内只播放一次
     */
    private Map<String, Long> speakMap = new HashMap<>();


    public RealTimeView(@NonNull Context context) {
        this(context, null);
    }

    public RealTimeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RealTimeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {

        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.RtvAttribute);
        int rtvType = typedArray.getInt(R.styleable.RtvAttribute_rtv_type, 0);
        typedArray.recycle(); // 因为源码中是进行回收的

        View view = LayoutInflater.from(mContext).inflate(rtvType == 0 ? R.layout.view_real_time : R.layout.view_real_time_wnd, null, false);
        addView(view);
        ButterKnife.bind(this, view);

        mParser = new SVGAParser(mContext);
    }


    private int res;
    private int aniName;
    private String speakTts = "";
    private int speakM4a = -1;
    public  String preEvent;//这两个变量控制 如果是一个场景 且发生时间小于3s就不重复播报
    private long preTime;//
    private float mHeading;


    public void bindData(DataInfoBean.RVMSGBean data, float heading, boolean onlyTts) {

        mHeading = heading;

        if (data == null) {
            return;
        }

        DataInfoBean.RVMSGBean.V2vEventBean v2vEvent = data.getV2vEvent();
        if (v2vEvent == null) {
            return;
        }
        String eventStr = v2vEvent.getEventStr();

        XLog.w("RealTimeView eventStr = %s", eventStr);

        long currentTimeMillis = System.currentTimeMillis();

        //同一个事件少于3s不执行
        long dTime = currentTimeMillis - preTime;

        //如果是一个事件不重复播放
        if (TextUtils.equals(eventStr, preEvent)) {
            XLog.w("RealTimeView the some event = %s !!!", eventStr);
            return;
        }
        if (dTime > 3000) {
            XLog.w("RealTimeView dTime > 3000, reset");
            preEvent = "";
            preTime = currentTimeMillis;
        }

        //这里需要对多个事件做一个优先级处理
        //如果这次的事件 比上次的小 那就不执行
        int level = ZYWarningLevelConstants.handleWarningLevel(eventStr);
        int preLevel = ZYWarningLevelConstants.handleWarningLevel(preEvent);

        XLog.w("RealTimeView eventStr = %s, preEvent = %s", eventStr, preEvent);

        if (level <= preLevel) {
            XLog.w("RealTimeView level <= preLevel !!!");
            return;
        }

        String sceneName = "";
        preTime = currentTimeMillis;
        preEvent = eventStr;

        switch (eventStr) {
            case WarningTypeConstants.Event_FCW://前向碰撞
                handleFCW(data);
                sceneName = "前向碰撞";
                break;

            case WarningTypeConstants.Event_FCWB://后向碰撞
                handleFCWB(data);
                sceneName = "后向碰撞";
                break;

            case WarningTypeConstants.Event_ICW://交叉路口碰撞
                handleICW(data);
                sceneName = "交叉路口预警";
                break;

            case WarningTypeConstants.Event_LTA://左转辅助
                handleLTA(data);
                sceneName = "左转辅助";
                break;

            case WarningTypeConstants.Event_BSW://盲区预警
                handleBSW(data);
                sceneName = "盲区预警";
                break;

            case WarningTypeConstants.Event_LCW://变道预警
                handleLCW(data);
                sceneName = "变道预警";
                break;

            case WarningTypeConstants.Event_DNPW://逆向超车预警
                handleDNPW(data);
                sceneName = "逆向超车";
                break;

            case WarningTypeConstants.Event_EBW://紧急制动预警
                handleEBW(data);
                sceneName = "紧急制动预警";
                break;

            case WarningTypeConstants.Event_AVW://异常车辆预警
                handleAVW(data);
                sceneName = "异常车辆";
                break;

            case WarningTypeConstants.Event_AVWB://异常车辆预警
                handleAVWB(data);
                sceneName = "异常车辆";
                break;

//            case WarningTypeConstants.Event_AVWB://异常车辆预警
//                handleAVWB(showVinfoBean, activity, ivRealSituation, tvTips);
//                break;
            case WarningTypeConstants.Event_CLW://车辆失控预警  这个就一个情况 不用单独处理了
                res = R.mipmap.ic_shikongcheliang;
                speakTts = "前车失控";
                speakM4a = SceneMediaPlayerManager.clw;
                aniName = R.drawable.clw;
                sceneName = "车辆失控";
                break;

            case WarningTypeConstants.Event_EVW://紧急车辆预警
                handleEVW(data);
                sceneName = "紧急车辆";
                break;

            case WarningTypeConstants.Event_PCR://行人横穿预警
            case WarningTypeConstants.Event_VRUCW:
                handlePCR(data);
                sceneName = "行人横穿预警";
                break;

            case WarningTypeConstants.Event_RLVW://自身闯红灯  //这个不需要单独处理
                res = R.mipmap.ic_chuanghongdeng;
                aniName = R.drawable.rlvw;
                speakTts = "注意红灯";
                speakM4a = SceneMediaPlayerManager.rlvw;
                sceneName = "闯红灯预警";
                //aniName="RLVW";
                break;

            case WarningTypeConstants.Event_CLC://协作式变道
                //handleCLC(data);
                double dirAngle = data.getV2vEvent().getDirAngle();
                if (dirAngle >= 0) {//左边
//            showGifAndPlayTTs("左前方车辆变道", "ic_xiezuo_zuoqiankaojin", ivRealSituation, "协作式变道");
                    res = R.mipmap.ic_xiezuobiandao_wo_you_rang;
                    speakTts = "左车变道";
                    speakM4a = SceneMediaPlayerManager.clc_left;
                    sceneName = "左车变道";
                    aniName = R.drawable.clc_left;
                } else {//右边
//            showGifAndPlayTTs("右前方车辆变道", "ic_xiezuo_youqiankaojin", ivRealSituation, "协作式变道");
                    res = R.mipmap.ic_xiezuobiandao_wo_zuo_rang;
                    speakTts = "右车变道";
                    speakM4a = SceneMediaPlayerManager.clc_right;
                    sceneName = "右车变道";
                    aniName = R.drawable.clc_right;
                }


                break;

            case WarningTypeConstants.Event_CVM://匝道汇入
                //handleCVM(data);
                DataInfoBean.RVMSGBean showVinfoBean = data;
                int vehicleType = showVinfoBean.getVehicleType();
                double angle = showVinfoBean.getV2vEvent().getDirAngle();
                boolean isMainRoad;//本车在主道还是辅道
                boolean isPriority = true;//本车是否优先过路
                DataInfoBean.RoadInfoBean roadInfo = showVinfoBean.getRoadInfo();
                int roadType = roadInfo.getRoadType();
                isMainRoad = !(roadType == 1);

                isPriority = showVinfoBean.getV2vEvent().getAct() == 1;
                vehicleType = 1;//没有非机动图 直接赋值机动车
                if ((vehicleType >= 40 && vehicleType <= 48) || vehicleType == 85) {//非机动车
                    if (angle > 0) {//左边
                        if (isMainRoad) {//我在主道
                            if (isPriority) {//可以先过
//                        showGifAndPlayTTs("左侧有非机动车汇入，可先行通过", "ic_zaidaohuiru_left_zhu_xian_fei", ivRealSituation, "匝道汇入");

                            } else {
//                        showGifAndPlayTTs("左侧有非机动车汇入，请减速让行", "ic_zaidaohuiru_left_zhu_hou_fei", ivRealSituation, "匝道汇入");

                            }
                        } else {//我在辅道
                            if (isPriority) {//我先过
//                        showGifAndPlayTTs("左侧有非机动车汇入，可先行通过", "ic_zaidaohuiru_right_fu_xian_fei", ivRealSituation, "匝道汇入");

                            } else {
//                        showGifAndPlayTTs("左侧有非机动车汇入，请减速让行", "ic_zaidaohuiru_right_fu_hou_fei", ivRealSituation, "匝道汇入");

                            }
                        }
                    } else {//右边
                        if (isMainRoad) {//我在主道
                            if (isPriority) {//可以先过
//                        showGifAndPlayTTs("右侧有非机动车汇入，可先行通过", "ic_zaidaohuiru_right_zhu_xian_fei", ivRealSituation, "匝道汇入");

                            } else {
//                        showGifAndPlayTTs("右侧有非机动车汇入，请减速让行", "ic_zaidaohuiru_right_zhu_hou_fei", ivRealSituation, "匝道汇入");

                            }
                        } else {//我在辅道
                            if (isPriority) {//我先过
//                        showGifAndPlayTTs("右侧有非机动车汇入，可先行通过", "ic_zaidaohuiru_left_fu_xian_fei", ivRealSituation, "匝道汇入");

                            } else {
//                        showGifAndPlayTTs("右侧有非机动车汇入，请减速让行", "ic_zaidaohuiru_left_fu_hou_fei", ivRealSituation, "匝道汇入");

                            }
                        }
                    }

                } else {//机动车
                    if (angle > 0) {//远车在左边
                        if (isMainRoad) {//我在主道
                            if (isPriority) {//可以先过
                                res = R.mipmap.ic_zaidaohuiru_left_zhu_xian;
//                        speak = "左侧有车辆汇入，可先行通过";
                                /*if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                                    res = R.mipmap.ic_zaidaohuiru_left_zhu_xian_white;
                                }*/
                                speakTts = "先行汇入";
                                speakM4a = SceneMediaPlayerManager.cvw_go_first;

                            } else {
                                res = R.mipmap.ic_zaidaohuiru_left_zhu_hou;
//                        speak = "左侧有车辆汇入，请减速让行";
                                /*if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                                    res = R.mipmap.ic_zaidaohuiru_left_zhu_hou_white;
                                }*/
                                speakTts = "汇入让行";
                                speakM4a = SceneMediaPlayerManager.cvw_go_second;

                            }
                        } else {//我在辅道
                            if (isPriority) {//我先过
                                res = R.mipmap.ic_zaidaohuiru_right_fu_xian;
//                        speak = "左侧有车辆汇入，可先行通过行";
                                /*if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                                    res = R.mipmap.ic_zaidaohuiru_right_fu_xian_white;
                                }*/
                                speakTts = "先行汇入";
                                speakM4a = SceneMediaPlayerManager.cvw_go_first;
                            } else {
                                res = R.mipmap.ic_zaidaohuiru_right_fu_hou;
                                /*if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                                    res = R.mipmap.ic_zaidaohuiru_right_fu_hou_white;
                                }*/
//                        speak = "左侧有车辆汇入，请减速让行";
                                speakTts = "汇入让行";
                                speakM4a = SceneMediaPlayerManager.cvw_go_second;
                            }
                        }
                    } else {//远车在右边
                        if (isMainRoad) {//我在主道
                            if (isPriority) {//可以先过

                                //res = R.mipmap.ic_zaidaohuiru_right_zhu_xian;
                                /*if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                                    res = R.mipmap.ic_zaidaohuiru_right_zhu_xian_white;
                                }*/
//                        speak = "右侧有车辆汇入，可先行通过";
                                aniName = R.drawable.cvm_main_first;
                                speakTts = "先行汇入";
                                speakM4a = SceneMediaPlayerManager.cvw_go_first;
                                sceneName = "先行汇入";


                            } else {
                                res = R.mipmap.ic_zaidaohuiru_right_zhu_hou;
                                /*if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                                    res = R.mipmap.ic_zaidaohuiru_right_zhu_hou_white;
                                }*/
//                        speak = "右侧有车辆汇入，请减速让";
                                speakTts = "汇入让行";
                                speakM4a = SceneMediaPlayerManager.cvw_go_second;
                            }
                        } else {//我在辅道
                            if (isPriority) {//我先过
                                res = R.mipmap.ic_zaidaohuiru_left_fu_xian;
                                /*if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                                    res = R.mipmap.ic_zaidaohuiru_left_fu_xian_white;
                                }*/
//                        speak = "右侧有车辆汇入，可先行通过";
                                speakTts = "先行汇入";
                                speakM4a = SceneMediaPlayerManager.cvw_go_first;
                            } else {
                                //res = R.mipmap.ic_zaidaohuiru_left_fu_hou;
                                /*if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                                    res = R.mipmap.ic_zaidaohuiru_left_fu_hou_white;
                                }*/
//                        speak = "右侧有车辆汇入，请减速让行";

                                aniName = R.drawable.cvm_sub_second;
                                speakTts = "汇入让行";
                                speakM4a = SceneMediaPlayerManager.cvw_go_second;
                                sceneName = "汇入让行";

                            }
                        }
                    }
                }


                break;

            case WarningTypeConstants.Event_YPC://礼让行人

                handleYPC(data);
                sceneName = "注意行人";
                break;

            case WarningTypeConstants.Event_FCS://前车起步

                handleFCS(data);
                sceneName = "";
                break;

            default:
                break;
        }

        if(!TextUtils.isEmpty(speakTts)){
            XLog.w("RealTimeView ======> speak = " + speakTts);

            long currTime = System.currentTimeMillis();
            Long lastTime = speakMap.get(speakTts);
            if(lastTime == null || (currTime - lastTime > SPEAK_DURATION)){
                XLog.w("RealTimeView ======> real speak = " + speakTts);
                //SpeakerHandler.getInstance().speak(speak, SpeakerHandler.SCENE);
                BaseApplication.getInstance().sceneMediaPlayerManager.play(speakM4a);
                speakMap.put(speakTts, currTime);
            }
            speakTts = "";
        }
        if(!onlyTts && aniName != 0){
            XLog.w("RealTimeView ======> aniName = " + aniName);
            try {
//                FrameSequenceDrawable drawable = new FrameSequenceDrawable(getResources().getAssets().open("anim/" + aniName + ".webp"));
//                drawable.setLoopBehavior(FrameSequenceDrawable.LOOP_INF);
//                drawable.setOnFinishedListener(frameSequenceDrawable -> {});

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse("res://" + mContext.getPackageName() + "/" + aniName))
                        .setControllerListener(new BaseControllerListener<ImageInfo>() {
                            @Override
                            public void onFinalImageSet(
                                    String id,
                                    @Nullable ImageInfo imageInfo,
                                    @Nullable Animatable animatable) {
                                if (animatable instanceof AnimatedDrawable2) {
                                    AnimatedDrawable2 animatedDrawable = (AnimatedDrawable2) animatable;
                                    animatedDrawable.setAnimationBackend(new LoopCountModifyingBackend(animatedDrawable.getAnimationBackend(), 1000));//设置循环次数
                                    animatedDrawable.start();
                                }
                                if(animatable != null){
                                    animatable.start();
                                }
                            }
                        })
                        .build();
                eventSiv.setVisibility(VISIBLE);
                eventSiv.setController(controller);

                setVisibility(VISIBLE);
                sceneNameTv.setText(sceneName);

                warningLlyt.setVisibility(aniName == R.drawable.pcr ? INVISIBLE : VISIBLE);
            } catch (Exception e) {
                eventSiv.setVisibility(INVISIBLE);
                warningLlyt.setVisibility(INVISIBLE);
                e.printStackTrace();
            }
            aniName = 0;
        }else {
            eventSiv.setVisibility(INVISIBLE);
            warningLlyt.setVisibility(INVISIBLE);
        }
    }

    public void reset(){
        XLog.w("RealTimeView reset !!!");
        preEvent = "";
        eventSiv.setVisibility(INVISIBLE);
        sceneNameTv.setText("");
        warningLlyt.setVisibility(INVISIBLE);
    }


    //处理前向碰撞
    private void handleFCW(DataInfoBean.RVMSGBean data) {

        double heading = data.getHeading();
        double dH = heading - mHeading;
        int vehicleType = data.getVehicleType();
        if (dH < 0) {
            dH = dH + 360;
        } else if (dH > 360) {
            dH = dH - 360;
        }


        if ((dH >= 0 && dH <= 45) || (dH >= 315 && dH < 360)) {
            if (vehicleType == 82 || vehicleType == 86) {
                speakTts = "前方行人";
                speakM4a = SceneMediaPlayerManager.fcw_person;
                res = R.mipmap.ic_qianxiangpengzhuang_xingren;
                aniName = R.drawable.fcw_person_tdx;
            } else if (vehicleType == 85 || (vehicleType >= 40 && vehicleType <= 48)) {
                speakTts = "前方车辆";
                speakM4a = SceneMediaPlayerManager.fcw_car;
                res = R.mipmap.ic_qianxiangpengzhuang_feijidong;
                aniName = R.drawable.fcw_bike_tdx;
            } else {
                speakTts = "前方车辆";
                speakM4a = SceneMediaPlayerManager.fcw_car;
                res = R.mipmap.ic_qian_xiang_peng_zhuang_jidong;
                aniName = R.drawable.fcw_car_tdx;
            }
        }else if (dH >= 135 && dH <= 225) {//对向
            if (vehicleType == 82 || vehicleType == 86) {
                speakTts = "前方行人";
                speakM4a = SceneMediaPlayerManager.fcw_person;
                res = R.mipmap.ic_qianxiangpengzhuang_xingren;
                aniName = R.drawable.fcw_person_tdx;
            } else if (vehicleType == 85 || (vehicleType >= 40 && vehicleType <= 48)) {
                speakTts = "前方车辆";
                speakM4a = SceneMediaPlayerManager.fcw_car;
                res = R.mipmap.ic_qianxiangpengzhuang_feijidong;
                aniName = R.drawable.fcw_bike_tdx;
            } else {
                speakTts = "前方车辆";
                speakM4a = SceneMediaPlayerManager.fcw_car;
                res = R.mipmap.ic_qian_xiang_peng_zhuang_jidong;
                aniName = R.drawable.fcw_car_tdx;
            }
        }else {
            if (vehicleType == 82 || vehicleType == 86) {
                speakTts = "前方行人";
                speakM4a = SceneMediaPlayerManager.fcw_person;
                res = R.mipmap.ic_qianxiangpengzhuang_xingren;
                aniName = R.drawable.fcw_person_tdx;
            } else if (vehicleType == 85 || (vehicleType >= 40 && vehicleType <= 48)) {
                speakTts = "前方车辆";
                speakM4a = SceneMediaPlayerManager.fcw_car;
                res = R.mipmap.ic_qianxiangpengzhuang_feijidong;
                aniName = R.drawable.fcw_bike_tdx;
            } else {
                speakTts = "前方车辆";
                speakM4a = SceneMediaPlayerManager.fcw_car;
                res = R.mipmap.ic_qian_xiang_peng_zhuang_jidong;
                aniName = R.drawable.fcw_car_tdx;
            }
        }
    }


    /**
     * 处理后向碰撞
     * @param data
     */
    private void handleFCWB(DataInfoBean.RVMSGBean data) {

        double heading = data.getHeading();
        double dH = heading - mHeading;
        int vehicleType = data.getVehicleType();
        if (dH < 0) {
            dH = dH + 360;
        } else if (dH > 360) {
            dH = dH - 360;
        }


        if ((dH >= 0 && dH <= 45) || (dH >= 315 && dH < 360)) {
            if (vehicleType == 82 || vehicleType == 86) {
                speakTts = "后方行人";
                speakM4a = SceneMediaPlayerManager.fcwb;
                res = R.mipmap.ic_qianxiangpengzhuang_xingren;
                aniName = R.drawable.fcwb_person_tdx;
            } else if (vehicleType == 85 || (vehicleType >= 40 && vehicleType <= 48)) {
                speakTts = "后方车辆快速接近";
                speakM4a = SceneMediaPlayerManager.fcwb;
                res = R.mipmap.ic_qianxiangpengzhuang_feijidong;
                aniName = R.drawable.fcwb_bike_tdx;
            } else {
                speakTts = "后方车辆快速接近";
                speakM4a = SceneMediaPlayerManager.fcwb;
                res = R.mipmap.ic_qian_xiang_peng_zhuang_jidong;
                aniName = R.drawable.fcwb_car_tdx;
            }
        }else if (dH >= 135 && dH <= 225) {//对向
            if (vehicleType == 82 || vehicleType == 86) {
                speakTts = "后方行人";
                speakM4a = SceneMediaPlayerManager.fcwb;
                res = R.mipmap.ic_qianxiangpengzhuang_xingren;
                aniName = R.drawable.fcwb_person_tdx;
            } else if (vehicleType == 85 || (vehicleType >= 40 && vehicleType <= 48)) {
                speakTts = "后方车辆快速接近";
                speakM4a = SceneMediaPlayerManager.fcwb;
                res = R.mipmap.ic_qianxiangpengzhuang_feijidong;
                aniName = R.drawable.fcwb_bike_tdx;
            } else {
                speakTts = "后方车辆快速接近";
                speakM4a = SceneMediaPlayerManager.fcwb;
                res = R.mipmap.ic_qian_xiang_peng_zhuang_jidong;
                aniName = R.drawable.fcwb_car_tdx;
            }
        }else {
            if (vehicleType == 82 || vehicleType == 86) {
                speakTts = "后方行人";
                speakM4a = SceneMediaPlayerManager.fcwb;
                res = R.mipmap.ic_qianxiangpengzhuang_xingren;
                aniName = R.drawable.fcwb_person_tdx;
            } else if (vehicleType == 85 || (vehicleType >= 40 && vehicleType <= 48)) {
                speakTts = "后方车辆快速接近";
                speakM4a = SceneMediaPlayerManager.fcwb;
                res = R.mipmap.ic_qianxiangpengzhuang_feijidong;
                aniName = R.drawable.fcwb_bike_tdx;
            } else {
                speakTts = "后方车辆快速接近";
                speakM4a = SceneMediaPlayerManager.fcwb;
                res = R.mipmap.ic_qian_xiang_peng_zhuang_jidong;
                aniName = R.drawable.fcwb_car_tdx;
            }
        }
    }
    //处理交叉路口碰撞
    private void handleICW(DataInfoBean.RVMSGBean data) {

        int vehicleType = data.getVehicleType();
        double dirAngle = data.getV2vEvent().getDirAngle();

        if (dirAngle > 0) {//左边
            if ((vehicleType >= 40 && vehicleType <= 48) || vehicleType == 85) {
                res = R.mipmap.ic_jiaocha_zuo_feijidong;
                speakTts = "左方来车";
                speakM4a = SceneMediaPlayerManager.icw_left;
                aniName = R.drawable.icw_bike_left;
            } else {
                res = R.mipmap.ic_jiaocha_zuo_jidong;
                speakTts = "左方来车";
                speakM4a = SceneMediaPlayerManager.icw_left;
                aniName = R.drawable.icw_car_left;
            }
        } else {//右边
            if ((vehicleType >= 40 && vehicleType <= 48) || vehicleType == 85) {
                res = R.mipmap.ic_jiaocha_you_feijidong;
                speakTts = "右方来车";
                speakM4a = SceneMediaPlayerManager.icw_right;
                aniName = R.drawable.icw_bike_right;
            } else {
                res = R.mipmap.ic_jiaocha_you_jidong;
                speakTts = "右方来车";
                speakM4a = SceneMediaPlayerManager.icw_right;
                aniName = R.drawable.icw_car_right;
            }
        }
        //speak = "";

    }


    //处理左转辅助
    private void handleLTA(DataInfoBean.RVMSGBean data) {
        int vehicleType = data.getVehicleType();
        if (vehicleType == 82 || vehicleType == 86 || vehicleType == 85 || (vehicleType >= 40 && vehicleType <= 48)) {
            res = R.mipmap.ic_zuozhuan_feijidong;
            aniName = R.drawable.lta_bike;
        } else {
            res = R.mipmap.ic_zuozhuan_jidong;
            aniName = R.drawable.lta_car;
        }
        speakTts = "对向来车";
        speakM4a = SceneMediaPlayerManager.lta_dnpw;

    }

    //处理盲区预警
    private void handleBSW(DataInfoBean.RVMSGBean data) {
        int vehicleType = data.getVehicleType();
        double dirAngle = data.getV2vEvent().getDirAngle();

        if (dirAngle > 0) {//左边

            if ((vehicleType >= 40 && vehicleType <= 48) || vehicleType == 85) {

                res = R.mipmap.ic_mangqu_zuohou_feijidong;
                speakTts = "有非机动车进入左侧盲区";
                aniName = R.drawable.bsm_bike_left;
            } else {
                res = R.mipmap.ic_mangqu_zuohou_jidong;
                speakTts = "有车辆进入左侧盲区";
                aniName = R.drawable.bsm_car_left;
            }

        } else {//右边
            if ((vehicleType >= 40 && vehicleType <= 48) || vehicleType == 85) {
                res = R.mipmap.ic_mangqu_youhou_feijidong;
                speakTts = "有非机动车进入右侧盲区";
                aniName = R.drawable.bsm_bike_right;
            } else {
                res = R.mipmap.ic_mangqu_youhou_jidong;
                speakTts = "有车辆进入右侧盲区";
                aniName = R.drawable.bsm_car_right;
            }
        }
        speakTts = "";
        speakM4a = -1;
    }

    //处理变道预警
    private void handleLCW(DataInfoBean.RVMSGBean data) {
        int vehicleType = data.getVehicleType();
        double dirAngle = data.getV2vEvent().getDirAngle();
        int distanceV = (int) data.getV2vEvent().getDistanceV();
        String distanceDesc;
        if (distanceV == 0) {
            distanceDesc = "";
        } else {
            distanceDesc = distanceV + "米";
        }
        if (dirAngle > 0) {//左边
            //图不够 没有区分机动车 he非机动车
            if ((vehicleType >= 40 && vehicleType <= 48) || vehicleType == 85) {//非机动
                res = R.mipmap.ic_bd_wo_you_qian;
                speakTts = "左侧车辆";
                speakM4a = SceneMediaPlayerManager.lcw_left;
                aniName = R.drawable.lcw_bike_left;
            } else {
                res = R.mipmap.ic_bd_wo_you_qian;
                speakTts = "左侧车辆";
                speakM4a = SceneMediaPlayerManager.lcw_left;
                aniName = R.drawable.lcw_car_left;
            }
        } else {//右边
            if ((vehicleType >= 40 && vehicleType <= 48) || vehicleType == 85) {
                res = R.mipmap.ic_bd_wo_zuo_qian;
                speakTts = "右侧车辆";
                speakM4a = SceneMediaPlayerManager.lcw_right;
//                showGifAndPlayTTs("右侧" + distanceDesc + "盲区有非机动车，请小心变", "ic_biandao_right_uncar", ivRealSituation, "变道预警");
                aniName = R.drawable.lcw_bike_right;
            } else {
                res = R.mipmap.ic_bd_wo_zuo_qian;
                speakTts = "右侧车辆";
                speakM4a = SceneMediaPlayerManager.lcw_right;
//                showGifAndPlayTTs("右侧" + distanceDesc + "盲区有车辆，请小心变道", "ic_biandao_right_car", ivRealSituation, "变道预警");
                aniName = R.drawable.lcw_car_right;
            }
        }
        //speakTts = "后方车辆";

    }


    //逆向超车预警
    private void handleDNPW(DataInfoBean.RVMSGBean data) {
        int vehicleType = data.getVehicleType();
        if ((vehicleType >= 40 && vehicleType <= 48) || vehicleType == 85) {
//            showGifAndPlayTTs("对向车道有非机动车接近", "ic_nixiangchaoche_person", WarningLevelConstants.LEVEL_10, activity, ivRealSituation);

            res = R.mipmap.ic_nixiangchaoche_feijidong;
            aniName = R.drawable.dnpw;
        } else {
            res = R.mipmap.ic_nixiangchaoche_jidong;
            aniName = R.drawable.dnpw;
        }
        speakTts = "对向来车";
        speakM4a = SceneMediaPlayerManager.lta_dnpw;
    }


    //紧急制动预警
    private void handleEBW(DataInfoBean.RVMSGBean data) {
        int vehicleType = data.getVehicleType();
        int distanceV = (int) data.getV2vEvent().getDistanceV();
        String distanceDesc;
        if (distanceV == 0) {
            distanceDesc = "";
        } else {
            distanceDesc = distanceV + "米";
        }
        if ((vehicleType >= 40 && vehicleType <= 48) || vehicleType == 85) {
            res = R.mipmap.ic_ebw_fei;
            speakTts = "前方" + distanceDesc + "非机动车急刹";
            aniName = R.drawable.ebw;
        } else {
            res = R.mipmap.ic_ebw;
            speakTts = "前方" + distanceDesc + "车辆急刹";
            aniName = R.drawable.ebw;
        }

        speakTts = "前车刹车";
    }


    //双闪 也属于异常车辆
    private void handleAVW(DataInfoBean.RVMSGBean data) {
        // avw判断
//        int distanceV = (int) data.getV2vEvent().getDistanceV();
//        String distanceDesc;
//        if (distanceV == 0) {
//            distanceDesc = "";
//        } else {
//            distanceDesc = distanceV + "米";
//        }
        res = R.mipmap.ic_yichangcheliang;

        if(data.getSafetyExt() != null && data.getSafetyExt().getExteriorLights() == 16){
            speakTts = "前车双闪";
            speakM4a = SceneMediaPlayerManager.avwb_warning_lights;
            aniName = R.drawable.avw;
        }else {
            int speed = data.getSpeed();
            speakTts = speed > 0 ? "前方有慢车":"前方有停车";
            speakM4a = speed > 0 ? SceneMediaPlayerManager.avwb_slow_car:SceneMediaPlayerManager.avwb_parking_car;
            aniName = R.drawable.avwb;
        }
    }

    private void handleAVWB(DataInfoBean.RVMSGBean data) {


//        int distanceV = (int) data.getV2vEvent().getDistanceV();
//        String distanceDesc;
//        if (distanceV == 0) {
//            distanceDesc = "";
//        } else {
//            distanceDesc = distanceV + "米";
//        }
        if(data.getSafetyExt() != null && data.getSafetyExt().getExteriorLights() == 16){
            speakTts = "前车双闪";
            speakM4a = SceneMediaPlayerManager.avwb_warning_lights;
            aniName = R.drawable.avw;
        }else {
            int speed = data.getSpeed();
            speakTts = speed > 0 ? "前方有慢车":"前方有停车";
            speakM4a = speed > 0 ? SceneMediaPlayerManager.avwb_slow_car:SceneMediaPlayerManager.avwb_parking_car;
            aniName = R.drawable.avwb;
        }
    }


//    //异常车辆提醒  如果打开 这个不需要语音
//    private void handleAVWB(DataInfoBean.RVMSGBean data, Activity activity, ImageView ivRealSituation, TextView tvTips) {
//        int vehicleType = data.getVehicleType();
//        int distanceV = (int) data.getV2vEvent().getDistanceV();
//        String distanceDesc;
//        if (distanceV == 0) {
//            distanceDesc = "";
//        } else {
//            distanceDesc = distanceV + "米";
//        }
//
//        int speed = data.getSpeed();
//        if ((vehicleType >= 40 && vehicleType <= 48) || vehicleType == 85) {
//            if (speed > 0) {
//                showGifAndPlayTTs("前方" + distanceDesc + "有非机动车行驶缓慢", "ic_yichangcheliang", ivRealSituation, "异常车辆预警");
//            } else {
//                showGifAndPlayTTs("前方" + distanceDesc + "停有非机动车", "ic_yichangcheliang", ivRealSituation, "异常车辆预警");
//            }
//        } else {
//            if (speed > 0) {
//                showGifAndPlayTTs("前方" + distanceDesc + "有车辆行驶缓慢", "ic_yichangcheliang", ivRealSituation, "异常车辆预警");
//            } else {
//                showGifAndPlayTTs("前方" + distanceDesc + "停有车辆", "ic_yichangcheliang", ivRealSituation, "异常车辆预警");
//            }
//        }
//
//    }


    /**
     * 紧急车辆提醒
     * @param data
     */
    private void handleEVW(DataInfoBean.RVMSGBean data) {
        int vehicleType = data.getVehicleType();
        if (vehicleType >= 62 && vehicleType <= 65) {
            res = R.mipmap.ic_xiaofangche;
            speakTts = "请让行消防车";
            speakM4a = SceneMediaPlayerManager.evw_fire_truck;
            aniName = R.drawable.evw_fire_car;
        } else if (vehicleType >= 66 && vehicleType <= 67) {
            res = R.mipmap.ic_jingche;
            speakTts = "请让行警车";
            speakM4a = SceneMediaPlayerManager.evw_police;
            aniName = R.drawable.evw_police_car;
        } else {
            res = R.mipmap.ic_jiuhuche;
            speakTts = "请让行救护车";
            speakM4a = SceneMediaPlayerManager.evw_ambulance;
            aniName = R.drawable.evw_rescue_car;
        }

    }


    //行人横穿预警
    private void handlePCR(DataInfoBean.RVMSGBean data) {

        double dirAngle = data.getV2vEvent().getDirAngle();
        int distanceV = (int) data.getV2vEvent().getDistanceV();
        String distanceDesc;
        if (distanceV == 0) {
            distanceDesc = "";
        } else {
            distanceDesc = distanceV + "米";
        }

        if (dirAngle > 0) {//左边
            res = R.mipmap.ic_xingren_zuobian;
            speakTts = "左前" + distanceDesc + "行人横穿";
            aniName = R.drawable.pcr;
        } else {//右边
            res = R.mipmap.ic_xingren_youbian;
            speakTts = "右前" + distanceDesc + "行人横穿";
            aniName = R.drawable.pcr;
        }

        speakTts = "注意行人";
        speakM4a = SceneMediaPlayerManager.pcr_ypc;
    }


    //礼让行人
    private void handleYPC(DataInfoBean.RVMSGBean data) {
//        int angle = data.getAngle();
        double dirAngle = data.getV2vEvent().getDirAngle();
        if (dirAngle > 0) {//左边
            res = R.mipmap.ic_xingren_zuobian;
            speakTts = "注意行人";
            speakM4a = SceneMediaPlayerManager.pcr_ypc;
            aniName = R.drawable.pcr;
        } else {//右边
            res = R.mipmap.ic_xingren_youbian;
            speakTts = "注意行人";
            speakM4a = SceneMediaPlayerManager.pcr_ypc;
            aniName = R.drawable.pcr;
        }
    }

    // 前车起步
    private void handleFCS(DataInfoBean.RVMSGBean data) {
        speakTts = "前车起步";
        speakM4a = SceneMediaPlayerManager.fcs;
        aniName = 0;
    }


    //匝道汇入
    /*private void handleCVM(DataInfoBean.RVMSGBean showVinfoBean) {
        int vehicleType = showVinfoBean.getVehicleType();
        double angle = showVinfoBean.getV2vEvent().getDirAngle();
        boolean isMainRoad;//本车在主道还是辅道
        boolean isPriority = true;//本车是否优先过路
        DataInfoBean.RoadInfoBean roadInfo = showVinfoBean.getRoadInfo();
        int roadType = roadInfo.getRoadType();
        isMainRoad = !(roadType == 1);

        isPriority = showVinfoBean.getV2vEvent().getAct() == 1;
        vehicleType = 1;//没有非机动图 直接赋值机动车
        if ((vehicleType >= 40 && vehicleType <= 48) || vehicleType == 85) {//非机动车
            if (angle > 0) {//左边
                if (isMainRoad) {//我在主道
                    if (isPriority) {//可以先过
//                        showGifAndPlayTTs("左侧有非机动车汇入，可先行通过", "ic_zaidaohuiru_left_zhu_xian_fei", ivRealSituation, "匝道汇入");

                    } else {
//                        showGifAndPlayTTs("左侧有非机动车汇入，请减速让行", "ic_zaidaohuiru_left_zhu_hou_fei", ivRealSituation, "匝道汇入");

                    }
                } else {//我在辅道
                    if (isPriority) {//我先过
//                        showGifAndPlayTTs("左侧有非机动车汇入，可先行通过", "ic_zaidaohuiru_right_fu_xian_fei", ivRealSituation, "匝道汇入");

                    } else {
//                        showGifAndPlayTTs("左侧有非机动车汇入，请减速让行", "ic_zaidaohuiru_right_fu_hou_fei", ivRealSituation, "匝道汇入");

                    }
                }
            } else {//右边
                if (isMainRoad) {//我在主道
                    if (isPriority) {//可以先过
//                        showGifAndPlayTTs("右侧有非机动车汇入，可先行通过", "ic_zaidaohuiru_right_zhu_xian_fei", ivRealSituation, "匝道汇入");

                    } else {
//                        showGifAndPlayTTs("右侧有非机动车汇入，请减速让行", "ic_zaidaohuiru_right_zhu_hou_fei", ivRealSituation, "匝道汇入");

                    }
                } else {//我在辅道
                    if (isPriority) {//我先过
//                        showGifAndPlayTTs("右侧有非机动车汇入，可先行通过", "ic_zaidaohuiru_left_fu_xian_fei", ivRealSituation, "匝道汇入");

                    } else {
//                        showGifAndPlayTTs("右侧有非机动车汇入，请减速让行", "ic_zaidaohuiru_left_fu_hou_fei", ivRealSituation, "匝道汇入");

                    }
                }
            }

        } else {//机动车
            if (angle > 0) {//远车在左边
                if (isMainRoad) {//我在主道
                    if (isPriority) {//可以先过
                        res = R.mipmap.ic_zaidaohuiru_left_zhu_xian;
//                        speak = "左侧有车辆汇入，可先行通过";
                        if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                            res = R.mipmap.ic_zaidaohuiru_left_zhu_xian_white;
                        }
                        speak = "先行汇入";

                    } else {
                        res = R.mipmap.ic_zaidaohuiru_left_zhu_hou;
//                        speak = "左侧有车辆汇入，请减速让行";
                        if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                            res = R.mipmap.ic_zaidaohuiru_left_zhu_hou_white;
                        }
                        speak = "汇入让行";

                    }
                } else {//我在辅道
                    if (isPriority) {//我先过
                        res = R.mipmap.ic_zaidaohuiru_right_fu_xian;
//                        speak = "左侧有车辆汇入，可先行通过行";
                        if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                            res = R.mipmap.ic_zaidaohuiru_right_fu_xian_white;
                        }
                        speak = "先行汇入";
                    } else {
                        res = R.mipmap.ic_zaidaohuiru_right_fu_hou;
                        if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                            res = R.mipmap.ic_zaidaohuiru_right_fu_hou_white;
                        }
//                        speak = "左侧有车辆汇入，请减速让行";
                        speak = "汇入让行";
                    }
                }
            } else {//远车在右边
                if (isMainRoad) {//我在主道
                    if (isPriority) {//可以先过

                        res = R.mipmap.ic_zaidaohuiru_right_zhu_xian;
                        if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                            res = R.mipmap.ic_zaidaohuiru_right_zhu_xian_white;
                        }
//                        speak = "右侧有车辆汇入，可先行通过";
                        speak = "先行汇入";
                    } else {
                        res = R.mipmap.ic_zaidaohuiru_right_zhu_hou;
                        if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                            res = R.mipmap.ic_zaidaohuiru_right_zhu_hou_white;
                        }
//                        speak = "右侧有车辆汇入，请减速让";
                        speak = "汇入让行";
                    }
                } else {//我在辅道
                    if (isPriority) {//我先过
                        res = R.mipmap.ic_zaidaohuiru_left_fu_xian;
                        if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                            res = R.mipmap.ic_zaidaohuiru_left_fu_xian_white;
                        }
//                        speak = "右侧有车辆汇入，可先行通过";
                        speak = "先行汇入";
                    } else {
                        res = R.mipmap.ic_zaidaohuiru_left_fu_hou;
                        if (TextUtils.equals(SkinConfig.getCurrentSkin(), SkinConfig.WHITE_SKIN)) {
                            res = R.mipmap.ic_zaidaohuiru_left_fu_hou_white;
                        }
//                        speak = "右侧有车辆汇入，请减速让行";
                        speak = "汇入让行";
                    }
                }
            }
        }
    }*/


    //协作式变道
    private void handleCLC(DataInfoBean.RVMSGBean showVinfoBean) {
        double dirAngle = showVinfoBean.getV2vEvent().getDirAngle();
        if (dirAngle >= 0) {//左边
//            showGifAndPlayTTs("左前方车辆变道", "ic_xiezuo_zuoqiankaojin", ivRealSituation, "协作式变道");
            res = R.mipmap.ic_xiezuobiandao_wo_you_rang;
            speakTts = "左车变道";
            aniName = R.drawable.clc_left;
        } else {//右边
//            showGifAndPlayTTs("右前方车辆变道", "ic_xiezuo_youqiankaojin", ivRealSituation, "协作式变道");
            res = R.mipmap.ic_xiezuobiandao_wo_zuo_rang;
            speakTts = "右车变道";
            aniName = R.drawable.clc_right;
        }
    }


    public boolean isShow(){
        return getVisibility() == VISIBLE && eventSiv.getVisibility() == VISIBLE && warningLlyt.getVisibility() == VISIBLE;
    }

}
