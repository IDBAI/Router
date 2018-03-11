package com.idba.icore.router;

import android.content.Context;
import android.text.TextUtils;

import com.idba.icore.ProcessConfig;
import com.idba.icore.tools.Logger;
import com.idba.icore.tools.ProcessUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 17:59
 **/
public class RouterRequest {
    private static final String TAG = "RouterRequest";
    private static volatile String DEFAULT_PROCESS = "";
    private String from;
    private String domain;
    private String provider;
    //回调标志位,暂时不需要启用
    private String callBackTag;
    private String action;
    private HashMap<String, String> data;
    private Object object;
    AtomicBoolean isIdle = new AtomicBoolean(true);


    private static final int length = 64;
    private static AtomicInteger sIndex = new AtomicInteger(0);
    private static final int RESET_NUM = 1000;
    private static volatile RouterRequest[] table = new RouterRequest[length];

    static {
        for (int i = 0; i < length; i++) {
            table[i] = new RouterRequest();
        }
    }

    private RouterRequest() {
        this.from = DEFAULT_PROCESS;
        this.domain = DEFAULT_PROCESS;
        this.provider = "";
        this.action = "";
        this.callBackTag = "";
        this.data = new HashMap<>();
    }


    private RouterRequest(Context context) {
        this.from = getProcess(context);
        this.domain = getProcess(context);
        this.provider = "";
        this.action = "";
        this.callBackTag = "";
        this.data = new HashMap<>();
    }

//    public RouterRequest callBackTag(String callBackTag) {
//        this.callBackTag = callBackTag;
//        return this;
//    }

    public String getCallBackTag() {
        return callBackTag;
    }

    private RouterRequest(Builder builder) {
        from = builder.mFrom;
        domain = builder.mDomain;
        provider = builder.mProvider;
        callBackTag = builder.mCallBackTag;
        action = builder.mAction;
        data = builder.mData;
        object = builder.mObject;
        isIdle = builder.mIsIdle;
    }

    public String getFrom() {
        return from;
    }

    public String getDomain() {
        return domain;
    }

    public String getProvider() {
        return provider;
    }

    public String getAction() {
        return action;
    }

    public HashMap<String, String> getData() {
        return data;
    }


    public Object getAndClearObject() {
        Object temp = object;
        object = null;
        return temp;

    }

    private static String getProcess(Context context) {
        if (TextUtils.isEmpty(DEFAULT_PROCESS) || ProcessConfig.UNKNOWN_PROCESS_NAME.equals(DEFAULT_PROCESS)) {
            DEFAULT_PROCESS = ProcessUtil.getProcessName(context, ProcessUtil.getMyProcessId());
        }
        return DEFAULT_PROCESS;
    }

