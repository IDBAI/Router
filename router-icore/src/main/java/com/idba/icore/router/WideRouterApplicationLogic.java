package com.idba.icore.router;

import com.idba.icore.multiprocess.RouterApplicationLogic;
import com.idba.icore.tools.Logger;



/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 * desc:wide router,please no changed
 **/
public final class WideRouterApplicationLogic extends RouterApplicationLogic {
    private static final String TAG = "WideRouterLogic";
    @Override
    public void onCreate() {
        Logger.d(TAG, "onCreate: ");
        initRouter();
    }

    //广域路由初始化其他进程路由
    protected void initRouter() {
        WideRouter.getInstance(mApplication);
        mApplication.initializeAllProcessRouter();
    }
}
