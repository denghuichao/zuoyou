package com.deng.mychat.service;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import tools.Logger;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.deng.mychat.R;
import com.deng.mychat.bean.FriendNotice;
import com.deng.mychat.bean.JsonMessage;
import com.deng.mychat.bean.NewsNotice;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.FriendNoticeManager;
import com.deng.mychat.config.NewsNoticeManager;
import com.deng.mychat.config.NoticeManager;
import com.deng.mychat.config.WCApplication;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.model.IMMessage;
import com.deng.mychat.model.Notice;
import com.deng.mychat.tools.JSONProcesor;
import com.deng.mychat.ui.FriendNoticeActivity;
import com.deng.mychat.ui.NewsDetilActivity;
import com.deng.mychat.ui.NewsDetilActivity;
import com.deng.mychat.ui.Tabbar;
import com.google.gson.Gson;

public class NoticeService extends Service{
	
	private Context context;
	private NotificationManager notificationManager;
	private Handler myHandler=new Handler();
    private static final String TAG = "MAIN";  
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		context = this;
		//Logger.i("c");
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		init();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.i("s");
		return super.onStartCommand(intent, flags, startId);
	}
	

	public int init()
	{        
		TimerTask myTask=new TimerTask()
		{
			public void run()
			{
				  WCApplication  appContext=(WCApplication)getApplication();
				  ApiClient.checkNews(appContext.getLoginApiKey(),  new ClientCallback(){
					@Override
					public void onSuccess(Object data) {
						try {
							JSONObject json=new JSONObject((String)data);
							JSONArray replyJson=json.getJSONArray("comments");
							if(replyJson.length()>0)
							{
								for(int i=0;i<replyJson.length ();i++)
								{
									JSONObject reply=replyJson.getJSONObject(i);
									UserInfo user=JSONProcesor.processUser(reply.getJSONObject("authorInfo").toString());
											NewsNoticeManager noticeManager = NewsNoticeManager
													.getInstance(context);
											//[{"shuo_comment_id":"297e9e794d89b7d8014d89ce51540010","to_id":"297e9e794d7175d5014d717a64da0009","context":"more","authorInfo":{"sex":1,"myTags":".乒乓球.科幻.经典怀旧.轻音乐.背包客","nickname":"邓辉超","userschool":"武汉理工大学","if_notice":0,"longitude":114.34374,"user_id":"297e9e794d7175d5014d717a64da0009","userhead_small_url":"http:\/\/115.29.228.117:8080\/297e9e794d7175d5014d717a64da0009\/userHead\/1432196324336_small.jpg","latitude":30.51317,"school_year":2013,"userhead_url":"http:\/\/115.29.228.117:8080\/297e9e794d7175d5014d717a64da0009\/userHead\/1432196324336.jpg","userbirthday":"1990\/12\/21 00:00:00"},"reply_type":0,"to_user_name":"邓辉超","shuoshuo_id":"297e9e794d7feae8014d861736370093","comment_time":"2015\/05\/25 14:41:49"}]
											NewsNotice notice = new NewsNotice();
											notice.type=1;
											notice.noticeTime=reply.getString("comment_time");
											notice.newsId=reply.getString("shuoshuo_id");
											notice.status=0;
											notice.userId=user.userId;
											String content="";
											if(reply.has("context"))content=reply.getString("context");
											if(reply.has("shuo_comment_id"))notice.commentId=reply.getString("shuo_comment_id");
											int reply_type=0;
											if(reply.has("reply_type"))reply_type=reply.getInt("reply_type");
											
											long noticeId=noticeManager.saveNotice(notice);
											if (noticeId != -1) {
												Intent intent = new Intent(CommonValue.ACTION_NEWS_NOTICE);
												intent.putExtra("notice", notice);
												sendBroadcast(intent);
												setNotiType(R.drawable.logo,
														user.userName+(reply_type==0?"评论了你的说说":"回复了你的评论"),content, Tabbar.class,noticeId+"", user);

											}
								}
							}
							
							JSONArray applysToMe=json.getJSONArray("myApplys");
							if(applysToMe.length()>0)
							{
								for(int i=0;i<applysToMe.length();i++)
								{
									JSONObject app=applysToMe.getJSONObject(i);
									JSONObject apply=app.getJSONObject("apply");
									UserInfo user=JSONProcesor.processUser(app.getJSONObject("userInfo").toString());

									FriendNotice notice = new FriendNotice();
									notice.type=0;
									notice.noticeId=apply.getString("apply_id");
									notice.timeStr=apply.getString("apply_time");
									notice.processStatus=0;
									notice.user=user;//通知描述
									notice.desc="请求添加你为好友";
									saveOrUpdateFriendNotice(notice);

									Intent intent = new Intent(CommonValue.ADD_FRIEND_ACTION);
									intent.putExtra(IMMessage.IMMESSAGE_KEY, notice.desc);
									intent.putExtra("notice", notice);
									sendBroadcast(intent);
									setNotiType(R.drawable.logo,
														user.userName,
											notice.desc, FriendNoticeActivity.class,notice.noticeId, user);

							}
							}
							JSONArray responesToMe=json.getJSONArray("handledApplys");
							if(responesToMe.length()>0)
							{
								for(int i=0;i<applysToMe.length();i++)
								{
									JSONObject app=applysToMe.getJSONObject(i);
									JSONObject apply=app.getJSONObject("apply");
									int result=apply.getInt("handle_result");
									String resStr="";
									int status=0;
									if(result==3){resStr="忽略了你的好友请求";status=3;}
									else if(result==1){resStr="接受了你的好友请求";status=1;}
									else if(result==2){resStr="拒绝了你的好友请求";status=2;}
									
									UserInfo user=JSONProcesor.processUser(app.getJSONObject("userInfo").toString());
											
									FriendNotice notice = new FriendNotice();
									notice.type=1;
									notice.noticeId=apply.getString("apply_id");
									notice.timeStr=apply.getString("apply_time");
									notice.processStatus=status+1;
									notice.user=user;//通知描述
									notice.desc=resStr;
									saveOrUpdateFriendNotice(notice);
											
									Intent intent = new Intent(CommonValue.ADD_FRIEND_ACTION);
									intent.putExtra(IMMessage.IMMESSAGE_KEY, notice.desc);
									intent.putExtra("notice", notice);
									sendBroadcast(intent);
									setNotiType(R.drawable.logo,
														user.userName,
														notice.desc, FriendNoticeActivity.class,notice.noticeId, user);

								}
							}
							
							JSONArray shares=json.getJSONArray("shares");
							if(shares.length()>0)
							{
								for(int i=0;i<shares.length();i++)
								{
									JSONObject share=shares.getJSONObject(i);
									UserInfo user=JSONProcesor.processUser(share.getJSONObject("userInfo").toString());
											NewsNoticeManager noticeManager = NewsNoticeManager
													.getInstance(context);
											
											NewsNotice notice = new NewsNotice();
											notice.type=3;
											notice.status=0;
											notice.noticeTime=share.getString("release_time");
											notice.userId=user.userId;
											notice.newsId=share.getString("shuoshuo_id");
											long noticeId=noticeManager.saveNotice(notice);
											
											if (noticeId != -1) {
												setNotiType(R.drawable.logo,
														user.userName,"分享了你的说说", NewsDetilActivity.class,notice.newsId, user);

											}

								}
							}	
							
							JSONArray zans=json.getJSONArray("zans");
							if(zans.length()>0)
							{
								for(int i=0;i<zans.length();i++)
								{
									JSONObject zan=zans.getJSONObject(i);
									UserInfo user=JSONProcesor.processUser(zan.getJSONObject("authorInfo").toString());
											NewsNoticeManager noticeManager = NewsNoticeManager
													.getInstance(context);
											NewsNotice notice = new NewsNotice();
											
											notice.type=2;
											notice.status=0;
											notice.noticeTime=zan.getString("zan_time");
											notice.userId=user.userId;
											if(zan.has("zan_id"))notice.priseId=zan.getString("zan_id");
											notice.newsId=zan.getString("shuoshuo_id");
											long noticeId = noticeManager.saveNotice(notice);
											if (noticeId != -1) {
												Intent intent = new Intent(CommonValue.ACTION_NEWS_NOTICE);
												intent.putExtra("notice", notice);
												sendBroadcast(intent);
												setNotiType(R.drawable.logo,
														user.userName,
														"赞了你的说说", Tabbar.class,noticeId+"", user);
											}
								}
							}							
						  } 
						catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(String message) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onRequestLogIn() {
						// TODO Auto-generated method stub
						
					}
					 
				 });
			}
		};
       
		Timer myTimer=new Timer();
		myTimer.schedule(myTask, 0, 1000*30);
        return START_STICKY;
	}
	
	
	 public void saveOrUpdateFriendNotice(final FriendNotice notice){
			myHandler.post(new Runnable(){
				@Override
				public void run() {
					FriendNoticeManager.getInstance(context).saveOrUpdateFriendNotice(notice,  
							((WCApplication) getApplicationContext()).getLoginLocation());
				}
			});
		}
	
	 public void onDestroy() {  
	        super.onDestroy();  
	        stopSelf();  
	    } 	
	 
	 private void setNotiType(int iconId, String contentTitle,
				String contentText, Class activity, String applyId,UserInfo user) {

			Intent notifyIntent = new Intent(this, activity);

			notifyIntent.putExtra("applyId", applyId);
			notifyIntent.putExtra("user", user);
			
			PendingIntent appIntent = PendingIntent.getActivity(this, 0,
					notifyIntent, 0);

			Notification myNoti = new Notification();
			myNoti.flags = Notification.FLAG_AUTO_CANCEL;
			myNoti.icon = iconId;
			myNoti.tickerText = contentTitle;
			myNoti.defaults |= Notification.DEFAULT_SOUND;
			myNoti.defaults |= Notification.DEFAULT_VIBRATE;

			myNoti.setLatestEventInfo(this, contentTitle, contentText, appIntent);
			notificationManager.notify(0, myNoti);
		}
}

