package com.taobao.myproject.util;

import com.taobao.myproject.application.MyApplication;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {

    private static OkHttpClient okHttpClient;
    public static String getSyncToInStream(String url) throws Exception {
        Request request = buildGetRequest(url);
        String result = syncExecuteToJson(request);
        return result;
    }

    private static Request buildGetRequest(String url) {
        return new Request.Builder().url(url).build();
    }

    private static String syncExecuteToJson(Request request) throws Exception {
        try {
            Response response = getOkHttpClient(null, null)
                    .newCall(request)
                    .execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                return result;
            } else {
            }
        } catch (Exception e) {
            throw e;
        }
        return "";
    }

    public static OkHttpClient getOkHttpClient(Interceptor interceptor, InputStream... certificates) {
        if (okHttpClient == null) {
            File sdcache = MyApplication.context.getExternalCacheDir();
            int cacheSize = 10 * 1024 * 1024;
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .cache(new Cache(sdcache.getAbsoluteFile(), cacheSize));
            if (interceptor != null) {
                builder.addInterceptor(interceptor);
            }
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }
}
