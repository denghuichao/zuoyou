package com.deng.mychat.adapter;

import java.util.ArrayList;
import java.util.List;

import tools.StringUtils;

import com.deng.mychat.R;
import com.deng.mychat.bean.FriendNotice;
import com.deng.mychat.bean.News;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.FriendNoticeManager;
import com.deng.mychat.config.NewsManager;
import com.deng.mychat.config.WCApplication;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.ui.ContactMainPageActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FriendNoticeAdapter extends BaseAdapter{
	private Context context;
	private WCApplication appContext;
	private List<FriendNotice> list = new ArrayList<FriendNotice>();
	//private Handler myHandler=new Handler();
	
	public FriendNoticeAdapter(Context context,WCApplication appContext,List<FriendNotice> list){
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
	public View getView(int position, View view, final ViewGroup parent) {
		 final FriendNotice notice= list.get(position);
		 final UserInfo user=notice.user;
	   	 final ViewHolder h;    
		if(view==null){
			h = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.friend_notice_item_layout, parent, false);
			h.userIcon = (ImageView)view.findViewById(R.id.userIcon);
			
			h.name=(TextView)view.findViewById(R.id.contactName);
			h.processButton=(Button)view.findViewById(R.id.processBtn);
			
			h.desc=(TextView)view.findViewById(R.id.desc);
			h.statusText=(TextView)view.findViewById(R.id.processStatus);
			view.setTag(h);
		}else{
			h = (ViewHolder)view.getTag();
		}
		
		
		((AppActivity)context).imageLoader.displayImage(user.txPath.smallPicture, h.userIcon, CommonValue.DisplayOptions.touxiang_options);
		h.name.setText(user.userName);
		
		
		if(!StringUtils.empty(notice.desc))
			h.desc.setText(notice.desc);
		
		if(notice.processStatus==0)
		{
			h.processButton.setVisibility(View.VISIBLE);
			h.statusText.setVisibility(View.GONE);
		}
		else
		{
			h.processButton.setVisibility(View.GONE);
			h.statusText.setVisibility(View.VISIBLE);
			
			h.statusText.setText("已处理");
		}
		
		
		h.processButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new PopupWindows(context,parent,notice,h);
			}
			
		});
		
		view.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent=new Intent(context,ContactMainPageActivity.class);
					intent.putExtra("isMyFriend", user.isMyFriend);
					intent.putExtra("user", user);
					context.startActivity(intent);
				}
		});
	
		return view;
	}

	 public class PopupWindows extends PopupWindow {

			public PopupWindows(final Context mContext, View parent,final FriendNotice notice,final ViewHolder h) {

				View view = View
						.inflate(mContext, R.layout.op_pop_layout, null);
				view.startAnimation(AnimationUtils.loadAnimation(mContext,
						R.anim.fade_ins));
				LinearLayout ll = (LinearLayout) view
						.findViewById(R.id.ll_popup);
				ll.startAnimation(AnimationUtils.loadAnimation(mContext,
						R.anim.push_bottom_in_2));

				setWidth(LayoutParams.FILL_PARENT);
				setHeight(LayoutParams.WRAP_CONTENT);
				setBackgroundDrawable(new BitmapDrawable());
				setFocusable(true);
				setOutsideTouchable(true);
				setContentView(view);
				setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);  
				setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); 
				showAtLocation(parent, Gravity.BOTTOM, 0, 0);
				update();

				Button bt1 = (Button) view
						.findViewById(R.id.item_popupwindows_agree);
				Button bt2 = (Button) view
						.findViewById(R.id.item_popupwindows_deline);
				Button bt3=(Button) view
						.findViewById(R.id.item_popupwindows_ignore);
				Button bt4 = (Button) view
						.findViewById(R.id.item_popupwindows_cancel);
				
				bt1.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						handleFriendRequest(notice.noticeId,1,notice,h);
						dismiss();
					}
					
				});
				bt2.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						handleFriendRequest(notice.noticeId,2,notice,h);
						dismiss();
					}
					
				});
				
				bt3.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						handleFriendRequest(notice.noticeId,3,notice,h);
						dismiss();
					}
					
				});
				
				bt4.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dismiss();
					}	
				});

			}
		}
	

	 public void handleFriendRequest(String applyId,final int reply,final FriendNotice notice,final ViewHolder h)
	{
		 ApiClient.handleFriendRequest(appContext.getLoginApiKey(),applyId,reply,
					new  ClientCallback(){
				@Override
				public void onSuccess(Object data) {
					// TODO Auto-generated method stub
					Toast.makeText(context, "处理成功", Toast.LENGTH_SHORT).show();
					
					notice.processStatus=reply;
					h.processButton.setVisibility(View.GONE);
					h.statusText.setVisibility(View.VISIBLE);	
					h.statusText.setText("已处理");
					
					saveOrUpdateFriendNotice(notice);
				}
	
				@Override
				public void onFailure(String message) {
					// TODO Auto-generated method stub
					Toast.makeText(context, "处理失败", Toast.LENGTH_SHORT).show();
				}
	
				@Override
				public void onError(Exception e) {
					// TODO Auto-generated method stub
					Toast.makeText(context, "处理失败", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onRequestLogIn() {
					// TODO Auto-generated method stub
					
				}
		
	});
 }

 public void saveOrUpdateFriendNotice(final FriendNotice notice){

	 	new Thread(){
		public void run() {
			FriendNoticeManager.getInstance(context).saveOrUpdateFriendNotice(notice, appContext.getLoginLocation());
		}
	 	}.start();
}
 
class ViewHolder{
		public ImageView userIcon;
		public TextView name;
		public Button processButton;
		public TextView desc;
		public TextView statusText;
	}
}

