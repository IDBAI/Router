package com.idba.picmodule.provider;

import com.idba.common.modulehelper.ActionHelper;
import com.idba.icore.provider.RouterProvider;
import com.idba.picmodule.action.StartMainActAction;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 19:00
 **/
public class PicProvider extends RouterProvider {
    @Override
    protected void registerActions() {
        registerAction(ActionHelper.Action_Pic.PIC_ACTION_MAIN, new StartMainActAction());
    }
}
