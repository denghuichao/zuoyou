package com.deng.mychat.ui;

import tools.AppManager;

import com.deng.mychat.R;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.XmppConnectionManager;
import com.deng.mychat.im.Chating;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MyPreferenceActivity extends AppActivity{
	
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mypreference_layout);
		init();
	}
	
	public void init()
	{
		
	}
	
	public void buttonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.cancelBtn:
				finish();break;
		}
	}
	
	public void onSelectPage(View view)
	{
		switch(view.getId())
		{
	
		case R.id.countAndSecurity:
			Intent intent1 =new Intent(this,AcountAndSecurityActivity.class);
			startActivity(intent1);
			break;
			
		case R.id.notification:
			Intent intent2 =new Intent(this,NotificationPrefActivity.class);
			startActivity(intent2);
			break;
			
		case R.id.about:
			Intent intent3 =new Intent(this,AboutActivity.class);
			startActivity(intent3);
			break;
			
		case R.id.privacy:
			Intent intent4 =new Intent(this,PrivacyActivity.class);
			startActivity(intent4);
			break;
			
		case R.id.distance:
			//Intent intent5 =new Intent(this,DistanceSettingActivity.class);
			//startActivity(intent5);
			break;
			
		case R.id.feedBack:
			Intent intent6 =new Intent(this,FeedbackActivity.class);
			
			String toId="297e9e794ca6dba4014cb2f01cbc018a"+XmppConnectionManager.BASE_XMPP_SERVER_NAME;
			intent6.putExtra("to", toId);

			startActivity(intent6);
			break;
			
		case R.id.logout:
			appContext.setUserLogout();
			stopService();
			AppManager.getAppManager().finishAllActivity();
			Intent intent =new Intent(this,Login.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			System.exit(0);
			break;
		}
	}
	
	
}
