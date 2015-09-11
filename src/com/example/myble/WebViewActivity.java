package com.example.myble;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {
	WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url); // ���Ը��ݴ���Ĳ�����ȥ�����µ���ҳ
				return true; // true��ʾ��sǰwebview���Դ��������ҳ�����󣬲��ý���ϵͳ�����
			}
		});
		webview.loadUrl("http://www.baidu.com");// ������ҳ
	}
}
