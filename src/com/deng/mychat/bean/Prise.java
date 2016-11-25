package com.deng.mychat.bean;

import java.io.Serializable;

import tools.AppException;
import tools.Logger;

import com.google.gson.Gson;


public class Prise implements Serializable{
	
	public String priseId;
	public String newsId;
	public String priseTime;
	public UserInfo authorInfo;//who prise you?
	public String userId;
	
	public Prise()//the default constructor
	{
		
	}
	
	//public  int getPriseValue()
	//{
	//	return this.priseValue;
	//}
	
	//public void setPriseValue(int value)
	//{
	//	if(value<=NEGATIVE)
	//		this.priseValue=NEGATIVE;
	//	else if(value>=POSITIVE)
	//		this.priseValue=POSITIVE;
	//	else this.priseValue=NEUTRALITY;
	//}
	
	//public int getSecondsBeforeNow()
	//{
	//	return this.secondsBeforeNow;
	//}
	
	//public void setSecondsBeforeNow(int s)
	//{
	//	this.secondsBeforeNow=s;
	//}
	
	public static Prise parse(String string) throws AppException {
		Prise data = new Prise();
		Logger.i(string);
		try {
			Gson gson = new Gson();
			data = gson.fromJson(string, Prise.class);
		} catch (Exception e) {
			Logger.i(e);
			throw AppException.json(e);
		}
		return data;
	}
}