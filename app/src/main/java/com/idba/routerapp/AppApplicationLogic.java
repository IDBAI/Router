package com.idba.routerapp;

import android.util.Log;

import com.idba.common.modulehelper.CallBackHelper;
import com.idba.common.modulehelper.ProviderHelper;
import com.idba.icore.multiprocess.RouterApplicationLogic;
import com.idba.icore.router.LocalRouter;
import com.idba.icore.tools.ProcessUtil;
import com.idba.routerapp.callback.AppCallbackMessageCenter;
import com.idba.routerapp.provider.AppProvider;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-09 14:17
 * describe:
 **/
public class AppApplicationLogic extends RouterApplicationLogic {
    private static final String TAG = "AppApplicationLogic";

    @Override
    public void onCreate() {

        String processName = ProcessUtil.getProcessName(mApplication.getApplicationContext(), ProcessUtil.getMyProcessId());
        Log.d(TAG, "onCreate: processName = " + processName);
        //必须要注册Provider哦，是充当外部接口的角色
        LocalRouter.getInstance().registerProvider(ProviderHelper.PROVIDER_APP, new AppProvider());
        //下面的远程回调是可选
        LocalRouter.getInstance().registerRemoteistener(CallBackHelper.CALLBACK_APP, new AppCallbackMessageCenter(mApplication));
    }
}
