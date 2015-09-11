package com.example.myble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BleDeviceListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;// 得到一个LayoutInfalter对象用来导入布局
	ArrayList<HashMap<String, String>> listItems;
	private ArrayList<BluetoothDevice> mLeDevices;

	public BleDeviceListAdapter(Context context,
			ArrayList<HashMap<String, String>> listItems) {
		mLeDevices = new ArrayList<BluetoothDevice>();
		this.mInflater = LayoutInflater.from(context);
		this.listItems = listItems;
	}

	public void addDevice(BluetoothDevice device) {

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("device_name", device.getName());
		hashMap.put("device_address", device.getAddress());
		if (!listItems.contains(hashMap)) {
			listItems.add(hashMap);
		}
		if (!mLeDevices.contains(device)) {
			this.mLeDevices.add(device);
		}
	}

	public BluetoothDevice getDevice(int position) {
		// TODO Auto-generated method stub
		return mLeDevices.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub

		ViewHolder viewholder;
		if (view == null) {
			view = mInflater.inflate(R.layout.item_devicelist, null);
			viewholder = new ViewHolder();
			viewholder.devicename = (TextView) view
					.findViewById(R.id.tv_devicelist_name);
			view.setTag(viewholder);

		} else {
			viewholder = (ViewHolder) view.getTag();
		}
		viewholder.devicename.setText(listItems.get(position)
				.get("device_name").toString());

		return view;

	}

	static class ViewHolder {
		TextView devicename;
	}

}
