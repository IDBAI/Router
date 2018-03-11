// IRouterCallBackAIDL.aidl
package com.idba.icore;

// Declare any non-default types here with import statements


/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/

interface IRouterCallBackAIDL {

    void callback(String fromModule,String event,boolean boo,String msg,inout Bundle bundle);
}
