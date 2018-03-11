package com.idba.picmodule.act;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.idba.annotation.inject.InjectParams;
import com.idba.icore.InjectUtils.RouterInjector;
import com.idba.icore.RouterApplication;
import com.idba.icore.router.LocalRouter;
import com.idba.icore.router.RouterRequest;
import com.idba.icore.router.RouterResponse;
import com.idba.picmodule.R;
import com.idba.picmodule.service.PicRouterService;

public class PicMainActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private TextView tvMusicInfoShow;
    private PicRouterService picRouterService;

    @InjectParams
    String PictureID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_main);
        picRouterService = LocalRouter.getInstance().create(PicRouterService.class);

        tvMusicInfoShow = (TextView) findViewById(R.id.tvMusicInfoShow);

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            String pictureID = extras.getString("PictureID");
//            tvMusicInfoShow.setText("接收到的PictureID = " + pictureID + "\n");
//
//        }

        RouterInjector.inject(this);
        tvMusicInfoShow.setText("接收到的PictureID = " + PictureID + "\n");

    }

    public void picOnClick(View view) {
        int id = view.getId();
        if (id == R.id.btnToPlay) {

            long start = System.currentTimeMillis();
            picRouterService.startPlayMusic(this);
            long time1 = System.currentTimeMillis() - start;
            String text1 = "不同模块，同一进程，Aciton控制 耗时："+time1 +"毫秒";
            Toast.makeText(this, text1, Toast.LENGTH_SHORT).show();
            System.out.println(text1);

        } else if (id == R.id.btnToStop) {

            long start = System.currentTimeMillis();
            picRouterService.stopPlayMusic(this);
            long time1 = System.currentTimeMillis() - start;
            String text1 = "不同模块，同一进程，Aciton控制 耗时："+time1 +"毫秒";
            Toast.makeText(this, text1, Toast.LENGTH_SHORT).show();
            System.out.println(text1);
        }
    }

//    private void play() {
//        final long start = System.currentTimeMillis();
//        //pic module call music module,set callback is pic module
//        RouterRequest request = RouterRequest.obtain(PicMainActivity.this).domain(MusicHelper.MUSIC_DOMAIN)
//                .provider(MusicHelper.MUSIC_PROVIDER).action(MusicHelper.Action.MUSIC_ACTION_PLAY);
//        try {
//            final RouterResponse response = LocalRouter.getInstance().route(PicMainActivity.this, request);
//            boolean async = response.isAsync();
//            System.out.println("async = " + async);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    try {
//                        String data = response.getData();
//                        System.out.println("data = " + data);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    final long time = System.currentTimeMillis() - start;
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                String text = "async:" + response.isAsync() + " cost:" + time + " response:" + response.get();
//                                System.out.println(text);
//                                Toast.makeText(PicMainActivity.this, text, Toast.LENGTH_SHORT).show();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//
//                }
//            }).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private void stop() {
//        final long start = System.currentTimeMillis();
//        RouterRequest request = RouterRequest.obtain(PicMainActivity.this).domain(MusicHelper.MUSIC_DOMAIN)
//                .provider(MusicHelper.MUSIC_PROVIDER).action(MusicHelper.Action.MUSIC_ACTION_STOP);
//        try {
//            final RouterResponse response = LocalRouter.getInstance().route(PicMainActivity.this, request);
//            boolean async = response.isAsync();
//            System.out.println("async = " + async);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        String data = response.getData();
//                        System.out.println("data = " + data);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    final long time = System.currentTimeMillis() - start;
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                String text = "async:" + response.isAsync() + " cost:" + time + " response:" + response.get();
//                                System.out.println(text);
//                                Toast.makeText(PicMainActivity.this, text, Toast.LENGTH_SHORT).show();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//
//                }
//            }).start();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("PicMainActivity","PicMainActivity");
        this.setResult(RESULT_OK,intent);
        super.onBackPressed();//必須在后面
    }
}
