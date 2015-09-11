package com.example.myble;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class DownloadTask extends AsyncTask<Void, Integer, Boolean> {
	Context context;
	ProgressDialog progressDialog;

	public DownloadTask(Context context) { // ���캯��ԭ����û�з���ֵ���Ͳ�����ȥ��void��
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(context);
		progressDialog.show();

	}

	@Override
	protected Boolean doInBackground(Void... params) { // �������߳���
		// TODO Auto-generated method stub
		try {
			while (true) {
				int downloadpercent = doDownload();
				publishProgress(downloadpercent);
				if (downloadpercent >= 100) {
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private int doDownload() {
		return 10;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		progressDialog.setMessage("Downloaded" + values[0] + "%");

	}

	@Override
	protected void onPostExecute(Boolean result) {
		progressDialog.dismiss();

	}

}