    @Override
    public String toString() {
        //Here remove Gson to save about 10ms.
        //String result = new Gson().toJson(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from", from);
            jsonObject.put("domain", domain);
            jsonObject.put("provider", provider);
            jsonObject.put("action", action);
            jsonObject.put("callBackTag", callBackTag);
            try {
                JSONObject jsonData = new JSONObject();
                Set<Map.Entry<String, String>> entries = data.entrySet();
                if (entries != null)
                    for (Map.Entry<String, String> entry : entries) {
                        jsonData.put(entry.getKey(), entry.getValue());
                    }
                jsonObject.put("data", jsonData);
            } catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("data", "{}");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public RouterRequest json(String requestJsonString) {
        //Here remove Gson to save about 10ms.
        //RouterRequest routerRequest = new Gson().fromJson(requestJsonString, RouterRequest.class);
        try {
            JSONObject jsonObject = new JSONObject(requestJsonString);
            this.from = jsonObject.getString("from");
            this.domain = jsonObject.getString("domain");
            this.provider = jsonObject.getString("provider");
            this.callBackTag = jsonObject.getString("callBackTag");
            this.action = jsonObject.getString("action");
            try {
                JSONObject jsonData = new JSONObject(jsonObject.getString("data"));
                Iterator it = jsonData.keys();
                while (it.hasNext()) {
                    String key = String.valueOf(it.next());
                    String value = (String) jsonData.get(key);
                    this.data.put(key, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.data = new HashMap<>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public RouterRequest url(String url) {
        int questIndex = url.indexOf('?');
        String[] urls = url.split("\\?");
        if (urls.length != 1 && urls.length != 2) {
            Logger.e(TAG, "The url is illegal.");
            return this;
        }
        String[] targets = urls[0].split("/");
        if (targets.length == 3) {
            this.domain = targets[0];
            this.provider = targets[1];
            this.action = targets[2];
        } else {
            Logger.e(TAG, "The url is illegal.");
            return this;
        }
        //Add params
        if (questIndex != -1) {
            String queryString = urls[1];
            if (queryString != null && queryString.length() > 0) {
                int ampersandIndex, lastAmpersandIndex = 0;
                String subStr, key, value;
                String[] paramPair, values, newValues;
                do {
                    ampersandIndex = queryString.indexOf('&', lastAmpersandIndex) + 1;
                    if (ampersandIndex > 0) {
                        subStr = queryString.substring(lastAmpersandIndex, ampersandIndex - 1);
                        lastAmpersandIndex = ampersandIndex;
                    } else {
                        subStr = queryString.substring(lastAmpersandIndex);
                    }
                    paramPair = subStr.split("=");
                    key = paramPair[0];
                    value = paramPair.length == 1 ? "" : paramPair[1];
                    try {
                        value = URLDecoder.decode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    data.put(key, value);
                } while (ampersandIndex > 0);
            }
        }
        return this;
    }

    public RouterRequest domain(String domain) {
        this.domain = domain;
        return this;
    }


    public RouterRequest provider(String provider) {
        this.provider = provider;
        return this;
    }


    public RouterRequest action(String action) {
        this.action = action;
        return this;
    }


    public RouterRequest data(String key, String data) {
        this.data.put(key, data);
        return this;
    }

    public RouterRequest object(Object object) {
        this.object = object;
        return this;
    }

    public static RouterRequest obtain(Context context) {
        return obtain(context, 0);
    }

    private static RouterRequest obtain(Context context, int retryTime) {
        int index = sIndex.getAndIncrement();
        if (index > RESET_NUM) {
            sIndex.compareAndSet(index, 0);
            if (index > RESET_NUM * 2) {
                sIndex.set(0);
            }
        }
        int num = index & (length - 1);
        RouterRequest target = table[num];
        if (target.isIdle.compareAndSet(true, false)) {
            target.from = getProcess(context);
            target.domain = getProcess(context);
            target.provider = "";
            target.action = "";
            target.callBackTag = "";
            target.data.clear();
            return target;
        } else {
            if (retryTime < 8) {
                return obtain(context, retryTime++);
            } else {
                return new RouterRequest(context);
            }

        }
    }

    @Deprecated
    public static class Builder {
        private String mFrom;
        private String mDomain;
        private String mProvider;
        private String mAction;
        private HashMap<String, String> mData;
        private String mCallBackTag;
        private Object mObject;
        private AtomicBoolean mIsIdle;


        public Builder(Context context) {
            mFrom = getProcess(context);
            mDomain = getProcess(context);
            mProvider = "";
            mCallBackTag = "";
            mAction = "";
            mData = new HashMap<>();
            mIsIdle = new AtomicBoolean(true);
        }

        public Builder() {
        }

        public Builder json(String requestJsonString) {
            //Here remove Gson to save about 10ms.
            //RouterRequest routerRequest = new Gson().fromJson(requestJsonString, RouterRequest.class);
            try {
                JSONObject jsonObject = new JSONObject(requestJsonString);
                this.mFrom = jsonObject.getString("from");
                this.mDomain = jsonObject.getString("domain");
                this.mProvider = jsonObject.getString("provider");
                this.mAction = jsonObject.getString("action");
                this.mCallBackTag = jsonObject.getString("callBackTag");

                try {
                    JSONObject jsonData = new JSONObject(jsonObject.getString("data"));
                    Iterator it = jsonData.keys();
                    while (it.hasNext()) {
                        String key = String.valueOf(it.next());
                        String value = (String) jsonData.get(key);
                        this.mData.put(key, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.mData = new HashMap<>();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder url(String url) {
            int questIndex = url.indexOf('?');
            String[] urls = url.split("\\?");
            if (urls.length != 1 && urls.length != 2) {
                Logger.e(TAG, "The url is illegal.");
                return this;
            }
            String[] targets = urls[0].split("/");
            if (targets.length == 3) {
                this.mDomain = targets[0];
                this.mProvider = targets[1];
                this.mAction = targets[2];
            } else {
                Logger.e(TAG, "The url is illegal.");
                return this;
            }
            //Add params
            if (questIndex != -1) {
                String queryString = urls[1];
                if (queryString != null && queryString.length() > 0) {
                    int ampersandIndex, lastAmpersandIndex = 0;
                    String subStr, key, value;
                    String[] paramPair, values, newValues;
                    do {
                        ampersandIndex = queryString.indexOf('&', lastAmpersandIndex) + 1;
                        if (ampersandIndex > 0) {
                            subStr = queryString.substring(lastAmpersandIndex, ampersandIndex - 1);
                            lastAmpersandIndex = ampersandIndex;
                        } else {
                            subStr = queryString.substring(lastAmpersandIndex);
                        }
                        paramPair = subStr.split("=");
                        key = paramPair[0];
                        value = paramPair.length == 1 ? "" : paramPair[1];
                        try {
                            value = URLDecoder.decode(value, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mData.put(key, value);
                    } while (ampersandIndex > 0);
                }
            }
            return this;
        }

        public Builder domain(String domain) {
            this.mDomain = domain;
            return this;
        }

        public String getCallBackTag() {
            return mCallBackTag;
        }

        public Builder setCallBackTag(String callBackTag) {
            this.mCallBackTag = callBackTag;
            return this;

        }

        public Builder provider(String provider) {
            this.mProvider = provider;
            return this;
        }


        public Builder action(String action) {
            this.mAction = action;
            return this;
        }

        public Builder data(HashMap<String, String> val) {
            mData = val;
            return this;
        }

        public Builder object(Object val) {
            mObject = val;
            return this;
        }

        public Builder isIdle(AtomicBoolean val) {
            mIsIdle = val;
            return this;
        }


        public Builder data(String key, String data) {
            this.mData.put(key, data);
            return this;
        }

        public RouterRequest build() {
            return new RouterRequest(this);
        }

        public Builder from(String val) {
            mFrom = val;
            return this;
        }
    }

}
