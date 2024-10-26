package com.matou.smartcar.view.base;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;

import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.util.AppUtils;
import com.matou.smartcar.util.DeviceUtil;
import com.matou.smartcar.util.DisplayUtil;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.ButterKnife;

/**
 * @author ranfeng
 */
public abstract class BaseWnd {

    public int wndType = -1;

    public Context context;

    private View rootLyt;

    private WindowManager.LayoutParams wndParams;
    private WindowManager wndManager;

    private boolean canShow;

    public BaseWnd(Context context) {
        this.context = context;
        init();
    }


    private void init() {
        rootLyt = LayoutInflater.from(context).inflate(bindLayout(), null);
        ButterKnife.bind(this, rootLyt);

        wndParams = new WindowManager.LayoutParams();
        wndManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wndParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wndParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        wndParams.format = PixelFormat.RGBA_8888;
        //wndParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wndParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                // 加上这句话悬浮窗不拦截事件
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        wndParams.gravity = Gravity.START | Gravity.TOP;
        wndParams.width = DisplayUtil.getScreenWidth(context.getApplicationContext());
        wndParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        wndParams.x = 0;
        wndParams.y = 0;
    }

    /**
     * 绑定布局ID
     * @return
     */
    public abstract @LayoutRes int bindLayout();



    public boolean showFloatWindow() {
//        if(!Settings.canDrawOverlays(context)){
//            XLog.e("no overlays permission");
//            return false;
//        }

        // 事件悬浮窗非桌面和APP内不显示
        if(wndType == GlobalVariables.WND_TYPE_1 && !AppUtils.isHome(context)){
            XLog.e("wndType = " + wndType);
            return false;
        }

        if(wndType < 0){
            XLog.e("wndType is not inited!!!");
            return false;
        }

        Map<Integer, Boolean> wndShowMap = GlobalVariables.wndShowMap;
        Set<Map.Entry<Integer, Boolean>> entries = wndShowMap.entrySet();

        // 当前有优先级高于此wnd的在显示，则不能显示
        canShow = true;

//        entries.forEach(item -> {
//            if(canShow.get()){
//                Integer type = item.getKey();
//                if(type < wndType && Boolean.TRUE.equals(item.getValue())){
//                    canShow.set(false);
//                }
//            }
//        });

        // 超速优先级最高
/*        entries.forEach(item -> {
            if(canShow){
                if((item.getKey() == GlobalVariables.WND_TYPE_0) && (Boolean.TRUE.equals(item.getValue()))){
                    canShow = false;
                }
            }
        });*/

        XLog.w("===> canShow = " + canShow + ", wndType = " + wndType);

        if(canShow){
            boolean isShow = Boolean.TRUE.equals(wndShowMap.get(wndType));
            if (rootLyt.getParent() == null && !isShow) {
                wndShowMap.put(wndType, true);
                XLog.w("ranfeng light actual show");
                wndManager.addView(rootLyt, wndParams);
                return true;
            }
        }
        return false;

    }

    public boolean hideFloatWindow() {
        if(wndType < 0){
            XLog.e("wndType is not inited!!!");
            return false;
        }

        boolean isShow = Boolean.TRUE.equals(GlobalVariables.wndShowMap.get(wndType));
        if (rootLyt.getParent() != null && isShow) {
            GlobalVariables.wndShowMap.put(wndType, false);
            XLog.w("ranfeng light actual hide");
            wndManager.removeView(rootLyt);
            return true;
        }
        return false;
    }

}
