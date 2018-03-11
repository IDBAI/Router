package com.idba.musicmodule.service;

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
 * created 2017-10-16 16:12
 * describe:
 **/
public interface MusicRouterService {


    /**
     * jump to pic module's Activity
     * @param context
     * @param PictureID
     * @return
     */
    @CombinationUri(domain = DomainHelper.DOMAIN_PIC,provider = ProviderHelper.PROVIDER_PIC,action = ActionHelper.Action_Pic.PIC_ACTION_MAIN)
    String startToPicMainActivity(Context context, @IntentExtrasParam("PictureID")String PictureID);


    /**
     * jump to memory module's Activity
     * @param context
     * @param memorySize
     * @return
     */
    @CombinationUri(domain = DomainHelper.DOMAIN_MEMORY,provider = ProviderHelper.PROVIDER_MEMORY,action = ActionHelper.Action_Memory.MEMORY_ACTION_STARTMAIN)
    String startToMemoryMainActivity(Context context,@IntentExtrasParam("memorySize") String memorySize);

}
