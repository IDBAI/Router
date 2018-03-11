package com.idba.musicmodule;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;


import com.idba.common.bean.MusicPlayBean;
import com.idba.common.modulehelper.CallBackHelper;
import com.idba.common.modulehelper.ModuleHelper;
import com.idba.icore.router.LocalRouter;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Router
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-16 11:20
 * describe:
 **/
public class Music implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    MediaPlayer mediaPlayer;
    private static final String TAG = "Music";
    private Timer timer;
    private TimerTask timerTask;
    private MusicPlayBean playBean;
    private String[] musiclistStr = {"music/mylove.mp3", "music/nuowei.mp3", "music/themass.mp3"};
    private Context context;
    private int current = 0;

    public Music(Context context) {
        this.context = context;
    }

    private void initMp() {
        current = current == 0 ? 1 : (current == 1 ? 2 : 0);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
//        mediaPlayer = MediaPlayer.create(context, musiclist[current]);

        AssetManager assetManager = context.getAssets();
        try {
            mediaPlayer = new MediaPlayer();
            AssetFileDescriptor fileDescriptor = assetManager.openFd(musiclistStr[current]);
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (current == 0)
            playBean = new MusicPlayBean().setArtist("西域男孩").setAuthor(100).setName("My Love").setDuration(3 * 60).setSpeed(0);
        else if (current == 1)
            playBean = new MusicPlayBean().setArtist("伍佰").setAuthor(100).setName("挪威的森林").setDuration(3 * 60).setSpeed(0);
        else
            playBean = new MusicPlayBean().setArtist("Era").setAuthor(100).setName("The Mass").setDuration(3 * 60).setSpeed(0);
    }

    public void play() {

        stopTimer();
        initMp();
        try {
            if (mediaPlayer != null) {
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnCompletionListener(this);
            } else
                Log.e(TAG, "play: mediaPlayer is null!!!!!!!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            startTimer();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "音乐播放完成！");
        stopTimer();
        play();
    }

    private void stopTimer() {
        if (timer != null)
            timer.cancel();
        timer = null;
        if (timerTask != null)
            timerTask.cancel();
        timerTask = null;
    }

    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                playTimerInCrease();

            }
        };
        timer.schedule(timerTask, new Date(), 1000);
    }


    /**
     * send msg to APP module
     */
    private void playTimerInCrease() {
        Bundle bundle = new Bundle();

        int speed = playBean.getSpeed();
        speed++;
        playBean.setSpeed(speed);
        bundle.putParcelable("iObject", playBean);
        bundle.setClassLoader(MusicPlayBean.class.getClassLoader());
        LocalRouter.getInstance().sendEventCallback(CallBackHelper.CALLBACK_APP, ModuleHelper.Module.MODULE_MUSIC, ModuleHelper.EventMusic.MUSIC_PLAYING, true, "音乐播放中...", bundle);
    }


    public void stop() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                stopTimer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}