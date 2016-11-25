package com.deng.mychat.config;

import java.util.List;

import tools.StringUtils;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.deng.mychat.bean.FriendNotice;
import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.db.DBManager;
import com.deng.mychat.db.SQLiteTemplate;
import com.deng.mychat.db.SQLiteTemplate.RowMapper;

public class FriendNoticeManager {

	private static FriendNoticeManager contactManager = null;
	private static DBManager manager = null;
	private Context context;
	
	private FriendNoticeManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				CommonValue.LOGIN_SET, Context.MODE_PRIVATE);
		
		String databaseName = sharedPre.getString(CommonValue.USERID, null);
		
		manager = DBManager.getInstance(context, databaseName);
		this.context=context;
	}
	
	public static FriendNoticeManager getInstance(Context context) {
		if (contactManager == null) {
			contactManager = new FriendNoticeManager(context);
		}
		return contactManager;
	}
	
	public static void destroy() {
		contactManager = null;
		manager = null;
	}
	

	
	public void saveOrUpdateFriendNotice(FriendNotice notice,LocationPoint location) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		UserInfo user=notice.user;
		ContentValues contentValues = new ContentValues();
		contentValues.put("user_id", user.userId);
		contentValues.put("desc", notice.desc);
		contentValues.put("type", notice.type);
		contentValues.put("status", notice.processStatus);
		contentValues.put("time_str", notice.timeStr);
						
		
		int s = st.update("im_friend_notice", contentValues, "notice_id =?", new String[]{notice.noticeId});
		if (s == 0) {
			contentValues.put("notice_id", StringUtils.doEmpty(notice.noticeId));
			st.insert("im_friend_notice", contentValues);
			
		}
		
		ContactManager.getInstance(context).saveOrUpdateContact(user, location);
	}

	
	public void delById(String noticeId) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		st.deleteByField("im_friend_notice", "notice_id", noticeId);
	}


	public void delAll() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		st.execSQL("delete from im_friend_notice");
	}
	
	
	public List<FriendNotice> getNoticeList() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<FriendNotice> list = st.queryForList(new RowMapper<FriendNotice>() {

			@Override
			public FriendNotice mapRow(Cursor cursor, int index) {
				FriendNotice notice = new FriendNotice();
				notice.noticeId=cursor.getString(cursor.getColumnIndex("notice_id"));
				notice.desc=cursor.getString(cursor.getColumnIndex("desc"));
				notice.processStatus=cursor.getInt(cursor.getColumnIndex("status"));
				notice.type=cursor.getInt(cursor.getColumnIndex("type"));
				notice.timeStr=cursor.getString(cursor.getColumnIndex("time_str"));
				notice.user=ContactManager.getInstance(context).getContact(cursor.getString(cursor.getColumnIndex("user_id")));
				return notice;
			}

		}, "select * from im_friend_notice",null);
		return list;
	}
	
	public int getUnHandledNoticeCount() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.getCount(
				"select _id from im_friend_notice where status = 0",
				null);
	}
}

