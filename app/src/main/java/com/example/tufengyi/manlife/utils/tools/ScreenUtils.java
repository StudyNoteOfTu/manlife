package com.example.tufengyi.manlife.utils.tools;

import android.content.Context;

public class ScreenUtils {

    public static int getScreenHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

}
