package com.deng.mychat.ui;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import tools.AppManager;
import tools.StringUtils;
import tools.UIHelper;
import bean.Entity;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import com.deng.mychat.R;
import com.deng.mychat.bean.UserDetail;
import com.deng.mychat.bean.UserEntity;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.WCApplication;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.view.CustomProgressDialog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class IdentifyPage extends AppActivity{
	private String mobile;
	
	private EditText editVerifyCode;
	private EditText editAccount;
	private CustomProgressDialog loadingPd;
	private TextView title;
	private boolean reset;
	private Button sendCode;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSDK();
		loadData();
		initUi();
	}
	
	public void initSDK()
	{
		SMSSDK.initSDK(this, CommonValue.APP_KEY, CommonValue.APP_SCREET);
		
		EventHandler eh=new EventHandler(){

			@Override
			public void afterEvent(int event, int result, Object data) {
				
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
			
		};
		SMSSDK.registerEventHandler(eh);
	}
	
	public void initUi()
	{
		final View view = View.inflate(this, R.layout.identify_page, null);
		setContentView(view);
		editAccount=(EditText)findViewById(R.id.editTextAccount);
		editVerifyCode=(EditText)findViewById(R.id.editVerifyCode);
		sendCode=(Button)findViewById(R.id.sendCode);
		title=(TextView) findViewById(R.id.title);
		if(reset){
			title.setText("忘记密码");
		}

	}
	
	public void loadData()
	{

		reset=this.getIntent().getBooleanExtra("reset", false);
	}
	
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==1111)
			{
				sendCode.setText("输入收到的验证码("+timerCount+"s)");
				if(timerCount==0){
					stopTimer(timer);
					sendCode.setText("重新发送验证码");
					sendCode.setEnabled(true);
				}
				return;
			}
			
			
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.e("event", "event="+event);
			
			if(result==SMSSDK.RESULT_ERROR)
			{
				if(loadingPd!=null)loadingPd.dismiss();
				sendCode.setEnabled(true);
				showToast("验证错误，请重试");
				return;
			}
			
			if (result == SMSSDK.RESULT_COMPLETE) {
				//短信注册成功后，返回MainActivity,然后提示新好友
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
					if(loadingPd!=null)loadingPd.dismiss();
					showToast("验证成功");
					nextStep();
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					showToast("验证码已经发送，请注意查收");
					if(loadingPd!=null)loadingPd.dismiss();
					startTimer(timer);
				}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
					//Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
					
				}
			} else {
				((Throwable) data).printStackTrace();
			}
			
		}
		
	};
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
	}

	private Timer timer=new Timer();
	private int timerCount=60;
	private TimerTask task=new TimerTask(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.what=1111;
			handler.sendMessage(msg);
			timerCount--;
	}};
	
	private void  startTimer(Timer timer)
	{
		timer.schedule(task, 0, 1000);
	}

	private void stopTimer(Timer timer)
	{
		timerCount=60;
		if(timer==null)return;
		timer.cancel();
	}
	
	public void nextStep()
	{
		Intent intent = new Intent(IdentifyPage.this, Register.class);
		intent.putExtra("mobile", mobile);
		intent.putExtra("reset", reset);
		startActivity(intent);
		finish();
	}
	
	public void buttonClick(View v) {
		switch (v.getId()) {
		case R.id.sendCode:
			if(!StringUtils.empty(editAccount.getText().toString())){
				sendCode.setEnabled(false);
				loadingPd = CustomProgressDialog.show(this, "请稍后...", false, null);//UIHelper.showProgress(this, null, "正在登录", true);
				SMSSDK.getVerificationCode("86",editAccount.getText().toString());
				mobile=editAccount.getText().toString();
			}else {
				//Toast.makeText(this, "电话不能为空", 1).show();
				showToast("电话不能为空");
			}
			break;
		case R.id.verifyMe:
			if(!StringUtils.empty(editVerifyCode.getText().toString())){
				loadingPd = CustomProgressDialog.show(this, "正在验证...", false, null);//UIHelper.showProgress(this, null, "正在登录", true);
				SMSSDK.submitVerificationCode("86", mobile, editVerifyCode.getText().toString());
			}else {
				//Toast.makeText(this, "验证码不能为空", 1).show();
				showToast("验证码不能为空");
			}
			break;
		}
	}
}
