package com.deng.mychat.ui;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.deng.mychat.R;
import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.config.CommonValue;

public class FriendReportPage extends AppActivity{
	private UserInfo userInfo;
	private boolean isMyFriend;
	private TextView nameTv;
	private ImageView headImg;
	private TextView friendRateTv;
	private TextView comFriendTv;
	private TextView comTagTv;
	private TextView interationsTv;
	private TextView distanceTv;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_report_page);
		userInfo=(UserInfo) getIntent().getSerializableExtra("user");
		isMyFriend= userInfo.isMyFriend==1;
		initUI();
		showFriendReport();
	}
	
	public void buttonClick(View v)
	{
		switch(v.getId())
		{
		case R.id.cancelBtn:finish();break;
		case R.id.add_btn:addFriend();break;
		}
	}
	
	private void addFriend() {
		// TODO Auto-generated method stub
		 String apiKey=appContext.getLoginApiKey();
		   ApiClient.addFriend(apiKey, userInfo.getId(), new ClientCallback(){
			@Override
			public void onSuccess(Object data) {
				showToast((String)data);
			}

			@Override
			public void onFailure(String message) {
				showToast(message);
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub	
			}

			@Override
			public void onRequestLogIn() {
				// TODO Auto-generated method stub	
			}   
		  });
	}

	public void initUI()
	{
		nameTv=(TextView)findViewById(R.id.name);
		headImg=(ImageView)findViewById(R.id.head);
		friendRateTv=(TextView)findViewById(R.id.friend_rate);
		comFriendTv=(TextView)findViewById(R.id.com_friend);
		comTagTv=(TextView)findViewById(R.id.com_tag);
		interationsTv=(TextView)findViewById(R.id.interation);
		distanceTv=(TextView)findViewById(R.id.distance);
	}
	
	public void showFriendReport()
	{
		nameTv.setText(userInfo.userName);
		double distance=LocationPoint.distanceBetween(appContext.getLoginLocation(),userInfo.location);
		imageLoader.displayImage(userInfo.txPath.smallPicture, headImg, CommonValue.DisplayOptions.touxiang_options);
		friendRateTv.setText(Html.fromHtml("好友概率  "+"<strong>"+userInfo.friendRate+"%</strong>"));
		comFriendTv.setText(Html.fromHtml("<font  size='2'>"+"你们有  </font>"+"<font size='4' color=#c44e20><strong>"+userInfo.commonFriends+"</strong></font>"+"<font size='2'>"+" 个共同好友</font>"));
		comTagTv.setText(Html.fromHtml("<font  size='2'>"+"你们有 </font> "+"<font size='4' color=#26b19c><strong>"+userInfo.commonTags+"</strong></font>"+"<font size='2'>"+" 个共同标签</font>"));
		distanceTv.setText(Html.fromHtml("<font size='2'>"+"你们相距只有  </font>"+"<font size='4' color=#ff334c><strong>"+(long)distance+"m</strong></font>"));
		interationsTv.setText(Html.fromHtml("<font  size='2'>"+"你们相互评论点赞过  </font>"+"<font size='4' color=#428798><strong>"+userInfo.interations+"次</strong></font>"));
	}
}
