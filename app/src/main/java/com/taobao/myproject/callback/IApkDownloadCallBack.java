package com.taobao.myproject.callback;

import java.io.File;

public interface IApkDownloadCallBack {
	public void onStart();
	public void onLoading(long count, long current);
	public void onLoading(int progress);
	public void onSuccess(File file);
	public void onFailure(Exception e);
}
