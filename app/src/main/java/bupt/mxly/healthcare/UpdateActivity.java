package bupt.mxly.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import bupt.mxly.healthcare.db.DBAdapter;
import bupt.mxly.healthcare.db.UserInfo;

import static bupt.mxly.healthcare.ModifyUI.setFitSystemWindow;
import static bupt.mxly.healthcare.ModifyUI.setStatusBarFullTransparent;
import static bupt.mxly.healthcare.ModifyUI.setStatusBarLightMode;

public class UpdateActivity extends AppCompatActivity {
    private Button insertbutton;
    private EditText phonetoinsert;
    private EditText pwdtoinsert;
    private EditText pwdtoverify;
    private EditText nametoinsert;
    private EditText agetoinsert;
    private EditText heightroinsert;
    private EditText weighttoinsert;
    private EditText sextoinsert;
    private EditText bloodtoinsert;
    private EditText historytoinsert;
    private EditText addresstoinsert;
    private EditText guardiantoinsert;
    String phone;

    String userPhone = "";
    private final String FILE_NAME = "config.ini";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update);
        insertbutton = (Button)findViewById(R.id.bt_insert);
        phonetoinsert = (EditText) findViewById(R.id.phonetoinsert);
        pwdtoinsert = (EditText) findViewById(R.id.pwdtoinsert);
        pwdtoverify=(EditText)findViewById(R.id.pwdtoverify);
        nametoinsert = (EditText) findViewById(R.id.nametoinsert);
        agetoinsert = (EditText) findViewById(R.id.agetoinsert);
        heightroinsert = (EditText) findViewById(R.id.heighttoinsert);
        weighttoinsert = (EditText) findViewById(R.id.weighttoinsert);
        sextoinsert = (EditText) findViewById(R.id.sextoinsert);
        bloodtoinsert = (EditText) findViewById(R.id.bloodtoinsert);
        historytoinsert = (EditText) findViewById(R.id.historytoinsert);
        addresstoinsert = (EditText) findViewById(R.id.addresstoinsert);
        guardiantoinsert = (EditText) findViewById(R.id.guardiantoinsert);

        setStatusBarFullTransparent(UpdateActivity.this);
        setFitSystemWindow(true, UpdateActivity.this);
        setStatusBarLightMode(this, true);

        phone = getIntent().getStringExtra("data");

    }

    @Override
    protected void onStart() {
        super.onStart();
        final RC4 rc4 = new RC4();
        //rc4算法初始化
        rc4.key = "WhQpVccuUyCblNuSmk6NXE3INAICmKdJpTFQUd5jYUSTg0thV58Kqrjk1KaLq0xZbJqAjhmr5lFzgCrbh4U6j2p5NarTW02YDv4QxkqhjbbH5SdzXuNt5xU4pEYHnM9Wkg34Sa1OU9zCNZk1tefeDrfBNR6419n3QdBPcESkJcXcsUzHws0gHDpHRzqPI0KRJd5s58Zc8vgQYFuT6GpWLbdrgOoV74Yj5mroMyGFW6DhMT8anvWRZLiFtWrbfR8d".toCharArray();
        rc4.initSbox();
        try {
            loadPreferencesFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        insertbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pwdtoinsert.getText().toString().equals(pwdtoverify.getText().toString())){
                    DBAdapter db = new DBAdapter();
                    UserInfo info = new UserInfo();
                    info.setPhone(userPhone);
//                    System.out.println(phone);
                    info.setName(nametoinsert.getText().toString());
                    info.setPwd(rc4.crypt(pwdtoinsert.getText().toString()));
                    info.setAge(Integer.parseInt(agetoinsert.getText().toString()));
                    info.setHeight(Double.parseDouble(heightroinsert.getText().toString()));
                    info.setWeight(Double.parseDouble(weighttoinsert.getText().toString()));
                    info.setSex(sextoinsert.getText().toString());
                    info.setBlood(bloodtoinsert.getText().toString());
                    info.setHistory(historytoinsert.getText().toString());
                    info.setAddress(addresstoinsert.getText().toString());

                    db.updateUserInfo(info);

                    Intent intent=new Intent(UpdateActivity.this, MainActivity.class);
                    intent.putExtra("userinfo",info);
//                    setResult(1,intent);
                    finish();
                }
                else{
                    Toast toast=Toast.makeText(UpdateActivity.this,"两次输入密码不一致！请您重新输入",Toast.LENGTH_SHORT    );
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    pwdtoinsert.setText("");
                    pwdtoverify.setText("");
                }


            }
        });
    }

    private void loadPreferencesFile() throws IOException {
        try {
            FileInputStream fis = UpdateActivity.this.openFileInput(FILE_NAME);
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
