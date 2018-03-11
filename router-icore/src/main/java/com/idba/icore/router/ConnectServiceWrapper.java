package com.idba.icore.router;



/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/

public class ConnectServiceWrapper {
    public Class<? extends LocalRouterConnectService> targetClass = null;

    public ConnectServiceWrapper( Class<? extends LocalRouterConnectService> logicClass) {
        this.targetClass = logicClass;
    }
}
