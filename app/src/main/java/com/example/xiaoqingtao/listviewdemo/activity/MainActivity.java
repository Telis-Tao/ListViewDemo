package com.example.xiaoqingtao.listviewdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.xiaoqingtao.listviewdemo.others.ListViewAdapter;
import com.example.xiaoqingtao.listviewdemo.bean.ListViewBean;
import com.example.xiaoqingtao.listviewdemo.R;
import com.example.xiaoqingtao.listviewdemo.db.DBManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.list_view);
        DBManager dbManager = new DBManager(getApplicationContext());
        List<ListViewBean> list = dbManager.getAll();
        listView.setAdapter(new ListViewAdapter(list, this));
//        dbManager.addAll(list);
//        dbManager.closeDB();
    }

    @NonNull
    private List<ListViewBean> initList() {
        List<ListViewBean> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(new ListViewBean("火宴山", 64, "火锅", 43, 9.1, 3, 4.5, ListViewBean.URLS[0]));
            list.add(new ListViewBean("宴稼厨房", 99, "浙江菜", 131, 5.3, 9, 5, ListViewBean.URLS[1]));
            list.add(new ListViewBean("盛世唐宫海鲜舫", 124, "粤菜", 43, 4.6, 7, 4, ListViewBean.URLS[2]));
            list.add(new ListViewBean("海底捞火锅", 103, "火锅", 264, 8.8, 5, 5, ListViewBean.URLS[3]));
        }
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
