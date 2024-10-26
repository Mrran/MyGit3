package com.matou.smartcar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author ranfeng
 */
public class MultiClickView extends View {

    private int clickCount;

    private OnMultiClickLitener onMultiClickLitener;

    public OnMultiClickLitener getOnMultiClickLitener() {
        return onMultiClickLitener;
    }

    public void setOnMultiClickLitener(OnMultiClickLitener onMultiClickLitener) {
        this.onMultiClickLitener = onMultiClickLitener;
    }

    public MultiClickView(Context context) {
        super(context);

        init();
    }

    public MultiClickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiClickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        this.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                clickCount++;
                if (clickCount >= 5) {
                    clickCount = 0;
                    if(onMultiClickLitener != null){
                        onMultiClickLitener.onMultiClick(v);
                    }
                }
            }
        });

    }


    public interface OnMultiClickLitener {


        void onMultiClick(View v);

    }



}
