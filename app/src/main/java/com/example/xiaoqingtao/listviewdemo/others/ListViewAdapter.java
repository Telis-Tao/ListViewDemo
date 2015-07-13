package com.example.xiaoqingtao.listviewdemo.others;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xiaoqingtao.listviewdemo.R;
import com.example.xiaoqingtao.listviewdemo.activity.ZoomImageViewActivity;
import com.example.xiaoqingtao.listviewdemo.bean.ListViewBean;
import com.example.xiaoqingtao.listviewdemo.view.NetworkImageView;

import java.util.List;

public class ListViewAdapter extends BaseAdapter {
    private List<ListViewBean> mList;

    private Activity mContext;

    public ListViewAdapter(List<ListViewBean> mList, Activity context) {
        this.mList = mList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_view_item, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (NetworkImageView) view.findViewById(R.id.icon);
            viewHolder.number = (TextView) view.findViewById(R.id.number);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
            viewHolder.price = (TextView) view.findViewById(R.id.price);
            viewHolder.description = (TextView) view.findViewById(R.id.description);
            viewHolder.distance = (TextView) view.findViewById(R.id.distance);
            viewHolder.discount = (TextView) view.findViewById(R.id.discount);
            viewHolder.route = (TextView) view.findViewById(R.id.route);
            viewHolder.telephone = (TextView) view.findViewById(R.id.telephone);
            viewHolder.innerMap = (TextView) view.findViewById(R.id.inner_map);
            viewHolder.listener = new ListViewListener(position);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        ListViewBean item = mList.get(position);
//        viewHolder.icon.setImageResource(R.drawable.icon);
//        viewHolder.icon.setImageBitmap(item.getIconURL());
        viewHolder.icon.clear();
        viewHolder.icon.setImageUrl(item.getIconURL());
        viewHolder.number.setText(String.valueOf(position + 1) + ".");
        viewHolder.name.setText(item.getName());
        viewHolder.ratingBar.setRating((float) item.getRating());
        viewHolder.price.setText(Html.fromHtml("人均：<font color='#3ECAF8'>$" + item.getPrice() +
                "</font>"));
        viewHolder.description.setText(item.getDescription());
        viewHolder.distance.setText(item.getDistance() + "m");
        viewHolder.discount.setText(Html.fromHtml("最低折扣" + "<font color='#3ECAF8'>" + item
                .getDiscount() + "折</font>,共计<font color='#3ECAF8'>" + item.getGroupCount() +
                "条</font>团购"));
        ListViewListener listener = viewHolder.listener;
        listener.setPosition(position);
        view.setOnClickListener(listener);
        viewHolder.icon.setOnClickListener(listener);
        viewHolder.route.setOnClickListener(listener);
        viewHolder.telephone.setOnClickListener(listener);
        viewHolder.innerMap.setOnClickListener(listener);
        return view;
    }

    private class ViewHolder {
        NetworkImageView icon;
        TextView number;
        TextView name;
        RatingBar ratingBar;
        TextView price;
        TextView description;
        TextView distance;
        TextView discount;
        TextView route;
        TextView telephone;
        TextView innerMap;
        ListViewListener listener;
    }

    private class ListViewListener implements View.OnClickListener {
        public ListViewListener(int position) {
            mPosition = position + 1;
        }

        public void setPosition(int position) {
            mPosition = position + 1;
        }

        private int mPosition;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.route:
                    Toast.makeText(mContext, "route " + mPosition, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.telephone:
                    Toast.makeText(mContext, "telephone " + mPosition, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.inner_map:
                    Toast.makeText(mContext, "inner_map " + mPosition, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.icon:
                    NetworkImageView imageView = (NetworkImageView) v;
                    String url = imageView.getImageUrl();
                    Intent i = new Intent();
                    i.putExtra(ZoomImageViewActivity.IMAGE_URL, url);
                    i.setClass(mContext, ZoomImageViewActivity.class);
                    mContext.startActivity(i);
                    break;
                default:
                    Toast.makeText(mContext, "main view" + mPosition, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
