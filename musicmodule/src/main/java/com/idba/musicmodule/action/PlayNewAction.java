package com.idba.musicmodule.action;

import android.content.Context;
import android.content.Intent;

import com.idba.icore.RouterActionResult;
import com.idba.icore.action.RouterAction;
import com.idba.musicmodule.MusicService;

import java.util.HashMap;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-12 11:10
 * describe:
 **/
public class PlayNewAction extends RouterAction {
    @Override
    public boolean isAsync(Context context, HashMap<String, String> requestData) {
        return false;
    }

    @Override
    public RouterActionResult invoke(Context context, HashMap<String, String> requestData) {
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra("command", "play");
        context.startService(intent);


        RouterActionResult result = new RouterActionResult.Builder()
                .code(RouterActionResult.CODE_SUCCESS)
                .msg("play success")
                .data("")
                .object(null)
                .build();




        return result;
    }
}
