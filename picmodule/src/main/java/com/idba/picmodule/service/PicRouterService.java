package com.idba.picmodule.service;

import android.content.Context;

import com.idba.annotation.router.CombinationUri;
import com.idba.common.modulehelper.ActionHelper;
import com.idba.common.modulehelper.DomainHelper;
import com.idba.common.modulehelper.ProviderHelper;

/**
 * Router
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-16 16:22
 * describe:
 **/
public interface PicRouterService {


    /**
     * 跨模块，控制音乐模块开始音乐播放
     * @param context
     * @return
     */
    @CombinationUri(domain = DomainHelper.DOMAIN_MUSIC,provider = ProviderHelper.PROVIDER_MUSIC,action = ActionHelper.Action_Music.MUSIC_ACTION_PLAY)
    String startPlayMusic(Context context);

    /**
     * 跨模块，控制音乐模块停止音乐播放
     * @param context
     * @return
     */
    @CombinationUri(domain = DomainHelper.DOMAIN_MUSIC,provider = ProviderHelper.PROVIDER_MUSIC,action = ActionHelper.Action_Music.MUSIC_ACTION_STOP)
    String stopPlayMusic(Context context);



}
