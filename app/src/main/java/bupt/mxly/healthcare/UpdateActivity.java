package bupt.mxly.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
    private Button back;
    private TextInputEditText pwdtoinsert;
    private TextInputEditText pwdtoverify;
    private TextInputEditText nametoinsert;
    private TextInputEditText agetoinsert;
    private TextInputEditText heightroinsert;
    private TextInputEditText weighttoinsert;

    private TextInputEditText bloodtoinsert;
    private TextInputEditText historytoinsert;
    private TextInputEditText addresstoinsert;
    private TextInputEditText guardiantoinsert;
    String phone;
    UserInfo defaultinfo = new UserInfo();

    String userPhone = "";
    private final String FILE_NAME = "config.ini";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update);
        back = findViewById(R.id.btn_back);
        insertbutton = findViewById(R.id.bt_insert);
        pwdtoinsert = findViewById(R.id.pwdtoinsert);
        pwdtoverify=findViewById(R.id.pwdtoverify);
        nametoinsert = findViewById(R.id.nametoinsert);
        agetoinsert = findViewById(R.id.agetoinsert);
        heightroinsert = findViewById(R.id.heighttoinsert);
        weighttoinsert = findViewById(R.id.weighttoinsert);
        bloodtoinsert = findViewById(R.id.bloodtoinsert);
        historytoinsert = findViewById(R.id.historytoinsert);
        addresstoinsert = findViewById(R.id.addresstoinsert);
        guardiantoinsert = findViewById(R.id.guardiantoinsert);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setStatusBarFullTransparent(UpdateActivity.this);
        setFitSystemWindow(true, UpdateActivity.this);
        setStatusBarLightMode(this, true);

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

        DBAdapter dba=new DBAdapter();
        defaultinfo = dba.queryUserInfo(userPhone);
        nametoinsert.setText(defaultinfo.getName());
        agetoinsert.setText(String.valueOf(defaultinfo.getAge()));
        heightroinsert.setText(String.valueOf(defaultinfo.getHeight()));
        weighttoinsert.setText(String.valueOf(defaultinfo.getWeight()));
        bloodtoinsert.setText(defaultinfo.getBlood());
        historytoinsert.setText(defaultinfo.getHistory());
        addresstoinsert.setText(defaultinfo.getAddress());
        guardiantoinsert.setText(defaultinfo.getGuardian());

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
                    info.setSex(defaultinfo.getSex());
                    info.setBlood(bloodtoinsert.getText().toString());
                    info.setHistory(historytoinsert.getText().toString());
                    info.setAddress(addresstoinsert.getText().toString());
                    info.setGuardian(guardiantoinsert.getText().toString());
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
