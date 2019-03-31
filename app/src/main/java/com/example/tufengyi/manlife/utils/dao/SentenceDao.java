package com.example.tufengyi.manlife.utils.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tufengyi.manlife.bean.Sentence;
import com.example.tufengyi.manlife.utils.helper.SentenceHelper;

import java.util.ArrayList;
import java.util.List;

public class SentenceDao {

    //这个可以不需要，后面改成S存储
    private SentenceHelper helper;

    //构造器
    public SentenceDao(Context context){
        helper = new SentenceHelper(context);
    }

    //增加数据
    public void insert(Sentence sentence){
        //插入数据
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("note",sentence.getSentence());
        long id = db.insert("sentence",null,values);
        sentence.setId(id);
        db.close();
    }

    /*
     * 删除对应id的数据
     */
    public int delete(long id){
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete("sentence","_id=?",new String[]{id+""});
        db.close();
        return count;
    }

    /*
     * 更新
     */
    public int update(Sentence sentence){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("note",sentence.getSentence());
        int count = db.update("sentence",values,"_id=?",new String[]{sentence.getId()+""});
        db.close();
        return count;
    }

    /*
     * 遍历数据库
     */
    public List<Sentence> queryAll(){
        SQLiteDatabase db;
        db = helper.getWritableDatabase();
        Cursor c = db.query("sentence",null,null,null,null,null,null);
        List<Sentence> list = new ArrayList<Sentence>();
        while(c.moveToNext()){
            long id = c.getLong(c.getColumnIndex("_id"));
            String note = c.getString(1);
            list.add(new Sentence(id,note));
        }
        c.close();
        db.close();
        return list;
    }
}
