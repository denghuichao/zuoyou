package com.deng.mychat.ui;

import cn.smssdk.SMSSDK;

import com.deng.mychat.R;
import com.deng.mychat.bean.UserEntity;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.tools.AppManager;
import com.deng.mychat.view.CustomProgressDialog;
import com.deng.mychat.view.MyRelativeLayout;
import com.deng.mychat.view.MyRelativeLayout.OnSizeChangedListenner;
import com.deng.mychat.view.RoundImageView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Login extends AppActivity implements OnSizeChangedListenner{
	
	private CustomProgressDialog loadingPd;
	private InputMethodManager imm;
	
	private ImageView headbgImg;
	private ImageView headImg;
	
	private EditText accountET;
	private EditText passwordET;
	private Button loginButton;
	private MyRelativeLayout topLayout;
	private RelativeLayout headLL;
	private LinearLayout bottomLl;
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUi();
		//initSDK();
	}
	
	public void buttonClick(View v) {
		switch (v.getId()) {
		case R.id.registerButton:
			register();
			finish();
			break;
			
		case R.id.forgetButton:
			findPassword();
			finish();
			break;

		case R.id.loginButton:
			imm.hideSoftInputFromWindow(passwordET.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(accountET.getWindowToken(), 0);
			login();
			break;
		case R.id.top_ll:
			imm.hideSoftInputFromWindow(passwordET.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(accountET.getWindowToken(), 0);
			break;
		}
	}
	
	public void initUi()
	{
		setContentView(R.layout.login);
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		topLayout=(MyRelativeLayout)findViewById(R.id.top_ll);
		topLayout.setOnSizeChangedListenner(this) ;
		bottomLl=(LinearLayout)findViewById(R.id.bottom_ll);
		headLL=(RelativeLayout)findViewById(R.id.head_ly);
		
		accountET=(EditText)findViewById(R.id.editTextAccount);
		passwordET=(EditText)findViewById(R.id.editTextPassword);

		imm.hideSoftInputFromWindow(passwordET.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(accountET.getWindowToken(), 0);
		
		headbgImg=(ImageView )findViewById(R.id.headbg);
		headImg=(ImageView )findViewById(R.id.head);
		
		RelativeLayout.LayoutParams headbgPara=(RelativeLayout.LayoutParams) headbgImg.getLayoutParams();
		headbgPara.width=this.getResources().getDisplayMetrics().widthPixels;
		headbgPara.height=(int) (headbgPara.width*0.65);
		headbgImg.setLayoutParams(headbgPara);
			
	}
	
	
	
	public void register()
	{
		Intent intent = new Intent(Login.this, IdentifyPage.class);
		intent.putExtra("reset", false);
		startActivity(intent);
		
		
	}
	
	public void findPassword()
	{
		Intent intent = new Intent(Login.this, IdentifyPage.class);
		intent.putExtra("reset", true);
		startActivity(intent);
	}
	
	public void finishThisActivity()
	{
		finish();
	}
	
	public void login()
	{
		String account = accountET.getText().toString();
		final String password = passwordET.getText().toString();
		if (account.length() == 0 ||  password.length() ==0) {
			showToast("ÇëÊäÈëÕËºÅºÍÃÜÂë");
		}
		else {
			loadingPd = CustomProgressDialog.show(this, "ÕýÔÚµÇÂ¼...", false, null);//UIHelper.showProgress(this, null, "ÕýÔÚµÇÂ¼", true);
			ApiClient.login( account, password, new ClientCallback() {
				@Override
				public void onSuccess(Object data) {
					loadingPd.dismiss();
					UserEntity user = (UserEntity) data;
					//Log.d("MAIN", "onSuccess "+user.msg);
					if (user.status == 1) {
						appContext.saveLoginInfo(user);
						appContext.saveLoginPassword(password);
						saveLoginConfig(appContext.getLoginInfo());
						startService();
						appContext.setNoFirstUse();
						Intent intent = new Intent(Login.this, Tabbar.class);
						startActivity(intent);
						finishThisActivity();
						}
					else
					{
						showToast(user.msg);
					}
				}	
				@Override
				public void onFailure(String message) {
					loadingPd.dismiss();
					showToast(message);
				}
				
				@Override
				public void onError(Exception e) {
					loadingPd.dismiss();
				}
				@Override
				public void onRequestLogIn() {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}

	@Override
	public void onSizeChange(boolean paramBoolean, int w, int h) {
		// TODO Auto-generated method stub
		RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams)headLL.getLayoutParams();
		int height=this.getResources().getDisplayMetrics().heightPixels;
		 if(paramBoolean){//¼üÅÌµ¯³öÊ±
			 
			 lp.setMargins(0, -300, 0, 0);
			 //headLL.setVisibility(View.GONE);
			 bottomLl.setVisibility(View.GONE);
	        }else{ //¼üÅÌÒþ²ØÊ±
	        	 lp.setMargins(0, 0, 0, 0);
	        	 bottomLl.setVisibility(View.VISIBLE);
	        }
		 
		 headLL.setLayoutParams(lp);
	}	
}
