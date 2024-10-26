package com.matou.smartcar.util;

import static android.content.Context.TELEPHONY_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.elvishew.xlog.XLog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.matou.smartcar.base.BaseApplication;
import com.matou.smartcar.bean.AlertTypeBean;
import com.matou.smartcar.bean.BaseResult;
import com.matou.smartcar.bean.RsiResBean;
import com.matou.smartcar.global.WarningTypeConstants;
import com.matou.smartcar.global.ZYWarningLevelConstants;
import com.matou.smartcar.net.Api;
import com.matou.smartcar.net.BaseObserver;
import com.matou.smartcar.net.NetManager;
import com.matou.smartcar.net.RxSchedulers;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AppUtils {

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意三个参数的时间格式要一致
     *
     * @param nowTime
     * @param startTime
     * @param endTime
     * @return 在时间段内返回true，不在返回false
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
    }

    public static byte[] readStream(Activity activity, String fileName) {
        try {
            InputStream inStream = activity.getResources().getAssets().open(fileName);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            outStream.close();
            inStream.close();
            return outStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCustomStyleFilePath(Context context, String customStyleFileName) {
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        String parentPath = null;

        try {
            inputStream = context.getAssets().open(customStyleFileName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            parentPath = context.getFilesDir().getAbsolutePath();
            File customStyleFile = new File(parentPath + "/" + customStyleFileName);
            if (customStyleFile.exists()) {
                customStyleFile.delete();
            }
            customStyleFile.createNewFile();

            outputStream = new FileOutputStream(customStyleFile);
            outputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e("CustomMapDemo", "Close stream failed", e);
                return null;
            }
        }

        return parentPath + "/" + customStyleFileName;
    }


//    public static LatLng getConvertLatLng(Activity activity, LatLng sourceLatLng) {
//        CoordinateConverter converter = new CoordinateConverter();
//// CoordType.GPS 待转换坐标类型
//        converter.from(CoordinateConverter.CoordType.GPS);
//// sourceLatLng待转换坐标点 LatLng类型
//        converter.coord(sourceLatLng);
//// 执行转换操作
//        return converter.convert();
//    }

    public static int getPort() {
        String port = (String) SPUtil.get(SPUtil.SERVER_PORT, "4410");


        if (TextUtils.isEmpty(port)) {
            port = "4410";
        }
        return Integer.parseInt(port);
    }


    public static String getFileSize(int length) {
        DecimalFormat format = new DecimalFormat("#.00");
        int KB = 1024;
        int MB = 1024 * 1024;
        String size;

        if (length < KB) {
            size = length + "B";
        } else if (length < MB) {
            size = format.format(length * 1.0 / KB) + "KB";
        } else {
            size = format.format(length * 1.0 / MB) + "MB";
        }

        return size;
    }

    public static String getVersionName() {
        try {
            PackageManager manager = BaseApplication.getInstance().getPackageManager();
            PackageInfo info = manager.getPackageInfo(BaseApplication.getInstance().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static int getVersionCode() {
        try {
            PackageManager manager = BaseApplication.getInstance().getPackageManager();
            PackageInfo info = manager.getPackageInfo(BaseApplication.getInstance().getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static String getDeviceId() {
        String deviceId = "";
        TelephonyManager tm = (TelephonyManager) BaseApplication.getInstance().getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(BaseApplication.getInstance(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            deviceId = tm != null ? tm.getDeviceId() : "";
        }

        if (TextUtils.isEmpty(deviceId)) {
            if (Build.VERSION.SDK_INT >= 29) {
                deviceId = Settings.System.getString(BaseApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
            } else {
                deviceId = "";
            }
        }
        return deviceId;
    }


    public static String getPwd() {
        String pwd = (String) SPUtil.get(SPUtil.SERVER_PWD, "123456");
        if (TextUtils.isEmpty(pwd)) {
            pwd = "123456";
        }
        return pwd;
    }

    public static String getUserName() {
        String userName = (String) SPUtil.get(SPUtil.SERVER_USENAME, "caeri");
        if (TextUtils.isEmpty(userName)) {
            userName = "caeri";
        }
        return userName;
    }

    public static String getServerIp() {
        String ip = (String) SPUtil.get(SPUtil.SERVER_PORT, "192.168.9.1:1883");
//        String ip = (String) SPUtil.get(SPUtil.SERVER_IP, "192.168.31.161:1883");
        ip = "tcp://" + ip;
        return ip;
    }


    public static int dp2px(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5);
    }

    public static int getDensityDpi(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.densityDpi;
    }


    /**
     * 判断当前应用是否在前台
     * 系统签名的应用，可以获取其他应用进程，当前是否在前台 ，非系统签名应用只能获取自己的
     */
    public static boolean isRunningForeground() {

        ActivityManager activityManager = (ActivityManager) BaseApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }

        String packageName = BaseApplication.getInstance().getApplicationInfo().packageName;

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (packageName.equalsIgnoreCase(runningAppProcessInfo.processName) && runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取需要展现的rsi信息，按优先级排序，最多显示两个
     *
     * @param srcRsiList
     * @return
     */
    public static List<RsiResBean> getShowRsiList(List<RsiResBean> srcRsiList) {
        if (srcRsiList.size() < 2) {
            return srcRsiList;
        } else {
            srcRsiList.sort(new Comparator<RsiResBean>() {
                @Override
                public int compare(RsiResBean o1, RsiResBean o2) {
                    // 相等的情况下，谁有语音谁先
                    if(o2.getPriority() == o1.getPriority()){
                        if(!TextUtils.isEmpty(o2.getPlayDescription())){
                            return 1;
                        }else if(!TextUtils.isEmpty(o1.getPlayDescription())){
                            return -1;
                        }
                        return o2.getPriority() - o1.getPriority();
                    }
                    return o2.getPriority() - o1.getPriority();
                }
            });
            return srcRsiList.subList(0, 2);
        }
    }


    public static boolean isOnlyTts(String eventStr) {
        return !((WarningTypeConstants.Event_FCW.equals(eventStr))
                || (WarningTypeConstants.Event_FCWB.equals(eventStr))
                || (WarningTypeConstants.Event_PCR.equals(eventStr))
                || (WarningTypeConstants.Event_VRUCW.equals(eventStr))
                || (WarningTypeConstants.Event_YPC.equals(eventStr))
                || (WarningTypeConstants.Event_ICW.equals(eventStr))
                || (WarningTypeConstants.Event_LTA.equals(eventStr))
                || (WarningTypeConstants.Event_AVW.equals(eventStr))
                || (WarningTypeConstants.Event_CVM.equals(eventStr))
                || (WarningTypeConstants.Event_CLC.equals(eventStr))
                || (WarningTypeConstants.Event_AVWB.equals(eventStr))
                || (WarningTypeConstants.Event_BSW.equals(eventStr))
                || (WarningTypeConstants.Event_CLW.equals(eventStr))
                || (WarningTypeConstants.Event_DNPW.equals(eventStr))
                || (WarningTypeConstants.Event_EBW.equals(eventStr))
                || (WarningTypeConstants.Event_EVW.equals(eventStr))
                || (WarningTypeConstants.Event_RLVW.equals(eventStr))
        );
    }

    public static boolean globalPriority(int priority, String preEvent) {
        return priority > ZYWarningLevelConstants.handleWarningLevel(preEvent);
    }


    /**
     * 判断当前界面显示的是哪个Activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName();
    }


    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    public static ArrayList<String> getHomePackages(Context context) {
        ArrayList<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        //属性
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    /**
     * 判断当前界面是否是桌面
     */
    public static boolean isHome(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        String runningPackage = rti.get(0).topActivity.getPackageName();
        ArrayList<String> desktopAppPackages = getHomePackages(context);
        return desktopAppPackages.contains(runningPackage);
    }


    public static String getAppVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }

    public static void uploadUpdateStatus(String upgradeCode, int status, String message) {
        XLog.w("uploadUpdateStatus upgradeCode = " + upgradeCode + ", status = " + status + ", message = " + message);
        String time = String.valueOf(System.currentTimeMillis());
        String encrypt = MD5Utils.encrypt(NetManager.getAppId() + "|" + time + "|" + "QAZzaq1!");

        message = "版本：" + AppUtils.getAppVersionName(BaseApplication.getInstance()) + ", " + message;

        //String encrypt = MD5Utils.encrypt(NetManager.getAppId() + "|" + time + "|" + "test");
        NetManager.getApi(NetManager.mBaseUrl, Api.class)
                .uploadUpdateStatus(NetManager.getAppId(), time, encrypt, getDeviceId(), upgradeCode, status, message)
                .compose(RxSchedulers.applySchedulers())
                .subscribe(new BaseObserver<BaseResult<Object>>() {
                    @Override
                    public void onSuccess(BaseResult<Object> baseResult) {
                        XLog.w("uploadUpdateStatus onSuccess");

                        // 状态上传成功，清除调升级过程数据
                        SPUtil.set("UpdateStatus", "");
                    }

                    @Override
                    public void onErrorMsg(String msg) {
                        XLog.w("uploadUpdateStatus onErrorMsg = " + msg);
                    }
                });

    }

    /**
     * 获取友好的时间显示
     *
     * @param time
     * @return
     */
    public static String convertDuration(int time) {
        StringBuilder sb = new StringBuilder();
        if (time < 60) {
            sb.append(CommUtils.formatNum(time)).append("秒");
        }else if (time < 3600) {
            int minute = time / 60 ;
            int second = time % 60;
            sb.append(CommUtils.formatNum(minute)).append("分钟");
            if(second > 0){
                sb.append(CommUtils.formatNum(second)).append("秒");
            }
        }else {
            int hour = time / 3600;
            sb.append(CommUtils.formatNum(hour)).append("小时");

            int minute = (time % 3600) / 60;
            if(minute > 0){
                sb.append(CommUtils.formatNum(minute)).append("分钟");
            }

            int second = (time % 3600) % 60;
            if(second > 0){
                sb.append(CommUtils.formatNum(second)).append("秒");
            }
        }
        return sb.toString();
    }


    private static long mLastCpuTime = 0;
    private static long mLastIdleTime = 0;
    private static long mLastUpdateTime = System.currentTimeMillis();

    /**
     * 获取系统cpu利用率
     * @return
     */
    public static float getCpuUsage() {
        long cpuTime = 0;
        long idleTime = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/stat"));
            String line = br.readLine();
            if (line.startsWith("cpu")) {
                String[] values = line.split("\\s+");
                cpuTime = Long.parseLong(values[1]) + Long.parseLong(values[2]) + Long.parseLong(values[3])
                        + Long.parseLong(values[4]) + Long.parseLong(values[6]) + Long.parseLong(values[5])
                        + Long.parseLong(values[7]);
                idleTime = Long.parseLong(values[4]);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - mLastUpdateTime;
        mLastUpdateTime = currentTime;

        float cpuUsage = 0.0f;
        if (mLastCpuTime != 0) {
            float totalDiff = (float) (cpuTime - mLastCpuTime);
            float idleDiff = (float) (idleTime - mLastIdleTime);
            cpuUsage = (totalDiff - idleDiff) / totalDiff;
        }

        mLastCpuTime = cpuTime;
        mLastIdleTime = idleTime;

        return cpuUsage;
    }


    /**
     * 获取系统内存利用率
     * @param context
     * @return
     */
    public static float getMemUsage(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem * 1.0f / memoryInfo.totalMem;
    }

    public static String getDateFormat(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    public static Bitmap generateQRCode(String text) throws WriterException {
        int width = 200; // 二维码的宽度
        int height = 200; // 二维码的高度
        BitMatrix bitMatrix;

        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1); /* default = 4 */
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            bitMatrix = new MultiFormatWriter().encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    width,
                    height,
                    hints
            );
        } catch (IllegalArgumentException e) {
            // 无效的格式
            return null;
        }

        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static String getProp(String key) {

        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            return (String) systemProperties.getMethod("get", String.class).invoke(systemProperties, key);
        } catch (Exception e) {
            e.printStackTrace();
            return "no version";
        }
    }

    /**
     * 兼容处理平台下发的descriptions，有两种可能：
     * 1、ascii码
     * 2、a50
     * @param descriptions
     * @return
     */
    public static AlertTypeBean parseAlertBean(String descriptions){
        AlertTypeBean alertTypeBean = new AlertTypeBean();
        try {
            checkFirstLetter(alertTypeBean, CommUtils.convertHexToString(descriptions));
        }catch (Exception e){
            //e.printStackTrace();
            checkFirstLetter(alertTypeBean, descriptions);
        }
        return alertTypeBean;
    }

    /**
     * 检查第一个字符是否为字母
     * @param alertTypeBean
     * @param alertTypeStr
     */
    private static void checkFirstLetter(AlertTypeBean alertTypeBean, String alertTypeStr) {
        if (Character.isLetter(alertTypeStr.charAt(0))) {
            alertTypeBean.setSubType(alertTypeStr.substring(0, 1));
            alertTypeBean.setValue(alertTypeStr.substring(1));
        } else {
            alertTypeBean.setSubType("");
            alertTypeBean.setValue(alertTypeStr);
        }
    }

}
