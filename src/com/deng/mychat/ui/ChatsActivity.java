package com.deng.mychat.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;

import tools.Logger;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.deng.mychat.R;
import com.deng.mychat.adapter.WeChatAdapter;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.FriendNoticeManager;
import com.deng.mychat.config.MessageManager;
import com.deng.mychat.config.XmppConnectionManager;
import com.deng.mychat.im.AWechatActivity;
import com.deng.mychat.model.HistoryChatBean;
import com.deng.mychat.model.IMMessage;
import com.deng.mychat.model.Notice;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

public class ChatsActivity extends AWechatActivity {
	//Chat Page上的组件
   // private SearchTextView chatSearchEdit;
	private TextView title;
	//private ImageView indicatorImageView;
	//private Animation indicatorAnimation;	
	private List inviteNotices;
	private WeChatAdapter noticeAdapter;
	
    //private InputMethodManager imm;  

	private PullToRefreshListView pullToRefreshListView;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatlist_layout);
		initUI();
	}

	private void initUI() {
		
		long a=System.currentTimeMillis();
		// TODO Auto-generated method stub
		//chatSearchEdit =(SearchTextView)findViewById(R.id.chatSearchEdit);
		//imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		//imm.hideSoftInputFromWindow(chatSearchEdit.getWindowToken(), 0);
		title = (TextView) findViewById(R.id.title);
		//indicatorImageView = (ImageView) findViewById(R.id.xindicator);
		//indicatorAnimation = AnimationUtils.loadAnimation(this, R.anim.refresh_button_rotation);
		//indicatorAnimation.setDuration(500);
		//indicatorAnimation.setInterpolator(new Interpolator() {
		//    private final int frameCount = 10;
		//    @Override
		//    public float getInterpolation(float input) {
		//        return (float)Math.floor(input*frameCount)/frameCount;
		//    }
		//});
		
		//chatsSwipeLayout=(SwipeRefreshLayout)findViewById(R.id.chatXrefresh);
		//chatsSwipeLayout.setOnRefreshListener(new OnRefreshListener() {	
		//	@Override
		//	public void onRefresh() {
				//getMyFriend(currentFriendPage, UIHelper.LISTVIEW_ACTION_SCROLL);
		//	}
		//});
		
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.chatsList);
		pullToRefreshListView.setMode(Mode.DISABLED);
		pullToRefreshListView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel("下拉刷新");
		pullToRefreshListView.getLoadingLayoutProxy(true, false).setPullLabel("正在刷新");
		pullToRefreshListView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新");
		pullToRefreshListView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开以刷新");
		// 上拉加载更多时的提示文本设置		
		pullToRefreshListView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("上拉加载");
		pullToRefreshListView.getLoadingLayoutProxy(false, true).setPullLabel("正在加载");
		pullToRefreshListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		pullToRefreshListView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
		
		pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>(){

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				//getHistoryChat();
				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				//pullToRefreshListView.onRefreshComplete();
			}
			
		});
		
        inviteNotices = new ArrayList<Object>();
		noticeAdapter = new WeChatAdapter(appContext,this, inviteNotices);
		pullToRefreshListView.setAdapter(noticeAdapter);
	
		getHistoryChat();
		
		//Log.d("Page","chats"+( System.currentTimeMillis()-a));

	}
	

	@Override
	protected void msgReceive(final Notice notice) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				noticeAdapter.notifyDataSetChanged();
			}
		};
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		singleThreadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				inviteNotices = MessageManager.getInstance(context).getRecentContactsWithLastMsg();
				for (Object ch : inviteNotices) {
				if(ch instanceof HistoryChatBean)
				{
					if (((HistoryChatBean) ch).getFrom().equals(notice.getFrom())) {
						((HistoryChatBean) ch).setContent(notice.getContent());
						((HistoryChatBean) ch).setNoticeTime(notice.getNoticeTime());
						Integer x = ((HistoryChatBean) ch).getNoticeSum() == null ? 0 : ((HistoryChatBean) ch).getNoticeSum();
						((HistoryChatBean) ch).setNoticeSum(x);
					}
				}
			}
			noticeAdapter.setNoticeList(inviteNotices);
			handler.sendEmptyMessage(0);
			}
		});
	}
	
	
	@Override
	protected void handReConnect(boolean isSuccess) {
		if (CommonValue.RECONNECT_STATE_SUCCESS == isSuccess) {
			//titleBarView.setText("已连接");

		} else if (CommonValue.RECONNECT_STATE_FAIL == isSuccess) {
			//titleBarView.setText("未连接");
		}
	}

	
	private void getHistoryChat() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//noticeAdapter.setNoticeList(inviteNotices);
				noticeAdapter.notifyDataSetChanged();
				//pullToRefreshListView.onRefreshComplete();
			}
		};
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		singleThreadExecutor.execute(new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				//inviteNotices.clear();
				inviteNotices.addAll(MessageManager.getInstance(context)
						.getRecentContactsWithLastMsg());		        
		        inviteNotices.addAll(FriendNoticeManager.getInstance(context).getNoticeList());
				handler.sendEmptyMessage(1);
			}
		});
	}
	
	private void connect2xmpp()  {
		//indicatorImageView.startAnimation(indicatorAnimation);
		//indicatorImageView.setVisibility(View.VISIBLE);
		//titleBarView.setText("连线中...");
		final Handler handler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch(msg.what){
				case 1:
					//indicatorImageView.setVisibility(View.INVISIBLE);
					//indicatorImageView.clearAnimation();
					//titleBarView.setText("会话");
					startService();
					break;
				case 2:
					//indicatorImageView.setVisibility(View.INVISIBLE);
					//indicatorImageView.clearAnimation();
					//titleBarView.setText("未连接");
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
	
	private boolean isExit = false;
	private void sortChat(final String to) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				noticeAdapter.notifyDataSetChanged();
			}
		};
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		singleThreadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				isExit = false;
				List<IMMessage> chats = MessageManager.getInstance(context).getMessageListByFrom(to, 1, 1);
				if (chats.size() < 1) {
					return;
				}
				for (Object ch : inviteNotices) {
					if(ch instanceof  HistoryChatBean)
					if (((HistoryChatBean) ch).getFrom().equals(chats.get(0).getFromSubJid())) {
						((HistoryChatBean) ch).setContent(chats.get(0).getContent());
						((HistoryChatBean) ch).setNoticeTime(chats.get(0).getTime());
						((HistoryChatBean) ch).setNoticeSum(0);
						isExit = true;
					}
				}
				if (!isExit) {
					HistoryChatBean ch = new HistoryChatBean();
					ch.setFrom(chats.get(0).getFromSubJid());
					ch.setContent(chats.get(0).getContent());
					ch.setNoticeSum(0);
					ch.setTo(to);
					ch.setStatus(Notice.READ);
					ch.setNoticeType(Notice.CHAT_MSG);
					ch.setNoticeTime(chats.get(0).getTime());
					inviteNotices.add(ch);
				}
				
				//Collections.sort(inviteNotices);
				handler.sendEmptyMessage(0);
			}
		});
	}

	@Override
	protected void msgSend(String to) {
		sortChat(to);
	}
		

	public class GetDataTask extends AsyncTask<Void, Void, String[]> {
		int pullState;
		
		public GetDataTask(int pullType) {
			this.pullState = pullType;
		}
		
		@Override
		protected String[] doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if(pullState == 1) {//name="pullDownFromTop" value="0x1" 涓嬫媺
				//mListItems.addFirst("Added after refresh by first...");
			}
			if(pullState == 2) {//涓婃媺
				//mListItems.addLast("Added after refresh by last...");
			}
			//297e9e794d749d77014d7938e5580043@webchat.com
			noticeAdapter.notifyDataSetChanged();

			pullToRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}

	private String[] mStrings = { "Abbaye de Belloc" };


}
