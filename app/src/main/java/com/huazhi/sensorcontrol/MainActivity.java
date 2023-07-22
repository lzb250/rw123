package com.huazhi.sensorcontrol;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.huazhi.sensorcontrol.fragment.HomeFragment;
import com.huazhi.sensorcontrol.fragment.MineFragment;
import com.huazhi.sensorcontrol.fragment.rw1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;
import android.widget.TextView;

import com.huazhi.sensorcontrol.fragment.rw1;
import com.huazhi.sensorcontrol.fragment.rw2;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

public class
MainActivity extends AppCompatActivity {
    private FragmentManager manager;
    //两个fragment
    private HomeFragment mHomeFragment;
    private MineFragment mMineFragment;
    private rw1 mrw1;
    private rw2 mrw2;

    //标题
    private TextView title_header;
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFindViewById();
        initFragment();
        checkPermission();
        context = this;
    }

    /**
     * 权限配置
     **/
    private void checkPermission() {
        AndPermission.with(this).permission(
                Permission.LOCATION).start();
    }

    private void initFindViewById() {
        getSupportActionBar().hide();
        title_header = findViewById(R.id.title_header);
        //导航按钮
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void initFragment() {
        mHomeFragment = new HomeFragment();
        mMineFragment = new MineFragment();
        mrw1=new rw1();
        mrw2=new rw2();
        manager = getSupportFragmentManager();
        manager.beginTransaction()
                .add(R.id.main_fragment,mHomeFragment).hide(mHomeFragment)
                .add(R.id.main_fragment,mMineFragment).hide(mMineFragment)
                .add(R.id.main_fragment,mrw1).hide(mrw1)
                .add(R.id.main_fragment,mrw2).hide(mrw2)
                .show(mHomeFragment)
                .commit();
    }

    /**
     * fragment重叠问题处理 注销掉
     **/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    manager.beginTransaction().hide(mrw2).hide(mMineFragment).hide(mrw1).show(mHomeFragment).commit();
                    title_header.setText(context.getResources().getString(R.string.app_name));
                    return true;
                case R.id.nav:
                    manager.beginTransaction().hide(mrw2).hide(mHomeFragment).hide(mMineFragment).show(mrw1).commit();
                    title_header.setText(context.getResources().getString(R.string.rw1));
                    return true;
                case R.id.nav_view:
                    manager.beginTransaction().hide(mMineFragment).hide(mrw1).hide(mHomeFragment).show(mrw2).commit();
                    title_header.setText(context.getResources().getString(R.string.rw2));
                    return true;
                case R.id.navigation_dashboard:
                    manager.beginTransaction().hide(mrw2).hide(mHomeFragment).hide(mrw1).show(mMineFragment).commit();
                    title_header.setText(context.getResources().getString(R.string.title_dashboard_center));
                    return true;
            }
            return false;
        }
    };
}
