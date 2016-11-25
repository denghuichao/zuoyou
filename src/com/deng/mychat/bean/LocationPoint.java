package com.deng.mychat.bean;

import java.io.Serializable;

public class LocationPoint implements Serializable{
	public Double latitude=Double.MAX_VALUE;
	public Double longitude=Double.MAX_VALUE;
	
	private String type="";
	
    public static final String BAIDU_LBS_TYPE = "bd09ll";
    public static double pi = 3.1415926535897932384626;
    public static double a = 6378245.0;
    public static double ee = 0.00669342162296594323;
	
	public LocationPoint(Double lat,Double lng)
	{
		this.latitude=(double) (((int)(lat*100000))/100000.00);
		this.longitude=(double) (((int)(lng*100000))/100000.00);
	}
	

	public double getWgLat() {
			return latitude;
	}
	
	public void setWgLat(double wgLat) {
			this.latitude = wgLat;
	}
	
	public double getWgLon() {
			return longitude;
	}
	
	public void setWgLon(double wgLon) {
		this.longitude = wgLon;
	}
	
	
	public void setType(String type)
	{
		this.type=type;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public static double distanceBetween(LocationPoint p1, LocationPoint p2)
	{
		if(p1==null || p2==null)return Double.MAX_VALUE;
		
		Double C = Math.sin(p1.latitude/57.2958)* Math.sin(p2.latitude/57.2958) 
				+  Math.cos(p1.latitude/57.2958)* Math.cos(p2.latitude/57.2958)*
				 Math.cos((p1.longitude-p2.longitude)/57.2958);

		double	distance= 6371.004* Math.acos(C) *1000;//meters 	
		
		return distance;
	}
	
	 /**
	    * ����ͼAPI����ϵͳ�Ƚ���ת��;
	    * WGS84����ϵ������������ϵ��������ͨ�õ�����ϵ���豸һ�����GPSоƬ���߱���оƬ��ȡ�ľ�γ��ΪWGS84��������ϵ,
	    * �ȸ��ͼ���õ���WGS84��������ϵ���й���Χ���⣩;
	    * GCJ02����ϵ������������ϵ�������й����Ҳ����ƶ��ĵ�����Ϣϵͳ������ϵͳ����WGS84����ϵ�����ܺ������ϵ��
	    * �ȸ��й���ͼ�������й���ͼ���õ���GCJ02��������ϵ; BD09����ϵ�����ٶ�����ϵ��GCJ02����ϵ�����ܺ������ϵ;
	    * �ѹ�����ϵ��ͼ������ϵ�ȣ�����Ҳ����GCJ02�����ϼ��ܶ��ɵġ� chenhua
	  */

	  
	      /**
	       * 84 to ��������ϵ (GCJ-02) World Geodetic System ==> Mars Geodetic System
	       * 
	       * @param lat
	       * @param lon
	       * @return
	       */
	      public static LocationPoint gps84_To_Gcj02(double lat, double lon) {
	          if (outOfChina(lat, lon)) {
	              return null;
	          }
	          double dLat = transformLat(lon - 105.0, lat - 35.0);
	          double dLon = transformLon(lon - 105.0, lat - 35.0);
	          double radLat = lat / 180.0 * pi;
	          double magic = Math.sin(radLat);
	          magic = 1 - ee * magic * magic;
	          double sqrtMagic = Math.sqrt(magic);
	          dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
	          dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
	          double mgLat = lat + dLat;
	          double mgLon = lon + dLon;
	          return new LocationPoint(mgLat, mgLon);
	      }
	  
	      /**
	       * * ��������ϵ (GCJ-02) to 84 * * @param lon * @param lat * @return
	       * */
	      public static LocationPoint gcj_To_Gps84(double lat, double lon) {
	    	  LocationPoint gps = transform(lat, lon);
	          double lontitude = lon * 2 - gps.getWgLon();
	          double latitude = lat * 2 - gps.getWgLat();
	          return new LocationPoint(latitude, lontitude);
	      }
	  
	      /**
	       * ��������ϵ (GCJ-02) ��ٶ�����ϵ (BD-09) ��ת���㷨 �� GCJ-02 ����ת���� BD-09 ����
	       * 
	       * @param gg_lat
	       * @param gg_lon
	       */
	      public static LocationPoint gcj02_To_Bd09(double gg_lat, double gg_lon) {
	          double x = gg_lon, y = gg_lat;
	          double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * pi);
	          double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * pi);
	          double bd_lon = z * Math.cos(theta) + 0.0065;
	          double bd_lat = z * Math.sin(theta) + 0.006;
	          return new LocationPoint(bd_lat, bd_lon);
	      }
	  
	      /**
	       * * ��������ϵ (GCJ-02) ��ٶ�����ϵ (BD-09) ��ת���㷨 * * �� BD-09 ����ת����GCJ-02 ���� * * @param
	       * bd_lat * @param bd_lon * @return
	       */
	      public static LocationPoint bd09_To_Gcj02(double bd_lat, double bd_lon) {
	          double x = bd_lon - 0.0065, y = bd_lat - 0.006;
	          double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
	          double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);
	          double gg_lon = z * Math.cos(theta);
	          double gg_lat = z * Math.sin(theta);
	          return new LocationPoint(gg_lat, gg_lon);
	      }
	  
	      /**
	       * (BD-09)-->84
	       * @param bd_lat
	       * @param bd_lon
	       * @return
	       */
	      public static LocationPoint bd09_To_Gps84(double bd_lat, double bd_lon) {
	  
	    	  LocationPoint gcj02 = bd09_To_Gcj02(bd_lat, bd_lon);
	    	  LocationPoint map84 = gcj_To_Gps84(gcj02.getWgLat(),
	                  gcj02.getWgLon());
	          return map84;
	  
	      }
	  
	      public static boolean outOfChina(double lat, double lon) {
	          if (lon < 72.004 || lon > 137.8347)
	              return true;
	          if (lat < 0.8293 || lat > 55.8271)
	              return true;
	         return false;
	     }
	 
	     public static LocationPoint transform(double lat, double lon) {
	         if (outOfChina(lat, lon)) {
	             return new LocationPoint(lat, lon);
	         }
	         double dLat = transformLat(lon - 105.0, lat - 35.0);
	         double dLon = transformLon(lon - 105.0, lat - 35.0);
	         double radLat = lat / 180.0 * pi;
	         double magic = Math.sin(radLat);
	         magic = 1 - ee * magic * magic;
	         double sqrtMagic = Math.sqrt(magic);
	         dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
	         dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
	         double mgLat = lat + dLat;
	         double mgLon = lon + dLon;
	         return new LocationPoint(mgLat, mgLon);
	     }
	 
	     public static double transformLat(double x, double y) {
	         double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
	                 + 0.2 * Math.sqrt(Math.abs(x));
	         ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
	         ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
	         ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
	         return ret;
	     }
	 
	     public static double transformLon(double x, double y) {
	         double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
	                 * Math.sqrt(Math.abs(x));
	         ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
	         ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
	         ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
	                 * pi)) * 2.0 / 3.0;
	         return ret;
	     }
	 
	
	public String toString()
	{
		return type+" location:"+"("+this.latitude+","+this.longitude+")";
	}
}
