package com.deng.mychat.ui;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tools.UIHelper;

import com.deng.mychat.R;
import com.deng.mychat.bean.News;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.FriendNoticeManager;
import com.deng.mychat.config.MessageManager;
import com.deng.mychat.config.NewsManager;
import com.deng.mychat.config.NewsNoticeManager;
import com.deng.mychat.model.HistoryChatBean;
import com.deng.mychat.model.Notice;
import com.deng.mychat.ui.NewsActivity.NewsPageTask;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;


public class Tabbar extends TabActivity implements OnCheckedChangeListener{
	private RadioGroup mainTab;
	public static TabHost mTabHost;
	private RelativeLayout redpoint1;
	private RelativeLayout redpoint2;
	private RelativeLayout redpoint3;
	private RelativeLayout redpoint4;
	
	private Intent newsIntent;
	private Intent contactIntent;
	private Intent chatsIntent;
	private Intent meIntent;
	
	private final static String TAB_TAG_NEWS = "tab_tag_news";
	private final static String TAB_TAG_CONTACT = "tab_tag_contact";
	private final static String TAB_TAG_CHAT = "tab_tag_chat";
	private final static String TAB_TAG_ME = "tab_tag_me";
	
	private int numOfNoticeOfNews=0;
	private int numOfNoticeOfContact=0;;
	private int numOfNoticeOfChat=0;
	private int numOfNoticeOfMe=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index);
        mainTab=(RadioGroup)findViewById(R.id.main_tab);
        mainTab.setOnCheckedChangeListener(this);
        prepareIntent();
        setupIntent();
        RadioButton homebutton = (RadioButton)findViewById(R.id.radio_button1);
        homebutton.setChecked(true); 
        
        redpoint1=(RelativeLayout)findViewById(R.id.redpoint1);
        redpoint2=(RelativeLayout)findViewById(R.id.redpoint2);
        redpoint3=(RelativeLayout)findViewById(R.id.redpoint3);
        redpoint4=(RelativeLayout)findViewById(R.id.redpoint4);
       
	}

	
	@Override
	protected void onPause() {
		unregisterReceiver(myBroadcastReceiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initReceiver();
	    updateTabbarNews();
	}
	

	
	private void initReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CommonValue.ACTION_TABBAR_NOTICE);
		intentFilter.addAction(CommonValue.ACTION_NEWS_NOTICE);
		//intentFilter.addAction(CommonValue.ACTION_CHAT_NOTICE_ADD);
		intentFilter.addAction(CommonValue.NEW_MESSAGE_ACTION);
		registerReceiver(myBroadcastReceiver, intentFilter);
	}
	
	private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			//if()
			updateTabbarNews();
		}
	};
	
	protected void updateTabbarNews() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//noticeAdapter.notifyDataSetChanged();
				redpoint1.setVisibility(numOfNoticeOfNews==0?View.INVISIBLE:View.VISIBLE);
				redpoint2.setVisibility(numOfNoticeOfContact==0?View.INVISIBLE:View.VISIBLE);
				redpoint3.setVisibility(numOfNoticeOfChat==0?View.INVISIBLE:View.VISIBLE);
				redpoint4.setVisibility(numOfNoticeOfMe==0?View.INVISIBLE:View.VISIBLE);
			}
		};
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		singleThreadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				//NewsNoticeManager nm=NewsNoticeManager.getInstance(Tabbar.this);
				numOfNoticeOfNews=NewsNoticeManager.getInstance(Tabbar.this).getUnReadNewsNoticeCount();
				numOfNoticeOfChat=0;
				List<HistoryChatBean>chatList = MessageManager.getInstance(Tabbar.this).getRecentContactsWithLastMsg();
				for(HistoryChatBean cb:chatList)numOfNoticeOfChat+=(cb.getNoticeSum()==null?0:cb.getNoticeSum());
				numOfNoticeOfChat+=FriendNoticeManager.getInstance(Tabbar.this).getUnHandledNoticeCount();
				handler.sendEmptyMessage(0);
			}
		});
	 }
	
	private void prepareIntent() {

		newsIntent = new Intent(this, NewsActivity.class);
		contactIntent = new Intent(this, ContactActivity.class);
		chatsIntent = new Intent(this, ChatsActivity.class);
		meIntent = new Intent(this, MeActivity.class);
	}
	
	private void setupIntent() {
		mTabHost = getTabHost();
		TabHost localTabHost = mTabHost;	
		localTabHost.addTab(buildTabSpec(TAB_TAG_NEWS, "", R.drawable.tabbar_button3, newsIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_CONTACT, "", R.drawable.tabbar_button2, contactIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_CHAT, "", R.drawable.tabbar_button1, chatsIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_ME, "", R.drawable.tabbar_button4, meIntent));
	}
	
	private TabHost.TabSpec buildTabSpec(String tag, String label, int resIcon,final Intent content) {
		return this.mTabHost.newTabSpec(tag).setIndicator(label,
				getResources().getDrawable(resIcon)).setContent(content);
	} 
	
	
	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		switch(checkedId){
		case R.id.radio_button1:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_NEWS);
			break;
		case R.id.radio_button2:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_CONTACT);
			break;
		case R.id.radio_button3:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_CHAT);
			break;
		case R.id.radio_button4:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_ME);
			break;
		}
	}
	
	public void tabClick(View v) {
		
	}
}

