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
		button.setOnClickListener(this); // �����������������ü�������������������������
	}

	@Override
	// ����ֻ��ʵ�ֵ������߼�����û�����ü���������button�Ĳ�����1.�ҵ��ؼ�2.Ϊ�ؼ����ü���3.ʵ�ֵ������߼�
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.frag_btn) {
			
		}
	}
}
