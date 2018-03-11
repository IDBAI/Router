package com.idba.bigmemorymodule.callback;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.idba.icore.callback.RouterCallbackMessageCenter;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 16:04
 **/
public class BigMemoryMessageCenter extends RouterCallbackMessageCenter {
    private static final String TAG = "BigMemoryMessageCenter";




    @Override
    public void callback(String fromModule, String event, boolean boo, String msg, Bundle bundle) throws RemoteException {
        Log.d(TAG, "callback() called with: fromModule = [" + fromModule + "], event = [" + event + "], boo = [" + boo + "], msg = [" + msg + "], bundle = [" + bundle + "]");
    }
}
