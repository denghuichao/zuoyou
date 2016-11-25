package com.deng.mychat.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tools.UIHelper;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

import com.deng.mychat.R;
import com.deng.mychat.adapter.MyExpandableListAdapter;
import com.deng.mychat.bean.ContactEntity;
import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.ContactManager;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.im.AWechatActivity;
import com.deng.mychat.model.Notice;
import com.deng.mychat.view.MyExpandableListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.umeng.analytics.MobclickAgent;



public class ContactActivity  extends AWechatActivity {

    private ArrayList<UserInfo> friends;//=new ArrayList<UserInfo>();
    private ArrayList<UserInfo>strangers;//=new ArrayList<UserInfo>();
	
	private ArrayList<String> groupList;// = new ArrayList<String>();
    private ArrayList<ArrayList<UserInfo>> contactArrays;//=new  ArrayList<ArrayList<UserInfo>>();
	
    private MyExpandableListAdapter mAdapter;
	private MyExpandableListView expandableList;
	
	PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;
	
	private boolean isBusyF=false;
	private boolean isBusyS=false;
    private FriendReceiver receiver = null;
    private InputMethodManager imm;  

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_layout);
		initData();
		initUI();	
	}

	public void initData()
	{
		groupList = new ArrayList<String>();
	    contactArrays=new  ArrayList<ArrayList<UserInfo>>();
	    
	    friends=new ArrayList<UserInfo>();
	    strangers=new ArrayList<UserInfo>();
	    
	    contactArrays.add(friends);
	    contactArrays.add(strangers);
	    
	    groupList.add("我的好友");
	    groupList.add("附近的人");
	}
	
	public void initUI()
	{
		
		mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		
		mPullRefreshScrollView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel("下拉刷新");
		mPullRefreshScrollView.getLoadingLayoutProxy(true, false).setPullLabel("");
		mPullRefreshScrollView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新");
		mPullRefreshScrollView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开以刷新");
		// 上拉加载更多时的提示文本设置		
		mPullRefreshScrollView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("上拉加载");
		mPullRefreshScrollView.getLoadingLayoutProxy(false, true).setPullLabel("");
		mPullRefreshScrollView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		mPullRefreshScrollView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
		
		mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {


			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ScrollView> refreshView) {
				if (!isBusyS && strangerlvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					isBusyS=true;
		        	strangerlvDataState = UIHelper.LISTVIEW_DATA_LOADING;
		        	currentStrangerPage++;
		        	new ContactPageTask(currentStrangerPage,UIHelper.LISTVIEW_COUNT,false,UIHelper.LISTVIEW_ACTION_SCROLL).execute();
				}
				mPullRefreshScrollView.onRefreshComplete();
			}

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ScrollView> refreshView) {
				// TODO Auto-generated method stub
				if(!isBusyS)
				{
					isBusyS=true;
					currentStrangerPage=1;
					new ContactPageTask(1,UIHelper.LISTVIEW_COUNT,false,UIHelper.LISTVIEW_ACTION_REFRESH).execute();
				}
				if(!isBusyF)
				{
					isBusyF=true;
					new ContactPageTask(1,1000,true,UIHelper.LISTVIEW_ACTION_REFRESH).execute();
				}
				mPullRefreshScrollView.onRefreshComplete();
			}
		});

		mScrollView = mPullRefreshScrollView.getRefreshableView();
		expandableList=(MyExpandableListView)findViewById(R.id.expandableList);
		expandableList.setGroupIndicator(null); 
    	mAdapter = new MyExpandableListAdapter(this,appContext,groupList, contactArrays);
    	expandableList.setAdapter(mAdapter);
	
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommonValue.ADD_FRIEND_ACTION);
		registerReceiver(receiver, filter);
		
		new ContactPageTask(1,UIHelper.LISTVIEW_COUNT,false,UIHelper.LISTVIEW_ACTION_REFRESH).execute();
		new ContactPageTask(1,1000,true,UIHelper.LISTVIEW_ACTION_INIT).execute();
		
		isBusyF=true;
		isBusyS=true;
		
	}
	
	
	
	public void buttonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.search_ll://showToast("search..");
		Intent i=new Intent(this,SearchActivity.class);
		startActivity(i);
		break;
		}
	}
	
	
	
	private int friendlvDataState;
	private int currentFriendPage=1;
	
	
	private void handleFriends(List<UserInfo> userList,boolean isNetWorkData, int action) {
		if(userList==null)return;
		switch (action) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
			contactArrays.get(0).clear();
			contactArrays.get(0).addAll(userList);
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			contactArrays.get(0).addAll(userList);
			break;
		}
		if(userList.size() == UIHelper.LISTVIEW_COUNT){					
			friendlvDataState = UIHelper.LISTVIEW_DATA_MORE;
			mAdapter.notifyDataSetChanged();
		}
		else {
			friendlvDataState = UIHelper.LISTVIEW_DATA_FULL;
			mAdapter.notifyDataSetChanged();
		}
		if(friends.isEmpty()){
			friendlvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
		}
		mAdapter.notifyDataSetChanged();
		
		if(isNetWorkData) saveOrUpdateContacts(userList);
	}
	
	public void saveOrUpdateContacts(final List<UserInfo> fs)
	{
		new Thread(){
			public void run() {
				ContactManager.getInstance(context).saveOrUpdateContact(fs,appContext.getLoginLocation());
			}
		}.start();
	}
	
	private int currentStrangerPage=1;
	private int  strangerlvDataState;
	
	private void handleStrangers(List<UserInfo> userList,boolean isNetWorkData, int action) {
		if( userList==null)return;
		
		
		switch (action) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
			contactArrays.get(1).clear();
			contactArrays.get(1).addAll(userList);
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			contactArrays.get(1).addAll(userList);
			break;
		}
		if(userList.size() == UIHelper.LISTVIEW_COUNT){					
			strangerlvDataState = UIHelper.LISTVIEW_DATA_MORE;
			mAdapter.notifyDataSetChanged();
		}
		else {
			strangerlvDataState = UIHelper.LISTVIEW_DATA_FULL;
			mAdapter.notifyDataSetChanged();
		}
		if(strangers.isEmpty()){
			strangerlvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
		}
		mAdapter.notifyDataSetChanged();		
		expandableList.expandGroup(1);
		if(isNetWorkData) saveOrUpdateContacts(userList);
	}
	
	
