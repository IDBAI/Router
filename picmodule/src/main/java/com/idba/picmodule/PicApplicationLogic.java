package com.idba.picmodule;

import android.util.Log;

import com.idba.common.modulehelper.CallBackHelper;
import com.idba.common.modulehelper.ProviderHelper;
import com.idba.icore.multiprocess.RouterApplicationLogic;
import com.idba.icore.router.LocalRouter;
import com.idba.icore.tools.ProcessUtil;
import com.idba.picmodule.callback.PicCallbackMessageCenter;
import com.idba.picmodule.provider.PicProvider;


/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 18:58
 **/
public class PicApplicationLogic extends RouterApplicationLogic {
    private static final String TAG = "PicApplicationLogic";
    @Override
    public void onCreate() {
        String checkProcessName = ProcessUtil.getProcessName(mApplication.getApplicationContext(), ProcessUtil.getMyProcessId());
        Log.d(TAG, "onCreate: checkProcessName = " + checkProcessName);
        LocalRouter.getInstance().registerProvider(ProviderHelper.PROVIDER_PIC, new PicProvider());

        LocalRouter.getInstance().registerRemoteistener(   CallBackHelper.CALLBACK_PIC, new PicCallbackMessageCenter(mApplication));
    }
}
