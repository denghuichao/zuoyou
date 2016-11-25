package com.deng.mychat.ui;

import android.os.Bundle;
import android.view.View;

import com.deng.mychat.R;
import com.deng.mychat.config.AppActivity;

public class AcountAndSecurityActivity extends AppActivity{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acount_security_page);
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
