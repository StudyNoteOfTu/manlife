package com.example.tufengyi.manlife.utils.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tufengyi.manlife.bean.DailyAssignment;
import com.example.tufengyi.manlife.bean.PunchedAss;
//import com.example.tufengyi.manlife.utils.helper.PunchedAssHelper;

import java.util.ArrayList;
import java.util.List;
//
//public class PunchedAssDao {
//    public PunchedAssHelper helper;
//    public PunchedAssDao(Context context){
//        helper = new PunchedAssHelper(context);
//    }
//
//    public void insert(PunchedAss ass){
//        //插入数据
//        SQLiteDatabase db = helper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("date",ass.getDate());
//        values.put("title",ass.getTitle());
//        values.put("icon",ass.getIcon());
//
//        long id = db.insert("punch",null,values);
//        ass.setId(id);
//        db.close();
//    }
//
//    public int delete(long id){
//        SQLiteDatabase db = helper.getWritableDatabase();
//        int count = db.delete("punch","_id=?",new String[]{id+""});
//        db.close();
//        return count;
//    }
//
//    public int update(PunchedAss ass){
//        SQLiteDatabase db = helper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("date",ass.getDate());
//        values.put("title",ass.getTitle());
//        values.put("icon",ass.getIcon());
//        int count = db.update("punch",values,"_id=?",new String[]{ass.getId()+""});
//        db.close();
//        return count;
//    }
//
//    public List<PunchedAss> queryAll(){
//        SQLiteDatabase db;
//        db = helper.getWritableDatabase();
//        Cursor c = db.query("punch",null,null,null,null,null,null);
//        List<PunchedAss> list = new ArrayList<>();
//        while(c.moveToNext()){
//            long id = c.getLong(c.getColumnIndex("_id"));
//            String date = c.getString(1);
//            String title = c.getString(2);
//            int icon = c.getInt(3);
//            list.add(new PunchedAss(id,date,title,icon));
//        }
//        c.close();
//        db.close();
//        return list;
//    }
//
//
//
//}
