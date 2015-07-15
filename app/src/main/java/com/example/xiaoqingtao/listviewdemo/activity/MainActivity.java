package com.example.xiaoqingtao.listviewdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.xiaoqingtao.listviewdemo.R;
import com.example.xiaoqingtao.listviewdemo.adapter.ListViewAdapter;
import com.example.xiaoqingtao.listviewdemo.bean.ListViewBean;
import com.example.xiaoqingtao.listviewdemo.db.DBManager;
import com.example.xiaoqingtao.listviewdemo.others.ImageProcess;
import com.example.xiaoqingtao.listviewdemo.view.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {


    private static final String TAG = "MainActivity";
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.list_view);
        DBManager dbManager = new DBManager(getApplicationContext());
        List<ListViewBean> list = dbManager.getAll();
        mListView.setOnScrollListener(
                new AbsListView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(AbsListView view, int
                            scrollState) {
                        switch (scrollState) {
                            case SCROLL_STATE_IDLE:
                                ImageProcess.getInstance(getApplication()).permitSubmit();
                                ImageProcess.getInstance(getApplicationContext()).clearTasks();
                                int first = view.getFirstVisiblePosition();
                                int last = view.getLastVisiblePosition();
//                            Log.d(TAG, "onScrollStateChanged " + first + " " + last);
                                for (int i = 0; i <= last - first; i++) {
                                    View tmp = view.getChildAt(i);
                                    if (tmp != null) {
                                        NetworkImageView niv = (NetworkImageView)
                                                tmp.findViewById(R.id
                                                        .icon);
//                                    Log.d(TAG, "onScrollStateChanged loadImage");
                                        niv.loadImage();
                                    }
                                }
                                break;
                            default:
                                ImageProcess.getInstance(getApplication()).forbidSubmit();
                                break;
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int
                            firstVisibleItem, int visibleItemCount,
                                         int totalItemCount) {
                    }
                }

        );
        mListView.setAdapter(new ListViewAdapter(list, this));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageProcess.getInstance(getApplication()).shutDown();
    }
}
