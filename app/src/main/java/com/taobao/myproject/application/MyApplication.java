package com.taobao.myproject.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class MyApplication extends Application {
    public static Context context;
    PackageInfo info = null;
    public static int versionCode;
    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        //取出当前App的版本号
        try {
            PackageManager manager = this.getPackageManager();
            info = manager.getPackageInfo(this.getPackageName(), 0);
            versionCode = info.versionCode;
            //将当前版本号存入到sp当中
            sp = getSharedPreferences("versionCode", 0);
            sp.edit().putInt("versionCode",versionCode).commit();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
