package bupt.mxly.healthcare.sipchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import bupt.mxly.healthcare.R;
import bupt.mxly.healthcare.db.DBAdapter;
import bupt.mxly.healthcare.db.UserInfo;
import bupt.mxly.healthcare.sipchat.jsip_ua.SipProfile;
import bupt.mxly.healthcare.sipchat.jsip_ua.impl.DeviceImpl;
import bupt.mxly.healthcare.sipchat.jsip_ua.impl.MessageProcessor;


//import bupt.mxly.healthcare.sipchat.jsip_ua.SipProfile;
//import bupt.jsip_demo.R;
//import sipchat.jsip_ua.SipProfile;
//import jsip_ua.impl.DeviceImpl;
//import jsip_ua.impl.MessageProcessor;

public class ChatActivity extends AppCompatActivity implements OnClickListener,
		OnSharedPreferenceChangeListener, MessageProcessor {
	SharedPreferences prefs;
	Button btnSubmit;
	Button tosetting;
	LinearLayout textlayout;
	EditText editTextUser;
	EditText editTextDomain;
	EditText editTextTo;
	EditText editTextMessage;
	TextView textViewChat;
	String chatText = "";
	SipProfile sipProfile;
	private final String FILE_NAME = "config.ini";
	private String userPhone = null;
	private UserInfo user= null;
	private UserInfo guardian = null;
	DeviceImpl device;
	ArrayAdapter<String> pairedDevicesArrayAdapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		sipProfile = new SipProfile();//定义了sip的相关属性，包括sip账户、域名和服务器信息
        HashMap<String, String> customHeaders = new HashMap<>();//定制的头
        customHeaders.put("customHeader1","customValue1");
        customHeaders.put("customHeader2","customValue2");
        DeviceImpl.getInstance().Initialize(getApplicationContext(), sipProfile,customHeaders);//单例模式创建DeviceImpl的实例
		//////
		Button btnRegister = (Button)findViewById(R.id.btnSubmit);
		Button tosetting = (Button)findViewById(R.id.tosetting);
		try {
			loadPreferencesFile();
		} catch (IOException e) {
			e.printStackTrace();
		}


		DBAdapter db = new DBAdapter();
		System.out.println("the user phone is "+ userPhone);
		user=db.queryUserInfo(userPhone);
		user.setIp(getLocalIpAddress(ChatActivity.this));
		db.updateuserip(user);
		System.out.println("信息发送人"+user.getPhone());



		DBAdapter db1 = new DBAdapter();
		guardian = db1.queryUserInfo(user.getGuardian());
		System.out.println("信息接收人"+guardian.getPhone());


		tosetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(ChatActivity.this, SettingsActivity.class);
				//startActivity(intent);
				//intent.putExtra("data",userInfo.getPhone());
				//startActivityForResult(intent,3);
				startActivity(intent);

			}
		});
		btnRegister.setOnClickListener(this);
		Button btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(this);
		Button btnCall = (Button) findViewById(R.id.btnCall);
		btnCall.setOnClickListener(this);
		textlayout=(LinearLayout)findViewById(R.id.textLayout);
		editTextTo = (EditText) findViewById(R.id.editTextTo);
		editTextMessage = (EditText) findViewById(R.id.editTextMessage);
		textViewChat = (TextView) findViewById(R.id.textViewChat);
		textViewChat.setMovementMethod(new ScrollingMovementMethod());
		// ////////////////////////////////////////////////////////////\
		DeviceImpl.getInstance().setMessageProcessor(this);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		pairedDevicesArrayAdapter =
				new ArrayAdapter<String>(this, R.layout.record);



		// Find and set up the ListView for paired devices
		ListView pairedListView = (ListView) findViewById(R.id.chatrecord);
		pairedListView.setDivider(null);
		pairedListView.setAdapter(pairedDevicesArrayAdapter);


		// register preference change listener
		prefs.registerOnSharedPreferenceChangeListener(this);



	}

	@Override
	protected void onStart() {
		super.onStart();
		initializeSipFromPreferences();

		DeviceImpl.getInstance().Register();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.btnSubmit):
			DeviceImpl.getInstance().Register();
			break;
		case (R.id.btnCall):

			DeviceImpl.getInstance().Call(editTextTo.getText().toString());
		
			break;
		case (R.id.btnSend):

