package com.deng.mychat.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.deng.mychat.R;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.view.CustomProgressDialog;

public class WebViewActivity extends AppActivity{
	
	private String url;
	private WebView webView;
	private CustomProgressDialog loadingPd;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_activity);
		url=this.getIntent().getStringExtra("url");
		initUI();

	}

	public void buttonClick(View v)
	{
		switch(v.getId())
		{
		case R.id.cancel:finish();break;
		}
	}
	
	private void initUI() {
		 	webView=(WebView)this.findViewById(R.id.webview); 
	        webView.setWebViewClient(new webViewClient()); 
	        webView.loadUrl(url);  
			loadingPd = CustomProgressDialog.show(this, "正在加载...", false, null);//UIHelper.showProgress(this, null, "正在登录", true);
			loadingPd.setCancelable(true);
	}
	
	
	
	   class webViewClient extends WebViewClient{ 

	    @Override 

	    public boolean shouldOverrideUrlLoading(WebView view, String url) { 

	        view.loadUrl(url); 
	        return true; 

	    } 
	    @Override
	    public void onPageFinished(WebView view, String url) 
	    {

	    	 if(loadingPd!=null)loadingPd.dismiss();
             super.onPageFinished(view, url);
	    }

	 }
}
