package com.matou.smartcar.base;

import android.text.TextUtils;

import androidx.multidex.MultiDexApplication;

import com.amap.api.maps.MapsInitializer;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.PrinterSet;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy;
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.matou.smartcar.BuildConfig;
import com.matou.smartcar.R;
import com.matou.smartcar.net.Pc5MQTTClient;
import com.matou.smartcar.net.UDPSocket;
import com.matou.smartcar.speaker.SpeakerHandler;
import com.matou.smartcar.util.PreloadedMediaPlayer;
import com.matou.smartcar.util.SceneMediaPlayerManager;

/**
 * @author ranfeng
 */
public class BaseApplication extends MultiDexApplication {
    private static BaseApplication instance;
    public PreloadedMediaPlayer greenStartPlayer;
    public PreloadedMediaPlayer rsiStartPlayer;
    public SceneMediaPlayerManager sceneMediaPlayerManager;

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        Fresco.initialize(this);
        PrinterSet.logFlag = false;

        initAMap();

        initXLog();

        XLog.w("BaseApplication onCreate");

        initMQTT();

        initTTS();

        initMediaPlayer();
    }

    private void initMediaPlayer() {
        greenStartPlayer = new PreloadedMediaPlayer(this, R.raw.start_green_tips);
        rsiStartPlayer = new PreloadedMediaPlayer(this, R.raw.rsi_tips);
        sceneMediaPlayerManager = new SceneMediaPlayerManager(this);
    }

    private void initAMap(){
        MapsInitializer.updatePrivacyShow(this, true, true);
        MapsInitializer.updatePrivacyAgree(this, true);
    }

    private void initMQTT() {
        if (TextUtils.equals(BuildConfig.APP_TYPE, "common")) {
            Pc5MQTTClient.getInstance(this).start();
        } else {
            UDPSocket.getInstance(this).startUDPSocket();
        }
    }

    private void initTTS() {
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=a59026ab");
        SpeakerHandler.getInstance();
    }

    private void initXLog() {
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.WARN)
                .tag("xlog")
                .enableThreadInfo()
                //.enableStackTrace(1)
                //.enableBorder()
                .build();

        Printer androidPrinter = new AndroidPrinter(true);
        Printer filePrinter = new FilePrinter.Builder(getExternalFilesDir("Smart_Car_Mirror").getPath())
                .fileNameGenerator(new DateFileNameGenerator())
                .backupStrategy(new NeverBackupStrategy())
                .cleanStrategy(new FileLastModifiedCleanStrategy(24*60*60*1000))
                .build();

        XLog.init(config, androidPrinter);
    }


}
