package com.idba.bigmemorymodule.act;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.idba.annotation.inject.InjectParams;
import com.idba.bigmemorymodule.R;
import com.idba.bigmemorymodule.service.BigMemoryConnenctService;
import com.idba.bigmemorymodule.service.MemoryRouterService;
import com.idba.common.modulehelper.CallBackHelper;
import com.idba.common.modulehelper.ModuleHelper;
import com.idba.icore.InjectUtils.RouterInjector;
import com.idba.icore.router.LocalRouter;

/**
 * Router
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-16 11:20
 * describe:
 **/
public class BigMemoryMainActivity extends AppCompatActivity {
    private static final String TAG = "BigMemoryMainActivity";
    private Handler handler = new Handler();
    private MemoryRouterService memoryRouterService;

//    获取 memorySize 方式2,注解方式声明注解字段
    @InjectParams
    String memorySize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_memory_main);
        memoryRouterService = LocalRouter.getInstance().create(MemoryRouterService.class);

//        获取 memorySize 方式1：原生api解析
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            String memorySize = extras.getString("memorySize");
//            System.out.println("memorySize = " + memorySize);
//        }

//        获取 memorySize 方式2,注解方式
        RouterInjector.inject(this);
        System.out.println("memorySize = " + memorySize);
        Toast.makeText(this, "memorySize = " + memorySize, Toast.LENGTH_SHORT).show();

    }

    public void menOnClick(View view) {
        int id = view.getId();
        if (id == R.id.btnToPlay) {
            long start1 = System.currentTimeMillis();

            String playMusic = memoryRouterService.startPlayMusic(BigMemoryMainActivity.this);

            long time1 = System.currentTimeMillis() - start1;

            String text1 = "跨进程 -> 不同模块 不同进程 ->启动音乐播放 耗时："+time1+" 毫秒";
            System.out.println(text1);
            Toast.makeText(this, text1, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, playMusic, Toast.LENGTH_SHORT).show();

        } else if (id == R.id.btnToStop) {
            long start2 = System.currentTimeMillis();
            String stopPlayMusic = memoryRouterService.stopPlayMusic(BigMemoryMainActivity.this);


            long time2 = System.currentTimeMillis() - start2;

            String text2 = "跨进程 -> 不同模块 不同进程 ->停止音乐播放 耗时："+time2+" 毫秒";
            System.out.println(text2);
            Toast.makeText(this, text2, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, stopPlayMusic, Toast.LENGTH_SHORT).show();

        } else if (id == R.id.btnToShutDown) {
            shutDown();


        } else if (id == R.id.btnToPic) {
            long start3 = System.currentTimeMillis();

            String toPicMain = memoryRouterService.startToPicMain(BigMemoryMainActivity.this, "10012");

            long time3 = System.currentTimeMillis() - start3;

            String text3 = "跨进程 -> 不同模块 不同进程 ->跳转至图片模块 耗时："+time3+" 毫秒";
            System.out.println(text3);
            Toast.makeText(this, text3, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, toPicMain, Toast.LENGTH_SHORT).show();


        }else if (id == R.id.btnMemoryCallback){
                LocalRouter.getInstance().sendEventCallback(CallBackHelper.CALLBACK_APP, ModuleHelper.Module.MODULE_BIG_MEMORY,
                        ModuleHelper.EventMemory.TEST_CALLBACK,false,"大内存崩溃之后的回调测试",null);
        }
    }

    private void shutDown() {
        //注意，结束进程，请一定调用这个方法，才能彻底的解除底层库的引用
        boolean stopslef = LocalRouter.getInstance().stopSelf(BigMemoryConnenctService.class);
        Log.e(TAG,""+stopslef);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }).start();
    }

//    private void startToPicMain() {
//        final long startTime = System.currentTimeMillis();
//        //memory module call pic module ,set the callback tag is MEMORY_CALLBACK
//        RouterRequest request = RouterRequest.obtain(BigMemoryMainActivity.this).domain(PicHelper.PIC_DOMAIN)
//                .provider(PicHelper.PIC_PROVIDER).action(PicHelper.Action.PIC_ACTION_MAIN)
//                .data("PictureID", "10012");
//        try {
//            final RouterResponse response = LocalRouter.getInstance().route(BigMemoryMainActivity.this, request);
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
//                                Toast.makeText(BigMemoryMainActivity.this, text, Toast.LENGTH_SHORT).show();
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

//    private void play() {
//        final long start = System.currentTimeMillis();
//        //pic module call music module,set callback is pic module
//        RouterRequest request = RouterRequest.obtain(BigMemoryMainActivity.this).domain(MusicHelper.MUSIC_DOMAIN)
//                .provider(MusicHelper.MUSIC_PROVIDER).action(MusicHelper.Action.MUSIC_ACTION_PLAY);
//        try {
//            final RouterResponse response = LocalRouter.getInstance().route(BigMemoryMainActivity.this, request);
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
//                                Toast.makeText(BigMemoryMainActivity.this, text, Toast.LENGTH_SHORT).show();
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
//        RouterRequest request = RouterRequest.obtain(BigMemoryMainActivity.this).domain(MusicHelper.MUSIC_DOMAIN)
//                .provider(MusicHelper.MUSIC_PROVIDER).action(MusicHelper.Action.MUSIC_ACTION_STOP);
//        try {
//            final RouterResponse response = LocalRouter.getInstance().route(BigMemoryMainActivity.this, request);
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
//                                Toast.makeText(BigMemoryMainActivity.this, text, Toast.LENGTH_SHORT).show();
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

}
