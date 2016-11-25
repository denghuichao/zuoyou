package com.deng.mychat.config;

import java.util.List;

import tools.StringUtils;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.Picture;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.db.DBManager;
import com.deng.mychat.db.SQLiteTemplate;
import com.deng.mychat.db.SQLiteTemplate.RowMapper;

public class ContactManager {

	private static ContactManager contactManager = null;
	private static DBManager manager = null;
	
	private ContactManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				CommonValue.LOGIN_SET, Context.MODE_PRIVATE);
		
		String databaseName = sharedPre.getString(CommonValue.USERID, null);		
		manager = DBManager.getInstance(context, databaseName);
	}
	
	public static ContactManager getInstance(Context context) {
		if (contactManager == null) {
			contactManager = new ContactManager(context);
		}
		return contactManager;
	}
	
	public static void destroy() {
		contactManager = null;
		manager = null;
	}
	
	/**
	 * 
	 * 保存朋友信息 or 更新朋友信息
	 * 
	 * @param user
		[userId] nvarchar, 
		[nickName] nvarchar, 
		[tx_path] nvarchar, 
		[tx_path_large] nvarchar, 
		[register_date] text,
		[school_name]nvarchar,
		[birthday]text,
		[schoolday] text,
		[sex] INTEGER,
		[user_lat] DOUBLE,
		[user_lng] DOUBLE,
		[is_my_friend]boolean,
		[is_online]boolean);");
	 */
	
	public void saveOrUpdateContact(UserInfo user,LocationPoint location) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("nick_name", user.userName);
		contentValues.put("tx_path", user.txPath.smallPicture);
		contentValues.put("tx_path_large", user.txPath.largePicPath);
		contentValues.put("register_date", user.registerDate);
		contentValues.put("school_name", user.schoolName);
		contentValues.put("birthday", user.birthDay);
		contentValues.put("schoolday", user.schoolDay);
		contentValues.put("sex", user.sex);
		if(user.friendRate>0)contentValues.put("friend_rate", user.friendRate);
		if(user.commonFriends>0)contentValues.put("common_friends", user.commonFriends);
		if(user.commonTags>0)contentValues.put("common_tags", user.commonTags);
		if(user.interations>0)contentValues.put("interations", user.interations);

		if(user.isMyFriend==1)
			contentValues.put("is_my_friend",user.isMyFriend);
		
		if(user.location!=null)
		{
			contentValues.put("user_lat", user.location.latitude);
			contentValues.put("user_lng", user.location.longitude);
		}
		
		double distanceFromMe=LocationPoint.distanceBetween(location, user.location);
		
		contentValues.put("distance_from_me",distanceFromMe);
		
		String labelStr="";
		for(int i=0;i<user.label.size();i++)
		{
			labelStr+=user.label.get(i);
			if(i!=user.label.size()-1)
				labelStr+=",";
		}
		
		contentValues.put("user_labels", labelStr);
		
		int s = st.update("im_all_contact", contentValues, "user_id =?", new String[]{user.userId});
		if (s == 0) {
			contentValues.put("user_id", StringUtils.doEmpty(user.userId));
			//297e9e794ca6dba4014cb2479e250163
			//297e9e794ca6dba4014cb26061ff0169
			//297e9e794ca6dba4014cb277c5c5016c
			if(user.isMyFriend==1)
				Log.d("DB", "insert contact:"+user.userId);
			st.insert("im_all_contact", contentValues);
			//insert contact:297e9e794c9db0d8014ca6ca74f9008e
		}
		
		st.execSQL("delete from im_all_contact where   "
				+ "_id in (select _id  from im_all_contact where user_id = \"" +user.userId+"\") "
				+ "and  _id not in (select min(_id) from im_all_contact where user_id =\"" +user.userId+"\");"
				);
	}
	
	public boolean isMyFriend(String userId)
	{
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		int num=st.getCount("select * from im_all_contact where is_my_friend= 1 and  user_id=?", new String[]{userId});
		return num>0;
	}
	
	public void  saveOrUpdateContact(List<UserInfo> userList,LocationPoint p)
	{
		for(UserInfo user:userList)
		{
			saveOrUpdateContact(user,p);
		}
	}
	
	public void deleteFriend(String userId)
	{
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("is_my_friend",0);	
		int s = st.update("im_all_contact", contentValues, "user_id =?", new String[]{userId});
	}
	
	/**
	 * 查找联系人
	 * 
	 * @param userId
	 * @return
	 * 
	 *  [user_id] nvarchar, 
		[nick_name] nvarchar, 
		[tx_path] nvarchar, 
		[tx_path_large] nvarchar, 
		[register_date] text,
		[school_name]nvarchar,
		[birthday]text,
		[schoolday] text,
		[sex] INTEGER,
		[user_lat] DOUBLE,
		[user_lng] DOUBLE,
		[is_my_friend]boolean,
		[is_online]boolean);");
	 */
	public UserInfo getContact(final String userId) {
		if (StringUtils.empty(userId)) {
			return null;
		}
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		UserInfo contact = st.queryForObject(new RowMapper<UserInfo>() {
			@Override
			public UserInfo mapRow(Cursor cursor, int index) {
				UserInfo user = new UserInfo();
				//cs=cursor.getColumnNames
				////
				user.userId = userId;
				user.userName = cursor.getString(cursor.getColumnIndex("nick_name"));
				String txPath= cursor.getString(cursor.getColumnIndex("tx_path"));
				String txPath_large=cursor.getString(cursor.getColumnIndex("tx_path_large"));
				
				user.txPath=new Picture(txPath_large,txPath);
				
				user.registerDate=cursor.getString(cursor.getColumnIndex("register_date"));
				user.schoolName=cursor.getString(cursor.getColumnIndex("school_name"));
				user.birthDay=cursor.getString(cursor.getColumnIndex("schoolday"));
				user.schoolDay=cursor.getString(cursor.getColumnIndex("schoolday"));
				user.sex=cursor.getInt(cursor.getColumnIndex("sex"));
				user.friendRate=cursor.getInt(cursor.getColumnIndex("friend_rate"));
				//user.distance=cursor.getDouble(cursor.getColumnIndex("distance_from_me"));
				user.commonFriends=cursor.getInt(cursor.getColumnIndex("common_friends"));
				user.commonTags=cursor.getInt(cursor.getColumnIndex("common_tags"));
				user.interations=cursor.getInt(cursor.getColumnIndex("interations"));
				
				Double lat=cursor.getDouble(cursor.getColumnIndex("user_lat"));
				Double lng=cursor.getDouble(cursor.getColumnIndex("user_lng"));
				if(lat!=null && lng !=null) user.location=new LocationPoint(lat,lng);
				
				user.isMyFriend=cursor.getInt(cursor.getColumnIndex("is_my_friend"));
				user.isOnline=cursor.getInt(cursor.getColumnIndex("is_online"));
				
				String labelStr=cursor.getString(cursor.getColumnIndex("user_labels"));
				String[]labels=labelStr.split(",");
				for(String s:labels)user.label.add(s);

				
				return user;
			}
		}, "select *  from im_all_contact where user_id=?", new String[]{userId});
		return contact;
	}
	
	//分页获取我的好友
	public List<UserInfo> getMyFriend(int pageIndex,int pageSize)
	{
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<UserInfo> contacts = st.queryForList(new RowMapper<UserInfo>() {
			@Override
			public UserInfo mapRow(Cursor cursor, int index) {
				UserInfo user = new UserInfo();
				//cs=cursor.getColumnNames
				////
				user.userId = cursor.getString(cursor.getColumnIndex("user_id"));
				Log.d("database", "id:"+user.userId);
				user.userName = cursor.getString(cursor.getColumnIndex("nick_name"));
				String txPath= cursor.getString(cursor.getColumnIndex("tx_path"));
				String txPath_large=cursor.getString(cursor.getColumnIndex("tx_path_large"));
				
				user.txPath=new Picture(txPath_large,txPath);
				
				user.registerDate=cursor.getString(cursor.getColumnIndex("register_date"));
				user.schoolName=cursor.getString(cursor.getColumnIndex("school_name"));
				user.birthDay=cursor.getString(cursor.getColumnIndex("schoolday"));
				user.schoolDay=cursor.getString(cursor.getColumnIndex("schoolday"));
				user.sex=cursor.getInt(cursor.getColumnIndex("sex"));
				user.friendRate=cursor.getInt(cursor.getColumnIndex("friend_rate"));
				
				//user.distance=cursor.getDouble(cursor.getColumnIndex("distance_from_me"));
				user.commonFriends=cursor.getInt(cursor.getColumnIndex("common_friends"));
				user.commonTags=cursor.getInt(cursor.getColumnIndex("common_tags"));
				user.interations=cursor.getInt(cursor.getColumnIndex("interations"));
				
				Double lat=cursor.getDouble(cursor.getColumnIndex("user_lat"));
				Double lng=cursor.getDouble(cursor.getColumnIndex("user_lng"));
				if(lat!=null && lng !=null) user.location=new LocationPoint(lat,lng);
				
				user.isMyFriend=cursor.getInt(cursor.getColumnIndex("is_my_friend"));
				user.isOnline=cursor.getInt(cursor.getColumnIndex("is_online"));
				
				String labelStr=cursor.getString(cursor.getColumnIndex("user_labels"));
				String[]labels=labelStr.split(",");
				for(String s:labels)user.label.add(s);
				
				return user;
			}
		}, "select * from im_all_contact  where is_my_friend= 1 order by distance_from_me", (pageIndex-1)*pageSize,pageSize);
		return contacts;
	}
	
	//分页获取周边联系人
	public List<UserInfo> getAroundContacts(int pageIndex,int pageSize)
	{
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);		
		List<UserInfo> contacts = st.queryForList(new RowMapper<UserInfo>() {
			@Override
			public UserInfo mapRow(Cursor cursor, int index) {
				UserInfo user = new UserInfo();
				//cs=cursor.getColumnNames
				//
				user.userId = cursor.getString(cursor.getColumnIndex("user_id"));
				user.userName = cursor.getString(cursor.getColumnIndex("nick_name"));
				String txPath= cursor.getString(cursor.getColumnIndex("tx_path"));
				String txPath_large=cursor.getString(cursor.getColumnIndex("tx_path_large"));
				
				user.txPath=new Picture(txPath_large,txPath);
				
				user.registerDate=cursor.getString(cursor.getColumnIndex("register_date"));
				user.schoolName=cursor.getString(cursor.getColumnIndex("school_name"));
				user.birthDay=cursor.getString(cursor.getColumnIndex("schoolday"));
				user.schoolDay=cursor.getString(cursor.getColumnIndex("schoolday"));
				user.sex=cursor.getInt(cursor.getColumnIndex("sex"));
				user.friendRate=cursor.getInt(cursor.getColumnIndex("friend_rate"));
				//user.distance=cursor.getDouble(cursor.getColumnIndex("distance_from_me"));
				user.commonFriends=cursor.getInt(cursor.getColumnIndex("common_friends"));
				user.commonTags=cursor.getInt(cursor.getColumnIndex("common_tags"));
				user.interations=cursor.getInt(cursor.getColumnIndex("interations"));
				Double lat=cursor.getDouble(cursor.getColumnIndex("user_lat"));
				Double lng=cursor.getDouble(cursor.getColumnIndex("user_lng"));
				if(lat!=null && lng !=null) user.location=new LocationPoint(lat,lng);
				
				user.isMyFriend=cursor.getInt(cursor.getColumnIndex("is_my_friend"));
				user.isOnline=cursor.getInt(cursor.getColumnIndex("is_online"));
				
				String labelStr=cursor.getString(cursor.getColumnIndex("user_labels"));
				String[]labels=labelStr.split(",");
				for(String s:labels)user.label.add(s);
				
				return user;
			}
		}, "select * from im_all_contact where distance_from_me < 10000 and is_my_friend = 0 order by distance_from_me", (pageIndex-1)*pageSize,pageSize);
		return contacts;
	}
}

