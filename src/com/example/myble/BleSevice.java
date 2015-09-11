package com.example.myble;

import java.text.Format;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BleSevice extends Service {
	private final static String TAG = BleSevice.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;

	private String mbluetoothDeviceAddress;
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	// 为了传送状态响应状态，要有几条ACTION
	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String BATTERY_LEVEL_AVAILABLE = "com.example.bluetooth.le.BATTERY_LEVEL_AVAILABLE";
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";

	// 集中常用的
	public static final UUID RX_ALART_UUID = UUID
			.fromString("00001802-0000-1000-8000-00805f9b34fb");
	public static final UUID RX_SERVICE_UUID = UUID
			.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");// DE5BF728-D711-4E47-AF26-65E3012A5DC7
	public static final UUID RX_CHAR_UUID = UUID
			.fromString("00002A06-0000-1000-8000-00805f9b34fb");// DE5BF729-D711-4E47-AF26-65E3012A5DC7
	public static final UUID TX_CHAR_UUID = UUID
			.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");// DE5BF72A-D711-4E47-AF26-65E3012A5DC7
	public static final UUID CCCD = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");
	public static final UUID C22D = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");
	public static final UUID BATTERY_SERVICE_UUID = UUID
			.fromString("0000180f-0000-1000-8000-00805f9b34fb");
	public static final UUID BATTERY_CHAR_UUID = UUID
			.fromString("00002a19-0000-1000-8000-00805f9b34fb");

	private final IBinder mBinder = new LocalBinder();

	@SuppressLint("NewApi")
	public BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				// 获取了GATT后，发现sevices
				gatt.discoverServices();

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				broadcastUpdate(intentAction);
			}
		}

		// 当Service成功发现后
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				readBatterylevel(gatt);
				enableTXNotification(gatt);
			} else {
				Log.w(TAG, "" + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Log.i(TAG, "onCharacteristic  READ");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				batteryBroadcastUpdate(BATTERY_LEVEL_AVAILABLE, characteristic);
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

			}

		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			Log.i(TAG, "onCharacteristicChanged");
		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder; // 返回了一个mBinder对象，而调用这个对象的getservice可以获得bleService对象
	}

	@SuppressLint("NewApi")
	private void batteryBroadcastUpdate(String action,
			BluetoothGattCharacteristic characteristic) {
		// TODO Auto-generated method stub
		Intent batteryIntent = new Intent();
		batteryIntent.setAction(action);
		Log.v("battery",
				"characteristic.getStringValue(0) = "
						+ characteristic.getIntValue(
								BluetoothGattCharacteristic.FORMAT_UINT8, 0));
		batteryIntent.putExtra(DeviceConnect.DEVICE_BATTERY, characteristic
				.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
		Log.v("bs", batteryIntent.getExtras().get(DeviceConnect.DEVICE_BATTERY)
				.toString());
		sendBroadcast(batteryIntent);

	}

	private void broadcastUpdate(String action) {
		Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	@SuppressLint("NewApi")
	private void broadcastUpdate(String action,
			BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent();
		intent.setAction(action);

		String str = "";
		final byte[] data = characteristic.getValue();
		final StringBuilder stringBuilder = new StringBuilder(data.length);
		for (byte byteChar : data) {
			stringBuilder.append(String.format("%02X", byteChar));
		}
		str = stringBuilder.toString();
		if ("01".equals(str)) {// ble设备
			sendBroadcast(intent);
		}

	}

	@SuppressLint("NewApi")
	public boolean init() {
		IntentFilter bleSeviceFilter = new IntentFilter();
		bleSeviceFilter.addAction(DeviceConnect.FIND_DEVICE_ALARM_ON);
		bleSeviceFilter.addAction(DeviceConnect.CANCEL_DEVICE_ALARM);
		registerReceiver(alarmReciver, bleSeviceFilter);
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) this
					.getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
			mBluetoothAdapter = mBluetoothManager.getAdapter();
		}
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}
		return true;

	}

	@SuppressLint("NewApi")
	public boolean connect(String bleAddress) {
		// TODO Auto-generated method stub
		if (mBluetoothAdapter == null || bleAddress == null) {
			Log.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		if (mbluetoothDeviceAddress != null
				&& bleAddress.equals(mbluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}

		}
		// 在MainActivity里获得了点击地址，然后可以获得该地址的device
		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(bleAddress);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		mBluetoothGatt = device
				.connectGatt(this, false, mBluetoothGattCallback);// 用device可以获得蓝牙连接的GATT
		mbluetoothDeviceAddress = bleAddress;
		mConnectionState = STATE_CONNECTING;
		Log.d(TAG, "Trying to create a new connection.");
		return true;

	}

	public void disconnect() {
		// TODO Auto-generated method stub
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	public class LocalBinder extends Binder {
		BleSevice getService() {
			return BleSevice.this;
		}
	}

	private BroadcastReceiver alarmReciver = new BroadcastReceiver() {

		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.v("bs", intent.getAction());
			if (DeviceConnect.FIND_DEVICE_ALARM_ON.equals(intent.getAction())) {

				writeRXCharacteristic(new byte[] { 0x02 },
						BleSevice.this.mBluetoothGatt);

				/*
				 * writeRXCharacteristic(new byte[] { 0x00, 0x34, 01, 00 },
				 * BleSevice.this.mBluetoothGatt);
				 */

			}
			if (DeviceConnect.CANCEL_DEVICE_ALARM.equals(intent.getAction())) {
				writeRXCharacteristic(new byte[] { 0x00 },
						BleSevice.this.mBluetoothGatt);
			}
			if (DeviceConnect.DISCONNECT_DEVICE.equals(intent.getAction())) {
				close(mBluetoothGatt);
			}
		}

	};

	// 点击和电量服务
	@SuppressLint("NewApi")
	private void enableTXNotification(BluetoothGatt gatt) {
		// 获取服务
		BluetoothGattService RxService = gatt.getService(RX_SERVICE_UUID);
		// 获取特征
		BluetoothGattCharacteristic TxChar = RxService
				.getCharacteristic(TX_CHAR_UUID);
		// 设置gatt的特征通知
		gatt.setCharacteristicNotification(TxChar, true);
		// 描述符
		BluetoothGattDescriptor txDescriptor = TxChar.getDescriptor(CCCD);
		if (txDescriptor != null) {
			txDescriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			gatt.writeDescriptor(txDescriptor);

		}

		// 将连接成功的gatt赋值给mBluetoothGatt
		mBluetoothGatt = gatt;

	}

	@SuppressLint("NewApi")
	private void writeRXCharacteristic(final String message,
			BluetoothGatt mBluetoothGatt) {
		// TODO Auto-generated method stub
		Log.v("bs", "wirtecharacteristic");
		Log.v("bs", "GATT:" + mBluetoothGatt.toString());
		byte[] value = message.getBytes();
		BluetoothGattService RxService = mBluetoothGatt
				.getService(RX_ALART_UUID);
		final BluetoothGattCharacteristic RxChar = RxService
				.getCharacteristic(RX_CHAR_UUID);
		RxChar.setValue(value);
		mBluetoothGatt.writeCharacteristic(RxChar);
	}

	@SuppressLint("NewApi")
	protected void writeRXCharacteristic(byte[] bs,
			BluetoothGatt mBluetoothGatt2) {
		// TODO Auto-generated method stub

		byte[] value = bs;
		BluetoothGattService RxService = mBluetoothGatt
				.getService(RX_ALART_UUID);

		final BluetoothGattCharacteristic RxChar = RxService
				.getCharacteristic(RX_CHAR_UUID);

		RxChar.setValue(value);
		mBluetoothGatt.writeCharacteristic(RxChar);

	}

	private void close(BluetoothGatt gatt) {
		gatt.disconnect();
		gatt.close();
		if (mBluetoothAdapter != null) {
			mBluetoothAdapter.cancelDiscovery();
			mBluetoothAdapter = null;
		}

	}

	@SuppressLint("NewApi")
	protected void readBatterylevel(BluetoothGatt mBluetoothGatt) {
		BluetoothGattService btrService = mBluetoothGatt
				.getService(BATTERY_SERVICE_UUID);
		BluetoothGattCharacteristic btrCharacteristic = btrService
				.getCharacteristic(BATTERY_CHAR_UUID);
		if (btrCharacteristic == null) {
			Log.v(TAG, "charcharcharchar is null");
		}
		mBluetoothGatt.readCharacteristic(btrCharacteristic);

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Notification notification = new Notification(R.drawable.ic_launcher,
				"Notification comes", System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_ALL;
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(this, "this is myBLE",
				"this is content", pendingIntent);
		startForeground(1, notification);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(alarmReciver);
	}

}
