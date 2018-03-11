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
public class RouterErrorAction extends RouterAction {

    private static final String DEFAULT_MESSAGE = "Something was really wrong. Ha ha!";
    private int mCode;
    private String mMessage;
    private boolean mAsync;
    public RouterErrorAction() {
        mCode = RouterActionResult.CODE_ERROR;
        mMessage = DEFAULT_MESSAGE;
        mAsync = false;
    }

    public RouterErrorAction(boolean isAsync, int code, String message) {
        this.mCode = code;
        this.mMessage = message;
        this.mAsync = isAsync;
    }

    @Override
    public boolean isAsync(Context context, HashMap<String, String> requestData) {
        return mAsync;
    }

    @Override
    public RouterActionResult invoke(Context context, HashMap<String, String> requestData) {
        RouterActionResult result = new RouterActionResult.Builder()
                .code(mCode)
                .msg(mMessage)
                .data(null)
                .object(null)
                .build();
        return result;
    }


}
