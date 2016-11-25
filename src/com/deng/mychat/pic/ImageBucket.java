package com.deng.mychat.pic;

import java.util.List;

/**
 * �?个目录的相册对象
 * 
 * @author Administrator
 * 
 */
public class ImageBucket implements Comparable{
	public int count = 0;
	public String bucketName;
	public List<ImageItem> imageList;
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return this.bucketName.compareTo(((ImageBucket)arg0).bucketName);
	}

}
