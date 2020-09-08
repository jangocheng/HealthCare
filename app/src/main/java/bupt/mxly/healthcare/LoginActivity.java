package bupt.mxly.healthcare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import bupt.mxly.healthcare.db.DBAdapter;
import bupt.mxly.healthcare.db.UserInfo;

import static bupt.mxly.healthcare.ModifyUI.setFitSystemWindow;
import static bupt.mxly.healthcare.ModifyUI.setStatusBarFullTransparent;
import static bupt.mxly.healthcare.ModifyUI.setStatusBarLightMode;

public class LoginActivity extends AppCompatActivity {
    EditText phone_login;
    EditText pwd_login;
    Button bt_login;
    TextView login_result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        phone_login = (EditText)findViewById(R.id.phone_login);
        pwd_login = (EditText)findViewById(R.id.pwd_login);
        bt_login = (Button)findViewById(R.id.bt_login);
        login_result=(TextView)findViewById(R.id.login_result);

        setStatusBarFullTransparent(LoginActivity.this);
        setFitSystemWindow(true, LoginActivity.this);
        setStatusBarLightMode(this, true);

    }
    @Override
    protected void onStart() {
        final RC4 rc4 = new RC4();
        //rc4算法初始化
        rc4.key="WhQpVccuUyCblNuSmk6NXE3INAICmKdJpTFQUd5jYUSTg0thV58Kqrjk1KaLq0xZbJqAjhmr5lFzgCrbh4U6j2p5NarTW02YDv4QxkqhjbbH5SdzXuNt5xU4pEYHnM9Wkg34Sa1OU9zCNZk1tefeDrfBNR6419n3QdBPcESkJcXcsUzHws0gHDpHRzqPI0KRJd5s58Zc8vgQYFuT6GpWLbdrgOoV74Yj5mroMyGFW6DhMT8anvWRZLiFtWrbfR8d".toCharArray();
        rc4.initSbox();
        super.onStart();

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DBAdapter db = new DBAdapter();

                UserInfo userinf = db.queryUserInfo(phone_login.getText().toString());
                String correctpwd = rc4.crypt(userinf.getPwd());
                String pwd = pwd_login.getText().toString();
                if(correctpwd.equals(pwd)){
//                    login_result.setText("登录成功");

                    Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userinfo",userinf);
                    //startActivity(intent);
                    setResult(1,intent);
                    finish();
                }
                else{
                    login_result.setText("登录失败");
                }
            }
        });
    }

}
