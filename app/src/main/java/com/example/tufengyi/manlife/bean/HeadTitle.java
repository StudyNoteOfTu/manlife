package com.example.tufengyi.manlife.bean;

public class HeadTitle {
    //格式yyyy-MM
    private String date;

    public HeadTitle(){}
    public HeadTitle(String date){
        super();
        this.date =date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
