package com.example.xiaoqingtao.listviewdemo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.xiaoqingtao.listviewdemo.bean.ListViewBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoqing.tao on 2015/7/9.
 */
public class DBManager {
    private static final String LIST_VIEW_DATA = "list_view_data";
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DBManager(Context context) {
        mDBHelper = new DBHelper(context);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void add(ListViewBean item) {
        mDB.execSQL("INSERT INTO " + LIST_VIEW_DATA + " VALUES(null, ?, ?,?,?,?,?,?)", new
                Object[]{item.getName(), item
                .getRating(), item.getPrice(), item.getDescription(), item.getDistance(), item
                .getDiscount(), item.getGroupCount()});

    }

    public ListViewBean get(int i) {
        Cursor cursor;
        ListViewBean listViewBean = new ListViewBean(ListViewBean.URLS[i]);
        cursor = mDB.rawQuery("select * from " + LIST_VIEW_DATA + " where _id = " + i, null);
        while (cursor.moveToNext()) {
            listViewBean.setName(cursor.getString(2));
            listViewBean.setRating(cursor.getFloat(3));
            listViewBean.setPrice(cursor.getDouble(4));
            listViewBean.setDescription(cursor.getString(5));
            listViewBean.setDistance(cursor.getDouble(6));
            listViewBean.setDiscount(cursor.getDouble(7));
            listViewBean.setGroupCount(cursor.getInt(8));
        }
        return listViewBean;
    }

    public List<ListViewBean> getAll() {
        Cursor cursor = mDB.rawQuery("select * from " + LIST_VIEW_DATA, null);
        List<ListViewBean> list = new ArrayList<>();
        int i = 0;
        while (cursor.moveToNext()) {
            ListViewBean listViewBean = new ListViewBean(ListViewBean.URLS[i++]);
            listViewBean.setName(cursor.getString(1));
            listViewBean.setRating(cursor.getFloat(2));
            listViewBean.setPrice(cursor.getDouble(3));
            listViewBean.setDescription(cursor.getString(4));
            listViewBean.setDistance(cursor.getDouble(5));
            listViewBean.setDiscount(cursor.getDouble(6));
            listViewBean.setGroupCount(cursor.getInt(7));
            list.add(listViewBean);
            i = i % 4;
        }
        return list;
    }

    public void addAll(List<ListViewBean> list) {
        mDB.beginTransaction();
        try {
            for (ListViewBean item : list) {
                add(item);
            }
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }
    }

    public void closeDB() {
        mDB.close();
    }
}
