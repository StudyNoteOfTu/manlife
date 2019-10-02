package com.example.tufengyi.manlife;

import android.app.Application;
import android.content.Context;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;

import com.example.tufengyi.manlife.bean.CommentOfFlag;
import com.example.tufengyi.manlife.bean.DailyAssignment;
import com.example.tufengyi.manlife.bean.Flag;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    public static Context context;

    private final List<DailyAssignment> dailyAssignments = new ArrayList<>();

    private final List<CommentOfFlag> comments = new ArrayList<>();

    private final List<String> likes = new ArrayList<>();

    public static final String APP_ID = "wx7ef876fe1742f5df";
    public static final String secret = "7842d96f93d4116b247a6d38c8824c29";

    public static final Thread.UncaughtExceptionHandler sUncaughtExceptionHandler = Thread
            .getDefaultUncaughtExceptionHandler();

    //用sharedpreference保存，这里不再用
    /* 存用户信息 */
    public static String userName;//用户昵称
    public static String userId;//用户id
    public static String userImg;//用户头像url
    public static String wx_id;
    //    private static String token = "";

    private static MyApplication instance;

    public static MyApplication getInstance(){
        return instance;
    }

    public void setLikes(List<String> likes){
        this.likes.clear();
        this.likes.addAll(likes);
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setComments(List<CommentOfFlag> comments){
        this.comments.clear();
        this.comments.addAll(comments);
    }

    public List<CommentOfFlag> getComments(){
        return comments;
    }

    public void setList(List<DailyAssignment> dailyAssignments){
        this.dailyAssignments.clear();
        this.dailyAssignments.addAll(dailyAssignments);
    }

    public List<DailyAssignment> getList(){
        return dailyAssignments;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        instance  = this;
        context = getApplicationContext();

        //在application中 尽量不要有过多的操作，可以放到需要埋点的操作开始之前进行初始化
        UMConfigure.init(this,"5cf21e880cafb22e76000b18","Umeng",UMConfigure.DEVICE_TYPE_PHONE,"");
        //MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setDebugMode(true);

    }
}
