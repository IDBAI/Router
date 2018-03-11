package com.idba.bigmemorymodule.provider;

import com.idba.bigmemorymodule.action.StartBigemoryMainActAction;
import com.idba.common.modulehelper.ActionHelper;
import com.idba.icore.provider.RouterProvider;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 16:05
 **/
public class BigMemoryProvider extends RouterProvider {
    private static final String TAG = "BigMemoryProvider";
    @Override
    protected void registerActions() {
        registerAction(ActionHelper.Action_Memory.MEMORY_ACTION_STARTMAIN,new StartBigemoryMainActAction());

    }
}
