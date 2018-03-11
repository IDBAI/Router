package com.idba.icore;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.idba.icore.multiprocess.PriorityLogicWrapper;
import com.idba.icore.multiprocess.RouterApplicationLogic;
import com.idba.icore.router.LocalRouter;
import com.idba.icore.router.LocalRouterConnectService;
import com.idba.icore.router.WideRouter;
import com.idba.icore.router.WideRouterApplicationLogic;
import com.idba.icore.router.WideRouterConnectService;
import com.idba.icore.tools.Logger;
import com.idba.icore.tools.ProcessUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/
public abstract class RouterApplication extends Application {
    private static final String TAG = "RouterApplication";

    private static RouterApplication sInstance;
    private ArrayList<PriorityLogicWrapper> mLogicList;
    private HashMap<String, ArrayList<PriorityLogicWrapper>> mLogicClassMap;
    private HashMap<String, Class<? extends LocalRouterConnectService>> processMap;

    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        String mProcessName = ProcessUtil.getProcessName(this, ProcessUtil.getMyProcessId());
        Logger.d(TAG, "onCreate: RouterApplication : " + mProcessName);

//        if (mProcessName.equals(ProcessConfig.PROCESS_NEED_BIDIRECTIONAL_MAIN)
//                || mProcessName.equals(ProcessConfig.PROCESS_NEED_BIDIRECTIONAL_MUSIC)
//                || mProcessName.equals(ProcessConfig.PROCESS_NEED_BIDIRECTIONAL_PIC)
//                ) {

        Logger.d(TAG, "Application onCreate start: " + System.currentTimeMillis());
        init();
        startWideRouter();
        initializeLogic();
        dispatchLogic();
        instantiateLogic();

        // Traverse the application logic.
        // 遍历mLogicList数组，触发initializeLogic 方法注册的Logic对象执行onCreate 方法，实现注册 Provider —> Action
        if (null != mLogicList && mLogicList.size() > 0) {
            for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
                if (null != priorityLogicWrapper && null != priorityLogicWrapper.instance) {
                    priorityLogicWrapper.instance.onCreate();
                }
            }
        }

//        }

        startOtherProcess();
        Logger.d(TAG, "Application onCreate end: " + System.currentTimeMillis());
    }

    /**
     * 启动其他进程
     */
    private void startOtherProcess() {
        addBackGroundStartProcess();
        if (processMap != null && !processMap.isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Iterator<Map.Entry<String, Class<? extends LocalRouterConnectService>>> iterator = processMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Class<? extends LocalRouterConnectService>> next = iterator.next();
                        String process = next.getKey();
                        if (!ProcessUtil.isProessRunning(sInstance.getApplicationContext(), process)) {
                            Class<? extends LocalRouterConnectService> serviceClass = next.getValue();
                            sInstance.getApplicationContext().startService(new Intent(sInstance, serviceClass));
                            Logger.e(TAG, "后台提前自启进程：" + process);
                        }
                    }
                }
            }).start();
        }
    }

    protected void addProcess(String process, @NonNull Class<? extends LocalRouterConnectService> serviceClass) {
        if (processMap == null)
            processMap = new HashMap<>();
        processMap.put(process, serviceClass);
    }


    /**
     * 默认情况下，路由方案的多进程是懒加载模式启动的，为了提高用户的体验，可以把需要的进程提前启动，这样，路由的跳转会比较流畅，
     * 就可以在这个方法，添加需要提前启动的进程，这个方法是最后执行的！
     */
    protected abstract void addBackGroundStartProcess();


    private void init() {
        LocalRouter.init(this);
        mLogicClassMap = new HashMap<>();
    }

    protected void startWideRouter() {
        if (needMultipleProcess()) {
            Logger.d(TAG, "startWideRouter: ");
            registerApplicationLogic(WideRouter.PROCESS_NAME, 1000, WideRouterApplicationLogic.class);
            Intent intent = new Intent(this, WideRouterConnectService.class);
            startService(intent);
        }
    }

    /**
     * 初始化所有进程的路由连接服务
     */
    public abstract void initializeAllProcessRouter();

    /**
     * 初始化配置所有的模块的逻辑部分（实现provider action的注册）
     */
    protected abstract void initializeLogic();

    /**
     * 是否是多进程环境
     *
     * @return
     */
    public abstract boolean needMultipleProcess();

    protected void registerApplicationLogic(String processName, int priority, @NonNull Class<? extends RouterApplicationLogic> logicClass) {
        if (null != mLogicClassMap) {
            ArrayList<PriorityLogicWrapper> tempList = mLogicClassMap.get(processName);
            if (null == tempList) {
                tempList = new ArrayList<>();
                mLogicClassMap.put(processName, tempList);
            }
            if (tempList.size() > 0) {
                for (PriorityLogicWrapper priorityLogicWrapper : tempList) {
                    if (logicClass.getName().equals(priorityLogicWrapper.logicClass.getName())) {
                        throw new RuntimeException(logicClass.getName() + " has registered.");
                    }
                }
            }
            PriorityLogicWrapper priorityLogicWrapper = new PriorityLogicWrapper(priority, logicClass);
            tempList.add(priorityLogicWrapper);
        }
    }

    private void dispatchLogic() {
        if (null != mLogicClassMap) {
            mLogicList = mLogicClassMap.get(ProcessUtil.getProcessName(this, ProcessUtil.getMyProcessId()));
        }
    }

    private void instantiateLogic() {
        if (null != mLogicList && mLogicList.size() > 0) {
            if (null != mLogicList && mLogicList.size() > 0) {
                Collections.sort(mLogicList);
                for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
                    if (null != priorityLogicWrapper) {
                        try {
                            priorityLogicWrapper.instance = priorityLogicWrapper.logicClass.newInstance();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        if (null != priorityLogicWrapper.instance) {
                            priorityLogicWrapper.instance.setApplication(this);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (null != mLogicList && mLogicList.size() > 0) {
            for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
                if (null != priorityLogicWrapper && null != priorityLogicWrapper.instance) {
                    priorityLogicWrapper.instance.onTerminate();
                }
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (null != mLogicList && mLogicList.size() > 0) {
            for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
                if (null != priorityLogicWrapper && null != priorityLogicWrapper.instance) {
                    priorityLogicWrapper.instance.onLowMemory();
                }
            }
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (null != mLogicList && mLogicList.size() > 0) {
            for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
                if (null != priorityLogicWrapper && null != priorityLogicWrapper.instance) {
                    priorityLogicWrapper.instance.onTrimMemory(level);
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (null != mLogicList && mLogicList.size() > 0) {
            for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
                if (null != priorityLogicWrapper && null != priorityLogicWrapper.instance) {
                    priorityLogicWrapper.instance.onConfigurationChanged(newConfig);
                }
            }
        }
    }

    public static RouterApplication getMaApplication() {
        return sInstance;
    }
}
