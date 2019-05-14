package com.example.tufengyi.manlife.bean;

import java.util.ArrayList;
import java.util.List;

public class Flag {

    boolean hasChanged = false;

    //这部分等后台完成
    String openid;//用户的id
    String name;//用户名
    String img_url;//头像
    String id;//flag的id
    long time;//时间
    String date;
    String content;//内容
    public List<String> likes;
    public List<CommentOfFlag> comments;

    private boolean canLike = true;

    public boolean isCanLike() {
        return canLike;
    }

    public void setCanLike(boolean canLike) {
        this.canLike = canLike;
    }

    public boolean equalTo(Flag flag){//以flag的id来判断是否是同一个flag
        if(flag == null) return false;
        if(id.equals(flag.getId())){
            return true;
        }else{
            return false;
        }
    }

    public Flag(String openid, String name, String img_url, String id, long time,String date, String content) {
        this.openid = openid;
        this.name = name;
        this.img_url = img_url;
        this.id = id;
        this.time = time;
        this.date = date;
        this.content = content;
        likes = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public Flag(String openid, String id, long time, String date, String content) {
        this.openid = openid;
        this.id = id;
        this.time = time;
        this.date = date;
        this.content = content;
        likes = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public Flag(String id, long time, String date, String content){
        this.id = id;
        this.date = date;
        this.time = time;
        this.content = content;
        likes = new ArrayList<String>();
        comments = new ArrayList<CommentOfFlag>();
    }

    public boolean isHasChanged() {
        return hasChanged;
    }

    public void setHasChanged(boolean hasChanged) {
        this.hasChanged = hasChanged;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes.clear();
        this.likes.addAll(likes);
    }

    public List<CommentOfFlag> getComments() {
        return comments;
    }

    public void setComments(List<CommentOfFlag> comments) {
        this.comments.clear();
        this.comments.addAll(comments);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    //    private String ID;
//    private long stamp;
//    private String date;
//    private String updates;
//    private int comments;
//    private int likes;
//
//    public Flag(){}
//    public Flag(String ID,String date,String updates, int comments, int likes){
//        this.ID = ID;
//        this.date = date;
//        this.updates = updates;
//        this.comments = comments;
//        this.likes = likes;
//    }
//
//
//    public String getID() {
//        return ID;
//    }
//
//    public void setID(String ID) {
//        this.ID = ID;
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public String getUpdates() {
//        return updates;
//    }
//
//    public void setUpdates(String updates) {
//        this.updates = updates;
//    }
//
//    public int getComments() {
//        return comments;
//    }
//
//    public void setComments(int comments) {
//        this.comments = comments;
//    }
//
//    public int getLikes() {
//        return likes;
//    }
//
//    public void setLikes(int likes) {
//        this.likes = likes;
//    }
}
