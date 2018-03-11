// IRouterAIDL.aidl
package com.idba.icore;

// Declare any non-default types here with import statements
import com.idba.icore.IRouterCallBackAIDL;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/

interface IWideRouterAIDL {
    boolean checkResponseAsync(String domain,String routerRequset);
    String route(String domain,String routerRequest);
    boolean stopRouter(String domain);
    void registListener(String callbacktag,IRouterCallBackAIDL callback);
    void unRegistListener(String callbacktag,IRouterCallBackAIDL callback);
    void finishAllRegist(String domain);
}
