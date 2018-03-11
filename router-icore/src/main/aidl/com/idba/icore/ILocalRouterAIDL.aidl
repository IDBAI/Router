package com.idba.icore;
import com.idba.icore.IRouterCallBackAIDL;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/

interface ILocalRouterAIDL {
    boolean checkResponseAsync(String routerRequset);
    String route(String routerRequest);
    boolean stopWideRouter();
    void connectWideRouter();
     void registListener(String callbacktag,IRouterCallBackAIDL callback);
     void unRegistListener(String callbacktag,IRouterCallBackAIDL callback);
}
