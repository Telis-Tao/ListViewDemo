package com.example.xiaoqingtao.listviewdemo.bean;

import com.example.xiaoqingtao.listviewdemo.R;

public class ListViewBean {
    public static final String[] URLS = {"http://img.tankr.net/s/medium/M0VF.jpg", "http://img" +
            ".tankr.net/s/medium/XL7V.jpg", "http://img.tankr.net/s/medium/ET95.jpg", "http://img" +
            ".tankr.net/s/medium/0CDA.jpg"};
    private int mIconResId = R.drawable.icon;
    private String mName;
    private double mPrice;
    private String mDescription;
    private double mDistance;
    private double mDiscount;

    public ListViewBean(String name, double price, String description, double distance, double
            discount, int groupCount, double rating, String iconURL) {
        mName = name;
        mPrice = price;
        mDescription = description;
        mDistance = distance;
        mDiscount = discount;
        mGroupCount = groupCount;
        mRating = rating;
        mIconURL = iconURL;
    }

    private int mGroupCount;
    private double mRating;

    public ListViewBean(String iconURL) {
        mIconURL = iconURL;
    }

    public String getIconURL() {
        return mIconURL;
    }

    public void setIconURL(String iconURL) {
        mIconURL = iconURL;
    }

    private String mIconURL;

    public double getDiscount() {
        return mDiscount;
    }

    public void setDiscount(double discount) {
        mDiscount = discount;
    }

    public int getGroupCount() {
        return mGroupCount;
    }

    public void setGroupCount(int groupCount) {
        mGroupCount = groupCount;
    }

    public double getRating() {
        return mRating;
    }

    public void setRating(double rating) {
        mRating = rating;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public void setIconResId(int iconResId) {
        mIconResId = iconResId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }


    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double distance) {
        mDistance = distance;
    }
}
