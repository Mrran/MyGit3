package com.matou.smartcar.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.elvishew.xlog.XLog;
import com.matou.smartcar.MainActivity;
import com.matou.smartcar.base.BaseApplication;
import com.matou.smartcar.bean.ObuInfo;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.global.OptiHandler;

import java.util.List;

public class WifiConnector {
    WifiManager wifiManager;

    private String connectingSSID = "";
    private Thread thread;

    private final WifiStateReceiver wifiStateReceiver = new WifiStateReceiver();

    private int tryConnCount;

    public void registerReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        BaseApplication.getInstance().registerReceiver(wifiStateReceiver, filter);
    }

    public void unRegisterReceiver(){
        BaseApplication.getInstance().unregisterReceiver(wifiStateReceiver);
    }
    
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    public WifiConnector(Activity activity) {
        this.wifiManager = (WifiManager) BaseApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
        OptiHandler checkHandler = new OptiHandler(activity);

        Runnable checkRnb = new Runnable() {
            @Override
            public void run() {
                // 尝试连接2次，若连接不上就取消
                if(tryConnCount < 2){
                    tryConnCount++;
                    checkHandler.postDelayed(this, 10000);
                }else {
                    XLog.w("obuwifi tryConnCount = " + tryConnCount);
                    return;
                }

                if(isConnectObuWifi()){
                    XLog.w("obuwifi return isConnectObuWifi");
                    return;
                }

                if(!TextUtils.isEmpty(connectingSSID)){
                    XLog.w("obuwifi return connecting :" + connectingSSID);
                    return;
                }

                WifiManager wifiManager = (WifiManager) BaseApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
                List<ScanResult> scanList = wifiManager.getScanResults();
                ObuInfo connObuInfo = null;
                if (scanList != null && scanList.size() > 0) {
                    for(int i=0; i<scanList.size(); i++){
                        ScanResult scanResult = scanList.get(i);
                        List<ObuInfo> obuInfoList = GlobalVariables.OBU_INFO_LIST;
                        for (int j=0; j<obuInfoList.size(); j++){
                            ObuInfo obuInfo = obuInfoList.get(j);
                            if(scanResult.SSID.contains(obuInfo.getSsidBegin())){
                                obuInfo.setSsid(scanResult.SSID);
                                connObuInfo = obuInfo;
                                break;
                            }
                        }
                        if(connObuInfo != null){
                            break;
                        }
                    }
                }

                if(connObuInfo != null){
                    XLog.w("obuwifi connObuInfo = " + connObuInfo);
                    connect(connObuInfo.getSsid(), connObuInfo.getSsidPwd(), WifiCipherType.WIFICIPHER_WPA);
                }else {
                    XLog.w("obuwifi has no connObuInfo to connect");
                }
            }
        };
        checkHandler.postDelayed(checkRnb, 5000);
    }

    public synchronized void connect(String ssid, String password, WifiCipherType type) {
        if(ssid.equals(connectingSSID)){
            XLog.w("obuwifi return is connecting ssid = " + ssid);
            return;
        }
        XLog.w("obuwifi set connectingSSID = " + ssid);
        connectingSSID = ssid;
        thread = new Thread(new ConnectRunnable(ssid, password, type));
        thread.start();
    }

    // 查看以前是否也配置过这个网络
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }


    public boolean isConnectObuWifi() {
        boolean isConnect = false;
        ConnectivityManager cm = (ConnectivityManager) BaseApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.getState() == NetworkInfo.State.CONNECTED && info.getType() == ConnectivityManager.TYPE_WIFI) {
            // wifi已连接的情况下不再连接指定wifi，兼容obu可能不能连接公网的情况
            isConnect = true;
//            WifiManager wm = (WifiManager) BaseApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
//            WifiInfo wifiInfo = wm.getConnectionInfo();
//            String ssid = wifiInfo.getSSID();
//            List<ObuInfo> obuInfoList = GlobalVariables.OBU_INFO_LIST;
//            for(int i=0; i<obuInfoList.size(); i++){
//                ObuInfo obuInfo = obuInfoList.get(i);
//                if(!TextUtils.isEmpty(ssid) && ssid.contains(obuInfo.getSsidBegin())){
//                    isConnect = true;
//                    break;
//                }
//            }
        }
        XLog.w("obuwifi isConnectObuWifi = " + isConnect);
        return isConnect;
    }


    private WifiConfiguration createWifiInfo(String SSID, String Password,
                                             WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // nopass
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        // wep
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // wpa
        if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    // 打开wifi功能
    public boolean openWifi() {
        boolean bRet = true;
        if (!wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
        }
        XLog.w("obuwifi openWifi");
        return bRet;
    }

    class ConnectRunnable implements Runnable {
        private String ssid;

        private String password;

        private WifiCipherType type;

        public ConnectRunnable(String ssid, String password, WifiCipherType type) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                // 打开wifi
                openWifi();
                Thread.sleep(200);
                // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
                // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
                while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                    try {
                        // 为了避免程序一直while循环，让它睡个100毫秒检测……
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                WifiConfiguration wifiConfig = createWifiInfo(ssid, password,
                        type);
                //
                if (wifiConfig == null) {
                    XLog.e("wifiConfig is null!");
                    return;
                }

                WifiConfiguration tempConfig = isExsits(ssid);

                if (tempConfig != null) {
                    wifiManager.removeNetwork(tempConfig.networkId);
                }

                int netID = wifiManager.addNetwork(wifiConfig);
                boolean enabled = wifiManager.enableNetwork(netID, true);
                boolean connected = wifiManager.reconnect();
                XLog.w("obuwifi enableNetwork connected=" + connected + ", status enable=" + enabled);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //setConnecting(false);
        }
    }

    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }

        return true;
    }


    private class WifiStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)){

            }else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)){
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                XLog.w("obuwifi info = " + info);

                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    String ssid = info.getExtraInfo();
                    XLog.w("obuwifi set connectingSSID = empty");
                    connectingSSID = "";
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    String ssid = info.getExtraInfo();
                    XLog.w("obuwifi set connectingSSID = empty");
                    connectingSSID = "";
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                }
            }else {

            }



        }
    }
    
}