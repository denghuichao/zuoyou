/**
 * wechatdonal
 */
package com.deng.mychat.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import tools.AppException;
import tools.Logger;

import com.google.gson.Gson;

public class ContactEntity implements Serializable{
	public int status;
	public String msg="";
	public List<UserInfo> userList;
	
	public static ContactEntity parse(String res) throws IOException, AppException{
		ContactEntity data = new ContactEntity();
		Logger.i(res);
		try {
			Gson gson = new Gson();
			data = gson.fromJson(res, ContactEntity.class);
		} catch (Exception e) {
			Logger.i(res);
			Logger.i(e);
			throw AppException.json(e);
		}
		return data;
	}
}
