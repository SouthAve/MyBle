package com.example.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					HttpURLConnection httpConnection = null;
					URL url = new URL(address);
					httpConnection = (HttpURLConnection) url.openConnection();
					httpConnection.setRequestMethod("GET");
					httpConnection.setConnectTimeout(8000);
					httpConnection.setReadTimeout(8000);
					httpConnection.setDoInput(true);
					httpConnection.setDoOutput(true);
					InputStream in = httpConnection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}

					if (listener != null) {
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						listener.onError(e);
					}
				}

			}
		}).start();
	}
}
