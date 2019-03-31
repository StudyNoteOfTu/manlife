package com.example.tufengyi.manlife.utils;


import android.util.Log;

import com.example.tufengyi.manlife.MyApplication;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

//处理重定向的拦截器
public class RedirectInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Request request = chain.request();
        Response response = chain.proceed(request);
        int code = response.code();
        if (code == 307) {
            //获取重定向的地址
            String location = response.headers().get("Location");
            Log.e("TestLog", "location = " + location);
            //重新构建请求
            Request newRequest = request.newBuilder()
                    .url("https://slow.hustonline.net"+location)
                    .build();
            response = chain.proceed(newRequest);
        }
        return response;
    }
}
