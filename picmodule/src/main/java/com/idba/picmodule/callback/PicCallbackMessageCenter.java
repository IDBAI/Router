package com.idba.picmodule.callback;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.idba.common.bean.MusicPlayBean;
import com.idba.common.bean.iObject;
import com.idba.icore.RouterApplication;
import com.idba.icore.callback.RouterCallbackMessageCenter;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 21:36
 **/
public class PicCallbackMessageCenter extends RouterCallbackMessageCenter {
    private static final String TAG = "PicCallbackMessage";

    public PicCallbackMessageCenter(RouterApplication mApplication) {

    }

    @Override
    public void callback(String fromModule, String event, boolean boo, String msg, Bundle bundle) throws RemoteException {
        Log.d(TAG, "callback() called with: fromModule = [" + fromModule + "], event = [" + event + "], boo = [" + boo + "], msg = [" + msg + "], bundle = [" + bundle + "]");
    }
}
