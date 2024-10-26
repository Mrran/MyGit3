package com.matou.smartcar.view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


/**
 * 标志标牌间距
 * @author ranfeng
 */
public class NationalItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public NationalItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
    }
}
