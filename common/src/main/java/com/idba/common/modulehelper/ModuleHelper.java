package com.idba.common.modulehelper;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-09 15:56
 * describe: 公共模块全局定义文件，定义一些event，使用String ，达到见名知意的效果
 **/
public class ModuleHelper {


    //定义模块标识符，规则为module_模块名
    public interface Module {
        String MODULE_APP = "module_app";
        String MODULE_BIG_MEMORY = "module_big_memory";
        String MODULE_MUSIC = "module_music";
        String MODULE_PIC = "module_pic";
    }


    //定义Music的event
    public interface EventMusic {

        String MUSIC_STOP = "music_stop";
        String MUSIC_START = "music_start";
        String MUSIC_PLAYING = "music_playing";
        String MUSIC_NEW_PLAYING = "music_new_playing";
    }


    public interface EventPic {
        String PARSE_LIST = "parse_list";
    }

    public interface EventMemory {
        String PARSE_LIST = "parse_list";
        String PARSE_MULITBEAN = "parse_mulitbean";
        String TEST_CALLBACK = "test_callback";
    }
}
