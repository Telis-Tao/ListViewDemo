package com.example.xiaoqingtao.listviewdemo.interfaces;

public interface Cacheable<K,V> {
    V get(K k);

    void put(K k,V v);
}
