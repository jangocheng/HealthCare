package bupt.mxly.healthcare.ui.contact;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import bupt.mxly.healthcare.MainActivity;
import bupt.mxly.healthcare.R;
import bupt.mxly.healthcare.sipchat.ChatActivity;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ContactFragment extends Fragment {

    private ContactViewModel contactViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contactViewModel =
                ViewModelProviders.of(this).get(ContactViewModel.class);
        View root = inflater.inflate(R.layout.fragment_contact, container, false);

        root.findViewById(R.id.btn_call_110).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent110 = new Intent(Intent.ACTION_DIAL);
                Uri data1 = Uri.parse("tel:110");
                String id = "channel_01";
                // 发送通知
                NotificationManager notificationManager =
                        (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                //用户可以看到的通知渠道的名字
                CharSequence name = "TimeMaster";
                //用户可看到的通知描述
                String description = "TimeMaster";
                //构建NotificationChannel实例
                NotificationChannel notificationChannel =
                        new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
                //配置通知渠道的属性
                notificationChannel.setDescription(description);
                //在notificationManager中创建通知渠道
                notificationManager.createNotificationChannel(notificationChannel);

                Notification notification = new NotificationCompat.Builder(getActivity(), id)
                        //指定通知的标题内容
                        .setContentTitle("老人健康预警")
                        //设置通知的内容
                        .setContentText("血糖高于8mmol/g！")
                        .setSmallIcon(R.drawable.ic_baseline_lock_24)
                        //指定通知被创建的时间
                        .setWhen(System.currentTimeMillis())
                        .build();

                notificationManager.notify(1, notification);

                intent110.setData(data1);
                startActivity(intent110);
            }
        });

        root.findViewById(R.id.btn_call_120).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent120 = new Intent(Intent.ACTION_DIAL);
                Uri data2 = Uri.parse("tel:120");
                intent120.setData(data2);
                startActivity(intent120);
            }
        });

        root.findViewById(R.id.btn_call_119).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent119 = new Intent(Intent.ACTION_DIAL);
                Uri data3 = Uri.parse("tel:119");
                intent119.setData(data3);
                startActivity(intent119);
            }
        });

        root.findViewById(R.id.btn_call_child).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent_elder = new Intent(Intent.ACTION_DIAL);
//                Uri data4 = Uri.parse("tel:18810059259");
//                intent_elder.setData(data4);
//                startActivity(intent_elder);
                Intent i = new Intent(getActivity(), ChatActivity.class);
                startActivity(i);
            }
        });
        return root;
    }
}