package com.example.tufengyi.manlife.bean;

public class SubTitle {
    //要注意的是这里的数字一定是两位数的
    private String date;

    public SubTitle(){}

    public SubTitle(String date){
        super();
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
