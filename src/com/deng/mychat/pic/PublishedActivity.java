package com.deng.mychat.pic;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import tools.ImageUtils;
import tools.StringUtils;
import tools.UIHelper;

import com.deng.mychat.R;
import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.tools.AppManager;
import com.deng.mychat.view.CustomProgressDialog;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PublishedActivity extends AppActivity {

	private GridView noScrollgridview;
	private GridAdapter adapter;
	private EditText contentTextEt;
	private CustomProgressDialog loadingPd;
	private InputMethodManager imm;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish);
		Init();
	}

	public void Init() {
		contentTextEt=(EditText)findViewById(R.id.contentTextEt);
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(contentTextEt.getWindowToken(), 0);
		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		
		float d=getResources().getDisplayMetrics().density;
		int width=getResources().getDisplayMetrics().widthPixels;
		
		noScrollgridview.setNumColumns((int) (width/(66*d)));
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				imm.hideSoftInputFromWindow(contentTextEt.getWindowToken(), 0);
				if (arg2 == Bimp.bmp.size()) {
					new PopupWindows(PublishedActivity.this, noScrollgridview);
				} else {
					Intent intent = new Intent(PublishedActivity.this,
							PhotoActivity.class);
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});
	}


	
	public void buttonClick(View v)
	{
		switch(v.getId())
		{
		case R.id.cancel:finish();break;
		case R.id.send:
			List<String> list = new ArrayList<String>();				
			for (int i = 0; i < Bimp.drr.size(); i++) {
				String Str = Bimp.drr.get(i);//.substring( 
				list.add(Str);	
				Log.d("Pub",Str);
			}
			
			publishNews(contentTextEt.getText().toString(),list);
			
			break;
		}
	}
	
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位�?
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			return (Bimp.bmp.size() + 1);
		}

		public Object getItem(int arg0) {

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			final int coord = position;
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();	
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);	
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.bmp.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 15) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.bmp.get(position));
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.drr.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else if(Bimp.drr.size()>=2){
							try {
								String path = Bimp.drr.get(Bimp.max);
								Bitmap bm = Bimp.revitionImageSize(path);
								Bimp.bmp.add(bm);
								String newStr = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
								FileUtils.saveBitmap(bm, "" + newStr);
								Bimp.max += 1;
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

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
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(PublishedActivity.this,
							TestPicActivity.class);
					startActivity(intent);
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

	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";

	public void photo() {

		
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
		path = savePath + fileName;//该照片的绝对路径
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, TAKE_PICTURE);
		////dismiss();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.drr.size() < 15 && resultCode == -1) {
				Bimp.drr.add(path);
				//showToast("a pic at :"+path);
			}
			break;
		}
	}

	public void publishNews(String contentText,List<String> files)
	{
		if(StringUtils.empty(contentText.trim()) && (files==null || files.size()==0))
		{
			showToast("请输入文字或者选择照片");
			return;
		}
		
		String apiKey = appContext.getLoginApiKey();
		
		LocationPoint location=null;
		location=appContext.getLoginInfo().userInfo.location;
		
		
		if(location==null)location=new LocationPoint(30.0,114.001);
		
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("type","location");
		map.put("location",appContext.getNickname()+"location:"+location); 
		MobclickAgent.onEvent(context, "publish_news", map); 

		loadingPd = CustomProgressDialog.show(this, "正在发布..", false, null);//UIHelper.showProgress(this, null, "正在登录", true);
				
		ApiClient.publishNews(apiKey,contentText, 
					files,"",0,"","",location.longitude,location.latitude,
					new ClientCallback(){
						@Override
						public void onSuccess(Object data) {
							loadingPd.dismiss();
							showToast("发布成功");
							FileUtils.deleteDir();
							adapter.notifyDataSetChanged();
							setResult(RESULT_OK);
							finish();
							
							notifyNewsList();
							
							Bimp.bmp.clear();
							Bimp.drr.clear();
						}

						@Override
						public void onFailure(String message) {
							showToast( message);
							loadingPd.dismiss();
						}

						@Override
						public void onError(Exception e) {
							// TODO Auto-generated method stub
							showToast("服务器连接故障");
							loadingPd.dismiss();
						}

						@Override
						public void onRequestLogIn() {
							// TODO Auto-generated method stub
							
						}		 
		 });
	}
	
	 public void notifyNewsList()
	   {
	    Intent i = new Intent(CommonValue.ACTION_REFLESH);
		 sendBroadcast(i);//传递过去	
	   }
	
}
