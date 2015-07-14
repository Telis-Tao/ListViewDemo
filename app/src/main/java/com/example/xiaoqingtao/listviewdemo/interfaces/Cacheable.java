package com.example.xiaoqingtao.listviewdemo.interfaces;

/**
 * Created by xiaoqing.tao on 2015/7/14.
 */
public interface Cacheable<T> {
    T get();

    void put(T t);
}
