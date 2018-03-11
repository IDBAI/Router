package com.idba.musicmodule.act;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.idba.icore.router.LocalRouter;
import com.idba.musicmodule.R;
import com.idba.musicmodule.service.MusicRouterService;


public class MusicMainActivity extends AppCompatActivity {

    private MusicRouterService musicRouterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_main);

        musicRouterService = LocalRouter.getInstance().create(MusicRouterService.class);

    }

    public void musicOnClick(View view) {
        int i = view.getId();
        if (i == R.id.btnToPic) {

            long start = System.currentTimeMillis();
            String activity = musicRouterService.startToPicMainActivity(this, "10012");

            long time1 = System.currentTimeMillis() - start;
            String text1 = "不同模块，同一进程，页面跳转 耗时："+time1 +"毫秒";
            Toast.makeText(this, text1, Toast.LENGTH_SHORT).show();
            System.out.println(text1);


        } else if (i == R.id.btnToBigMemory) {


            long start = System.currentTimeMillis();
            musicRouterService.startToMemoryMainActivity(this,"10245");
            long time1 = System.currentTimeMillis() - start;
            String text1 = "不同模块，不同进程，页面跳转 耗时："+time1 +"毫秒";
            Toast.makeText(this, text1, Toast.LENGTH_SHORT).show();
            System.out.println(text1);


        }

    }

//    private void startToBigMemory() {
//
//        final long startTime = System.currentTimeMillis();
//        RouterRequest routerRequest = RouterRequest.obtain(MusicMainActivity.this).domain(BigMemoryHelper.MEMORY_DOMAIN)
//                .provider(BigMemoryHelper.MEMORY_PROVIDER).action(BigMemoryHelper.Action.MEMORY_ACTION_STARTMAIN)
//                .data("memorySize", "1024");
//
//        try {
//            final RouterResponse response = LocalRouter.getInstance().route(MusicMainActivity.this, routerRequest);
//            boolean async = response.isAsync();
//            System.out.println("async = " + async);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        response.getData();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    final long time = System.currentTimeMillis() - startTime;
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                String text = "async:" + response.isAsync() + " cost:" + time + " response:" + response.get();
//                                System.out.println(text);
//                                Toast.makeText(MusicMainActivity.this, text, Toast.LENGTH_SHORT).show();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            }).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

//    private void startToPicMain() {
//        final long startTime = System.currentTimeMillis();
//        //music module call pic module ,set the callback tag is musiccallback
//        RouterRequest request = RouterRequest.obtain(MusicMainActivity.this).domain(PicHelper.PIC_DOMAIN)
//                .provider(PicHelper.PIC_PROVIDER).action(PicHelper.Action.PIC_ACTION_MAIN)
//                .data("PictureID", "10012");
//        try {
//            final RouterResponse response = LocalRouter.getInstance().route(MusicMainActivity.this, request);
//            boolean async = response.isAsync();
//            System.out.println("async = " + async);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        String data = response.getData();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    final long time = System.currentTimeMillis() - startTime;
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                String text = "async:" + response.isAsync() + " cost:" + time + " response:" + response.get();
//                                System.out.println(text);
//                                Toast.makeText(MusicMainActivity.this, text, Toast.LENGTH_SHORT).show();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//
//
//                }
//            }).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
