package com.idba.routerapp;

import com.idba.bigmemorymodule.MemoryApplicationLogic;
import com.idba.bigmemorymodule.service.BigMemoryConnenctService;
import com.idba.icore.RouterApplication;
import com.idba.icore.router.WideRouter;
import com.idba.musicmodule.MusicApplicationLogic;
import com.idba.musicmodule.service.MusicConnectService;
import com.idba.picmodule.PicApplicationLogic;
import com.idba.routerapp.service.MainRouterConnectService;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/
public class MyApplication extends RouterApplication {
    @Override
    public void onCreate() {
        super.onCreate();
//        BlockCanary.install(this, new AppBlockCanaryContext()).start();
    }

    /**
     * 每增加一个进程，必须有对应的一个的连接服务（第一步回调）
     */
    @Override
    public void initializeAllProcessRouter() {
        WideRouter.registerLocalRouter("com.idba.routerapp", MainRouterConnectService.class);
        WideRouter.registerLocalRouter("com.idba.routerapp:memory", BigMemoryConnenctService.class);
        WideRouter.registerLocalRouter("com.idba.routerapp:music", MusicConnectService.class);
    }

    /**
     * 每个模块，或者一个模块存在两个进程，就需要定义一个Logic类（第二步回调）
     */
    @Override
    protected void initializeLogic() {
        registerApplicationLogic("com.idba.routerapp", 999, AppApplicationLogic.class);
        registerApplicationLogic("com.idba.routerapp", 999, MusicApplicationLogic.class);
        registerApplicationLogic("com.idba.routerapp:music", 999, MusicApplicationLogic.class);
        registerApplicationLogic("com.idba.routerapp", 998, PicApplicationLogic.class);
        registerApplicationLogic("com.idba.routerapp:memory", 998, MemoryApplicationLogic.class);
    }
    /**
     * 添加需要自启的进程，以及连接服务，保证后续的性能（第三步回调）
     */
    @Override
    protected void addBackGroundStartProcess() {
        addProcess("com.idba.routerapp:memory", BigMemoryConnenctService.class);
        addProcess("com.idba.routerapp:music", MusicConnectService.class);
    }
    @Override
    public boolean needMultipleProcess() {
        return true;
    }
}
