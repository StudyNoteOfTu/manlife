package com.example.tufengyi.manlife.utils.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tufengyi.manlife.bean.Flag;
//import com.example.tufengyi.manlife.utils.helper.FlagHelper;

import java.util.ArrayList;
import java.util.List;
//
//public class FlagDao {
//
//    //这个可以不需要，后面改成S存储
//    private FlagHelper helper;
//
//    //构造器
//    public FlagDao(Context context){
//        helper = new FlagHelper(context);
//    }
//
//    //增加数据
//    public void insert(Flag flag){
//        //插入数据
//        SQLiteDatabase db = helper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("ID",flag.getID());
//        values.put("date",flag.getDate());
//        values.put("updates",flag.getUpdates());
//        values.put("comments",flag.getComments());
//        values.put("likes",flag.getLikes());
//        long id = db.insert("flag",null,values);
//        flag.setId(id);
//        db.close();
//    }
//
//    /*
//     * 删除对应id的数据
//     */
//    public int delete(long id){
//        SQLiteDatabase db = helper.getWritableDatabase();
//        int count = db.delete("flag","_id=?",new String[]{id+""});
//        db.close();
//        return count;
//    }
//
//    /*
//     * 更新
//     */
//    public int update(Flag flag){
//        SQLiteDatabase db = helper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("date",flag.getDate());
//        values.put("updates",flag.getUpdates());
//        values.put("comments",flag.getComments());
//        values.put("likes",flag.getLikes());
//        int count = db.update("flag",values,"_id=?",new String[]{flag.getId()+""});
//        db.close();
//        return count;
//    }
//
//    /*
//     * 遍历数据库
//     */
//    public List<Flag> queryAll(){
//        SQLiteDatabase db;
//        db = helper.getWritableDatabase();
//        Cursor c = db.query("flag",null,null,null,null,null,null);
//        List<Flag> list = new ArrayList<>();
//        while(c.moveToNext()){
//            long id = c.getLong(c.getColumnIndex("_id"));
//            String ID = c.getString(1);
//            String date = c.getString(2);
//            String updates = c.getString(3);
//            int comments = c.getInt(4);
//            int likes = c.getInt(5);
//            list.add(new Flag(id,ID,date,updates,comments,likes));
//        }
//        c.close();
//        db.close();
//        return list;
//    }
//}

