package com.kkkk.l.mobile.app;

import android.app.Application;

import org.xutils.x;



public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true); // 是否输出debug日志...
    }
}
