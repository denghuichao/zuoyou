package com.deng.mychat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.deng.mychat.R;
import com.deng.mychat.bean.LocationPoint;

/**
 * 婕旂ずMapView鐨勫熀鏈敤娉�
 */
public class MapActivity extends Activity implements
OnGetGeoCoderResultListener{
	@SuppressWarnings("unused")
	private static final String LTAG ="MAP";
	private BaiduMap mBaiduMap;
	private MapView mMapView;
	
	private LinearLayout topll;
	private InfoWindow mInfoWindow;
	private Marker mMarker;
	private GeoCoder mSearch;
	BitmapDescriptor point =BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);//point = null; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.map_layout);
		
		topll=(LinearLayout) this.findViewById(R.id.topll);

		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		Intent intent = getIntent();
		if (intent.hasExtra("x") && intent.hasExtra("y")) {
			// 褰撶敤intent鍙傛暟鏃讹紝璁剧疆涓績鐐逛负鎸囧畾鐐�
			Bundle b = intent.getExtras();
			LocationPoint lp= LocationPoint.gcj02_To_Bd09(b.getDouble("y"), b.getDouble("x"));
			LatLng p = new LatLng(lp.latitude, lp.longitude);
			mMapView = new MapView(this,
					new BaiduMapOptions().mapStatus(new MapStatus.Builder()
							.target(p).zoom(16).build()));
			mBaiduMap = mMapView.getMap();	
			OverlayOptions ooA = new MarkerOptions().position(p).icon(point)
				.zIndex(9).draggable(true);
			mMarker = (Marker) (mBaiduMap.addOverlay(ooA));
		} else {
			mMapView = new MapView(this, new BaiduMapOptions());
			mBaiduMap = mMapView.getMap();
		}

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				if (marker == mMarker) {
					
					LatLng ptCenter =marker.getPosition();
					// 反Geo搜索
					mSearch.reverseGeoCode(new ReverseGeoCodeOption()
							.location(ptCenter));
				}
				return true;
			}
		});
		
		topll.addView(mMapView);
		mBaiduMap.setMaxAndMinZoomLevel(20, 100);
		
	}
	
	public void buttonClick(View v)
	{
		switch (v.getId())
		{
			case R.id.cancel:finish();break;
		}
	}

	public void initOverlay() {
		// add marker overlay


	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// activity 鏆傚仠鏃跺悓鏃舵殏鍋滃湴鍥炬帶浠�
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}
	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		mSearch.destroy();
		super.onDestroy();
		point.recycle();

	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		String strInfo = String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			return;
		}

		Button button = new Button(getApplicationContext());
		button.setBackgroundResource(R.drawable.popup);
		OnInfoWindowClickListener listener = null;
		
		listener = new OnInfoWindowClickListener() {
				public void onInfoWindowClick() {
					mBaiduMap.hideInfoWindow();
				}
		};
			
		LatLng ll = result.getLocation();
		button.setText(result.getAddress());
		mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
		mBaiduMap.showInfoWindow(mInfoWindow);

	}
	

}
