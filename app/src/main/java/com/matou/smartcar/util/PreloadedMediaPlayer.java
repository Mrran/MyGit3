package com.matou.smartcar.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.elvishew.xlog.XLog;

/**
 * @author ranfeng
 */
public class PreloadedMediaPlayer {
    private MediaPlayer mediaPlayer;
    private final Context context;
    private final int resId;
    private boolean isPrepared = false;

    private MediaPlayer.OnCompletionListener onCompletionListener;

    public PreloadedMediaPlayer(Context context, int resId) {
        this.context = context;
        this.resId = resId;
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(context, resId);
        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(mp -> isPrepared = true);
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.seekTo(0);
                isPrepared = true;
                if(onCompletionListener != null){
                    onCompletionListener.onCompletion(mp);
                }
            });
        } else {
            XLog.w("Failed to create MediaPlayer");
        }
    }

    public void play(MediaPlayer.OnCompletionListener onCompletionListener) {
        if (mediaPlayer != null && isPrepared) {
            this.onCompletionListener = onCompletionListener;
            mediaPlayer.start();
        }
    }

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
