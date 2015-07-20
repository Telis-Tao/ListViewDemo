package com.example.xiaoqingtao.listviewdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "list_view.db";
    private static final int VERSION = 1;
    private static final String list_view_data = "list_view_data";
    private static final String DROP_LIST_VIEW_DATA = "DROP TABLE IF EXISTS " + list_view_data;
    private static final String CREATE_LIST_VIEW_DATA = "create table if not exists " +
            list_view_data + "(_id integer primary key " +
            "autoincrement,name text,rating float, price integer,description text,distance " +
            "integer, discount float, group_count integer)";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DROP_LIST_VIEW_DATA);
        db.execSQL(CREATE_LIST_VIEW_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
