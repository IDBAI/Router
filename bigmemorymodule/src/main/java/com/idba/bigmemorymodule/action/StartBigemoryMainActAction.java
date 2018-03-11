package com.idba.bigmemorymodule.action;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.idba.bigmemorymodule.act.BigMemoryMainActivity;
import com.idba.common.bean.MemoryBean;
import com.idba.common.bean.MusicPlayBean;
import com.idba.common.modulehelper.CallBackHelper;
import com.idba.common.modulehelper.ModuleHelper;
import com.idba.icore.RouterActionResult;
import com.idba.icore.action.RouterAction;
import com.idba.icore.router.LocalRouter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 16:03
 **/
public class StartBigemoryMainActAction extends RouterAction {
    private static final String TAG = "StartBigemoryMainActAct";

    @Override
    public boolean isAsync(Context context, HashMap<String, String> requestData) {
        return false;
    }

    /**
     * 实现这个Action的具体逻辑事件
     * @param context
     * @param requestData
     * @return
     */
    @Override
    public RouterActionResult invoke(Context context, HashMap<String, String> requestData) {
        String memorySize = requestData.get("memorySize");
        if (context instanceof Activity) {
            Intent intent = new Intent(context, BigMemoryMainActivity.class);
            intent.putExtra("memorySize", memorySize);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, BigMemoryMainActivity.class);
            intent.putExtra("memorySize", memorySize);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
//        testListSend();
//        testMulitBeanSend();
//        return new RouterActionResult.Builder().code(RouterActionResult.CODE_SUCCESS).msg("success").data("跳转成功~").build();

        return null;
    }


    /**
     * 测试回调同时发送多个大对象，跨进程，库模块发送到APP模块
     */
    private void testMulitBeanSend() {
        Log.v(TAG, "sendRemoteCallBack: 跳转成功，准备同时发送多个大对象数据！");

        Bundle bundle = new Bundle();
        MusicPlayBean playBean = new MusicPlayBean().setDuration(12 * 60).setSpeed(100).setArtist("古巨基").setName("情歌王").setAuthor(100);
        bundle.putParcelable("playBean", playBean);

        MemoryBean memoryBean = new MemoryBean().setTotalSize(1024).setRelaySize(450);
        bundle.putParcelable("memoryBean", memoryBean);

        LocalRouter.getInstance().sendEventCallback(CallBackHelper.CALLBACK_APP, ModuleHelper.Module.MODULE_BIG_MEMORY, ModuleHelper.EventMemory.PARSE_MULITBEAN, true, "启动memory页面，独立进程，这个是远程回调方法！", bundle);

    }

    /**
     * 测试回调发送列表，跨进程，库模块发送到APP模块
     */
    private void testListSend() {
        Log.v(TAG, "sendRemoteCallBack: 跳转成功，准备发送列表数据！");
        Bundle bundle = new Bundle();
        ArrayList<MusicPlayBean> list = getList(1000);
        bundle.putParcelableArrayList("list", list);
        LocalRouter.getInstance().sendEventCallback(CallBackHelper.CALLBACK_APP, ModuleHelper.Module.MODULE_BIG_MEMORY, ModuleHelper.EventMemory.PARSE_LIST, true, "启动memory页面，独立进程，这个是远程回调方法！", bundle);
    }

    private ArrayList<MusicPlayBean> getList(int size) {
        ArrayList<MusicPlayBean> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MusicPlayBean playBean = new MusicPlayBean().setDuration(12 * 60).setSpeed(i).setArtist("古巨基").setName("情歌王").setAuthor(100);
            list.add(playBean);
        }
        return list;

    }


}
