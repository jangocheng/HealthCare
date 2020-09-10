package bupt.mxly.healthcare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import bupt.mxly.healthcare.db.DBAdapter;
import bupt.mxly.healthcare.db.Sipserver;
import bupt.mxly.healthcare.db.UserInfo;

import static bupt.mxly.healthcare.ModifyUI.setStatusBar;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText phone_login;
    TextInputEditText pwd_login;
    Button bt_login;
    Button btn_back;
    Button bt_signup;
    TextView login_result;
    LinearLayout actionBar;

    private final String FILE_NAME = "config.ini";
    private String userPhone = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        phone_login = (TextInputEditText) findViewById(R.id.phone_login);
        pwd_login = (TextInputEditText) findViewById(R.id.pwd_login);
        bt_login = (Button) findViewById(R.id.bt_login);
        login_result = (TextView) findViewById(R.id.login_result);
        actionBar = findViewById(R.id.back);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        bt_signup = findViewById(R.id.bt_signup);
        bt_signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        try {
            loadPreferencesFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (userPhone == null) {
            actionBar.setVisibility(View.GONE);
        }


        setStatusBar(LoginActivity.this, true, true);
    }

    @Override
    protected void onStart() {
        final RC4 rc4 = new RC4();
        //rc4算法初始化
        rc4.key = "WhQpVccuUyCblNuSmk6NXE3INAICmKdJpTFQUd5jYUSTg0thV58Kqrjk1KaLq0xZbJqAjhmr5lFzgCrbh4U6j2p5NarTW02YDv4QxkqhjbbH5SdzXuNt5xU4pEYHnM9Wkg34Sa1OU9zCNZk1tefeDrfBNR6419n3QdBPcESkJcXcsUzHws0gHDpHRzqPI0KRJd5s58Zc8vgQYFuT6GpWLbdrgOoV74Yj5mroMyGFW6DhMT8anvWRZLiFtWrbfR8d".toCharArray();
        rc4.initSbox();
        super.onStart();

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DBAdapter db = new DBAdapter();

                UserInfo userinf = db.queryUserInfo(phone_login.getText().toString());
                String correctpwd = rc4.crypt(userinf.getPwd());
//                String correctpwd = userinf.getPwd();
                String pwd = pwd_login.getText().toString();
                if (correctpwd.equals(pwd)) {
//                    login_result.setText("登录成功");
                    try {
                        savePreferenceFiles(userinf.getPhone());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //自动分配sipid,检索数据库中空闲的sipid,自动分配最后一条给user
                    Sipserver sip;
                    DBAdapter sipdb = new DBAdapter();
                    sip=sipdb.querySipserver();//检索空闲sipid
                    userinf.setSipid(sip.getSipid());//为user分配sipid
                    sipdb.updateusersip(userinf);//更新用户信息，将其sipid保存到数据库
                    sip.setOccupied(1);//更改sipid的状态为占用
                    sipdb.updatesip(sip);//保存sipid的状态到数据库

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userinfo", userinf);
                    startActivity(intent);
//                    setResult(1, intent);
                    finish();
                } else {
                    login_result.setText("登录失败");
                }
            }
        });
    }

    private void savePreferenceFiles(String phone) throws IOException {
        FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        fos.write(phone.getBytes());
        fos.flush();
        fos.close();
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
