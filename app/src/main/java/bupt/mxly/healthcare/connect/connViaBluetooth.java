package bupt.mxly.healthcare.connect;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fingerth.supdialogutils.SYSDiaLogUtils;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import bupt.mxly.healthcare.MainActivity;
import bupt.mxly.healthcare.R;
import bupt.mxly.healthcare.addDevice;

public class connViaBluetooth extends AppCompatActivity {

    private static final String TAG = "error";
    Button btn_back;
    private final static int REQUEST_ENABLE_BT = 1; //用于打开手机蓝牙
    private final static int HEALTH_PROFILE_SOURCE_DATA_TYPE = 0x1007; //IEEE血压数据类型
    private BluetoothAdapter mBluetoothAdapter;
    // 新发现设备列表
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothSocket mBluetoothSocket;

    private String mConnectedDeviceName = null;

    private BluetoothChatService mChatService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conn_via_bluetooth);

        setStatusBarFullTransparent();
        setFitSystemWindow(true);
        setStatusBarLightMode(this, true);

        getPermission();

        btn_back = findViewById(R.id.btn_back2addDev);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        ArrayAdapter<String> pairedDevicesArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.old_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevices);
        }


        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "当前设备不支持蓝牙！", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            doDiscovery();
        }

        doDiscovery();

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

    }


    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            SYSDiaLogUtils.showSuccessDialog(
                                    connViaBluetooth.this, "连接成功",
                                    "确认", false);

                            // 延时3秒返回添加设备页面
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(connViaBluetooth.this, addDevice.class);
                                    startActivity(intent);
                                    finish();
                                }
                            },3000);      //8000为毫秒单位
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Toast toast2 = Toast.makeText(
                                    connViaBluetooth.this,
                                    "连接中……", Toast.LENGTH_SHORT);
                            toast2.show();
                            SYSDiaLogUtils.showProgressDialog(connViaBluetooth.this, SYSDiaLogUtils.SYSDiaLogType.IosType, "正在连接设备...", false, new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    doDiscovery();
                                }
                            });
                    }
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    // 数据格式：yyyy/mm/dd-TYPE-data
                    String[] healthData = readMessage.split("-");


                    Toast toast4 = Toast.makeText(
                            connViaBluetooth.this,
                            readMessage, Toast.LENGTH_LONG);
                    toast4.show();

            }
        }
    };


    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        // Indicate scanning in the title
        setTitle("正在搜寻");
        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * The on-click listener for all devices in the ListViews
     */
    private final AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        @SuppressLint("WrongConstant")
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBluetoothAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            mConnectedDeviceName = info;

            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

            // 连接设备
            mChatService.connect(device, true);

        }
    };


    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setTitle("选择需要连接的设备");
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    //String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add("No device found");
                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }


    /**
     * 全透状态栏
     */
    protected void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 如果需要内容紧贴着StatusBar
     * 应该在对应的xml布局文件中，设置根布局fitsSystemWindows=true。
     */
    private View contentViewGroup;

    protected void setFitSystemWindow(boolean fitSystemWindow) {
        if (contentViewGroup == null) {
            contentViewGroup = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        }
        contentViewGroup.setFitsSystemWindows(fitSystemWindow);
    }

    /**
     * 让状态栏字体颜色变深
     *
     * @param activity
     * @param isLightMode
     */
    public static void setStatusBarLightMode(Activity activity, boolean isLightMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            int option = window.getDecorView().getSystemUiVisibility();
            if (isLightMode) {
                option |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                option &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            window.getDecorView().setSystemUiVisibility(option);
        }
    }


    /**
     * 解决：无法发现蓝牙设备的问题
     * <p>
     * 对于发现新设备这个功能, 还需另外两个权限(Android M 以上版本需要显式获取授权,附授权代码):
     */
    private final int ACCESS_LOCATION = 1;

    @SuppressLint("WrongConstant")
    private void getPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int permissionCheck = 0;
            permissionCheck = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                //未获得权限
                this.requestPermissions( // 请求授权
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        ACCESS_LOCATION);// 自定义常量,任意整型
            }
        }
    }

    /**
     * 请求权限的结果回调。每次调用 requestpermissions（string[]，int）时都会调用此方法。
     *
     * @param requestCode  传入的请求代码
     * @param permissions  传入permissions的要求
     * @param grantResults 相应权限的授予结果:PERMISSION_GRANTED 或 PERMISSION_DENIED
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ACCESS_LOCATION:
                if (hasAllPermissionGranted(grantResults)) {
                    Log.i(TAG, "onRequestPermissionsResult: 用户允许权限");
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: 拒绝搜索设备权限");
                }
                break;
        }
    }

    private boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

}