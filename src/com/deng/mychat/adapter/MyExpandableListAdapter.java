package com.deng.mychat.adapter;

import java.util.ArrayList;
import java.util.List;

import com.deng.mychat.R;
import com.deng.mychat.adapter.FriendAdapter.FriendView;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.WCApplication;
import com.deng.mychat.config.XmppConnectionManager;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.ui.ContactActivity;
import com.deng.mychat.ui.ContactMainPageActivity;
import com.deng.mychat.ui.SuggestFriendActivity;
import com.deng.mychat.view.MyGridView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public  class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private ArrayList<String> groupList;
    private ArrayList<ArrayList<UserInfo>> childList;
    private Context mContext;
    private WCApplication appContext;
    public MyExpandableListAdapter(Context context,WCApplication appContext,ArrayList<String> groupList, ArrayList<ArrayList<UserInfo>> childList) {
        this.mContext=context;
       this.appContext=appContext;
    	this.groupList = groupList;
        this.childList = childList;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    private int selectedGroupPosition = -1;
    private int selectedChildPosition = -1;

    public void setSelectedPosition(int selectedGroupPosition, int selectedChildPosition) {
        this.selectedGroupPosition = selectedGroupPosition;
        this.selectedChildPosition = selectedChildPosition;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }

    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup parent) {
    	final UserInfo user = (UserInfo) getChild(groupPosition, childPosition);
    	ContactView h = null;
		if(view==null){
			h = new ContactView();
			view = LayoutInflater.from(mContext).inflate(R.layout.contact_item_layout, parent, false);
			h.userIcon = (ImageView)view.findViewById(R.id.userIcon);
			
			h.suggestButton=(Button)view.findViewById(R.id.suggestFriends);
			h.contactName=(TextView)view.findViewById(R.id.contactName);
			h.jobTitle=(TextView)view.findViewById(R.id.jobtitle);
			h.distance=(TextView)view.findViewById(R.id.distance);
			h.labelView=(TextView)view.findViewById(R.id.labelView);
			
			h.friendRateText= (TextView)view.findViewById(R.id.friendRateText);
			h.addContactButton=(Button)view.findViewById(R.id.addContactButton);
			
			h.friendOp=(LinearLayout)view.findViewById(R.id.friend_op);
			h.strangerOp=(LinearLayout)view.findViewById(R.id.stranger_op);
			h.bottomLine=view.findViewById(R.id.bottom_line);
			
			
			view.setTag(h);
		}else{
			h = (ContactView)view.getTag();
		}
		
		if(user.isMyFriend==1)
		{
			h.friendOp.setVisibility(View.VISIBLE);
			h.strangerOp.setVisibility(View.GONE);
		}
		else
		{
			h.friendOp.setVisibility(View.GONE);
			h.strangerOp.setVisibility(View.VISIBLE);
		}
		
		((AppActivity)mContext).imageLoader.displayImage(user.txPath.smallPicture, h.userIcon, CommonValue.DisplayOptions.touxiang_options);
		h.userIcon.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				goTocontactMainPage(user);
			}
			
		});
		
		h.suggestButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				suggestFriends(user);
			}	
		});
		
		h.friendRateText.setText(user.getFriendRate()+"好友概率");
		h.addContactButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				addContact(user);
			}
			
		});
		
		h.contactName.setText(user.getName());
		
		int s=user.getSex();
		
		if(s==1||s==2)
		{
			Drawable d=mContext.getResources().getDrawable(s==1?R.drawable.male:R.drawable.female);
			if(s==1)d.setBounds(0, 0, 30, 30);
			else d.setBounds(0, 0, 34, 34);
			h.contactName.setCompoundDrawables(null, null, d, null);
		}
		
		double dis=user.distanceFromMe(appContext.getLoginLocation());
		String distanceStr="";
		if(dis<1000)distanceStr=(int)dis+"m";
		else if(dis<1000000)distanceStr=((int)dis)/1000+"km";
		else distanceStr="1000km以外";
		
		h.distance.setText(distanceStr);
		
		List<String>labelList=user.getLabel();
		//h.labelView.setAdapter(new LabelAdapter(mContext,labelList));
		
		StringBuilder lables=new StringBuilder("");
		for(String str:labelList)
			lables.append(str+"  ");
		
		h.labelView.setText(lables);
		
		view.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//Log.d("MAIN", "chatting to "+model.userId);
					if(user.isMyFriend==1)
						((ContactActivity)mContext).createChat(user.userId+XmppConnectionManager.BASE_XMPP_SERVER_NAME);
					else goTocontactMainPage(user);}
		});
		//if(user.isMyFriend==1 && groupPosition==1)return null;
		return view;
    }

    protected void goTocontactMainPage(UserInfo user) {
		// TODO Auto-generated method stub
    	Intent intent=new Intent(mContext,ContactMainPageActivity.class);
		intent.putExtra("isMyFriend", user.isMyFriend);
		intent.putExtra("user", user);
		mContext.startActivity(intent);
	}

	public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    public int getGroupCount() {
        return groupList.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LinearLayout cotain = new LinearLayout(mContext);
        cotain.setPadding(15, 15, 15, 15);
        cotain.setGravity(Gravity.CENTER_VERTICAL);
        TextView textView = new TextView(mContext);
        textView.setPadding(5, 0, 0, 0);
        textView.setText(getGroup(groupPosition).toString());
        textView.setTextColor(Color.BLACK);
       
        ImageView imgIndicator = new ImageView(mContext);
        
        if (isExpanded) {
            imgIndicator.setBackgroundResource(R.drawable.ex_down);
        } else {
            imgIndicator.setBackgroundResource(R.drawable.ex_right);
        }
        cotain.addView(imgIndicator);
        cotain.addView(textView);
        return cotain;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
	private void suggestFriends(UserInfo user) {
		// TODO Auto-generated method stub
		Intent intent =new Intent(mContext,SuggestFriendActivity.class);
		intent.putExtra("userId", user.getId());
		mContext.startActivity(intent);
	}
	
	protected void addContact(UserInfo user) {
		   String apiKey=appContext.getLoginApiKey();
		   ApiClient.addFriend(apiKey, user.getId(), new ClientCallback(){

			@Override
			public void onSuccess(Object data) {
				// TODO Auto-generated method stub
				//Log.d("MAIN", "请求成功。。。。");
				//JSONObject json=new JSONObject(new String())
				Toast.makeText(mContext,(String)data, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(String message) {
				// TODO Auto-generated method stub
				//Log.d("MAIN", "请求失败。。。。");
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
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

	class ContactView{
		public ImageView userIcon;
		public Button suggestButton;
		
		public TextView contactName;//好友的
		public TextView jobTitle;
		public TextView distance;
		public TextView labelView;	//好友的
		
		public TextView friendRateText;//陌生人的
		public Button addContactButton;//陌生人的
		
		public LinearLayout friendOp;
		public LinearLayout strangerOp;
		
		public View bottomLine;
	}
}