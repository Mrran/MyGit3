package com.matou.smartcar.view;

import android.content.Context;
import android.os.Handler;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.elvishew.xlog.XLog;
import com.matou.smartcar.R;
import com.matou.smartcar.bean.DataInfoBean;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.speaker.SpeakerHandler;
import com.matou.smartcar.view.base.BaseWnd;

import butterknife.BindView;

/**
 * @author ranfeng
 */
public class GlobalTipsWnd extends BaseWnd {

    @BindView(R.id.clyt_global_bg)
    ConstraintLayout globalClyt;

    @BindView(R.id.tv_current_speed)
    TextView currentSpeedTv;


    private long preTime;

    private final Handler handler = new Handler();

    private int count;

    private final Runnable runnable  = () -> {
        XLog.w("---> delay 5000 to speedLimit Window");
        hideFloatWindow();
    };

    public GlobalTipsWnd(Context context) {
        super(context);
        init();
    }

    private void init() {
        wndType = GlobalVariables.WND_TYPE_0;
    }

    @Override
    public int bindLayout() {
        return R.layout.global_tips_lyt;
    }



    public void bindData(DataInfoBean data) {
        if (data == null) {
            checkShowStatus(false, 0);
            XLog.w("data is null");
            return;
        }

        if(data.getHVMSG() != null && data.getHVMSG().getRoadInfo() != null){
            DataInfoBean.RoadInfoBean roadInfo = data.getHVMSG().getRoadInfo();
            float speedLimit = roadInfo.getSpeedLimit();

            // 解决限速为0的异常
            if(speedLimit < 5){
                speedLimit = 70;
            }

            float currSpeed = data.getHVMSG().getSpeed();
            if (currSpeed > speedLimit) {
                checkShowStatus(true, speedLimit);
            } else {
                checkShowStatus(false, 0);
            }
        }else {
            checkShowStatus(false, 0);
        }
    }

    private void checkShowStatus(boolean isShow, float speedLimit) {
        if(count > 4){
            count = 0;
            if(isShow){
                showFloatWindow();
                long nowTime = System.currentTimeMillis();
                if(nowTime - preTime < GlobalVariables.SHOW_SPEED_DURATION){
                    return;
                }
                preTime = nowTime;

                /**
                 * 判断语音优先级，需要优化
                 */
                SpeakerHandler.getInstance().speak("您已超速，道路限速" + ((int)speedLimit), SpeakerHandler.SPEEDING);

                currentSpeedTv.setText(String.valueOf((int)speedLimit));
            }else {
                hideFloatWindow();
            }
        }
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(runnable, 5000);
        count++;
    }

    @Override
    public boolean showFloatWindow() {
        if(super.showFloatWindow()){
            globalClyt.clearAnimation();
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            //设置动画时间d
            anim.setDuration(1200);
            anim.setRepeatCount(Integer.MAX_VALUE);
            //设置动画
            globalClyt.startAnimation(anim);
            return true;
        }
        return false;
    }

    @Override
    public boolean hideFloatWindow() {
        if(super.hideFloatWindow()){
            globalClyt.clearAnimation();
            return true;
        }
        return false;
    }
}
