package com.matou.smartcar.global;

import android.os.Environment;

import com.matou.smartcar.base.BaseApplication;

public class Config {
    /**
     * 文件存储基础路径
     */
//    public static final String BASE_PATH = BaseApplication.getInstance().getExternalCacheDir() + "/ZQY/";

    /**
     * 文件存储基础路径
     */
    public static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ZQY/";
    public static final String APK_NAME = "Idawns_";
    public static final String APK_POSTFIX = ".apk";

}
