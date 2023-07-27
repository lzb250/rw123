package com.example.iotagriculturetemp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.iotagriculturetemp.R;
import com.example.iotagriculturetemp.model.ConnectServer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FragmentDashboard extends Fragment implements View.OnClickListener{
    public FragmentDashboard(){

    }

    private ConnectServer connectServer;

    private EditText et_conn_ip;
    private EditText et_conn_port;

    private ImageView iv_WifiIsRun;
    private Button btn_conn;
    Handler mHandler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            connectServer.resumeState(FragmentDashboard.this,iv_WifiIsRun);
            if(!connectServer.isConnect())
            {
                connectSocket();
            }
            //检查socket连接
            mHandler.postDelayed(r, 50);
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册event事件
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_fragment_dashboard, container, false);

        connectServer = new ConnectServer();
        connectServer.initMineStatus();

        et_conn_ip = view.findViewById(R.id.conn_et_ip);
        et_conn_port = view.findViewById(R.id.conn_et_port);
        btn_conn = view.findViewById(R.id.btn_conn);
        iv_WifiIsRun = view.findViewById(R.id.complete_right);

        mHandler.postDelayed(r, 5);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
    }

    private void initListener(){
        btn_conn.setOnClickListener(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        connectServer.resumeState(this,iv_WifiIsRun);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_conn:
                connectSocket();
                break;
        }
    }

    private void connectSocket() {
        String str_IP = et_conn_ip.getText().toString().trim();
        String str_port = et_conn_port.getText().toString().trim();
        if(connectServer.checkIP(str_IP)){

        }
        connectServer.setData(str_IP,str_port);
        try {
            connectServer.startConnSocket();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSensorEvent(String recvData){

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}