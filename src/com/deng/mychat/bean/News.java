package com.deng.mychat.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tools.AppException;
import tools.Logger;

import com.google.gson.Gson;


public class News implements Serializable {
	
	public  UserInfo authorInfo ;
	
	public String userId;
	public  String shuoshuoId;
	public  String context;//说说的内容
	public  List<Picture>pictures;  
	public  Date releaseTime;//
	public  int shareCount;
	public  List<Reply>comments=new ArrayList<Reply>();//
	public  List<Prise>zans=new ArrayList<Prise>();//

   public int replyNum;
   public Double  longitude;
   public Double latitude;
   public int zanCount;
   
  
   public String sharedNewId;
   
   public int shuoType;//说说类型
   public String shareUrl;//分享进来的链接
   public String descImg="";//链接缩略图
   public String descText="";//链接标题
   
   public News sharedNews;
 
   public News()
	{
		
	}
	
	public News(UserInfo u)
	{
		this.authorInfo=u;
	}
	
	public void setContextText(String s)
	{
		this.context=s;
	}
	public String getContextText()
	{
		return this.context;
	}
	
	public void setpictures(List<Picture> pictures)
	{
		this.pictures=pictures;
	}
	
	public List<Picture> getPictures()
	{
		return this.pictures;
	}
	
	public UserInfo getUser()
	{
		return this.authorInfo;
	}
	
	
	public long getSecondsBeforeNow()
	{
		if(releaseTime==null)return -1;
		return  (new Date().getTime()- this.releaseTime.getTime())/1000;
	}
	
	public void addReply(Reply r)
	{
		this.comments.add(r);
	}
	
	public void addPrise(Prise p)
	{
		this.zans.add(p);
	}
	
	public List<Reply> getReply()
	{
		return this.comments;
	}
	
	public List<Prise> getPrise()
	{
		return this.zans;
	}
	
	public void setShareCount(int count)
	{
		this.shareCount=count;
	}
	
	public int getShareCount()
	{
		return this.shareCount;
	}
	
    public static News parse(String string) throws AppException {//from json to news
		News data = new News();
		Logger.i(string);
		try {
			Gson gson = new Gson();
			data = gson.fromJson(string, News.class);
		} catch (Exception e) {
			Logger.i(e);
			throw AppException.json(e);
		}
		return data;
	}

	public String deletePrise(String loginUid) {
		// TODO Auto-generated method stub
		String id=null;
		for(int i=0;i<zans.size();i++)
		{
			if(zans.get(i).authorInfo.getId().equals(loginUid))
			{
				id=zans.get(i).priseId;
				zans.remove(i);
				//return id;
			}
		}
		return id;
	}



}
