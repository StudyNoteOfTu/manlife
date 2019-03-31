package com.example.tufengyi.manlife.utils.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SentenceHelper extends SQLiteOpenHelper {
    public SentenceHelper(Context context){
        super(context,"sentence.db",null,1);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE sentence(_id INTEGER PRIMARY KEY AUTOINCREMENT,note VERCHAR(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int oldVersion,int newVersion){

    }
}
