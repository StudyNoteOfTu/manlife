package com.example.tufengyi.manlife.utils.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SettingsHelper extends SQLiteOpenHelper {
    public SettingsHelper(Context context){
        super(context,"settings.db",null,1);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE settings(_id INTEGER PRIMARY KEY AUTOINCREMENT,up INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int oldVersion,int newVersion){

    }
}
