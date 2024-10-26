package com.matou.smartcar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.elvishew.xlog.XLog;
import com.flyjingfish.gradienttextviewlib.GradientTextView;
import com.matou.smartcar.R;
import com.matou.smartcar.base.BaseApplication;
import com.matou.smartcar.bean.DataInfoBean;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.util.AppUtils;
import com.matou.smartcar.util.CommUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import butterknife.BindView;

/**
 * @author ranfeng
 */
public class TrafficLightWndNew extends BaseTrafficLightWnd {

    private final static int GREEN_START_DISTANCE_LIMIT = 50;

    private static final int MSG_AUTO_HIDE = 10001;
    @BindView(R.id.flyt_whole)
    ConstraintLayout wholeFlyt;

    @BindView(R.id.llyt_base)
    LinearLayout baseLlyt;

    @BindView(R.id.tv_curr_gps_bad)
    TextView currGpsBadTv;

    /**
     * 按车道分组
     */
    private Map<Integer, List<DataInfoBean.TrafficLightBean.LightBean>> dataMap = new HashMap<>();

    /**
     * 每个倒计时的出现次数
     */
    private final HashMap<Integer, Integer> timeCountMap = new HashMap<>();

    private final View[] tempViewList = new View[12];

    /**
     * 单车道按道路级展示，不做合并
     */
    private boolean isMerge = true;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_AUTO_HIDE) {
                hideFloatWindow();
            }
        }
    };

    private int laneIndex;
    private Integer currLaneId;

    public TrafficLightWndNew(Context context) {
        super(context);
        init();
    }

    private void init() {
        wndType = GlobalVariables.WND_TYPE_3;

        for (int i = 0; i < tempViewList.length; i++) {
            tempViewList[i] = LayoutInflater.from(context).inflate(R.layout.item_traffic_light_new, null);
        }

    }

    @Override
    public int bindLayout() {
        return R.layout.view_traffic_light_new;
    }

    /**
     * 为了防止过度绘制，此处进行拦截
     *
     * @param data
     */
    private long preTime = 0;

    private boolean playingStartTips;

    @Override
    public void bindData(DataInfoBean data) {

        if (AppUtils.isRunningForeground()) {
            wholeFlyt.setBackgroundColor(context.getColor(R.color.transparent));
        } else {
            wholeFlyt.setBackgroundColor(context.getColor(R.color.low_black));
        }

        if (data == null) {
            XLog.w("data is null");
            return;
        }

        long nowTime = System.currentTimeMillis();
        if (nowTime - preTime < 500) {
            return;
        }
        preTime = nowTime;

        DataInfoBean.TrafficLightBean trafficLight = data.getTrafficLight();
        if (trafficLight == null) {
            XLog.w("trafficLight is null");
            hideFloatWindow();
            return;
        }
        List<DataInfoBean.TrafficLightBean.LightBean> lightList = trafficLight.getLight();
        double distance = trafficLight.getDistance();
        if (lightList == null || lightList.size() == 0) {
            hideFloatWindow();
        } else {
            showFloatWindow();
            autoHideWnd(5000);

            currLaneId = lightList.stream()
                    .filter(light -> light.getCurrDir() == 1)
                    .findFirst()
                    .map(DataInfoBean.TrafficLightBean.LightBean::getLaneID)
                    .orElse(0);
            dataMap = lightList.stream().collect(Collectors.groupingBy(DataInfoBean.TrafficLightBean.LightBean::getLaneID));

            updateUI(distance);
        }
    }

    /**
     * 5秒内未收到红绿灯数据，自动关闭
     */
    private void autoHideWnd(int delay) {
        handler.removeMessages(MSG_AUTO_HIDE);
        Message msg = Message.obtain();
        msg.what = MSG_AUTO_HIDE;
        handler.sendMessageDelayed(msg, delay);
    }

    private final Map<Integer, View> viewMap = new HashMap<>();

    private boolean showFlag;
    private long lastTime;

    @SuppressLint("SetTextI18n")
    private void updateUI(double distance) {
        Resources resources = context.getResources();
        String packageName = context.getPackageName();

        viewMap.clear();

        // 最多显示12个红绿灯
        List<Integer> laneIdList = dataMap.keySet().stream().sorted().collect(Collectors.toList());
        int laneNum = Math.min(laneIdList.size(), 12);

        laneIndex = 0;
        dataMap.entrySet().forEach(entry -> {
            List<DataInfoBean.TrafficLightBean.LightBean> lightBeanList = entry.getValue();
            ViewGroup itemView = (ViewGroup) tempViewList[laneIndex];

            RelativeLayout laneBgRlyt = itemView.findViewById(R.id.rlyt_current_lane);
            GradientTextView curLaneGtv = itemView.findViewById(R.id.gtv_current_lane);
            LinearLayout greenSpeedLlyt = itemView.findViewById(R.id.llyt_green);
            TextView greenSpeedTv = itemView.findViewById(R.id.tv_green_speed);

            ImageView leftDividerIv = itemView.findViewById(R.id.iv_lane_left_divider);
            ImageView rightDividerIv = itemView.findViewById(R.id.iv_lane_right_divider);

            int currLandIndex = laneIdList.indexOf(currLaneId);
            if(laneIndex == currLandIndex){
                leftDividerIv.setVisibility(View.VISIBLE);
                rightDividerIv.setVisibility(View.VISIBLE);
                leftDividerIv.setBackgroundResource(R.mipmap.icon_curr_lane_divider);
                rightDividerIv.setBackgroundResource(R.mipmap.icon_curr_lane_divider);
            }else if(laneIndex == currLandIndex - 1){
                leftDividerIv.setVisibility(View.VISIBLE);
                rightDividerIv.setVisibility(View.GONE);
                leftDividerIv.setBackgroundResource(R.mipmap.icon_other_lane_divider);
                if(laneIndex == 0){
                    leftDividerIv.setVisibility(View.GONE);
                }
            }else if(laneIndex == currLandIndex + 1){
                leftDividerIv.setVisibility(View.GONE);
                rightDividerIv.setVisibility(View.VISIBLE);
                rightDividerIv.setBackgroundResource(R.mipmap.icon_other_lane_divider);
                if(laneIndex == laneNum - 1){
                    rightDividerIv.setVisibility(View.GONE);
                }
            }else if(laneIndex < currLandIndex){
                leftDividerIv.setVisibility(View.VISIBLE);
                rightDividerIv.setVisibility(View.GONE);
                leftDividerIv.setBackgroundResource(R.mipmap.icon_other_lane_divider);
                if(laneIndex == 0){
                    leftDividerIv.setVisibility(View.GONE);
                }
            }else {
                leftDividerIv.setVisibility(View.GONE);
                rightDividerIv.setVisibility(View.VISIBLE);
                rightDividerIv.setBackgroundResource(R.mipmap.icon_other_lane_divider);
                if(laneIndex == laneNum - 1){
                    rightDividerIv.setVisibility(View.GONE);
                }
            }
            laneIndex++;

            // 车道1开始获取
            DataInfoBean.TrafficLightBean.LightBean light0 = lightBeanList.get(0);

            if(isMerge){
                lightBeanList = mergeLightList(lightBeanList);
                if (light0.getCurrDir() == 1) {
                    curLaneGtv.setVisibility(View.VISIBLE);
                    laneBgRlyt.setBackgroundResource(R.drawable.curr_lane_bg);
                } else {
                    curLaneGtv.setVisibility(View.INVISIBLE);
                    laneBgRlyt.setBackgroundResource(R.color.transparent);
                }
            }else {
                laneBgRlyt.setBackgroundResource(R.color.transparent);
            }

            itemView.findViewById(R.id.llyt_item_light0).setVisibility(View.GONE);
            itemView.findViewById(R.id.llyt_item_light1).setVisibility(View.GONE);
            itemView.findViewById(R.id.llyt_item_light2).setVisibility(View.GONE);
            itemView.findViewById(R.id.llyt_item_light3).setVisibility(View.GONE);

            for (int j = 0; j < lightBeanList.size(); j++) {

                LinearLayout itemLaneLlyt = itemView.findViewById(resources.getIdentifier("llyt_item_light" + j, "id", packageName));
                itemLaneLlyt.setVisibility(View.VISIBLE);

                RelativeLayout lightRlyt = itemView.findViewById(resources.getIdentifier("rlyt_light" + j, "id", packageName));

                ImageView lightIv = itemView.findViewById(resources.getIdentifier("iv_light" + j, "id", packageName));
                TextView timeTv = itemView.findViewById(resources.getIdentifier("tv_time" + j, "id", packageName));

                DataInfoBean.TrafficLightBean.LightBean item = lightBeanList.get(j);

                int color = item.getColor();
                int time = item.getTime();

                timeTv.setText(String.valueOf(CommUtils.formatNum(Math.min(time, 99))));
                int dir = item.getDir();
                int textColor = R.color.transparent;
                int bgRes = 0;
                boolean isAnim = false;
                switch (color) {
                    case 0://未知
                        bgRes = R.mipmap.trafficlight_bg_gray;
                        break;

                    case 4://绿灯待行（红末闪烁）
                    case 5://绿灯
                    case 6://受保护相位绿灯（箭头灯）
                        textColor = R.color.traffic_light_green;
                        bgRes = R.mipmap.trafficlight_bg_green;
                        break;
                    case 7://黄灯
                        textColor = R.color.traffic_light_yellow;
                        bgRes = R.mipmap.trafficlight_bg_yellow;
                        break;
                    case 8://黄闪
                        isAnim = true;
                        textColor = R.color.traffic_light_yellow;
                        bgRes = R.mipmap.trafficlight_bg_yellow;
                        break;
                    case 3://红灯
                        textColor = R.color.traffic_light_red;
                        bgRes = R.mipmap.trafficlight_bg_red;
                        break;
                    case 2://红闪
                        isAnim = true;
                        textColor = R.color.traffic_light_red;
                        bgRes = R.mipmap.trafficlight_bg_red;
                        break;
                    default:

                        break;
                }
                item.setAnimIng(isAnim);

                if (dir == 1) { //直行
                    lightIv.setImageResource(R.mipmap.go_straight);
                } else if (dir == 2) { //右转
                    lightIv.setImageResource(R.mipmap.trun_right);
                } else if (dir == 3) { //左转
                    lightIv.setImageResource(R.mipmap.trun_left);
                } else if (dir == 4) { //掉头
                    lightIv.setImageResource(R.mipmap.trun_round);
                }else if (dir == 34) { //掉头+左转
                    lightIv.setImageResource(R.mipmap.turn_round_left);
                } else if (dir == 13) { //左转+直行
                    lightIv.setImageResource(R.mipmap.turn_left_staight);
                } else if (dir == 12) { //直行+右转
                    lightIv.setImageResource(R.mipmap.turn_traight_right);
                } else {
                    //continue;
                }

                /**
                 * 处理绿波速度、绿灯起步提醒，考虑三种情况：
                 * 1、当前道路只有一个车道
                 * 2、车道级地图能够明确知道当前在哪个车道上（非车道地图 右转是否受灯控）—— 待实现
                 * 3、具有导航的情况下明确知道当前要往哪个方向行驶（需考虑同方向多车道相位不一致情况）—— 待实现
                 */
                if (laneNum == 1 && entry.getValue().size() == 1) {
                    showGreenWave(item, true, greenSpeedLlyt, greenSpeedTv);
                    if(GlobalVariables.carStatus.getMoveStatus() != 2){
                        playGreenStart(color, time, distance);
                    }

                }else if(currLaneId.equals(entry.getKey())){
                    if(entry.getValue().size() == 1){
                        showGreenWave(item, true, greenSpeedLlyt, greenSpeedTv);
                        XLog.w("---> playGreenStart before time = " + time + ", playingStartTips = " + playingStartTips);
                        if(GlobalVariables.carStatus.getMoveStatus() != 2){
                            playGreenStart(color, time, distance);
                        }
                    }else {
                        boolean allSpeedDownEqual = lightBeanList.stream()
                                .map(DataInfoBean.TrafficLightBean.LightBean::getSpeedDown)
                                .distinct()
                                .count() == 1;

                        boolean allSpeedUpEqual = lightBeanList.stream()
                                .map(DataInfoBean.TrafficLightBean.LightBean::getSpeedUp)
                                .distinct()
                                .count() == 1;
                        if(allSpeedDownEqual && allSpeedUpEqual){
                            showGreenWave(item, true, greenSpeedLlyt, greenSpeedTv);
                        }else {
                            showGreenWave(null, false, greenSpeedLlyt, null);
                        }
                    }
                }else {
                    showGreenWave(null, false, greenSpeedLlyt, null);
                }

                timeTv.setTextColor(context.getColor(textColor));
                lightRlyt.setBackgroundResource(bgRes);
            }
            viewMap.put(entry.getKey(), itemView);
        });

        long nowTime = System.currentTimeMillis();
        if (nowTime - lastTime > 500) {
            showFlag = !showFlag;
            lastTime = nowTime;
        }

        baseLlyt.removeAllViews();

        viewMap.entrySet().forEach(entry -> {
            View itemView = entry.getValue();
            List<DataInfoBean.TrafficLightBean.LightBean> lightBeanList = dataMap.get(entry.getKey());

            int size = lightBeanList.size();
            for(int i=0; i<size; i++){
                View view = itemView.findViewById(resources.getIdentifier("iv_light" + i, "id", packageName));
                DataInfoBean.TrafficLightBean.LightBean lightBean = lightBeanList.get(i);
                if (lightBean.isAnimIng()) {
                    view.setVisibility(showFlag ? View.VISIBLE : View.INVISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
            baseLlyt.addView(itemView);
        });

        if(GlobalVariables.rtkInfo != null){
            int state = GlobalVariables.rtkInfo.getState();
            if(state == 4 || state == 6){
                currGpsBadTv.setVisibility(View.GONE);
            }else {
                currGpsBadTv.setVisibility(View.VISIBLE);
            }
        }else {
            currGpsBadTv.setVisibility(View.GONE);
        }
    }

    /**
     *
     * @param lightBeanList
     * @return
     */
    private List<DataInfoBean.TrafficLightBean.LightBean> mergeLightList(List<DataInfoBean.TrafficLightBean.LightBean> lightBeanList) {
        Map<String, DataInfoBean.TrafficLightBean.LightBean> mergedMap = new LinkedHashMap<>();

        for (DataInfoBean.TrafficLightBean.LightBean item : lightBeanList) {
            String key = item.getColor() + "-" + item.getTime();
            if (mergedMap.containsKey(key)) {
                DataInfoBean.TrafficLightBean.LightBean existingItem = mergedMap.get(key);
                String combinedDir = combineDirs(existingItem.getDir(), item.getDir());
                existingItem.setDir(Integer.parseInt(combinedDir));
            } else {
                mergedMap.put(key, item);
            }
        }

        return new ArrayList<>(mergedMap.values());
    }

    private String combineDirs(int dir1, int dir2) {
        String combined = dir1 < dir2 ? String.valueOf(dir1) + dir2 : String.valueOf(dir2) + dir1;
        char[] chars = combined.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    /**
     * 红灯倒计时剩3s，开始提醒
     * @param color
     * @param time
     */
    private void playGreenStart(int color, int time, double distance) {
        if (!playingStartTips && time <= 3 && color == 3 && distance < GREEN_START_DISTANCE_LIMIT) {
            XLog.w("---> playingStartTips ");
            playingStartTips = true;
            BaseApplication.getInstance().greenStartPlayer.play(onCompletionListener);
        }
    }

    /**
     * 显示绿波
     *
     * @param item
     * @param isShow
     * @param greenLlyt
     * @param greenSpeedTv
     */
    @SuppressLint("SetTextI18n")
    private void showGreenWave(DataInfoBean.TrafficLightBean.LightBean item, boolean isShow, LinearLayout greenLlyt, TextView greenSpeedTv) {
        if(isShow && GlobalVariables.greenWaveEnable){
            int down = item.getSpeedDown();
            int up = item.getSpeedUp();

            if (up > 0) {
                greenSpeedTv.setText(CommUtils.formatNum(down) + "-" + CommUtils.formatNum(up));
                greenLlyt.setVisibility(View.VISIBLE);
            } else {
                greenLlyt.setVisibility(View.GONE);
            }
        }else {
            greenLlyt.setVisibility(View.GONE);
        }
    }

    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            // 延时重置播放状态，防止重复播放
            handler.postDelayed(() -> playingStartTips = false, 5000);
        }
    };


}
