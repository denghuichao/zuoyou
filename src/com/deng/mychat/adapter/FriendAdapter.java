package com.deng.mychat.adapter;

import java.util.ArrayList;
import java.util.List;

import com.deng.mychat.R;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.WCApplication;
import com.deng.mychat.config.XmppConnectionManager;
import com.deng.mychat.ui.ContactActivity;
import com.deng.mychat.ui.ContactMainPageActivity;
import com.deng.mychat.ui.SuggestFriendActivity;
import com.deng.mychat.view.MyGridView;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendAdapter extends BaseAdapter{
	private Context context;
	private WCApplication appContext;
	private List<UserInfo> list = new ArrayList<UserInfo>();
	
	public FriendAdapter(Context context,WCApplication appContext,List<UserInfo> list){
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
		FriendView h = null;
		if(view==null){
			h = new FriendView();
			view = LayoutInflater.from(context).inflate(R.layout.contact_item_layout, parent, false);
			h.userIcon = (ImageView)view.findViewById(R.id.userIcon);
			
			h.suggestButton=(Button)view.findViewById(R.id.suggestFriends);
			h.friendTitle=(TextView)view.findViewById(R.id.strangerTitle);
			h.labelView=(MyGridView)view.findViewById(R.id.labelView);
			view.setTag(h);
		}else{
			h = (FriendView)view.getTag();
		}
		
		final UserInfo model = list.get(position);
		
		((AppActivity)context).imageLoader.displayImage(user.txPath.smallPicture, h.userIcon, CommonValue.DisplayOptions.touxiang_options);
		//h.userIcon.setImageResource(user.getImageId());
		h.userIcon.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context,ContactMainPageActivity.class);
				intent.putExtra("isMyFriend", true);
				intent.putExtra("user", user);
				context.startActivity(intent);
			}
			
		});
		h.suggestButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//加为好友
				//Toast.makeText(context, "给"+user.getName()+"推荐好友", Toast.LENGTH_SHORT).show();
				suggestFriends(user);
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
		
		h.friendTitle.setText(user.getName()+" "+sex+" "+" "+distanceStr);		
		List<String>labelList=user.getLabel();
		h.labelView.setAdapter(new LabelAdapter(context,labelList));
		
		view.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//Log.d("MAIN", "chatting to "+model.userId);
				((ContactActivity)context).createChat(model.userId+XmppConnectionManager.BASE_XMPP_SERVER_NAME);
			}
		});
		view.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				((ContactActivity)context).show2OptionsDialog(new String[]{"删除好友"}, model);
				return true;
			}
		});
		
		return view;
	}

	private void suggestFriends(UserInfo user) {
		// TODO Auto-generated method stub
		Intent intent =new Intent(context,SuggestFriendActivity.class);
		intent.putExtra("userId", user.getId());
		context.startActivity(intent);
	}

class FriendView{
		public ImageView userIcon;
		public Button suggestButton;
		
		public TextView friendTitle;
		public MyGridView labelView;	
	}
}

