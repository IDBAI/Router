package com.idba.common.modulehelper;

/**
 * Router
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-18 10:45
 * describe: 全局定义不同模块的ACTION，定义规则为模块名_action_具体的action动作，达到见名知意的效果
 **/
public class ActionHelper {


    public interface Action_Pic {
        String PIC_ACTION_MAIN = "pic_action_main";
    }


    public interface Action_App {
        String APP_ACTION_MAIN = "App_action_main";
    }


    public interface Action_Memory {
        String MEMORY_ACTION_STARTMAIN = "memory_action_startmain";
    }

    public interface Action_Music{
        //
        String MUSIC_ACTION_PLAY = "music_action_play";
        String MUSIC_ACTION_STOP = "music_action_stop";
        String MUSIC_ACTION_NEW_PLAY = "music_action_new_play";
    }
}
