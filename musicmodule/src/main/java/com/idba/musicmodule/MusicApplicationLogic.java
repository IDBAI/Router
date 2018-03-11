package com.idba.musicmodule;

import android.util.Log;

import com.idba.common.modulehelper.CallBackHelper;
import com.idba.common.modulehelper.ProviderHelper;
import com.idba.icore.multiprocess.RouterApplicationLogic;
import com.idba.icore.router.LocalRouter;
import com.idba.icore.tools.ProcessUtil;
import com.idba.musicmodule.callback.MusicCallBackMessageCenter;
import com.idba.musicmodule.provider.MusicProvider;




/**
 * Router
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-16 11:20
 * describe:for register Provider and callback
 **/
public class MusicApplicationLogic extends RouterApplicationLogic {
    private static final String TAG = "MusicApplicationLogic";

    @Override
    public void onCreate() {

        String checkProcessName = ProcessUtil.getProcessName(mApplication.getApplicationContext(), ProcessUtil.getMyProcessId());
        Log.d(TAG, "onCreate: checkProcessName = " + checkProcessName);
        LocalRouter.getInstance().registerProvider(ProviderHelper.PROVIDER_MUSIC, new MusicProvider());
        LocalRouter.getInstance().registerRemoteistener(CallBackHelper.CALLBACK_MUSIC, new MusicCallBackMessageCenter(mApplication));
    }


}
