package com.idba.routerapp.act;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.idba.common.bean.MusicPlayBean;
import com.idba.icore.router.LocalRouter;
import com.idba.musicmodule.act.MusicMainActivity;
import com.idba.routerapp.R;
import com.idba.routerapp.service.AppRouterService;

public class MainActivity extends AppCompatActivity {

    private TextView tvMusicPlayStatus;
    private BroadcastReceiver receive;
    private static final String TAG = "MainActivity";
    private AppRouterService appRouterService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMusicPlayStatus = (TextView) findViewById(R.id.tvMusicPlayStatus);
        //下面的是注解支持
        appRouterService = LocalRouter.getInstance().create(AppRouterService.class);



    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();

        registBroadcast();

    }



    private void registBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("music_action");
        receive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MusicPlayBean music = intent.getParcelableExtra("music");
                if (music != null) {
                    tvMusicPlayStatus.setText(music.toString());
                }
            }
        };
        registerReceiver(receive, filter);
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (receive != null)
            unregisterReceiver(receive);
    }

    public void mainOnClick(View view) {
        switch (view.getId()) {
            case R.id.btnToMusic:
                long start = System.currentTimeMillis();
                startActivity(new Intent(MainActivity.this, MusicMainActivity.class));
                long time = System.currentTimeMillis() - start;
                String text = "原生API -> startActivity();耗时：" + time + "毫秒";
                System.out.println(text);
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

                break;
            case R.id.btnToPic:

                long start0 = System.currentTimeMillis();
                String picModule = appRouterService.startPicModuleActivity(MainActivity.this, "PictureID-10021");
                long time0 = System.currentTimeMillis() - start0;
                String text0 = "路由跳转 —> 其他模块 同一进程耗时：" + time0 + "毫秒";
                System.out.println(text0);
                Toast.makeText(this, text0, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "picModule = " + picModule, Toast.LENGTH_SHORT).show();

                break;
            case R.id.btnToMemory:
//                启动方式1 （存在依赖情况）
//                startActivity(new Intent(MainActivity.this, BigMemoryMainActivity.class));
//                启动方式2（传统路由方式）
//                startToBigMemory();
//                启动方式3（注解方式，推荐使用）

                long start1 = System.currentTimeMillis();

                String memory = appRouterService.startToBigMemory(MainActivity.this, "1024");
                long time1 = System.currentTimeMillis() - start1;
                //返回的String是 RouterActionResult.toString()
                System.out.println("memory = " + memory);
                String text1 = "路由跳转 —> 其他模块的其他进程耗时：" + time1 + "毫秒";
                System.out.println(text1);
                Toast.makeText(this, text1, Toast.LENGTH_SHORT).show();

                break;
            case R.id.btnToStartActForResult:
                long start2 = System.currentTimeMillis();
                String result = appRouterService.startPicModuleActivityForResult(MainActivity.this, "PictureID-10021", "10021");
                long time2 = System.currentTimeMillis() - start2;
                String text2 = "路由跳转 —> 其他模块 相同进程耗时：" + time2 + "毫秒";
                System.out.println(text2);
                Toast.makeText(this, text2, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "result = " + result, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnPlayMusic:

                long start3 = System.currentTimeMillis();
                String play = appRouterService.startMusicModulePlay(MainActivity.this, "1201");
                long time3 = System.currentTimeMillis() - start3;
                String text3 = "路由跳转 —> 其他模块 相同进程 -> 新进程播放音乐耗时：" + time3 + "毫秒";
                System.out.println(text3);
                Toast.makeText(this, text3, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "play = " + play, Toast.LENGTH_SHORT).show();

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (resultCode == RESULT_OK) {
            String stringExtra = data.getStringExtra("PicMainActivity");
            String msg = "onActivityResult: stringExtra = " + stringExtra;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.d(TAG, msg);
        }
    }





}