//			DeviceImpl.getInstance().SendMessage(editTextTo.getText().toString(), editTextMessage.getText().toString() );
			System.out.println("sip:"+guardian.getSipid()+"@"+guardian.getIp());
			DeviceImpl.getInstance().SendMessage("sip:"+guardian.getSipid()+"@"+guardian.getIp(), editTextMessage.getText().toString() );
			System.out.println("cyy:从发送信息的文本栏中获取的信息为"+editTextMessage.getText().toString());
//			TextView child = new TextView(this);
//			child.setText("发送： "+editTextMessage.getText().toString());
//			textlayout.addView(child);
			//textViewChat.setText("发送"+editTextMessage.getText().toString()+"\n"+editTextMessage.getText().toString());
			textViewChat.append("我:\n"+editTextMessage.getText().toString()+"\n");
//			pairedDevicesArrayAdapter.add("发送"+editTextMessage.getText().toString());
			//System.out.println(getLocalIpAddress(ChatActivity.this));
			break;
		}
	}



	public static boolean checkEnable(Context paramContext) {
		boolean i = false;
		@SuppressLint("WrongConstant") NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
				.getSystemService("connectivity")).getActiveNetworkInfo();
		if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
			return true;
		return false;
	}

	/**
	 * 将ip的整数形式转换成ip形式
	 *
	 * @param ipInt
	 * @return
	 */
	public static String int2ip(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	/**
	 * 获取当前ip地址
	 *
	 * @param context
	 * @return
	 */
	public static String getLocalIpAddress(Context context) {
		try {

			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int i = wifiInfo.getIpAddress();
			return int2ip(i);
		} catch (Exception ex) {
			return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
		}
		// return null;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("pref_proxy_ip")) {
//			sipProfile.setRemoteIp((prefs.getString("pref_proxy_ip", "")));
			sipProfile.setRemoteIp("10.128.206.204");
		} else if (key.equals("pref_proxy_port")) {
//			sipProfile.setRemotePort(Integer.parseInt(prefs.getString(
//					"pref_proxy_port", "5060")));
			sipProfile.setRemotePort(5060);
		}  else if (key.equals("pref_sip_user")) {
//			sipProfile.setSipUserName(prefs.getString("pref_sip_user",
//					"alice"));
			sipProfile.setSipUserName(user.getSipid());
		} else if (key.equals("pref_sip_password")) {
//			sipProfile.setSipPassword(prefs.getString("pref_sip_password",
//					"1234"));
			sipProfile.setSipPassword("100");
		}

	}

	@SuppressWarnings("static-access")
	private void initializeSipFromPreferences() {
//		sipProfile.setRemoteIp((prefs.getString("pref_proxy_ip", "")));
		sipProfile.setRemoteIp("10.128.206.204");
//		sipProfile.setRemotePort(Integer.parseInt(prefs.getString(
//				"pref_proxy_port", "5060")));
		sipProfile.setRemotePort(5060);
//		sipProfile.setSipUserName(prefs.getString("pref_sip_user", "alice"));
		sipProfile.setSipUserName(user.getSipid());
		System.out.println("ffffffff"+user.getSipid());
//		sipProfile.setSipPassword(prefs
//				.getString("pref_sip_password", "1234"));
		sipProfile.setSipPassword("100");

	}

	@Override
	public void processWhisperMessage(String sender, String message) {

		System.out.println("cyy:activity receive message");
		System.out.println(message);
//		pairedDevicesArrayAdapter.add("接受"+message);
//		pairedDevicesArrayAdapter.notifyDataSetChanged();

//		TextView child = new TextView(this);
//		child.setText("收到消息： "+message);
//		textlayout.addView(child);
		textViewChat.append("对方\n"+message+"\n");
	}

	@Override
	public void processGroupMessage(String sender, String message) {

	}

	@Override
	public void processError(String errorMessage) {

	}

	@Override
	public void processInfo(String infoMessage) {

	}

	private void loadPreferencesFile() throws IOException {
		try {
			FileInputStream fis = openFileInput(FILE_NAME);
			if (fis.available() == 0) {
				return;
			}
			byte[] readBytes = new byte[fis.available()];
			while (fis.read(readBytes) != -1) {
			}
			userPhone = new String(readBytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
