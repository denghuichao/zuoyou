package com.deng.mychat.ui;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.deng.mychat.R;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;


public class Welcome extends AppActivity{
	
	public static final String KEY_HELP_VERSION_SHOWN = "preferences_help_version_shown";
	
	private ImageView welcomeImg;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.welcome_page, null);
		
		
		setContentView(view);
		welcomeImg=(ImageView) findViewById(R.id.welcome_img);
		welcomeImg.setBackgroundResource(R.drawable.yd4);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2));
		double screenSize = diagonalPixels / (160*dm.density);
		appContext.saveScreenSize(screenSize);
		 if(!appContext.isFirstUse() && !showWhatsNewOnFirstLaunch())
		 {
			 AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
			 aa.setDuration(2000);
			 view.startAnimation(aa);
			 aa.setAnimationListener(new AnimationListener()
			 {
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationStart(Animation animation) {}
			
			 });
		 }
		 else redirectTo();
		 
		//registerTestUsers();
	}

	
	public void registerTestUsers()
	{
		for(int i=0;i<5;i++)
		 ApiClient.register((10086+i)+"", "0000", "左右研发团队", "左右研发团队", "", new  ClientCallback(){
			@Override
			public void onSuccess(Object data) {
				showToast("ok...");
			}

			@Override
			public void onFailure(String message) {
				showToast("f..."+message);
			}

			@Override
			public void onError(Exception e) {
				showToast("e...");
			}

			@Override
			public void onRequestLogIn() {
				// TODO Auto-generated method stub
				
			}});
	}
	
	private void redirectTo(){     //已经登录-->直接到首页  未登录-->到登录界面
		
		 if(appContext.isFirstUse() || showWhatsNewOnFirstLaunch())
		   {
		    	Intent intent = new Intent(this, GuideActivity.class);
		    	startActivity(intent);
		    	finish();
		   }
		 else if(!appContext.isLogin()){
			Intent intent = new Intent(this,Login.class);
			startActivity(intent);
			finish();	
		}
		else {
			startService();
			Intent intent = new Intent(this, Tabbar.class);
	        startActivity(intent);
	        finish();
		}
    }
	
	private boolean showWhatsNewOnFirstLaunch() {
	    try {
		      PackageInfo info = getPackageManager().getPackageInfo(CommonValue.PackageName, 0);
		      int currentVersion = info.versionCode;
		      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		      int lastVersion = prefs.getInt(KEY_HELP_VERSION_SHOWN, 0);
		      if (currentVersion > lastVersion) {
			        prefs.edit().putInt(KEY_HELP_VERSION_SHOWN, currentVersion).commit();
			        return true;
		      	}
	    	} catch (PackageManager.NameNotFoundException e) {
	    		e.printStackTrace();
	    	}
	    return false;
	}
}
