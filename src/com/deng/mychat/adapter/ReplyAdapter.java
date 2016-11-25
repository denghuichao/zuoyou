package com.deng.mychat.adapter;

import java.util.List;

import com.deng.mychat.R;
import com.deng.mychat.bean.News;
import com.deng.mychat.bean.Reply;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.ContactManager;
import com.deng.mychat.config.NewsManager;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.config.WCApplication;
import com.deng.mychat.ui.ContactMainPageActivity;
import com.deng.mychat.view.LinkTouchMovementMethod;
import com.deng.mychat.view.MyClickableSpan;
import com.deng.mychat.view.MyIntentSpan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ReplyAdapter extends BaseAdapter{

	private Context context;
	private List<Reply>listItems;
	private LayoutInflater listContainer;
	private boolean []hasChecked;
	private ListView mListView;
	
	//private Handler myHandler=new Handler();
	
	public interface OnContentClickCallback{
		public void onContentClick(int pos);
		public void  reflesh(int p);
	}
	
	private void notifyCallback(int p)
	{
		if(callback!=null)
		{
			callback.onContentClick(p);
		}
	}
	
	private void notifyReflesh(int p)
	{
		if(callback!=null)
		{
			callback.reflesh(p);
		}
	}
	
	private OnContentClickCallback callback;
	
	public void setOnContentClickCallback(OnContentClickCallback c)
	{
		this.callback=c;
	}
	
	public ReplyAdapter(Context context, List<Reply> listItems,ListView listView)
	{
		 this.context=context;
		 listContainer = LayoutInflater.from(context); 
		 this.listItems = listItems;   
	     hasChecked = new boolean[getCount()]; 
	     mListView=listView;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		final MyHolder item;
		
		
		if(v==null)
		{
			item=new MyHolder();
			v= listContainer.inflate(R.layout.replay_layout,null);
			item.content=(TextView)v.findViewById(R.id.content);
			v.setTag(item);
		}
		else
		{
			item=(MyHolder)v.getTag();
		}
			
		final Reply ar=listItems.get(position);
		final String toId=listItems.get(position).toId;

		item.content.setVisibility(View.GONE);
		
		if(ar.replyType==0)
		{
			SpannableString spb = new SpannableString(((ar.author==null || ar.author.userName==null)?"": ar.author.userName)+":"+(ar.context==null?"":ar.context)); 
			Intent fromintent=new Intent(context,ContactMainPageActivity.class);
			fromintent.putExtra("user", ar.author);
			spb.setSpan(new MyIntentSpan(Color.rgb(0x41, 0x69, 0xEE), Color.rgb(0x41, 0x69, 0xEE), Color.rgb(0xCD, 0xCD, 0xc1), fromintent), 0, ar.author.userName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			MyClickableSpan cp=new MyClickableSpan(Color.rgb(0x0d, 0x0d, 0x0d), Color.rgb(0x0d, 0x0d, 0x0d), Color.rgb(0xCD, 0xCD, 0xc1));
			cp.setOnClickCallback(new MyClickableSpan.OnClickCallback() {
				@Override
				public void onClick() {
					notifyCallback(position);
				}
			});
			
			spb.setSpan(cp, ar.author.userName.length(), ar.author.userName.length()+1+ar.context.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			item.content.setText(spb);   
			item.content.setMovementMethod(new LinkTouchMovementMethod());			
			item.content.setVisibility(View.VISIBLE);
		}
		else if(ar.toUser!=null)
		{
			    String guding="»Ø¸´";
				SpannableString spb = new SpannableString(ar.author.userName+guding+ar.toUser.userName+":"+ar.context+""); 
				
				Intent fromintent=new Intent(context,ContactMainPageActivity.class);
				fromintent.putExtra("user", ar.author);
				spb.setSpan(new MyIntentSpan(Color.rgb(0x41, 0x69, 0xEE), Color.rgb(0x41, 0x69, 0xEE), Color.rgb(0xCD, 0xCD, 0xc1),fromintent),
						0, ar.author.userName.length(), 
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				Intent tointent=new Intent(context,ContactMainPageActivity.class);
				tointent.putExtra("user", ar.toUser);
				spb.setSpan(new MyIntentSpan(Color.rgb(0x41, 0x69, 0xEE), Color.rgb(0x41, 0x69, 0xEE), Color.rgb(0xCD, 0xCD, 0xc1),tointent), 
						ar.author.userName.length()+guding.length(), 
						ar.author.userName.length()+guding.length()+ar.toUser.userName.length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				MyClickableSpan cp=new MyClickableSpan(Color.rgb(0x0d, 0x0d, 0x0d), Color.rgb(0x0d, 0x0d, 0x0d), Color.rgb(0xCD, 0xCD, 0xc1));
				cp.setOnClickCallback(new MyClickableSpan.OnClickCallback() {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						notifyCallback(position);
					}
				});
				
			spb.setSpan(cp, ar.author.userName.length()+guding.length()+ar.toUser.userName.length(), ar.author.userName.length()+guding.length()+ar.toUser.userName.length()+ar.context.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				
				item.content.setText(spb); 
				item.content.setVisibility(View.VISIBLE);
				item.content.setMovementMethod(new LinkTouchMovementMethod());
		}
		else new  AsyncTask<Object, Object, UserInfo>(){	
 		@Override
	 		public UserInfo doInBackground(Object... params) 
	 	    {
	 				return  ContactManager.getInstance(context).getContact(toId);
	 			
	 		}

	 	 @Override
	 	   public void onPostExecute(final UserInfo u) {
	 		 if(u==null)
	 		 ApiClient.getUserInfo(((WCApplication)context.getApplicationContext()).getLoginApiKey(), toId, new ClientCallback(){
	 					@Override
	 					public void onSuccess(Object data) {
	 							// TODO Auto-generated method stub	
	 						UserInfo au=(UserInfo)data;
	 						ar.toUser=au;
	 						//notifyReflesh(position);
	 						notifyDataSetChanged();
	 						saveOrUpdateContact((UserInfo)data);
	 					}
	 			
	 					@Override
	 					public void onFailure(String message) {
	 							// TODO Auto-generated method stub		
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
	 			else 
	 			{
	 				ar.toUser=u;
	 				notifyDataSetChanged();
	 				//notifyReflesh(position);
	 			}
	 		}
 		}.execute();
		return v;
	}
	
	
	public void saveOrUpdateContact(final UserInfo user)
	{
		new Thread(){
			public void run() {
				
				ContactManager.getInstance(context).saveOrUpdateContact(user, ((WCApplication)context.getApplicationContext()).getLoginLocation());		
				
			}
		}.start();
		
	}
	
	public class MyHolder
	{
		TextView content;
	}

}
