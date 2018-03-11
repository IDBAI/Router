package com.idba.picmodule.action;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.idba.common.bean.MusicPlayBean;
import com.idba.common.modulehelper.CallBackHelper;
import com.idba.common.modulehelper.ModuleHelper;
import com.idba.icore.RouterActionResult;
import com.idba.icore.action.RouterAction;
import com.idba.icore.router.LocalRouter;
import com.idba.picmodule.act.PicMainActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 19:04
 **/
public class StartMainActAction extends RouterAction {
    private static final String TAG = "StartMainActAction";

    @Override
    public boolean isAsync(Context context, HashMap<String, String> requestData) {
        return false;
    }

    @Override
    public RouterActionResult invoke(Context context, HashMap<String, String> requestData) {
        String pictureID = requestData.get("PictureID");

        //同一个进程
        if (context instanceof Activity) {
            Intent intent = new Intent(context, PicMainActivity.class);
            intent.putExtra("PictureID", pictureID);
            String code = requestData.get("requestCode");
            if (code != null) {
                int requestCode = 0;
                try {
                    requestCode = Integer.parseInt(code);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                //支持startActivityForResult
                ((Activity) context).startActivityForResult(intent, requestCode);
            } else {
                context.startActivity(intent);
            }
        } else {
            //在其他进程启动
            Intent intent = new Intent(context, PicMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("PictureID", pictureID);
            context.startActivity(intent);
        }
//        testSendList();
        return new RouterActionResult.Builder().code(RouterActionResult.CODE_SUCCESS).msg("success").data("跳转成功~").build();
    }


    /**
     * 测试回调发送列表数据
     */
    private void testSendList() {
        Log.v(TAG, "sendRemoteCallBack: 跳转成功，准备发送列表数据！");
        Bundle bundle = new Bundle();
        ArrayList<MusicPlayBean> list = getList(1000);
        bundle.putParcelableArrayList("list", list);
        LocalRouter.getInstance().sendEventCallback(CallBackHelper.CALLBACK_APP, ModuleHelper.Module.MODULE_PIC,ModuleHelper.EventPic.PARSE_LIST, true, "启动图片首页成功，这个是远程回调方法！", bundle);
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
