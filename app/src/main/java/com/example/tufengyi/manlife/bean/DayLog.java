package com.example.tufengyi.manlife.bean;

public class DayLog extends PunchedAss {
    private String id;
//    private String date;//time  yyyy-MM-dd -- 用来判断recyclerView头部
    private long stamp;//时间戳 -- 用来排序
    private String content;


    public DayLog(){};

    public DayLog(String id, String date, long stamp, String content) {
        setTitle("日志");
        setIcon(9);
        setDate(date);
        this.id = id;
//        this.date = date;
        this.stamp = stamp;
        this.content = content;
    }

    public DayLog(String id, String date, String content){
        setTitle("日志");
        setIcon(9);
        this.id = id;
        setDate(date);
//        this.date = date;
        this.content = content;
    }

    public DayLog(String date, String content){
        setTitle("日志");
        setIcon(9);
        setDate(date);
//        this.date = date;
        this.content = content;
    }

//    public long getStamp() {
//        return stamp;
//    }
//
//    public void setStamp(long stamp) {
//        this.stamp = stamp;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getStamp() {
        return stamp;
    }

    public void setStamp(long stamp) {
        this.stamp = stamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
