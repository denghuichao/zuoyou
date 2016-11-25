package com.deng.mychat.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import tools.AppException;
import tools.Logger;

import com.google.gson.Gson;

public class NewsEntity implements Serializable{
	public int status;
	public String msg;
	public List<News>newsList;
	
	public static NewsEntity parse(String res) throws IOException, AppException{
		NewsEntity data = new NewsEntity();
		Logger.i(res);
		try {
			Gson gson = new Gson();
			data = gson.fromJson(res, NewsEntity.class);
		} catch (Exception e) {
			Logger.i(res);
			Logger.i(e);
			throw AppException.json(e);
		}
		return data;
	}
}
