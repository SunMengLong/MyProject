package com.taobao.myproject.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by dell on 2017/7/25.
 */

public class InstallAppUtil {

    /**
     * 安装新版本apk的方法
     * @param packageUri apk路径,必须先做好判断,此方法默认uri正确
     */
    public static void installApk(Uri packageUri, Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(packageUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
