package com.deng.mychat.service;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import com.deng.mychat.R;
import com.deng.mychat.bean.JsonMessage;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.ContactManager;
import com.deng.mychat.config.MessageManager;
import com.deng.mychat.config.NoticeManager;
import com.deng.mychat.config.XmppConnectionManager;
import com.deng.mychat.model.IMMessage;
import com.deng.mychat.model.Notice;
import com.deng.mychat.ui.ChatsActivity;
import com.google.gson.Gson;

import tools.Logger;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class IMChatService extends Service {
	private Context context;
	private NotificationManager notificationManager;
	private ChatListener cListener;	
	
	@Override
	public void onCreate() {
		context = this;
		Logger.i("c");
		super.onCreate();
		initChatManager();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.i("s");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
//		XMPPConnection conn = XmppConnectionManager.getInstance()
//				.getConnection();
//		if (cListener != null) {
//			conn.removePacketListener(cListener);
//		}
		super.onDestroy();
	}

	private void initChatManager() {
		XMPPConnection conn = XmppConnectionManager.getInstance()
				.getConnection();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		cListener = new ChatListener();
		conn.addPacketListener(cListener, new MessageTypeFilter(
				Message.Type.chat));
	}

	class ChatListener implements PacketListener {

		@Override
		public void processPacket(Packet arg0) {
			Message message = (Message) arg0;
			if (message != null && message.getBody() != null
					&& !message.getBody().equals("null")) {
				IMMessage msg = new IMMessage();
				String time = (System.currentTimeMillis()/1000)+"";//DateUtil.date2Str(Calendar.getInstance(), Constant.MS_FORMART);
				msg.setTime(time);
				msg.setContent(message.getBody());
				if (Message.Type.error == message.getType()) {
					msg.setType(IMMessage.ERROR);
				} else {
					msg.setType(IMMessage.SUCCESS);
				}
				String from = message.getFrom().split("/")[0];
				msg.setFromSubJid(from);
				NoticeManager noticeManager = NoticeManager
						.getInstance(context);
				Notice notice = new Notice();
				notice.setTitle("会话信息");
				notice.setNoticeType(Notice.CHAT_MSG);
				notice.setContent(message.getBody());
				notice.setFrom(from);
				notice.setStatus(Notice.UNREAD);
				notice.setNoticeTime(time);

				IMMessage newMessage = new IMMessage();
				newMessage.setMsgType(0);
				newMessage.setFromSubJid(from);
				newMessage.setContent(message.getBody());
				newMessage.setTime(time);
				newMessage.setType(0); 
				MessageManager.getInstance(context).saveIMMessage(newMessage);
				
				UserInfo user=ContactManager.getInstance(context).getContact(from);
				long noticeId = -1;

				noticeId = noticeManager.saveNotice(notice);
				if (noticeId != -1) {
					Intent intent = new Intent(CommonValue.NEW_MESSAGE_ACTION);
					intent.putExtra(IMMessage.IMMESSAGE_KEY, msg);
					intent.putExtra("notice", notice);
					sendBroadcast(intent);
					setNotiType(R.drawable.logo,
							"新消息",
							notice.getContent(), ChatsActivity.class, from);

				}
			}
		}
		
	}

	private void setNotiType(int iconId, String contentTitle,
			String contentText, Class activity, String from) {
		JsonMessage msg = new JsonMessage();
		Gson gson = new Gson();
		msg = gson.fromJson(contentText, JsonMessage.class);
		Intent notifyIntent = new Intent(this, activity);
		notifyIntent.putExtra("to", from);
		PendingIntent appIntent = PendingIntent.getActivity(this, 0,
				notifyIntent, 0);
		Notification myNoti = new Notification();
		myNoti.flags = Notification.FLAG_AUTO_CANCEL;
		myNoti.icon = iconId;
		myNoti.tickerText = contentTitle;
		myNoti.defaults |= Notification.DEFAULT_SOUND;
		myNoti.defaults |= Notification.DEFAULT_VIBRATE;
		myNoti.setLatestEventInfo(this, contentTitle, msg.text, appIntent);
		notificationManager.notify(0, myNoti);
	}
}
