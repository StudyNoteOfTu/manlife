package com.example.tufengyi.manlife.bean;

import com.example.tufengyi.manlife.utils.tools.DateUtil;

public class CommentOfFlag {
    String id;
    String from_id;
    String content;
    String name;
    String img;
    long time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFullDate(long time){
        return DateUtil.full_stampToDate(time);
    }

    public String getDate(long time){
        return DateUtil.stampToDate(time);
    }
}
