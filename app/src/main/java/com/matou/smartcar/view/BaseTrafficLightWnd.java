package com.matou.smartcar.view;

import android.content.Context;

import com.matou.smartcar.bean.DataInfoBean;
import com.matou.smartcar.view.base.BaseWnd;

/**
 * @author ranfeng
 */
public abstract class BaseTrafficLightWnd extends BaseWnd {


    public BaseTrafficLightWnd(Context context) {
        super(context);
    }

    public void bindData(DataInfoBean data) {

    }
}
