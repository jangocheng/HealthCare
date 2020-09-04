package bupt.mxly.healthcare.ui.smartdev;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.card.MaterialCardView;

import bupt.mxly.healthcare.R;
import bupt.mxly.healthcare.addDevice;

public class SmartdevFragment extends Fragment {

    private SmartdevViewModel smartdevViewModel;
    MaterialCardView materialCardView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        smartdevViewModel =
                ViewModelProviders.of(this).get(SmartdevViewModel.class);
        View root = inflater.inflate(R.layout.fragment_smartdev, container, false);
        materialCardView = root.findViewById(R.id.addDev);
        materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(getActivity(), addDevice.class);
                    startActivity(intent);
                } catch (Exception ex) {
                    // 显示异常
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }
}