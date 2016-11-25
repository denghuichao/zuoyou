package com.deng.mychat.tools;

import java.io.File;
import java.io.IOException;

import com.deng.mychat.pic.FileUtils;

import android.content.Context;
import android.util.Log;
 
public class CacheTool {
   // private static final String TAG = CacheTool.class.getName();
 
    public static final int CONFIG_CACHE_MOBILE_TIMEOUT  = 900000;  //15 minute
    public static final int CONFIG_CACHE_WIFI_TIMEOUT    = 180000;   //3 minute
 
    public static String getUrlCache(Context context,String url,boolean init) throws IOException {
        if (url == null) {
            return null;
        }
     
        String result = null;
        File file = new File(FileUtils.SDPATH + "/" + getCacheDecodeString(url));
        if (file.exists() && file.isFile()) {
            long expiredTime = System.currentTimeMillis() - file.lastModified();
           // Log.d(TAG, file.getAbsolutePath() + " expiredTime:" + expiredTime/60000 + "min");
            //1. in case the system time is incorrect (the time is turn back long ago)
            //2. when the network is invalid, you can only read the cache
            if (NetworkTool.isConnectInternet(context) && expiredTime < 0) {
                return null;
            }
            if(NetworkTool.isConnectWifi(context) && !init
                   && expiredTime > CONFIG_CACHE_WIFI_TIMEOUT) {
                return null;
            } else if (NetworkTool.isConnectInternet(context)&& !NetworkTool.isConnectWifi(context)
            		&& !init  && expiredTime > CONFIG_CACHE_MOBILE_TIMEOUT) {
                return null;
            }
            result = FileUtils.readTextFile(file);
        }
        return result;
    }
 
    public static void setUrlCache(Context context,String data, String url) {
        File file = new File(FileUtils.SDPATH + "/" + getCacheDecodeString(url));
        File path=new File(FileUtils.SDPATH);
        if( !path.exists()) {  
          //  Log.d("TestFile", "Create the path:" + FileUtils.SDPATH);  
            path.mkdir();  
        }  
        //�����������ݵ����̣����Ǵ����ļ�
      //  Log.d("MAIN", "Writing cache");
		FileUtils.writeTextFile(file, data);
	//	Log.d("MAIN", "Writing cache done");
		
    }
 
    public static String getCacheDecodeString(String url) {
        //1. ���������ַ�
        //2. ȥ����׺���������ļ����������ͼ����(�ر���ͼƬ����Ҫ������ƴ��������е��ֻ���ͼ�⣬ȫ�����ǵĻ���ͼƬ)
        if (url != null) {
            return url.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
        }
        return null;
    }
}
