package com.example.tufengyi.manlife.utils.tools;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static long stringToDate(String dateString){
        long re_time = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try{
            d=sdf.parse(dateString+" 00:00:00");
            long l = d.getTime();
            re_time=l;
//            String str = String.valueOf(l);
//            re_time = str.substring(0,10);
        }catch(ParseException e){

        }
        return re_time;
    }

    public static String stampToDate(long timeMillis){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date).substring(0,10);
    }


    public static long full_stringToDate(String dateString){
        long re_time = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try{
            d=sdf.parse(dateString);
            long l = d.getTime();
            re_time=l;
//            String str = String.valueOf(l);
//            re_time = str.substring(0,10);
        }catch(ParseException e){

        }
        return re_time;
    }



    public static String full_stampToDate(long timeMillis){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillis);
        Log.d("TestLog","full_stampToDate"+simpleDateFormat.format(date));
        return simpleDateFormat.format(date);
    }
}
