package com.idba.icore.router;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.idba.icore.ILocalRouterAIDL;
import com.idba.icore.IRouterCallBackAIDL;
import com.idba.icore.ProcessConfig;
import com.idba.icore.RouterActionResult;
import com.idba.icore.RouterApplication;
import com.idba.icore.tools.Logger;
import com.idba.icore.tools.ProcessUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.Context.BIND_AUTO_CREATE;


/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 * desc:
 **/
public class WideRouter {
    private static final String TAG = "WideRouter";
    public static final String PROCESS_NAME = ProcessConfig.ICORE_PROCESS_NAME;
    private static HashMap<String, ConnectServiceWrapper> sLocalRouterClasses;
    private static WideRouter sInstance = null;
    private RouterApplication mApplication;
    private Object lock = new Object();
    /**
     * 异步等待超时时间
     */
    private static final int LOCK_TIMEOUT = 2000;
    /**
     * 保存一组局域路由的ServiceConnection 对象
     */
    private HashMap<String, ServiceConnection> mLocalRouterConnectionMap;
    /**
     * 保存一组局域路由的AIDL连接对象
     */
    private HashMap<String, ILocalRouterAIDL> mLocalRouterAIDLMap;
    boolean mIsStopping = false;
    /**
     * 保存一组局域路由的接口
     */
    private HashMap<String, IRouterCallBackAIDL> mLocalRouterCallbackMap;

    private WideRouter(RouterApplication context) {
        mApplication = context;
        String checkProcessName = ProcessUtil.getProcessName(context, ProcessUtil.getMyProcessId());
        //非主进程（DoMain进程），不能初始化广域路由！
        if (!PROCESS_NAME.equals(checkProcessName)) {
            throw new RuntimeException("You should not initialize the WideRouter in process:" + checkProcessName);
//            return;
        }
        sLocalRouterClasses = new HashMap<>();
        mLocalRouterConnectionMap = new HashMap<>();
        mLocalRouterAIDLMap = new HashMap<>();
        mLocalRouterCallbackMap = new HashMap<>();
    }

    public static synchronized WideRouter getInstance(@NonNull RouterApplication context) {
        if (sInstance == null) {
            sInstance = new WideRouter(context);
        }
        return sInstance;
    }

    public static void registerLocalRouter(String processName, Class<? extends LocalRouterConnectService> targetClass) {
        if (null == sLocalRouterClasses) {
            sLocalRouterClasses = new HashMap<>();
        }
        ConnectServiceWrapper connectServiceWrapper = new ConnectServiceWrapper(targetClass);
        sLocalRouterClasses.put(processName, connectServiceWrapper);
    }

