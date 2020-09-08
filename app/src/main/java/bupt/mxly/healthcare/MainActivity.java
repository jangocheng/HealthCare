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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import bupt.mxly.healthcare.db.UserInfo;

import static bupt.mxly.healthcare.ModifyUI.setFitSystemWindow;
import static bupt.mxly.healthcare.ModifyUI.setStatusBarFullTransparent;
import static bupt.mxly.healthcare.ModifyUI.setStatusBarLightMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        setStatusBarFullTransparent(MainActivity.this);
        setFitSystemWindow(true, MainActivity.this);
        setStatusBarLightMode(this, true);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_health, R.id.navigation_smartdev, R.id.navigation_contact, R.id.navigation_about)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


    }



}