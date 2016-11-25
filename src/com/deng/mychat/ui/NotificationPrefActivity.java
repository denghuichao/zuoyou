package com.deng.mychat.ui;

import android.os.Bundle;
import android.view.View;

import com.deng.mychat.R;
import com.deng.mychat.config.AppActivity;

public class NotificationPrefActivity extends AppActivity{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_pref_page);
		//init();
	}
	
	public void buttonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.cancelBtn:
				finish();break;
		}
	}
}
