package com.matou.smartcar.net;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.elvishew.xlog.XLog;
import com.matou.smartcar.bean.UpdateStatus;
import com.matou.smartcar.event.UpgradeEvent;
import com.matou.smartcar.global.Config;
import com.matou.smartcar.util.AppUtils;
import com.matou.smartcar.util.CommUtils;
import com.matou.smartcar.util.GsonUtil;
import com.matou.smartcar.util.SPUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;


public class AppUpdateTask extends AsyncTask<String, Integer, Integer> {

    /**
     * 是否正在升级标志
     */
    public static boolean upgrading;
    private final String upgradeCode;
    private final Context context;
    private final String version;

    private long totalLength;

    private Exception downLoadExcep;

    public AppUpdateTask(Context context, int totalLength, String version, String upgradeCode) {
        this.context = context;
        this.totalLength = totalLength;
        this.version = version;
        this.upgradeCode = upgradeCode;
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        HttpURLConnection connection = null;
        RandomAccessFile raf = null;

        try {

            File folder = new File(Config.BASE_PATH);
            folder.mkdirs();
            clearOtherFile(folder, version);

            File file = new File(Config.BASE_PATH, Config.APK_NAME + version + Config.APK_POSTFIX);
            long downloadedBytes = file.length();
            XLog.w("====> downloadedBytes = " + downloadedBytes);
            // 已下载数据包大小超过了整包大小，则删除此包
            if(downloadedBytes >= totalLength){
                boolean delete = file.delete();
                XLog.w("====> downloadedBytes >= totalLength, delete =  " + delete);
                downloadedBytes = 0;
            }

            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Range", "bytes=" + downloadedBytes + "-");

            is = connection.getInputStream();
            raf = new RandomAccessFile(file, "rw");
            raf.seek(downloadedBytes);

            byte[] data = new byte[1024];
            int len;
            int lastProgress = 0;
            while ((len = is.read(data)) != -1) {
                raf.write(data, 0, len);
                downloadedBytes += len;
                int progress = (int) (downloadedBytes * 100f / totalLength);
                if(progress > lastProgress){
                    EventBus.getDefault().post(new UpgradeEvent(1, progress));
                    lastProgress = progress;
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();

            int error = -3;
            if (CommUtils.isNetworkError(e)) {
                error = -2;
            }
            downLoadExcep = e;
            return error;
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
                if (is != null) {
                    is.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清理非本版本的升级包
     *
     * @param folder
     * @param version
     */
    private void clearOtherFile(File folder, String version) {
        if(folder == null){
            XLog.e("folder == null, return");
            return;
        }
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.getName().contains(version)) {
                        boolean delete = file.delete();
                        XLog.w(file.getName() + " delete result = " + delete);
                    }
                } else if (file.isDirectory()) {
                    boolean delete = file.delete();
                    XLog.w(file.getName() + " delete result = " + delete);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Integer i) {
        super.onPostExecute(i);
        if (i == 0) {
            File file = new File(Config.BASE_PATH + Config.APK_NAME + version + Config.APK_POSTFIX);
            String md5 = getMd5FromFile(file);
            String serverMd5 = (String) SPUtil.get(SPUtil.MD5_KEY, "");
            if (TextUtils.equals(serverMd5, md5)) {
                XLog.w("检验成功开始安装:" + md5);
                EventBus.getDefault().post(new UpgradeEvent(0, 0));
                install(context, Config.BASE_PATH + Config.APK_NAME + version + Config.APK_POSTFIX);
            } else {
                XLog.w("md5校验失败，删除下载包");
                EventBus.getDefault().post(new UpgradeEvent(-3, 0));
                try {
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();

                }
                AppUtils.uploadUpdateStatus(upgradeCode, 0,"升级包校验失败, serverMd5 = " + serverMd5 + ", " + "localmd5 = " + md5);
            }
        } else if (i == -1) {
            XLog.w("下载取消");
            EventBus.getDefault().post(new UpgradeEvent(-1, 0));
            AppUtils.uploadUpdateStatus(upgradeCode, 0,"用户取消升级");
        } else {
            XLog.w("下载失败");
            EventBus.getDefault().post(new UpgradeEvent(-3, 0));
            saveFailReason(downLoadExcep);
            AppUtils.uploadUpdateStatus(upgradeCode, 0,"升级包下载失败: " + downLoadExcep);
        }
        upgrading = false;
    }

    /**
     * 如果是网络中断导致的升级失败，则缓存原因，用于下次网络正常时上传
     * @param downLoadExcep
     */
    private void saveFailReason(Exception downLoadExcep) {
        String updateStatusStr = (String) SPUtil.get("UpdateStatus", "");
        if (TextUtils.isEmpty(updateStatusStr)) {
            XLog.e("saveFailReason updateStatusStr is empty, return");
            return;
        }
        UpdateStatus updateStatus = GsonUtil.gsonToBean(updateStatusStr, UpdateStatus.class);
        if(updateStatus != null){
            updateStatus.setFailReason(AppUtils.getDateFormat() + "--->" + downLoadExcep.toString());
            SPUtil.set("UpdateStatus", GsonUtil.beanToJson(updateStatus));
        }
    }

    //车机提供的静默安装
    private void install(Context mContext, String apkFilePath) {
        Intent intent = new Intent();
        intent.setAction("com.zqc.package.silentinstaller");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.setComponent(new ComponentName("com.android.packageinstaller",
                "com.android.packageinstaller.SilentInstallReceiver"));
        intent.putExtra("package_name", "com.matou.smartcar");
        intent.putExtra("file_path", apkFilePath);
        XLog.e("开始安装...");
        Toast.makeText(mContext, "正在升级V2X云镜APP，升级完成后请手动重启", Toast.LENGTH_LONG).show();
        mContext.sendBroadcast(intent);
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    public static String getMd5FromFile(File file) {
        if (file == null) {
            return "";
        }
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
                    file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }
}
