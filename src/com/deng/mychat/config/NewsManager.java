
package com.deng.mychat.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import tools.AppContext;
import tools.StringUtils;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.News;
import com.deng.mychat.bean.Picture;
import com.deng.mychat.bean.Prise;
import com.deng.mychat.bean.Reply;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.db.DBManager;
import com.deng.mychat.db.SQLiteTemplate;
import com.deng.mychat.db.SQLiteTemplate.RowMapper;

public class NewsManager {

	private static NewsManager newsManager = null;
	private static DBManager manager = null;
	private Context context;
	
	private NewsManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				CommonValue.LOGIN_SET, Context.MODE_PRIVATE);
		
		String databaseName = sharedPre.getString(CommonValue.USERID, null);
		Log.d("database","databaseName:-->"+databaseName);
		
		manager = DBManager.getInstance(context, databaseName);
		this.context=context;
	}
	
	public static NewsManager getInstance(Context context) {
		if (newsManager == null) {
			newsManager = new NewsManager(context);
		}
		return newsManager;
	}
	
	public static void destroy() {
		newsManager = null;
		manager = null;
	}
	/**
	 * 
	 * table [im_news] ([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	 * [news_id] NVARCHAR,
	 * [user_id] NVARCHAR,
	 * [news_type] INTEGER,
	 * [content_text] NVARCHAR, 
	 * [shared_url] NVARCHAR, 
	 * [shared_news_id] NVARCHAR,
	 * [news_lat] DOUBLE,
	 * [news_lng] DOUBLE)");//说说表

	 */
	public void saveOrUpdateNews(LocationPoint location,News news) {
		
		delANews(news.shuoshuoId);
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		UserInfo user=news.getUser();
		contentValues.put("user_id", user.userId);
		contentValues.put("news_type", news.shuoType);
		contentValues.put("content_text", news.context);
		contentValues.put("shared_url", news.shareUrl);
		contentValues.put("shared_news_id", news.sharedNewId); 
		contentValues.put("shared_count", news.shareCount); 
		contentValues.put("release_time", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(news.releaseTime));
		
		contentValues.put("desc_img", news.descImg);
		contentValues.put("desc_text", news.descText);
		
		contentValues.put("news_lat", news.latitude);
		contentValues.put("news_lng", news.longitude);
		
		
		double distanceFromMe=LocationPoint.distanceBetween(location, new LocationPoint(news.latitude,news.longitude));
		
		contentValues.put("distance_from_me", distanceFromMe);
		
		String sid=news.shuoshuoId;
		
		int s = st.update("im_news", contentValues, "news_id =?", new String[]{news.shuoshuoId});
		
		
		if (s == 0) {
			contentValues.put("news_id", StringUtils.doEmpty(news.shuoshuoId));
			st.insert("im_news", contentValues);
		}
		
		
		saveOrUpdateReplys(news.getReply(),location);
		saveOrUpdatePrises(news.getPrise(),location);
		saveUpdatePics(news.getPictures(),news.shuoshuoId,user.userId);
		
		if(news.sharedNews!=null)
			saveOrUpdateNews(location,news.sharedNews);
		
		ContactManager.getInstance(context).saveOrUpdateContact(user, location);
	}
	

		public void delANews(String newsId) {
			SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
			
			st.deleteByField("im_news", "news_id", newsId);
			st.deleteByField("im_comment", "news_id", newsId);
			st.deleteByField("im_prise", "news_id", newsId);
			st.deleteByField("im_pic", "news_id", newsId);
		}
		
		public News getANews(String newsId)
		{
			SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
			News news=st.queryForObject(new RowMapper<News>(){

				@Override
				public News mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					News news=new News();
					news.shuoshuoId = cursor.getString(cursor.getColumnIndex("news_id"));
					
					news.shuoType= cursor.getInt(cursor.getColumnIndex("news_type"));
					news.context=cursor.getString(cursor.getColumnIndex("content_text"));
					news.shareUrl=cursor.getString(cursor.getColumnIndex("shared_url"));
					news.sharedNewId=cursor.getString(cursor.getColumnIndex("shared_news_id"));
					news.shareCount=cursor.getInt(cursor.getColumnIndex("shared_count"));
					String timeStr=cursor.getString(cursor.getColumnIndex("release_time"));
					
					news.descImg=cursor.getString(cursor.getColumnIndex("desc_img"));
					news.descText=cursor.getString(cursor.getColumnIndex("desc_text"));

					
					if(!StringUtils.empty(timeStr))
						try {
							news.releaseTime=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(timeStr);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}
					
					news.latitude=cursor.getDouble(cursor.getColumnIndex("news_lat"));
					news.longitude=cursor.getDouble(cursor.getColumnIndex("news_lng"));
					
					news.pictures=getPicsOfNews(news.shuoshuoId);
					news.comments=getCommentsOfNews(news.shuoshuoId);
					news.zans=getPrisesOfNews(news.shuoshuoId);
					news.authorInfo = ContactManager.getInstance(context).getContact(cursor.getString(cursor.getColumnIndex("user_id")));
					return news;
				}
				
			}, "select * from im_news where news_id="+"\""+newsId+"\"",null);
			return news;
		}
	
		public void delAllNews() {
			SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
			
			st.execSQL("delete * from im_news");
			st.execSQL("delete * im_comment");
			st.execSQL("delete * im_prise");
			st.execSQL("delete * im_pic");
			st.execSQL("delete * from im_news");

		}

	
	public void saveOrUpdateNews(LocationPoint location,List<News> newsList) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		//delAllNews();
		
		for(News news:newsList)
		{
			saveOrUpdateNews(location,news);
	  }
	}
	
	//		db.execSQL("create table [im_pic] ([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	//[user_id] NVARCHAR,
	//[news_id] NVARCHAR,
	//[url] NVARCHAR,
	//FOREIGN KEY(news_id) REFERENCES im_news(news_id));");//用户相册列表

	public void saveUpdatePics(List<Picture> pictureUrls,String newsId,String userId ) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		for(Picture p:pictureUrls)
		{
				ContentValues contentValues = new ContentValues();
				contentValues.put("user_id", userId);
				//contentValues.put("url", StringUtils.doEmpty(url));	
				contentValues.put("news_id", StringUtils.doEmpty(newsId));
				int s = st.update("im_pic", contentValues, "url =?", new String[]{p.smallPicture});
				if (s == 0) {
				
					contentValues.put("url", StringUtils.doEmpty(p.smallPicture));
					contentValues.put("url_large", StringUtils.doEmpty(p.largePicPath));
					st.insert("im_pic", contentValues);
				}
		}
	}

	public List<Picture>getPicsOfNews(String newsId)
	{
		if (StringUtils.empty(newsId)) {
			return null;
		}
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Picture> rList = st.queryForList(new RowMapper<Picture>(){
			@Override
			public Picture mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				
				String url = cursor.getString(cursor.getColumnIndex("url"));
				String url_large=cursor.getString(cursor.getColumnIndex("url_large"));
				return new Picture(url_large,url);
			}},"select * from im_pic where news_id="+"\""+newsId+"\"",null);
		
		return rList;
	}
	
	// table [im_prise] ([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	//[prise_id] NVARCHAR,
	//[news_id] NVARCHAR,
	//[prise_time] TEXT,
	//[prise_lat] DOUBLE,
	//[prise_lng] DOUBLE,
	//FOREIGN KEY(news_id) REFERENCES im_news(news_id));");//点赞列表
	public void saveOrUpdatePrises(List<Prise> priseList,LocationPoint mp) {
		// TODO Auto-generated method stub
		for(Prise p:priseList)
		{
			saveOrUpdatePrise(p,mp);
		}
	}

	public List<Prise>getPrisesOfNews(String newsId)
	{
		if (StringUtils.empty(newsId)) {
			return null;
		}
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Prise> rList = st.queryForList(new RowMapper<Prise>(){
			@Override
			public Prise mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Prise p=new Prise();
				p.priseId = cursor.getString(cursor.getColumnIndex("prise_id"));
				p.newsId = cursor.getString(cursor.getColumnIndex("news_id"));
				p.priseTime= cursor.getString(cursor.getColumnIndex("prise_time"));
				p.userId=cursor.getString(cursor.getColumnIndex("from_id"));
				return p;
			}},"select * from im_prise where news_id="+"\""+newsId+"\"",null);
		
		for(Prise p:rList)
			p.authorInfo = ContactManager.getInstance(context).getContact(p.userId);
		return rList;
	}
	
	public void saveOrUpdatePrise(Prise p,LocationPoint mlocation)
	{
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();

		UserInfo u=p.authorInfo;
		contentValues.put("news_id", p.newsId);
		contentValues.put("prise_time", p.priseTime);
		LocationPoint location=u.location;
		if(location!=null)
			{
			contentValues.put("prise_lat", u.location.latitude);
			contentValues.put("prise_lng", u.location.longitude);
			}
		contentValues.put("from_id", u.userId);
		int s = st.update("im_prise", contentValues, "prise_id =?", new String[]{p.priseId});
		
		if(s==0)
		{
			contentValues.put("prise_id", p.priseId);
			st.insert("im_prise", contentValues);
		}
		
		ContactManager.getInstance(context).saveOrUpdateContact(u, mlocation);
	}
	
