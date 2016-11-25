package com.deng.mychat.model;

import com.deng.mychat.bean.FriendNotice;
import com.deng.mychat.bean.UserInfo;

/**
 * 
 * �?��联系人显示的与某个的聊天记录
 * 
 */
public class HistoryChatBean implements Comparable{
	public static final int ADD_FRIEND = 1;
	public static final int SYS_MSG = 2; 
	public static final int CHAT_MSG = 3;

	public static final int READ = 0;
	public static final int UNREAD = 1;
	public  String id; 
	public  String title; 
	public  String content; 
	public  Integer status; // 0已读 1未读
	public  String from; 
	public UserInfo fromUser;
	public UserInfo toUser;
	public  String to; 
	public String noticeTime; 
	public  Integer noticeSum;
	public  Integer noticeType;

	public Integer getNoticeSum() {
		return noticeSum;
	}

	public void setNoticeSum(Integer noticeSum) {
		this.noticeSum = noticeSum;
	}

	public Integer getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(Integer noticeType) {
		this.noticeType = noticeType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getNoticeTime() {
		return noticeTime;
	}

	public void setNoticeTime(String noticeTime) {
		this.noticeTime = noticeTime;
	}



	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		if(arg0 instanceof HistoryChatBean)
			return ((HistoryChatBean)arg0).noticeTime.compareTo(this.noticeTime);
		else if(arg0 instanceof FriendNotice)
			return ((FriendNotice)arg0).timeStr.compareTo(this.noticeTime);
				
		return 0;
	}

}
