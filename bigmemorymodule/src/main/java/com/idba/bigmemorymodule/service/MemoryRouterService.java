package com.idba.bigmemorymodule.service;

import android.content.Context;

import com.idba.annotation.router.CombinationUri;
import com.idba.annotation.router.IntentExtrasParam;
import com.idba.common.modulehelper.ActionHelper;
import com.idba.common.modulehelper.DomainHelper;
import com.idba.common.modulehelper.ProviderHelper;

/**
 * Router
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-16 15:57
 * describe:
 **/
public interface MemoryRouterService {


    /**
     * 控制音乐模块开始音乐播放
     * @param context
     * @return
     */
    @CombinationUri(domain = DomainHelper.DOMAIN_MUSIC,provider = ProviderHelper.PROVIDER_MUSIC,action = ActionHelper.Action_Music.MUSIC_ACTION_PLAY)
    String startPlayMusic(Context context);

    /**
     * 控制音乐模块停止音乐播放
     * @param context
     * @return
     */
    @CombinationUri(domain = DomainHelper.DOMAIN_MUSIC,provider = ProviderHelper.PROVIDER_MUSIC,action = ActionHelper.Action_Music.MUSIC_ACTION_STOP)
    String stopPlayMusic(Context context);


    /**
     * 跨进程，跳转到图片模块的一个页面
     * @param context
     * @param PictureID
     * @return
     */
    @CombinationUri(domain = DomainHelper.DOMAIN_PIC,provider = ProviderHelper.PROVIDER_PIC,action = ActionHelper.Action_Pic.PIC_ACTION_MAIN)
    String startToPicMain(Context context, @IntentExtrasParam("PictureID") String PictureID);
}
