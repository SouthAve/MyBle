package com.example.myble;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.utils.ContacterObj;
import com.example.utils.ContentHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HttpConActivity extends Activity implements OnClickListener {
	public static final String TAG = "HttpUrlConActivity";
	Button sendRequest;
	TextView responseText;

	// JSON Node names
	private static final String TAG_CONTACTS = "contacts";
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_ADDRESS = "address";
	private static final String TAG_GENDER = "gender";
	private static final String TAG_PHONE = "phone";
	private static final String TAG_PHONE_MOBILE = "mobile";
	private static final String TAG_PHONE_HOME = "home";
	private static final String TAG_PHONE_OFFICE = "office";

	public static final int SHOW_RESPONSE = 0;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_RESPONSE:
				String response = (String) msg.obj;
				responseText.setText(response);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_httpurlcon);
		sendRequest = (Button) findViewById(R.id.send_request);
		responseText = (TextView) findViewById(R.id.response_text);
		sendRequest.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == R.id.send_request) {
			// sendRequestWithHttpConnection();
			sendRequestWithHttpClient();
		}
	}

	private void sendRequestWithHttpClient() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {

					// newһ��HttpClient��HttpCLient��һ���ӿڣ�����һ�㴴��DefaultHttpClient
					HttpClient mHttpClient = new DefaultHttpClient();
					// httpGet ���ڷ���һ��get����
					HttpGet httpGet = new HttpGet(
							"http://192.168.1.124/contact.json");
					// execute��httpGet�����Ի��һ����������response
					HttpResponse httpResponse = mHttpClient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200)
					// ��Ӧ�ɹ�
					{
						// ��ȡ��httpʵ��
						HttpEntity entity = httpResponse.getEntity();
						// ��EntityUtils��entityת��String
						String response = EntityUtils.toString(entity, "utf-8");

						// parseXMLWithPull(response);

						// parseXMLWithSAX(response);

						// parseJSONWithJSONObject(response);

						parseJSONWithGSON(response);

						/*
						 * Message msg = new Message(); msg.what =
						 * SHOW_RESPONSE; msg.obj = response;
						 * handler.sendMessage(msg);
						 */

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	private void parseJSONWithGSON(String jsonData) {
		Gson gson = new Gson();

		ContacterObj con = gson.fromJson(jsonData, ContacterObj.class);

		for (com.example.utils.ContacterObj.Contacter c : con.getContacts()) {
			Log.v(TAG, c.getName());
			Log.v(TAG, c.getPhone().getOffice());
			
		}
		
		
	}

	private void parseJSONWithJSONObject(String jsonData) {
		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			JSONArray contacts = jsonObject.getJSONArray(TAG_CONTACTS);
			for (int i = 0; i < contacts.length(); i++) {
				JSONObject c = contacts.getJSONObject(i);
				String id = c.getString(TAG_ID);
				String name = c.getString(TAG_NAME);
				String email = c.getString(TAG_EMAIL);
				String address = c.getString(TAG_ADDRESS);
				String gender = c.getString(TAG_GENDER);
				JSONObject phone = c.getJSONObject(TAG_PHONE);
				String home = phone.getString(TAG_PHONE_HOME);
				Log.v(TAG, id);
				Log.v(TAG, name);
				Log.v(TAG, email);
				Log.v(TAG, home);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void parseXMLWithSAX(String xmlData) {
		// TODO Auto-generated method stub
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// ��ȡXMLreaderʵ��
			XMLReader xmlReader = factory.newSAXParser().getXMLReader();
			// ���ݴ�����
			ContentHandler handler = new ContentHandler();
			// reader���ô�����
			xmlReader.setContentHandler(handler);
			// ��ʼ����
			xmlReader.parse(new InputSource(new StringReader(xmlData)));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void parseXMLWithPull(String xmlData) {
		try {
			XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
			XmlPullParser parser = fac.newPullParser();
			parser.setInput(new StringReader(xmlData));
			int eventType = parser.getEventType();
			String id = "";
			String name = "";
			String version = "";
			while (eventType != XmlPullParser.END_DOCUMENT) {
				// nextName()����ȡ�ӵ������
				String nodeName = parser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG: {
					if ("id".equals(nodeName)) {
						// nextText()����ȡ�ӵ�ľ�������
						id = parser.nextText();
					}
					if ("name".equals(nodeName)) {
						name = parser.nextText();
					}
					if ("version".endsWith(nodeName)) {
						version = parser.nextText();
					}
					break;
				}
				case XmlPullParser.END_TAG: {
					if ("app".equals(nodeName)) {
						Log.v("HttpConActivity", "id is " + id);
						Log.v("HttpConActivity", "name is " + name);
						Log.v("HttpConActivity", "version is " + version);
					}
				}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendRequestWithHttpConnection() {
		// TODO Auto-generated method stub

		// ����һ���µ��߳���ִ���������������
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				HttpURLConnection connection = null;
				try {

					URL url = new URL("http://www.baidu.com");
					// ͨ��URL��ȡ��HttpURLConnection
					connection = (HttpURLConnection) url.openConnection();
					// setRequestMethod�����ύ��ʽget
					connection.setRequestMethod("GET");
					// �������ӳ�ʱ�����룩
					connection.setConnectTimeout(8000);
					// ���ö�ȡ��ʱ
					connection.setReadTimeout(8000);
					// getInputStream��ȡ���������ص������ж�ȡ
					InputStream in = connection.getInputStream();
					// InputStreamReader���ֽ���ת�����ַ�����BufferedReader��ȡ
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));

					StringBuilder response = new StringBuilder();
					String line;
					// buffereadReader��readline������ʼ��ȡ�У�����StringBuilder
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					// ����message������UI
					Message msg = new Message();
					msg.what = SHOW_RESPONSE;
					msg.obj = response.toString();
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
