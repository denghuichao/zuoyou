package com.deng.mychat.bean;

import java.io.Serializable;

import com.deng.mychat.model.HistoryChatBean;

public class FriendNotice implements Comparable,Serializable{
	
	public  UserInfo user ;
	public  String noticeId;
	public  String desc;//Í¨ÖªÃèÊö
	public  String timeStr;//
	public int processStatus;
	public int type;
  
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		if(arg0 instanceof HistoryChatBean)
			return ((HistoryChatBean)arg0).noticeTime.compareTo(this.timeStr);
		else if(arg0 instanceof FriendNotice)
			return ((FriendNotice)arg0).timeStr.compareTo(this.timeStr);
				
		return 0;
	}
}
