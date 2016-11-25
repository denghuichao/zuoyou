package com.deng.mychat.config;

import java.util.List;
import java.util.Properties;

import org.apache.http.client.CookieStore;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.Picture;
import com.deng.mychat.bean.UserEntity;
import com.deng.mychat.bean.UserInfo;
import com.loopj.android.http.PersistentCookieStore;
import com.nostra13.universalimageloader.utils.L;

import tools.AppContext;
import tools.AppException;
import tools.AppManager;
import tools.ImageCacheUtil;
import tools.Logger;
import tools.StringUtils;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

/*
 * mychat
 *
 * @author denghuichao
 *
 */

public class WCApplication extends AppContext {
	private static WCApplication mApplication;
	
	private NotificationManager mNotificationManager;
	
	private boolean login = false;	//登录状态
	private String loginUid = "0";	//登录用户的id
	private String apiKey = "0";	//登录用户的id
	
	
	public static String sdDir=Environment.getExternalStorageDirectory().toString();//获取跟目录
	
	public synchronized static WCApplication getInstance() {
		return mApplication;
	}
	
	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}
	
	public void onCreate() {
		mApplication = this;
		Logger.setDebug(true);
		Logger.getLogger().setTag("mychat");
		ImageCacheUtil.init(this);
		Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
		L.disableLogging();
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		CookieStore cookieStore = new PersistentCookieStore(this);  
		QYRestClient.getIntance().setCookieStore(cookieStore);
		SDKInitializer.initialize(this.getApplicationContext()); 
		Intent intent = new Intent();
        intent.setAction("tools.NetworkState.Service");
        startService(intent);
		}
	
	public void exit() {
		AppManager.getAppManager().finishAllActivity();
		
	}
	
	public boolean isFirstUse()
	{
		String isFirst = getProperty("user.first_use");
		if(StringUtils.empty(isFirst))
		{
			return true;
		}
		else 
		{
			//Log.d("MAIN", "not first use");
			return false;
		}
	}
	
	
	public void setNoFirstUse()
	{
		setProperty("user.first_use","1");
	}
	
	/**
	 * 用户是否登录
	 * @return
	 */
	public boolean isLogin() {
		try {
			String loginStr = getProperty("user.login");
			if (StringUtils.empty(loginStr)) {
				login = false;
			}
			else {
				login = (loginStr.equals("1")) ? true : false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return login;
	}

	/**
	 * 保存登录信息
	 * @param username
	 * @param pwd
	 */
	@SuppressWarnings("serial")
	public void saveLoginInfo(final UserEntity user) {
		this.loginUid = user.userInfo.userId;
		this.apiKey = user.apiKey;
		
		//if(user.userInfo==null)return ;
		
		this.login = true;
		
		Properties p=new Properties(){
			{	
				setProperty("user.login","1");
				setProperty("user.uid", user.userInfo.userId);
				setProperty("user.name", user.userInfo.userName);
				setProperty("user.face", user.userInfo.txPath.smallPicture);
				
				setProperty("user.face_large", user.userInfo.txPath.largePicPath);
				//setProperty("user.description", user.userInfo.description);
				setProperty("user.registerDate", user.userInfo.registerDate);
				setProperty("user.apikey", user.apiKey);
				setProperty("user.sex", user.userInfo.sex+"");
				setProperty("user.birthday", user.userInfo.birthDay);
				setProperty("user.schoolday", user.userInfo.schoolDay);
				setProperty("user.schoolname", user.userInfo.schoolName);
				if(user.userInfo.location!=null)
					setProperty("user.location", user.userInfo.location.latitude+","
				+user.userInfo.location.longitude);
				
				String label="";
				for(String s:user.userInfo.label)
					label+=s+',';
				setProperty("user.label", label);
			}};
		
		setProperties(p);
	}
	public void modifyLoginLocation(final LocationPoint l)
	{
		if(l!=null)
			setProperties(new Properties(){
				{
					setProperty("user.location",l.latitude+","
							+l.longitude);
				}
			});
	}
	
	public void modifyLoginInfo(final UserInfo user) {
		setProperties(new Properties(){
			{
				if (StringUtils.notEmpty(user.userName)) {
					setProperty("user.name", user.userName);
				}
				if (StringUtils.notEmpty(user.txPath.smallPicture)) {
					setProperty("user.face", user.txPath.smallPicture);
				}
				
				if (StringUtils.notEmpty(user.txPath.largePicPath)) {
					setProperty("user.face_large", user.txPath.largePicPath);
				}
				
			
				if (StringUtils.notEmpty(user.location)) {
					setProperty("user.location", user.location.latitude+","
							+user.location.longitude);
				}
				
				if (StringUtils.notEmpty(user.registerDate))
					setProperty("user.registerDate", user.registerDate);
				
				if (StringUtils.notEmpty(user.sex))
					setProperty("user.sex", user.sex+"");
				
				if (StringUtils.notEmpty(user.birthDay))
					setProperty("user.birthday", user.birthDay);
				
				if (StringUtils.notEmpty(user.schoolDay))
					setProperty("user.schoolday", user.schoolDay);
				
				if (StringUtils.notEmpty(user.schoolName))
					setProperty("user.schoolname", user.schoolName);
				
				if(user.label.size()>0)
				{
				String label="";
				for(String s:user.label)
					label+=s+',';
				setProperty("user.label", label);}
				
			}
		});		
	}
  
	public void setLoginUserLabel(final List<String>labels)
	{
		if(labels==null || labels.size()==0)return;
		setProperties(new Properties(){
			{
				String label="";
				for(String s:labels)
					label+=s+',';
				setProperty("user.label", label);}

		});		
	}
	
	/**
	 * 获取登录用户id
	 * @return
	 */
	public String getLoginUid() {
		return (getProperty("user.uid"));
	}
	
	public String getLoginApiKey() {
		return (getProperty("user.apikey"));
	}
	
	public String getLoginUserHead() {
		return (getProperty("user.face"));
	}
	
	public void saveLoginPassword(final String password) {
		setProperties(new Properties(){
			{
				setProperty("user.password",password);
			}
		});		
	}
	
	public String getLoginPassword() {
		return (getProperty("user.password"));
	}
	/**
	 * 获取登录信息
	 * @return
	 */
	public UserEntity getLoginInfo() {		
		UserEntity lu = new UserEntity();		
		UserInfo userInfo = new UserInfo();
		userInfo.userId = (getProperty("user.uid"));
		userInfo.userName = (getProperty("user.name"));
		String txPath = (getProperty("user.face"));
		String txPath_large=(getProperty("user.face_large"));
		userInfo.txPath=new Picture(txPath_large,txPath);
		//userInfo.description = (getProperty("user.description"));
		userInfo.registerDate = (getProperty("user.registerDate"));
		
		String location=(getProperty("user.location"));
		if(location!=null && location.matches("\\d+.?\\d+,\\d+.?\\d+"))
		{	
			double lng=Double.parseDouble(location.split(",")[1]);
			double lat=Double.parseDouble(location.split(",")[0]);
			userInfo.location=new LocationPoint(lat,lng);
		}
		userInfo.birthDay=(getProperty("user.birthday"));
		userInfo.schoolDay=(getProperty("user.schoolday"));
		userInfo.schoolName=(getProperty("user.schoolname"));
		if(!StringUtils.empty(getProperty("user.sex")) && getProperty("user.sex").matches("\\d"))
			userInfo.sex=Integer.parseInt(getProperty("user.sex"));
		String labels=getProperty("user.label");
		if(labels==null)labels="";
		String []ls=labels.split(",");
		for(String l:ls)
			userInfo.label.add(l);
		lu.apiKey = (getProperty("user.apikey"));
		
		lu.userInfo = userInfo;
		return lu;
	}
	
	public LocationPoint getLoginLocation()
	{
		String location=(getProperty("user.location"));
		if(location!=null && location.matches("\\d+.?\\d+,\\d+.?\\d+"))
		{	
			double lng=Double.parseDouble(location.split(",")[1]);
			double lat=Double.parseDouble(location.split(",")[0]);
			LocationPoint lo=new LocationPoint(lat,lng);
			return lo;
		}
		return null;
	}
	
	public String getNickname() {		
		return (getProperty("user.name"));
	}
	
	/**
	 * 退出登录
	 */
	public void setUserLogout() {
		this.login = false;
		setProperties(new Properties(){
			{
				setProperty("user.login","0");
			}
		});	
	}
	
	public boolean isNeedCheckLogin() {
		try {
			String loginStr = getProperty("user.needchecklogin");
			if (StringUtils.empty(loginStr)) {
				return false;
			}
			else {
				return (loginStr.equals("1")) ? true : false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setNeedCheckLogin() {
		setProperties(new Properties(){
			{
				setProperty("user.needchecklogin","1");
			}
		});
	}
	
	public void saveNotiWhen(final String when) {
		setProperties(new Properties(){
			{
				setProperty("noti.when",when);
			}
		});
	}
	
	public String getNotiWhen() {
		try {
			String loginStr = getProperty("noti.when");
			if (StringUtils.empty(loginStr)) {
				return "0";
			}
			else {
				return loginStr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}
	

}
