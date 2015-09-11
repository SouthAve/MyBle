package com.example.myble;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceConnect extends Activity implements OnClickListener {
	private final static String TAG = DeviceConnect.class.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	// 警报广播的action
	public static final String FIND_DEVICE_ALARM_ON = "find.device.alarm.on";// 查找设备打开报警广播
	public static final String DISCONNECT_DEVICE = "find.device.disconnect";// 查找设备打开报警广播

	public static final String CANCEL_DEVICE_ALARM = "find.device.cancel.alarm";

	public static final String DEVICE_BATTERY = "device.battery.level";
	String bleName;
	String bleAddress;
	TextView name;
	TextView address;
	TextView statues;
	TextView battery;
	Button connect_btn;
	Button alarm_btn;

	Intent intent;

	boolean isConnecting = false;
	boolean isAlarm = false;

	// BLE Sevice
	BleSevice bleSevice;

	private final ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			bleSevice = ((BleSevice.LocalBinder) service).getService(); //
			Log.d("dc", bleSevice.toString());
			if (!bleSevice.init()) { // bleSevice的init开始了！！如果开始不成功，就结束这个Activity，
										// 从这里我们就获得了bleSevice，从而可以在这个Activity和bleservice进行通信。
				finish();
			}
			bleSevice.connect(bleAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

			bleSevice = null;
		}
	};

	// 用于接收bleService的广播
	BroadcastReceiver mbtBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();

			if (BleSevice.ACTION_GATT_CONNECTED.equals(action)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						isConnecting = true;
						statues.setText("连接上");
						connect_btn.setText("断开");
					}
				});
			}
			if (BleSevice.ACTION_DATA_AVAILABLE.equals(action)) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						Toast.makeText(DeviceConnect.this,
								"ble device clicked!", Toast.LENGTH_SHORT)
								.show();
					}
				});
			}
			if (BleSevice.BATTERY_LEVEL_AVAILABLE.equals(action)) {
				if (intent != null) {
					Log.v("dc",
							intent.getExtras()
									.get(DeviceConnect.DEVICE_BATTERY)
									.toString());

				}
				final String b = intent.getExtras()
						.get(DeviceConnect.DEVICE_BATTERY).toString();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Log.v("dc", "!!!!!!!!!!!!!");
						battery.setText("电量： " + b);
					}
				});
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		ActivityCollector.addActivity(this);
		init();
		bindBleSevice();
		connectDevice();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(mbtBroadcastReceiver, makeGattUpdateIntentFilter()); // 动态注册广播接受器,使得mbtBroadcastReceiver能接收到belservice发过来的广播
	}

	private void bindBleSevice() {
		Intent serviceIntent = new Intent(this, BleSevice.class);
		// 绑定服务
		bindService(serviceIntent, conn, BIND_AUTO_CREATE);
	}

	private void connectDevice() {
		// TODO Auto-generated method stub
		connect_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 开始连接
				if (!isConnecting) {
					isConnecting = true;
					bleSevice.connect(bleAddress);
				} else {
					isConnecting = false;
					sendBroadcast(new Intent(DISCONNECT_DEVICE));
					bleSevice.disconnect();
				}
			}
		});
	}

	private void init() {
		name = (TextView) findViewById(R.id.tv_device_name);
		address = (TextView) findViewById(R.id.tv_device_address);
		statues = (TextView) findViewById(R.id.tv_connect_status);
		connect_btn = (Button) findViewById(R.id.bt_connect);
		battery = (TextView) findViewById(R.id.tv_battery_level);
		alarm_btn = (Button) findViewById(R.id.alarm);

		alarm_btn.setOnClickListener(this);
		intent = getIntent();
		if (intent != null) {
			bleAddress = intent.getExtras().getString(EXTRAS_DEVICE_ADDRESS);
			name.setText(intent.getExtras().getString(EXTRAS_DEVICE_NAME));
			address.setText(bleAddress);
		}

	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BleSevice.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BleSevice.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BleSevice.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BleSevice.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BleSevice.BATTERY_LEVEL_AVAILABLE);

		return intentFilter;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.v("dc", "dc onDestroy");
		bleSevice.close();
		unbindService(conn);
		unregisterReceiver(mbtBroadcastReceiver);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.alarm) {
			if (!isAlarm) {
				isAlarm = true;
				Intent alarmIntent = new Intent(FIND_DEVICE_ALARM_ON);
				Log.v("dc", alarmIntent.getAction());
				this.sendBroadcast(alarmIntent);
			} else {
				isAlarm = false;
				sendBroadcast(new Intent(CANCEL_DEVICE_ALARM));
			}
		}
	}
}
