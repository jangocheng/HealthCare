package bupt.mxly.healthcare.ui.health;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private int[] color = {Color.RED, Color.GRAY, Color.BLACK, Color.BLUE, 0xFFF76055, 0xFF9B3655, 0xFFF7A055};

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        final SuitLines suitLines = root.findViewById(R.id.suitlines);
        final SuitLines suitLines_2 = root.findViewById(R.id.suitlines_2);
        final SuitLines suitLines_3 = root.findViewById(R.id.suitlines_3);
        final SuitLines suitLines_4 = root.findViewById(R.id.suitlines_4);
//        List<Unit> lines = new ArrayList<>();

        final SecureRandom a = new SecureRandom();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<Unit> lines2 = new ArrayList<>();
                List<Unit> lines3 = new ArrayList<>();
                List<Unit> lines4 = new ArrayList<>();
                SuitLines.LineBuilder builder = new SuitLines.LineBuilder();

                List<Unit> lines = new ArrayList<>();
                for (int i = 0; i < 14; i++) {
                    lines.add(new Unit(100+ a.nextInt(60), "" + i));
                }
                builder.add(lines, new int[]{0xFFF7A055, Color.WHITE});

                List<Unit> lines5 = new ArrayList<>();
                for (int i = 0; i < 14; i++) {
                    lines5.add(new Unit(40+ a.nextInt(50), "" + i));
                }
                builder.add(lines5, new int[]{Color.GRAY, Color.WHITE});
                suitLines.setLineForm(true);
                builder.build(suitLines, true);


                for (int i = 0; i < 14; i++) {
                    lines2.add(new Unit(6 + a.nextInt(3), i + ""));
                    lines3.add(new Unit(60 + a.nextInt(40), i + ""));
                    lines4.add(new Unit(3 + a.nextInt(4), i + ""));
                }

                suitLines_2.feedWithAnim(lines2);
                suitLines_2.setLineForm(true);
                suitLines_2.setDefaultOneLineColor(0xFFF7A055, Color.WHITE);
                suitLines_3.feedWithAnim(lines3);
                suitLines_3.setLineForm(true);
                suitLines_3.setDefaultOneLineColor(0xFFF7A055, Color.WHITE);
                suitLines_4.feedWithAnim(lines4);
                suitLines_4.setLineForm(true);
                suitLines_4.setDefaultOneLineColor(0xFFF7A055, Color.WHITE);
            }
        }).start();

    }

    @Override


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        healthViewModel =
                ViewModelProviders.of(this).get(HealthViewModel.class);
        View root = inflater.inflate(R.layout.fragment_health, container, false);


        return root;
    }

}