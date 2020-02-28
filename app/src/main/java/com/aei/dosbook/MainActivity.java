package com.aei.dosbook;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aei.dosbook.Utils.Database;
import com.aei.dosbook.Utils.MyApp;
import com.aei.dosbook.ui.NavigationDataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_find, R.id.navigation_feed, R.id.navigation_notifications,
                R.id.navigation_profile)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    void showMyProfile(){
        NavigationDataManager.setCurrentProfile(MyApp.getMyUserProfile());
        navController.navigate(R.id.navigation_profile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        MenuItem genderChoose = menu.findItem(R.id.settings_gender);
        genderChoose.setCheckable(true);
//        genderChoose.setChecked(MyApp.getMyUserProfile().isShowOppositeGender());
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem genderChoose = menu.findItem(R.id.settings_gender);
        genderChoose.setChecked(MyApp.getMyUserProfile().isShowOppositeGender());
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_gender:
                MyApp.getMyUserProfile().setShowOppositeGender(!MyApp.getMyUserProfile().isShowOppositeGender());
                Database.getInstance().updateShowOpposingGender();
                Toast.makeText(this,"Please Refresh Feed",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.settings_profile:
                showMyProfile();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
