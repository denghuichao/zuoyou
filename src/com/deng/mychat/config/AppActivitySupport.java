/**
 * wechatdonal
 */
package com.deng.mychat.config;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * wechat
 *
 * @author donal
 *
 */
public interface AppActivitySupport {

	/**
	 * 
	 * 获取EimApplication.
	 * 
	 */
	public abstract WCApplication getWcApplication();

	/**
	 * 
	 * 终止服务.
	 * 
	 */
	public abstract void stopService();

	/**
	 * 
	 * �?��服务.
	 * 
	 */
	public abstract void startService();

	/**
	 * 
	 * 校验网络-如果没有网络就弹出设�?并返回true.
	 * 
	 * @return
	 */
	public abstract boolean validateInternet();

	/**
	 * 
	 * 校验网络-如果没有网络就返回true.
	 * 
	 * @return
	 */
	public abstract boolean hasInternetConnected();

	/**
	 * 
	 * �?��应用.
	 * 
	 */
	public abstract void isExit();

	/**
	 * 
	 * 判断GPS是否已经�?��.
	 * 
	 * @return
	 */
	public abstract boolean hasLocationGPS();

	/**
	 * 
	 * 判断基站是否已经�?��.
	 * 
	 * @return
	 */
	public abstract boolean hasLocationNetWork();

	/**
	 * 
	 * �?��内存�?
	 * 
	 */
	public abstract void checkMemoryCard();

	/**
	 * 
	 * 显示toast.
	 * 
	 * @param text
	 *            内容
	 * @param longint
	 *            内容显示多长时间
	 */
	public abstract void showToast(String text, int longint);

	/**
	 * 
	 * 短时间显示toast.
	 * 
	 * @param text
	 */
	public abstract void showToast(String text);

	/**
	 * 
	 * 获取进度�?
	 * 
	 * @return
	 */
	public abstract ProgressDialog getProgressDialog();

	/**
	 * 
	 * 返回当前Activity上下�?
	 * 
	 * @return
	 */
	public abstract Context getContext();

	/**
	 * 
	 * 获取当前登录用户的SharedPreferences配置.
	 * 
	 * @return
	 */
	public SharedPreferences getLoginUserSharedPre();


	/**
	 * 
	 * 发出Notification的method.
	 * 
	 * @param iconId
	 *            图标
	 * @param contentTitle
	 *            标题
	 * @param contentText
	 *            你内�?	 * @param activity
	 */
	public void setNotiType(int iconId, String contentTitle,
			String contentText, Class activity, String from);
}
