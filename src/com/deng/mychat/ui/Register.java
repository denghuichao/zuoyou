package com.deng.mychat.ui;

import java.util.HashMap;

import tools.AppManager;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import com.deng.mychat.R;
import com.deng.mychat.bean.UserEntity;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.view.CustomProgressDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Register extends AppActivity{
	private String mobile;
	private String password;
	private EditText editPassword;
	private EditText editAccount;
	private EditText editPasswordAgain;
	private CustomProgressDialog loadingPd;
	private TextView title;
	private  Button registerMe;
	private int clickCount=0;
	private boolean reset;
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadData();
		initUi();
	}
	
	public void initUi()
	{
		final View view = View.inflate(this, R.layout.register1, null);
		setContentView(view);
		editAccount=(EditText)findViewById(R.id.editTextAccount);
		editPassword=(EditText)findViewById(R.id.editPassword);
		editPasswordAgain=(EditText)findViewById(R.id.editPasswordAgain);
		editAccount.setText(mobile);
		title=(TextView) findViewById(R.id.title);
		registerMe=(Button)findViewById(R.id.registerMe);
		if(reset){
			editAccount.setClickable(false);
			editPassword.setHint("������");
			title.setText("��������");
			registerMe.setText("��������");
		}

	}
	
	public void loadData()
	{
		mobile=this.getIntent().getStringExtra("mobile");
		reset=this.getIntent().getBooleanExtra("reset", false);
	}
	
	private void resetPassword(String newPass)
	{
		loadingPd = CustomProgressDialog.show(this, "��������...", false, null);
		//AppManager.getAppManager().finishActivity(Register.class);
		ApiClient.resetUserPassword(mobile, newPass, new  ClientCallback(){

			@Override
			public void onSuccess(Object data) {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
				showToast("���������ã����μ���������");
				AppManager.getAppManager().finishActivity(Register.class);
			}

			@Override
			public void onFailure(String message) {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
				showToast(message);
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
			}

			@Override
			public void onRequestLogIn() {
				// TODO Auto-generated method stub
				
			}});

	}
	
	public void registerMe()//
	{  
		final String pw=editPassword.getText().toString();
		final String pwa=editPasswordAgain.getText().toString();
		
		if(pw.equals(""))
		{
			this.showToast("���벻��Ϊ��");
			return;
		}
		
		if(!pw.equals(pwa))
		{
			this.showToast("�����������벻һ��,����������");
			editPassword.setText("");
			editPasswordAgain.setText("");
			return ;
		}
		
		
		if(reset)
		{
			 resetPassword(pw);
			 return;
		 }
		 
		loadingPd = CustomProgressDialog.show(this, "����ע��...", false, null);//UIHelper.showProgress(this, null, "���ڵ�¼", true);
		ApiClient.register(mobile, pw, "ƽ��", "ƽ��", "", new  ClientCallback(){
			@Override
			public void onSuccess(Object data) {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
				UserEntity entity = (UserEntity) data;
				switch(entity.status)
				{
				case 1:showToast(entity.getMessage());
					  String apiKey=entity.apiKey;
					  password=pw;
					  nextStep(apiKey);//ע��ɹ������������û�����ҳ��
					  break;
				case -2:
					showToast("���ֻ������Ѿ�ע�ᣬ��ֱ�ӵ�¼");
					break;
				default :
					showToast("���������ϣ�ע��ʧ��");
					break;
				}
			}

			@Override
			public void onFailure(String message) {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
				showToast(message);
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
				//showToast("error");
			}

			@Override
			public void onRequestLogIn() {
				// TODO Auto-generated method stub
				
			}});
	
	}
	

	public void nextStep(final String apiKey)
	{
		
		//Intent intent =new Intent(Register.this,Register2.class); 
		//intent.putExtra("apiKey", apiKey);
		//intent.putExtra("mobile", mobile);
		//this.startActivity(intent);
		//setResult(RESULT_OK);
		//AppManager.getAppManager().finishActivity(Register.class);
		login();
	}
	
	public void login()
	{

		loadingPd = CustomProgressDialog.show(this, "���ڵ�¼...", false, null);//UIHelper.showProgress(this, null, "���ڵ�¼", true);
		ApiClient.login( mobile, password, new ClientCallback() {
				@Override
				public void onSuccess(Object data) {
					loadingPd.dismiss();
					UserEntity user = (UserEntity) data;
					Log.d("MAIN", "onSuccess "+user.msg);
					if (user.status == 1) {
						appContext.saveLoginInfo(user);
						appContext.saveLoginPassword(password);
						saveLoginConfig(appContext.getLoginInfo());
						startService();
						appContext.setNoFirstUse();
						finish();
						Intent intent = new Intent(Register.this, Register2.class);
						intent.putExtra("first_log", true);
						startActivity(intent);
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
	
	public void buttonClick(View v) {
		switch (v.getId()) {
		case R.id.registerMe:
			registerMe();
			break;
		case R.id.editTextAccount:
			clickCount++;
			if(clickCount%2==0)
			{
				RegisterPage registerPage = new RegisterPage();
				registerPage.setRegisterCallback(new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					if (result == SMSSDK.RESULT_COMPLETE) {
						HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
						String phone = (String) phoneMap.get("phone");
						editAccount.setText(phone);
					}
				}
			});
				registerPage.show(appContext);
			}
			else
			{
				this.showToast("����˺�������֤");
			}
		}
	}
}
