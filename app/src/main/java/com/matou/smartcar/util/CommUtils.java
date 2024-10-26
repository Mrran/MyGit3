package com.matou.smartcar.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.text.TextUtils;

import com.elvishew.xlog.XLog;
import com.matou.smartcar.R;
import com.matou.smartcar.base.BaseApplication;
import com.matou.smartcar.global.GlobalVariables;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.concurrent.TimeoutException;

/**
 * 通用，与业务无关的工具类
 * @author ranfeng
 */
public class CommUtils {

    /**
     * 将十六进制字符串转换为 ASCII 字符串。
     */
    public static String convertHexToString(String hex) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            char c = (char) Integer.parseInt(str, 16);
            // 检查字符是否是字母、数字或小数点
            if (Character.isLetter(c) || Character.isDigit(c) || c == '.') {
                sb.append(c);
            }else {
                throw new Exception("");
            }
        }
        return sb.toString();
    }

    /**
     * 可视化数字转换
     * @param number
     * @return
     */
    public static String formatNum(int number){
        if(number == 0){
            return "0";
        }
        return number > 9 ? String.valueOf(number) : String.format("%02d", number);
    }

    /**
     * 保留一位小数
     * @param value
     * @return
     */
    public static double remainOneDouble(double value){
        return Math.round(value * 10.0) / 10.0;
    }


    private static final double EARTH_RADIUS = 6378.137;

    /**
     * 弧度转换
     * @param d
     * @return
     */
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 返回单位是:米
     * @param longitude1
     * @param latitude1
     * @param longitude2
     * @param latitude2
     * @return
     */
    public static double getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        //有小数的情况;注意这里的10000d中的“d”
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return s;
    }

    public static String formatDistance(double distanceInMeters) {
        if (distanceInMeters >= 1000) {
            // 距离超过或等于1000米，转化为千米并保留一位小数
            double distanceInKilometers = distanceInMeters / 1000.0;
            return String.format("%.1fkm", distanceInKilometers);
        } else {
            // 距离未超过1000米，直接以米为单位显示
            return ((int)distanceInMeters) + "m";
        }
    }

    /**
     * 返回距离
     * @param longitude1
     * @param latitude1
     * @param longitude2
     * @param latitude2
     * @return
     */
    public static String getDistanceStr(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        return formatDistance(getDistance(longitude1, latitude1, longitude2, latitude2));
    }


    /**
     * 获取导航白天黑夜模式
     *
     * @return
     */
    public static int getNaviMode() {

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        if (hour > 6 && hour < 19) {
            return 1;
        }
        return 0;
    }

    /**
     * 判断是否为网络异常
     * @param e
     * @return
     */
    public static boolean isNetworkError(Throwable e){
        return e instanceof ConnectException || e instanceof UnknownHostException || e instanceof SocketTimeoutException
                || e instanceof TimeoutException || e instanceof MalformedURLException || e instanceof NoRouteToHostException;
    }

    public static boolean isVersionGreaterThan(String version1, String version2) {

        /**
         * {
         *     "id": "07010178",
         *     "sn": "221207010178",
         *     "supplier": "CAERI",
         *     "model": "ICTC-V855",
         *     "mfd": "2021-5-1",
         *     "swVer": "OBU_D_3.1.0",
         *     "hwVer": "OBU_GV_LB-LW10",
         *     "protocolStack": "Day1",
         *     "uptime": "00:16 "
         * }
         */

        if(TextUtils.isEmpty(version1)){
            return false;
        }

        try {
            String[] verList = version1.split("_");
            version1 = verList[verList.length -1];
        }catch (Exception e){
            e.printStackTrace();
            version1 = GlobalVariables.OBU_VERSION_SUPPORT_LANE_MAP;
        }


        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int v1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int v2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (v1 > v2) {
                return true;
            } else if (v1 < v2) {
                return false;
            }
        }
        return false;
    }

    /**
     * 打印当前时间戳
     */
    public static void sprintTimestamp(String word){
        XLog.w("=========> " + word + ": " + SystemClock.currentThreadTimeMillis());
    }

    private static long msgId;
    public static String geneMsgId(){
        String msdIdStr = String.valueOf(msgId);
        msgId++;
        return msdIdStr;
    }

    public static void playTipsSound() {
        if(BaseApplication.getInstance().rsiStartPlayer != null){
            BaseApplication.getInstance().rsiStartPlayer.play(null);
        }
    }
}
