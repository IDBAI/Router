package com.idba.icore.multiprocess;

import android.content.res.Configuration;
import android.support.annotation.NonNull;

import com.idba.icore.RouterApplication;




/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 * desc:
 Register Provider and remote callback
 eg，you need register it by onCreate()：
 <p></p>
 <code>
 <p>//you can logger the current process's name</p>
 <p>String processName = ProcessUtil.getProcessName(mApplication.getApplicationContext(), ProcessUtil.getMyProcessId());</p>
 <p>Logger.d(TAG, "onCreate: processName = " + processName);</p>
 <p>You must register the provider ,it used as external interface</p>
 <p>LocalRouter.getInstance().registerProvider(XxxHelper.XXX_PROVIDER, new XXXProvider());</p>
 <p>The following remote callback is optional</p>
 <p>LocalRouter.getInstance().registerRemoteistener(XxxHelper.XXX_CALLBACK, new XxxCallBackMessageCenter(mApplication));</p>
 </code>
 **/

public abstract class RouterApplicationLogic {
    protected RouterApplication mApplication;

    public RouterApplicationLogic() {
    }

    public void setApplication(@NonNull RouterApplication application) {
        mApplication = application;
    }

    public abstract void onCreate();

    public void onTerminate() {
    }

    public void onLowMemory() {
    }

    public void onTrimMemory(int level) {
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }
}
