package bupt.mxly.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import bupt.mxly.healthcare.db.DBAdapter;
import bupt.mxly.healthcare.db.UserInfo;

import static bupt.mxly.healthcare.ModifyUI.setFitSystemWindow;
import static bupt.mxly.healthcare.ModifyUI.setStatusBarFullTransparent;
import static bupt.mxly.healthcare.ModifyUI.setStatusBarLightMode;

public class RegisterActivity extends AppCompatActivity {
    private Button insertbutton;
    private Button back;
    private TextInputEditText phonetoinsert;
    private TextInputEditText pwdtoinsert;
    private TextInputEditText nametoinsert;
    private TextInputEditText agetoinsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        setStatusBarFullTransparent(RegisterActivity.this);
        setFitSystemWindow(true, RegisterActivity.this);
        setStatusBarLightMode(this, true);

        back = findViewById(R.id.btn_back);
        insertbutton = findViewById(R.id.bt_insert);
        phonetoinsert = findViewById(R.id.phonetoinsert);
        pwdtoinsert = findViewById(R.id.pwdtoinsert);
        nametoinsert = findViewById(R.id.nametoinsert);
        agetoinsert = findViewById(R.id.agetoinsert);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        final RC4 rc4 = new RC4();
        //rc4算法初始化
        rc4.key = "WhQpVccuUyCblNuSmk6NXE3INAICmKdJpTFQUd5jYUSTg0thV58Kqrjk1KaLq0xZbJqAjhmr5lFzgCrbh4U6j2p5NarTW02YDv4QxkqhjbbH5SdzXuNt5xU4pEYHnM9Wkg34Sa1OU9zCNZk1tefeDrfBNR6419n3QdBPcESkJcXcsUzHws0gHDpHRzqPI0KRJd5s58Zc8vgQYFuT6GpWLbdrgOoV74Yj5mroMyGFW6DhMT8anvWRZLiFtWrbfR8d".toCharArray();
        rc4.initSbox();
        insertbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DBAdapter db = new DBAdapter();
                UserInfo info = new UserInfo();
                info.setPhone(phonetoinsert.getText().toString());
                info.setName(nametoinsert.getText().toString());
                info.setPwd(rc4.crypt(pwdtoinsert.getText().toString()));
                info.setAge(Integer.parseInt(agetoinsert.getText().toString()));
//                info.setHeight(Double.parseDouble(heightroinsert.getText().toString()));
//                info.setWeight(Double.parseDouble(weighttoinsert.getText().toString()));
//                info.setSex(sextoinsert.getText().toString());
//                info.setBlood(bloodtoinsert.getText().toString());
//                info.setHistory(historytoinsert.getText().toString());
//                info.setAddress(addresstoinsert.getText().toString());
                db.insertUserInfo(info);

                Intent intent=new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("userinfo",info);
                setResult(1,intent);
                finish();
            }
        });
    }
}
