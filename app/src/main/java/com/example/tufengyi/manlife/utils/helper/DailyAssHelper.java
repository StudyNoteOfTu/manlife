package com.example.tufengyi.manlife.utils.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DailyAssHelper extends SQLiteOpenHelper {
    public DailyAssHelper(Context context){
        super(context,"dailyAss.db",null,1);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE dailyAss(_id INTEGER PRIMARY KEY AUTOINCREMENT,objId VERCHAR(50),date VERCHAR(20),title VERCHAR(20),icon VERCHAR(10),finish VERCHAR(10),progress INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int oldVersion,int newVersion){

    }
}
