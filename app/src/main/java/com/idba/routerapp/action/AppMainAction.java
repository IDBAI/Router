package com.idba.routerapp.action;

import android.content.Context;

import com.idba.icore.RouterActionResult;
import com.idba.icore.action.RouterAction;

import java.util.HashMap;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-09 14:13
 * describe:
 **/
public class AppMainAction extends RouterAction {
    @Override
    public boolean isAsync(Context context, HashMap<String, String> requestData) {
        return false;
    }

    @Override
    public RouterActionResult invoke(Context context, HashMap<String, String> requestData) {
        return null;
    }

}
