package com.idba.routerapp.provider;

import com.idba.common.modulehelper.ActionHelper;
import com.idba.icore.provider.RouterProvider;
import com.idba.routerapp.action.AppMainAction;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-09 14:13
 * describe:
 **/
public class AppProvider extends RouterProvider {
    @Override
    protected void registerActions() {
        registerAction(ActionHelper.Action_App.APP_ACTION_MAIN, new AppMainAction());
    }
}
