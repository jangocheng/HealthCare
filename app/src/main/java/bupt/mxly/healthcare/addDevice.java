package bupt.mxly.healthcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import bupt.mxly.healthcare.connect.WiFiServiceDiscoveryActivity;
import bupt.mxly.healthcare.connect.connViaBluetooth;

import static bupt.mxly.healthcare.ModifyUI.setFitSystemWindow;
import static bupt.mxly.healthcare.ModifyUI.setStatusBar;
import static bupt.mxly.healthcare.ModifyUI.setStatusBarFullTransparent;
import static bupt.mxly.healthcare.ModifyUI.setStatusBarLightMode;

public class addDevice extends AppCompatActivity {

    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
//    private BluetoothService mChatService = null;


    Button btn_back;
    MaterialCardView btn_blt;
    MaterialCardView btn_wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        setStatusBar(addDevice.this, true, true);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        btn_blt = findViewById(R.id.bluetooth);
        btn_blt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(addDevice.this, connViaBluetooth.class);
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE_SECURE);
                } catch (Exception ex) {
                    // 显示异常
                    Toast.makeText(addDevice.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_wifi = findViewById(R.id.wifi);
        btn_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(addDevice.this, WiFiServiceDiscoveryActivity.class);
                    addDevice.this.startActivity(intent);
                } catch (Exception ex) {
                    Toast.makeText(addDevice.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}