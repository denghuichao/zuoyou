package com.deng.mychat.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.Header;
import org.jivesoftware.smack.packet.Packet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.AppContext;
import tools.AppException;
import tools.Logger;
import tools.MD5Util;
import tools.StringUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.deng.mychat.R;
import com.deng.mychat.bean.AppUpdate;
import com.deng.mychat.bean.ApplyToMe;
import com.deng.mychat.bean.ContactEntity;
import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.News;
import com.deng.mychat.bean.NewsEntity;
import com.deng.mychat.bean.Picture;
import com.deng.mychat.bean.Prise;
import com.deng.mychat.bean.Reply;
import com.deng.mychat.bean.UserDetail;
import com.deng.mychat.bean.UserEntity;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.model.IMMessage;
import com.deng.mychat.model.Notice;
import com.deng.mychat.tools.AudioRecoderManager;
import com.deng.mychat.tools.CacheTool;
import com.deng.mychat.tools.NetworkTool;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class ApiClient {
	public final static String message_error = "服务器连接故障";
	public final static String message_not_login="API KEY无效，请重新登录";
	public interface ClientCallback{
        abstract void onSuccess(Object data);
        abstract void onFailure(String message);
        abstract void onError(Exception e);
        abstract void onRequestLogIn();
   }
	
	private static void saveCache(Context context, String url, String data) {
		CacheTool.setUrlCache(context,data, url) ;
    }
	
	public static void login( /*WCApplication  appContext,*/ String mobile, String password, final ClientCallback callback) {
		

		
		RequestParams params = new RequestParams();						
		params.add("mobile", mobile);
		params.add("uPass", password);
		params.add("versionInfo", " ");
		params.add("deviceInfo", " ");
		
		QYRestClient.post("login.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {	
					UserEntity user =new UserEntity();
					String ss=new String(responseBody);
					JSONObject json = new JSONObject(new String(responseBody));
					user.setError_code(json.getInt("status"));
					user.msg=json.getString("msg");
					user.status=json.getInt("status");
					if(user.status==1)
					{
						user.apiKey=json.getString("apiKey");	
						JSONObject userinfo=json.getJSONObject("userInfo");
						user.userInfo=new UserInfo();
						user.userInfo.sex=userinfo.getInt("sex");
						user.userInfo.birthDay=userinfo.getString("userbirthday");
						user.userInfo.userId=userinfo.getString("user_id");
						user.userInfo.userName=userinfo.getString("nickname");
						user.userInfo.schoolName=userinfo.getString("userschool");
						user.userInfo.schoolDay=userinfo.getInt("school_year")+"";
						String txPath=userinfo.getString("userhead_small_url");
						String txPath_large=userinfo.getString("userhead_url");
						
						user.userInfo.txPath=new Picture(txPath_large,txPath);
						if(userinfo.has("myTags"))
						{
							String labels=userinfo.getString("myTags");
							//Log.d("MAIN", "getContact.do :"+labels);	
							if(labels!=null && !labels.equals(""))
							{
								String []ls=labels.split("\\.");
								for(String s:ls){
									if(!s.equals("") && !user.userInfo.label.contains(s))
										user.userInfo.label.add(s);}
							}
						}
						
						callback.onSuccess(user);
					}
					else callback.onFailure(user.msg);
					} catch (Exception e) {
						e.printStackTrace();
						callback.onError(e);
					}
				}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				callback.onFailure(message_error);
			}
		});
	}
	
	public static void register(/*WCApplication  appContext,*/ String mobile, String password, String nickname, String intro, String avatar, final ClientCallback callback) {
		

		
		RequestParams params = new RequestParams();
		params.add("mobile", mobile);
		params.add("uPass", password);
		QYRestClient.post("register.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Log.d("Register","onSuccess");
				try {
					UserEntity data = new UserEntity();
					Log.d("MAIN",new String(responseBody));
					JSONObject json = new JSONObject(new String(responseBody));
					data.setError_code(json.getInt("status"));
					data.setMessage(json.getString("msg"));
					if(json.has("apiKey"))data.apiKey=json.getString("apiKey");
					data.status=json.getInt("status");
					data.msg=json.getString("msg");
					data.userInfo=null;
					callback.onSuccess(data);
				} catch (JSONException e) {
					e.printStackTrace();
					callback.onError(e);
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	
	public static void addFriend(/*WCApplication  appContext,*/ String apiKey, String userId, final ClientCallback callback) {
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("user_id", userId);
		QYRestClient.post("addFriend.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					JSONObject js = new JSONObject(new String(responseBody));
					Log.d("MAIN",js.toString());
					int status = js.getInt("status");
					if (status == 1) {
						callback.onSuccess(js.getString("msg"));
					} else {
						callback.onFailure(js.getString("msg"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
					callback.onError(e);
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	public static void deleteFriend(/*WCApplication  appContext,*/ String apiKey, String userId, final ClientCallback callback) {
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("userId", userId);
		QYRestClient.post("deleteFriend.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					JSONObject json = new JSONObject(new String(responseBody));
					if(json.getInt("status")==1)
					{
						callback.onSuccess(json.getString("msg"));
					}
					else callback.onFailure(json.getString("msg"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				}
		
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	public static void getMyFriend(final String loginId, final String apiKey, int page, int pageSize, final ClientCallback callback) {

		
		
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.put("pageIndex", page);
		params.put("pageSize", pageSize);
		QYRestClient.post("getMyFriends.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					Log.d("MAIN","getFriend.do"+new String(responseBody));
					JSONObject json  = new JSONObject(new String(responseBody));
					if(json.getInt("status")==1)
					{
						callback.onSuccess(processContact(json,1,loginId));	
					}
					else
					{
						callback.onFailure(json.getString("msg"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	public static void getContactByPhone( final String apiKey,String phone, int page, int pageSize, final ClientCallback callback) {
		
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.put("pageIndex", page);
		params.put("pageSize", pageSize);
		params.put("phonenumber", phone);
		QYRestClient.post("findFriendByPhoneNum.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					JSONObject json  = new JSONObject(new String(responseBody));
					if(json.getInt("status")==1)
					{
						callback.onSuccess(processUser(json.getJSONObject("searchResult")));	
					}
					else
					{
						callback.onFailure(json.getString("msg"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
					callback.onError(e);
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	public static ContactEntity processContact(JSONObject json,int isMyFriend,String loginId) throws JSONException
	{
		
			ContactEntity data =new ContactEntity();
			data.userList=new ArrayList<UserInfo>();
			data.status=json.getInt("status");
			if(json.has("msg"))data.msg=json.getString("msg");
			JSONArray plj=json.getJSONArray("userList");
			for(int i=0;i<plj.length();i++)
			{
				JSONObject userJson=plj.getJSONObject(i);
				UserInfo user=processUser(userJson);
				user.isMyFriend=isMyFriend;
//				user.userId=userJson.getString("user_id");
//				user.userName=userJson.getString("nickname");
//				user.birthDay=userJson.getString("userbirthday");
//				user.schoolDay=userJson.getInt("school_year")+"";
//				user.schoolName=userJson.getString("userschool");
//				user.sex=userJson.getInt("sex");
//				
//				if(userJson.has("freindRate"))user.friendRate=(int) (userJson.getDouble("freindRate")*100);
//				if(userJson.has("commonFreinds"))
//					user.commonFriends=userJson.getInt("commonFreinds");
//				if(userJson.has("commonTags"))
//					user.commonTags=userJson.getInt("commonTags");
//				if(userJson.has("interations"))
//					user.interations=userJson.getInt("interations");
//				
//				
				
//				String latStr=userJson.getString("latitude");
//				String lngStr=userJson.getString("longitude");
//				
//				Double lat=null;
//				Double lng=null;
//				
//				if(!StringUtils.empty(latStr)&& !StringUtils.empty(lngStr))
//				{
//					lat=Double.parseDouble(latStr);
//					lng=Double.parseDouble(lngStr);
//				}
//				
//				if(lat!=null && lng !=null )user.location=new LocationPoint(lat,lng);
//				String txPath=userJson.getString("userhead_small_url");
//				String txPath_large=userJson.getString("userhead_url");
//				
//				txPath_large=txPath_large.replace("F:/apachetomcat/webapps/", CommonValue.BASE_URL);						
//				txPath=txPath.replace("F:/apachetomcat/webapps/", CommonValue.BASE_URL);	
//				
//				user.txPath=new Picture(txPath_large,txPath);
//				
//				if(userJson.has("myTags"))
//				{
//					String labels=userJson.getString("myTags");
//					if(labels!=null && !labels.equals(""))
//					{
//						String []ls=labels.split("\\.");
//						for(String s:ls){
//							if(!s.equals("") && !user.label.contains(s))
//								user.label.add(s);}
//					}
//				}
//				
				if(!user.userId.equals(loginId))
					data.userList.add(user);
			}
			
			return data;	
	}
	

	public static void getAroundPerson(
			final String  loginId, final String apiKey, int page, 
			int pageSize, 
			double longitude,double latitude,
			final ClientCallback callback)
	{
		
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("pageIndex", page+"");
		params.add("pageSize", pageSize+"");
		params.add("longitude", longitude+"");
		params.add("latitude", latitude+"");
		
		QYRestClient.post("getAroundPerson.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Log.d("MAIN","getAroundPerson.do"+new String(responseBody));
				try {
					JSONObject json  = new JSONObject(new String(responseBody));
					if(json.getInt("status")==1)
					{
						
						callback.onSuccess(processContact(json,0,loginId));	
					}
					else
					{
						callback.onFailure(json.getString("msg"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	
	public static void getNewsFromFriend(
			 /*WCApplication  appContext,*/ final String apiKey, 
			int page,int pageSize, 
			final ClientCallback callback)
	{
		
		
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("pageIndex", page+"");
		params.add("pageSize", pageSize+"");
		QYRestClient.post("getFriendShuoshuo.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					JSONObject json  = new JSONObject(new String(responseBody));
					if(json.getInt("status")==1)
					{
						callback.onSuccess(processNews(json));	
					}
					else
					{
						callback.onFailure(json.getString("msg"));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				} 
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	public static NewsEntity processNews(JSONObject json) throws Exception
	{
		
			NewsEntity data = new NewsEntity();//.parse(new String(responseBody));
			data.status=json.getInt("status");
			if(json.has("msg"))data.msg=json.getString("msg");
			else data.msg="";
			List<News>newsList=new ArrayList<News>();
			if(data.status!=1)
			{
				data.newsList=newsList;
				return data;
			}
			
			
			JSONArray newsJsonList=json.getJSONArray("shuoList");
			for(int i=0;i<newsJsonList.length();i++)
			{
				News aNews=new News();
				JSONObject newsJson=newsJsonList.getJSONObject(i);
				aNews.shuoshuoId=newsJson.getString("shuoshuo_id");
				aNews.context=newsJson.getString("context");
				String timeStr=newsJson.getString("release_time");
				if(!StringUtils.empty(timeStr))aNews.releaseTime=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(timeStr);
				aNews.longitude=newsJson.getDouble("longitude");
				aNews.latitude=newsJson.getDouble("latitude");
				
				if(newsJson.has("type")){
					String t=newsJson.getString("type");
					if(!(t==null || t.equals("null")))
							aNews.shuoType=Integer.parseInt(t);
					}
				else aNews.shuoType=newsJson.getInt("shuotype");
				
				aNews.shareCount=newsJson.getInt("share_count");
				
				if(newsJson.has("share_url"))
					aNews.shareUrl=newsJson.getString("share_url");
				if(newsJson.has("desc_img"))
					aNews.descImg=newsJson.getString("desc_img");
				if(newsJson.has("desc_text"))
					aNews.descText=newsJson.getString("desc_text");
				
				
				List<Picture> picUrls=new ArrayList<Picture>();
				JSONArray picJsonList=newsJson.getJSONArray("pictures");
				for(int j=0;j<picJsonList.length();j++)
				{
					JSONObject picJson=picJsonList.getJSONObject(j);
					
					String pl=picJson.getString("picture_url");
					String ps=picJson.getString("picture_small_url");
					picUrls.add(new Picture(pl,ps));
				}
				aNews.setpictures(picUrls);
				
				JSONArray replyJsonList=newsJson.getJSONArray("comments");
				for(int j=0;j<replyJsonList.length();j++)
				{
					JSONObject replyJson=replyJsonList.getJSONObject(j);
					Reply reply=new Reply();
					reply.newsId=aNews.shuoshuoId;
					reply.shuoCommentId=replyJson.getString("shuo_comment_id");
					reply.toId=replyJson.getString("to_id");
					reply.context=replyJson.getString("context");
					reply.commentTime=replyJson.getString("comment_time");
					reply.replyType=replyJson.getInt("reply_type");
					
					
					JSONObject replyAuthorJson=replyJson.getJSONObject("authorInfo");
					UserInfo replyAuthor=processUser(replyAuthorJson);
//					replyAuthor.userId=replyAuthorJson.getString("user_id");
//					replyAuthor.userName=replyAuthorJson.getString("nickname");
//					replyAuthor.sex=replyAuthorJson.getInt("sex");
//					replyAuthor.schoolName=replyAuthorJson.getString("userschool");
//					String txPath=replyAuthorJson.getString("userhead_small_url");
//					String txPath_large=replyAuthorJson.getString("userhead_url");
//					//replyAuthor.txPath_large=replyAuthor.txPath_large.replace("F:/apachetomcat/webapps/", CommonValue.BASE_URL);						
//					replyAuthor.txPath=new Picture(txPath_large,txPath);//replyAuthor.txPath.replace("F:/apachetomcat/webapps/", CommonValue.BASE_URL);
//
//					if(replyAuthorJson.has("freindRate"))replyAuthor.friendRate=(int) (replyAuthorJson.getDouble("freindRate")*100);
//					if(replyAuthorJson.has("commonFreinds"))replyAuthor.commonFriends=replyAuthorJson.getInt("commonFreinds");
//					if(replyAuthorJson.has("commonTags"))replyAuthor.commonTags=replyAuthorJson.getInt("commonTags");
//					if(replyAuthorJson.has("interations"))replyAuthor.interations=replyAuthorJson.getInt("interations");
//					//if(replyAuthorJson.has("distance"))replyAuthor.distance=replyAuthorJson.getDouble("distance");
//					if(replyAuthorJson.has("myTags"))
//					{
//						String labels=replyAuthorJson.getString("myTags");
//						//Log.d("MAIN", "getContact.do :"+labels);	
//						if(labels!=null && !labels.equals(""))
//						{
//							String []ls=labels.split("\\.");
//							for(String s:ls){
//								if(!s.equals("") && !replyAuthor.label.contains(s))
//									replyAuthor.label.add(s);}
//						}
//					}
					
					reply.author=replyAuthor;
					aNews.addReply(reply);
				}
				
				JSONArray priseJsonList=newsJson.getJSONArray("zans");
				for(int j=0;j<priseJsonList.length();j++)
				{
					JSONObject zanJson=priseJsonList.getJSONObject(j);
					Prise prise=new Prise();
					prise.newsId=aNews.shuoshuoId;
					prise.priseId=zanJson.getString("zan_id");
					prise.priseTime=zanJson.getString("zan_time");
					JSONObject zanAuthorJson=zanJson.getJSONObject("authorInfo");
					UserInfo zanAuthor=processUser(zanAuthorJson);
//					zanAuthor.userId=zanAuthorJson.getString("user_id");
//					zanAuthor.userName=zanAuthorJson.getString("nickname");
//					zanAuthor.schoolName=zanAuthorJson.getString("userschool");
//					zanAuthor.sex=zanAuthorJson.getInt("sex");
//
//					if(zanAuthorJson.has("freindRate"))zanAuthor.friendRate=(int) (zanAuthorJson.getDouble("freindRate")*100);
//					if(zanAuthorJson.has("commonFreinds"))zanAuthor.commonFriends=zanAuthorJson.getInt("commonFreinds");
//					if(zanAuthorJson.has("commonTags"))zanAuthor.commonTags=zanAuthorJson.getInt("commonTags");
//					if(zanAuthorJson.has("interations"))zanAuthor.interations=zanAuthorJson.getInt("interations");
//					//if(zanAuthorJson.has("distance"))zanAuthor.distance=zanAuthorJson.getDouble("distance");
//					String txPath=zanAuthorJson.getString("userhead_small_url");
//					String txPath_large=zanAuthorJson.getString("userhead_url");
//					//replyAuthor.txPath_large=replyAuthor.txPath_large.replace("F:/apachetomcat/webapps/", CommonValue.BASE_URL);						
//					zanAuthor.txPath=new Picture(txPath_large,txPath);
//										
//					if(zanAuthorJson.has("myTags"))
//					{
//						String labels=zanAuthorJson.getString("myTags");
//						//Log.d("MAIN", "getContact.do :"+labels);	
//						if(labels!=null && !labels.equals(""))
//						{
//							String []ls=labels.split("\\.");
//							for(String s:ls){
//								if(!s.equals("") && !zanAuthor.label.contains(s))
//									zanAuthor.label.add(s);}
//						}
//					}
					
					prise.authorInfo=zanAuthor;
					aNews.addPrise(prise);
				}	
				
				JSONObject userJson=newsJson.getJSONObject("authorInfo");
				UserInfo user=processUser(userJson);
//				user.userId=userJson.getString("user_id");
//				user.userName=userJson.getString("nickname");
//				user.schoolName=userJson.getString("userschool");
//				user.sex=userJson.getInt("sex");
//				if(userJson.has("freindRate"))user.friendRate=(int) (userJson.getDouble("freindRate")*100);
//				if(userJson.has("commonFreinds"))user.commonFriends=userJson.getInt("commonFreinds");
//				if(userJson.has("commonTags"))user.commonTags=userJson.getInt("commonTags");
//				if(userJson.has("interations"))user.interations=userJson.getInt("interations");
//				//if(userJson.has("distance"))user.distance=userJson.getDouble("distance");
//				String txPath=userJson.getString("userhead_small_url");
//				String txPath_large=userJson.getString("userhead_url");
//				//replyAuthor.txPath_large=replyAuthor.txPath_large.replace("F:/apachetomcat/webapps/", CommonValue.BASE_URL);						
//				user.txPath=new Picture(txPath_large,txPath);
//										
//
//				if(userJson.has("myTags"))
//				{
//					String labels=userJson.getString("myTags");
//					if(labels!=null && !labels.equals(""))
//					{
//						String []ls=labels.split("\\.");
//						for(String s:ls){
//							if(!s.equals("") && !user.label.contains(s))
//								user.label.add(s);}
//					}
//				}
				
				aNews.authorInfo=user;
				
				String s=newsJson.getString("shareShuoshuo");
				if(s!=null && !s.equals("null"))
				{
				   aNews.sharedNews=processANews(newsJson.getJSONObject("shareShuoshuo"));
				   if(aNews.sharedNews!=null)aNews.sharedNewId=aNews.sharedNews.shuoshuoId;
				}
				
				newsList.add(aNews);
			}
			data.newsList=newsList;
			return data;
	}
	
	
	public static News processANews(JSONObject newsJson)
	{
		if(newsJson==null)return null;
		
		News aNews=new News();
		try {
			aNews.shuoshuoId=newsJson.getString("shuoshuo_id");
			aNews.context=newsJson.getString("context");
			String timeStr=newsJson.getString("release_time");
			if(!StringUtils.empty(timeStr))aNews.releaseTime=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(timeStr);
			aNews.longitude=newsJson.getDouble("longitude");
			aNews.latitude=newsJson.getDouble("latitude");
			
			if(newsJson.has("type")){
				String t=newsJson.getString("type");
				if(!(t==null || t.equals("null")))
						aNews.shuoType=Integer.parseInt(t);
				}
			else aNews.shuoType=newsJson.getInt("shuotype");
			
			aNews.shareUrl=newsJson.getString("share_url");
			aNews.shareCount=newsJson.getInt("share_count");
			
			if(newsJson.has("share_url"))aNews.shareUrl=newsJson.getString("share_url");
			if(newsJson.has("desc_img"))aNews.descImg=newsJson.getString("desc_img");
			if(newsJson.has("desc_text"))aNews.descText=newsJson.getString("desc_text");
			
			List<Picture> picUrls=new ArrayList<Picture>();
			JSONArray picJsonList=newsJson.getJSONArray("pictures");
			for(int j=0;j<picJsonList.length();j++)
			{
				JSONObject picJson=picJsonList.getJSONObject(j);
				
				String pl=picJson.getString("picture_url");
				String ps=picJson.getString("picture_small_url");
				picUrls.add(new Picture(pl,ps));
			}
			
			aNews.setpictures(picUrls);
			
			JSONArray replyJsonList=newsJson.getJSONArray("comments");
			for(int j=0;j<replyJsonList.length();j++)
			{
				JSONObject replyJson=replyJsonList.getJSONObject(j);
				Reply reply=new Reply();
				reply.newsId=aNews.shuoshuoId;
				reply.shuoCommentId=replyJson.getString("shuo_comment_id");
				reply.toId=replyJson.getString("to_id");
				reply.context=replyJson.getString("context");
				reply.commentTime=replyJson.getString("comment_time");
				reply.replyType=replyJson.getInt("reply_type");
				
				
				JSONObject replyAuthorJson=replyJson.getJSONObject("authorInfo");
				UserInfo replyAuthor=processUser(replyAuthorJson);
//				replyAuthor.userId=replyAuthorJson.getString("user_id");
//				replyAuthor.userName=replyAuthorJson.getString("nickname");
//				replyAuthor.sex=replyAuthorJson.getInt("sex");
//				replyAuthor.schoolName=replyAuthorJson.getString("userschool");
//				
//				String txPath=replyAuthorJson.getString("userhead_small_url");
//				String txPath_large=replyAuthorJson.getString("userhead_url");
//				//replyAuthor.txPath_large=replyAuthor.txPath_large.replace("F:/apachetomcat/webapps/", CommonValue.BASE_URL);						
//				replyAuthor.txPath=new Picture(txPath_large,txPath);
//				if(replyAuthorJson.has("freindRate"))replyAuthor.friendRate=(int) (replyAuthorJson.getDouble("freindRate")*100);
//				if(replyAuthorJson.has("commonFreinds"))replyAuthor.commonFriends=replyAuthorJson.getInt("commonFreinds");
//				if(replyAuthorJson.has("commonTags"))replyAuthor.commonTags=replyAuthorJson.getInt("commonTags");
//				if(replyAuthorJson.has("interations"))replyAuthor.interations=replyAuthorJson.getInt("interations");
//			//	if(replyAuthorJson.has("distance"))replyAuthor.distance=replyAuthorJson.getDouble("distance");
//
//				if(replyAuthorJson.has("myTags"))
//				{
//					String labels=replyAuthorJson.getString("myTags");
//					//Log.d("MAIN", "getContact.do :"+labels);	
//					if(labels!=null && !labels.equals(""))
//					{
//						String []ls=labels.split("\\.");
//						for(String s:ls){
//							if(!s.equals("") && !replyAuthor.label.contains(s))
//								replyAuthor.label.add(s);}
//					}
//				}
				
				reply.author=replyAuthor;
				aNews.addReply(reply);
			}
			
			JSONArray priseJsonList=newsJson.getJSONArray("zans");
			for(int j=0;j<priseJsonList.length();j++)
			{
				JSONObject zanJson=priseJsonList.getJSONObject(j);
				Prise prise=new Prise();
				
				prise.priseId=zanJson.getString("zan_id");
				prise.priseTime=zanJson.getString("zan_time");
				JSONObject zanAuthorJson=zanJson.getJSONObject("authorInfo");
				UserInfo zanAuthor=processUser(zanAuthorJson);
//				zanAuthor.userId=zanAuthorJson.getString("user_id");
//				zanAuthor.sex=zanAuthorJson.getInt("sex");
//				zanAuthor.userName=zanAuthorJson.getString("nickname");
//				zanAuthor.schoolName=zanAuthorJson.getString("userschool");
//				if(zanAuthorJson.has("freindRate"))zanAuthor.friendRate=(int) (zanAuthorJson.getDouble("freindRate")*100);
//				if(zanAuthorJson.has("commonFreinds"))zanAuthor.commonFriends=zanAuthorJson.getInt("commonFreinds");
//				if(zanAuthorJson.has("commonTags"))zanAuthor.commonTags=zanAuthorJson.getInt("commonTags");
//				if(zanAuthorJson.has("interations"))zanAuthor.interations=zanAuthorJson.getInt("interations");
//				//if(zanAuthorJson.has("distance"))zanAuthor.distance=zanAuthorJson.getDouble("distance");
//				String txPath=zanAuthorJson.getString("userhead_small_url");
//				String txPath_large=zanAuthorJson.getString("userhead_url");
//				//replyAuthor.txPath_large=replyAuthor.txPath_large.replace("F:/apachetomcat/webapps/", CommonValue.BASE_URL);						
//				zanAuthor.txPath=new Picture(txPath_large,txPath);
//				
//				if(zanAuthorJson.has("myTags"))
//				{
//					String labels=zanAuthorJson.getString("myTags");
//					//Log.d("MAIN", "getContact.do :"+labels);	
//					if(labels!=null && !labels.equals(""))
//					{
//						String []ls=labels.split("\\.");
//						for(String s:ls){
//							if(!s.equals("") && !zanAuthor.label.contains(s))
//								zanAuthor.label.add(s);}
//					}
//				}
				prise.authorInfo=zanAuthor;
				aNews.addPrise(prise);
			}	
			
			JSONObject userJson=newsJson.getJSONObject("authorInfo");
			UserInfo user=processUser(userJson);
//			user.userId=userJson.getString("user_id");
//			user.sex=userJson.getInt("sex");
//			user.userName=userJson.getString("nickname");
//			user.schoolName=userJson.getString("userschool");
//			
//			String txPath=userJson.getString("userhead_small_url");
//			String txPath_large=userJson.getString("userhead_url");
//			//replyAuthor.txPath_large=replyAuthor.txPath_large.replace("F:/apachetomcat/webapps/", CommonValue.BASE_URL);						
//			user.txPath=new Picture(txPath_large,txPath);
//			if(userJson.has("freindRate"))user.friendRate=(int) (userJson.getDouble("freindRate")*100);
//			if(userJson.has("commonFreinds"))user.commonFriends=userJson.getInt("commonFreinds");
//			if(userJson.has("commonTags"))user.commonTags=userJson.getInt("commonTags");
//			if(userJson.has("interations"))user.interations=userJson.getInt("interations");
//			//if(userJson.has("distance"))user.distance=userJson.getDouble("distance");
//
//			if(userJson.has("myTags"))
//			{
//				String labels=userJson.getString("myTags");
//				//Log.d("MAIN", "getContact.do :"+labels);	
//				if(labels!=null && !labels.equals(""))
//				{
//					String []ls=labels.split("\\.");
//					for(String s:ls){
//						if(!s.equals("") && !user.label.contains(s))
//							user.label.add(s);}
//				}
//			}
			
			aNews.authorInfo=user;
			
			return aNews;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void getMyNews(//获取自己发表的动态
			final /*WCApplication  appContext,*/boolean init,final String apiKey, int page, int pageSize, 
			final ClientCallback callback)
	{
		final String url="mynews"+apiKey+page;
		
		
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("pageIndex", page+"");
		params.add("pageSize", pageSize+"");
		
		QYRestClient.post("getUserShuoshuo.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {				
				try {
					JSONObject json  = new JSONObject(new String(responseBody));
					if(json.getInt("status")==1)
					{
						callback.onSuccess(processNews(json));	
					}
					else
					{
						callback.onFailure(json.getString("msg"));
					}
				} catch (Exception e) {
					e.printStackTrace();
					callback.onError(e);
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	
	
	
	/*
	 * 
	 * @FormParam("pageSize") Integer pageSize,
	   @FormParam("pageIndex") Integer pageIndex,
	   @FormParam("longitude") Double longitude,
	   @FormParam("latitude") Double latitude
	 */
	
	public static void getNewsAround(
			 /*WCApplication  appContext,*/final String apiKey, 
			int page, int pageSize, 
			double longitude,double latitude,
			final ClientCallback callback)
	{
		
		
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("pageIndex", page+"");
		params.add("pageSize", pageSize+"");
		
		params.add("longitude", longitude+"");
		params.add("latitude", latitude+"");
		QYRestClient.post("getAroundShuoshuo.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {		
				
				String s=new String(responseBody);
				
				try {
					JSONObject json  = new JSONObject(new String(responseBody));
					if(json.getInt("status")==1)
					{
						callback.onSuccess(processNews(json));	
					}
					else
					{
						callback.onFailure(json.getString("msg"));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				} 
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	
	public static void publishNews(final String apiKey,String contentText, 
			List<String> filepaths,String shraredLink,int type,String descImg,String descText,double longitude,double latitude,
		    final ClientCallback callback)
	{
			RequestParams params = new RequestParams();
			List<File> myFiles =new ArrayList<File>();
			for(int i=0;i<filepaths.size();i++)
			{
				myFiles.add(new File(filepaths.get(i)));
			}
			
			params.add("apiKey", apiKey);
			params.add("context", contentText);
			
			JSONObject json=new JSONObject();
			try {
				json.put("longitude", longitude);
				json.put("latitude", latitude);
				json.put("pic_count", myFiles.size());
				json.put("context", contentText);
				json.put("share_url", shraredLink);
				json.put("type", type);
				json.put("desc_img", descImg);
				json.put("desc_text", descText);
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
			
			params.add("shuo_string", json.toString());
			
			int i=0;
			try {
					for(File f:myFiles)
						params.put("picture"+(i++), f,"image/jpeg");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			QYRestClient.post(myFiles.size()>0?"writeShuoshuo.do":"writeTextShuo.do", params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					//try {
					String s=new String(responseBody);
						try {
							JSONObject json = new JSONObject(new String(responseBody));
							if (json.getInt("status") == 1) {
								callback.onSuccess("发布成功");
							}
							else 
							{
								callback.onFailure("发布失败");
							}
						} catch (JSONException e) {
							e.printStackTrace();
							callback.onError(e);
						}
						
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					callback.onFailure(message_error);
				}
			});
	}
	
	
	

	public static void getUserPhotos(final String apiKey, String userId,int page,int pageSize, final ClientCallback callback)
	{

		
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("pageIndex", page+"");
		params.add("pageSize", pageSize+"");
		if(userId!=null && !userId.equals(""))
			params.add("userId", userId);
		
		QYRestClient.post("getUserPhotos.do", params, new AsyncHttpResponseHandler(){

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] responseBody) {
				// TODO Auto-generated method stub
				try {					
					JSONObject json=new JSONObject(new String(responseBody));
					if(json.getInt("status")==1)
					{
						
					JSONArray picJsonList=json.getJSONArray("picList");
					List<Picture>picDirList=new ArrayList<Picture>();
					
					for(int j=0;j<picJsonList.length();j++)
					{
						JSONObject picJson=picJsonList.getJSONObject(j);
						
						String pl=picJson.getString("picture_url");
						String ps=picJson.getString("picture_small_url");
						picDirList.add(new Picture(pl,ps));
					}
					
					callback.onSuccess(picDirList);
					}
					else
					{
						callback.onFailure(json.getString("msg"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("MAIN","Exception in getUserPhotos");
					callback.onError(e);
				}
				
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				callback.onFailure(message_error);
			}	
		});
	}
	
	public static void getUserInfo(/*WCApplication  appContext,*/final String apiKey, String userId, final ClientCallback callback) {
		
		final String url="userinfo"+userId;
		
		String res=null;
		
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
	    userId=userId.replace(XmppConnectionManager.BASE_XMPP_SERVER_NAME, "");
	    
		params.add("userId", userId);
		QYRestClient.post("getUserDetail.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					JSONObject json=new JSONObject(new String(responseBody));
					if(json.getInt("status")==1)
					{
					 UserInfo user=processUser(json.getJSONObject("userDetail"));
					 callback.onSuccess(user);
					}
					else 
					{
						callback.onFailure(json.getString("msg"));
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onError(e);
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	
	public static UserInfo processUser(JSONObject userJson) throws JSONException
	{
		
			UserInfo user=new UserInfo();
			user.userId=userJson.getString("user_id");
			user.sex=userJson.getInt("sex");
			user.userName=userJson.getString("nickname");
			user.birthDay=userJson.getString("userbirthday");
			user.schoolDay=userJson.getInt("school_year")+"";
			user.schoolName=userJson.getString("userschool");
			user.sex=userJson.getInt("sex");
			
			if(userJson.has("myTags"))
			{
				String labels=userJson.getString("myTags");
				if(labels!=null && !labels.equals(""))
				{
					String []ls=labels.split("\\.");
					for(String s:ls){
						if(!s.equals("") && !user.label.contains(s))
							user.label.add(s);}
				}
			}
			
			Double lat=userJson.getDouble("latitude");
			Double lng=userJson.getDouble("longitude");
			if(lat!=null && lng !=null )user.location=new LocationPoint(lat,lng);
			
			String txPath="",txPath_large="";
			if(userJson.has("userhead_small"))txPath=userJson.getString("userhead_small");
			else if(userJson.has("userhead_small_url"))txPath=userJson.getString("userhead_small_url");
			
			if(userJson.has("userhear"))txPath_large=userJson.getString("userhear");
			else if(userJson.has("userhead_url"))txPath_large=userJson.getString("userhead_url");
			
			if(userJson.has("freindRate"))user.friendRate=(int) (userJson.getDouble("freindRate")*100);
			else user.isMyFriend=1;
			if(userJson.has("commonFreinds"))user.commonFriends=userJson.getInt("commonFreinds");
			if(userJson.has("commonTags"))user.commonTags=userJson.getInt("commonTags");
			if(userJson.has("interations"))user.interations=userJson.getInt("interations");
			if(userJson.has("is_friend"))user.isMyFriend=userJson.getInt("is_friend");

			user.txPath=new Picture(txPath_large,txPath);
			
			return user;
	}
	

	
	public static void getNewsOfUser( /*WCApplication  appContext,*/
			 final String apiKey,String userId, int page, int pageSize, 
			final ClientCallback callback)
	{
		
				
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("pageIndex", page+"");
		params.add("pageSize", pageSize+"");
		params.add("userId", userId);

		QYRestClient.post("getUserShuoshuo.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				
				try {
					JSONObject json  = new JSONObject(new String(responseBody));
					if(json.getInt("status")==1)
					{
						callback.onSuccess(processNews(json));	
					}
					else
					{
						callback.onFailure(json.getString("msg"));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				}
		}
	
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	
	
	public static void getNewsById(
			 final String apiKey,String newsId, final ClientCallback callback)
	{
		
				
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("shuoshuoId", newsId);

		QYRestClient.post("getShuoshuoById.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					JSONObject json  = new JSONObject(new String(responseBody));
					if(json.getInt("status")==1)
					{
						callback.onSuccess(processANews(json.getJSONObject("shuoshuo")));	
					}
					else
					{
						callback.onFailure(json.getString("msg"));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				}
		}
	
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	
	public static void getNewsById(
			 final String apiKey,Set<String> newsIdSet, final ClientCallback callback)
	{
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		String idss="";
		for(String s:newsIdSet)
		{
			idss+=s+",";
		}
		idss=idss.replaceFirst(",$", "");
		params.add("ids", idss);
		
		QYRestClient.post("getShuoshuo.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {						
				String s=new String(responseBody);
				
				try {
					JSONObject json  = new JSONObject(new String(responseBody));
					if(json.getInt("status")==1)
					{
						callback.onSuccess(processNews(json));	
					}
					else
					{
						callback.onFailure(json.getString("msg"));
					}
				} catch (Exception e) {
					e.printStackTrace();
					callback.onError(e);
				} 
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	public static void update(final ClientCallback callback) {
		RequestParams params = new RequestParams();
		QYRestClient.get("http://www.hdletgo.com/check.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				AppUpdate update;
				try {
					update = AppUpdate.prase(new String(responseBody));
					callback.onSuccess(update);
				} catch (AppException e) {
					Logger.i(e);
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	
	public static void uploadHead(Context context,String apiKey, String filepath,String contentType, final ClientCallback callback) {
		
		if(!NetworkTool.networkIsAvailable(context))
		{
			callback.onFailure("网络连接不可用,请检查网络设置");
			return;
		}
		
		RequestParams params = new RequestParams();
		File myFile = new File(filepath);  
		params.add("apiKey", apiKey);
		try {
			params.put("file", myFile,contentType);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		QYRestClient.post("uploadHead.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					//Log.d("MAIN",new String(responseBody));
					JSONObject json = new JSONObject(new String(responseBody));
					if (json.getInt("status") == 1) {
						callback.onSuccess(json.getString("userhead_url"));
					}
					else callback.onFailure(json.getString("msg"));
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("MAIN","Exception in uploadFile");
					callback.onError(e);
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	
	
public static void uploadAnFile(Context context,String apiKey, String filepath,String contentType, final ClientCallback callback)
{
	if(!NetworkTool.networkIsAvailable(context))
	{
		callback.onFailure("网络连接不可用,请检查网络设置");
		return;
	}
	
	RequestParams params = new RequestParams();
	File myFile = new File(filepath);  
	params.add("apiKey", apiKey);
	try {
		params.put("file", myFile,contentType);
	} catch (FileNotFoundException e1) {
		e1.printStackTrace();
	}
	
	QYRestClient.post("uploadFile.do", params, new AsyncHttpResponseHandler() {
		@Override
		public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
			try {
				JSONObject json = new JSONObject(new String(responseBody));
				if (json.getInt("status") == 1) {
					callback.onSuccess(json.getString("fileLocation"));
				}
				else callback.onFailure(json.getString("msg"));
			} catch (Exception e) {
				e.printStackTrace();
				callback.onError(e);
			}
		}
		
		@Override
		public void onFailure(int statusCode, Header[] headers,
				byte[] responseBody, Throwable error) {
			callback.onFailure(message_error);
		}
	});
}
	
public static void uploadAnImage(Context context,String apiKey, String filepath, final ClientCallback callback) {	
	uploadAnFile(context,apiKey, filepath,"image/jpeg", callback);
	}


public static void uploadAVoice(Context context,String apiKey, String filepath, final ClientCallback callback) {
	uploadAnFile(context,apiKey, filepath,"audio/amr", callback);
}
	
	//批量上传文件
	public static void uploadFiles(String apiKey,String newsId,List<File> files, final ClientCallback callback) throws FileNotFoundException {
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("shuoshuo_id", newsId);
		params.put("pic_count", files.size());
		int i=0;
		for(File f:files)
		params.put("pictures"+i, files.get(i),"image/jpeg");
		
		
		QYRestClient.post("anzhuoUploadPicture.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					Logger.i(new String(responseBody));
					JSONObject json = new JSONObject(new String(responseBody));
					if (json.getInt("status") == 1) {
						JSONArray file = json.getJSONArray("files");
						JSONObject f = file.getJSONObject(0);
						String head = f.getString("fileId");
						callback.onSuccess(head);
					}
					else callback.onFailure(json.getString("msg"));
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("MAIN","Exception in anzhuoUploadPicture.do");
					callback.onError(e);
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {				
				callback.onFailure(message_error);
				
			}
		});
	}
	
	public static void updateUserLocation(String apiKey,final Double longitude,final Double latitude,final ClientCallback callback)
	{
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.put("longitude", longitude);
		params.put("latitude", latitude);
		
		JSONObject json=new JSONObject();
		
		try {
			json.put("longitude", longitude);
			json.put("latitude", latitude);
			params.add("location",json.toString());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		QYRestClient.post("updateLocation.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					Log.d("MAIN","FROM location..."+new String(responseBody));
					JSONObject json = new JSONObject(new String(responseBody));
					if (json.getInt("status") == 1) {
						callback.onSuccess("保存成功");
					}
					else callback.onFailure(json.getString("msg"));
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("MAIN","Exception updateLocation");
					callback.onError(e);
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}

	
	public static void modifiedUser(final WCApplication  appContext, String apiKey, String nickname, String head, String des, 
			String birthDay,String schoolDay,String schoolName,int sex,String labels,final ClientCallback callback) {

		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		if (StringUtils.notEmpty(nickname)) {
			params.add("nickname", nickname);
		}
		if (StringUtils.notEmpty(head)) {
			params.add("userhead", head);
		}
		if (StringUtils.notEmpty(des)) {
			params.add("description", des);
		}
		if (StringUtils.notEmpty(birthDay)) {
			params.add("userbirthday", birthDay);
		}
		
		if (StringUtils.notEmpty(schoolName)) {
			params.add("userschool", schoolName);
			Log.d("MAIN", "school:"+schoolName);
		}
		
		if (StringUtils.notEmpty(schoolDay)) {
			params.add("school_year", schoolDay);
		}
		
		if (sex>=0) {
			params.put("sex",sex+"");
		}
		
		if (StringUtils.notEmpty(labels)) {
			params.add("tag", labels);
		}
		
		QYRestClient.post("detailInfo.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					Log.d("MAIN","modify info->"+new String(responseBody));
					
					JSONObject json = new JSONObject(new String(responseBody));
					if (json.getInt("status") == 1) {
						UserInfo user = new UserInfo();
						
						JSONObject userDetail = json.getJSONObject("userDetail");
						String nickName = userDetail.getString("nickname");
						String userId=userDetail.getString("user_id");
						String userHead_large = userDetail.getString("userhear");
						String userHead=userDetail.getString("userhead_small");
						String birthday=userDetail.getString("userbirthday");
						String schoolday=userDetail.getInt("school_year")+"";
						int sex=userDetail.getInt("sex");
						String schoolName=userDetail.getString("userschool");
						
				        String longitude=null;//: 116.425182,
				        String latitude=null;//: 39.963019,
				        
				        if(userDetail.has("longitude"))
				        	longitude=userDetail.getString("longitude");
						if(userDetail.has("latitude"))
							latitude=userDetail.getString("latitude");
						
						if(!StringUtils.empty(longitude) && !StringUtils.empty(latitude))
						{
							double lng=Double.parseDouble(longitude);
							double lat=Double.parseDouble(latitude);
							user.location=new LocationPoint(lat, lat);
						}
						
						user.userName = nickName;
						
						user.txPath=new Picture(userHead_large,userHead);
						
						user.birthDay=birthday;
						user.schoolDay=schoolday;
						user.sex=sex;
						user.userId=userId;
						user.schoolName=schoolName;
						
						appContext.modifyLoginInfo(user);
						callback.onSuccess("保存成功");
					}
					else callback.onFailure(json.getString("msg"));
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("MAIN","Exception in detailInfo");
					callback.onError(e);
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				callback.onFailure(message_error);
			}
		});
	}
	
	public static void priseANews(String apiKey,String newsId,String userId,final ClientCallback callback)
	{
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("shuoshuo_id", newsId);
		params.add("from_id", userId);
		
		QYRestClient.post("dianZan.do", params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				//callback.onFailure("点赞失败");
				callback.onFailure(message_error);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				
				try {
					JSONObject json = new JSONObject(new String(arg2));
					if (json.getInt("status") == 1) 
						callback.onSuccess("点赞成功");
					else callback.onFailure("点赞失败");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				}
				
			}
			
		});
		
	}
	
	public static void cancelPrise(String apiKey,String newsId,String userId,String zanId,final ClientCallback callback)
	{
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("shuoshuo_id", newsId);
		params.add("from_id", userId);
		params.add("zan_id", zanId);
		
		QYRestClient.post("cancelZan.do", params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				callback.onFailure(message_error);
			}
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					JSONObject json = new JSONObject(new String(arg2));
					if (json.getInt("status") == 1) 
						callback.onSuccess("取消点赞成功");
					else callback.onFailure("取消点赞失败");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				}
			}
			
		});
		
	}
	
	
	public static void commentNews(String apiKey,String newsId,String toId,String fromId,
			String content,int replyType,final ClientCallback callback)
	{
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("shuoshuo_id", newsId);
		params.add("from_id", fromId);
		params.add("to_id", toId);
		params.add("context",content);
		params.add("reply_type", replyType+"");
		
		QYRestClient.post("commentShuoshuo.do", params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				callback.onFailure(message_error);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

				try {
					JSONObject json = new JSONObject(new String(arg2));
					
					if (json.getInt("status") == 1) 
						callback.onSuccess("评论成功");
					else callback.onFailure("评论失败");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				}
			}});
		
	}
	
	public static void deleteComment(String apiKey,String commentId,final ClientCallback callback)
	{
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("comment_id", commentId);
		
		QYRestClient.post("deleteComment.do", params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				callback.onFailure(message_error);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					JSONObject json = new JSONObject(new String(arg2));
					if (json.getInt("status") == 1) 
						callback.onSuccess("删除评论成功");
					else callback.onFailure("删除评论失败");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				}
			}});
		
	}
	
	public static void shareNews(String apiKey,String oldNewsId,double lng,double lat,
			String content,final ClientCallback callback)
	{
		
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("oldShuoshuoId", oldNewsId);
		params.add("longitude",lng+"");
		params.add("latitude",lat+"");
		params.add("context", content);
		
		QYRestClient.post("shareShuoshuo.do", params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				//callback.onFailure("分享失败");
				callback.onFailure(message_error);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				//Log.d("MAIN", new String(arg2));
				try {
					JSONObject json = new JSONObject(new String(arg2));
					if (json.getInt("status") == 1) 
						callback.onSuccess("分享成功");
					else callback.onFailure("分享失败");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				}
			}});
	}
	
	public static void shareAnUrl(String apiKey,String url,double lng,double lat,
			String content,final ClientCallback callback)
	{
		
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("url", url);
		params.add("longitude",lng+"");
		params.add("latitude",lat+"");
		params.add("context", content);
		
		QYRestClient.post("shareUrl.do", params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				//callback.onFailure("分享失败");
				callback.onFailure(message_error);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				//Log.d("MAIN", new String(arg2));
				try {
					JSONObject json = new JSONObject(new String(arg2));
					if (json.getInt("status") == 1) 
						callback.onSuccess("分享成功");
					else callback.onFailure("分享失败");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				}
			}});
	}
	
	public static void setUserLabel(String apiKey,List<String>labels,final ClientCallback callback)
	{
		if(labels==null ||labels.size()==0)return;
		String labelStr="";
		for(int i=0;i<labels.size();i++)labelStr+=labels.get(i)+(i==labels.size()-1?"":".");
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("self_tags", labelStr);
		QYRestClient.post("setTags.do", params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				callback.onFailure(message_error);
				//Log.d("MAIN", new String(arg2));
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
					try {
						JSONObject json = new JSONObject(new String(arg2));
						if (json.getInt("status") == 1) 
							callback.onSuccess(json.getString("msg"));
						else callback.onFailure(json.getString("msg"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callback.onError(e);
				}
				
			}});

	}
	
	public static void deleteANews(String apiKey,String newsId,
			final ClientCallback callback)
	{
		
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("shuoshuo_id", newsId);

		QYRestClient.post("deleteShuoshuo.do", params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				//callback.onFailure("删除说说失败");
				callback.onFailure(message_error);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				//Log.d("MAIN", new String(arg2));
				try {
					JSONObject json = new JSONObject(new String(arg2));
					if (json.getInt("status") == 1) 
						callback.onSuccess(json.getString("msg"));
					else callback.onFailure(json.getString("msg"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				callback.onError(e);
			}
		}});
	}
	
	public static void checkNews(String apiKey,
			final ClientCallback callback)
	{
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		
		QYRestClient.post("checkNews.do", params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				//callback.onFailure("请求通知失败");
				callback.onFailure(message_error);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				Log.d("MAIN","check news:"+ new String(arg2));
				callback.onSuccess(new String(arg2));
			}});
	}
	
	public static void handleFriendRequest(String apiKey,String applyId,int reply,
			final ClientCallback callback)
	{
		
		RequestParams params = new RequestParams();
		params.add("apiKey", apiKey);
		params.add("apply_id", applyId);
		params.add("result", reply+"");
		
		QYRestClient.post("confirmAddFriend.do", params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				//callback.onFailure("请求通知失败");
				callback.onFailure(message_error);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				Log.d("MAIN", new String(arg2));
				try {
					JSONObject json = new JSONObject(new String(arg2));
					if (json.getInt("status") == 1) 
						callback.onSuccess(json.getString("msg"));
					else callback.onFailure(json.getString("msg"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				callback.onError(e);
			}
		}});
	}
	
//	//下载
//	public static void downVoiceFromQiniu(Context context, final String url, final String format, final ClientCallback callback) {
//		QYRestClient.getWeb(context, url, null, new BinaryHttpResponseHandler() {
//			@Override
//			public void onSuccess(int arg0, Header[] arg1, byte[] binaryData) {
//				handleDownloadFile(binaryData, callback, url, format);
//			}
//			
//			@Override
//			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
//				callback.onFailure("语音地址错误");
//			}
//		});
//	}
//	public static void handleDownloadFile(final byte[] binaryData, final ClientCallback callback, final String url, final String format) {
//		final Handler handler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				switch (msg.what) {
//				case 1:
//					callback.onSuccess((String)msg.obj);
//					break;
//
//				default:
//					callback.onError((Exception)msg.obj);
//					break;
//				}
//			}
//		};
//		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
//		singleThreadExecutor.execute(new Runnable() {
//			@Override
//			public void run() {
//				Message msg = new Message();
//				String storageState = Environment.getExternalStorageState();	
//				String savePath = null;
//				if(storageState.equals(Environment.MEDIA_MOUNTED)){
//					savePath = AudioRecoderManager.CACHE_VOICE_FILE_PATH;
//					File dir = new File(savePath);
//					if(!dir.exists()){
//						dir.mkdirs();
//					}
//				}
//				String md5FilePath = savePath + MD5Util.getMD5String(url) + format;
//				File ApkFile = new File(md5FilePath);
//				if(ApkFile.exists()){
//					ApkFile.delete();
//				}
//				File tmpFile = new File(md5FilePath);
//				try {
//					FileOutputStream fos = new FileOutputStream(tmpFile);
//					fos.write(binaryData);
//					fos.close();
//					msg.what = 1;
//					msg.obj = md5FilePath;
//				} catch (Exception e) {
//					e.printStackTrace();
//					msg.what = -1;
//					msg.obj = e;
//				} 
//				handler.sendMessage(msg);
//			}
//		});
//	}

	public static void resetUserPassword(String phone,
			String newPass, final ClientCallback callback) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.add("phone", phone);
		params.add("newPassword", newPass);
		
		QYRestClient.post("forgetPassword.do", params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				callback.onFailure(message_error);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				//Log.d("MAIN", new String(arg2));
				try {
					JSONObject json = new JSONObject(new String(arg2));
					if (json.getInt("status") == 1) 
						callback.onSuccess(json.getString("msg"));
					else callback.onFailure(json.getString("msg"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				callback.onError(e);
			}
			}});
	}
}
