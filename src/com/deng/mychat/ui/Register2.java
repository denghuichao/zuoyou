package com.deng.mychat.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import tools.AppManager;
import tools.ImageUtils;
import tools.StringUtils;
import tools.UIHelper;

import com.deng.mychat.R;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.pic.FileUtils;
import com.deng.mychat.pic.PublishedActivity;
import com.deng.mychat.pic.TestPicActivity;
import com.deng.mychat.pic.PublishedActivity.PopupWindows;
import com.deng.mychat.view.CustomProgressDialog;
import com.deng.mychat.view.CustomerDatePickerDialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 注册的第二阶段
 */
 
public class Register2 extends AppActivity/* implements View.OnTouchListener*/{
	
	private static final int BIRTH_DATE_DIALOG_ID=1;
	private static final int SCHOOL_DATE_DIALOG_ID=2;
	private static final int SELECT_SCHOOL_REQUEST=3;
	private static final int SELECT_LABEL_REQUEST=4;
	private static final int REQUEST_SELECT_PIC=5;
	private static final int REQUEST_GETIMAGE_BYCAMERA=6;
	private static final int REQUEST_CUT_PIC=7;
	
	private int birthYear;
	private int birthMonth;
	private int birthDay;
	
	private int schoolYear;
	private int schoolMonth;
	private int schoolDay;
	
	private EditText editTextName;
	private EditText schooldayEditText;
	private EditText birthdayEditText;
	private EditText schoolNameEditText;
	private Spinner selectSex;

	private ImageView userIcon;
	//private ImageView addPicBtn;
	
	private Button selectLabelBtn;
	
	private StringBuilder interestLabels=new StringBuilder();
	private StringBuilder identifyLabels=new StringBuilder();
	
	String apiKey;
	
	private CustomProgressDialog loadingPd;
	
