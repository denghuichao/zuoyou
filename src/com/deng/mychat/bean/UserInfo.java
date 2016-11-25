package com.deng.mychat.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tools.AppException;
import tools.Logger;

import com.google.gson.Gson;

/**
 * mychat yuzhi
 *
 * @author denghuichao
 *
 */

public class UserInfo implements Serializable,Comparable<UserInfo>{
	public String userId="";
	public String userName="匿名用户";
	public String registerDate="";
	
	public Picture txPath;
	
	public String schoolName="";
	public String birthDay="";
	public String schoolDay="";
	public int sex=-99;
	public int isMyFriend;
	public int isOnline;
	
	public String desc="暂无个性签名";
	public LocationPoint location;//经度 纬度 ','隔开
	public int friendRate=0;
	public int commonFriends=0;
	public int  commonTags=0;
	public int  interations=0;
	//public double  distance=50000;
	
	public List<String> label=new ArrayList<String>();
	
	public UserInfo()
	{
		
	}
	
	public UserInfo(String userId)
	{
		this.userId=userId;
	}
	
	public UserInfo(String userId,String name)
	{
		this.userId=userId;
		this.userName=name;
	}
	
	
	public String getId()
	{
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId= userId;
	}
	
	public String getName()
	{
		return this.userName;
	}
	
	public void setName(String name) {
		this.userName = name;
	}
	
	public Picture getTxPath() {
		return txPath;
	}
	
	public void setTxPath(Picture txPath) {
		this.txPath = txPath;
	}
	
	public int getDistance()
	{
		return 1000;
	}
	
	public void setDistance(int d)
	{
		//distance=d;
	}
	
	public String toString()
	{
		return "User ID: "+userId+", User Name: "+userName+" "+"icon: "+txPath +" schoolName:"+schoolName
				+" birthDay:"+birthDay+" schoolDay:"+schoolDay+" sex:"+sex+" location:"+location;
	}

	public List<String> getLabel() {
		// TODO Auto-generated method stub
		return label;
	}
	
	public void setLabel(List<String> label) {
		// TODO Auto-generated method stub
		this.label=label;
	}

	public String getFriendRate() {
		// TODO Auto-generated method stub
		return this.friendRate+"%";
	}

    public void setSex(int sex)
    {
    	this.sex=sex;
    }
    
    public int getSex()
    {
    	return sex;
    }
    
    public double distanceFromMe(LocationPoint myLocation)
    {
    	return LocationPoint.distanceBetween(myLocation, location);
    }
    
    public static UserInfo parse(String string) throws AppException {
		UserInfo data = new UserInfo();
		try {
			Gson gson = new Gson();
			data = gson.fromJson(string, UserInfo.class);
		} catch (Exception e) {
			Logger.i(e);
			throw AppException.json(e);
		}
		return data;
	}

	@Override
	public int compareTo(UserInfo arg0) {
		// TODO Auto-generated method stub
		return this.userId.compareTo(arg0.userId);
	}
}
