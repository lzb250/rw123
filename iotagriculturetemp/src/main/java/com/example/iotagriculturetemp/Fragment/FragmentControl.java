package com.example.iotagriculturetemp.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.iotagriculturetemp.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FragmentControl extends Fragment {

    //接受数据并显示
    //下发命令接口

    private View view;
    private TextView tv_message;
    private TextView tv_show_shiDu;
    private TextView tv_show_wenDu;
    private TextView tv_show_guangZhaoDu;

    private Button btn_ctl_flight1_close;
    private Button btn_ctl_flight1_open;

    private Button btn_ctl_flight2_close;
    private Button btn_ctl_flight2_open;

    private Button btn_ctl_light_close;
    private Button btn_ctl_light_open;

    private Button btn_ctl_bjdj_close;
    private Button btn_ctl_bjdj_open1;
    private Button btn_ctl_bjdj_open2;

    private Button btn_autoCtl;

    private EditText et_wd_limit_down;

    private EditText et_wd_limit_up;

    private EditText et_gz_limit_up;

    private boolean isAutoctl = false;

    public FragmentControl() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHomeCtrEvent(String recvData) {
        handleSocketReciveData(recvData);
    }
    private void handleSocketReciveData(String recvData) {
        tv_message.setText(recvData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.layout_fragment_control, container, false);
        initView();
        initListener();
        //调试
//        et_wd_limit_down.setText("1111");
//        et_gz_limit_up.setText("9999");
//        et_wd_limit_up.setText("2222");

        return view;
    }
    private void initListener() {
        //风扇
        btn_ctl_flight1_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post("Hwcaf0803offT");//发送事件
            }
        });
        btn_ctl_flight1_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post("Hwcaf0802onT");//发送事件
            }
        });
        btn_ctl_flight2_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post("Hwcaf0803off2T");//发送事件
            }
        });
        btn_ctl_flight2_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post("Hwcaf0802on2T");//发送事件
            }
        });
        //可调灯
        btn_ctl_light_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post("Hwdsr0105al000T");//发送事件

            }
        });
        btn_ctl_light_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post("Hwdsr0105al100T");//发送事件

            }
        });

        //步进电机
        btn_ctl_bjdj_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post("Hwcrs09040000T");//发送事件

            }
        });
        btn_ctl_bjdj_open1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post("Hwcrs0904on00T");//发送事件

            }
        });
        btn_ctl_bjdj_open2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post("Hwcrs0905off00T");//发送事件
            }
        });
        btn_autoCtl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAutoctl == false){
                    isAutoctl = true;
                    String wd_up = et_wd_limit_up.getText().toString().trim();
                    String wd_down = et_wd_limit_down.getText().toString().trim();
                    String gz_up = et_gz_limit_up.getText().toString().trim();
                    String gz_up_final = "";

                    for(int i=0;i<6-gz_up.length();i++)
                    {
                        gz_up_final +="0";
                    }
                    gz_up_final += gz_up;

                    EventBus.getDefault().post("Hwdie0210illk"+gz_up_final+"T");//发送事件


                    EventBus.getDefault().post("Hwche0106tm"+wd_up+wd_down+"T");//发送事件
                    new Thread(){
                        public void run(){
                            SystemClock.sleep(200);
                            EventBus.getDefault().post("Hwche0108temautonT");//发送事件

                        }
                    }.start();

                    btn_autoCtl.setText("关闭自动控制...");
                }
                else {
                    isAutoctl = false;
                    EventBus.getDefault().post("Hwche0109temautoffT");//发送事件
                    btn_autoCtl.setText("开启自动控制");
                }
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    private void initView(){
        tv_message = view.findViewById(R.id.tv_message);

        tv_show_guangZhaoDu = view.findViewById(R.id.tv_show_guangZhaoDu);
        tv_show_shiDu = view.findViewById(R.id.tv_show_shiDu);
        tv_show_wenDu = view.findViewById(R.id.tv_show_wenDu);

        et_wd_limit_down = view.findViewById(R.id.et_wd_limit_down);
        et_wd_limit_up = view.findViewById(R.id.et_wd_limit_up);
        et_gz_limit_up = view.findViewById(R.id.et_gz_limit_up);

        btn_ctl_bjdj_close = view.findViewById(R.id.motor_bjdj_close);
        btn_ctl_bjdj_open1 = view.findViewById(R.id.motor_bjdj_open1);
        btn_ctl_bjdj_open2 = view.findViewById(R.id.motor_bjdj_open2);

        btn_ctl_flight1_close = view.findViewById(R.id.motor_rb_close);
        btn_ctl_flight1_open = view.findViewById(R.id.motor_rb_open);

        btn_ctl_flight2_close = view.findViewById(R.id.motor_rb1_close);
        btn_ctl_flight2_open = view.findViewById(R.id.motor_rb1_open);

        btn_ctl_light_close = view.findViewById(R.id.motor_ktd_close);
        btn_ctl_light_open = view.findViewById(R.id.motor_ktd_open);

        btn_autoCtl = view.findViewById(R.id.btn_autoCtl);
    }

}