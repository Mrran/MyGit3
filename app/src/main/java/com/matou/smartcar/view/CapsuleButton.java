package com.matou.smartcar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
public class CapsuleButton extends FrameLayout {

    @BindView(R.id.btn_left)
    Button leftBtn;

    @BindView(R.id.btn_middle)
    Button middleBtn;

    @BindView(R.id.btn_right)
    Button rightBtn;

    private Context mContext;

    private OnButtonClickListener onButtonClickListener;

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public CapsuleButton(@NonNull Context context) {
        this(context, null);
    }

    public CapsuleButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CapsuleButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        View view = LayoutInflater.from(mContext).inflate(R.layout.capsule_button_lyt, null, false);
        addView(view);
        ButterKnife.bind(this, view);

        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.capsule_button);
        boolean showMiddle = typedArray.getBoolean(R.styleable.capsule_button_show_middle, false);
        middleBtn.setVisibility(showMiddle ? VISIBLE : GONE);

        String leftName = typedArray.getString(R.styleable.capsule_button_left_name);
        leftBtn.setText(leftName);
        String middleName = typedArray.getString(R.styleable.capsule_button_middle_name);
        middleBtn.setText(middleName);
        String rightName = typedArray.getString(R.styleable.capsule_button_right_name);
        rightBtn.setText(rightName);
        typedArray.recycle();

        initView();
    }

    private void initView() {
        leftBtn.setOnClickListener(v -> {
            leftBtn.setBackgroundResource(R.drawable.capsule_btn_left_selected);
            leftBtn.setTextColor(Color.parseColor("#112141"));

            middleBtn.setBackgroundResource(R.drawable.capsule_btn_middle_normal);
            middleBtn.setTextColor(Color.parseColor("#a7b9ea"));

            rightBtn.setBackgroundResource(R.drawable.capsule_btn_right_normal);
            rightBtn.setTextColor(Color.parseColor("#a7b9ea"));

            if(onButtonClickListener != null){
                onButtonClickListener.onButtonClick(this, 0);
            }
        });

        middleBtn.setOnClickListener(v -> {
            leftBtn.setBackgroundResource(R.drawable.capsule_btn_left_normal);
            leftBtn.setTextColor(Color.parseColor("#a7b9ea"));

            middleBtn.setBackgroundResource(R.drawable.capsule_btn_middle_selected);
            middleBtn.setTextColor(Color.parseColor("#112141"));

            rightBtn.setBackgroundResource(R.drawable.capsule_btn_right_normal);
            rightBtn.setTextColor(Color.parseColor("#a7b9ea"));

            if(onButtonClickListener != null){
                onButtonClickListener.onButtonClick(this, 1);
            }
        });

        rightBtn.setOnClickListener(v -> {
            leftBtn.setBackgroundResource(R.drawable.capsule_btn_left_normal);
            leftBtn.setTextColor(Color.parseColor("#a7b9ea"));

            middleBtn.setBackgroundResource(R.drawable.capsule_btn_middle_normal);
            middleBtn.setTextColor(Color.parseColor("#a7b9ea"));

            rightBtn.setBackgroundResource(R.drawable.capsule_btn_right_selected);
            rightBtn.setTextColor(Color.parseColor("#112141"));

            if(onButtonClickListener != null){
                onButtonClickListener.onButtonClick(this, 2);
            }
        });
    }

    /**
     * 设置当前选中项
     * @param index 左中右分别为0,1,2
     */
    public void setSelect(int index){
        if(index == 0){
            leftBtn.setBackgroundResource(R.drawable.capsule_btn_left_selected);
            leftBtn.setTextColor(Color.parseColor("#112141"));

            middleBtn.setBackgroundResource(R.drawable.capsule_btn_middle_normal);
            middleBtn.setTextColor(Color.parseColor("#a7b9ea"));

            rightBtn.setBackgroundResource(R.drawable.capsule_btn_right_normal);
            rightBtn.setTextColor(Color.parseColor("#a7b9ea"));
        }else if(index ==1 ){
            leftBtn.setBackgroundResource(R.drawable.capsule_btn_left_normal);
            leftBtn.setTextColor(Color.parseColor("#a7b9ea"));

            middleBtn.setBackgroundResource(R.drawable.capsule_btn_middle_selected);
            middleBtn.setTextColor(Color.parseColor("#112141"));

            rightBtn.setBackgroundResource(R.drawable.capsule_btn_right_normal);
            rightBtn.setTextColor(Color.parseColor("#a7b9ea"));
        }else {
            leftBtn.setBackgroundResource(R.drawable.capsule_btn_left_normal);
            leftBtn.setTextColor(Color.parseColor("#a7b9ea"));

            middleBtn.setBackgroundResource(R.drawable.capsule_btn_middle_normal);
            middleBtn.setTextColor(Color.parseColor("#a7b9ea"));

            rightBtn.setBackgroundResource(R.drawable.capsule_btn_right_selected);
            rightBtn.setTextColor(Color.parseColor("#112141"));
        }
    }

    /**
     * 按钮点击监听器
     */
    public interface OnButtonClickListener {

        /**
         *
         * @param view
         * @param index 左中右分别为0,1,2
         */
        void onButtonClick(View view, int index);
    }

}
