package com.idba.musicmodule.callback;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.idba.icore.RouterApplication;
import com.idba.icore.callback.RouterCallbackMessageCenter;


/**
 * android-2
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 14:25
 * this module's message center
 **/
public class MusicCallBackMessageCenter extends RouterCallbackMessageCenter {
    private static final String TAG = "MusicCallBackMessage";

    public MusicCallBackMessageCenter(RouterApplication mApplication) {

    }

    @Override
    public void callback(String fromModule, String event, boolean boo, String msg, Bundle bundle) throws RemoteException {
        Log.d(TAG, "callback() called with: fromModule = [" + fromModule + "], event = [" + event + "], boo = [" + boo + "], msg = [" + msg + "], bundle = [" + bundle + "]");
    }
}
