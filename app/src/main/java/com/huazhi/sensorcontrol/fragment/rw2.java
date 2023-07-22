package com.huazhi.sensorcontrol.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.huazhi.sensorcontrol.R;
import com.huazhi.sensorcontrol.SQL.User;
import com.huazhi.sensorcontrol.SQL.UserDao;
import com.huazhi.sensorcontrol.model.bind.HomeCtr;
import com.huazhi.sensorcontrol.utils.LogUtils;
import com.huazhi.sensorcontrol.utils.ToastUtils;
import com.huazhi.sensorcontrol.PictureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class rw2 extends Fragment {
    private Spinner types;
    private String type="";
    private Button zx;
    private Button sx;
    private ListView date;
    private View view;
    private HomeCtr homeCtr;
    private Handler mMainHandler;
    Handler mHandler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
        }
    };
    public rw2(){

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.rw2, null);
        }

        homeCtr = new HomeCtr();
        //初始化视图
        initView();
        initListener();

        // 实例化主线程,用于更新接收过来的消息
        mMainHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Nullable
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0x117: //延迟一秒，可调灯再次生效
                        SeekBar bar = (SeekBar) msg.obj;
                        bar.setEnabled(true);
                        break;
                }
            }
        };

        //注册事件
        EventBus.getDefault().register(this);
        //编号刷新
        mHandler.postDelayed(r, 5000);
        return view;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHomeCtrEvent(String recvData) {
        LogUtils.i("recvData=" + recvData);

    }

    private void initView() {
        types=view.findViewById(R.id.type_select);
        sx=view.findViewById(R.id.select_btn);
        date=view.findViewById(R.id.lv_data_list);
        zx=view.findViewById(R.id.select_btn1);
    }
    private void initListener() {
        types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] types = getResources().getStringArray(R.array.sensorArray);
                type=types[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }});
       sx.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(type==""){
                   ToastUtils.showToast(view.getContext(), view.getContext().getString(R.string.try_again1));
               }else{
                   UserDao userDao=UserDao.getInstance(getContext());
                   List<User> userList=userDao.Show(type);
                   ListAdapter adapter = new com.huazhi.sensorcontrol.ListAdapter(getContext(),userList);
                   date.setAdapter(adapter);
               }
           }
       });
       zx.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               UserDao userDao = UserDao.getInstance(getContext());
               //温度数据在数据库中的组数
               List<User> userList1 = userDao.find1("厢内温度");
               System.out.println("条件查询数据组数：" + userList1.size());
               List<User> userList2 = userDao.find1("厢外温度");
               System.out.println("条件查询数据组数：" + userList2.size());
               //湿度数据在数据库中的组数
               List<User> userList3 = userDao.find1("湿度");
               System.out.println("条件查询数据组数：" + userList3.size());
               //光照度数据在数据库中的组数
               List<User> userList4 = userDao.find1("光照");
               System.out.println("条件查询数据组数：" + userList4.size());
               Intent intent1 = new Intent(getContext(),PictureActivity.class);
               startActivityForResult(intent1,1);
           }
       });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(this);
    }
}
