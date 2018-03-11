package com.idba.icore.router;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.idba.annotation.router.CombinationUri;
import com.idba.annotation.router.IntentExtrasParam;
import com.idba.icore.IRouterCallBackAIDL;
import com.idba.icore.IWideRouterAIDL;
import com.idba.icore.ProcessConfig;
import com.idba.icore.RouterActionResult;
import com.idba.icore.RouterApplication;
import com.idba.icore.action.RouterAction;
import com.idba.icore.action.RouterErrorAction;
import com.idba.icore.provider.RouterProvider;
import com.idba.icore.tools.Logger;
import com.idba.icore.tools.ProcessUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/
public class LocalRouter {
    private static final String TAG = "LocalRouter";
    private String mProcessName = ProcessConfig.UNKNOWN_PROCESS_NAME;
    private static LocalRouter sInstance = null;
    private HashMap<String, RouterProvider> mProviders = null;
    private RouterApplication mApplication;
    private IWideRouterAIDL mWideRouterAIDL;
    private static ExecutorService threadPool = null;
    /**
     * 自己的远程监听——>传递给广域路由
     */
    private HashMap<String, IRouterCallBackAIDL> mMineRemoteCallbackMap;
    /**
     * 来自广域路由传递的远程监听——>其他模块的远程监听
     */
    private HashMap<String, RemoteCallbackList<IRouterCallBackAIDL>> mOtherModuleRemoteCallbackMap;


    /**
     * 将消息回调给指定的Module接收
     *
     * @param toModuleCallback 指定接收消息module的callback
     * @param fromModule       消息来自哪个module
     * @param eventCode        消息的EventCode
     * @param boo              未定义含义
     * @param msg              message
     * @param bundle           bundle 发送的大对象包裹
     * @return callback是否发送成功
     */
    public synchronized boolean sendEventCallback(String toModuleCallback, String fromModule, String eventCode, boolean boo, String msg, Bundle bundle) {
        Logger.v(TAG, "sendEventCallback() called with: toModuleCallback = [" + toModuleCallback + "], fromModule = [" + fromModule + "], eventCode = [" + eventCode + "], boo = [" + boo + "], msg = [" + msg + "]");
        if (mOtherModuleRemoteCallbackMap == null || mOtherModuleRemoteCallbackMap.isEmpty()) {
            Logger.e(TAG, "sendEventCallback -> mOtherModuleRemoteCallbackMap is null or empty!");
            return false;
        }
        RemoteCallbackList<IRouterCallBackAIDL> callbackList = mOtherModuleRemoteCallbackMap.get(toModuleCallback);
        if (callbackList == null) {
            Logger.e(TAG, "sendEventCallback -> callbackList is null");
            return false;
        }
        callbackList.beginBroadcast();
        IRouterCallBackAIDL callBackAIDL = callbackList.getBroadcastItem(0);
        if (callBackAIDL == null) {
            Logger.e(TAG, "sendEventCallback -> callBackAIDL is null");
            callbackList.finishBroadcast();
            return false;
        }
        try {
            callBackAIDL.callback(fromModule, eventCode, boo, msg, bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
            Logger.e(TAG, "sendEventCallback -> RemoteException");
            return false;
        } finally {
            callbackList.finishBroadcast();
            Logger.v(TAG, "sendEventCallback success!");
            return true;
        }

    }

    /**
     * 发送消息，默认所有module能够接收此消息
     *
     * @param fromModule 消息来自哪个module
     * @param eventCode  消息的EventCode
     * @param boo        未定义含义
     * @param msg        message
     * @param bundle     bundle 发送的大对象包裹
     * @return callback是否发送成功
     */
    public synchronized boolean sendEventCallbackForAll(String fromModule, String eventCode, boolean boo, String msg, Bundle bundle) {
        Logger.v(TAG, "sendEventCallbackForAll() called with: fromModule = [" + fromModule + "], eventCode = [" + eventCode + "], boo = [" + boo + "], msg = [" + msg + "]");
        if (mOtherModuleRemoteCallbackMap == null || mOtherModuleRemoteCallbackMap.isEmpty()) {
            Logger.e(TAG, "sendEventCallbackForAll -> mOtherModuleRemoteCallbackMap is null or empty!");
            return false;
        }
        Iterator<Map.Entry<String, RemoteCallbackList<IRouterCallBackAIDL>>> iterator = mOtherModuleRemoteCallbackMap.entrySet().iterator();
        if (iterator == null) {
            Logger.e(TAG, "sendEventCallbackForAll -> iterator is null");
            return false;
        }
        //遍历所有module的callback
        HashMap<String, String> errorLog = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<String, RemoteCallbackList<IRouterCallBackAIDL>> callbackListEntry = iterator.next();
            if (callbackListEntry == null) {
                Logger.e(TAG, "callbackListEntry some are empty");
                continue;
            }
            RemoteCallbackList<IRouterCallBackAIDL> callbackList = callbackListEntry.getValue();
            String key = callbackListEntry.getKey();
            if (callbackList == null) {
                Logger.e(TAG, "sendEventCallbackForAll -> callbackList is null");
                errorLog.put(key == null ? "nullkey" : key, "callbackList is null");
                continue;
            }
            callbackList.beginBroadcast();
            IRouterCallBackAIDL callBackAIDL = callbackList.getBroadcastItem(0);
            if (callBackAIDL == null) {
                Logger.e(TAG, "sendEventCallbackForAll -> callBackAIDL is null");
                errorLog.put(key == null ? "nullkey" : key, "callBackAIDL is null");
                callbackList.finishBroadcast();
                continue;
            }
            try {
                callBackAIDL.callback(fromModule, eventCode, boo, msg, bundle);
            } catch (RemoteException e) {
                e.printStackTrace();
                Logger.e(TAG, "sendEventCallbackForAll -> RemoteException");
                errorLog.put(key == null ? "nullkey" : key, e.getMessage());
                continue;
            } finally {
                callbackList.finishBroadcast();
                Logger.v(TAG, "sendEventCallbackForAll success!");
                continue;
            }
        }
        if (!errorLog.isEmpty()) {
            printErrorLog(errorLog);
            return false;
        } else
            return true;
    }

