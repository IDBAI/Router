package com.idba.bigmemorymodule;

import android.util.Log;

import com.idba.bigmemorymodule.callback.BigMemoryMessageCenter;
import com.idba.bigmemorymodule.provider.BigMemoryProvider;
import com.idba.common.modulehelper.CallBackHelper;
import com.idba.common.modulehelper.ProviderHelper;
import com.idba.icore.multiprocess.RouterApplicationLogic;
import com.idba.icore.router.LocalRouter;
import com.idba.icore.tools.ProcessUtil;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 16:06
 **/
public class MemoryApplicationLogic extends RouterApplicationLogic {
    private static final String TAG = "MemoryApplicationLogic";
    @Override
    public void onCreate() {
        String processName = ProcessUtil.getProcessName(mApplication.getApplicationContext(), ProcessUtil.getMyProcessId());
        Log.d(TAG, "onCreate: processName = " + processName);

        LocalRouter.getInstance().registerProvider(ProviderHelper.PROVIDER_MEMORY,new BigMemoryProvider());

        LocalRouter.getInstance().registerRemoteistener(CallBackHelper.CALLBACK_MEMORY,new BigMemoryMessageCenter());
    }
}
