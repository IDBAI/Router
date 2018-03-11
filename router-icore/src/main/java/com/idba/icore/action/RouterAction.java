package com.idba.icore.action;

import android.content.Context;

import com.idba.icore.RouterActionResult;

import java.util.HashMap;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/
public abstract class RouterAction {
    public abstract boolean isAsync(Context context, HashMap<String, String> requestData);

    public abstract RouterActionResult invoke(Context context, HashMap<String, String> requestData);



    public boolean isAsync(Context context, HashMap<String, String> requestData, Object object) {
        return false;
    }

    public RouterActionResult invoke(Context context, HashMap<String, String> requestData, Object object) {
        return new RouterActionResult.Builder().code(RouterActionResult.CODE_NOT_IMPLEMENT).msg("This method has not yet been implemented.").build();
    }
}
