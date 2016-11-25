package com.deng.mychat.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;

import tools.Logger;
import tools.UIHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.deng.mychat.R;
import com.deng.mychat.adapter.MyNewsAdapter;
import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.News;
import com.deng.mychat.bean.NewsEntity;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.NewsManager;
import com.deng.mychat.config.NewsNoticeManager;
import com.deng.mychat.config.XmppConnectionManager;
import com.deng.mychat.pic.PublishedActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

public class NewsActivity extends AppActivity implements  OnClickListener{
	
	private LinearLayout[] mImageViews;	
	private int mViewCount;	
	private int mCurSel;
	
	private TextView fujin;
	private TextView haoyou;
	private TextView noticeTv;
	
	private ImageButton actionbarPublishButton;//actionbar_publish_button
	private PullToRefreshListView aroundNewsList;//@+id/newsList
	private PullToRefreshListView friendNewsList;
	
	private MyNewsAdapter aroundNewsAdapter;
	private MyNewsAdapter friendNewsAdapter;
	private List<News>aroundNewsdata;
	private List<News>friendNewsdata;
	boolean newsIsFriend=false;

	private boolean visible;
	
	private boolean aBusy;
	private boolean fBusy;
	private  boolean hasInit=false;

	private ImageView indicatorImageView;
	private Animation indicatorAnimation;	
	
	private Set<String>newsToUpdate=new HashSet<String>();
	
