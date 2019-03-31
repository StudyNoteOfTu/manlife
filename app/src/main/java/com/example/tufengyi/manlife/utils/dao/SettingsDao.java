package com.example.tufengyi.manlife.utils.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tufengyi.manlife.bean.Settings;
import com.example.tufengyi.manlife.utils.helper.SettingsHelper;

import java.util.ArrayList;
import java.util.List;

public class SettingsDao{
        //这个可以不需要，后面改成S存储
        private SettingsHelper helper;

        //构造器
        public SettingsDao(Context context){
            helper = new SettingsHelper(context);
        }

        //增加数据
        public void insert(Settings set){
            //插入数据
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("up",set.getUp());
            long id = db.insert("settings",null,values);
            set.setId(id);
            db.close();
        }

        /*
         * 删除对应id的数据
         */
        public int delete(long id){
            SQLiteDatabase db = helper.getWritableDatabase();
            int count = db.delete("settings","_id=?",new String[]{id+""});
            db.close();
            return count;
        }

        /*
         * 更新
         */
        public int update(Settings set){
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("up",set.getUp());
            int count = db.update("settings",values,"_id=?",new String[]{set.getId()+""});
            db.close();
            return count;
        }

        /*
         * 遍历数据库
         */
        public List<Settings> queryAll(){
            SQLiteDatabase db;
            db = helper.getWritableDatabase();
            Cursor c = db.query("settings",null,null,null,null,null,null);
            List<Settings> list = new ArrayList<>();
            while(c.moveToNext()){
                long id = c.getLong(c.getColumnIndex("_id"));
                int up = c.getInt(1);
                list.add(new Settings(id,up));
            }
            c.close();
            db.close();
            return list;
        }
}
