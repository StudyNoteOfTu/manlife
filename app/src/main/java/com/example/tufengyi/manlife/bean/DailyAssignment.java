package com.example.tufengyi.manlife.bean;

public class DailyAssignment extends PunchedAss{
    long id;
    String objId;
    String date;
    int icon;
    String title;
    int progress;
    String finish;

    public DailyAssignment(){}

    public DailyAssignment(long id,String objId,String date,String title,int icon,String finish,int progress){
        super();
        this.id = id;
        this.objId = objId;
        this.date = date;
        this.icon = icon;
        this.title = title;
        this.finish = finish;
        this.progress = progress;
    }

    public DailyAssignment(String objId,String date,String title,int icon,String finish,int progress){
        super();
        this.objId = objId;
        this.date = date;
        this.icon = icon;
        this.title= title;
        this.progress = progress;
        this.finish = finish;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }
}
