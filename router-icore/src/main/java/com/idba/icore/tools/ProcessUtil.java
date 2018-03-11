package com.idba.icore.tools;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static com.idba.icore.ProcessConfig.UNKNOWN_PROCESS_NAME;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/
public class ProcessUtil {

    private static final String TAG = "ProcessUtil";

    public static int getMyProcessId() {
        return android.os.Process.myPid();
    }

    public static String getProcessName(int pid) {
        String processName = UNKNOWN_PROCESS_NAME;
        try {
            File file = new File("/proc/" + pid + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }
        }
        return UNKNOWN_PROCESS_NAME;
    }

    public static String getProcessName(Context context, int pid) {
        String processName = getProcessName(pid);
        if(UNKNOWN_PROCESS_NAME.equals(processName)){
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
            if (runningApps == null) {
                return UNKNOWN_PROCESS_NAME;
            }
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }else{
            return processName;
        }
        return UNKNOWN_PROCESS_NAME;
    }


    public static boolean isProessRunning(Context context, String proessName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info : lists){
            if(info.processName.equals(proessName)){
                isRunning = true;
            }
        }
        return isRunning;
    }


    /**
     * 有些时候我们使用Service的时需要采用隐私启动的方式，
     * 但是Android 5.0一出来后，其中有个特性就是Service Intent  must be explitict，
     * 也就是说从Lollipop开始，service服务必须采用显示方式启动。
     * 次方法解决隐式启动的问题，根据自带ACTION的intent ，查找到具体的package和组件名
     *
     * @return
     */
    private static List<Intent> getExplicitIntent(Context context, Intent implicitIntent) {
        List<Intent> result = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfos == null) {
            return result;
        }
        for (ResolveInfo info : resolveInfos) {
            String packageName = info.serviceInfo.packageName;
            if (packageName.equalsIgnoreCase(context.getPackageName())) {
                //排除自己
                continue;
            }
            String className = info.serviceInfo.name;
            ComponentName component = new ComponentName(packageName, className);
            Intent explicitIntent = new Intent(implicitIntent);
            explicitIntent.setComponent(component);
            explicitIntent.addCategory(packageName);
            boolean serviceRunning = isServiceRunning(context, packageName, className);
            if (!serviceRunning) {
                Logger.d(TAG,"拉起的类：" + packageName + " : " + className);
                result.add(explicitIntent);
            }
        }
        return result;
    }

    /**
     * @param context
     * @param pakname     包名
     * @param serviceName 服务的类名，注意是全类名
     * @return
     */
    private static boolean isServiceRunning(Context context, String pakname, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices == null || runningServices.size() == 0)
            return false;
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
            String packageName = serviceInfo.service.getPackageName();
            ComponentName service = serviceInfo.service;
            if (packageName.equalsIgnoreCase(pakname) && service.getClassName().equalsIgnoreCase(serviceName)) {
                Logger.d(TAG,pakname + "/" + serviceName + " service is Running");
                return true;
            }
        }
        return false;
    }



}
