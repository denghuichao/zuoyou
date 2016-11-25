package com.deng.mychat.bean;

import java.io.Serializable;

public class Picture implements Serializable{
	public String largePicPath="";
	public String smallPicture="";
	
	public Picture(String large,String small)
	{
		this.largePicPath=large;
		this.smallPicture=small;
	}
}
