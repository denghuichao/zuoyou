package com.deng.mychat.pic;

import java.io.Serializable;

/**
 * �?个图片对�?
 * 
 * @author Administrator
 * 
 */
public class ImageItem implements Serializable,Comparable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;
	public long modifyTime;
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return (int) (this.modifyTime-((ImageItem)o).modifyTime);
	}
}
