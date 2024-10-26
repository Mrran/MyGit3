package com.matou.smartcar.net;

import android.accounts.NetworkErrorException;

import com.matou.smartcar.bean.BaseResult;
import com.matou.smartcar.util.CommUtils;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class BaseObserver<T extends BaseResult> implements Observer<T> {
    public static final String TAG = BaseObserver.class.getSimpleName();
    public static final String SUCCESS_CODE_1 = "200";
    public static final String SUCCESS_CODE_2 = "0000";

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        try {
            if (t == null) {
//                ToastUtil.show("服务器异常");
//            onErrorMsg("服务器异常");
            } else if ("app_01_0006".equals(t.getCode())){
//                LogoutManager.logout(App.getApplication());
                onErrorMsg(t.getMsg());
            }else if ("app_99_1111".equals(t.getCode())) {
//                EventBus.getDefault().post(new ForceUpdateEvent());
                onErrorMsg(t.getMsg());
            } else if (SUCCESS_CODE_1.equals(t.getCode()) || SUCCESS_CODE_2.equals(t.getCode())) {
                onSuccess(t);
            } else {
                onErrorMsg(t.getMsg());
            }
        }catch (Exception e){
            onErrorMsg(e.getMessage());
        }

    }


    @Override
    public void onError(Throwable e) {

        if (CommUtils.isNetworkError(e)) {
            onErrorMsg("NetError");
        } else {
            onErrorMsg("数据异常");
        }
        //onErrorMsg("数据异常");

    }

    @Override
    public void onComplete() {

    }


    public abstract void onSuccess(T t);

    public abstract void onErrorMsg(String msg);
}