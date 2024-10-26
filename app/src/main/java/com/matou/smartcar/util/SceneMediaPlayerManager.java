package com.matou.smartcar.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.matou.smartcar.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SceneMediaPlayerManager {
    private final List<MediaPlayer> mediaPlayers = new ArrayList<>();

    public final static int avwb_parking_car = 0;
    public final static int avwb_slow_car = 1;
    public final static int avwb_warning_lights = 2;
    public final static int clc_left = 3;
    public final static int clc_right = 4;
    public final static int clw = 5;
    public final static int cvw_go_first = 6;
    public final static int cvw_go_second = 7;
    public final static int evw_ambulance = 8;
    public final static int evw_fire_truck = 9;
    public final static int evw_police = 10;
    public final static int fcs = 11;
    public final static int fcw_car = 12;
    public final static int fcw_person = 13;
    public final static int fcwb = 14;
    public final static int icw_left = 15;
    public final static int icw_right = 16;
    public final static int lcw_left = 17;
    public final static int lcw_right = 18;
    public final static int lta_dnpw = 19;
    public final static int pcr_ypc = 20;
    public final static int rlvw = 21;

    private final static Integer[] playList = {
            R.raw.avwb_parking_car,
            R.raw.avwb_slow_car,
            R.raw.avwb_warning_lights,
            R.raw.clc_left,
            R.raw.clc_right,
            R.raw.clw,
            R.raw.cvw_go_first,
            R.raw.cvw_go_second,
            R.raw.evw_ambulance,
            R.raw.evw_fire_truck,
            R.raw.evw_police,
            R.raw.fcs,
            R.raw.fcw_car,
            R.raw.fcw_person,
            R.raw.fcwb,
            R.raw.icw_left,
            R.raw.icw_right,
            R.raw.lcw_left,
            R.raw.lcw_right,
            R.raw.lta_dnpw,
            R.raw.pcr_ypc,
            R.raw.rlvw,
    };

    public SceneMediaPlayerManager(Context context) {
        for (int uri : playList) {
            try {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(context, Uri.parse("android.resource://" + context.getPackageName() + "/" + uri));
                mediaPlayer.prepareAsync();
                mediaPlayers.add(mediaPlayer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MediaPlayer getMediaPlayer(int index) {
        if (index >= 0 && index < mediaPlayers.size()) {
            return mediaPlayers.get(index);
        } else {
            throw new IndexOutOfBoundsException("Invalid index for media player list");
        }
    }

    public void releaseMediaPlayers() {
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        }
        mediaPlayers.clear();
    }

    public void play(int index) {
        getMediaPlayer(index).start();
    }
}
