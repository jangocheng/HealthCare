package bupt.mxly.healthcare.ui.health;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import bupt.mxly.healthcare.R;
import tech.linjiang.suitlines.SuitLines;
import tech.linjiang.suitlines.Unit;

public class HealthFragment extends Fragment {

    private HealthViewModel healthViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        healthViewModel =
                ViewModelProviders.of(this).get(HealthViewModel.class);
        View root = inflater.inflate(R.layout.fragment_health, container, false);
        SuitLines suitLines=root.findViewById(R.id.suitlines);
        List<Unit> lines = new ArrayList<>();
        SecureRandom a = new SecureRandom();
        for (int i = 0; i < 14; i++) {
            lines.add(new Unit(110+a.nextInt(20), i + ""));}
        suitLines.feedWithAnim(lines);
        return root;
    }

}