	private  Handler uiHandler=new Handler(){
    	@Override
    	public void handleMessage(Message m)
    	{
    		friendNewsAdapter.notifyDataSetChanged();
    		aroundNewsAdapter.notifyDataSetChanged();
    	}
	};
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_layout);
		initUI();
	}
	
	public void initUI()
	{
		fujin=(TextView)findViewById(R.id.around);
		haoyou=(TextView)findViewById(R.id.friend);
		noticeTv=(TextView)findViewById(R.id.noticeTv);
    	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lllayout);   	
    	mViewCount = 2;
    	mImageViews = new LinearLayout[mViewCount];   	
    	for(int i = 0; i < mViewCount; i++)    	{
    		mImageViews[i] = (LinearLayout) linearLayout.getChildAt(i);
    		mImageViews[i].setEnabled(true);
    		mImageViews[i].setOnClickListener(this);
    		mImageViews[i].setTag(i);
    	}    	
    	mCurSel = 0;
    	mImageViews[mCurSel].setEnabled(false);    	
		
		aroundNewsList=(PullToRefreshListView)findViewById(R.id.aroundNewsList);
		friendNewsList=(PullToRefreshListView)findViewById(R.id.friendNewsList);
		
		setCurPoint(1);
		setCurPoint(0);
		
		aroundNewsList.setMode(Mode.BOTH);
		aroundNewsList.getLoadingLayoutProxy(true, false).setLastUpdatedLabel("下拉刷新");
		aroundNewsList.getLoadingLayoutProxy(true, false).setPullLabel("");
		aroundNewsList.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新");
		aroundNewsList.getLoadingLayoutProxy(true, false).setReleaseLabel("放开以刷新");
		// 上拉加载更多时的提示文本设置		
		aroundNewsList.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("上拉加载");
		aroundNewsList.getLoadingLayoutProxy(false, true).setPullLabel("");
		aroundNewsList.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		aroundNewsList.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
		
		aroundNewsList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>(){

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if(aBusy)return;
				// TODO Auto-generated method stub
				if (aroundNewlvDataState != UIHelper.LISTVIEW_DATA_LOADING) {
					aroundNewlvDataState = UIHelper.LISTVIEW_DATA_LOADING;
					currentAroundNewsPage = 1;
					new NewsPageTask(currentAroundNewsPage,UIHelper.LISTVIEW_COUNT,false,false,UIHelper.LISTVIEW_ACTION_REFRESH).execute();
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if(aBusy)return;
				if (aroundNewlvDataState != UIHelper.LISTVIEW_DATA_MORE) {
		            return;
		        }
		        aroundNewlvDataState = UIHelper.LISTVIEW_DATA_LOADING;
		        currentAroundNewsPage++;
		        new NewsPageTask(currentAroundNewsPage,UIHelper.LISTVIEW_COUNT,false,false,UIHelper.LISTVIEW_ACTION_SCROLL).execute();
			}});


		friendNewsList.setMode(Mode.BOTH);
		friendNewsList.getLoadingLayoutProxy(true, false).setLastUpdatedLabel("下拉刷新");
		friendNewsList.getLoadingLayoutProxy(true, false).setPullLabel("");
		friendNewsList.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新");
		friendNewsList.getLoadingLayoutProxy(true, false).setReleaseLabel("放开以刷新");

		friendNewsList.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("上拉加载");
		friendNewsList.getLoadingLayoutProxy(false, true).setPullLabel("");
		friendNewsList.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		friendNewsList.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
		friendNewsList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>(){
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			if(fBusy)return;
			if (friendNewslvDataState != UIHelper.LISTVIEW_DATA_LOADING) {
				friendNewslvDataState = UIHelper.LISTVIEW_DATA_LOADING;
				currentFriendNewsPage = 1;				
				new NewsPageTask(currentFriendNewsPage,UIHelper.LISTVIEW_COUNT,true,false,UIHelper.LISTVIEW_ACTION_REFRESH).execute();
			}
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// TODO Auto-generated method stub
			if(fBusy)return;
			if (friendNewslvDataState != UIHelper.LISTVIEW_DATA_MORE) {
				friendNewsList.onRefreshComplete();
	        }
	        friendNewslvDataState = UIHelper.LISTVIEW_DATA_LOADING;
	        currentFriendNewsPage++;
	        new NewsPageTask(currentFriendNewsPage,UIHelper.LISTVIEW_COUNT,true,false,UIHelper.LISTVIEW_ACTION_SCROLL).execute();
		}});

		
    	actionbarPublishButton=(ImageButton)findViewById(R.id.publish);

    	
    	aroundNewsdata=new ArrayList<News>();
    	aroundNewsAdapter = new MyNewsAdapter(this,appContext,aroundNewsdata);
		aroundNewsList.setAdapter(aroundNewsAdapter);

		
		friendNewsdata=new ArrayList<News>();
		friendNewsAdapter = new MyNewsAdapter(this,appContext,friendNewsdata);
		friendNewsList.setAdapter(friendNewsAdapter);
		
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
		
		new NewsPageTask(1,UIHelper.LISTVIEW_COUNT,false,true,UIHelper.LISTVIEW_ACTION_REFRESH).execute();
		new NewsPageTask(1,UIHelper.LISTVIEW_COUNT,true,true,UIHelper.LISTVIEW_ACTION_REFRESH).execute();
		
		XMPPConnection connection = XmppConnectionManager.getInstance().getConnection();
			if (!connection.isConnected()) {
				connect2xmpp();
		}
	
	 int num=NewsNoticeManager.getInstance(context).getUnReadNewsNoticeCount();
	 noticeTv.setText(Html.fromHtml("你有"+"<font color=#c44e20 >"+num+"</font>"+"条新消息,点击查看"));
	 noticeTv.setVisibility(num>0?View.VISIBLE:View.GONE);
	 initReceiver();    
	 hasInit=true;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myBroadcastReceiver);
	}
	
	private void initReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CommonValue.ACTION_REFLESH);
		intentFilter.addAction(CommonValue.ACTION_NOTIFY);
		intentFilter.addAction(CommonValue.ACTION_NEWS_NOTICE);
		registerReceiver(myBroadcastReceiver, intentFilter);
	}
	
	private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(CommonValue.ACTION_NEWS_NOTICE))
			{
			//	noticeTv.setVisibility(View.VISIBLE);
				int num=NewsNoticeManager.getInstance(context).getUnReadNewsNoticeCount();
				noticeTv.setText(Html.fromHtml("你有"+"<font color=#c44e20 >"+num+"</font>"+"条新消息"));
				 noticeTv.setVisibility(num>0?View.VISIBLE:View.GONE);
			}
			if (action.equals(CommonValue.ACTION_REFLESH))
			{
				if(!hasInit)return;
				
				 if(!aBusy)
					if (aroundNewlvDataState != UIHelper.LISTVIEW_DATA_LOADING)
					{
						aroundNewlvDataState = UIHelper.LISTVIEW_DATA_LOADING;
						currentAroundNewsPage = 1;
						new NewsPageTask(currentAroundNewsPage,UIHelper.LISTVIEW_COUNT,false,false,UIHelper.LISTVIEW_ACTION_REFRESH).execute();
			
					}	
				 if(!fBusy)
					if (friendNewslvDataState != UIHelper.LISTVIEW_DATA_LOADING) {
						friendNewslvDataState = UIHelper.LISTVIEW_DATA_LOADING;
						currentFriendNewsPage = 1;				
						new NewsPageTask(currentFriendNewsPage,UIHelper.LISTVIEW_COUNT,true,false,UIHelper.LISTVIEW_ACTION_REFRESH).execute();
					}
			}
			else if (action.equals(CommonValue.ACTION_NOTIFY))
			{
				String newsId=intent.getStringExtra("newsId");
			    News news=NewsManager.getInstance(context).getANews(newsId);
				if(news==null) return;
				for(int i=0;i<aroundNewsdata.size();i++)
				{
					News n=aroundNewsdata.get(i);
					if(n.shuoshuoId.equals(newsId))
					{
						aroundNewsdata.remove(i);
						aroundNewsdata.add(i, news);
						
						new Thread(){
							public void run()
							{
								while(!visible);
									uiHandler.sendEmptyMessage(1);
							}
						}.start();
						
					}
				}
					
				for(int i=0;i<friendNewsdata.size();i++)
				{
					News n=friendNewsdata.get(i);
					if(n.shuoshuoId.equals(newsId))
					{
						friendNewsdata.remove(i);
						friendNewsdata.add(i, news);
						
						new Thread(){
							public void run()
							{
								while(!visible);
									uiHandler.sendEmptyMessage(1);
							}
						}.start();
						
					}
				}
			}
		
			new Thread(){
				public void run()
				{
					while(!visible);
						uiHandler.sendEmptyMessage(1);
				}
			}.start();
			
		}
	};
	
	private void connect2xmpp()  {
		final Handler handler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch(msg.what){
				case 1:
					startChatService();
					break;
				case 2:
					Exception e = (Exception) msg.obj;
					Logger.i(e);
					break;
				default:
					break;
				}
			};
		};
		
		new Thread(new Runnable() {		
			@Override
			public void run() {
				Message msg = new Message();
				try {
					String password = appContext.getLoginPassword();
					String userId = appContext.getLoginUid();
					XMPPConnection connection = XmppConnectionManager.getInstance()
							.getConnection();
					connection.connect();
					connection.login(userId, password, "android"); 
					connection.sendPacket(new Presence(Presence.Type.available));
					Logger.i("XMPPClient Logged in as " +connection.getUser());
					msg.what = 1;
					
				} catch (Exception xee) {
					if (xee instanceof XMPPException) {
						XMPPException xe = (XMPPException) xee;
						final XMPPError error = xe.getXMPPError();
						int errorCode = 0;
						if (error != null) {
							errorCode = error.getCode();
						}
						msg.what = errorCode;
						msg.obj = xee;
					}
					
				}	
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public void buttonClick(View view)//按下顶部的按键
	{
		switch(view.getId())
		{
		case R.id.publish:
			publishANews();
			break;
		case R.id.noticeTv:
			gotoMessagePage();
			noticeTv.setVisibility(View.GONE);
			break;
		}
	}
	
	

	private int  friendNewslvDataState;
	private int currentFriendNewsPage=1;
	
	private int aroundNewlvDataState;
	private int currentAroundNewsPage=1;

	private void handleNews(List<News>newsList,int whos,boolean isNetWorkData, int action) {
 
		
			switch (action) {
			case UIHelper.LISTVIEW_ACTION_INIT:
			case UIHelper.LISTVIEW_ACTION_REFRESH:
				if(whos==CommonValue.FRIEND_NEWS )
				{	
					friendNewsdata.clear();
					friendNewsdata.addAll(newsList);
					friendNewsAdapter.notifyDataSetChanged();
				}
				else 
				{	
					aroundNewsdata.clear();
					aroundNewsdata.addAll(newsList);
					aroundNewsAdapter.notifyDataSetChanged();
				}
				break;
			case UIHelper.LISTVIEW_ACTION_SCROLL:
				if(whos==CommonValue.FRIEND_NEWS)
				{
						friendNewsdata.addAll(newsList);
						friendNewsAdapter.notifyDataSetChanged();
				}
				else 
				{		aroundNewsdata.addAll(newsList);
						aroundNewsAdapter.notifyDataSetChanged();
				}
				break;
			}
			
			if(whos==CommonValue.FRIEND_NEWS)
			{
				if(newsList.size() == UIHelper.LISTVIEW_COUNT){					
					friendNewslvDataState = UIHelper.LISTVIEW_DATA_MORE;
					}
				else {
					friendNewslvDataState = UIHelper.LISTVIEW_DATA_FULL;
					}
				if(friendNewsdata.isEmpty()){
					friendNewslvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
				}
			}
			
			else 
			{
				if(newsList.size() == UIHelper.LISTVIEW_COUNT){					
					aroundNewlvDataState = UIHelper.LISTVIEW_DATA_MORE;
					}
				else {
					aroundNewlvDataState = UIHelper.LISTVIEW_DATA_FULL;
					}
				if(aroundNewsdata.isEmpty()){
					aroundNewlvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
				}
			}
		
		if(whos==CommonValue.FRIEND_NEWS)
		{
			fBusy=false;
			friendNewsList.onRefreshComplete();
		}
		else
		{
			aBusy=false;
			aroundNewsList.onRefreshComplete();
		}

		indicatorImageView.setVisibility(View.INVISIBLE);
		indicatorImageView.clearAnimation();
		
		if(isNetWorkData && UIHelper.LISTVIEW_ACTION_REFRESH==action)saveOrUpdateNews(newsList );
	}
	
	
	public void saveOrUpdateNews(final List<News> nList)
	{
		
		new Thread(){
			public void run() {		
				NewsManager.getInstance(context).saveOrUpdateNews(appContext.getLoginLocation(),nList);		
			}
		}.start();	
	}
	
	
	public class NewsPageTask extends AsyncTask<Object, Integer, List<News>> {
	    
		private int mPageIndex;
		private int mPageSize;
		private boolean mIsFriend;
		private int mAction;
		boolean mIsInit ;
		
	public	NewsPageTask(int pageIndex,int pageSize,boolean isFriend,boolean isInit,int action)
		{
			this.mPageIndex=pageIndex;
			this.mPageSize=pageSize;
			this.mIsFriend=isFriend;
			this.mAction=action;
			this.mIsInit=isInit;
		}
	
	 public List<News> doInBackground(Object... params) {
		 if(!mIsInit)return new ArrayList<News>();
		 if(mIsFriend )
			{
				fBusy=true;
				return NewsManager.getInstance(context).getNewsFromFriends(mPageIndex, mPageSize);
			}
			else {
				aBusy=true;
				return  NewsManager.getInstance(context).getAroundNews(mPageIndex, mPageSize);
			}
			
		}

		@Override
	  public  void onCancelled() {
			super.onCancelled();
	   }

		@Override
		public void onPreExecute()
		{
			if(mIsInit)
			{
			 indicatorImageView.startAnimation(indicatorAnimation);
			 indicatorImageView.setVisibility(View.VISIBLE);
			}
		}
		
		@Override
		public void onPostExecute(final List<News> result) {
			if(mIsFriend)
			{
				String apiKey = appContext.getLoginApiKey();
				if(mIsInit && result!=null && result.size()>0){handleNews(result,CommonValue.FRIEND_NEWS,false, mAction);}
				
				else  ApiClient.getNewsFromFriend( apiKey, mPageIndex, UIHelper.LISTVIEW_COUNT, new ClientCallback() {
					@Override
					public void onSuccess(Object data) {
						NewsEntity entity = (NewsEntity)data;
						switch (entity.status) {
						case 1:
							handleNews(entity.newsList,CommonValue.FRIEND_NEWS,true, mAction);
							break;
						default:
							handleNews(result,CommonValue.FRIEND_NEWS,false, mAction);
							//friendNewsList.onRefreshComplete();
							break;
						}
					}
					
					@Override
					public void onFailure(String message) {
						handleNews(result,CommonValue.FRIEND_NEWS,false, mAction);
					}
					
					@Override
					public void onError(Exception e) {
						handleNews(result,CommonValue.FRIEND_NEWS,false, mAction);
					}

					@Override
					public void onRequestLogIn() {
						// TODO Auto-generated method stub	
						friendNewsList.onRefreshComplete();
					}
				});
			}
			else {
				String apiKey = appContext.getLoginApiKey();
				appContext.getLoginInfo();
				LocationPoint location=appContext.getLoginInfo().userInfo.location;
				if(location==null)location=new LocationPoint(30.0,114.001);
				
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("type","location");
				map.put("location",appContext.getNickname()+"location:"+location.toString()); 
				MobclickAgent.onEvent(context, "get_around_news", map); 
				
				if(mIsInit && result!=null && result.size()>0){handleNews(result,CommonValue.AROUND_NEWS,false, mAction);}
				else ApiClient.getNewsAround(apiKey, mPageIndex, UIHelper.LISTVIEW_COUNT,location.longitude,location.latitude, new ClientCallback() {
					@Override
					public void onSuccess(Object data) {
						NewsEntity entity = (NewsEntity)data;
						switch (entity.status) {
						case 1:
							handleNews(entity.newsList,CommonValue.AROUND_NEWS,true,mAction);
							break;
						default:
							handleNews(result,CommonValue.AROUND_NEWS,false, mAction);
							//friendNewsList.onRefreshComplete();
							break;
						}
					}
					
					@Override
					public void onFailure(String message) {
						handleNews(result,CommonValue.AROUND_NEWS,false, mAction);
					}
					
					@Override
					public void onError(Exception e) {
						handleNews(result,CommonValue.AROUND_NEWS,false, mAction);
					}

					@Override
					public void onRequestLogIn() {
						indicatorImageView.setVisibility(View.INVISIBLE);
						indicatorImageView.clearAnimation();
					}
				});

			}
		}

	}
	
	
	public void publishANews()
	{
		Intent intent =new Intent(this,PublishedActivity.class);
		startActivityForResult(intent,0x88);
	}
	
	private void gotoMessagePage() {
		// TODO Auto-generated method stub
		Intent intent =new Intent(this,NewsNoticeListActivity.class);
		startActivity(intent);
	}
	
	public void onResume()
	{
		super.onResume();
		visible=true;
	}
	
	public void onPause()
	{
		super.onPause();
		visible=false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int pos = (Integer)(v.getTag());
		setCurPoint(pos);
	}

	
	private void setCurPoint(int index)
    {
    	if (index < 0 || index > mViewCount - 1 || mCurSel == index){
    		return ;
    	}    	
    	mImageViews[mCurSel].setEnabled(true);
    	mImageViews[index].setEnabled(false);    	
    	mCurSel = index;
    	
    	if(index == 0){
    		fujin.setTextColor(0xff008000);
    		haoyou.setTextColor(Color.BLACK);
    		aroundNewsList.setVisibility(View.VISIBLE);
    		friendNewsList.setVisibility(View.GONE);
    	}else if(index==1){
    		fujin.setTextColor(Color.BLACK);
    		haoyou.setTextColor(0xff008000);
    		aroundNewsList.setVisibility(View.GONE);
    		friendNewsList.setVisibility(View.VISIBLE);
    	}
    }

}
		
