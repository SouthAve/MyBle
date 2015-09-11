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

	// �����㲥��action
	public static final String FIND_DEVICE_ALARM_ON = "find.device.alarm.on";// �����豸�򿪱����㲥
	public static final String DISCONNECT_DEVICE = "find.device.disconnect";// �����豸�򿪱����㲥

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
			if (!bleSevice.init()) { // bleSevice��init��ʼ�ˣ��������ʼ���ɹ����ͽ������Activity��
										// ���������Ǿͻ����bleSevice���Ӷ����������Activity��bleservice����ͨ�š�
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

	// ���ڽ���bleService�Ĺ㲥
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
						statues.setText("������");
						connect_btn.setText("�Ͽ�");
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
						battery.setText("������ " + b);
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
		registerReceiver(mbtBroadcastReceiver, makeGattUpdateIntentFilter()); // ��̬ע��㲥������,ʹ��mbtBroadcastReceiver�ܽ��յ�belservice�������Ĺ㲥
	}

	private void bindBleSevice() {
		Intent serviceIntent = new Intent(this, BleSevice.class);
		// �󶨷���
		bindService(serviceIntent, conn, BIND_AUTO_CREATE);
	}

	private void connectDevice() {
		// TODO Auto-generated method stub
		connect_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ��ʼ����
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
