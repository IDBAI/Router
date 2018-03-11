package com.idba.musicmodule.action;

import android.content.Context;
import android.content.Intent;

import com.idba.icore.RouterActionResult;
import com.idba.icore.action.RouterAction;
import com.idba.icore.tools.Logger;
import com.idba.musicmodule.MusicService;

import java.util.HashMap;


public class StopAction extends RouterAction {

    @Override
    public boolean isAsync(Context context, HashMap<String, String> requestData) {
        return false;
    }

    @Override
    public RouterActionResult invoke(Context context, HashMap<String, String> requestData) {
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra("command", "stop");
        context.startService(intent);
        RouterActionResult result = new RouterActionResult.Builder()
                .code(RouterActionResult.CODE_SUCCESS)
                .msg("stop success")
                .data("")
                .object(null)
                .build();

        Logger.d("StopAction", "\nStopAction end: " + System.currentTimeMillis());
        return result;
    }


}
