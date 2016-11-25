package com.deng.mychat.config;

import tools.AppManager;
import tools.ImageUtils;
import android.graphics.Bitmap;

import com.deng.mychat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * wechat
 *
 * @author donal
 *
 *///http://denghuichao.oicp.net
public class CommonValue {
	public static String PackageName = "com.deng.mychat";//192.168.139.1
	public static String BASE_API ="http://115.29.228.117:8080/webchat/api/";//;"http://192.168.1.115:8080/webchat/api/";
	public static String BASE_URL ="http://115.29.228.117:8080/";//;"http://192.168.1.115:8080/";////
	
	public static final int kWCMessageTypePlain = 0;
	public static final int kWCMessageTypeImage = 1;
	public static final int kWCMessageTypeVoice =2;
	public static final int kWCMessageTypeLocation=3;                                                         
	
	public static final int kWCMessageStatusWait = 1;
	public static final int kWCMessageStatusSending = 2;
	
	// auil options
	public interface DisplayOptions {
		
        
		public DisplayImageOptions default_options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_stub)
		.showImageOnFail(R.drawable.ic_stub)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new FadeInBitmapDisplayer(100))
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		
		public DisplayImageOptions touxiang_options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.avatar_placeholder)
		.showImageForEmptyUri(R.drawable.avatar_placeholder)
		.showImageOnFail(R.drawable.avatar_placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(30))
		.displayer(new FadeInBitmapDisplayer(100))
		.bitmapConfig(Bitmap.Config.RGB_565)
		//.displayer(new RoundedBitmapDisplayer(20))
		.build();
		
		public DisplayImageOptions avatar_options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_stub)
		.showImageOnFail(R.drawable.ic_stub)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new FadeInBitmapDisplayer(100))
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
	
	public interface Operation {
		String addFriend = "加好友";
		String chatFriend = "发消息";
	}
	
	public static final String NEW_MESSAGE_ACTION = "chat.newmessage";
	public static final String SEND_MESSAGE_ACTION = "chat.sendmessage";
	
	public static final String ADD_FRIEND_ACTION = "add.friend";
	/**
	 * USERINFO
	 */
	public static final String LOGIN_SET = "login_set";
	public static final String USERID = "userId";
	public static final String APIKEY = "apiKey";
	
	public static final int MY_NEWS=0;
	public static final int FRIEND_NEWS=1;
	public static final int AROUND_NEWS=2;

	/**
	 * 重连�?	 */
	/**
	 * 重连接状态acttion
	 * 
	 */
	public static final String ACTION_RECONNECT_STATE = "action_reconnect_state";
	/**
	 * 描述重连接状态的关机子，寄放的intent的关键字
	 */
	public static final String RECONNECT_STATE = "reconnect_state";
	/**
	 * 描述重连接，
	 */
	public static final boolean RECONNECT_STATE_SUCCESS = true;
	public static final boolean RECONNECT_STATE_FAIL = false;
	
	/**
	 * 注册
	 */
	public static final int REQUEST_REGISTER_INFO = 1;
	
	/**
	 * 打开会话
	 */
	public static final int REQUEST_OPEN_CHAT = 2;
	
	/*短信SDK Key*/
	public static final String APP_KEY="6635b9c91af8";
	public static final String APP_SCREET="6f0ea59af53d99f8ca7623972c8d3acd";
	
	public static final String UMENG_APP_KEY="5513e7fafd98c50415000315";
	public static final String ACTION_REFLESH ="mychat.action.reflesh";
	public static final String ACTION_NOTIFY = "mychat.action.notify";
	public static final String ACTION_NEWS_NOTICE="mychat.action.news_notice";//新鲜事列表顶端的通知
	public static final String ACTION_TABBAR_NOTICE="mychat.action.tabbar_notice";//导航条的通知
	public static final String ACTION_CHAT_NOTICE_ADD="mychat.action.chat_notice_add";
	
}
