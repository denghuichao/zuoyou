package com.deng.mychat.service;

import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.WCApplication;
import com.deng.mychat.config.ApiClient.ClientCallback;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class LocationService extends Service{

    private static final String TAG = "MAIN";  
	
    
	public LocationClient mLocationClient;
	public GeofenceClient mGeofenceClient;
	public MyLocationListener mMyLocationListener;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public int onStartCommand(Intent intent,int flags,int startId)
	{
        mLocationClient = new LocationClient(getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		mGeofenceClient = new GeofenceClient(getApplicationContext());
		InitLocation();
		mLocationClient.start();
        return START_STICKY;
	}
	
	
	 public void onDestroy() {  
	        super.onDestroy();  
	        mLocationClient.stop();
	        mLocationClient.unRegisterLocationListener(mMyLocationListener);
	        stopSelf();  
	    }  
	
	 private void updateWithNewLocation(BDLocation location)  
	    {  
	        double lat=0;
	        double lng=0;
	        if (location != null)  
	        {  
	             lat= location.getLatitude();  
	             lng = location.getLongitude();  
	        }  
	        else  
	        {  
	            return;
	        } 
	        
	        WCApplication  appContext=(WCApplication) this.getApplication();
	        LocationPoint lp=new LocationPoint(lat,lng);
	        appContext.modifyLoginLocation(lp);
	        ApiClient.updateUserLocation(appContext.getLoginApiKey(), lp.longitude,lp.latitude,
	    		   new ClientCallback() {
					@Override
					public void onSuccess(Object data) {

					}

					@Override
					public void onFailure(String message) {

					}

					@Override
					public void onError(Exception e) {
						//Log.d(TAG,e.getMessage());
					}

					@Override
					public void onRequestLogIn() {
						
					}
	    	   
	       });
	    }  
	 
	 private void InitLocation(){
		 LocationClientOption option = new LocationClientOption();
		 option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
		 option.setCoorType("gcj02");//���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		 option.setScanSpan(10000);//���÷���λ����ļ��ʱ��Ϊ5000ms
		 option.setIsNeedAddress(true);//���صĶ�λ���������ַ��Ϣ
		 option.setNeedDeviceDirect(true);//���صĶ�λ��������ֻ���ͷ�ķ���
		 mLocationClient.setLocOption(option);
		}
	
		/**
		 * ʵ��ʵλ�ص�����
		 */
		public class MyLocationListener implements BDLocationListener {

			@Override
			public void onReceiveLocation(BDLocation location) {
				//Receive Location 
				StringBuffer sb = new StringBuffer(256);
				sb.append("time : ");
				sb.append(location.getTime());
				sb.append("\nerror code : ");
				sb.append(location.getLocType());
				sb.append("\nlatitude : ");
				sb.append(location.getLatitude());
				sb.append("\nlontitude : ");
				sb.append(location.getLongitude());
				sb.append("\nradius : ");
				sb.append(location.getRadius());
				if (location.getLocType() == BDLocation.TypeGpsLocation){
					sb.append("\nspeed : ");
					sb.append(location.getSpeed());
					sb.append("\nsatellite : ");
					sb.append(location.getSatelliteNumber());
					sb.append("\ndirection : ");
					sb.append("\naddr : ");
					sb.append(location.getAddrStr());
					sb.append(location.getDirection());
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
					sb.append("\naddr : ");
					sb.append(location.getAddrStr());
					//��Ӫ����Ϣ
					sb.append("\noperationers : ");
					sb.append(location.getOperators());
				}
				
				Log.d(TAG, sb.toString());
				updateWithNewLocation(location);
			}


		}
}
