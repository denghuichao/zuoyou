package com.deng.mychat.ui;

import com.deng.mychat.R;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.util.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class SuggestFriendActivity extends AppActivity{
	private UserInfo user;
	private UserInfo randomUser;
	private TextView name1;
	private TextView name2;
	private ImageView userIcon1;
	private ImageView userIcon2;
	
	private ImageView indicatorImageView;
	private Animation indicatorAnimation;	
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggest_friend_page1);
		user=(UserInfo) this.getIntent().getSerializableExtra("user");
		//initUi();
	}
	
	public void initUi()
	{
		name1=(TextView)findViewById(R.id.name1);
		name2=(TextView)findViewById(R.id.name2);
		userIcon1=(ImageView)findViewById(R.id.user_icon1);
		userIcon2=(ImageView)findViewById(R.id.user_icon2);
		
		LayoutParams p1=(LayoutParams) userIcon1.getLayoutParams();
		p1.width=this.getResources().getDisplayMetrics().widthPixels/2-10;
		p1.height=p1.width;
		userIcon1.setLayoutParams(p1);
		
		LayoutParams p2=(LayoutParams) userIcon2.getLayoutParams();
		p2.width=this.getResources().getDisplayMetrics().widthPixels/2-10;
		p2.height=p2.width;
		userIcon2.setLayoutParams(p2);
		
		indicatorImageView = (ImageView) findViewById(R.id.xindicator);
		indicatorAnimation = AnimationUtils.loadAnimation(this, R.anim.refresh_button_rotation);
		indicatorAnimation.setDuration(500);
		indicatorAnimation.setInterpolator(new Interpolator() {
				    private final int frameCount = 10;
				    @Override
				    public float getInterpolation(float input) {
				        return (float)Math.floor(input*frameCount)/frameCount;
				    }
		});
		
		showUser1Info();
	}
	
	public void showUser2Info()
	{
		if(randomUser==null)return;
		
		 String picUrl=randomUser.txPath.smallPicture;
		 picUrl=picUrl.replace("F:/apachetomcat/webapps/",CommonValue.BASE_URL);
		 
		 int sex=randomUser.getSex();
		   DisplayImageOptions default_options = new DisplayImageOptions.Builder()
		   .showStubImage(R.drawable.avatar_placeholder)
			.showImageForEmptyUri(R.drawable.avatar_placeholder)
			.showImageOnFail(R.drawable.avatar_placeholder)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.displayer(new FadeInBitmapDisplayer(100))
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();			 
			imageLoader.displayImage(picUrl, userIcon2, default_options);
			
			String name=randomUser.userName;
			
			name2.setText(name);
			if(sex==1)name2.setCompoundDrawables(null, null, 
					this.getResources().getDrawable(R.drawable.nanren), null);
			else if(sex==2)
				name2.setCompoundDrawables(null, null, 
						this.getResources().getDrawable(R.drawable.nvren), null);
			else name2.setCompoundDrawables(null, null, null, null);
	}
	
	public void showUser1Info()
	{
		if(user==null)return;
		String picUrl=user.txPath.largePicPath;
		 picUrl=picUrl.replace("F:/apachetomcat/webapps/",CommonValue.BASE_URL);
		 
		 int sex=user.getSex();
		   DisplayImageOptions default_options = new DisplayImageOptions.Builder()
		   .showStubImage(R.drawable.avatar_placeholder)
			.showImageForEmptyUri(R.drawable.avatar_placeholder)
			.showImageOnFail(R.drawable.avatar_placeholder)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.displayer(new FadeInBitmapDisplayer(100))
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();			 
			imageLoader.displayImage(picUrl, userIcon1, default_options);
			
			String name=user.userName;
			//int sex=user.getSex();
			
			name1.setText(name);
			if(sex==1)name1.setCompoundDrawables(null, null, 
					this.getResources().getDrawable(R.drawable.nanren), null);
			else if(sex==2)
				name1.setCompoundDrawables(null, null, 
						this.getResources().getDrawable(R.drawable.nvren), null);
			else name1.setCompoundDrawables(null, null, null, null);
	}
	
	public void generateRandomUser()
	{
		indicatorImageView.startAnimation(indicatorAnimation);
		indicatorImageView.setVisibility(View.VISIBLE);
		
		
		//indicatorImageView.setVisibility(View.INVISIBLE);
		//indicatorImageView.clearAnimation();
	}
	
	public void buttonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.cancelBtn:finish();break;
		case R.id.friendButton:suggestAsFriend();break;
		case R.id.loverButton:suggestAsLover();break;
		case R.id.change_btn:changeContact();break;
		}
	}


	private void suggestAsLover() {
		// TODO Auto-generated method stub
		
	}

	private void suggestAsFriend() {
		// TODO Auto-generated method stub
		
	}
	
	private void changeContact()
	{
		
	}
}
