package com.deng.mychat.ui;

import java.util.ArrayList;
import java.util.List;

import tools.StringUtils;
import tools.UIHelper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.deng.mychat.R;
import com.deng.mychat.adapter.MyNewsAdapter;
import com.deng.mychat.adapter.ZujiAdapter;
import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.News;
import com.deng.mychat.bean.NewsEntity;
import com.deng.mychat.bean.UserEntity;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.NewsManager;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.image_viewer.ImagePagerActivity;
import com.deng.mychat.ui.ContactActivity.ContactPageTask;
import com.deng.mychat.util.CircleBitmapDisplayer;
import com.deng.mychat.view.MyListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ZujiActivity extends AppActivity{
	private ImageView themeBg;
	private ImageView head;
	private TextView name;
	private UserInfo user;
	
	private PullToRefreshScrollView pullRefreshScrollview;
	private MyListView newsList;
	
	private ZujiAdapter mAdapter;
	private List<News>newsdata;
	
	private boolean busy=false;
	
	
	private ImageView indicatorImageView;
	private Animation indicatorAnimation;	
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zuji_layout);
		
		
		user=(UserInfo) this.getIntent().getSerializableExtra("user");
		if(user==null)
			user=appContext.getLoginInfo().userInfo;
		initUi();
		//showUserInfo();
	}
	
	public void buttonClick(View v)
	{
		switch (v.getId())
		{
		case R.id.cancel:finish();break;
		}
	}
	
	public void initUi()
	{
		  themeBg=(ImageView)findViewById(R.id.theme_bg);  
		  RelativeLayout.LayoutParams headimgPara=(RelativeLayout.LayoutParams) themeBg.getLayoutParams();
		  headimgPara.width=this.getResources().getDisplayMetrics().widthPixels;
		  headimgPara.height=(int) (headimgPara.width*0.65);
		  themeBg.setLayoutParams(headimgPara);
		  head=(ImageView)findViewById(R.id.head);
		  name=(TextView)findViewById(R.id.name);
		  
		  newsList=(MyListView)findViewById(R.id.listView);
		  pullRefreshScrollview=(PullToRefreshScrollView)findViewById(R.id.pull_refresh_scrollview);
		  pullRefreshScrollview.setMode(Mode.BOTH);
		  pullRefreshScrollview.getLoadingLayoutProxy(true, false).setLastUpdatedLabel("下拉刷新");
		  pullRefreshScrollview.getLoadingLayoutProxy(true, false).setPullLabel("");
		  pullRefreshScrollview.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新");
		  pullRefreshScrollview.getLoadingLayoutProxy(true, false).setReleaseLabel("放开以刷新");
			// 上拉加载更多时的提示文本设置		
		  pullRefreshScrollview.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("上拉加载");
		  pullRefreshScrollview.getLoadingLayoutProxy(false, true).setPullLabel("");
		  pullRefreshScrollview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		  pullRefreshScrollview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
		  
		  pullRefreshScrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>(){

			  @Override
				public void onPullUpToRefresh(
						PullToRefreshBase<ScrollView> refreshView) {
					if (!busy &&newslvDataState == UIHelper.LISTVIEW_DATA_MORE) {
		
			        	newslvDataState = UIHelper.LISTVIEW_DATA_LOADING;
			        	currentNewsPage++;
			        	new NewsPageTask(currentNewsPage,UIHelper.LISTVIEW_COUNT,false,UIHelper.LISTVIEW_ACTION_SCROLL).execute();
					}
					pullRefreshScrollview.onRefreshComplete();
				}

				@Override
				public void onPullDownToRefresh(
						PullToRefreshBase<ScrollView> refreshView) {
					// TODO Auto-generated method stub
					currentNewsPage=1;
					if(!busy)new NewsPageTask(1,UIHelper.LISTVIEW_COUNT,false,UIHelper.LISTVIEW_ACTION_REFRESH).execute();
					pullRefreshScrollview.onRefreshComplete();
				}});
		  
		  newsdata=new ArrayList<News>();
		  mAdapter=new ZujiAdapter(context, appContext, newsdata);
		  newsList.setAdapter(mAdapter);
		  
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
		  
		  showUserInfo();
		  new NewsPageTask(1,UIHelper.LISTVIEW_COUNT,true,UIHelper.LISTVIEW_ACTION_REFRESH).execute();
	}
	
	public void showUserInfo()
	{
		   String picUrl=user.txPath.smallPicture;
		   if(!StringUtils.empty(picUrl))picUrl=picUrl.replace("F:/apachetomcat/webapps/",CommonValue.BASE_URL);
		   
		   

			 DisplayImageOptions touxiang_options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.avatar_placeholder)
			.showImageForEmptyUri(R.drawable.avatar_placeholder)
			.showImageOnFail(R.drawable.avatar_placeholder)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new FadeInBitmapDisplayer(100))
			.build();
						 
			imageLoader.displayImage(picUrl, head, touxiang_options);
			head.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					goImagePage();
				}
				
			});
			name.setText(user.userName);
		   
	}
	
	private void goImagePage() {
		// TODO Auto-generated method stub
		// UserEntity logeduser=appContext.getLoginInfo();
		 String picUrl=user.txPath.largePicPath;
		 
		 if(StringUtils.empty(picUrl) || picUrl.equals("defaultPath"))return;
		 
		 if(!StringUtils.empty(picUrl))picUrl=picUrl.replace("F:/apachetomcat/webapps/",CommonValue.BASE_URL);
		 
		 String[]urls=new String[]{picUrl};


			Intent intent = new Intent(context, ImagePagerActivity.class);
			// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
			context.startActivity(intent);
	}
	
	private int  newslvDataState;
	private int currentNewsPage=1;
	
	private void handleNews(List<News>newsList,boolean isNetWorkData, int action) {
		
		switch (action) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
				newsdata.clear();
				newsdata.addAll(newsList);
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
				newsdata.addAll(newsList);
			break;
		}
		
			if(newsList.size() == UIHelper.LISTVIEW_COUNT){					
				newslvDataState = UIHelper.LISTVIEW_DATA_MORE;
				mAdapter.notifyDataSetChanged();}
			else {
				newslvDataState = UIHelper.LISTVIEW_DATA_FULL;
				mAdapter.notifyDataSetChanged();
				}
			if(newsdata.isEmpty()){
				newslvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
			}
		
		mAdapter.notifyDataSetChanged();
		
	    if(isNetWorkData && UIHelper.LISTVIEW_ACTION_REFRESH==action)saveOrUpdateNews(newsList);
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
		private int mAction;
		boolean mIsInit ;
		
	public	NewsPageTask(int pageIndex,int pageSize,boolean isInit,int action)
		{
			this.mPageIndex=pageIndex;
			this.mPageSize=pageSize;
			this.mAction=action;
			this.mIsInit=isInit;
		}
	
	
	
	 public List<News> doInBackground(Object... params) {
		 		busy=true;
				return NewsManager.getInstance(context).getNewsFromContact(user.userId, mPageIndex, mPageSize);
			
		}

		@Override
		public void onPreExecute()
		{
			if(mIsInit)
			{indicatorImageView.startAnimation(indicatorAnimation);
			indicatorImageView.setVisibility(View.VISIBLE);}
		}
	 
		@Override
	  public  void onCancelled() {
			super.onCancelled();
	   }

		
		@Override
		public void onPostExecute(final List<News> result) {
			
				String apiKey = appContext.getLoginApiKey();
				appContext.getLoginInfo();
				LocationPoint location=appContext.getLoginInfo().userInfo.location;
				if(location==null)location=new LocationPoint(30.0,114.001);
				if(mIsInit && result.size()>0){
					handleNews(result,false, mAction);
					pullRefreshScrollview.onRefreshComplete();
					busy=false;
					indicatorImageView.setVisibility(View.INVISIBLE);
					indicatorImageView.clearAnimation();
					}
				else ApiClient.getNewsOfUser(apiKey, user.userId, mPageIndex, mPageSize, new ClientCallback(){
					@Override
					public void onSuccess(Object data) {
						NewsEntity entity = (NewsEntity)data;
						switch (entity.status) {
						case 1:
							handleNews(entity.newsList,true,mAction);
							pullRefreshScrollview.onRefreshComplete();
							indicatorImageView.setVisibility(View.INVISIBLE);
							indicatorImageView.clearAnimation();
							busy=false;
							break;
						default:
							pullRefreshScrollview.onRefreshComplete();
							busy=false;
							indicatorImageView.setVisibility(View.INVISIBLE);
							indicatorImageView.clearAnimation();
							break;
						
						}
					}
					@Override
					public void onFailure(String message) {
						//UIHelper.dismissProgress(pg);
						handleNews(result,false, mAction);
						pullRefreshScrollview.onRefreshComplete();
						busy=false;
						indicatorImageView.setVisibility(View.INVISIBLE);
						indicatorImageView.clearAnimation();
					}
					
					@Override
					public void onError(Exception e) {
						handleNews(result,false, mAction);
						pullRefreshScrollview.onRefreshComplete();
						busy=false;
						indicatorImageView.setVisibility(View.INVISIBLE);
						indicatorImageView.clearAnimation();
					}
					@Override
					public void onRequestLogIn() {
						// TODO Auto-generated method stub
						
					}
				});

			}
	}	
}
