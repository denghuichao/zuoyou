package com.deng.mychat.im;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import tools.AppManager;
import tools.DateUtil;
import tools.ImageUtils;
import tools.Logger;
import tools.StringUtils;
import tools.UIHelper;

import com.deng.mychat.R;
import com.deng.mychat.auth.JSONObjectRet;
import com.deng.mychat.bean.JsonMessage;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.ContactManager;
import com.deng.mychat.config.NoticeManager;
import com.deng.mychat.image_viewer.ImagePagerActivity;
import com.deng.mychat.io.IO;
import com.deng.mychat.io.PutExtra;
import com.deng.mychat.model.IMMessage;
import com.deng.mychat.model.Notice;
import com.deng.mychat.tools.AudioPlayManager;
import com.deng.mychat.tools.AudioRecoderManager;
import com.deng.mychat.ui.ContactMainPageActivity;
import com.deng.mychat.ui.MeActivity;
import com.deng.mychat.util.Config;
import com.deng.mychat.util.Mac;
import com.deng.mychat.util.PutPolicy;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class Chating extends AChating implements OnTouchListener, OnItemClickListener, VoiceBubbleListener, OnEditorActionListener{
	private boolean isFace = false;
	private Button voiceOrTextButton;
	private Button voiceButton;
	private MessageListAdapter adapter = null;
	private EditText messageInput = null;
	private ListView listView;
	private TextView chatName;
	private int recordCount;
	private UserInfo user;// 
	private Notice notice;
	
	private int firstVisibleItem;
	private int currentPage = 1;
	private int objc;
	
	private LinearLayout topLayout;
	
	private AnimationDrawable leftAnimationDrawable;
	private AnimationDrawable rightAnimationDrawable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chating);
		
 		user = ContactManager.getInstance(context).getContact(to.split("@")[0]);
 		
		initUI();
	}
	

	
	private void initUI() {
		topLayout=(LinearLayout)findViewById(R.id.topLayout);
		chatName=(TextView)findViewById(R.id.chatName);
		chatName.setText((user==null||user.userName==null)?"":user.userName);
		voiceOrTextButton = (Button) findViewById(R.id.voiceOrTextButton);
		voiceButton = (Button) findViewById(R.id.voiceButton);
		voiceButton.setOnTouchListener(this);
		listView = (ListView) findViewById(R.id.chat_list);
		listView.setCacheColorHint(0);
		adapter = new MessageListAdapter(Chating.this, getMessages(),
				listView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case SCROLL_STATE_FLING:
					break;
				case SCROLL_STATE_IDLE:
					if (firstVisibleItem == 0) {
						int num = addNewMessage(++currentPage);
						if (num > 0) {
							adapter.refreshList(getMessages());
							listView.setSelection(num-1);
						}
					}
					break;
				case SCROLL_STATE_TOUCH_SCROLL:
					closeInput();
					break;
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Chating.this.firstVisibleItem = firstVisibleItem;
			}
		});

		messageInput = (EditText) findViewById(R.id.chat_content);
		messageInput.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listView.setSelection(getMessages().size()-1);
			}
		});
		messageInput.setOnEditorActionListener(this);
	}
	
	 protected void goTocontactMainPage(UserInfo user) {
			// TODO Auto-generated method stub
	    	Intent intent=new Intent(context,ContactMainPageActivity.class);
			intent.putExtra("isMyFriend", user.isMyFriend);
			intent.putExtra("user", user);
			context.startActivity(intent);
		}
	
	public void ButtonClick(View v) {
		switch (v.getId()) {
		case R.id.leftBarButton:
			closeInput();
			finish();
			break;
		case R.id.voiceOrTextButton:
			if (messageInput.getVisibility() == View.VISIBLE) {
				closeInput();
				messageInput.setVisibility(View.INVISIBLE);
				voiceButton.setVisibility(View.VISIBLE);
				voiceOrTextButton.setBackgroundResource(R.drawable.keyborad);
				////faceOrTextButton.setBackgroundResource(R.drawable.face);
			}
			else if (messageInput.getVisibility() == View.INVISIBLE) {
				messageInput.setVisibility(View.VISIBLE);
				voiceButton.setVisibility(View.INVISIBLE);
				voiceOrTextButton.setBackgroundResource(R.drawable.voice);
				
			}
			break;
		case R.id.sendButton:
			String message = messageInput.getText().toString();
			if ("".equals(message)) {
				Toast.makeText(Chating.this, "不能为空",
						Toast.LENGTH_SHORT).show();
			} else {

				try {
					sendMessage(message);
					messageInput.setText("");
				} catch (Exception e) {
					showToast("信息发送失败");
					messageInput.setText(message);
				}
				//closeInput();
			}
			listView.setSelection(getMessages().size()-1);
			break;

		case R.id.multiMediaButton:
			AudioPlayManager.getInstance(this, this).stopPlay();
			closeInput();
			new PopupWindows(this, topLayout);
			break;
		}
	}

	@Override
	protected void receiveNotice(Notice notice) {
		this.notice = notice;
	}
	
	@Override
	protected void receiveNewMessage(IMMessage message) {
		
	}

	@Override
	protected void refreshMessage(List<IMMessage> messages) {
		adapter.refreshList(messages);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.refreshList(getMessages());
		listView.setSelection(getMessages().size()-1);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		String newPhotoPath;
		switch (requestCode) {
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
			if (StringUtils.notEmpty(theLarge)) {
				File file = new File(theLarge);
				File dir = new File( ImageUtils.CACHE_IMAGE_FILE_PATH);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				String imagePathAfterCompass = ImageUtils.CACHE_IMAGE_FILE_PATH + file.getName();
				try {
					ExifInterface sourceExif = new ExifInterface(theLarge);
					String orientation = sourceExif.getAttribute(ExifInterface.TAG_ORIENTATION);
					ImageUtils.saveImageToSD(imagePathAfterCompass, ImageUtils.getSmallBitmap(theLarge, 200), 80);
					ExifInterface exif = new ExifInterface(imagePathAfterCompass);
					exif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation);
				    exif.saveAttributes();
					newPhotoPath = imagePathAfterCompass;
					uploadPhotoToQiniu(newPhotoPath);
				} catch (IOException e) {
					//Crashlytics.logException(e);
				}
			}
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
			if(data == null)  return;
			Uri thisUri = data.getData();
        	String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(thisUri);
        	if(StringUtils.empty(thePath)) {
        		newPhotoPath = ImageUtils.getAbsoluteImagePath(this,thisUri);
        	}
        	else {
        		newPhotoPath = thePath;
        	}
        	File file = new File(newPhotoPath);
			File dir = new File( ImageUtils.CACHE_IMAGE_FILE_PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String imagePathAfterCompass = ImageUtils.CACHE_IMAGE_FILE_PATH + file.getName();
			try {
				ExifInterface sourceExif = new ExifInterface(newPhotoPath);
				String orientation = sourceExif.getAttribute(ExifInterface.TAG_ORIENTATION);
				ImageUtils.saveImageToSD(imagePathAfterCompass, ImageUtils.getSmallBitmap(newPhotoPath, 200), 80);
				ExifInterface exif = new ExifInterface(imagePathAfterCompass);
				exif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation);
			    exif.saveAttributes();
				newPhotoPath = imagePathAfterCompass;
				uploadPhotoToQiniu(newPhotoPath);
			} catch (IOException e) {
				//Crashlytics.logException(e);
			}
			break;
		}
	}
	
	private String theLarge;
	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String savePath = "";
					String storageState = Environment.getExternalStorageState();		
					if(storageState.equals(Environment.MEDIA_MOUNTED)){
						savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + ImageUtils.DCIM;//存放照片的文件夹
						File savedir = new File(savePath);
						if (!savedir.exists()) {
							savedir.mkdirs();
						}
					}
					if(StringUtils.empty(savePath)){
						showToast("无法保存照片，请检查SD卡是否挂载");
						return;
					}
					String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					String fileName = "c_" + timeStamp + ".jpg";//照片命名
					File out = new File(savePath, fileName);
					Uri uri = Uri.fromFile(out);
					theLarge = savePath + fileName;//该照片的绝对路径
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}
	
	
	
	
	private class MessageListAdapter extends BaseAdapter {

		class ViewHolderLeftText {
			TextView timeTV;
			ImageView leftAvatar;
			TextView leftNickname;
			TextView leftText;
		}
		
		class ViewHolderLeftImage {
			TextView timeTV;
			ImageView leftAvatar;
			TextView leftNickname;
			ImageView leftPhoto;
		}
		
		class ViewHolderLeftVoice {
			TextView timeTV;
			ImageView leftAvatar;
			TextView leftNickname;
			ImageView leftVoice;
		}
		
		class ViewHolderRightText {
			TextView timeTV;
			ImageView rightAvatar;
			TextView rightNickname;
			TextView rightText;
			ProgressBar rightProgress;
		}
		
		class ViewHolderRightImage {
			TextView timeTV;
			ImageView rightAvatar;
			TextView rightNickname;
			ImageView rightPhoto;
			TextView photoProgress;
			ProgressBar rightProgress;
		}
		
		class ViewHolderRightVoice {
			TextView timeTV;
			ImageView rightAvatar;
			TextView rightNickname;
			ImageView rightVoice;
			ProgressBar rightProgress;
		}
		
		private List<IMMessage> items;
		private Context context;
		private ListView adapterList;
		private LayoutInflater inflater;

		DisplayImageOptions options;
		DisplayImageOptions photooptions;
		
		public MessageListAdapter(Context context, List<IMMessage> items,
				ListView adapterList) {
			this.context = context;
			this.items = items;
			this.adapterList = adapterList;
			inflater = LayoutInflater.from(context);
			photooptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_stub)
			.showImageOnFail(R.drawable.ic_stub)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
			options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.avatar_placeholder)
			.showImageForEmptyUri(R.drawable.avatar_placeholder)
			.showImageOnFail(R.drawable.avatar_placeholder)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
		}

		public void refreshList(List<IMMessage> items) {
			this.items = items;
			this.notifyDataSetChanged();
			if (this.items.size()>1) {
				listView.setSelection(items.size()-1);
			}
		}

		@Override
		public int getCount() {
			return items == null ? 0 : items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolderRightText holderRightText = null;
			ViewHolderRightImage holderRightImg = null;
			ViewHolderRightVoice holderRightVoice = null;
			ViewHolderLeftText holderLeftText = null;
			ViewHolderLeftImage holderLeftImg = null;
			ViewHolderLeftVoice holderLeftVoice = null;
			try {
				IMMessage message = items.get(position);
				String content = message.getContent();
				final JsonMessage msg = JsonMessage.parse(content);
				if (convertView == null) {
					if (message.getMsgType() == 0) {
						switch (msg.messageType) {
						case CommonValue.kWCMessageTypePlain:
							holderLeftText = new ViewHolderLeftText();
							convertView = inflater.inflate(R.layout.chat_left_text,null);
							holderLeftText.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
							holderLeftText.leftAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_l);
							holderLeftText.leftNickname = (TextView) convertView.findViewById(R.id.textview_name_l);
							holderLeftText.leftText = (TextView) convertView.findViewById(R.id.textview_content_l);
							displayLeftText(msg, holderLeftText, position);
							convertView.setTag(holderLeftText);
							break;
						case CommonValue.kWCMessageTypeImage:
							holderLeftImg = new ViewHolderLeftImage();
							convertView = inflater.inflate(R.layout.chat_left_image,null);
							holderLeftImg.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
							holderLeftImg.leftAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_l);
							holderLeftImg.leftNickname = (TextView) convertView.findViewById(R.id.textview_name_l);
							holderLeftImg.leftPhoto = (ImageView) convertView.findViewById(R.id.photo_content_l);
							displayLeftImage(msg, holderLeftImg, position);
							convertView.setTag(holderLeftImg);
							break;
						case CommonValue.kWCMessageTypeVoice:
							holderLeftVoice = new ViewHolderLeftVoice();
							convertView = inflater.inflate(R.layout.chat_left_voice,null);
							holderLeftVoice.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
							holderLeftVoice.leftAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_l);
							holderLeftVoice.leftNickname = (TextView) convertView.findViewById(R.id.textview_name_l);
							holderLeftVoice.leftVoice = (ImageView) convertView.findViewById(R.id.receiverVoiceNode);
							displayLeftVoice(msg, holderLeftVoice, position);
							convertView.setTag(holderLeftVoice);
							break;
						}
					}
					else {
						switch (msg.messageType) {
						case CommonValue.kWCMessageTypePlain:
							holderRightText = new ViewHolderRightText();
							convertView = inflater.inflate(R.layout.chat_right_text, null);
							holderRightText.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
							holderRightText.rightAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_r);
							holderRightText.rightNickname = (TextView) convertView.findViewById(R.id.textview_name_r);
							holderRightText.rightText = (TextView) convertView.findViewById(R.id.textview_content_r);
							displayRightText(msg, holderRightText, position);
							convertView.setTag(holderRightText);
							break;
						case CommonValue.kWCMessageTypeImage:
							holderRightImg = new ViewHolderRightImage();
							convertView = inflater.inflate(R.layout.chat_right_image, null);
							holderRightImg.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
							holderRightImg.rightAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_r);
							holderRightImg.rightNickname = (TextView) convertView.findViewById(R.id.textview_name_r);
							holderRightImg.rightPhoto = (ImageView) convertView.findViewById(R.id.photo_content_r);
							holderRightImg.photoProgress = (TextView) convertView.findViewById(R.id.photo_content_progress);
							holderRightImg.rightProgress = (ProgressBar) convertView.findViewById(R.id.view_progress_r);
							displayRightImage(message, msg, holderRightImg, position);
							convertView.setTag(holderRightImg);
							break;
							
						case CommonValue.kWCMessageTypeVoice:
							holderRightVoice = new ViewHolderRightVoice();
							convertView = inflater.inflate(R.layout.chat_right_voice, null);
							holderRightVoice.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
							holderRightVoice.rightAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_r);
							holderRightVoice.rightNickname = (TextView) convertView.findViewById(R.id.textview_name_r);
							holderRightVoice.rightVoice = (ImageView) convertView.findViewById(R.id.senderVoiceNode);
							holderRightVoice.rightProgress = (ProgressBar) convertView.findViewById(R.id.view_progress_r);
							displayRightVoice(message, msg, holderRightVoice, position);
							convertView.setTag(holderRightVoice);
							break;
						}
					}
				}
				else {
					if (message.getMsgType() == 0) {
						switch (msg.messageType) {
						case CommonValue.kWCMessageTypePlain:
							if (convertView.getTag() instanceof ViewHolderLeftText) {
								holderLeftText = (ViewHolderLeftText) convertView.getTag();
								displayLeftText(msg, holderLeftText, position);
							}
							else {
								holderLeftText = new ViewHolderLeftText();
								convertView = inflater.inflate(R.layout.chat_left_text,null);
								holderLeftText.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
								holderLeftText.leftAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_l);
								holderLeftText.leftNickname = (TextView) convertView.findViewById(R.id.textview_name_l);
								holderLeftText.leftText = (TextView) convertView.findViewById(R.id.textview_content_l);
								displayLeftText(msg, holderLeftText, position);
								convertView.setTag(holderLeftText);
							}
							break;
						case CommonValue.kWCMessageTypeImage:
							if (convertView.getTag() instanceof ViewHolderLeftImage) {
								holderLeftImg = (ViewHolderLeftImage) convertView.getTag();
								displayLeftImage(msg, holderLeftImg, position);
							}
							else {
								holderLeftImg = new ViewHolderLeftImage();
								convertView = inflater.inflate(R.layout.chat_left_image,null);
								holderLeftImg.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
								holderLeftImg.leftAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_l);
								holderLeftImg.leftNickname = (TextView) convertView.findViewById(R.id.textview_name_l);
								holderLeftImg.leftPhoto = (ImageView) convertView.findViewById(R.id.photo_content_l);
								displayLeftImage(msg, holderLeftImg, position);
								convertView.setTag(holderLeftImg);
							}
							break;
						case CommonValue.kWCMessageTypeVoice:
							if (convertView.getTag() instanceof ViewHolderLeftVoice) {
								holderLeftVoice = (ViewHolderLeftVoice) convertView.getTag();
								displayLeftVoice(msg, holderLeftVoice, position);
							}
							else {
								holderLeftVoice = new ViewHolderLeftVoice();
								convertView = inflater.inflate(R.layout.chat_left_voice,null);
								holderLeftVoice.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
								holderLeftVoice.leftAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_l);
								holderLeftVoice.leftNickname = (TextView) convertView.findViewById(R.id.textview_name_l);
								holderLeftVoice.leftVoice = (ImageView) convertView.findViewById(R.id.receiverVoiceNode);
								displayLeftVoice(msg, holderLeftVoice, position);
								convertView.setTag(holderLeftVoice);
							}
							break;
						}
					}
					else {
						switch (msg.messageType) {
						case CommonValue.kWCMessageTypePlain:
							if (convertView.getTag() instanceof ViewHolderRightText) {
								holderRightText = (ViewHolderRightText) convertView.getTag();
								displayRightText(msg, holderRightText, position);
							}
							else {
								holderRightText = new ViewHolderRightText();
								convertView = inflater.inflate(R.layout.chat_right_text, null);
								holderRightText.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
								holderRightText.rightAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_r);
								holderRightText.rightNickname = (TextView) convertView.findViewById(R.id.textview_name_r);
								holderRightText.rightText = (TextView) convertView.findViewById(R.id.textview_content_r);
								displayRightText(msg, holderRightText, position);
								convertView.setTag(holderRightText);
							}
							break;
						case CommonValue.kWCMessageTypeImage:
							if (convertView.getTag() instanceof ViewHolderRightImage) {
								holderRightImg = (ViewHolderRightImage) convertView.getTag();
								displayRightImage(message, msg, holderRightImg, position);
							}
							else {
								holderRightImg = new ViewHolderRightImage();
								convertView = inflater.inflate(R.layout.chat_right_image, null);
								holderRightImg.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
								holderRightImg.rightAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_r);
								holderRightImg.rightNickname = (TextView) convertView.findViewById(R.id.textview_name_r);
								holderRightImg.rightPhoto = (ImageView) convertView.findViewById(R.id.photo_content_r);
								holderRightImg.photoProgress = (TextView) convertView.findViewById(R.id.photo_content_progress);
								holderRightImg.rightProgress = (ProgressBar) convertView.findViewById(R.id.view_progress_r);
								displayRightImage(message, msg, holderRightImg, position);
								convertView.setTag(holderRightImg);
							}
							break;
						case CommonValue.kWCMessageTypeVoice:
							if (convertView.getTag() instanceof ViewHolderRightVoice) {
								holderRightVoice = (ViewHolderRightVoice) convertView.getTag();
								displayRightVoice(message, msg, holderRightVoice, position);
							}
							else {
								holderRightVoice = new ViewHolderRightVoice();
								convertView = inflater.inflate(R.layout.chat_right_voice, null);
								holderRightVoice.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
								holderRightVoice.rightAvatar = (ImageView) convertView.findViewById(R.id.image_portrait_r);
								holderRightVoice.rightNickname = (TextView) convertView.findViewById(R.id.textview_name_r);
								holderRightVoice.rightVoice = (ImageView) convertView.findViewById(R.id.senderVoiceNode);
								holderRightVoice.rightProgress = (ProgressBar) convertView.findViewById(R.id.view_progress_r);
								displayRightVoice(message, msg, holderRightVoice, position);
								convertView.setTag(holderRightVoice);
							}
							break;
						}
					}
				}
			}
			catch (Exception e) {
				Logger.i(e);
			}
			return convertView;
		}
		
		private void displayLeftText(JsonMessage msg, ViewHolderLeftText viewHolderLeftText, int position) {
			imageLoader.displayImage(user.txPath.smallPicture, viewHolderLeftText.leftAvatar, options);
			viewHolderLeftText.leftText.setText(msg.text);
			displayTime(position, viewHolderLeftText.timeTV);
			viewHolderLeftText.leftAvatar.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					goTocontactMainPage(user);
				}
				
			});
		}
		
		private void displayLeftImage(final JsonMessage msg, ViewHolderLeftImage viewHolderLeftImage, int position) {
			imageLoader.displayImage(user.txPath.smallPicture, viewHolderLeftImage.leftAvatar, options);
			imageLoader.displayImage(msg.file, viewHolderLeftImage.leftPhoto, photooptions);
			displayTime(position, viewHolderLeftImage.timeTV);
			viewHolderLeftImage.leftAvatar.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					goTocontactMainPage(user);
				}
				
			});
			
			viewHolderLeftImage.leftPhoto.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String[]urls=new String[]{msg.file};
					
					Intent intent = new Intent(context, ImagePagerActivity.class);
					// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
					intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
					intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
					context.startActivity(intent);
				}
				
			});
		}
		
		private void displayLeftVoice(JsonMessage msg, ViewHolderLeftVoice viewHolderLeftVoice, int position) {
			viewHolderLeftVoice.leftVoice.setTag(msg.file);
			imageLoader.displayImage( user.txPath.smallPicture, viewHolderLeftVoice.leftAvatar, options);
			displayTime(position, viewHolderLeftVoice.timeTV);
			viewHolderLeftVoice.leftAvatar.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					goTocontactMainPage(user);
				}
				
			});
		}
		
		private void displayRightText(JsonMessage msg, ViewHolderRightText viewHolderRightText, int position) {
			imageLoader.displayImage( appContext.getLoginUserHead(), viewHolderRightText.rightAvatar, options);
			viewHolderRightText.rightText.setText(msg.text);
			displayTime(position, viewHolderRightText.timeTV);
			
			viewHolderRightText.rightAvatar.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i=new Intent(Chating.this,MeActivity.class);
					startActivity(i);
				}	
			});
		}
		
		private void displayRightImage(IMMessage message, final JsonMessage msg, ViewHolderRightImage viewHolderRightImage, int position) {
			imageLoader.displayImage(appContext.getLoginUserHead(), viewHolderRightImage.rightAvatar, options);
			imageLoader.displayImage(msg.file, viewHolderRightImage.rightPhoto, photooptions);
			if (message.getType() == CommonValue.kWCMessageStatusWait) {
				message.setType(CommonValue.kWCMessageStatusSending);
				viewHolderRightImage.photoProgress.setVisibility(View.VISIBLE);
				imageLoader.displayImage("file:///"+msg.file, viewHolderRightImage.rightPhoto, photooptions);
				uploadImageToQiniu(message, msg.file, viewHolderRightImage, CommonValue.kWCMessageTypeImage);
			}
			else if (message.getType() == 0) {
				viewHolderRightImage.photoProgress.setVisibility(View.GONE);
			}
			displayTime(position, viewHolderRightImage.timeTV);
			
			viewHolderRightImage.rightAvatar.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i=new Intent(Chating.this,MeActivity.class);
					startActivity(i);
				}	
			});
			
			 viewHolderRightImage.rightPhoto.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String[]urls=new String[]{msg.file};
					
					Intent intent = new Intent(context, ImagePagerActivity.class);
					// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
					intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
					intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
					context.startActivity(intent);
				}
				
			});
		}
		
		private void displayRightVoice(IMMessage message, JsonMessage msg, ViewHolderRightVoice viewHolderRightVoice, int position) {
			viewHolderRightVoice.rightVoice.setTag(msg.file);
			imageLoader.displayImage( appContext.getLoginUserHead(), viewHolderRightVoice.rightAvatar, options);
			if (message.getType() == CommonValue.kWCMessageStatusWait) {
				message.setType(CommonValue.kWCMessageStatusSending);
				viewHolderRightVoice.rightProgress.setVisibility(View.VISIBLE);
				uploadVoiceToQiniu(message, msg.file, viewHolderRightVoice, CommonValue.kWCMessageTypeVoice);
			}
			else if (message.getType() == 0) {
				viewHolderRightVoice.rightProgress.setVisibility(View.GONE);
			}
			displayTime(position, viewHolderRightVoice.timeTV);
			
			viewHolderRightVoice.rightAvatar.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i=new Intent(Chating.this,MeActivity.class);
					startActivity(i);
				}	
			});
		}
		
		private void displayTime(int position, TextView timeTV) {
			String currentTime = items.get(position).getTime();
			String previewTime = (position - 1) >= 0 ? items.get(position-1).getTime() : "0";
			try {
				long time1 = Long.valueOf(currentTime);
				long time2 = Long.valueOf(previewTime);
				if ((time1-time2) >= 5 * 60 ) {
					timeTV.setVisibility(View.VISIBLE);
					timeTV.setText(DateUtil.wechat_time(currentTime));
				}
				else {
					timeTV.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				Logger.i(e);
			}
		}
		
		private void uploadImageToQiniu(final IMMessage message, String filePath, final ViewHolderRightImage cell, final int messageType) {
			ApiClient.uploadAnImage(context,appContext.getLoginApiKey(), filePath, new  ClientCallback(){

				@Override
				public void onSuccess(Object data) {
					// TODO Auto-generated method stub
					try {
						JsonMessage msg = new JsonMessage();
						msg.file = (String)data;
						Logger.i(msg.file);
						switch (messageType) {
						case CommonValue.kWCMessageTypeImage:
							msg.messageType = CommonValue.kWCMessageTypeImage;
							msg.text = "[图片]";
							break;
	
						case CommonValue.kWCMessageTypeVoice:
							msg.messageType = CommonValue.kWCMessageTypeVoice;
							msg.text = "[语音]";
							break;
						}
						Gson gson = new Gson();
						String json = gson.toJson(msg);
						message.setContent(json);
						sendMediaMessage(message);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
					
				}});
		}
		
		private void uploadVoiceToQiniu(final IMMessage message, String filePath, final ViewHolderRightVoice cell, final int messageType) {
			
				ApiClient.uploadAVoice(context, appContext.getLoginApiKey(), filePath, new ClientCallback(){
					
					@Override
					public void onSuccess(Object data) {
						// TODO Auto-generated method stub
						try {
							JsonMessage msg = new JsonMessage();
							msg.file = (String)data;
							Logger.i(msg.file);
							switch (messageType) {
							case CommonValue.kWCMessageTypeImage:
								msg.messageType = CommonValue.kWCMessageTypeImage;
								msg.text = "[图片]";
								break;

							case CommonValue.kWCMessageTypeVoice:
								msg.messageType = CommonValue.kWCMessageTypeVoice;
								msg.text = "[语音]";
								break;
							}
							Gson gson = new Gson();
							String json = gson.toJson(msg);
							message.setContent(json);
							sendMediaMessage(message);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
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
						
					}});
				
		}
	}
	
	@Override
	public void onBackPressed() {
		NoticeManager.getInstance(context).updateStatusByFrom(to, Notice.READ);
		super.onBackPressed();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (view.getId()) {
		case R.id.voiceButton:
			voiceTouch(event);
			break;

		default:
			break;
		}
		return false;
	}
	
	private double voiceValue;
	private Dialog voiceDialog;
	private ImageView voiceImage;
	private static int MIN_TIME = 1;
	private static float recodeTime = 0.0f;
	private Thread recordThread;
	private boolean isRecording = false;
	void showVoiceDialog(){
		voiceDialog = new Dialog(this,R.style.VoiceDialogStyle);
		voiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		voiceDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		voiceDialog.setContentView(R.layout.voice_dialog);
		voiceImage = (ImageView)voiceDialog.findViewById(R.id.dialog_img);
		voiceDialog.show();
	}
	void mythread(){
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}
	void setDialogImage(){
		if (voiceValue < 200.0) {
			voiceImage.setImageResource(R.drawable.record_animate_01);
		}else if (voiceValue > 200.0 && voiceValue < 400) {
			voiceImage.setImageResource(R.drawable.record_animate_02);
		}else if (voiceValue > 400.0 && voiceValue < 800) {
			voiceImage.setImageResource(R.drawable.record_animate_03);
		}else if (voiceValue > 800.0 && voiceValue < 1600) {
			voiceImage.setImageResource(R.drawable.record_animate_04);
		}else if (voiceValue > 1600.0 && voiceValue < 3200) {
			voiceImage.setImageResource(R.drawable.record_animate_05);
		}else if (voiceValue > 3200.0 && voiceValue < 5000) {
			voiceImage.setImageResource(R.drawable.record_animate_06);
		}else if (voiceValue > 5000.0 && voiceValue < 7000) {
			voiceImage.setImageResource(R.drawable.record_animate_07);
		}else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
			voiceImage.setImageResource(R.drawable.record_animate_08);
		}else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
			voiceImage.setImageResource(R.drawable.record_animate_09);
		}else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
			voiceImage.setImageResource(R.drawable.record_animate_10);
		}else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
			voiceImage.setImageResource(R.drawable.record_animate_11);
		}else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
			voiceImage.setImageResource(R.drawable.record_animate_12);
		}else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
			voiceImage.setImageResource(R.drawable.record_animate_13);
		}else if (voiceValue > 28000.0) {
			voiceImage.setImageResource(R.drawable.record_animate_14);
		}
	}
	private void voiceTouch(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			try {
				AudioPlayManager.getInstance(this, this).stopPlay();
				isRecording = true;
				showVoiceDialog();
				AudioRecoderManager.getInstance(this).start();
				mythread();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			try {
				isRecording = false;
				if (voiceDialog.isShowing()) {
					voiceDialog.dismiss();
				}
				String voicePath = AudioRecoderManager.getInstance(this).stop();
				voiceValue = 0.0;
				if (recodeTime < MIN_TIME) {
					
				}
				else {
					uploadVoiceToQiniu(voicePath);
				}
			} catch (IOException e) {
					e.printStackTrace();
			}
			AudioRecoderManager.destroy();
			break;
		}
	}
	private Runnable ImgThread = new Runnable() {
		@Override
		public void run() {
			recodeTime = 0.0f;
			while (isRecording) {
				try {
					Thread.sleep(200);
					recodeTime += 0.2;
					voiceValue = AudioRecoderManager.getInstance(context).getAmplitude();
					imgHandle.sendEmptyMessage(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		Handler imgHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					setDialogImage();
					break;
				default:
					break;
				}
				
			}
		};
	};
	
	protected void onDestroy() {
		AudioPlayManager.getInstance(this, null).stopPlay();
		AudioRecoderManager.destroy();
		AudioPlayManager.destroy();
		super.onDestroy();
	};
	
	
	private String currentVoice = "";
	private View currentConvertView;

	@Override
	public void onItemClick(AdapterView<?> arg0, View convertView, int position, long arg3) {
		if (convertView.getTag() instanceof Chating.MessageListAdapter.ViewHolderLeftVoice) {
			Chating.MessageListAdapter.ViewHolderLeftVoice viewHolderLeftVoice = (Chating.MessageListAdapter.ViewHolderLeftVoice) convertView.getTag();
			String voice = (String) viewHolderLeftVoice.leftVoice.getTag();
			if (currentVoice.equals(voice)) {
				AudioPlayManager audioPlayManager = AudioPlayManager.getInstance(this, this);
				audioPlayManager.setConvertView(convertView);
				audioPlayManager.setURL(voice);
				audioPlayManager.startStopPlay();
			}
			else {
				if (currentConvertView != null) {
					this.playStoped(currentConvertView);
				}
				AudioPlayManager audioPlayManager = AudioPlayManager.getInstance(this, this);
				audioPlayManager.stopPlay();
				audioPlayManager.setConvertView(convertView);
				audioPlayManager.setURL(voice);
				audioPlayManager.startStopPlay();
			}
			currentVoice = voice;
			currentConvertView = convertView;
		}
		else if (convertView.getTag() instanceof Chating.MessageListAdapter.ViewHolderRightVoice) {
			Chating.MessageListAdapter.ViewHolderRightVoice viewHolderRightVoice = (Chating.MessageListAdapter.ViewHolderRightVoice) convertView.getTag();
			String voice = (String) viewHolderRightVoice.rightVoice.getTag();
			if (currentVoice.equals(voice)) {
				AudioPlayManager audioPlayManager = AudioPlayManager.getInstance(this, this);
				audioPlayManager.setConvertView(convertView);
				audioPlayManager.setURL(voice);
				audioPlayManager.startStopPlay();
			}
			else {
				if (currentConvertView != null) {
					this.playStoped(currentConvertView);
				}
				AudioPlayManager audioPlayManager = AudioPlayManager.getInstance(this, this);
				audioPlayManager.stopPlay();
				audioPlayManager.setConvertView(convertView);
				audioPlayManager.setURL(voice);
				audioPlayManager.startStopPlay();
			}
			currentVoice = voice;
			currentConvertView = convertView;
		}
		else if (convertView.getTag() instanceof Chating.MessageListAdapter.ViewHolderLeftImage) {
			
		}
		else if (convertView.getTag() instanceof Chating.MessageListAdapter.ViewHolderRightImage) {
			
		}
	}

	@Override
	public void playFail(View messageBubble) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playStoped(View messageBubble) {
		if (messageBubble.getTag() instanceof Chating.MessageListAdapter.ViewHolderLeftVoice) {
			Chating.MessageListAdapter.ViewHolderLeftVoice viewHolderLeftVoice = (Chating.MessageListAdapter.ViewHolderLeftVoice) messageBubble.getTag();
			if (leftAnimationDrawable != null && leftAnimationDrawable.isRunning()) {
				leftAnimationDrawable.stop();
			}
			viewHolderLeftVoice.leftVoice.setImageResource(R.drawable.chatfrom_voice_playing);
		}
		else if (messageBubble.getTag() instanceof Chating.MessageListAdapter.ViewHolderRightVoice) {
			Chating.MessageListAdapter.ViewHolderRightVoice viewHolderRightVoice = (Chating.MessageListAdapter.ViewHolderRightVoice) messageBubble.getTag();
			if (rightAnimationDrawable != null && rightAnimationDrawable.isRunning()) {
				rightAnimationDrawable.stop();
			}
			viewHolderRightVoice.rightVoice.setImageResource(R.drawable.chatto_voice_playing_f3);
		}
	}

	@Override
	public void playStart(View messageBubble) {
		if (messageBubble.getTag() instanceof Chating.MessageListAdapter.ViewHolderLeftVoice) {
			Chating.MessageListAdapter.ViewHolderLeftVoice viewHolderLeftVoice = (Chating.MessageListAdapter.ViewHolderLeftVoice) messageBubble.getTag();
			viewHolderLeftVoice.leftVoice.setImageResource(R.anim.receiver_voice_node_playing);
			leftAnimationDrawable = (AnimationDrawable) viewHolderLeftVoice.leftVoice.getDrawable();
			leftAnimationDrawable.start();
		}
		else if (messageBubble.getTag() instanceof Chating.MessageListAdapter.ViewHolderRightVoice) {
			Chating.MessageListAdapter.ViewHolderRightVoice viewHolderRightVoice = (Chating.MessageListAdapter.ViewHolderRightVoice) messageBubble.getTag();
			viewHolderRightVoice.rightVoice.setImageResource(R.anim.sender_voice_node_playing);
			rightAnimationDrawable = (AnimationDrawable) viewHolderRightVoice.rightVoice.getDrawable();
			rightAnimationDrawable.start();
		}
	}

	@Override
	public void playCompletion(View messageBubble) {
		
		if (messageBubble.getTag() instanceof Chating.MessageListAdapter.ViewHolderLeftVoice) {
			Chating.MessageListAdapter.ViewHolderLeftVoice viewHolderLeftVoice = (Chating.MessageListAdapter.ViewHolderLeftVoice) messageBubble.getTag();
			if (leftAnimationDrawable != null && leftAnimationDrawable.isRunning()) {
				leftAnimationDrawable.stop();
			}
			viewHolderLeftVoice.leftVoice.setImageResource(R.drawable.chatfrom_voice_playing);
		}
		else if (messageBubble.getTag() instanceof Chating.MessageListAdapter.ViewHolderRightVoice) {
			Chating.MessageListAdapter.ViewHolderRightVoice viewHolderRightVoice = (Chating.MessageListAdapter.ViewHolderRightVoice) messageBubble.getTag();
			if (rightAnimationDrawable != null && rightAnimationDrawable.isRunning()) {
				rightAnimationDrawable.stop();
			}
			viewHolderRightVoice.rightVoice.setImageResource(R.drawable.chatto_voice_playing_f3);
		}
	}
	
	@Override
	public void playDownload(View messageBubble) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onEditorAction(TextView view, int actionId, KeyEvent arg2) {
		switch (actionId) {
		case EditorInfo.IME_ACTION_SEND:
			String message = messageInput.getText().toString();
			if ("".equals(message)) {
				Toast.makeText(Chating.this, "不能为空",
						Toast.LENGTH_SHORT).show();
			} else {

				try {
					sendMessage(message);
					messageInput.setText("");
				} catch (Exception e) {
					showToast("信息发送失败");
					messageInput.setText(message);
				}
				closeInput();
			}
			listView.setSelection(getMessages().size()-1);
			break;

		default:
			break;
		}
		return true;
	}
}
