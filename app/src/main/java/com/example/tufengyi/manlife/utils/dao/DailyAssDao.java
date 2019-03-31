package com.example.tufengyi.manlife.utils.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tufengyi.manlife.bean.DailyAssignment;
import com.example.tufengyi.manlife.bean.Sentence;
import com.example.tufengyi.manlife.utils.helper.DailyAssHelper;
import com.example.tufengyi.manlife.utils.helper.SentenceHelper;

import java.util.ArrayList;
import java.util.List;

public class DailyAssDao {

        //这个可以不需要，后面改成S存储
    private DailyAssHelper helper;

    //构造器
    public DailyAssDao(Context context){
        helper = new DailyAssHelper(context);
    }

    //增加数据
    public void insert(DailyAssignment ass){
        //插入数据
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("objId",ass.getObjId());
        values.put("date",ass.getDate());
        values.put("title",ass.getTitle());
        values.put("icon",ass.getIcon());
        values.put("finish",ass.getFinish());
        values.put("progress",ass.getProgress());

        long id = db.insert("dailyAss",null,values);
        ass.setId(id);
        db.close();
    }

    /*
     * 删除对应id的数据
     */
    public int delete(long id){
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete("dailyAss","_id=?",new String[]{id+""});
        db.close();
        return count;
    }

    /*
     * 更新
     */
    public int update(DailyAssignment ass){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("objId",ass.getObjId());
        values.put("date",ass.getDate());
        values.put("title",ass.getTitle());
        values.put("icon",ass.getIcon());
        values.put("finish",ass.getFinish());
        values.put("progress",ass.getProgress());
        int count = db.update("dailyAss",values,"_id=?",new String[]{ass.getId()+""});
        db.close();
        return count;
    }

    /*
     * 遍历数据库
     */
    public List<DailyAssignment> queryAll(){
        SQLiteDatabase db;
        db = helper.getWritableDatabase();
        Cursor c = db.query("dailyAss",null,null,null,null,null,null);
        List<DailyAssignment> list = new ArrayList<DailyAssignment>();
        while(c.moveToNext()){
            long id = c.getLong(c.getColumnIndex("_id"));
            String objId = c.getString(1);
            String date = c.getString(2);
            String ass = c.getString(3);
            int icon = c.getInt(4);
            String finish = c.getString(5);
            int progress = c.getInt(6);
            list.add(new DailyAssignment(id,objId,date,ass,icon,finish,progress));
        }
        c.close();
        db.close();
        return list;
    }
    }
