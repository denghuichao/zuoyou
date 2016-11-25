package com.deng.mychat.bean;

import java.io.Serializable;

import tools.AppException;
import tools.Logger;


import com.google.gson.Gson;


public class UserDetail implements Serializable{
	public int status;
	public String msg;
	public String apiKey;
	public UserInfo userDetail;
	
	public static UserDetail parse(String res) throws  AppException {
		UserDetail data = new UserDetail();
		try {
			Gson gson = new Gson();
			data = gson.fromJson(res, UserDetail.class);
		} catch (Exception e) {
			Logger.i(e);
			throw AppException.json(e);
		}
		return data;
	}

}
