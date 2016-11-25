package com.deng.mychat.config;

import java.util.List;
import java.util.Set;

import com.deng.mychat.bean.NewsNotice;
import com.deng.mychat.db.DBManager;
import com.deng.mychat.db.SQLiteTemplate;
import com.deng.mychat.db.SQLiteTemplate.RowMapper;
import com.deng.mychat.model.Notice;

import tools.StringUtils;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

public class NewsNoticeManager {
	private static NewsNoticeManager noticeManager = null;
	private static DBManager manager = null;

	private NewsNoticeManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				CommonValue.LOGIN_SET, Context.MODE_PRIVATE);
		String databaseName = sharedPre.getString(CommonValue.USERID, null);
		manager = DBManager.getInstance(context, databaseName);
	}

	public static NewsNoticeManager getInstance(Context context) {

		if (noticeManager == null) {
			noticeManager = new NewsNoticeManager(context);
		}

		return noticeManager;
	}
	
	public static void destroy() {
		noticeManager = null;
		manager = null;
	}


	public long saveNotice(NewsNotice notice) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		
		if (StringUtils.notEmpty(notice.commentId)) {
			contentValues.put("comment_id",
					StringUtils.doEmpty(notice.commentId));
		}
		if (StringUtils.notEmpty(notice.priseId)) {
			contentValues.put("prise_id", StringUtils.doEmpty(notice.priseId));
		}
		if (StringUtils.notEmpty(notice.noticeTime)) {
			contentValues.put("notice_time", StringUtils.doEmpty(notice.noticeTime));
		}
		
		if (StringUtils.notEmpty(notice.userId)) {
			contentValues.put("user_id", StringUtils.doEmpty(notice.userId));
		}
		
		if (StringUtils.notEmpty(notice.newsId)) {
			contentValues.put("news_id", StringUtils.doEmpty(notice.newsId));
		}
		
		contentValues.put("type", notice.type);
		contentValues.put("status", notice.status);
		return st.insert("news_notice", contentValues);
	}


	public List<NewsNotice> getUnReadNoticeList() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<NewsNotice> list = st.queryForList(new RowMapper<NewsNotice>() {

			@Override
			public NewsNotice mapRow(Cursor cursor, int index) {
				NewsNotice notice = new NewsNotice();
				notice.noticeId=cursor.getString(cursor.getColumnIndex("_id"));
				notice.commentId=cursor.getString(cursor.getColumnIndex("comment_id"));
				notice.priseId=cursor.getString(cursor.getColumnIndex("prise_id"));
				notice.noticeTime=cursor.getString(cursor.getColumnIndex("notice_time"));
				notice.userId=cursor.getString(cursor.getColumnIndex("user_id"));
				notice.newsId=cursor.getString(cursor.getColumnIndex("news_id"));
				notice.type=cursor.getInt(cursor
						.getColumnIndex("type"));
				notice.status=cursor.getInt(cursor.getColumnIndex("status"));
				return notice;
			}

		}, "select * from news_notice where status=" + 0 + "", null);
		return list;
	}

	public void updateReadStatus(String id, Integer status) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("status", status);
		st.updateById("news_notice", id, contentValues);
	}


	public Integer getUnReadNoticeCount() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.getCount("select _id from news_notice where status=?",
				new String[] { "" + 0 });
	}

	
	public NewsNotice getNoticeById(String id) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.queryForObject(new RowMapper<NewsNotice>() {

			@Override
			public NewsNotice mapRow(Cursor cursor, int index) {
				NewsNotice notice = new NewsNotice();
				notice.noticeId=cursor.getString(cursor.getColumnIndex("_id"));
				notice.commentId=cursor.getString(cursor.getColumnIndex("comment_id"));
				notice.priseId=cursor.getString(cursor.getColumnIndex("prise_id"));
				notice.noticeTime=cursor.getString(cursor.getColumnIndex("notice_time"));
				notice.type=cursor.getInt(cursor
						.getColumnIndex("type"));
				notice.userId=cursor.getString(cursor.getColumnIndex("user_id"));
				notice.newsId=cursor.getString(cursor.getColumnIndex("news_id"));
				notice.status=cursor.getInt(cursor.getColumnIndex("status"));
				return notice;
			}

		}, "select * from news_notice where _id=?", new String[] { id });
	}


	public List<NewsNotice> getUnReadNoticeListByType(int type) {

		String sql;
		String[] str = new String[] { "" + 0, "" + type };
		sql = "select * from news_notice where status=? and type=? order by notice_time desc";
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<NewsNotice> list = st.queryForList(new RowMapper<NewsNotice>() {

			@Override
			public NewsNotice mapRow(Cursor cursor, int index) {
				NewsNotice notice = new NewsNotice();
				notice.noticeId=cursor.getString(cursor.getColumnIndex("_id"));
				notice.commentId=cursor.getString(cursor.getColumnIndex("comment_id"));
				notice.priseId=cursor.getString(cursor.getColumnIndex("prise_id"));
				notice.noticeTime=cursor.getString(cursor.getColumnIndex("notice_time"));
				notice.type=cursor.getInt(cursor
						.getColumnIndex("type"));
				notice.userId=cursor.getString(cursor.getColumnIndex("user_id"));
				notice.newsId=cursor.getString(cursor.getColumnIndex("news_id"));
				notice.status=cursor.getInt(cursor.getColumnIndex("status"));
				return notice;
			}
		}, sql, str);
		return list;
	}


	public List<NewsNotice> getNoticeListByType(int type) {

		String sql;
		String[] str = new String[] { "" + type };
		sql = "select * from news_notice where  type=? order by notice_time desc";
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<NewsNotice> list = st.queryForList(new RowMapper<NewsNotice>() {

			@Override
			public NewsNotice mapRow(Cursor cursor, int index) {
				NewsNotice notice = new NewsNotice();
				notice.noticeId=cursor.getString(cursor.getColumnIndex("_id"));
				notice.commentId=cursor.getString(cursor.getColumnIndex("comment_id"));
				notice.priseId=cursor.getString(cursor.getColumnIndex("prise_id"));
				notice.noticeTime=cursor.getString(cursor.getColumnIndex("notice_time"));
				notice.type=cursor.getInt(cursor
						.getColumnIndex("type"));
				notice.userId=cursor.getString(cursor.getColumnIndex("user_id"));
				notice.newsId=cursor.getString(cursor.getColumnIndex("news_id"));
				notice.status=cursor.getInt(cursor.getColumnIndex("status"));
				return notice;
			}
		}, sql, str);
		return list;
	}
	
	
	public Integer getUnReadNoticeCountByType(int type) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.getCount(
				"select _id from news_notice where status=? and type=?",
				new String[] { "" + 0, "" + type });
	}

	public List<NewsNotice> getUnReadNewsNoticeList() {

		String sql;
		//String[] str = new String[] { "" + 0, "" + type };
		sql = "select * from news_notice where status= 0 and ( type=1 or type =2) order by notice_time desc";
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<NewsNotice> list = st.queryForList(new RowMapper<NewsNotice>() {

			@Override
			public NewsNotice mapRow(Cursor cursor, int index) {
				NewsNotice notice = new NewsNotice();
				notice.noticeId=cursor.getString(cursor.getColumnIndex("_id"));
				notice.commentId=cursor.getString(cursor.getColumnIndex("comment_id"));
				notice.priseId=cursor.getString(cursor.getColumnIndex("prise_id"));
				notice.noticeTime=cursor.getString(cursor.getColumnIndex("notice_time"));
				notice.type=cursor.getInt(cursor
						.getColumnIndex("type"));
				notice.userId=cursor.getString(cursor.getColumnIndex("user_id"));
				notice.newsId=cursor.getString(cursor.getColumnIndex("news_id"));
				notice.status=cursor.getInt(cursor.getColumnIndex("status"));
				return notice;
			}
		}, sql,null);
		return list;
	}
	
	
	public Set<NewsNotice> getUnReadNewsNoticeSet() {

		String sql;
		//String[] str = new String[] { "" + 0, "" + type };
		sql = "select * from news_notice where status= 0 and ( type=1 or type =2) order by notice_time desc";
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		Set<NewsNotice> list = st.queryForSet(new RowMapper<NewsNotice>() {

			@Override
			public NewsNotice mapRow(Cursor cursor, int index) {
				NewsNotice notice = new NewsNotice();
				notice.noticeId=cursor.getString(cursor.getColumnIndex("_id"));
				notice.commentId=cursor.getString(cursor.getColumnIndex("comment_id"));
				notice.priseId=cursor.getString(cursor.getColumnIndex("prise_id"));
				notice.noticeTime=cursor.getString(cursor.getColumnIndex("notice_time"));
				notice.type=cursor.getInt(cursor
						.getColumnIndex("type"));
				notice.userId=cursor.getString(cursor.getColumnIndex("user_id"));
				notice.newsId=cursor.getString(cursor.getColumnIndex("news_id"));
				notice.status=cursor.getInt(cursor.getColumnIndex("status"));
				return notice;
			}
		}, sql,null);
		return list;
	}
	
	
	public List<NewsNotice> getReadNewsNoticeList() {

		String sql;
		//String[] str = new String[] { "" + 0, "" + type };
		sql = "select * from news_notice where status = 1 and ( type=1 or type =2) order by notice_time desc";
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<NewsNotice> list = st.queryForList(new RowMapper<NewsNotice>() {

			@Override
			public NewsNotice mapRow(Cursor cursor, int index) {
				NewsNotice notice = new NewsNotice();
				notice.noticeId=cursor.getString(cursor.getColumnIndex("_id"));
				notice.commentId=cursor.getString(cursor.getColumnIndex("comment_id"));
				notice.priseId=cursor.getString(cursor.getColumnIndex("prise_id"));
				notice.noticeTime=cursor.getString(cursor.getColumnIndex("notice_time"));
				notice.type=cursor.getInt(cursor
						.getColumnIndex("type"));
				notice.userId=cursor.getString(cursor.getColumnIndex("user_id"));
				notice.newsId=cursor.getString(cursor.getColumnIndex("news_id"));
				notice.status=cursor.getInt(cursor.getColumnIndex("status"));
				return notice;
			}
		}, sql,null);
		return list;
	}
	
	public Integer getUnReadNewsNoticeCount() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.getCount(
				"select _id from news_notice where status=0 and ( type= 1 or type=2 )",
				null);
	}


	
	public void delById(String noticeId) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		st.deleteById("news_notice", noticeId);
	}

	public void delAll() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		st.execSQL("delete from news_notice");
	}

	public int delNoticeHisByType(int type) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.deleteByCondition("news_notice", "type=?",
				new String[] { "" + type });
	}
	
	public int delNewsNotice() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.deleteByCondition("news_notice", "type=?",
				new String[] { "" + 1 })
				+
				st.deleteByCondition("news_notice", "type=?",
						new String[] { "" + 2 });
	}
	
}
