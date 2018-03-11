package com.idba.musicmodule.provider;


import com.idba.common.modulehelper.ActionHelper;
import com.idba.icore.provider.RouterProvider;
import com.idba.musicmodule.action.PlayAction;
import com.idba.musicmodule.action.PlayNewAction;
import com.idba.musicmodule.action.StopAction;


/**
 *
 */
public class MusicProvider extends RouterProvider {
    @Override
    protected void registerActions() {
        registerAction(ActionHelper.Action_Music.MUSIC_ACTION_PLAY, new PlayAction());
        registerAction(ActionHelper.Action_Music.MUSIC_ACTION_STOP, new StopAction());
        registerAction(ActionHelper.Action_Music.MUSIC_ACTION_NEW_PLAY, new PlayNewAction());
    }
}
