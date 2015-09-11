package com.example.fragment;

import com.example.myble.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FragmentActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frag);
		Button button = (Button) findViewById(R.id.frag_btn);
		button.setOnClickListener(this); // ！！！！！！！设置监听！！！！！！！！！！！
	}

	@Override
	// 这里只是实现点击后的逻辑，并没有设置监听，所以button的步骤是1.找到控件2.为控件设置监听3.实现点击后的逻辑
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.frag_btn) {
			
		}
	}
}
