package com.deng.mychat.tools;

import org.json.JSONException;
import org.json.JSONObject;

import tools.StringUtils;

import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.Picture;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.CommonValue;

public class JSONProcesor {
	public static UserInfo processUser(String str )
	{
		 UserInfo user=null;//=new UserInfo();
		try {
			JSONObject userJson=new JSONObject(str);
			
			 user=new UserInfo();
			
			user.userId=userJson.getString("user_id");
			user.userName=userJson.getString("nickname");
			user.schoolName=userJson.getString("userschool");
			user.sex=userJson.getInt("sex");
			String txPath=userJson.getString("userhead_small_url");
			String txPath_large=userJson.getString("userhead_url");
			
			user.txPath=new Picture(txPath_large,txPath);
			
			Double lat=null;
			Double lng=null;
			
			String latStr=userJson.getString("latitude");
			String lngStr=userJson.getString("longitude");
			
			if(!StringUtils.notEmpty(latStr)&& !StringUtils.empty(lngStr))
			{
				lat=Double.parseDouble(latStr);
				lng=Double.parseDouble(lngStr);
				
			}
			
		
			if(lat!=null && lng !=null )user.location=new LocationPoint(lat,lng);
			
			//user.txPath_large=user.txPath_large.replace("F:/apachetomcat/webapps/", CommonValue.BASE_URL);						
			//user.txPath=user.txPath.replace("F:/apachetomcat/webapps/", CommonValue.BASE_URL);
		//	return user;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return user;
	}
}
