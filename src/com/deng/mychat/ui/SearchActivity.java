package com.deng.mychat.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import tools.StringUtils;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deng.mychat.R;
import com.deng.mychat.adapter.MyNewsAdapter.MyHolder;
import com.deng.mychat.bean.News;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.view.CustomProgressDialog;
import com.deng.mychat.view.MyGridView;
import com.deng.mychat.view.MyListView;

public class SearchActivity extends AppActivity{
	private CustomProgressDialog loadingPd;
	
	private ImageView clearBtn;
	private EditText edInput;
	
	private RelativeLayout searchBtn;
	private TextView searchMobile;
	private LinearLayout sugestionList;
	private MyListView mList;
	private List<Contact> mobileContactList=new ArrayList<Contact>();
	private List<Contact>allMobileContacts=new ArrayList<Contact>();
	private Cursor cusor;
	private MobileContactAdapter adapter;
	private String commSubstring="";
	
	private Handler mHandler=new Handler(){
		@Override
    	public void handleMessage(Message m)
    	{
			adapter.notifyDataSetChanged();
			if(mobileContactList.size()>0)sugestionList.setVisibility(View.VISIBLE);
			else sugestionList.setVisibility(View.GONE);
    	}
	};
	
	 private static final String[] PHONES_PROJECTION = new String[] {
		    Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
	   
	    /**联系人显示名称**/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;
	    
	    /**电话号码**/
	private static final int PHONES_NUMBER_INDEX = 1;
	    
	    /**头像ID**/
	private static final int PHONES_PHOTO_ID_INDEX = 2;
	   
	    /**联系人的ID**/
	private static final int PHONES_CONTACT_ID_INDEX = 3;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);	
		initUI();	
	}
	
	public void initUI()
	{
		clearBtn=(ImageView)findViewById(R.id.iv_clear);
		edInput=(EditText)findViewById(R.id.et_input);
		
		
		searchBtn=(RelativeLayout)findViewById(R.id.searchBtn);
		searchMobile=(TextView)findViewById(R.id.searchMobile);
		sugestionList=(LinearLayout)findViewById(R.id.sugestionList);;
		mList=(MyListView)findViewById(R.id.mList);
		adapter=new MobileContactAdapter();
		mList.setAdapter(adapter);
		mList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				 startSearch(mobileContactList.get(arg2).mobile);
			}
			
		});		
		ContentResolver resolver = getContentResolver();
		// 获取手机联系人
		cusor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);
		getAllContact();
		
		edInput.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
				commSubstring=""+arg0;
				
				if(arg0.length()>0){
					clearBtn.setVisibility(View.VISIBLE);
					searchBtn.setVisibility(View.VISIBLE);
					searchMobile.setText(arg0);
					searchContact(""+arg0);
					}
				else 
					{
					clearBtn.setVisibility(View.GONE);
					searchBtn.setVisibility(View.GONE);
					sugestionList.setVisibility(View.GONE);
				}
			}

			
			
		});
	}
	
	public void onPause()
	{
		super.onPause();
		if(cusor!=null)cusor.close();
	}
	
	private void searchContact(final String commStr) {
				// TODO Auto-generated method stub
		new Thread(){
			public void run()
			{
				mobileContactList.clear();
				//sugestionList.setVisibility(View.GONE);
				for(Contact c:allMobileContacts)
				{
					if(c.mobile.startsWith(commStr))
					{
						mobileContactList.add(c);					
					}
				}
				
				mHandler.sendEmptyMessage(1);
			}
		}.start();
		

	}
	
	protected void getAllContact() {
		// TODO Auto-generated method stub	
		new Thread()
		{
			public void run()
			{
				while (cusor.moveToNext()) {

				Contact c=new Contact();
				//得到手机号码
				c.mobile = cusor.getString(PHONES_NUMBER_INDEX);
				//当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(c.mobile))
				    continue;
				
				if(c.mobile.startsWith("+86"))
					c.mobile=c.mobile.replaceFirst("\\+86", "");
				
				c.mobile=c.mobile.replaceAll(" +", "");
				//得到联系人名称
				c.name = cusor.getString(PHONES_DISPLAY_NAME_INDEX);
				allMobileContacts.add(c);		
				}
		 }
		}.start();
		
	}

	public void buttonClick(View v)
	{
		switch(v.getId())
		{
			case R.id.cancelBtn:finish();break;
			case R.id.iv_clear:edInput.setText("");
			case R.id.searchBtn:startSearch(edInput.getText().toString());
		}
	}

	private void startSearch(String string) {
		// TODO Auto-generated method stub
		if(StringUtils.empty(string))return;
		loadingPd = CustomProgressDialog.show(this, "正在查联系人...", false, null);
		ApiClient.getContactByPhone(appContext.getLoginApiKey(), string, 1, 1, new ClientCallback(){

			@Override
			public void onSuccess(Object data) {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
				UserInfo u=(UserInfo)data;
				Intent intent=new Intent(context,ContactMainPageActivity.class);
				intent.putExtra("isMyFriend", u.isMyFriend);
				intent.putExtra("user", u);
				context.startActivity(intent);
			}

			@Override
			public void onFailure(String message) {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
				showToast("查找失败,该用户不存在");
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
				showToast("查找失败");
			}

			@Override
			public void onRequestLogIn() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	private  class MobileContactAdapter extends BaseAdapter{

		private LayoutInflater listContainer;
		
		public MobileContactAdapter()
		{
			listContainer = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mobileContactList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mobileContactList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View cv, ViewGroup arg2) {
			// TODO Auto-generated method stub
			 Contact contact= mobileContactList.get(position);
		   	 ViewHolder holder;

		   	 if(cv==null)
		   	 {
		   		holder=new ViewHolder();
		   		cv=listContainer.inflate(R.layout.mobile_contact_item, null);
		   		
		    	holder.headIcon=(ImageView)cv.findViewById(R.id.userIcon);
		    	holder.name=(TextView)cv.findViewById(R.id.name);
		    	holder.mobile=(TextView)cv.findViewById(R.id.mobile);
		    	cv.setTag(holder);   
		     }else {   
		         holder = (ViewHolder)cv.getTag();   
		     }  
		   	 
		   	imageLoader.displayImage(contact.headPath, holder.headIcon, CommonValue.DisplayOptions.touxiang_options);
		   	holder.name.setText(contact.name); 
		   	if(contact.mobile.startsWith(commSubstring))
		   	{
		   		int i=commSubstring.length();
		   		CharSequence c=Html.fromHtml("手机号码:"+"<font color=#008000> "+commSubstring+"</font>"+contact.mobile.substring(i));
		   		
		   		holder.mobile.setText(c);
		   	}
		   	else  
		   	holder.mobile.setText("手机号码:"+contact.mobile);
			return cv;
		}
		
		private class ViewHolder{
			public ImageView headIcon;
			public TextView name;
			public TextView mobile;
		}
		
	}
	
 private  class Contact{
	 public String name="";
	 public String mobile="13545236532";
	 public String headPath="";
 }
}
