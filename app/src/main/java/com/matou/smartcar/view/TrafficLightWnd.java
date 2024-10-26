package com.matou.smartcar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elvishew.xlog.XLog;
import com.flyjingfish.gradienttextviewlib.GradientTextView;
import com.matou.smartcar.R;
import com.matou.smartcar.base.BaseApplication;
import com.matou.smartcar.bean.DataInfoBean;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.util.AppUtils;
import com.matou.smartcar.util.CommUtils;
import com.matou.smartcar.util.DisplayUtil;
import com.matou.smartcar.view.base.BaseWnd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * @author ranfeng
 */
public class TrafficLightWnd extends BaseTrafficLightWnd {

    private static final int MSG_SUPPLE_FRAME = 10000;
    private static final int MSG_AUTO_HIDE = 10001;
    @BindView(R.id.flyt_whole)
    FrameLayout wholeFlyt;

    @BindView(R.id.llyt_base)
    LinearLayout baseLlyt;

    private final List<DataInfoBean.TrafficLightBean.LightBean> dataList = new ArrayList<>();

    /**
     * 每个倒计时的出现次数
     */
    private final HashMap<Integer, Integer> timeCountMap = new HashMap<>();

    private final View[] tempViewList = new View[12];

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_SUPPLE_FRAME) {

                // 只对倒计时大于1的进行补帧，因为我们无法预知下一灯态
//                if(minLight.getTime() > 1){
//                    Message msg2 = Message.obtain();
//                    msg2.what = MSG_SUPPLE_FRAME;
//                    msg2.arg1 = 950;
//                    handler.sendMessageDelayed(msg2, msg2.arg1);
//                }
                int newTime = minLight.getTime();
                int oldTime = msg.arg2;

                XLog.w("ranfeng handleMessage minLight time = " + minLight.getTime() + ", delay = " + msg.arg1);
                dataList.forEach(item -> item.setTime(item.getTime() - 1));

                updateUI();
            }else if (msg.what == MSG_AUTO_HIDE) {
                hideFloatWindow();
            }
        }
    };


    DataInfoBean.TrafficLightBean.LightBean minLight = new DataInfoBean.TrafficLightBean.LightBean();

    public TrafficLightWnd(Context context) {
        super(context);
        init();
    }

    private void init() {
        wndType = GlobalVariables.WND_TYPE_3;

        for(int i=0; i<tempViewList.length; i++){
            tempViewList[i] = LayoutInflater.from(context).inflate(R.layout.item_traffic_light, null);
        }

    }

    @Override
    public int bindLayout() {
        return R.layout.view_traffic_light;
    }

    /**
     * 为了防止过度绘制，此处进行拦截
     * @param data
     */
    private long preTime = 0;

    private boolean playingStartTips;

    @Override
    public void bindData(DataInfoBean data) {

        if(AppUtils.isRunningForeground()){
            wholeFlyt.setBackgroundColor(context.getColor(R.color.transparent));
        }else {
            wholeFlyt.setBackgroundColor(context.getColor(R.color.low_black));
        }

        if (data == null) {
            XLog.w("data is null");
            return;
        }

        long nowTime = System.currentTimeMillis();
        if(nowTime - preTime < 500){
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

        if (lightList == null || lightList.size() == 0) {
            //XLog.w("ranfeng light hide");
            hideFloatWindow();
        //    handler.removeCallbacksAndMessages(null);
        } else {
            //XLog.w("ranfeng light show");
            showFloatWindow();
            autoHideWnd(5000);

            //supplyFrame(lightList);

            dataList.clear();
            dataList.addAll(lightList);
            updateUI();
        }
    }

    /**
     * 5秒内未收到红绿灯数据，自动关闭
     */
    private void autoHideWnd(int delay) {
        handler.removeCallbacksAndMessages(null);
        Message msg = Message.obtain();
        msg.what = MSG_AUTO_HIDE;
        handler.sendMessageDelayed(msg, delay);
    }

    /**
     * 进行补帧操作
     *
     * @param lightList
     */
    private void supplyFrame(List<DataInfoBean.TrafficLightBean.LightBean> lightList) {
        minLight = lightList.get(0);
        for (int i=0; i< lightList.size(); i++){
            DataInfoBean.TrafficLightBean.LightBean item = lightList.get(i);
            if(item.getTime() < minLight.getTime()){
                minLight = item;
            }
        }

        // 只对倒计时大于1的进行补帧，因为我们无法预知下一灯态
        if(minLight.getTime() > 1){
            Integer count = timeCountMap.get(minLight.getTime());
            if(count == null){
                timeCountMap.clear();
                timeCountMap.put(minLight.getTime(), 0);
            }else {
                timeCountMap.put(minLight.getTime(), count + 1);
            }

            Integer newCount = timeCountMap.get(minLight.getTime());
            long tempDelay = 1500;
            XLog.w("ranfeng tempDelay = " + tempDelay);
            if(tempDelay > 10){
                handler.removeCallbacksAndMessages(null);

                Message msg = Message.obtain();
                msg.what = MSG_SUPPLE_FRAME;
                msg.arg1 = (int) tempDelay;
                msg.arg2 = minLight.getTime();

                handler.sendMessageDelayed(msg, tempDelay);
            }

        }

        XLog.w("ranfeng findMinLight minLight time = " + minLight.getTime());
    }

    private final List<View> viewList = new ArrayList<>();

    private boolean showFlag;
    private long lastTime;

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        viewList.clear();

        // 最多显示8个红绿灯
        int lightNum = Math.min(dataList.size(), 12);

        // mock
        // lightNum = 1;

        for (int i=0; i<lightNum; i++){

            DataInfoBean.TrafficLightBean.LightBean item = dataList.get(i);
            View view = tempViewList[i];

            RelativeLayout lightRlyt = view.findViewById(R.id.rlyt_light);
            LinearLayout greenLlyt = view.findViewById(R.id.llyt_green);
            ImageView lightIv = view.findViewById(R.id.iv_light);
            TextView timeTv = view.findViewById(R.id.tv_time);
            TextView greenSpeedTv = view.findViewById(R.id.tv_green_speed);

            // mock
/*            if(i != 0){
                lightBgRlyt.setBackgroundColor(Color.TRANSPARENT);
                curLaneGtv.setVisibility(View.INVISIBLE);
            }else {
                lightBgRlyt.setBackgroundResource(R.mipmap.bg_current_lane);
                curLaneGtv.setVisibility(View.VISIBLE);
            }*/


            // 红绿灯小于6个居中展示，大于6个从左到右
            ViewGroup.LayoutParams baseLlytLp = baseLlyt.getLayoutParams();
            if (lightNum < 6){
                baseLlytLp.width = DisplayUtil.getScreenWidth(context.getApplicationContext()) / 2;
                baseLlyt.setGravity(Gravity.CENTER_HORIZONTAL);
            }else {
                baseLlytLp.width = DisplayUtil.getScreenWidth(context.getApplicationContext());
                baseLlyt.setGravity(Gravity.START);
            }
            baseLlyt.setLayoutParams(baseLlytLp);


            int color = item.getColor();
            int time = item.getTime();

            // mock
            // color = 8;
            // time = 120;

            timeTv.setText(String.valueOf(CommUtils.formatNum(Math.min(time, 99))));
            int dir = item.getDir();
            int textColor = R.color.transparent;
            int bgRes = 0;
            boolean isAnim = false;
            switch (color) {
                case 0://未知
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
            } else { //掉头
                lightIv.setImageResource(R.mipmap.trun_round);
            }

            /**
             * 处理绿波速度、绿灯起步提醒，考虑三种情况：
             * 1、当前道路只有一个车道
             * 2、车道级地图能够明确知道当前在哪个车道上（非车道地图 右转是否受灯控）—— 待实现
             * 3、具有导航的情况下明确知道当前要往哪个方向行驶（需考虑同方向多车道相位不一致情况）—— 待实现
             */
            if(lightNum == 1){
                if(GlobalVariables.greenWaveEnable){
                    int down = item.getSpeedDown();
                    int up = item.getSpeedUp();

                    // mock
                    /*down = 0;
                    up = 0;*/
                    if (down != 0 && up != 0){
                        greenSpeedTv.setText(CommUtils.formatNum(down) + "-" + CommUtils.formatNum(up));
                        greenLlyt.setVisibility(View.VISIBLE);
                    }else {
                        greenLlyt.setVisibility(View.GONE);
                    }
                }else {
                    greenLlyt.setVisibility(View.GONE);
                }

                // 红灯倒计时剩3s，开始提醒
                if(!playingStartTips && time <= 3 && color == 3){
                    XLog.w("---> playingStartTips ");
                    playingStartTips = true;
                    BaseApplication.getInstance().greenStartPlayer.play(onCompletionListener);
                }
            }else {
                greenLlyt.setVisibility(View.GONE);
            }

            timeTv.setTextColor(context.getColor(textColor));
            lightRlyt.setBackgroundResource(bgRes);
            viewList.add(view);
        }

        long nowTime = System.currentTimeMillis();
        if (nowTime - lastTime > 800) {
            showFlag = !showFlag;
            lastTime = nowTime;
        }

        baseLlyt.removeAllViews();
        int size = viewList.size();
        for (int i = 0; i < size; i++) {
            View itemView = viewList.get(i);
            View view = itemView.findViewById(R.id.rlyt_light);
            DataInfoBean.TrafficLightBean.LightBean lightBean = dataList.get(i);
            if (lightBean.isAnimIng()) {
                view.setVisibility(showFlag ? View.VISIBLE : View.INVISIBLE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
            baseLlyt.addView(itemView);
        }
    }

    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            playingStartTips = false;
        }
    };


}
