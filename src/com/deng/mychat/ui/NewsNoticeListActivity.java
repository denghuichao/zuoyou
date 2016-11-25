package com.deng.mychat.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tools.StringUtils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deng.mychat.R;
import com.deng.mychat.bean.News;
import com.deng.mychat.bean.NewsEntity;
import com.deng.mychat.bean.NewsNotice;
import com.deng.mychat.bean.Prise;
import com.deng.mychat.bean.Reply;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.ContactManager;
import com.deng.mychat.config.NewsNoticeManager;
import com.deng.mychat.view.CustomProgressDialog;
import com.deng.mychat.view.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewsNoticeListActivity extends AppActivity{
	
	private List<NewsNotice>unreadNoticeList;
	private Map<String,News> idToNews=new HashMap<String,News>();
	private Set<String>newsIdSet=new HashSet<String>();
	private Map<String,String>noticeidToNewsid=new HashMap<String,String>();
	private MyListView list;
	private TextView more;
	private NewsNoticeAdapter adapter; 
	private CustomProgressDialog loadingPd;
	private boolean hasLoad=false;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_comment_prise_notice);	
		list=(MyListView)findViewById(R.id.list);
		more=(TextView)findViewById(R.id.more);
		adapter=new NewsNoticeAdapter();
		
		unreadNoticeList=NewsNoticeManager.getInstance(context).getUnReadNewsNoticeList();
		
		Collections.sort(unreadNoticeList);
		for(NewsNotice n:unreadNoticeList)
		{
			if(n.type==1 || n.type==2)//0:导航有更新，1:说说评论 2:说说点赞
			{
				noticeidToNewsid.put(n.noticeId, n.newsId);
				newsIdSet.add(n.newsId);
			}
		}		
		getAllNewsNoticed();
	}
	
	private void getAllNewsNoticed()
	{
		loadingPd = CustomProgressDialog.show(this, "加载中...", false, null);//UIHelper.showProgress(this, null, "正在登录", true);
		loadingPd.setCancelable(true);
		ApiClient.getNewsById(appContext.getLoginApiKey(), newsIdSet, new ClientCallback(){

			@Override
			public void onSuccess(Object data) {
				NewsEntity ne=(NewsEntity)data;
				if(ne!=null && ne.newsList!=null)
				{
					for(News n:ne.newsList)
					{
						idToNews.put(n.shuoshuoId, n);
					}	
					hasLoad=true;
					if(hasLoad)setAllNewsNoticeRead();
					handleAllNotice(true);
					loadingPd.dismiss();
				}
			}

			@Override
			public void onFailure(String message) {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
				showToast("加载失败");
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
				showToast("加载失败");
			}

			@Override
			public void onRequestLogIn() {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
				showToast("加载失败");
			}
			
		});
	}
	
	public void loadMoreNotice()
	{
		loadingPd = CustomProgressDialog.show(this, "加载中...", false, null);//UIHelper.showProgress(this, null, "正在登录", true);
		loadingPd.setCancelable(true);
		final List<NewsNotice>readNoticeList=NewsNoticeManager.getInstance(context).getReadNewsNoticeList();
		Set<String> readNewsIdSet=new HashSet<String>();
		for(NewsNotice n:readNoticeList)
		{
			noticeidToNewsid.put(n.noticeId, n.newsId);
			readNewsIdSet.add(n.newsId);
		}
		Set<String> newsIdSetToLoad=new HashSet<String>();
		
		for(String s:readNewsIdSet)
		{
			if(!newsIdSet.contains(s))
				newsIdSetToLoad.add(s);
		}
		
		if(newsIdSetToLoad.size()==0)
		{
			//unreadNoticeList.clear();
			unreadNoticeList.clear();
			unreadNoticeList.addAll(readNoticeList);
			Collections.sort(unreadNoticeList);
			handleAllNotice(false);
			loadingPd.dismiss();
			return;
		}
		ApiClient.getNewsById(appContext.getLoginApiKey(), newsIdSetToLoad, new ClientCallback(){

			@Override
			public void onSuccess(Object data) {
				NewsEntity ne=(NewsEntity)data;
				if(ne!=null && ne.newsList!=null)
				{
					for(News n:ne.newsList)
					{
						idToNews.put(n.shuoshuoId, n);
					}	
					unreadNoticeList.clear();
					unreadNoticeList.addAll(readNoticeList);
					Collections.sort(unreadNoticeList);
					handleAllNotice(false);
					loadingPd.dismiss();
				}
			}

			@Override
			public void onFailure(String message) {
				// TODO Auto-generated method stub
				//unreadNoticeList.clear();
				//unreadNoticeList.addAll(readNoticeList);
				//Collections.sort(unreadNoticeList);
				//handleAllNotice(false);
				loadingPd.dismiss();
				showToast("加载失败");
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				//unreadNoticeList.clear();
				//unreadNoticeList.addAll(readNoticeList);
				//Collections.sort(unreadNoticeList);
				//handleAllNotice(false);
				loadingPd.dismiss();
				showToast("加载失败");
			}

			@Override
			public void onRequestLogIn() {
				// TODO Auto-generated method stub
				//unreadNoticeList.clear();
				//unreadNoticeList.addAll(readNoticeList);
				//Collections.sort(unreadNoticeList);
				//handleAllNotice(false);
				loadingPd.dismiss();
				showToast("加载失败");
			}
			
		});
	}
	
	private void handleAllNotice(boolean v)
	{
		if(v)list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		more.setVisibility(v?View.VISIBLE:View.GONE);
	}
	
	public void onResume()
	{
		super.onResume();
		//Intent intent = new Intent(CommonValue.ACTION_NEWS_NOTICE);
		//sendBroadcast(intent);
	}

	
	public void setAllNewsNoticeRead()
	{
		new Thread()
		{
			public void run()
			{
				for(NewsNotice n:unreadNoticeList)
				{
					NewsNoticeManager.getInstance(context).updateReadStatus(n.noticeId, 1);
				}
				Intent intent = new Intent(CommonValue.ACTION_NEWS_NOTICE);
				sendBroadcast(intent);
			}
		}.start();
	}
	
	public void buttonClick(View v)
	{
		switch(v.getId())
		{
		case R.id.cancelBtn:finish();break;
		case R.id.more:loadMoreNotice();break;
		}
	}
	
	private class NewsNoticeAdapter extends BaseAdapter
	{

		private LayoutInflater inflater; // 视图容器
		
		NewsNoticeAdapter()
		{
			inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return noticeidToNewsid.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return unreadNoticeList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NewsNotice notice=unreadNoticeList.get(position);
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.news_comment_prise_item_layout,
						parent, false);
				holder = new ViewHolder();
				 holder.topll=(RelativeLayout) convertView
							.findViewById(R.id.topll);
				holder.userIcon=(ImageView) convertView
						.findViewById(R.id.userIcon);
				holder.nameTv=(TextView) convertView
						.findViewById(R.id.name);
				holder.contentTextTv=(TextView) convertView
						.findViewById(R.id.contentText);
				holder.priseImg=(ImageView) convertView
						.findViewById(R.id.priseImg);
				holder.timeTv=(TextView) convertView
						.findViewById(R.id.time);
				
				holder.shuoshuoContent=(FrameLayout)convertView
						.findViewById(R.id.shuoshuo_content);
				holder.shuoshuoText=(TextView) convertView
						.findViewById(R.id.shuoshuoText);
				holder.shuoshuoImg=(ImageView) convertView
						.findViewById(R.id.shuoshuoImg);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			
			String nid=noticeidToNewsid.get(notice.noticeId);
			final News n;
			if(nid!=null)n=idToNews.get(nid);
			else n=null;
			
			//if(n==null){holder.topll.setVisibility(View.GONE);}
			//else {holder.topll.setVisibility(View.VISIBLE);}
			
			if(notice.type==1)
			{
				holder.contentTextTv.setVisibility(View.VISIBLE);
				holder.priseImg.setVisibility(View.GONE);
				if(nid!=null)
				{
					if(n!=null)
					{
						for(Reply r:n.getReply())
						{
							if(r!=null && r.shuoCommentId!=null && r.shuoCommentId.equals(notice.commentId))
							{
								holder.contentTextTv.setText(r.context);
								holder.nameTv.setText(r.author.userName);
								holder.timeTv.setText(r.commentTime);
								imageLoader.displayImage(r.author.txPath.smallPicture, holder.userIcon, CommonValue.DisplayOptions.touxiang_options);
								final UserInfo u=r.author;
								holder.userIcon.setOnClickListener(new OnClickListener(){

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										goTocontactMainPage(u);
									}
									
								});
								break;
							}
						}
					}
				}
				
			}
			else if(notice.type==2 && nid!=null)
			{
				holder.contentTextTv.setVisibility(View.GONE);
				holder.priseImg.setVisibility(View.VISIBLE);
				boolean hasFind=false;
				if(n!=null)
				{
					for(Prise p:n.getPrise())
					{
						if(p!=null && p.priseId!=null && p.priseId.equals(notice.priseId))
						{
							holder.nameTv.setText(p.authorInfo.userName);
							holder.timeTv.setText(p.priseTime);
							imageLoader.displayImage(p.authorInfo.txPath.smallPicture, holder.userIcon, CommonValue.DisplayOptions.touxiang_options);
							final UserInfo u=p.authorInfo;
							holder.userIcon.setOnClickListener(new OnClickListener(){

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									goTocontactMainPage(u);
								}
								
							});
							hasFind=true;
							break;
						}
					}
					if(!hasFind)
					{
						getUserInfo(notice.userId, holder, notice);
					}
				}
			}

			if(n!=null)
			{
				if(n.shuoType==0)
				{
					if(n.getPictures()!=null && n.getPictures().size()>0)
					{
						holder.shuoshuoImg.setVisibility(View.VISIBLE);
						holder.shuoshuoText.setVisibility(View.GONE);
						imageLoader.displayImage(n.getPictures().get(0).smallPicture, holder.shuoshuoImg, CommonValue.DisplayOptions.touxiang_options);
					}
					else	
					{
						holder.shuoshuoImg.setVisibility(View.GONE);
						holder.shuoshuoText.setVisibility(View.VISIBLE);
						holder.shuoshuoText.setText(n.context);
					}
				}
				else if(n.shuoType==1)
				{
					holder.shuoshuoImg.setVisibility(View.GONE);
					holder.shuoshuoText.setVisibility(View.VISIBLE);
					holder.shuoshuoText.setText(n.descText);
				}
				else if(n.shuoType==2)
				{
					News sn=n.sharedNews;
					if(sn!=null)
					{
						if(sn.getPictures()!=null && sn.getPictures().size()>0)
						{
							holder.shuoshuoImg.setVisibility(View.VISIBLE);
							holder.shuoshuoText.setVisibility(View.GONE);
							imageLoader.displayImage(sn.getPictures().get(0).smallPicture, holder.shuoshuoImg, CommonValue.DisplayOptions.touxiang_options);
						}
						else	
						{
							holder.shuoshuoImg.setVisibility(View.GONE);
							holder.shuoshuoText.setVisibility(View.VISIBLE);
							holder.shuoshuoText.setText(sn.context);
						}
					}
					else 
					{
						holder.shuoshuoImg.setVisibility(View.GONE);
						holder.shuoshuoText.setVisibility(View.VISIBLE);
						holder.shuoshuoText.setText(n.context);
					}
				}
			}
			
			holder.shuoshuoContent.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(context,NewsDetilActivity.class);
					intent.putExtra("news", n);
					context.startActivity(intent);
				}	
			});
			return convertView;
		}
		
		
		private void getUserInfo(final String userId, final ViewHolder holder, final NewsNotice notice) {
			holder.timeTv.setText(notice.noticeTime);
			UserInfo friend = ContactManager.getInstance(context).getContact(userId);
			if (friend != null && StringUtils.notEmpty(friend.txPath)) {
				ImageLoader.getInstance().displayImage(friend.txPath.smallPicture, holder.userIcon, CommonValue.DisplayOptions.touxiang_options);
				holder.nameTv.setText(friend.userName);
				return;
			}
			SharedPreferences sharedPre = context.getSharedPreferences(
					CommonValue.LOGIN_SET, Context.MODE_PRIVATE);
			String apiKey = sharedPre.getString(CommonValue.APIKEY, null);
			
			ApiClient.getUserInfo(apiKey, userId, new ClientCallback() {
				
				@Override
				public void onSuccess(Object data) {
					UserInfo userInfo = (UserInfo) data;
					ContactManager.getInstance(context).saveOrUpdateContact(userInfo,appContext.getLoginLocation());
					holder.nameTv.setText(userInfo.userName);
					ImageLoader.getInstance().displayImage(userInfo.txPath.smallPicture, holder.userIcon, CommonValue.DisplayOptions.touxiang_options);
				}
				
				@Override
				public void onFailure(String message) {
					unreadNoticeList.remove(notice);
					adapter.notifyDataSetChanged();
				}
				
				@Override
				public void onError(Exception e) {
					unreadNoticeList.remove(notice);
					adapter.notifyDataSetChanged();
				}

				@Override
				public void onRequestLogIn() {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		private class ViewHolder{
			RelativeLayout topll;
			ImageView userIcon;
			TextView nameTv;
			TextView contentTextTv;
			ImageView priseImg;
			TextView timeTv;
			
			FrameLayout shuoshuoContent;
			TextView shuoshuoText;
			ImageView shuoshuoImg;	
		}	
	}
	
	 private void goTocontactMainPage(UserInfo user) {
			// TODO Auto-generated method stub
	    	Intent intent=new Intent(context,ContactMainPageActivity.class);
			intent.putExtra("isMyFriend", user.isMyFriend);
			intent.putExtra("user", user);
			context.startActivity(intent);
		}	
}
