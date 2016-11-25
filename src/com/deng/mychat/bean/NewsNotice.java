package com.deng.mychat.bean;

import java.io.Serializable;

public class NewsNotice  implements Serializable,Comparable<NewsNotice>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7757525476594208312L;
	public int type;//0:导航有更新，1:说说评论 2:说说点赞
	public int status;
	public String noticeId;
	public String commentId;
	public String priseId;
	public String noticeTime;
	public String userId;
	public String newsId;

	@Override
	public int compareTo(NewsNotice arg0) {
		// TODO Auto-generated method stub
		return arg0.noticeTime.compareTo(this.noticeTime);
	}

}
