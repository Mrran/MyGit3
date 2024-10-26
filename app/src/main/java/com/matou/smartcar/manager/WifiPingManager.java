package com.matou.smartcar.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;

import com.elvishew.xlog.XLog;
import com.matou.smartcar.global.GlobalVariables;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 针对金溢obu的wifi进行判断
 * @author ranfeng
 */
public class WifiPingManager {

    private static final String PING_IP = "192.168.110.1";
    private static final String SUBNET_ADDRESS = "192.168.110.0";
    private static final String SUBNET_MASK = "255.255.255.0";

    private static final int PING_INTERVAL_MS = 20 * 1000;
    private static final int WIFI_RECONNECT_DELAY_MS = 5000;
    private static final int MAX_FAILURE_COUNT = 3;
    private static final int MAX_WIFI_FAILURE_ATTEMPTS = 3;
    public static final int INITIAL_DELAY = 3 * 60 * 1000;

    private static WifiPingManager instance;
    private final Context context;
    private final WifiManager wifiManager;
    private final Handler handler;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> pingTask;
    private String lastConnectedSSID = null;
    private boolean isPinging = false;
    private int failureCount = 0;
    private int wifiFailureAttemptCount = 0; // 新增 handleWifiFailure 计数器

    private WifiPingManager(Context context) {
        this.context = context.getApplicationContext();
        this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        this.handler = new Handler();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public static synchronized WifiPingManager getInstance(Context context) {
        if (instance == null) {
            instance = new WifiPingManager(context);
        }
        return instance;
    }

    public void startPinging() {
        if (isPinging) {
            return;
        }
        isPinging = true;
        wifiFailureAttemptCount = 0; // 启动时复位计数器
        schedulePingTask();
    }

    public void stopPinging() {
        if (!isPinging) {
            return;
        }
        isPinging = false;
        if (pingTask != null) {
            pingTask.cancel(true);
        }
    }

    private void schedulePingTask() {
        pingTask = scheduler.scheduleAtFixedRate(() -> {

            if(!GlobalVariables.remWifi){
                XLog.w("Ping has close remWifi");
                return;
            }

            // 轮询开启wifi
            if(!wifiManager.isWifiEnabled()){
                wifiManager.setWifiEnabled(true);
            }

            String currIp = getCurrentIPAddress();
            boolean ipInSubnet = isIpInSubnet(currIp);
            XLog.w("Ping currIp = " + currIp + ", ipInSubnet = " + ipInSubnet);
            if(ipInSubnet){
                boolean canPing = canPingIPAddress(PING_IP);
                if (!canPing) {
                    failureCount++;
                    XLog.w("Ping failed. Failure count: " + failureCount);
                    GlobalVariables.pingStatus.status = PingStatus.FAILED;
                    GlobalVariables.pingStatus.failCount = failureCount;
                    if (failureCount >= MAX_FAILURE_COUNT) {
                        XLog.w("Ping Max failure count reached. Restarting WiFi...");
                        handler.post(this::handleWifiFailure);
                        failureCount = 0;
                    }
                } else {
                    XLog.w("Ping succeeded. Waiting for next ping...");
                    failureCount = 0;
                    GlobalVariables.pingStatus.status = PingStatus.SUCCESS;
                    GlobalVariables.pingStatus.failCount = 0;
                }
            }else {
                GlobalVariables.pingStatus.status = PingStatus.NOT_JY_WIFI;
                GlobalVariables.pingStatus.failCount = 0;
            }
            EventBus.getDefault().post(GlobalVariables.pingStatus);

        }, INITIAL_DELAY, PING_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private boolean canPingIPAddress(String ipAddress) {
        try {
            Process process = Runtime.getRuntime().exec("ping -c 1 " + ipAddress);
            int returnVal = process.waitFor();
            return (returnVal == 0);
        } catch (IOException | InterruptedException e) {
            XLog.e("Ping failed", e);
            return false;
        }
    }

    private void handleWifiFailure() {
        if (wifiFailureAttemptCount >= MAX_WIFI_FAILURE_ATTEMPTS) {
            XLog.w("Ping Exceeded maximum WiFi failure attempts. Stopping pings.");
            stopPinging();
            return;
        }

        if (wifiManager.isWifiEnabled()) {
            lastConnectedSSID = getCurrentSSID();
            wifiManager.setWifiEnabled(false);
            handler.postDelayed(() -> {
                wifiManager.setWifiEnabled(true);
                if (!TextUtils.isEmpty(lastConnectedSSID)) {
                    connectToLastSSID(lastConnectedSSID);
                }
            }, WIFI_RECONNECT_DELAY_MS);
            wifiFailureAttemptCount++;
            failureCount = 0;
        }
    }

    private String getCurrentSSID() {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && networkInfo.isConnected()) {
            String ssid = wifiManager.getConnectionInfo().getSSID();
            return ssid.replace("\"", "");
        }
        return null;
    }

    private void connectToLastSSID(String ssid) {
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configuredNetworks) {
            if (config.SSID != null && config.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(config.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }

    /**
     * 获取当前ip地址
     * @return
     */
    @SuppressLint("DefaultLocale")
    public String getCurrentIPAddress() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }

    public boolean isIpInSubnet(String ip) {
        try {
            // 获取 IP 地址和子网段的网络地址
            int ipAddress = inetAddressToInt(InetAddress.getByName(ip));
            int subnetAddress = inetAddressToInt(InetAddress.getByName(SUBNET_ADDRESS));
            int subnetMask = inetAddressToInt(InetAddress.getByName(SUBNET_MASK));

            // 使用子网掩码来检测 IP 是否在子网段内
            return (ipAddress & subnetMask) == (subnetAddress & subnetMask);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int inetAddressToInt(InetAddress inetAddress) {
        byte[] addressBytes = inetAddress.getAddress();
        int address = 0;
        for (int i = 0; i < 4; i++) {
            address |= ((addressBytes[i] & 0xFF) << (24 - (i * 8)));
        }
        return address;
    }

    public static class PingStatus {

        public final static int INIT = 0;
        public final static int NOT_JY_WIFI = 1;
        public final static int SUCCESS = 2;
        public final static int FAILED = 3;

        /**
         * ping的结果
         * 0: 初始状态
         * 1: 当前未连接obuwifi
         * 2: ping成功
         * 3: ping失败
         */
        public int status;

        /**
         * 失败次数
         */
        public int failCount;
    }

}