	private boolean firstLog=false;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.register2, null);
		setContentView(view);
		if(appContext.isLogin())apiKey=appContext.getLoginApiKey();
		else apiKey=getIntent().getStringExtra("apiKey");
		
		//Log.d("MAIN", apiKey);
		
		initUi();
		loadData();
			
	}
	
	private void initUi()
	{
		editTextName=(EditText)findViewById(R.id.editTextName);
		
		schooldayEditText=(EditText)findViewById(R.id.schoolday);
		birthdayEditText=(EditText)findViewById(R.id.birthday);
		schoolNameEditText=(EditText)findViewById(R.id.schoolName);
		
		selectSex=(Spinner)findViewById(R.id.selectSex);
		selectLabelBtn=(Button)findViewById(R.id.selectLabelBtn);
		schooldayEditText.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {  
		    @Override  
		    public void onFocusChange(View v, boolean hasFocus) {  
		        if(hasFocus) {
		  		  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
		  		  schooldayEditText.setCursorVisible(false);//失去光标
		          imm.hideSoftInputFromWindow(schooldayEditText.getWindowToken(), 0);
		          showDialog(SCHOOL_DATE_DIALOG_ID);
		        }
		    }
		});
		
		birthdayEditText.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {  
		    @Override  
		    public void onFocusChange(View v, boolean hasFocus) {  
		        if(hasFocus) {
			  		  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
			  		  birthdayEditText.setCursorVisible(false);//失去光标
			          imm.hideSoftInputFromWindow(birthdayEditText.getWindowToken(), 0);
			          showDialog(BIRTH_DATE_DIALOG_ID);
			         
		        }
		    }    
		    
		});
		
		schoolNameEditText.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {  
		    @Override  
		    public void onFocusChange(View v, boolean hasFocus) {  
		        if(hasFocus) {
			  		  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
			  		  birthdayEditText.setCursorVisible(false);//失去光标
			          imm.hideSoftInputFromWindow(schoolNameEditText.getWindowToken(), 0);
			          selectSchool();	         
		        }
		    }
		});
		
		userIcon=(ImageView)findViewById(R.id.setUserLogo);
		
		
		if(appContext.isLogin())
		{
			String headPath=appContext.getLoginUserHead();
			if(!StringUtils.empty(headPath) && !headPath.equals("defaultPath"))
				imageLoader.displayImage(headPath,userIcon, CommonValue.DisplayOptions.touxiang_options);
			
			//if(!StringUtils.empty(headPath) && !headPath.equals("defaultPath"))addPicBtn.setVisibility(View.GONE);
			UserInfo user=appContext.getLoginInfo().userInfo;
			String name =user.userName;
			if(!StringUtils.empty(name));
				editTextName.setText(name);
			
			String schoolYearStr=user.schoolDay;
			if((!StringUtils.empty(schoolYearStr))&&
					schoolYearStr.matches("\\d\\d\\d\\d"))
			{
				schoolYearStr=user.schoolDay+"年";
				schooldayEditText.setText("入学年份:"+schoolYearStr);
			}
			
			String birthStr=user.birthDay;
			if((!StringUtils.empty(birthStr)) && 
					birthStr.matches("\\d\\d\\d\\d/\\d\\d/\\d\\d \\d\\d:\\d\\d:\\d\\d"))
			{
				try {
					SimpleDateFormat sf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					SimpleDateFormat sft=new SimpleDateFormat("yyyy年MM月dd日");
				
					birthStr=sft.format(sf.parse(birthStr));
					birthdayEditText.setText("生日:"+birthStr);//)=(EditText)findViewById(R.id.birthday);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			if(!StringUtils.empty(user.schoolName))
			schoolNameEditText.setText(user.schoolName);
			
			int sex=0;
			if(user.sex==1)
				sex=1;
			else if(user.sex==2)
				sex=2;
			selectSex.setSelection(sex);//=(Spinner)findViewById(R.id.selectSex);
			
			//if(user.label!=null && user.label.size()>0 && !StringUtils.empty(user.label.get(0)))
			//	selectLabelBtn.setEnabled(false);
			
		}
	}
	
	
	
	public void loadData()
	{
		 Calendar mycalendar=Calendar.getInstance(Locale.CHINA);
	     Date mydate=new Date(); //获取当前日期Date对象
	     mycalendar.setTime(mydate);////为Calendar对象设置时间为当前日期
	      	
	    firstLog=this.getIntent().getBooleanExtra("first_log",false);
	    
		birthYear=1995;//mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
		birthMonth=mycalendar.get(Calendar.MONTH)+1;//获取Calendar对象中的月
		birthDay=mycalendar.get(Calendar.DAY_OF_MONTH);
		
		schoolYear=mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
		schoolMonth=mycalendar.get(Calendar.MONTH)+1;//获取Calendar对象中的月
		schoolDay=mycalendar.get(Calendar.DAY_OF_MONTH);
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
					startActivityForResult(intent, REQUEST_GETIMAGE_BYCAMERA);
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					//Intent intent = new Intent(PublishedActivity.this,
					///		TestPicActivity.class);
					//startActivity(intent);
					//dismiss();
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					startActivityForResult(intent, REQUEST_SELECT_PIC);
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
	
	
	private String theLarge;
	
	public void setImage(View v)
	{
		new PopupWindows(Register2.this, (View) userIcon.getParent());
	}
	
	public void buttonClick(View v) {
		switch (v.getId()) {
			
		case R.id.finishButton:
		case R.id.finishBtn:
			try {
				saveUserInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;	
			
		case R.id.cancelButton:
			finishRegister();
			break;
		
		case R.id.selectLabelBtn:
			 openSelectLabelAcitity();
			break;
		}
	}
	
	public void openSelectLabelAcitity()
	{
		Intent intent=new Intent(this,SelectLabelAcitity.class);
		startActivityForResult(intent, SELECT_LABEL_REQUEST);
	}
	

	
	public void selectSchool()
	{
		startActivityForResult(new Intent(this,SelectSchoolActivity.class), SELECT_SCHOOL_REQUEST);    
	}
	
	 @Override  
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		 	if(resultCode!=RESULT_OK)return ;
		 	
		 	String newPhotoPath;
		    switch(requestCode)
		 	{
		 		case SELECT_SCHOOL_REQUEST:
		 				schoolNameEditText.setText(data.getStringExtra("result"));
		 				schoolNameEditText.clearFocus();
		 				break;	
		 		case SELECT_LABEL_REQUEST:
		 			UserInfo user=appContext.getLoginInfo().userInfo;
		 			if(user.label.size()>0&&!StringUtils.empty(user.label.get(0)))selectLabelBtn.setEnabled(false);
		 			break;
		 		case REQUEST_GETIMAGE_BYCAMERA:
		 			File temp = new File(theLarge);
					startPhotoZoom(Uri.fromFile(temp));
					break;
				case REQUEST_SELECT_PIC:
					startPhotoZoom(data.getData());
					break;
					
				case REQUEST_CUT_PIC:
					if(data != null){
						reflashUserHeadIcon(data);
					}
					break;
		 	}
		    
		    super.onActivityResult(requestCode, resultCode, data);
	    }  
	 
	 private void reflashUserHeadIcon(Intent data) {
		// TODO Auto-generated method stub
		 Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				//Environment.getExternalStorageDirectory().getAbsolutePath() + ImageUtils.DCIM
				String filePath=Environment.getExternalStorageDirectory().getAbsolutePath() + ImageUtils.DCIM+"/"+System.currentTimeMillis()+".jpg";
				File file=new File(filePath);//将要保存图片的路径
	            try {
	                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
	                    photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
	                    bos.flush();
	                    bos.close();
	                    uploadPhotoService(filePath);
	            } catch (IOException e) {
	                    e.printStackTrace();
	            }
			}
	}

	public void startPhotoZoom(Uri uri) {
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			//下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
			intent.putExtra("crop", "true");
			// aspectX aspectY 是宽高的比例
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			// outputX outputY 是裁剪图片宽高
			intent.putExtra("outputX", 150);
			intent.putExtra("outputY", 150);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, REQUEST_CUT_PIC);
		}

	private void uploadPhotoService(String file) {
		loadingPd = CustomProgressDialog.show(this, "保存头像..", false, null);//UIHelper.showProgress(this, null, "正在登录", true);
			imageLoader.displayImage("file:///"+file, userIcon, CommonValue.DisplayOptions.touxiang_options);
			//if(!StringUtils.empty(headPath))addPicBtn.setVisibility(View.GONE);
			ApiClient.uploadHead(context,apiKey, file,"image/jpeg", new ClientCallback() {
				@Override
				public void onSuccess(Object data) {
					loadingPd.dismiss();
					String head = (String) data;
					modify("", head, "", "", "", "", -100, "",false);
				}
				
				@Override
				public void onFailure(String message) {
					loadingPd.dismiss();
					showToast(message);
				}
				
				@Override
				public void onError(Exception e) {
					loadingPd.dismiss();
				}

				@Override
				public void onRequestLogIn() {
					// TODO Auto-generated method stub
					
				}
			});
			
		}
	 
	 
	
	
	@Override
	protected Dialog onCreateDialog(int id)
	{
	
	     Calendar mycalendar=Calendar.getInstance(Locale.CHINA);
	     Date mydate=new Date(); //获取当前日期Date对象
	     mycalendar.setTime(mydate);////为Calendar对象设置时间为当前日期
	        
	     int year=mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
	     int  month=mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
	     int day=mycalendar.get(Calendar.DAY_OF_MONTH);
	     
	     
		switch(id)
		{
			case BIRTH_DATE_DIALOG_ID:
				return new CustomerDatePickerDialog(this,birthDateSetListener,year-19,month,day,true);
				
			case SCHOOL_DATE_DIALOG_ID:
				return new CustomerDatePickerDialog(this,schoolDateSetListener,year-2,month,day,false);					
		}
		return null;
	}
	
	private DatePickerDialog.OnDateSetListener birthDateSetListener=new DatePickerDialog.OnDateSetListener()
	{

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			birthYear=year;
			birthMonth=monthOfYear+1;
			birthDay=dayOfMonth;
			
			birthdayEditText.setText("生日:"+birthYear+"年"+birthMonth+"月"+birthDay+"日");
			birthdayEditText.clearFocus();
		}
	};
	
	private DatePickerDialog.OnDateSetListener schoolDateSetListener=new DatePickerDialog.OnDateSetListener()
	{

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			schoolYear=year;
			schoolMonth=monthOfYear+1;
			schoolDay=dayOfMonth;
			
			schooldayEditText.setText("入学年份:"+schoolYear+"年"+schoolMonth+"月"+schoolDay+"日");
			schooldayEditText.clearFocus();
		}
	};
	
	public void saveUserInfo() throws Exception
	{
		String userName=editTextName.getText().toString();
		if(StringUtils.empty(userName)||userName.toLowerCase().equals("null"))
		{
			showToast("请输入有效的用户名");
			return ;
		}
		String birthDay=this.birthYear+"-"+this.birthMonth+"-"+this.birthDay;
		String schoolDay=this.schoolYear+"";//+"-"+this.schoolMonth+"-"+this.schoolDay;
		
		int sex=selectSex.getSelectedItemPosition();
		//0--未选择 1--男  2--女  3-其他
		String school=schoolNameEditText.getText().toString();	
		modify(userName, "", "",birthDay,
				 schoolDay,school,sex,identifyLabels.toString()+";"+interestLabels.toString(),true);
	}
	
	 private void modify(String userName, String head, String des,String birthDay,
			 String schoolDay,String schoolName,int sex,String labels,final boolean finish) {
			loadingPd = CustomProgressDialog.show(this, "正在保存..", false, null);//UIHelper.showProgress(this, null, "正在登录", true);
			ApiClient.modifiedUser(appContext, apiKey, userName, head, des,birthDay,schoolDay,
					schoolName,sex,labels,new ClientCallback() {
				
				@Override
				public void onSuccess(Object data) {
					loadingPd.dismiss();
					showToast("保存成功");
					if(finish)
						finishRegister();
				}
				
				
				@Override
				public void onFailure(String message) {
					loadingPd.dismiss();
					showToast(message);
				}
				
				@Override
				public void onError(Exception e) {
					loadingPd.dismiss();
					showToast("保存失败");
				}


				@Override
				public void onRequestLogIn() {
					// TODO Auto-generated method stub
					
				}
			});
		}
	 private void finishRegister() {
			if(firstLog)
			{
				Intent intent = new Intent(Register2.this, Login.class);
				startActivity(intent);
				AppManager.getAppManager().finishActivity(Register2.class);
			}
			else
			{
				//Intent intent = new Intent(Register2.this, Tabbar.class);
				//startActivity(intent);
				AppManager.getAppManager().finishActivity(Register2.class);
			}
		}

}
