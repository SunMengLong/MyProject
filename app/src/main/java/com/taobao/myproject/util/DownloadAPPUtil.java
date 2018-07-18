package com.taobao.myproject.util;

import android.util.Log;

import com.taobao.myproject.callback.IApkDownloadCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadAPPUtil {
	private final String TAG = this.getClass().getName();
	
	private static DownloadAPPUtil dwonloadAppUtil;

	private DownloadAPPUtil() {
		
	}

	public static DownloadAPPUtil getUtilInstance() {
		if (dwonloadAppUtil == null){
			dwonloadAppUtil = new DownloadAPPUtil();
		}
		return dwonloadAppUtil;
	}

	public void downLoadApk(final String url, final String target, final IApkDownloadCallBack callBack) {
		File prefile  = new File(target);
		prefile.delete();
		File file = creatNewFile(target);
		if (file != null && file.exists() && file.isFile()) {
			if (callBack != null)
				callBack.onStart();
			InputStream input = null;
			FileOutputStream output = null;
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setConnectTimeout(3000);
				conn.setReadTimeout(3000);
				int fileSize = conn.getContentLength();
				double rate = (double) 100 / fileSize;  //最大进度转化为100
				input = conn.getInputStream();
				if (input != null) {
					output = new FileOutputStream(file);
					byte buf[] = new byte[1024];
					int downLoadFileSize = 0;
					int times=0;
					while (downLoadFileSize <= fileSize) {
						int numread = input.read(buf);
						if (numread == -1) {
							break;
						}
						output.write(buf, 0, numread);
						downLoadFileSize += numread;
						if (times >= 50 && callBack != null){
							times = 0;
							callBack.onLoading((int) (downLoadFileSize * rate));
						}
						times ++;
					}
					if (callBack != null)
						callBack.onSuccess(file);
				}
			} catch (Exception e) {
				if(file.exists() && file.isFile()){
					file.delete();
				}
				if (callBack != null)
					callBack.onFailure(e);
				Log.e(TAG, e.toString());
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						Log.e(TAG, e.getMessage());
					} finally {
						if (input != null) {
							try {
								input.close();
							} catch (IOException e) {
								Log.e(TAG, e.getMessage());
							}
						}
					}
				}
			}
		} else {
			callBack.onFailure(new Exception("target is not a file"));
		}
	}

	private File creatNewFile(String target) {
		File targetFile = new File(target);
		if (targetFile.exists() && targetFile.isFile()) {
			targetFile.delete();
			targetFile = new File(target);
			return targetFile;
		}
		String[] split = target.split("/");
		String path = "";
		for (int i = 0; i < split.length - 1; i++) {
			path += "/" + split[i];
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		targetFile = new File(target);
		try {
			targetFile.createNewFile();
		} catch (IOException e) {
			Log.e(TAG, "Create file error", e);
		}
		return targetFile;
	}
}
