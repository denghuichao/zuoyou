package com.deng.mychat.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * SQLite数据库管理类
 * 
 * 主要负责数据库资源的初始化,关闭,以及获得DatabaseHelper帮助类操作
 * 
 */
public class DBManager {
	private int version =94;
	private String databaseName;

	// 本地Context对象
	private Context mContext = null;

	private static DBManager dBManager = null;

	/**
	 * 构�?函数
	 * 
	 * @param mContext
	 */
	private DBManager(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public static DBManager getInstance(Context mContext, String databaseName) {
		if (null == dBManager) {
			dBManager = new DBManager(mContext);
		}
		dBManager.databaseName = databaseName;
		return dBManager;
	}

	/**
	 * 关闭数据库,注意:当事务成功或者一次查询操作完毕时再关闭
	 */
	public void closeDatabase(SQLiteDatabase dataBase, Cursor cursor) {
		if (null != dataBase) {
			dataBase.close();
		}
		if (null != cursor) {
			cursor.close();
		}
	}

	/**
	 * 打开数据库
	 */
	public SQLiteDatabase openDatabase() {
		return getDatabaseHelper().getWritableDatabase();
	}

	/**
	 * 获取DataBaseHelper
	 * 
	 * @return
	 */
	public DataBaseHelper getDatabaseHelper() {
		return new DataBaseHelper(mContext, this.databaseName, null,
				this.version);
	}

}
