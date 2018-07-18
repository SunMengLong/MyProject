package com.taobao.myproject;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.taobao.myproject.application.MyApplication;
import com.taobao.myproject.bean.InfoBean;
import com.taobao.myproject.callback.IApkDownloadCallBack;
import com.taobao.myproject.util.DownloadAPPUtil;
import com.taobao.myproject.util.HttpUtil;
import com.taobao.myproject.util.InstallAppUtil;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static String updateUrl="http://192.168.1.103:8080/json.txt";
    public static String downLoadUrl="http://192.168.1.103:8080/app-debug.apk";
    public String TAG="sunmenglong";
    private String result;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //拿到服务器apk的信息
                    String result= (String) msg.obj;
                    Gson gson=new Gson();
                    InfoBean infoBean=gson.fromJson(result, InfoBean.class);
                    SharedPreferences sp = getSharedPreferences("versionCode", 0);
                    int spVersionCode=sp.getInt("versionCode",-400);
                    //当前版本号与服务器号做对比  判断是否需要更新
                    if(infoBean.getVersionCode()>spVersionCode){
                        AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("提示")
                                .setMessage(infoBean.getDes())
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        updateApk();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("暂不更新",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int whichButton) {
                                                // 点击"取消"按钮之后退出程序
                                                dialog.dismiss();
                                            }
                                        }).create();// 创建
                        // 显示对话框
                        ad.show();
                    }
                    break;
                case 1:
                    //获取权限
                    File file= (File) msg.obj;
                    String[] command = {"chmod","777",file.getPath()};
                    ProcessBuilder builder=new ProcessBuilder(command);
                    try {
                        builder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //更新安装从服务器下载的Apk
                    InstallAppUtil.installApk(Uri.fromFile(file),MainActivity.this);
                    break;
                default:
                    break;
            }
        }
    };
    private TextView version_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        version_tv = (TextView) findViewById(R.id.version_tv);
        version_tv.setText("当前的版本号为："+MyApplication.versionCode);
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    //从服务器拿到apk的信息，判断是否要更新
                    result = HttpUtil.getSyncToInStream(updateUrl);
                    Message msg=new Message();
                    msg.obj=result;
                    msg.what=0;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void updateApk(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                DownloadAPPUtil.getUtilInstance().downLoadApk(downLoadUrl, MainActivity.this.getCacheDir().getAbsolutePath(), new IApkDownloadCallBack() {
                    @Override
                    public void onStart() {
                        Log.i(TAG, "onStart: ............正在启动下载");
                    }

                    @Override
                    public void onLoading(long count, long current) {
                        Log.i(TAG, "onLoading: ...........正在下载中");
                    }

                    @Override
                    public void onLoading(int progress) {
                        Log.i(TAG, "onLoading: ..........当前下载+"+progress+"%");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.i(TAG, "onSuccess: ...........下载成功");
                        //下载成功之后，调用系统方法进行安装
                        Message msg=new Message();
                        msg.obj=file;
                        msg.what=1;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.i(TAG, "onFailure: 下载失败");
                    }
                });
            }
        }.start();
    }
}
