package com.example.tufengyi.manlife;

import android.app.Application;

public class MyApplication extends Application {

    public static final String APP_ID = "wx7ef876fe1742f5df";
    public static final String secret = "7842d96f93d4116b247a6d38c8824c29";

    public static final Thread.UncaughtExceptionHandler sUncaughtExceptionHandler = Thread
            .getDefaultUncaughtExceptionHandler();

    //用sharedpreference保存，这里不再用
    /* 存用户信息 */
    public static String userName;//用户昵称
    public static String userId;//用户id
    public static String userImg;//用户头像url
    //    private static String token = "";

    private static MyApplication instance;

    public static MyApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        instance  = this;
    }
}
