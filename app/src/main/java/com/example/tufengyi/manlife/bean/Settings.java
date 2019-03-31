package com.example.tufengyi.manlife.bean;

public class Settings {
    long id;
    int up;

    public Settings(){}

    public Settings(long id,int up){
        this.id = id;
        this.up = up;
    }

    public Settings(int up){
        this.up = up;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }
}
