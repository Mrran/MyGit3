package com.matou.smartcar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.matou.smartcar.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ranfeng
 */
public class SettingSwitch extends FrameLayout {

    @BindView(R.id.tv_name)
    TextView nameTv;

    @BindView(R.id.tb_control)
    ToggleButton controlTb;

    private Context mContext;

    private OnCheckedChangeListener onCheckedChangeListener;

    private boolean isProcess;

    public SettingSwitch(@NonNull Context context) {
        this(context, null);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public SettingSwitch(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingSwitch(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        View view = LayoutInflater.from(mContext).inflate(R.layout.setting_switch_lyt, null, false);
        addView(view);
        ButterKnife.bind(this, view);

        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.setting_switch);
        String name = typedArray.getString(R.styleable.setting_switch_name);
        nameTv.setText(name);
        typedArray.recycle();

        initView();
    }

    private void initView() {
        controlTb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            nameTv.setTextColor(isChecked ? Color.parseColor("#ffffff") : Color.parseColor("#a7baf2"));

            // 手动点击才回调
            if(!isProcess){
                if(onCheckedChangeListener != null){
                    onCheckedChangeListener.onCheckedChanged(SettingSwitch.this, isChecked);
                }
            }
            isProcess = false;
        });
    }

    /**
     * 设置是否开启
     * @param check
     */
    public void setCheck(boolean check){
        isProcess = true;
        controlTb.setChecked(check);
        isProcess = false;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(View buttonView, boolean isChecked);
    }
}
