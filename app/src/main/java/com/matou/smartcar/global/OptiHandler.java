package com.matou.smartcar.global;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @author ranfeng
 */
public class OptiHandler extends Handler {
    private final WeakReference<Activity> actRef;

    private OnOptiHandleListener listener;

    public void setListener(OnOptiHandleListener listener) {
        this.listener = listener;
    }

    public OptiHandler(Activity activity) {
        actRef = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        final Activity activity = actRef.get();
        if (activity != null && listener != null) {
            listener.onOptiHandle(msg);
        }
    }

    public interface OnOptiHandleListener {

        void onOptiHandle(Message msg);

    }


}


