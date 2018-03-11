package com.idba.routerapp.callback;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.idba.common.bean.MemoryBean;
import com.idba.common.bean.MusicPlayBean;
import com.idba.common.modulehelper.ModuleHelper;
import com.idba.icore.RouterApplication;
import com.idba.icore.callback.RouterCallbackMessageCenter;

import java.util.ArrayList;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-09 14:10
 * describe: APP模块的跨模块消息回调中心
 **/
public class AppCallbackMessageCenter extends RouterCallbackMessageCenter {
    private static final String TAG = "AppCallbackMessageCente";
    private RouterApplication mApplication;

    public AppCallbackMessageCenter(RouterApplication mApplication) {
        this.mApplication = mApplication;
    }


    @Override
    public void callback(String fromModule, String event, boolean boo, String msg, Bundle bundle) throws RemoteException {
        disPatchModuleEvent(fromModule, event, boo, msg, bundle);
    }

    /**
     * 分发不同模块传递的事件消息
     *  @param module
     * @param event
     * @param b
     * @param msg
     * @param bundle
     */
    private void disPatchModuleEvent(String module, String event, boolean b, String msg, Bundle bundle) {
        switch (module) {
            case ModuleHelper.Module.MODULE_APP:
                break;
            case ModuleHelper.Module.MODULE_BIG_MEMORY://分发Memory模块事件
                distPatchMemoryModule(event,b,msg, bundle);
                break;
            case ModuleHelper.Module.MODULE_MUSIC://分发音乐模块事件
                dispatchMusicModuleEvent(event,b,msg, bundle);
                break;
            case ModuleHelper.Module.MODULE_PIC://分发图片模块事件
                dispatchPicModule(event,b,msg, bundle);
                break;
        }
    }

    private void distPatchMemoryModule(String event, boolean b, String msg, Bundle bundle) {
        Log.d(TAG, "dispatchMusicModuleEvent() called with: event = [" + event + "], b = [" + b + "], msg = [" + msg + "], bundle = [" + bundle + "]");

        switch (event) {
            case ModuleHelper.EventMemory.PARSE_LIST:
                if (bundle != null) {
                    bundle.setClassLoader(MusicPlayBean.class.getClassLoader());
                    ArrayList<MusicPlayBean> list = bundle.getParcelableArrayList("list");
                    if (list != null && !list.isEmpty()) {
                        MusicPlayBean musicPlayBean = list.get(0);
                        Log.e(TAG, "获取到列表，大小为：" + list.size());
                        System.out.println(musicPlayBean.toString());
                    }
                }
                break;
            case ModuleHelper.EventMemory.PARSE_MULITBEAN:
                bundle.setClassLoader(MusicPlayBean.class.getClassLoader());
                MusicPlayBean playBean = bundle.getParcelable("playBean");
                if (playBean != null)
                    Log.e(TAG, "disPatchModuleEvent: 解析到 playBean = " + playBean.toString());

                bundle.setClassLoader(MemoryBean.class.getClassLoader());
                MemoryBean memoryBean = bundle.getParcelable("memoryBean");
                if (memoryBean != null)
                    Log.e(TAG, "disPatchModuleEvent: 解析到 memoryBean = " + memoryBean.toString());

                break;
        }
    }

    private void dispatchPicModule(String event, boolean b, String msg, Bundle bundle) {
        switch (event) {
            case ModuleHelper.EventPic.PARSE_LIST:
                if (bundle != null) {
                    bundle.setClassLoader(MusicPlayBean.class.getClassLoader());
                    ArrayList<MusicPlayBean> list = bundle.getParcelableArrayList("list");
                    if (list != null && !list.isEmpty()) {
                        MusicPlayBean musicPlayBean = list.get(0);
                        Log.e(TAG, "获取到列表，大小为：" + list.size());
                        System.out.println(musicPlayBean.toString());
                    }
                }
                break;
        }
    }

    private void dispatchMusicModuleEvent(String event, boolean b, String msg, Bundle bundle) {
        Log.d(TAG, "dispatchMusicModuleEvent() called with: event = [" + event + "], b = [" + b + "], msg = [" + msg + "], bundle = [" + bundle + "]");
        switch (event) {
            case ModuleHelper.EventMusic.MUSIC_PLAYING:
                Intent intent = new Intent();
                intent.setAction("music_action");
                MusicPlayBean playBean = null;
                if (bundle != null) {
                    bundle.setClassLoader(MusicPlayBean.class.getClassLoader());
                    playBean = bundle.getParcelable("iObject");

                }
                intent.putExtra("music", playBean);
                mApplication.getApplicationContext().sendBroadcast(intent);

                Log.d(TAG, "dispatchMusicModuleEvent() called with: event = [" + event + "], bundle = [" + bundle + "]");

                break;
            case ModuleHelper.EventMusic.MUSIC_START:
                break;
            case ModuleHelper.EventMusic.MUSIC_STOP:
                break;


            case ModuleHelper.EventMusic.MUSIC_NEW_PLAYING:



                break;
        }
    }


}
