package com.idba.musicmodule;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Router
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-16 11:20
 * describe:
 **/
public class MusicService extends Service {
    public Music music;


    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        music = new Music(this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(intent !=null){
            String command = intent.getStringExtra("command");
            String musicId = intent.getStringExtra("musicId");
            Toast.makeText(this, "musicId = "+musicId, Toast.LENGTH_SHORT).show();

            if("play".equals(command)){
                music.play();
            }else if("stop".equals(command)){
                music.stop();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("MusicService","onDestroy");
    }
}