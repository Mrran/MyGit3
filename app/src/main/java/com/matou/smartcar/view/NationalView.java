package com.matou.smartcar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.elvishew.xlog.XLog;
import com.matou.smartcar.R;
import com.matou.smartcar.base.BaseApplication;
import com.matou.smartcar.bean.RsiResBean;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.speaker.SpeakerHandler;
import com.matou.smartcar.util.CommUtils;
import com.matou.smartcar.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ranfeng
 */
public class NationalView extends FrameLayout {

    @BindView(R.id.rvFlag)
    RecyclerView rvFlag;

    private Context mContext;
    private BaseQuickAdapter mFlagAdapter;
    private final List<RsiResBean> mResIdList = new ArrayList<>();

    /**
     * 当前播放ID
     */
    private String currRsiId;

    private int count;

    private final static int MSG_HIDE_WND = 1000;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_HIDE_WND) {
                //XLog.w("national view set showing = false");

                // 定时释放显示状态
                showing = false;
                if(!GlobalVariables.traffInfoEnable){
                    setVisibility(INVISIBLE);
                }
            }

        }
    };

    /**
     * 当前是否正在显示
     */
    private boolean showing;

    /**
     * 当前rsi播放优先级
     */
    public int currPriority;


    public NationalView(@NonNull Context context) {
        this(context, null);
    }

    public NationalView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NationalView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_national, null, false);
        addView(view);
        ButterKnife.bind(this, view);

        rvFlag.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mFlagAdapter = new BaseQuickAdapter<RsiResBean, BaseViewHolder>(R.layout.item_traffic_flag, mResIdList) {
            @Override
            protected void convert(BaseViewHolder helper, RsiResBean item) {
                ImageView ivItemFlag = helper.getView(R.id.iv_item_flag);
                TextView tvValue = helper.getView(R.id.tvValue);
                String value = item.getValue();
                if (!TextUtils.isEmpty(value) && item.isShowValue()) {
                    if(value.length() <= 2){
                        tvValue.setTextSize(DisplayUtil.spToPx(BaseApplication.getInstance(), 11));
                    }else if(value.length() == 3){
                        tvValue.setTextSize(DisplayUtil.spToPx(BaseApplication.getInstance(), 9));
                    }else if(value.length() == 4){
                        tvValue.setTextSize(DisplayUtil.spToPx(BaseApplication.getInstance(), 6));
                    }else {
                        tvValue.setTextSize(DisplayUtil.spToPx(BaseApplication.getInstance(), 4));
                    }
                    tvValue.setText(value);
                }else {
                    tvValue.setText("");
                }
                Glide.with(mContext).load(item.getRes()).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        ivItemFlag.setImageDrawable(resource);
                    }
                });
            }
        };
        rvFlag.addItemDecoration(new NationalItemDecoration(DisplayUtil.dpToPx(getContext(), 9)));
        rvFlag.setAdapter(mFlagAdapter);
    }


    public void bindData(List<RsiResBean> resList, boolean canPlay) {
        if ((resList != null && resList.size() > 0)) {
            // 第0个为优先级最高的
            RsiResBean maxPriorityItem = resList.get(0);

            // 新的rsi优先级高于当前正在播放的优先级，则强制打断语音
            if((!TextUtils.isEmpty(maxPriorityItem.getPlayDescription())) && (maxPriorityItem.getPriority() > currPriority)){
                showing = false;
                count = 0;
                XLog.e("=======> currPriority stopSpeak");
                //SpeakerHandler.getInstance().stopSpeak();
            }
        }

        if (showing) {
            //XLog.w("---> national view is showing");
            return;
        }
        if ((resList != null && resList.size() > 0)) {

            showing = true;
            RsiResBean maxPriorityItem = resList.get(0);
            currPriority = maxPriorityItem.getPriority();

            // 相同的id则10s才更新一次
            if(TextUtils.equals(currRsiId, maxPriorityItem.getRsiId())){
                count++;
            }else {
                count = 0;
            }

            // 相同类型事件12s报一次，不同类型事件3s报一次
            if(count % 12 == 0){
                if (canPlay) {
                    String tts;
                    if(!TextUtils.isEmpty(maxPriorityItem.getPlayDescription())){
                        tts = maxPriorityItem.getPlayDescription();
                    }else if(resList.size() > 1 && !TextUtils.isEmpty(resList.get(1).getPlayDescription())){
                        tts = resList.get(1).getPlayDescription();
                    }else {
                        tts = "";
                    }
                    playTts(tts);
                }

                //XLog.w("national view start show");
                setVisibility(VISIBLE);
                mResIdList.clear();
                mResIdList.addAll(resList);
                mFlagAdapter.notifyDataSetChanged();
            }

            currRsiId = maxPriorityItem.getRsiId();

            handler.removeMessages(MSG_HIDE_WND);
            handler.sendEmptyMessageDelayed(MSG_HIDE_WND, GlobalVariables.SHOW_RSI_DURATION);

        }else {
            //XLog.w("national view start hide");
            currPriority = GlobalVariables.RSI_LEVEL_0;
            currRsiId = "";
            count = 0;
            setVisibility(INVISIBLE);
        }
    }

    private void playTts(String ttsStr) {
        if (TextUtils.isEmpty(ttsStr)) {
            CommUtils.playTipsSound();
        }else {
            SpeakerHandler.getInstance().speak(ttsStr, SpeakerHandler.National);
        }
    }
}
