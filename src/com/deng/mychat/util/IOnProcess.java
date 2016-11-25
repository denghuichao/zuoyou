package com.deng.mychat.util;

public interface IOnProcess {
	public void onProcess(long current, long total);
	public void onFailure(Exception ex);
}
