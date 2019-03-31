package com.example.tufengyi.manlife.bean;

public class Flag {


    //这部分等后台完成

    private String ID;
    private long stamp;
    private String date;
    private String updates;
    private int comments;
    private int likes;

    public Flag(){}
    public Flag(String ID,String date,String updates, int comments, int likes){
        this.ID = ID;
        this.date = date;
        this.updates = updates;
        this.comments = comments;
        this.likes = likes;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUpdates() {
        return updates;
    }

    public void setUpdates(String updates) {
        this.updates = updates;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
