package com.kkkk.l.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by KangYueLong on 2017/1/11.
 */

public class CacheUtils {
    public static String getString(Context mContext,String key){
        SharedPreferences sp =mContext.getSharedPreferences("kkkk",Context.MODE_PRIVATE);
        return sp.getString(key,"");
    }
    public static void putString(Context mContext,String key,String value){
        SharedPreferences sp =mContext.getSharedPreferences("kkkk",Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }
}
