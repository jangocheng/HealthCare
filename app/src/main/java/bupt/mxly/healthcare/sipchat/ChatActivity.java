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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import bupt.mxly.healthcare.R;
import bupt.mxly.healthcare.db.DBAdapter;
import bupt.mxly.healthcare.db.UserInfo;
import bupt.mxly.healthcare.sipchat.jsip_ua.SipProfile;
import bupt.mxly.healthcare.sipchat.jsip_ua.impl.DeviceImpl;
import bupt.mxly.healthcare.sipchat.jsip_ua.impl.MessageProcessor;

import static bupt.mxly.healthcare.ModifyUI.setStatusBar;



public class ChatActivity extends AppCompatActivity implements OnClickListener,
		OnSharedPreferenceChangeListener, MessageProcessor {
	public static String serverip="10.128.206.204";
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
	private Button btn_back;
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
		setStatusBar(ChatActivity.this, true, true);

		btn_back = findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

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



		Button btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(this);
		textlayout=(LinearLayout)findViewById(R.id.textLayout);
		editTextMessage = (EditText) findViewById(R.id.editTextMessage);
		textViewChat = (TextView) findViewById(R.id.textViewChat);
		textViewChat.setMovementMethod(new ScrollingMovementMethod());
		// ////////////////////////////////////////////////////////////\
		DeviceImpl.getInstance().setMessageProcessor(this);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		pairedDevicesArrayAdapter =
				new ArrayAdapter<String>(this, R.layout.record);

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
		case (R.id.btnSend):

			Date date = new Date();
			String timeStamp = String.valueOf(date.getTime());
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));      // 时间戳转换成时间
			System.out.println("sip:"+guardian.getSipid()+"@"+guardian.getIp());
			DeviceImpl.getInstance().SendMessage("sip:"+guardian.getSipid()+"@"+guardian.getIp(), editTextMessage.getText().toString() );
			System.out.println("cyy:从发送信息的文本栏中获取的信息为"+editTextMessage.getText().toString());

			textViewChat.append(user.getName()+" "+sd+":\n"+editTextMessage.getText().toString()+"\n");

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
			return " 获取IP出错!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
		}
		// return null;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("pref_proxy_ip")) {
			sipProfile.setRemoteIp("10.128.206.204");
		} else if (key.equals("pref_proxy_port")) {
			sipProfile.setRemotePort(5060);
		}  else if (key.equals("pref_sip_user")) {
			sipProfile.setSipUserName(user.getSipid());
		} else if (key.equals("pref_sip_password")) {
			sipProfile.setSipPassword("100");
		}

	}

	@SuppressWarnings("static-access")
	private void initializeSipFromPreferences() {
		sipProfile.setRemoteIp("10.128.206.204");
		sipProfile.setRemotePort(5060);
		sipProfile.setSipUserName(user.getSipid());
		sipProfile.setSipPassword("100");

	}

	@Override
	public void processWhisperMessage(String sender, String message) {
		Date date = new Date();
		String timeStamp = String.valueOf(date.getTime());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));      // 时间戳转换成时间
		System.out.println("cyy:activity receive message");
		System.out.println(message);

		textViewChat.append(guardian.getName()+" "+sd+":\n"+message);
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