//	// table [im_comment] 
//	([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,
//			[comment_id] NVARCHAR,
//			[news_id] NVARCHAR, 
//			[from_id] NVARCHAR,
//			[to_id] NVARCHAR,
//			[content] NVARCHAR,
//			[comment_type] INTEGER,
//			[comment_time] TEXT,
//			[comment_lat] DOUBLE,
//			[comment_lng] DOUBLE,
//			FOREIGN KEY(news_id) REFERENCES im_news(news_id));");//评论列表

	public List<Reply>getCommentsOfNews(String newsId)
	{
		if (StringUtils.empty(newsId)) {
			return null;
		}
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Reply> rList = st.queryForList(new RowMapper<Reply>(){
			@Override
			public Reply mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Reply r=new Reply();
				r.shuoCommentId = cursor.getString(cursor.getColumnIndex("comment_id"));
				r.newsId = cursor.getString(cursor.getColumnIndex("news_id"));
				r.fromId= cursor.getString(cursor.getColumnIndex("from_id"));
				r.toId=cursor.getString(cursor.getColumnIndex("to_id"));
				r.context=cursor.getString(cursor.getColumnIndex("content"));
				r.replyType=cursor.getInt(cursor.getColumnIndex("comment_type"));
				//r.
				return r;
			}},"select * from im_comment where news_id="+"\""+newsId+"\"",null);
		
		for(Reply r:rList)
			r.author = ContactManager.getInstance(context).getContact(r.fromId);
		
		return rList;
	}
	
	

	public void saveOrUpdateComment(Reply r,LocationPoint mlocation)
	{
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();

		UserInfo u=r.author;
		contentValues.put("news_id", r.newsId);
		contentValues.put("from_id", u.userId);
		contentValues.put("to_id", r.toId);
		contentValues.put("content", r.context);
		
		contentValues.put("comment_type", r.replyType);
		contentValues.put("comment_time", r.commentTime);
		
		LocationPoint location=u.location;
		if(location!=null)
			{
			contentValues.put("comment_lat", location.latitude);
			contentValues.put("comment_lng", location.longitude);
			}
		

		int s = st.update("im_comment", contentValues, "comment_id =?", new String[]{r.shuoCommentId});
		
		if(s==0)
		{
			contentValues.put("comment_id", r.shuoCommentId);
			st.insert("im_comment", contentValues);
		}
		
		ContactManager.getInstance(context).saveOrUpdateContact(u, mlocation);
	}
	
	public void saveOrUpdateReplys(List<Reply>rList,LocationPoint mp)
	{
		for(Reply r:rList)
		{
			saveOrUpdateComment(r,mp);
		}
	}
	
	
	/**
	 * 获取指定User的说说
	 * @param userId
	 * @return
	 * 
	 *  table [im_news] ([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
			[news_id] NVARCHAR,
			[user_id] NVARCHAR,
			[news_type] INTEGER,
			[content_text] NVARCHAR,
			[shared_url] NVARCHAR, 
			[shared_news_id] NVARCHAR,
			[news_lat] DOUBLE,
			[news_lng] DOUBLE)");
			 * @param rowMapper
	 */
	public List<News> getNewsFromContact(final String userId,int pageIndex,int pageSize) {
		if (StringUtils.empty(userId)) {
			return null;
		}
				
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<News> newsList = st.queryForList(new RowMapper<News>(){
			@Override
			public News mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				News news=new News();
				news.shuoshuoId = cursor.getString(cursor.getColumnIndex("news_id"));
				news.userId = cursor.getString(cursor.getColumnIndex("user_id"));
				news.shuoType= cursor.getInt(cursor.getColumnIndex("news_type"));
				news.context=cursor.getString(cursor.getColumnIndex("content_text"));
				news.shareUrl=cursor.getString(cursor.getColumnIndex("shared_url"));
				news.sharedNewId=cursor.getString(cursor.getColumnIndex("shared_news_id"));
				news.shareCount=cursor.getInt(cursor.getColumnIndex("shared_count"));
				news.descImg=cursor.getString(cursor.getColumnIndex("desc_img"));
				news.descText=cursor.getString(cursor.getColumnIndex("desc_text"));
				String timeStr=cursor.getString(cursor.getColumnIndex("release_time"));
				if(!StringUtils.empty(timeStr))
					try {
						news.releaseTime=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(timeStr);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
				
				news.latitude=cursor.getDouble(cursor.getColumnIndex("news_lat"));
				news.longitude=cursor.getDouble(cursor.getColumnIndex("news_lng"));
				
				news.pictures=getPicsOfNews(news.shuoshuoId);
				news.comments=getCommentsOfNews(news.shuoshuoId);
				news.zans=getPrisesOfNews(news.shuoshuoId);
				
				
				return news;
			}}, "select * from im_news where user_id="+"\""+userId+"\" order by release_time desc",
			(pageIndex - 1) * pageSize,//start 
			pageSize) ;//maxSize
		
		for(News n:newsList)
		{
			n.authorInfo=ContactManager.getInstance(context).getContact(n.userId);
			if(n.sharedNewId!=null)
				n.sharedNews=getANews(n.sharedNewId);
		}
		return newsList;
	}	
	
	public List<News> getNewsFromFriends(int pageIndex,int pageSize) {
		
				
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<News> newsList = st.queryForList(new RowMapper<News>(){
			@Override
			public News mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				News news=new News();
				news.shuoshuoId = cursor.getString(cursor.getColumnIndex("news_id"));
				news.userId=cursor.getString(cursor.getColumnIndex("user_id"));
				news.shuoType= cursor.getInt(cursor.getColumnIndex("news_type"));
				news.context=cursor.getString(cursor.getColumnIndex("content_text"));
				news.shareUrl=cursor.getString(cursor.getColumnIndex("shared_url"));
				news.sharedNewId=cursor.getString(cursor.getColumnIndex("shared_news_id"));
				news.shareCount=cursor.getInt(cursor.getColumnIndex("shared_count"));
				news.descImg=cursor.getString(cursor.getColumnIndex("desc_img"));
				news.descText=cursor.getString(cursor.getColumnIndex("desc_text"));
				news.latitude=cursor.getDouble(cursor.getColumnIndex("news_lat"));
				news.longitude=cursor.getDouble(cursor.getColumnIndex("news_lng"));
				
				String timeStr=cursor.getString(cursor.getColumnIndex("release_time"));
				if(!StringUtils.empty(timeStr))
					try {
						news.releaseTime=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(timeStr);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
				
				news.pictures=getPicsOfNews(news.shuoshuoId);
				news.comments=getCommentsOfNews(news.shuoshuoId);
				news.zans=getPrisesOfNews(news.shuoshuoId);

				return news;
			}}, "select * from im_news where user_id in (select user_id from im_all_contact where is_my_friend =1)  order by release_time desc",
			(pageIndex - 1) * pageSize,//start 
			pageSize) ;//maxSize
		
		for(News n:newsList)
		{
			n.authorInfo = ContactManager.getInstance(context).getContact(n.userId);
			if(n.sharedNewId!=null)
					n.sharedNews=getANews(n.sharedNewId);
		}
		
		return newsList;
	}	
	
	public List<News> getAroundNews(int pageIndex,int pageSize) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<News> newsList = st.queryForList(new RowMapper<News>(){
			@Override
			public News mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				News news=new News();
				news.shuoshuoId = cursor.getString(cursor.getColumnIndex("news_id"));
				news.userId=cursor.getString(cursor.getColumnIndex("user_id"));
				news.shuoType= cursor.getInt(cursor.getColumnIndex("news_type"));
				news.context=cursor.getString(cursor.getColumnIndex("content_text"));
				news.shareUrl=cursor.getString(cursor.getColumnIndex("shared_url"));
				news.sharedNewId=cursor.getString(cursor.getColumnIndex("shared_news_id"));
				news.shareCount=cursor.getInt(cursor.getColumnIndex("shared_count"));
				news.descImg=cursor.getString(cursor.getColumnIndex("desc_img"));
				news.descText=cursor.getString(cursor.getColumnIndex("desc_text"));
				String timeStr=cursor.getString(cursor.getColumnIndex("release_time"));
				if(!StringUtils.empty(timeStr))
					try {
						news.releaseTime=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(timeStr);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
				
				news.latitude=cursor.getDouble(cursor.getColumnIndex("news_lat"));
				news.longitude=cursor.getDouble(cursor.getColumnIndex("news_lng"));
				
				news.pictures=getPicsOfNews(news.shuoshuoId);
				news.comments=getCommentsOfNews(news.shuoshuoId);
				news.zans=getPrisesOfNews(news.shuoshuoId);
				
				
				
				return news;
			}}, "select * from im_news where distance_from_me <10000 order by release_time desc",
			(pageIndex - 1) * pageSize,//start 
			pageSize) ;//maxSize
		
		for(News n:newsList)
		{
			n.authorInfo = ContactManager.getInstance(context).getContact(n.userId);
			if(n.sharedNewId!=null)
					n.sharedNews=getANews(n.sharedNewId);
		}
		
		return newsList;
	}	
	
	public List<Picture> getUserAlbum(String userId)
	{
		if (StringUtils.empty(userId)) {
			return null;
		}
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Picture> rList = st.queryForList(new RowMapper<Picture>(){
			@Override
			public Picture mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				String s=cursor.getString(cursor.getColumnIndex("url"));
				String l=cursor.getString(cursor.getColumnIndex("url_large"));
				return new Picture(l,s);
			}},"select * from im_pic where user_id="+"\""+userId+"\"",null);
		
		return rList;
	}	
}

