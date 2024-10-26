package com.matou.smartcar.speaker;

import android.os.Bundle;

import com.elvishew.xlog.XLog;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.matou.smartcar.base.BaseApplication;

/**
 * @author ranfeng
 */
public class SpeakerHandler {
    private static SpeakerHandler instance;
    private final SpeechSynthesizer tts;

    public static SpeakerHandler getInstance() {
        if (instance == null) {
            synchronized (SpeakerHandler.class) {
                if (instance == null) {
                    instance = new SpeakerHandler();
                }
            }
        }
        return instance;
    }

    /**
     * TTS播报优先级
     */
    public final static int INIT = 0;
    public final static int National = 1;
    public final static int ILL_PARKING = 2;
    public final static int TRAFFIC_CONTROL = 4;
    public final static int TIRE_PRESS = 6;
    public final static int SCENE = 8;
    public final static int SPEEDING = 10;

    /**
     * 当前播放等级
     */
    private int currLevel;

    public SpeakerHandler() {
        tts = SpeechSynthesizer.createSynthesizer(BaseApplication.getInstance(), code -> XLog.e("InitListener init() code = " + code));
        setParam();
    }


    public void speak(String text, int level) {
        //XLog.w("tts before speak text = " + text + ", force = " + force);
        if (level > currLevel) {
            tts.stopSpeaking();
        }

        if (tts.isSpeaking()) {
            XLog.w("tts is speaking!!!");
            return;
        }

        //XLog.w("tts after speak text = " + text + ", force = " + force);

        tts.startSpeaking(text, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {
                //XLog.w("tts onSpeakBegin");
            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {
                currLevel = INIT;
            }

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {
                currLevel = INIT;
                if (progressListener != null) {
                    progressListener.complete();
                }
            }

            @Override
            public void onEvent(int arg1, int arg2, int arg3, Bundle bundle) {
                // XLog.w("onEvent arg1=%d, arg2=%d, arg3=%d", arg1, arg2, arg3);
            }
        });
    }

    public void stopSpeak() {
        if (tts != null) {
            tts.stopSpeaking();
        }
    }

    /**
     * 引擎类型
     */
    private final String mEngineType = SpeechConstant.TYPE_LOCAL;


    /**
     * 设置相关参数
     */
    private void setParam() {
        // 清空参数
        tts.setParameter(SpeechConstant.PARAMS, null);

        tts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_XTTS);

        //设置发音人资源路径
        tts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());

        //设置发音人
        tts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");

        //设置合成语速
        tts.setParameter(SpeechConstant.SPEED, "55");

        //设置合成音调
        tts.setParameter(SpeechConstant.PITCH, "50");

        //设置合成音量
        tts.setParameter(SpeechConstant.VOLUME, "100");

        //设置播放器音频流类型
        tts.setParameter(SpeechConstant.STREAM_TYPE, "3");

        // 设置播放合成音频打断音乐播放，默认为true
        tts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
    }


    /**
     * 获取发音人资源路径
     *
     * @return
     */
    private String getResourcePath() {
        StringBuilder tempBuffer = new StringBuilder();
        String type = "tts";
        if (mEngineType.equals(SpeechConstant.TYPE_XTTS)) {
            type = "xtts";
        }
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(BaseApplication.getInstance(), ResourceUtil.RESOURCE_TYPE.assets, type + "/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(BaseApplication.getInstance(), ResourceUtil.RESOURCE_TYPE.assets, type + "/" + "xiaoyan.jet"));

        return tempBuffer.toString();
    }

    public void destroy() {
        if (tts != null) {
            tts.stopSpeaking();
            // 退出时释放连接
            tts.destroy();
        }

    }

    private ProgressListener progressListener;

    public SpeakerHandler setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public interface ProgressListener {
        void complete();

        void progress(int progress);
    }
}