    boolean checkLocalRouterHasRegistered(final String domain) {
        if (sLocalRouterClasses == null) {
            Logger.d(TAG, "checkLocalRouterHasRegistered() called with: domain = [" + domain + "]");
            Logger.e(TAG, "checkLocalRouterHasRegistered: oh shit unbeliveable sLocalRouterClasses == null");
            sLocalRouterClasses = new HashMap<>();
        }

        ConnectServiceWrapper connectServiceWrapper = sLocalRouterClasses.get(domain);
        if (null == connectServiceWrapper) {
            return false;
        }
        Class<? extends LocalRouterConnectService> clazz = connectServiceWrapper.targetClass;
        if (null == clazz) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 绑定本地路由连接服务
     *
     * @param domain
     * @param isNeedLock
     * @return
     */
    boolean bindLocalRouter(final String domain, final boolean isNeedLock) {
        Logger.d(TAG, "bindLocalRouter: 绑定本地路由：" + domain + " isNeedLock : " + isNeedLock);
        ConnectServiceWrapper connectServiceWrapper = sLocalRouterClasses.get(domain);
        if (null == connectServiceWrapper) {
            return false;
        }
        Class<? extends LocalRouterConnectService> clazz = connectServiceWrapper.targetClass;
        if (null == clazz) {
            return false;
        }
        final Intent binderIntent = new Intent(mApplication, clazz);
        Bundle bundle = new Bundle();
        binderIntent.putExtras(bundle);
        final ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ILocalRouterAIDL mLocalRouterAIDL = ILocalRouterAIDL.Stub.asInterface(service);
                ILocalRouterAIDL temp = mLocalRouterAIDLMap.get(domain);
                if (null == temp) {
                    mLocalRouterAIDLMap.put(domain, mLocalRouterAIDL);
                    mLocalRouterConnectionMap.put(domain, this);
                    //死循环怀疑点1
                    try {
                        mLocalRouterAIDL.connectWideRouter();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                if (isNeedLock) {
                    try {
                        synchronized (lock) {
                            Logger.w(TAG, "onServiceConnected: lock 被锁" + LOCK_TIMEOUT + "毫秒了！");
                            lock.wait(LOCK_TIMEOUT);
                        }
                        Logger.w(TAG, "onServiceConnected: lock 开始向下执行！");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                registListenerToLocalRouter(mLocalRouterAIDLMap);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Logger.d(TAG, "onServiceDisconnected :" + name.toString());
                unRegistListenerToLocalRouter(domain);
                mLocalRouterAIDLMap.remove(domain);
                mLocalRouterConnectionMap.remove(domain);
            }
        };
        mApplication.bindService(binderIntent, serviceConnection, BIND_AUTO_CREATE);
        return true;
    }


    /**
     * 向所有的局域路对象，注册所有的callback对象，避免后续创建的路由缺失callback
     *
     * @param mLocalRouterAIDL
     */
    private void registListenerToLocalRouter(HashMap<String, ILocalRouterAIDL> mLocalRouterAIDL) {
        Logger.d(TAG, "registListenerToLocalRouter: 广域路由注册callback 添加到对应的局域路由module中...");
        if (mLocalRouterCallbackMap != null && !mLocalRouterCallbackMap.isEmpty()) {

            Iterator<Map.Entry<String, IRouterCallBackAIDL>> iterator = mLocalRouterCallbackMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, IRouterCallBackAIDL> next = iterator.next();
                String callbacktag = next.getKey();
//                String checkProcessName = ProcessUtil.getProcessName(mApplication, ProcessUtil.getMyProcessId());
                IRouterCallBackAIDL callBackAIDL = next.getValue();
                try {
                    if (mLocalRouterAIDL != null && !mLocalRouterAIDL.isEmpty()) {
                        Iterator<Map.Entry<String, ILocalRouterAIDL>> entryIterator = mLocalRouterAIDL.entrySet().iterator();
                        while (entryIterator.hasNext()) {
                            Map.Entry<String, ILocalRouterAIDL> entry = entryIterator.next();
                            ILocalRouterAIDL entryValue = entry.getValue();
                            String domainProcess = entry.getKey();
                            Logger.v(TAG, "registListenerToLocalRouter: domainProcess = " + domainProcess + " callbacktag = " + callbacktag + " success");
                            entryValue.registListener(callbacktag, callBackAIDL);
                        }
                    }
                } catch (RemoteException e) {
                    Logger.e(TAG, "registListenerToWideRouter: 向局域路由注册的远程监听 失败");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 移除局域路由的远程监听
     */
    private void unRegistListenerToLocalRouter(String domain) {
        Logger.d(TAG, "unRegistListenerToLocalRouter: ");
        Logger.d(TAG, domain + " 进程被杀 ，其他进程的AIDL 将移除它！");

        //获取需要移除的接口
        IRouterCallBackAIDL needUnregistCallback = null;
        if (mLocalRouterCallbackMap != null && !mLocalRouterCallbackMap.isEmpty())
            needUnregistCallback = mLocalRouterCallbackMap.get(domain);
        if (needUnregistCallback == null)
            return;

        if (mLocalRouterAIDLMap != null && !mLocalRouterAIDLMap.isEmpty()) {
            Iterator<Map.Entry<String, ILocalRouterAIDL>> iterator = mLocalRouterAIDLMap.entrySet().iterator();
            //遍历连接对象
            while (iterator.hasNext()) {
                Map.Entry<String, ILocalRouterAIDL> next = iterator.next();
                //跳过自己
                String key = next.getKey();
                if (domain.equals(key))
                    continue;
                ILocalRouterAIDL localRouterAIDL = next.getValue();
                if (localRouterAIDL != null) {
                    try {
                        Logger.d(TAG, key + " 进程移除了" + domain + "监听");
                        localRouterAIDL.unRegistListener(domain, needUnregistCallback);
                    } catch (RemoteException e) {
                        Logger.e(TAG, key + " 进程移除" + domain + "监听失败");
                        e.printStackTrace();
                    }
                }

            }
        }

    }
//    private void unRegistListenerToLocalRouter(String domain) {
//        Logger.d(TAG, "unRegistListenerToLocalRouter: ");
//        if (mLocalRouterCallbackMap != null && !mLocalRouterCallbackMap.isEmpty()) {
//            Iterator<Map.Entry<String, IRouterCallBackAIDL>> iterator = mLocalRouterCallbackMap.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry<String, IRouterCallBackAIDL> next = iterator.next();
//                String key = next.getKey();
//                IRouterCallBackAIDL callBackAIDL = next.getValue();
//                try {
//                    ILocalRouterAIDL iLocalRouterAIDL = mLocalRouterAIDLMap.get(domain);
//                    if (iLocalRouterAIDL != null)
//                        iLocalRouterAIDL.unRegistListener(key, callBackAIDL);
//                } catch (RemoteException e) {
//                    Logger.e(TAG, "unRegistListenerToLocalRouter: 移除局域路由的远程监听 失败");
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    boolean disconnectLocalRouter(String domain) {
        Logger.d(TAG, "disconnectLocalRouter() called with: domain = [" + domain + "]");
        if (TextUtils.isEmpty(domain)) {
            return false;
        } else if (PROCESS_NAME.equals(domain)) {
            stopSelf();
            return true;
        } else if (null == mLocalRouterConnectionMap.get(domain)) {
            return false;
        } else {
            ILocalRouterAIDL aidl = mLocalRouterAIDLMap.get(domain);
            if (null != aidl) {
                try {
                    aidl.stopWideRouter();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            unRegistListenerToLocalRouter(domain);
            mApplication.unbindService(mLocalRouterConnectionMap.get(domain));
            mLocalRouterAIDLMap.remove(domain);
            mLocalRouterConnectionMap.remove(domain);
            return true;
        }
    }

    /**
     */
    void stopSelf() {
        mIsStopping = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> locals = new ArrayList<>();
                locals.addAll(mLocalRouterAIDLMap.keySet());
                for (String domain : locals) {
                    ILocalRouterAIDL aidl = mLocalRouterAIDLMap.get(domain);
                    if (null != aidl) {
                        try {
                            aidl.stopWideRouter();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "stopSelf -> run() called");
                        unRegistListenerToLocalRouter(domain);
                        mApplication.unbindService(mLocalRouterConnectionMap.get(domain));
                        mLocalRouterAIDLMap.remove(domain);
                        mLocalRouterConnectionMap.remove(domain);
                    }
                }
                try {
                    Thread.sleep(1000);
                    mApplication.stopService(new Intent(mApplication, WideRouterConnectService.class));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }).start();
    }

    boolean answerLocalAsync(String domain, String routerRequest) {
        ILocalRouterAIDL target = mLocalRouterAIDLMap.get(domain);
        if (target == null) {
            ConnectServiceWrapper connectServiceWrapper = sLocalRouterClasses.get(domain);
            if (null == connectServiceWrapper) {
                return false;
            }
            Class<? extends LocalRouterConnectService> clazz = connectServiceWrapper.targetClass;
            if (null == clazz) {
                return false;
            } else {
                return true;
            }
        } else {
            try {
                return target.checkResponseAsync(routerRequest);
            } catch (RemoteException e) {
                e.printStackTrace();
                return true;
            }
        }
    }

    public RouterResponse route(String domain, String routerRequest) {
        Logger.d(TAG, "route() called with: domain = [" + domain + "], routerRequest = [" + routerRequest + "]");
        Logger.d(TAG, "Process:" + PROCESS_NAME + " Wide route start: " + System.currentTimeMillis());
        RouterResponse routerResponse = new RouterResponse();
        if (mIsStopping) {
            RouterActionResult result = new RouterActionResult.Builder()
                    .code(RouterActionResult.CODE_WIDE_STOPPING)
                    .msg("Wide router is stopping.")
                    .build();
            routerResponse.mIsAsync = true;
            routerResponse.mResultString = result.toString();
            return routerResponse;
        }
        if (PROCESS_NAME.equals(domain)) {
            RouterActionResult result = new RouterActionResult.Builder()
                    .code(RouterActionResult.CODE_TARGET_IS_WIDE)
                    .msg("Domain can not be " + PROCESS_NAME + ".")
                    .build();
            routerResponse.mIsAsync = true;
            routerResponse.mResultString = result.toString();
            return routerResponse;
        }
        ILocalRouterAIDL target = mLocalRouterAIDLMap.get(domain);
        if (null == target) {
            if (!bindLocalRouter(domain, true)) {
                RouterActionResult result = new RouterActionResult.Builder()
                        .code(RouterActionResult.CODE_ROUTER_NOT_REGISTER)
                        .msg("The " + domain + " has not registered.")
                        .build();
                routerResponse.mIsAsync = false;
                routerResponse.mResultString = result.toString();
                Logger.d(TAG, "Process:" + PROCESS_NAME + " Local not register end: " + System.currentTimeMillis());
                return routerResponse;
            } else {
                // Wait to bind the target process connect service, timeout is 30s.
                Logger.d(TAG, "Process:" + PROCESS_NAME + " Bind local router start: " + System.currentTimeMillis());
                int time = 0;
                while (true) {
                    target = mLocalRouterAIDLMap.get(domain);
                    if (null == target) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        time++;
                    } else {
                        Logger.d(TAG, "Process:" + PROCESS_NAME + " Bind local router end: " + System.currentTimeMillis());
                        break;
                    }
                    if (time >= 600) {
                        RouterActionResult result = new RouterActionResult.Builder()
                                .code(RouterActionResult.CODE_CANNOT_BIND_LOCAL)
                                .msg("Can not bind " + domain + ", time out.")
                                .build();
                        routerResponse.mResultString = result.toString();
                        return routerResponse;
                    }
                }
            }
        }
        try {
            Logger.d(TAG, "Process:" + PROCESS_NAME + " Wide target start: " + System.currentTimeMillis());
            String resultString = target.route(routerRequest);
            routerResponse.mResultString = resultString;
            Logger.d(TAG, "Process:" + PROCESS_NAME + " Wide route end: " + System.currentTimeMillis());
        } catch (RemoteException e) {
            e.printStackTrace();
            RouterActionResult result = new RouterActionResult.Builder()
                    .code(RouterActionResult.CODE_REMOTE_EXCEPTION)
                    .msg(e.getMessage())
                    .build();
            routerResponse.mResultString = result.toString();
            return routerResponse;
        }
        return routerResponse;
    }

    /**
     * 局域路由的远程回调添加广域路由中，添加完成之后才能执行 回调集合的分发到各个局域路由的服务中，以免发生BUG
     *
     * @param callbacktag
     * @param callback
     */
    public void registenerCallBackFromLocalRouter(String callbacktag, IRouterCallBackAIDL callback) {
        Logger.d(TAG, "registenerCallBackFromLocalRouter: callbacktag = " + callbacktag);
        if (mLocalRouterCallbackMap == null) {
            mLocalRouterCallbackMap = new HashMap<>();
        }
        mLocalRouterCallbackMap.put(callbacktag, callback);
    }

    public void unRegistener(String callbacktag, IRouterCallBackAIDL callback) {
        Logger.d(TAG, "unRegistener: ");
        if (mLocalRouterCallbackMap != null) {
            mLocalRouterCallbackMap.remove(callbacktag);
        }

    }

    /**
     * 判断广域路由是已经连接到特定的本地路由连接服务
     *
     * @param localProcess
     * @return
     */
    private boolean isWideRouterHadConnectLocalConnectService(String localProcess) {
        if (mLocalRouterAIDLMap == null && mLocalRouterAIDLMap.isEmpty())
            return false;
        ILocalRouterAIDL localRouterAIDL = mLocalRouterAIDLMap.get(localProcess);
        return localRouterAIDL != null;
    }

    //通知所有的局域路由的回调都完整注册到了广域路由中，现在可以分发至各个局域路由之中了
    public void finishAllRegist(String localProcess) {
        if (!isWideRouterHadConnectLocalConnectService(localProcess)) {
            //未连接，启动连接
            bindLocalRouter(localProcess, false);
        }
        synchronized (lock) {
            Logger.w(TAG, "finishAllRegist: lock锁被释放！");
            lock.notifyAll();
        }
    }
}
