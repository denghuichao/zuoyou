package com.deng.mychat.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpDataGetter {
	
    /** 
     * Get image from newwork 
     * @param path The path of image 
     * @return byte[] 
     * @throws Exception 
     */  
    public static byte[] getImage(String path) throws Exception{  
        URL url = new URL(path);  
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setConnectTimeout(5 * 1000);  
        conn.setRequestMethod("GET");  
        InputStream inStream = conn.getInputStream();  
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){  
            return readStream(inStream);  
        }  
        return null;  
    }  
  
    /** 
     * Get image from newwork 
     * @param path The path of image 
     * @return InputStream 
     * @throws Exception 
     */  
    public static InputStream getImageStream(String path) throws Exception{  
        URL url = new URL(path);  
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setConnectTimeout(5 * 1000);  
        conn.setRequestMethod("GET");  
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){  
            return conn.getInputStream();  
        }  
        return null;  
    }  
    /** 
     * Get data from stream 
     * @param inStream 
     * @return byte[] 
     * @throws Exception 
     */  
    public static byte[] readStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1){  
            outStream.write(buffer, 0, len);  
        }  
        outStream.close();  
        inStream.close();  
        return outStream.toByteArray();  
    } 
	
	public static String getHttpData(String url){
	    InputStream is = null;
	    String result = "";

	    try{
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(url);
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	    }catch(Exception e){
	        return "Fail to establish http connection!"+e.toString();
	    }

	    try{
	    	StringBuffer sb = new StringBuffer();
	        InputStreamReader isr = new InputStreamReader(is,"UTF-8");  
	        int len = 0;  
	        char[] buf = new char[1024];  

	        while ((len = isr.read(buf)) != -1)  
	        {  
	            sb.append(new String(buf, 0, len));  
	        }  

	        is.close();  
            isr.close();
	        result=sb.toString();
	    }catch(Exception e){
	        return "Fail to convert net stream!";
	    }

	    return result;
	}

	 
	
	public static  String getTitle(String httpData)
	{
		//<title s_t="知乎日报3.0b...是编辑 | 36氪">知乎日报3.0beta版发布，UGC当过滤器，人人都是编辑 | 36氪</title>
		//<title>井贤栋空降蚂蚁金服的“亲儿子”天弘基金，人事地震为哪般？-看点-虎嗅网</title>
		
		Pattern p =Pattern.compile("<title.*?>(.*)</title>");
		Matcher m=p.matcher(httpData);
		
		String res="";
		if(m.find())
		{
			res=m.group(1);
		}
				
		return res;
	}
	
	
	public static String getContent(String data)
	{
		//Pattern p =Pattern.compile("<header.*?>.*</header>");
		//Matcher m=p.matcher(data);
		String r=data.replaceAll("<head(.*)?>.*</head(er)?>", "");
		return r;
	}
	
	 // 获取img标签正则  
    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";  
    // 获取src路径的正则  
    private static final String IMGSRC_REG = "http(s)?:\"?(.*?)(\"|>|\\s+)";  


    /*** 
     * 获取ImageUrl地址 
     *  
     * @param HTML 
     * @return 
     */  
    public static List<String> getImageLable(String HTML) {  
    	if(HTML==null)return null;
        Matcher matcher = Pattern.compile(IMGURL_REG).matcher(getContent(HTML));  
        List<String> listImgUrl = new ArrayList<String>();  
        while (matcher.find()) {  
        	String ss=matcher.group();
        	listImgUrl.add(ss);  
        }  
        return listImgUrl;  
    }  
  
    /*** 
     * 获取ImageSrc地址 
     *  
     * @param listImageUrl 
     * @return 
     */  
    public static List<String> getImageSrc(List<String> listImageUrl) { 
    	
    	if(listImageUrl==null)return null;
        List<String> listImgSrc = new ArrayList<String>();  
        for (String image : listImageUrl) {  
            Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(image);  
            while (matcher.find()) { 
            	String ss=matcher.group().substring(0, matcher.group().length() - 1);
            	String s=ss.toLowerCase();
            	//if(s.endsWith("png")||s.endsWith("jpg")||s.endsWith("jpeg"))
            	listImgSrc.add(ss);  
            }  
        }  
//        
//        Collections.sort(listImgSrc, new Comparator<String>(){
//
//			@Override
//			public int compare(String arg0, String arg1) {
//				// TODO Auto-generated method stub
//				if(arg0==null)return -1;
//				else if(arg1==null)return 1;
//				String s1=arg0.toLowerCase();
//				String s2=arg1.toLowerCase();
//				
//				if(s1.endsWith("jpeg"))return -1;
//				else if(s2.endsWith("jpeg"))return 1;
//				else return 0;
//			}
//        	
//        });
        
        return listImgSrc;  
    }  
    
    
    
    public static List<String> getImgUrls(String html)
    {
    	return getImageSrc(getImageLable(html));
    }
	
}
