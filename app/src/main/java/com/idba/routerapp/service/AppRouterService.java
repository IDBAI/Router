package com.idba.routerapp.service;

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
 * created 2017-10-16 11:20
 * describe:
 **/
public interface AppRouterService {


    /**
     * 控制音乐模块的音乐播放，并且传递一个musicId
     * @param context
     * @param musicId
     * @return
     */

    @CombinationUri(domain =DomainHelper.DOMAIN_MUSIC,provider = ProviderHelper.PROVIDER_MUSIC,action =  ActionHelper.Action_Music.MUSIC_ACTION_PLAY)
    String startMusicModulePlay(Context context,@IntentExtrasParam("musicId") String musicId);


    /**
     * 跳转至图片模块的Activity，以 startActivityForResult 的方式跳转
     * （note：暂时仅支持在相同进程中 实现 startActivityForResult 方式跳转）
     * @param context
     * @param PictureID
     * @param requestCode
     * @return
     */
    @CombinationUri(domain =DomainHelper.DOMAIN_PIC,provider = ProviderHelper.PROVIDER_PIC ,action = ActionHelper.Action_Pic.PIC_ACTION_MAIN)
    String startPicModuleActivityForResult(Context context,@IntentExtrasParam("PictureID") String PictureID,@IntentExtrasParam("requestCode") String requestCode);

    /**
     * 跳转至图片模块的Activity，以 startActivity 的方式跳转
     * @param context
     * @param PictureID
     * @return
     */
    @CombinationUri(domain = DomainHelper.DOMAIN_PIC,provider = ProviderHelper.PROVIDER_PIC,action =ActionHelper.Action_Pic.PIC_ACTION_MAIN)
    String startPicModuleActivity(Context context,@IntentExtrasParam("PictureID") String PictureID);

    /**
     * 跳转至 大内存模块，另一个进程 的Activity，以 startActivity 的方式跳转
     * @param context
     * @param memorySize
     * @return
     */
    @CombinationUri(domain = DomainHelper.DOMAIN_MEMORY,provider = ProviderHelper.PROVIDER_MEMORY,action = ActionHelper.Action_Memory.MEMORY_ACTION_STARTMAIN)
    String startToBigMemory(Context context,@IntentExtrasParam("memorySize") String memorySize);
}
