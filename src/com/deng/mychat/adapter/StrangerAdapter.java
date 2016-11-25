package com.deng.mychat.adapter;

import java.util.ArrayList;
import java.util.List;

import com.deng.mychat.R;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.WCApplication;
import com.deng.mychat.config.XmppConnectionManager;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.ui.ContactMainPageActivity;
import com.deng.mychat.view.MyGridView;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StrangerAdapter extends BaseAdapter{
	private Context context;
	private WCApplication appContext;
	private List<UserInfo> list = new ArrayList<UserInfo>();
	
	public StrangerAdapter(Context context,WCApplication appContext,List<UserInfo> list){
		this.context = context;
		this.appContext=appContext;
		this.list = list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		final UserInfo user = list.get(position);
		StrangerView h = null;
		if(view==null){
			h = new StrangerView();
			view = LayoutInflater.from(context).inflate(R.layout.stranger_item_layout, parent, false);
			h.userIcon = (ImageView)view.findViewById(R.id.userIcon);
			h.friendRate = (TextView)view.findViewById(R.id.friendRate);
			h.friendRateText= (TextView)view.findViewById(R.id.friendRateText);
			h.addContactButton=(Button)view.findViewById(R.id.addContactButton);
			h.strangerTitle=(TextView)view.findViewById(R.id.strangerTitle);
			h.labelView=(MyGridView)view.findViewById(R.id.labelView);
			view.setTag(h);
		}else{
			h = (StrangerView)view.getTag();
		}
		//头像异步加载
		((AppActivity)context).imageLoader.displayImage(user.txPath.smallPicture, h.userIcon, CommonValue.DisplayOptions.touxiang_options);

		h.userIcon.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//查看大图
			}
			
		});
		
		view.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(context,ContactMainPageActivity.class);
				intent.putExtra("isMyFriend", false);
				intent.putExtra("user", user);
				context.startActivity(intent);
			}
		});
		
		h.friendRate.setText(user.getFriendRate());
		h.friendRateText.setText("好友几率");
		h.addContactButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//加为好友
				addContact(user);
			}
			
		});
		
		int s=user.getSex();
		String sex="";
		if(s==1)
			sex="男  ";
		 else if(s==2)
			 sex="女 ";
		 else sex="";
		
		double dis=user.distanceFromMe(appContext.getLoginLocation());
		String distanceStr="";
		if(dis<1000)distanceStr=(int)dis+"m";
		else if(dis<10000)distanceStr=((int)dis)/1000+"km";
		else distanceStr="10km以外";
		
		h.strangerTitle.setText(user.getName()+" "+sex+" "+distanceStr);
		List<String>labelList=user.getLabel();
		h.labelView.setAdapter(new LabelAdapter(context,labelList));
		return view;
	}

   protected void addContact(UserInfo user) {
	   String apiKey=appContext.getLoginApiKey();
	   ApiClient.addFriend(apiKey, user.getId(), new ClientCallback(){

		@Override
		public void onSuccess(Object data) {
			// TODO Auto-generated method stub
			//Log.d("MAIN", "请求成功。。。。");
			//JSONObject json=new JSONObject(new String())
			Toast.makeText(context,(String)data, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onFailure(String message) {
			// TODO Auto-generated method stub
			//Log.d("MAIN", "请求失败。。。。");
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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

class StrangerView{
		public ImageView userIcon;
		public TextView friendRate;
		public TextView friendRateText;
		public Button addContactButton;
		
		public TextView strangerTitle;
		public MyGridView labelView;		
	}
}
