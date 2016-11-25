package com.deng.mychat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DataBaseHelper extends SDCardSQLiteOpenHelper {

	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE [im_msg_his] ([_id] INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT, [content] NVARCHAR , [msg_from] NVARCHAR, [msg_to] NVARCHAR, [msg_time] TEXT, [msg_type] INTEGER);");
		db.execSQL("CREATE TABLE [im_notice]  ([_id] INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT, [type] INTEGER, [title] NVARCHAR, [content] NVARCHAR, [notice_from] NVARCHAR, [notice_to] NVARCHAR, [notice_time] TEXT, [status] INTEGER);");
		db.execSQL("create table [im_friend]  ([_id] INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT, [userId] nvarchar, [nickName] nvarchar, [description] nvarchar, [avatar] nvarchar);");
		db.execSQL("create table [im_all_contact]  ([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, [user_id] nvarch, [nick_name] nvarchar, [tx_path] nvarchar, [tx_path_large] nvarchar, [register_date] text,[school_name]nvarchar,[user_labels] nvarchar,[birthday]text,[schoolday] text,[sex] INTEGER,[user_lat] DOUBLE,[user_lng] DOUBLE,[is_my_friend]INTEGER,[is_online]INTEGER,[distance_from_me] DOUBLE,[friend_rate] INTEGER ,[common_friends] INTEGER, [common_tags] INTEGER ,[interations] INTEGER);");
		db.execSQL("create table [im_news] ([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,[news_id] NVARCHAR,[user_id] NVARCHAR,[news_type] INTEGER,[desc_img] NVARCHAR,[desc_text] NVARCHAR,[content_text] NVARCHAR,[shared_count] INTEGER, [shared_url] NVARCHAR, [shared_news_id] NVARCHAR ,[release_time] TEXT,[news_lat] DOUBLE,[news_lng] DOUBLE,[distance_from_me] DOUBLE,FOREIGN KEY(user_id) REFERENCES im_all_contact(user_id));");//说说表
		db.execSQL("create table [im_comment] ([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,[comment_id] NVARCHAR,[news_id] NVARCHAR, [from_id] NVARCHAR,[to_id] NVARCHAR,[content] NVARCHAR,[comment_type] INTEGER,[comment_time] TEXT,[comment_lat] DOUBLE,[comment_lng] DOUBLE, FOREIGN KEY(news_id) REFERENCES im_news(news_id));");//评论列表
		db.execSQL("create table [im_prise] ([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,[prise_id] NVARCHAR,[from_id] NVARCHAR,[news_id] NVARCHAR,[prise_time] TEXT,[prise_lat] DOUBLE,[prise_lng] DOUBLE,FOREIGN KEY(news_id) REFERENCES im_news(news_id));");//点赞列表
		db.execSQL("create table [im_pic] ([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,[user_id] NVARCHAR, [news_id] NVARCHAR,[url] NVARCHAR,[url_large] NVARCHAR,FOREIGN KEY(news_id) REFERENCES im_news(news_id));");//用户相册列表
		db.execSQL("create table [im_friend_notice] ([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,[notice_id] NVARCHAR ,[user_id] NVARCHAR,[time_str] NVARCHAR, [desc] NVARCHAR,[type] INTEGER, [status] INTEGER);");//用户相册列表
		db.execSQL("CREATE TABLE [news_notice]  ([_id] INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT,[user_id] NVARCHAR,[news_id] NVARCHAR,[type] INTEGER, [status] INTEGER,[comment_id] NVARCHAR, [prise_id] NVARCHAR, [notice_time] TEXT);");//what's new
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.e
		db.execSQL("DROP TABLE IF EXISTS [im_msg_his] ");// ([_id] INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT, [content] NVARCHAR, [msg_from] NVARCHAR, [msg_to] NVARCHAR, [msg_time] TEXT, [msg_type] INTEGER);");
		db.execSQL("DROP TABLE IF EXISTS [im_notice] ");// ([_id] INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT, [type] INTEGER, [title] NVARCHAR, [content] NVARCHAR, [notice_from] NVARCHAR, [notice_to] NVARCHAR, [notice_time] TEXT, [status] INTEGER);");
		db.execSQL("DROP table IF EXISTS [im_friend]");//  ([_id] INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT, [userId] nvarchar, [nickName] nvarchar, [description] nvarchar, [avatar] nvarchar);");
		db.execSQL("DROP table IF EXISTS [im_all_contact] ");// ([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, [user_id] nvarchar, [nick_name] nvarchar, [tx_path] nvarchar, [tx_path_large] nvarchar, [register_date] text,[school_name]nvarchar,[user_labels] nvarchar,[birthday]text,[schoolday] text,[sex] INTEGER,[user_lat] DOUBLE,[user_lng] DOUBLE,[is_my_friend]INTEGER,[is_online]INTEGER);");
		db.execSQL("DROP table IF EXISTS [im_news] ");//([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,[news_id] NVARCHAR,[user_id] NVARCHAR,[news_type] INTEGER,[content_text] NVARCHAR, [shared_url] NVARCHAR, [shared_news_id] NVARCHAR,[news_lat] DOUBLE,[news_lng] DOUBLE)");//说说表
		db.execSQL("DROP table IF EXISTS [im_comment] ");//([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,[comment_id]NVARCHAR,[news_id]NVARCHAR, [from_id] NVARCHAR,[to_id] NVARCHAR,[content]NVARCHAR),[comment_type] INTEGER,[comment_time] TEXT,[comment_lat] DOUBLE,[comment_lng] DOUBLE);");//评论列表
		db.execSQL("DROP table IF EXISTS [im_prise]");// ([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,[prise_id] NVARCHAR,[news_id] NVARCHAR,[prise_time] TEXT,[prise_lat] DOUBLE,[prise_lng] DOUBLE);");//点赞列表
		db.execSQL("DROP table IF EXISTS [im_pic] ");//
		db.execSQL("DROP table IF EXISTS [im_friend_notice] ");//
		db.execSQL("DROP table IF EXISTS [news_notice] ");//
		this.onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}
}
