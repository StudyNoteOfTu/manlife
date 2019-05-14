package com.example.tufengyi.manlife;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.tufengyi.manlife.bean.CommentOfFlag;
import com.example.tufengyi.manlife.bean.DailyAssignment;
import com.example.tufengyi.manlife.bean.Flag;

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
        Log.d("TestFlag","comment size"+this.comments.size());
    }

    public List<CommentOfFlag> getComments(){
        return comments;
    }

    public void setList(List<DailyAssignment> dailyAssignments){
        this.dailyAssignments.clear();
        this.dailyAssignments.addAll(dailyAssignments);
        Log.d("TestPreview","list size"+this.dailyAssignments.size());
    }

    public List<DailyAssignment> getList(){
        return dailyAssignments;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        instance  = this;
        context = getApplicationContext();
    }
}
