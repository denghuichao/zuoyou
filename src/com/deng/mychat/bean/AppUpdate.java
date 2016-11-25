package com.deng.mychat.bean;

import com.google.gson.Gson;

import tools.AppException;
import tools.Logger;
import bean.Entity;

/*
 * desc:保存应用的更新信息
 * author：邓辉超
 */

public class AppUpdate extends Entity{
	public String version_code;
	public String version_name;
	public String app_name;
	public String app_url;
	public String update_log;
	
	public static AppUpdate prase(String postRequest)throws AppException
	{
		AppUpdate update=new AppUpdate();
		try {
			Gson gson = new Gson();
			update = gson.fromJson(postRequest, AppUpdate.class);
		} catch (Exception e) {
			Logger.i(e);
			throw AppException.json(e);
		}
		return update;
		
	}
}
