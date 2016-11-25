package com.deng.mychat.adapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tools.DateUtil;
import tools.StringUtils;

import com.deng.mychat.R;
import com.deng.mychat.bean.FriendNotice;
import com.deng.mychat.bean.JsonMessage;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.ContactManager;
import com.deng.mychat.config.FriendNoticeManager;
import com.deng.mychat.config.MessageManager;
import com.deng.mychat.config.WCApplication;
import com.deng.mychat.im.Chating;
import com.deng.mychat.model.HistoryChatBean;
import com.deng.mychat.ui.ContactMainPageActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class WeChatAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Object> inviteUsers;
	private Context context;
	private WCApplication appContext;
	//private Handler myHandler=new Handler();

	public static class CellHolder {
		TextView alpha;
		ImageView avatarImageView;
		TextView titleView;
		TextView timeView;
		TextView desView;
		TextView paopao;
		TextView newDate;
	}
	
	class ViewHolder{
		public ImageView userIcon;
		public TextView name;
		public Button processButton;
		public TextView desc;
		public TextView statusText;
		public ImageView bubble;
	}
	
	public WeChatAdapter(WCApplication appContext,Context context, List<Object> inviteUsers) {
		this.context = context;
		this.appContext=appContext;
		mInflater = LayoutInflater.from(context);
		this.inviteUsers = inviteUsers;
	}

	public void setNoticeList(List<Object> inviteUsers) {
		this.inviteUsers = inviteUsers;
	}

	@Override
	public int getCount() {
		return inviteUsers.size();
	}

	@Override
	public Object getItem(int position) {
		return inviteUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		Object o=inviteUsers.get(position);
		final CellHolder cell;
		final ViewHolder h; 
		if(o instanceof HistoryChatBean )
		{
			final HistoryChatBean notice = (HistoryChatBean) o;
			if (convertView == null) {
				cell = new CellHolder();
				convertView = mInflater.inflate(R.layout.friend_card_cell, null);
				cell.alpha = (TextView) convertView.findViewById(R.id.alpha);
				cell.avatarImageView = (ImageView) convertView.findViewById(R.id.avatarImageView);
				cell.timeView = (TextView) convertView.findViewById(R.id.time);
				cell.titleView = (TextView) convertView.findViewById(R.id.title);
				cell.desView = (TextView) convertView.findViewById(R.id.des);
				cell.paopao = (TextView) convertView.findViewById(R.id.paopao);
				convertView.setTag(cell);
			} else {
				if(convertView.getTag() instanceof CellHolder)
					cell = (CellHolder) convertView.getTag();
				else 
				{
					cell = new CellHolder();
					convertView = mInflater.inflate(R.layout.friend_card_cell, null);
					cell.alpha = (TextView) convertView.findViewById(R.id.alpha);
					cell.avatarImageView = (ImageView) convertView.findViewById(R.id.avatarImageView);
					cell.timeView = (TextView) convertView.findViewById(R.id.time);
					cell.titleView = (TextView) convertView.findViewById(R.id.title);
					cell.desView = (TextView) convertView.findViewById(R.id.des);
					cell.paopao = (TextView) convertView.findViewById(R.id.paopao);
					convertView.setTag(cell);
				}
			}
			String jid = notice.getFrom();
			cell.desView.setTag(notice);
			getUserInfo(jid, cell, notice);
			convertView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					createChat(notice.getFrom());
					removeSingelChatPao(notice);
				}
				
			});
			convertView.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View arg0) {
					// TODO Auto-generated method stub
					new DelPopupWindows(context, parent,position);
					return false;
				}
				
			});
			return convertView;
		}
		else
		{
			final FriendNotice notice=(FriendNotice)o;
			final UserInfo user=notice.user;
			if(convertView==null){
				h = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.friend_notice_item_layout, parent, false);
				h.userIcon = (ImageView)convertView.findViewById(R.id.userIcon);
				
				h.name=(TextView)convertView.findViewById(R.id.contactName);
				h.processButton=(Button)convertView.findViewById(R.id.processBtn);
				
				h.desc=(TextView)convertView.findViewById(R.id.desc);
				h.statusText=(TextView)convertView.findViewById(R.id.processStatus);
				
				h.bubble=(ImageView)convertView.findViewById(R.id.bubble);
				convertView.setTag(h);
			}else{
				if(convertView.getTag() instanceof ViewHolder)
					h = (ViewHolder)convertView.getTag();
				else
				{
					h = new ViewHolder();
					convertView = LayoutInflater.from(context).inflate(R.layout.friend_notice_item_layout, parent, false);
					h.userIcon = (ImageView)convertView.findViewById(R.id.userIcon);
					
					h.name=(TextView)convertView.findViewById(R.id.contactName);
					h.processButton=(Button)convertView.findViewById(R.id.processBtn);
					
					h.desc=(TextView)convertView.findViewById(R.id.desc);
					h.statusText=(TextView)convertView.findViewById(R.id.processStatus);
					
					h.bubble=(ImageView)convertView.findViewById(R.id.bubble);
					convertView.setTag(h);
				}
			}
				
			((AppActivity)context).imageLoader.displayImage(user.txPath.smallPicture, h.userIcon, CommonValue.DisplayOptions.touxiang_options);
			h.name.setText(user.userName);
		
			
			if(!StringUtils.empty(notice.desc))
				h.desc.setText(notice.desc);
			
			if(notice.processStatus==0)
			{
				h.processButton.setVisibility(View.VISIBLE);
				h.statusText.setVisibility(View.GONE);
				h.bubble.setVisibility(View.VISIBLE);
			}
			else
			{
				h.processButton.setVisibility(View.GONE);
				h.statusText.setVisibility(View.VISIBLE);
				h.bubble.setVisibility(View.GONE);
				h.statusText.setText("已处理");
			}
			
			
			h.processButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					new PopupWindows(context,parent,notice,h);
				}
				
			});
			
			
			convertView.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View arg0) {
					// TODO Auto-generated method stub
					new DelPopupWindows(context, parent,position);
					return false;
				}
				
			});
			
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					goTocontactMainPage(user);
				}
				
			});
		
			return convertView;
		}
	}


	public void createChat(String userId) {
		Intent intent = new Intent(context, Chating.class);
		intent.putExtra("to", userId);
		//intent.putExtra("user", user);
		context.startActivity(intent);
	}
	
	 protected void goTocontactMainPage(UserInfo user) {
			// TODO Auto-generated method stub
	    	Intent intent=new Intent(context,ContactMainPageActivity.class);
			intent.putExtra("isMyFriend", user.isMyFriend);
			intent.putExtra("user", user);
			context.startActivity(intent);
		}
	
	private void removeSingelChatPao(final HistoryChatBean notice) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				notifyDataSetChanged();
			}
		};
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		singleThreadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				//Intent intent = new Intent(CommonValue.ACTION_NEWS_NOTICE);
				//context.sendBroadcast(intent);
				notice.setNoticeSum(0);
				handler.sendEmptyMessage(0);
				
			}
		});
	}
	
	private void getUserInfo(final String userId, final CellHolder holder, HistoryChatBean notice) {
		holder.timeView.setText(DateUtil.wechat_time(notice.getNoticeTime()));
		Integer ppCount = notice.getNoticeSum();
		if (ppCount != null && ppCount > 0) {
			holder.paopao.setText(ppCount + "");
			holder.paopao.setVisibility(View.VISIBLE);

		} else {
			holder.paopao.setVisibility(View.GONE);
		}
		
		String content = notice.getContent();
		try {
			JsonMessage msg = JsonMessage.parse(content);
			holder.desView.setText(msg.text);
		} catch(Exception e) {
			holder.desView.setText(content);
		}
		UserInfo friend = ContactManager.getInstance(context).getContact(userId.split("@")[0]);
		if (friend != null && StringUtils.notEmpty(friend.txPath)) {
			ImageLoader.getInstance().displayImage(friend.txPath.smallPicture, holder.avatarImageView, CommonValue.DisplayOptions.touxiang_options);
			holder.titleView.setText(friend.userName);
			return;
		}
		SharedPreferences sharedPre = context.getSharedPreferences(
				CommonValue.LOGIN_SET, Context.MODE_PRIVATE);
		String apiKey = sharedPre.getString(CommonValue.APIKEY, null);
		
		ApiClient.getUserInfo(apiKey, userId.split("@")[0], new ClientCallback() {
			
			@Override
			public void onSuccess(Object data) {
				UserInfo userInfo = (UserInfo) data;
				ContactManager.getInstance(context).saveOrUpdateContact(userInfo,appContext.getLoginLocation());
				holder.titleView.setText(userInfo.userName);
				ImageLoader.getInstance().displayImage(userInfo.txPath.smallPicture, holder.avatarImageView, CommonValue.DisplayOptions.touxiang_options);
		
			}
			
			@Override
			public void onFailure(String message) {
			
			}
			
			@Override
			public void onError(Exception e) {
				
			}

			@Override
			public void onRequestLogIn() {
				// TODO Auto-generated method stub
				
			}
		});
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
	
	 
	 public class DelPopupWindows extends PopupWindow {

			public DelPopupWindows(final Context mContext, View parent,final int pos) {

				View view = View
						.inflate(mContext, R.layout.op_duihua_popo, null);
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
				showAtLocation(parent, Gravity.BOTTOM, 0, 0);
				update();

				Button bt1 = (Button) view
						.findViewById(R.id.item_popupwindows_del);

				Button bt4 = (Button) view
						.findViewById(R.id.item_popupwindows_cancel);
				
				bt1.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						final Object o=inviteUsers.get(pos);
						if(o instanceof HistoryChatBean)
						{
							inviteUsers.remove(pos);
							notifyDataSetChanged();
							new Thread()
							{
								public void run()
								{
									MessageManager.getInstance(context).delChatHisWithSb(((HistoryChatBean) o).getFrom());
									Intent intent = new Intent(CommonValue.ACTION_TABBAR_NOTICE);
									context.sendBroadcast(intent);
								}
							}.start();

						}
						else if(o instanceof FriendNotice)
						{
							inviteUsers.remove(pos);
							notifyDataSetChanged();
							new Thread(){
								public void run()
								{
									FriendNoticeManager.getInstance(context).delById(((FriendNotice)o).noticeId);
									Intent intent = new Intent(CommonValue.ACTION_TABBAR_NOTICE);
									context.sendBroadcast(intent);
								}
							}.start();

						}
							
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
					h.bubble.setVisibility(View.GONE);
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
		@Override
		public void run() {
			FriendNoticeManager.getInstance(context).saveOrUpdateFriendNotice(notice, appContext.getLoginLocation());
			//Intent intent = new Intent(CommonValue.ACTION_NEWS_NOTICE);
			Intent intent = new Intent(CommonValue.ACTION_TABBAR_NOTICE);
			context.sendBroadcast(intent);
		}
	}.start();
}
	
}