public class ContactPageTask extends AsyncTask<Object, Integer, List<UserInfo>> {
	    
		private int mPageIndex;
		private int mPageSize;
		private boolean mIsFriend;
		private int mAction;
		
	public	ContactPageTask(int pageIndex,int pageSize,boolean isFriend,int action)
		{
			this.mPageIndex=pageIndex;
			this.mPageSize=pageSize;
			this.mIsFriend=isFriend;
			this.mAction=action;
		}
	
		protected List<UserInfo> doInBackground(Object... params) {
			if(mIsFriend)
				return ContactManager.getInstance(context).getMyFriend(mPageIndex, mPageSize);
			else return  ContactManager.getInstance(context).getAroundContacts(mPageIndex, mPageSize);
			
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(final List<UserInfo> result) {
			if(mIsFriend)
			{
				if(result==null || result.size()==0 || mAction==UIHelper.LISTVIEW_ACTION_REFRESH)
				{
					String apiKey = appContext.getLoginApiKey();
					ApiClient.getMyFriend(appContext.getLoginUid(), apiKey, 1, 1000, new ClientCallback() {
					@Override
					public void onSuccess(Object data) {
						ContactEntity entity = (ContactEntity)data;
						isBusyF=false;
						switch (entity.status) {
								case 1:
									handleFriends(entity.userList,true, mAction);
									break;
								default:
									showToast(entity.msg);
									break;
								}
							}
							@Override
						public void onFailure(String message) {
								isBusyF=false;
						}
							
							@Override
						public void onError(Exception e) {
								isBusyF=false;
						}
							@Override
							public void onRequestLogIn() {
								// TODO Auto-generated method stub
								
							}
					});		
				}
				else 
					{handleFriends(result,false, mAction);
					isBusyF=false;}
			}
			else 
			{
				String apiKey = appContext.getLoginApiKey();
				LocationPoint location=appContext.getLoginLocation();
				

				HashMap<String,String> map = new HashMap<String,String>();
				map.put("type","location");
				map.put("location",appContext.getNickname()+"location:"+location); 
				MobclickAgent.onEvent(context, "get_around_persons", map); 
				
				if(location==null){
					isBusyS=false;
					handleStrangers(result,false ,mAction);
					return;
				}
				
				ApiClient.getAroundPerson(appContext.getLoginUid(), apiKey, mPageIndex, mPageSize, location.longitude, location.latitude, new ClientCallback() {
						@Override
						public void onSuccess(Object data) {
							ContactEntity entity = (ContactEntity)data;
							isBusyS=false;
							switch (entity.status) {
							case 1:
								handleStrangers(entity.userList,true, mAction);
								break;
							default:
								showToast(entity.msg);
								break;
							}
						}
						
						@Override
						public void onFailure(String message) {
							isBusyS=false;
							ArrayList<UserInfo>res=new ArrayList<UserInfo>();
							handleStrangers(result,false ,mAction);
							
						}
						
						@Override
						public void onError(Exception e) {
							isBusyS=false;
							handleStrangers(result,false ,mAction);
						}

						@Override
						public void onRequestLogIn() {
							// TODO Auto-generated method stub
							
						}
					});
				
			}
		}
		@Override
		protected void onPreExecute() {
			
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			
		}
	}
	
	private class FriendReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (CommonValue.ADD_FRIEND_ACTION.equals(action)) {
				UserInfo user = (UserInfo) intent.getSerializableExtra("user");
				friends.add(0, user);
			} 
		}
	}
	
	public void show2OptionsDialog(final String[] arg ,final UserInfo model){
		new AlertDialog.Builder(context).setTitle(null).setItems(arg,
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				switch(which){
				case 0:
					try {
						ApiClient.deleteFriend( appContext.getLoginApiKey(), model.userId, null);
						friends.remove(model);
						ContactManager.getInstance(context).deleteFriend(model.userId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}).show();
	}
	


	@Override
	protected void msgSend(String to) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void msgReceive(Notice notice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handReConnect(boolean isSuccess) {
		// TODO Auto-generated method stub
		
	}
}
