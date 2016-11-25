package com.deng.mychat.bean;

import java.io.Serializable;
import java.util.Date;

import tools.AppException;
import tools.Logger;

import com.google.gson.Gson;

public class Reply implements Serializable{
	
    public String shuoCommentId;
    public String newsId;
    
    public String context;
    public int sort;
    public String commentTime;
    public int replyType;
    public UserInfo author;
    public UserInfo toUser;
    public String fromId;
    public String toId;
	
	
	
	public void setContentText(String cText)
	{
		this.context=cText;
	}
	
    public String getContentText()
    {
    	return this.context;
    }
	
	
	
	  public static Reply parse(String string) throws AppException {
		  Reply data = new Reply();
		  Logger.i(string);
			try {
				Gson gson = new Gson();
				data = gson.fromJson(string, Reply.class);
			} catch (Exception e) {
				Logger.i(e);
				throw AppException.json(e);
			}
			return data;
		}
}
