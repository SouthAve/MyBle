package com.example.myble;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.db.DateBaseHelper;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";
	Button mButton;
	Button localbroadcast;
	Button forceoffline;
	Button qeury;
	Button replace;
	EditText savefile;
	ListView listView;
	// 准备退出
	private static Boolean isExit = false;
	// 蓝牙适配器
	BluetoothAdapter mBluetoothAdapter;
	// 扫描间隔时间
	private static final long SCAN_PERIOD = 6300;
	// Callback interface used to deliver LE scan results.回调借口用于传递扫描结果
	private LeScanCallback mLeScanCallback;

	// listviewAdapter
	BleDeviceListAdapter mBleDeviceListAdapter;
	// listview 数据
	HashMap<String, String> hashMap;
	ArrayList<HashMap<String, String>> listItems = new ArrayList<HashMap<String, String>>();

	// 本地广播
	LocalBroadcastManager localBroadcastManager;
	String action = "com.example.LOCAL_BROADCAST";
	private Handler handler;
	private boolean isStart;

	// 数据库
	private DateBaseHelper dbHelper;
	private String dbname = "bledevice.db";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActivityCollector.addActivity(this);
		dbHelper = new DateBaseHelper(this, dbname, null, 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		databaseOperation(db);
		init();
		getBleAdapter();
		getScanResualt();
		scanLeDevice(true);
		setListItemListener();
	}

	private void databaseOperation(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		ContentValues values = new ContentValues();
		values.put("name", "gg");
		db.insert("detail_record", null, values);
		values.clear();
		values.put("name", "hh");
		db.insert("detail_record", null, values);
		values.clear();
		values.put("mac", "12345");
		db.update("detail_record", values, "name = ?", new String[] { "gg" });
		db.delete("detail_record", "mac = ?", new String[] { "1234" });
	}

	private void setListItemListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				BluetoothDevice device = mBleDeviceListAdapter
						.getDevice(position); // 获取listview中所点击的device
				Log.v("ma", "position" + position);
				Log.v("ma", "name:" + device.getName());

				final Intent intent = new Intent(MainActivity.this,
						DeviceConnect.class);
				intent.putExtra(DeviceConnect.EXTRAS_DEVICE_NAME,
						device.getName());
				intent.putExtra(DeviceConnect.EXTRAS_DEVICE_ADDRESS,
						device.getAddress());
				if (isStart) {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
				startActivity(intent);
			}
		});
	}

	private void init() {
		qeury = (Button) findViewById(R.id.querydb);
		listView = (ListView) findViewById(R.id.lv_deviceList);
		mButton = (Button) findViewById(R.id.bt_scan);
		localbroadcast = (Button) findViewById(R.id.bt_localbroadcast);
		replace = (Button) findViewById(R.id.replacedata);
		savefile = (EditText) findViewById(R.id.savefile);
		String editString = load();
		if (!TextUtils.isEmpty(editString)) {
			savefile.setText(editString);
			savefile.setSelection(editString.length());
			Toast.makeText(this, "restroring is ok", Toast.LENGTH_SHORT).show();
		}
		mBleDeviceListAdapter = new BleDeviceListAdapter(this, listItems);
		listView.setAdapter(mBleDeviceListAdapter);
		localBroadcastManager = LocalBroadcastManager.getInstance(this);

		localbroadcast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				localBroadcastManager.sendBroadcastSync(new Intent(action));
			}
		});

		localBroadcastManager.registerReceiver(localReceiver, new IntentFilter(
				action));

		forceoffline = (Button) findViewById(R.id.forceoffline);
		forceoffline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendBroadcast(new Intent(
						"com.example.broadcastbestpractice.FORCE_OFFLINE"));
			}
		});

		qeury.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				Cursor cursor = db.query("detail_record", null, null, null,
						null, null, null);
				if (cursor.moveToFirst()) {
					do {
						Log.v(TAG,
								cursor.getString(cursor.getColumnIndex("name")));
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
		});
		replace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				db.beginTransaction();
				try {
					db.delete("detail_record", null, null);
					if (true) {
						// throw new NullPointerException();
					}
					ContentValues values = new ContentValues();
					values.put("name", "hf");
					db.insert("detail_record", null, values);
					db.setTransactionSuccessful();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.endTransaction();
				}

			}
		});

		handler = new Handler() {
			public void handleMessage(Message msg) {

				super.handleMessage(msg);
			}
		};

	}

	@SuppressLint("NewApi")
	private void getBleAdapter() {
		final BluetoothManager bluetoothManager = (BluetoothManager) this
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}

	@SuppressLint("NewApi")
	private void scanLeDevice(final boolean enable) {
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (enable) {
					mBluetoothAdapter.startLeScan(mLeScanCallback);
					isStart = true;
					// 扫描一段时间后停止扫描
					handler.postDelayed(new Runnable() {

						@SuppressLint("NewApi")
						@Override
						public void run() {
							mBluetoothAdapter.stopLeScan(mLeScanCallback);
						}
					}, SCAN_PERIOD);
				} else {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					isStart = false;
				}
			}

		});
	}

	@SuppressLint("NewApi")
	private void getScanResualt() {

		mLeScanCallback = new LeScanCallback() {

			@Override
			public void onLeScan(final BluetoothDevice device, int rssi,
					byte[] scanRecord) {
				MainActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						mBleDeviceListAdapter.addDevice(device);
						mBleDeviceListAdapter.notifyDataSetChanged(); // 可以在修改适配器绑定的数组后，不用重新刷新Activity，通知Activity更新ListView
																		// handler.sendEmptyMessage(0);
					}
				});

			}
		};

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.test_item:
			startActivity(new Intent(this, LoginActivity.class));
			break;
		case R.id.webview_item:
			startActivity(new Intent(this, WebViewActivity.class));
			break;
		case R.id.httpurlconnection:
			startActivity(new Intent(this, HttpConActivity.class));
			break;
		}
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	private void exitBy2Click() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
			new Timer().schedule(new TimerTask() {
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000);
		} else {
			String a = savefile.getText().toString();
			save(a);
			finish();
			System.exit(0);
		}

	}

	BroadcastReceiver localReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Toast.makeText(context, "localReceiver!", Toast.LENGTH_SHORT)
					.show();
		}

	};

	protected void onDestroy() {
		super.onDestroy();
		String a = savefile.getText().toString();
		save(a);
		ActivityCollector.removeActivity(this);

	};

	/**
	 * 存储文件
	 * 
	 * @param data
	 */
	private void save(String date) {
		FileOutputStream out = null;
		BufferedWriter bw = null;
		try {
			// Open a private file associated with this Context's application
			// package for writing. Creates the file if it doesn't already
			// exist.
			out = openFileOutput("date", MODE_PRIVATE);
			bw = new BufferedWriter(new OutputStreamWriter(out)); // InputStreamReader:字节到字符的桥梁
																	// OutputStreamWriter:字符到字节的桥梁
			bw.write(date);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String load() {
		FileInputStream in = null;
		BufferedReader br = null;
		StringBuilder content = new StringBuilder();

		try {
			in = openFileInput("date");
			br = new BufferedReader(new InputStreamReader(in));// InputStreamReader:字节到字符的桥梁
			String line = "";
			while ((line = br.readLine()) != null) {
				content.append(line);
			}
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
		}
		return content.toString();

	}

}
