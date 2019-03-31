package com.example.tufengyi.manlife.bean;

public class Comments {
    long id;
    String comment;
    String ID;
    //int icon;

    public Comments(){}

    public Comments(long id,String comment,String ID){
        this.id =id;
        this.comment = comment;
        this.ID = ID;
    }

    public Comments(String comment,String ID){
        this.comment = comment;
        this.ID = ID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
