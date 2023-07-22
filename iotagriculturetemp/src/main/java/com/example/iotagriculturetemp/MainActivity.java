package com.example.iotagriculturetemp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.iotagriculturetemp.Fragment.FragmentControl;
import com.example.iotagriculturetemp.Fragment.FragmentDashboard;
import com.example.iotagriculturetemp.Fragment.FragmentDataShow;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    private FragmentControl fragmentControl;
    private FragmentDataShow fragmentDataShow;
    private FragmentDashboard fragmentDashboard;

    private TextView title_header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title_header = findViewById(R.id.title_header);
        initFindViewByID();
        initFragment();
    }

    private void initFindViewByID() {
        getSupportActionBar().hide();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener);
    }
    /**
     * fragment重叠问题处理 注销掉
     **/
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        //super.onSaveInstanceState(outState);
//        super.onSaveInstanceState(outState);
//    }
    private void initFragment() {
        fragmentControl = new FragmentControl();
        fragmentDashboard = new FragmentDashboard();
        fragmentDataShow = new FragmentDataShow();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.main_fragment,fragmentControl).hide(fragmentControl)
                .add(R.id.main_fragment,fragmentDataShow).hide(fragmentDataShow)
                .add(R.id.main_fragment,fragmentDashboard).hide(fragmentDashboard)
                .show(fragmentControl)
                .commit();

    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.navigation_dashboard:
                    fragmentManager.beginTransaction().hide(fragmentDataShow).hide(fragmentControl).show(fragmentDashboard).commit();
                    return true;
                case R.id.navigation_control:
                    fragmentManager.beginTransaction().hide(fragmentDataShow).hide(fragmentDashboard).show(fragmentControl).commit();
                    return true;
                case R.id.navigation_dataShow:
                    fragmentManager.beginTransaction().hide(fragmentControl).hide(fragmentDashboard).show(fragmentDataShow).commit();
                    return true;
            }
            return false;
        }
    };
}
