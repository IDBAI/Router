package com.idba.icore.router;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.idba.icore.ILocalRouterAIDL;
import com.idba.icore.IRouterCallBackAIDL;
import com.idba.icore.RouterActionResult;
import com.idba.icore.tools.Logger;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/

public class LocalRouterConnectService extends Service {
    private static final String TAG = "LocalRouterConnectServi";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "onStartCommand: ");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.e(TAG,"onBind: ");
        return stub;
    }

    ILocalRouterAIDL.Stub stub = new ILocalRouterAIDL.Stub() {

        @Override
        public boolean checkResponseAsync(String routerRequest) throws RemoteException {
            return LocalRouter.getInstance().
                    answerWiderAsync(new RouterRequest
                            .Builder(getApplicationContext())
                            .json(routerRequest)
                            .build());
        }

        @Override
        public String route(String routerRequest) {
            try {
                LocalRouter localRouter = LocalRouter.getInstance();
                RouterRequest routerRequest1 = new RouterRequest
                        .Builder(getApplicationContext())
                        .json(routerRequest)
                        .build();
                RouterResponse routerResponse = localRouter.route(LocalRouterConnectService.this,routerRequest1);
                return routerResponse.get();
            } catch (Exception e) {
                e.printStackTrace();
                return new RouterActionResult.Builder().msg(e.getMessage()).build().toString();
            }
        }

        @Override
        public boolean stopWideRouter() throws RemoteException {
            LocalRouter
                    .getInstance()
                    .disconnectWideRouter();
            return true;
        }

        @Override
        public void connectWideRouter() throws RemoteException {
            LocalRouter
                    .getInstance()
                    .bindWideRouter();
        }

        @Override
        public void registListener(String callbackTag,IRouterCallBackAIDL callback) throws RemoteException {
            LocalRouter
                    .getInstance().addOtherModuleRemoteListener(callbackTag,callback);

        }

        @Override
        public void unRegistListener(String domain,IRouterCallBackAIDL callback) throws RemoteException {
            Logger.d(TAG, "unRegistListener: ");
            LocalRouter
                    .getInstance().removeOtherModuleRemoteListener(domain,callback);
        }
    };
}