    private void printErrorLog(HashMap<String, String> errorLog) {
        Iterator<Map.Entry<String, String>> iterator = errorLog.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            String value = next.getValue();
            Logger.e(TAG, key + "：" + value);
        }
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mWideRouterAIDL = IWideRouterAIDL.Stub.asInterface(service);
            registListenerToWideRouter();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mWideRouterAIDL = null;
        }
    };

    /**
     * 向广域路由注册自己的LocalRouter的远程监听
     */
    private void registListenerToWideRouter() {
        Logger.d(TAG, "registListenerToWideRouter: 向广域路由注册自己的LocalRouter的远程监听");
        if (mMineRemoteCallbackMap != null && !mMineRemoteCallbackMap.isEmpty()) {
            Iterator<Map.Entry<String, IRouterCallBackAIDL>> iterator = mMineRemoteCallbackMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, IRouterCallBackAIDL> next = iterator.next();
                String callbacktag = next.getKey();
                IRouterCallBackAIDL callBackAIDL = next.getValue();
                try {
                    mWideRouterAIDL.registListener(callbacktag, callBackAIDL);
                } catch (RemoteException e) {
                    Logger.e(TAG, "registListenerToWideRouter: 向广域路由注册LocalRouter的远程监听 失败");
                    e.printStackTrace();
                }
            }
            //通知本地路由的所有callback注册完成
            try {
                mWideRouterAIDL.finishAllRegist(mProcessName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 移除广域路由自己的远程监听
     */
    private void unRegistListenerToWideRouter() {
        Logger.d(TAG, "unRegistListenerToWideRouter: 移除广域路由自己的远程监听");
        if (mMineRemoteCallbackMap != null && !mMineRemoteCallbackMap.isEmpty()) {
            Iterator<Map.Entry<String, IRouterCallBackAIDL>> iterator = mMineRemoteCallbackMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, IRouterCallBackAIDL> next = iterator.next();
                String callbacktag = next.getKey();
                IRouterCallBackAIDL value = next.getValue();
                try {
                    mWideRouterAIDL.unRegistListener(callbacktag, value);
                } catch (RemoteException e) {
                    Logger.e(TAG, "unRegistListenerToWideRouter: 移除广域路由的远程监听 失败");
                    e.printStackTrace();
                }
            }
        }
    }

    private LocalRouter(RouterApplication context) {
        mApplication = context;
        mProcessName = ProcessUtil.getProcessName(context, ProcessUtil.getMyProcessId());
        mProviders = new HashMap<>();
        mMineRemoteCallbackMap = new HashMap<>();
        mOtherModuleRemoteCallbackMap = new HashMap<>();
        if (mApplication.needMultipleProcess() && !WideRouter.PROCESS_NAME.equals(mProcessName)) {
            Logger.d(TAG, "LocalRouter: mProcessName = " + mProcessName + " , will bind WideRouter！");
            //每个进程都需要绑定广域路由
            Logger.d(TAG, "LocalRouter: 新的局域路由进程创建 -> 绑定广域路由");
            bindWideRouter();
        }
    }

    public static synchronized LocalRouter getInstance() {
        if (sInstance == null) {
            sInstance = new LocalRouter(RouterApplication.getMaApplication());
        }
        return sInstance;
    }

    public static synchronized void init(@NonNull RouterApplication context) {
        if (sInstance == null) {
            sInstance = new LocalRouter(context);
        }
    }

    private static synchronized ExecutorService getThreadPool() {
        if (null == threadPool) {
            threadPool = Executors.newCachedThreadPool();
        }
        return threadPool;
    }

    public void bindWideRouter() {
        Logger.d(TAG, "bindWideRouter: domain = " + mProcessName);
        Intent binderIntent = new Intent(mApplication, WideRouterConnectService.class);
        binderIntent.putExtra("domain", mProcessName);
        mApplication.bindService(binderIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public void disconnectWideRouter() {
        if (null == mServiceConnection) {
            return;
        }
        unRegistListenerToWideRouter();
        mApplication.unbindService(mServiceConnection);
        mWideRouterAIDL = null;
    }

    public void registerProvider(String providerName, RouterProvider provider) {
        mProviders.put(providerName, provider);
    }

    public void registerRemoteistener(String callbackTag, IRouterCallBackAIDL callback) {
        Logger.d(TAG, "registerRemoteistener: callbackTag = " + callbackTag);
        if (mMineRemoteCallbackMap == null)
            mMineRemoteCallbackMap = new HashMap<>();
        mMineRemoteCallbackMap.put(callbackTag, callback);
    }


    public boolean checkWideRouterConnection() {
        boolean result = false;
        if (mWideRouterAIDL != null) {
            result = true;
        }
        return result;
    }

    boolean answerWiderAsync(@NonNull RouterRequest routerRequest) {
        if (mProcessName.equals(routerRequest.getDomain()) && checkWideRouterConnection()) {
            return findRequestAction(routerRequest).isAsync(mApplication, routerRequest.getData());
        } else {
            return true;
        }
    }

    public RouterResponse route(Context context, @NonNull RouterRequest routerRequest) throws Exception {
        Logger.d(TAG, "route -> Process:" + mProcessName + " Local route start: " + System.currentTimeMillis());
        RouterResponse routerResponse = new RouterResponse();
        // Local request
        if (mProcessName.equals(routerRequest.getDomain())) {
            Logger.d(TAG, "route: Local request 。");
            HashMap<String, String> params = new HashMap<>();
            Object attachment = routerRequest.getAndClearObject();
            params.putAll(routerRequest.getData());
            Logger.d(TAG, "Process:" + mProcessName + " Local find action start: " + System.currentTimeMillis());
            RouterAction targetAction = findRequestAction(routerRequest);
            routerRequest.isIdle.set(true);
            Logger.d(TAG, "Process:" + mProcessName + " Local find action end: " + System.currentTimeMillis());
            routerResponse.mIsAsync = attachment == null ? targetAction.isAsync(context, params) : targetAction.isAsync(context, params, attachment);
            // Sync result, return the result immediately.
            if (!routerResponse.mIsAsync) {
                RouterActionResult result = attachment == null ? targetAction.invoke(context, params) : targetAction.invoke(context, params, attachment);
                if (result != null) {
                    routerResponse.mResultString = result.toString();
                    routerResponse.mObject = result.getObject();
                }
                Logger.d(TAG, "Process:" + mProcessName + " Local sync end: " + System.currentTimeMillis());

            }
            // Async result, use the thread pool to execute the task.
            else {
                LocalTask task = new LocalTask(routerResponse, params, attachment, context, routerRequest.getCallBackTag(), targetAction);
                routerResponse.mAsyncResponse = getThreadPool().submit(task);
            }
        } else if (!mApplication.needMultipleProcess()) {
            throw new Exception("Please make sure the returned value of needMultipleProcess in RouterApplication is true, so that you can invoke other process action.");
        }
        // IPC request
        else {
            Logger.d(TAG, "route: IPC request 。");
            String domain = routerRequest.getDomain();
            String routerRequestString = routerRequest.toString();
            routerRequest.isIdle.set(true);
            if (checkWideRouterConnection()) {
                Logger.d(TAG, "route: 已经连接了广域路由！");
                Logger.d(TAG, "Process:" + mProcessName + " Wide async check start: " + System.currentTimeMillis());
                //If you don't need wide async check, use "routerResponse.mIsAsync = false;" replace the next line to improve performance.
                routerResponse.mIsAsync = false;//mWideRouterAIDL.checkResponseAsync(domain, routerRequestString);
                Logger.d(TAG, "Process:" + mProcessName + " Wide async check end: " + System.currentTimeMillis());
            }
            // Has not connected with the wide router.
            else {
                Logger.d(TAG, "route:  Has not connected with the wide router.");
                routerResponse.mIsAsync = true;
                ConnectWideTask task = new ConnectWideTask(routerResponse, domain, routerRequestString);
                routerResponse.mAsyncResponse = getThreadPool().submit(task);
                return routerResponse;
            }
            if (!routerResponse.mIsAsync) {
                routerResponse.mResultString = mWideRouterAIDL.route(domain, routerRequestString);
                Logger.d(TAG, "Process:" + mProcessName + " Wide sync end: " + System.currentTimeMillis());
            }
            // Async result, use the thread pool to execute the task.
            else {
                WideTask task = new WideTask(domain, routerRequestString);
                routerResponse.mAsyncResponse = getThreadPool().submit(task);
            }
        }
        return routerResponse;
    }

    public boolean stopSelf(Class<? extends LocalRouterConnectService> clazz) {
        if (checkWideRouterConnection()) {
            try {
                if (mWideRouterAIDL != null)
                    return mWideRouterAIDL.stopRouter(mProcessName);
                else
                    return false;
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            mApplication.stopService(new Intent(mApplication, clazz));
            return true;
        }
    }

    public void stopWideRouter() {
        if (checkWideRouterConnection()) {
            try {
                mWideRouterAIDL.stopRouter(WideRouter.PROCESS_NAME);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Logger.e(TAG, "This local router hasn't connected the wide router.");
        }
    }

    private RouterAction findRequestAction(RouterRequest routerRequest) {
        RouterProvider targetProvider = mProviders.get(routerRequest.getProvider());
        RouterErrorAction defaultNotFoundAction = new RouterErrorAction(false, RouterActionResult.CODE_NOT_FOUND, "Not found the action.");
        if (null == targetProvider) {
            return defaultNotFoundAction;
        } else {
            RouterAction targetAction = targetProvider.findAction(routerRequest.getAction());
            if (null == targetAction) {
                return defaultNotFoundAction;
            } else {
                return targetAction;
            }
        }
    }

    /**
     * 添加其他进程的远程监听
     *
     * @param callbacktag
     * @param callback
     */
    public void addOtherModuleRemoteListener(String callbacktag, IRouterCallBackAIDL callback) {
        if (mOtherModuleRemoteCallbackMap == null) {
            mOtherModuleRemoteCallbackMap = new HashMap<>();
        }
        RemoteCallbackList<IRouterCallBackAIDL> remoteCallbackList = new RemoteCallbackList<>();
        remoteCallbackList.register(callback);
        mOtherModuleRemoteCallbackMap.put(callbacktag, remoteCallbackList);
        Logger.d(TAG, "addOtherModuleRemoteListener: callbacktag = " + callbacktag);
    }

    public void removeOtherModuleRemoteListener(String callbacktag, IRouterCallBackAIDL callback) {
        Logger.d(TAG, "removeOtherModuleRemoteListener: ");
        if (mOtherModuleRemoteCallbackMap != null) {
            RemoteCallbackList<IRouterCallBackAIDL> callbackList = mOtherModuleRemoteCallbackMap.get(callbacktag);
            if (callbackList != null)
                callbackList.unregister(callback);
            mOtherModuleRemoteCallbackMap.remove(callbacktag);
        }
    }

    private class LocalTask implements Callable<String> {
        private RouterResponse mResponse;
        private HashMap<String, String> mRequestData;
        private Context mContext;
        private String callBackTag;
        private RouterAction mAction;
        private Object mObject;

        public LocalTask(RouterResponse routerResponse, HashMap<String, String> requestData, Object object, Context context, String callBackTag, RouterAction routerAction) {
            this.mContext = context;
            this.mResponse = routerResponse;
            this.mRequestData = requestData;
            this.callBackTag = callBackTag;
            this.mAction = routerAction;
            this.mObject = object;
        }

        @Override
        public String call() throws Exception {
            RouterActionResult result = mObject == null ? mAction.invoke(mContext, mRequestData) : mAction.invoke(mContext, mRequestData, mObject);
            if (result != null) {
                mResponse.mObject = result.getObject();
                Logger.d(TAG, "Process:" + mProcessName + " Local async end: " + System.currentTimeMillis());
                return result.toString();
            } else
                return "";
        }
    }

    private class WideTask implements Callable<String> {

        private String mDomain;
        private String mRequestString;

        public WideTask(String domain, String requestString) {
            this.mDomain = domain;
            this.mRequestString = requestString;
        }

        @Override
        public String call() throws Exception {
            Logger.d(TAG, "WideTask:call() -> Process:" + mProcessName + " Wide async start: " + System.currentTimeMillis());
            Logger.d(TAG, "call: String result = mWideRouterAIDL.route(mDomain, mRequestString);");
            String result = mWideRouterAIDL.route(mDomain, mRequestString);
            Logger.d(TAG, "WideTask:call() -> Process:" + mProcessName + " Wide async end: " + System.currentTimeMillis());
            return result;
        }
    }

    private class ConnectWideTask implements Callable<String> {
        private RouterResponse mResponse;
        private String mDomain;
        private String mRequestString;

        public ConnectWideTask(RouterResponse routerResponse, String domain, String requestString) {
            this.mResponse = routerResponse;
            this.mDomain = domain;
            this.mRequestString = requestString;
        }

        @Override
        public String call() throws Exception {
            Logger.d(TAG, "ConnectWideTask : call() -> Process:" + mProcessName + " Bind wide router start: " + System.currentTimeMillis());
            bindWideRouter();
            int time = 0;
            while (true) {
                if (null == mWideRouterAIDL) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    time++;
                } else {
                    break;
                }
                if (time >= 600) {
                    RouterErrorAction defaultNotFoundAction = new RouterErrorAction(true, RouterActionResult.CODE_CANNOT_BIND_WIDE, "Bind wide router time out. Can not bind wide router.");
                    RouterActionResult result = defaultNotFoundAction.invoke(mApplication, new HashMap<String, String>());
                    if (result != null) {
                        mResponse.mResultString = result.toString();
                        return result.toString();
                    } else
                        return "";
                }
            }
            Logger.d(TAG, "Process:" + mProcessName + " Bind wide router end: " + System.currentTimeMillis());
            String result = mWideRouterAIDL.route(mDomain, mRequestString);
            Logger.d(TAG, "Process:" + mProcessName + " Wide async end: " + System.currentTimeMillis());
            return result;
        }
    }

    /**
     * 注解支持
     *
     * @param service
     * @param <T>
     * @return
     */
    public <T> T create(final Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public String invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String domain = "";
                String provider = "";
                String action = "";
                HashMap<Object, Object> serialParams = null;
                CombinationUri combinationUri = method.getAnnotation(CombinationUri.class);
                if (combinationUri != null) {
                    domain = combinationUri.domain();
                    provider = combinationUri.provider();
                    action = combinationUri.action();
                } else {
                    Logger.e(TAG, "combinationUri is null");
                }
                Annotation[][] parameter = method.getParameterAnnotations();
                if (parameter != null) {
                    serialParams = new HashMap<>();
                    for (int i = 0; i < parameter.length; i++) {
                        Annotation[] annotations = parameter[i];
                        if (annotations == null || annotations.length == 0)
                            continue;
                        Annotation annotation = annotations[0];
                        if (annotation instanceof IntentExtrasParam) {
                            IntentExtrasParam intentExtrasParam = (IntentExtrasParam) annotation;
                            serialParams.put(intentExtrasParam.value(), args[i]);
                        }
                    }
                }
                //get the first params
                Context context = RouterApplication.getMaApplication();
                if (args != null && args.length != 0) {
                    if (args[0] instanceof Activity) {
                        context = (Context) args[0];
                    } else if (args[0] instanceof Service) {
                        context = (Context) args[0];
                    }
                }
                RouterRequest request = RouterRequest.obtain(context).domain(domain)
                        .provider(provider).action(action);
                if (serialParams != null && !serialParams.isEmpty()) {
                    Iterator<Map.Entry<Object, Object>> iterator = serialParams.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Object, Object> next = iterator.next();
                        request.data(next.getKey().toString(), next.getValue().toString());
                    }

                }
                System.out.println("代理方法：" + request.toString());
                RouterResponse response = route(context, request);
                System.out.println("代理方法：response.isAsync() = " + response.isAsync());


                return response.get();
            }
        });

    }
}
