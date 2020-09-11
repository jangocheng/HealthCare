package bupt.mxly.healthcare.ui.about;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.card.MaterialCardView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import bupt.mxly.healthcare.LoginActivity;
import bupt.mxly.healthcare.R;
import bupt.mxly.healthcare.UpdateActivity;
import bupt.mxly.healthcare.db.DBAdapter;
import bupt.mxly.healthcare.db.Sipserver;
import bupt.mxly.healthcare.db.UserInfo;

public class AboutFragment extends Fragment {

    private AboutViewModel aboutViewModel;
    //Button toinformation;
    Button tologin;
    Button toregister;
    MaterialCardView logout;
    LinearLayout inforlayout;
    TextView username;
    TextView showage;
    TextView showheight;
    TextView showweight;
    TextView showsex;
    TextView showblood;
    TextView showhistory;
    TextView showaddress;
    MaterialCardView toupdate;
    UserInfo userInfo;

    String userPhone = "";

    private final String FILE_NAME = "config.ini";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        aboutViewModel =
                ViewModelProviders.of(this).get(AboutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_about, container, false);
        inforlayout = (LinearLayout) root.findViewById(R.id.infolayout);
        username = (TextView) root.findViewById(R.id.username);
        showage = (TextView) root.findViewById(R.id.showage);
        showheight = (TextView) root.findViewById(R.id.showheight);
        showweight = (TextView) root.findViewById(R.id.showweight);
        showsex = (TextView) root.findViewById(R.id.showsex);
        showblood = (TextView) root.findViewById(R.id.showblood);
        showhistory = (TextView) root.findViewById(R.id.showhisrory);
        showaddress = (TextView) root.findViewById(R.id.showaddress);
        toupdate=(MaterialCardView)root.findViewById(R.id.bt_update);
        logout = (MaterialCardView) root.findViewById(R.id.bt_logout);

        toupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), UpdateActivity.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBAdapter db = new DBAdapter();
                UserInfo user= new UserInfo();
                user = db.queryUserInfo(userPhone);//根据从sharepreference得到的userphone检索user信息
                String sipid = user.getSipid();//获取用户占用的sipid
                //解除sipid的占用状态
                Sipserver sip = db.querySipserver(sipid);
                sip.setOccupied(0);
                db.updatesip(sip);
                //重置用户的sipid
                user.setSipid(" ");
                db.updateusersip(user);

                logout.setVisibility(getView().GONE);
                username.setVisibility(getView().GONE);
                inforlayout.setVisibility(getView().GONE);
                try {
                    savePreferenceFiles("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        try {
            loadPreferencesFile();
            System.out.println("用户id:" + userPhone);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DBAdapter dbAdapter = new DBAdapter();
        if (!userPhone.isEmpty()) {
            username.setVisibility(getView().VISIBLE);
//            username.setText(dbAdapter.queryUserInfo(userPhone).getName());
//            tologin.setVisibility(getView().GONE);
//            toregister.setVisibility(getView().GONE);
            logout.setVisibility(getView().VISIBLE);
            inforlayout.setVisibility(getView().VISIBLE);
//            showage.setText(String.valueOf(dbAdapter.queryUserInfo(userPhone).getAge()));
//            showheight.setText(String.valueOf(dbAdapter.queryUserInfo(userPhone).getHeight()));
//            showweight.setText(String.valueOf(dbAdapter.queryUserInfo(userPhone).getWeight()));
//            showsex.setText(dbAdapter.queryUserInfo(userPhone).getSex());
//            showblood.setText(dbAdapter.queryUserInfo(userPhone).getBlood());
//            showhistory.setText(dbAdapter.queryUserInfo(userPhone).getHistory());
//            showaddress.setText(dbAdapter.queryUserInfo(userPhone).getAddress());
        }


        return root;
    }

    @Override
    public void onStart() {

        super.onStart();
        DBAdapter dbAdapter = new DBAdapter();
        username.setText(dbAdapter.queryUserInfo(userPhone).getName());
        username.setText(dbAdapter.queryUserInfo(userPhone).getName());
        showage.setText(String.valueOf(dbAdapter.queryUserInfo(userPhone).getAge()));
        showheight.setText(String.valueOf(dbAdapter.queryUserInfo(userPhone).getHeight()));
        showweight.setText(String.valueOf(dbAdapter.queryUserInfo(userPhone).getWeight()));
        showsex.setText(dbAdapter.queryUserInfo(userPhone).getSex());
        showblood.setText(dbAdapter.queryUserInfo(userPhone).getBlood());
        showhistory.setText(dbAdapter.queryUserInfo(userPhone).getHistory());
        showaddress.setText(dbAdapter.queryUserInfo(userPhone).getAddress());
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        userInfo = (UserInfo) data.getSerializableExtra("userinfo");
        System.out.println(userInfo.getName());
        try {
            savePreferenceFiles(userInfo.getPhone());
        } catch (IOException e) {
            e.printStackTrace();
        }
        username.setVisibility(getView().VISIBLE);
        username.setText("欢迎您: " + userInfo.getName());
//        tologin.setVisibility(getView().GONE);
//        toregister.setVisibility(getView().GONE);
        logout.setVisibility(getView().VISIBLE);
        inforlayout.setVisibility(getView().VISIBLE);
        showage.setText(String.valueOf(userInfo.getAge()));
        showheight.setText(String.valueOf(userInfo.getHeight()));
        showweight.setText(String.valueOf(userInfo.getWeight()));
        showsex.setText(userInfo.getSex());
        showblood.setText(userInfo.getBlood());
        showhistory.setText(userInfo.getHistory());
        showaddress.setText(userInfo.getAddress());
    }

    private void savePreferenceFiles(String phone) throws IOException {
        FileOutputStream fos = getActivity().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        fos.write(phone.getBytes());
        fos.flush();
        fos.close();
    }

    private void loadPreferencesFile() throws IOException {
        try {
            FileInputStream fis = getActivity().openFileInput(FILE_NAME);
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