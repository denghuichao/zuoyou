package com.deng.mychat.bean;

import java.io.IOException;

import tools.AppException;
import tools.Logger;

import com.google.gson.Gson;

import bean.Entity;

public class UserEntity extends Entity{
	public int status;
	public String msg;
	public String apiKey;
	public UserInfo userInfo;
	
	public static UserEntity parse(String res) throws IOException, AppException {
		UserEntity data = new UserEntity();
		try {
			Gson gson = new Gson();
			data = gson.fromJson(res, UserEntity.class);
		} catch (Exception e) {
			Logger.i(e);
			throw AppException.json(e);
		}
		return data;
	}
}
