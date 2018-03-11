package com.idba.musicmodule.action;

import android.content.Context;
import android.content.Intent;

import com.idba.icore.RouterActionResult;
import com.idba.icore.action.RouterAction;
import com.idba.musicmodule.MusicService;

import java.util.HashMap;



public class PlayAction extends RouterAction {
    private static final String TAG = "PlayAction";

    @Override
    public boolean isAsync(Context context, HashMap<String, String> requestData) {
        return false;
    }

    @Override
    public RouterActionResult invoke(Context context, HashMap<String, String> requestData) {
        Intent intent = new Intent(context, MusicService.class);
        String musicId = requestData.get("musicId");
        intent.putExtra("command", "play");
        intent.putExtra("musicId", musicId);
        context.startService(intent);
        RouterActionResult result = new RouterActionResult.Builder()
                .code(RouterActionResult.CODE_SUCCESS)
                .msg("play success")
                .data("")
                .object(null)
                .build();

//        return result;
        return null;
    }



}
