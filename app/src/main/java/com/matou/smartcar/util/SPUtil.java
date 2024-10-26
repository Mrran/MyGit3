package com.matou.smartcar.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.matou.smartcar.base.BaseApplication;

import java.util.List;

/**
 * SharedPreferences工具类
 */
public class SPUtil {

    public static final String SP_NAME = "SmartCar_Matou";//sp的Name
    public static final String SERVER_IP = "SERVER_IP";//服务器地址
    public static final String SERVER_PORT = "SERVER_PORT";//服务器地址
    public static final String SERVER_USENAME = "SERVER_USENAME";//服务器账号
    public static final String SERVER_PWD = "SERVER_PWD";//服务器密码
    public static final String MAP_ZOOM = "MAP_ZOOM";//地图缩放等级
    public static final String IS_NIGHT = "IS_NIGHT";//是否是夜间模式
    public static final String MD5_KEY = "MD5_KEY";//md5


    /**
     * 保存Preferences
     *
     * @param key   键
     * @param value 值，值的类型共5种：Boolean、Integer、Float、Long、String
     */
    public static void set(String key, Object value) {
        Context context = BaseApplication.getInstance();
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        }

        editor.apply();
    }

    /**
     * 读取Preferences
     *
     * @param key          键
     * @param defaultValue 默认值，值的类型共5种：Boolean、Integer、Float、Long、String
     * @return 值
     */
    public static Object get(String key, Object defaultValue) {
        Context context = BaseApplication.getInstance();

        Object object = null;
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        if (defaultValue instanceof Boolean) {
            object = sp.getBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Integer) {
            object = sp.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Float) {
            object = sp.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Long) {
            object = sp.getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof String) {
            object = sp.getString(key, (String) defaultValue);
        }

        return object;
    }

    /**
     * 手动清楚缓存数据
     */
    public static void clearLocalData() {
        //手动 缓存数据清理

    }


    /**
     * 获取对象列表
     * @param key
     * @return
     * @param <T>
     */
    public static <T> List<T> getDataList(String key) {
        String strJson = (String) get(key, "");
        if(TextUtils.isEmpty(strJson)){
            return null;
        }
        return GsonUtil.gson.fromJson(strJson, new TypeToken<List<T>>() {}.getType());

    }

    /**
     * 设置对象列表
     * @param key
     * @param dataList
     * @param <T>
     */
    public static <T>  void setDataList(String key, List<T> dataList) {
        String strJson;
        if (null == dataList || dataList.size() <= 0) {
            strJson = "";
        }else {
            strJson = GsonUtil.gson.toJson(dataList);
        }
        set(key, strJson);
    }

}
