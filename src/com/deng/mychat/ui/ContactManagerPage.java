package com.deng.mychat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.deng.mychat.R;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.ContactManager;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.view.CustomProgressDialog;

public class ContactManagerPage extends AppActivity{
	private UserInfo userInfo;
	private boolean isMyFriend;
	
	private Button delBtn;
	
	private CustomProgressDialog loadingPd;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_contact_layout);
		userInfo=(UserInfo) getIntent().getSerializableExtra("user");
		isMyFriend= userInfo.isMyFriend==1;
		
		initUI();

	}
	
	public void initUI()
	{
		delBtn=(Button)findViewById(R.id.delBtn);
		if(isMyFriend)delBtn.setVisibility(View.VISIBLE);
		else delBtn.setVisibility(View.GONE);
	}
	
	public void buttonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.cancelBtn:
			finish();break;
		case R.id.delBtn:deleteContact();break;
		case R.id.backButton:break;
		case R.id.jubaoBtn:break;
		}
	} 
	
	public void deleteContact()
	{
		//ContactManager.getInstance(context).deleteFriend(userInfo.userId);
		loadingPd = CustomProgressDialog.show(this, "ÕýÔÚÉ¾³ý..", false, null);
		ApiClient.deleteFriend(appContext.getLoginApiKey(), userInfo.userId, new ClientCallback(){

			@Override
			public void onSuccess(Object data) {
				// TODO Auto-generated method stub
				ContactManager.getInstance(context).deleteFriend(userInfo.userId);
				loadingPd.dismiss();
				showToast("É¾³ýºÃÓÑ³É¹¦");
				finish();
			}

			@Override
			public void onFailure(String message) {
				// TODO Auto-generated method stub
				showToast(message);
				loadingPd.dismiss();
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				showToast("É¾³ýÊ§°Ü");
				loadingPd.dismiss();
			}

			@Override
			public void onRequestLogIn() {
				// TODO Auto-generated method stub
				
			}
			
		});
		//finish();
	}
}
