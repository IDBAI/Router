package com.idba.icore.router;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.idba.icore.IRouterCallBackAIDL;
import com.idba.icore.IWideRouterAIDL;
import com.idba.icore.RouterActionResult;
import com.idba.icore.RouterApplication;
import com.idba.icore.tools.Logger;


/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/
public final class WideRouterConnectService extends Service {
    private static final String TAG = "WideRouterConService";

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "onCreate: ");
        if (!(getApplication() instanceof RouterApplication)) {
            throw new RuntimeException("Please check your AndroidManifest.xml and make sure the application is instance of RouterApplication.");
        }
    }

    @Override
    public void onDestroy() {
        Logger.e(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "onStartCommand: ");

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.d(TAG, "onBind: ");
        String domain = intent.getStringExtra("domain");
        if (WideRouter.getInstance(RouterApplication.getMaApplication()).mIsStopping) {
            Logger.e(TAG, "Bind error: The wide router is stopping.");
            return null;
        }
        if (domain != null && domain.length() > 0) {
            boolean hasRegistered = WideRouter.getInstance(RouterApplication.getMaApplication()).checkLocalRouterHasRegistered(domain);
            if (!hasRegistered) {
                Logger.e(TAG, "Bind error: The local router of process " + domain + " is not bidirectional." +
                        "\nPlease create a Service extend LocalRouterConnectService then register it in AndroidManifest.xml and the initializeAllProcessRouter method of RouterApplication." +
                        "\nFor example:" +
                        "\n<service android:name=\"XXXConnectService\" android:process=\"your process name\"/>" +
                        "\nWideRouter.registerLocalRouter(\"your process name\",XXXConnectService.class);");
                return null;
            }
            WideRouter.getInstance(RouterApplication.getMaApplication()).bindLocalRouter(domain, true);
        } else {
            Logger.e(TAG, "Bind error: Intent do not have \"domain\" extra!");
            return null;
        }
        return stub;
    }

    IWideRouterAIDL.Stub stub = new IWideRouterAIDL.Stub() {

        @Override
        public boolean checkResponseAsync(String domain, String routerRequest) throws RemoteException {
            return
                    WideRouter.getInstance(RouterApplication.getMaApplication())
                            .answerLocalAsync(domain, routerRequest);
        }


        @Override
        public String route(String domain, String routerRequest) {
            try {
                return WideRouter.getInstance(RouterApplication.getMaApplication())
                        .route(domain, routerRequest)
                        .mResultString;
            } catch (Exception e) {
                e.printStackTrace();
                return new RouterActionResult.Builder()
                        .code(RouterActionResult.CODE_ERROR)
                        .msg(e.getMessage())
                        .build()
                        .toString();
            }
        }

        @Override
        public boolean stopRouter(String domain) throws RemoteException {
            return WideRouter.getInstance(RouterApplication.getMaApplication())
                    .disconnectLocalRouter(domain);
        }

        @Override
        public void registListener(String callbacktag, IRouterCallBackAIDL callback) throws RemoteException {
            Logger.d(TAG, "registListener: callbacktag = " + callbacktag);
            WideRouter.getInstance(RouterApplication.getMaApplication()).registenerCallBackFromLocalRouter(callbacktag, callback);
        }

        @Override
        public void unRegistListener(String domain, IRouterCallBackAIDL callback) throws RemoteException {
            Logger.d(TAG, "unRegistListener: ");
            WideRouter.getInstance(RouterApplication.getMaApplication()).unRegistener(domain, callback);
        }
        @Override
        public void finishAllRegist(String domain) throws RemoteException {
            Logger.d(TAG, "finishAllRegist 局域路由注册完callback，通知广域路由: domain = "+domain);
            WideRouter.getInstance(RouterApplication.getMaApplication()).finishAllRegist(domain);
        }

    };
}
