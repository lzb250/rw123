package com.huazhi.sensorcontrol.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.huazhi.sensorcontrol.R;
import com.huazhi.sensorcontrol.databinding.FragmentMineBinding;
import com.huazhi.sensorcontrol.model.Command;
import com.huazhi.sensorcontrol.model.bind.MineStatus;
import com.huazhi.sensorcontrol.utils.GlobalDefs;
import com.huazhi.sensorcontrol.utils.ToastUtils;
import com.huazhi.sensorcontrol.utils.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment {
    private String TAG = "MineFragment";

    public MineFragment() {
    }

    private FragmentMineBinding binding;
    private MineStatus status;
    private ImageView wifiImage;
    private EditText ipaddrr;
    private Spinner spinner;
    private TextView tvVersion;
    private View view;

    private String port;

    Handler mHandler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            status.wifiStatus(view);
            //执行
            status.resumeState(MineFragment.this,wifiImage);
            //检查socket连接
            mHandler.postDelayed(r, 5000);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null) {
            binding = FragmentMineBinding.inflate(inflater, container, false);
        }
        status = new MineStatus();
        status.initMineStatus();
        binding.setStatus(status);
        binding.setSetClick(listener);

        ipaddrr = binding.getRoot().findViewById(R.id.editText2);
        spinner = binding.getRoot().findViewById(R.id.spinner_serverPort);
        tvVersion = binding.getRoot().findViewById(R.id.tv_version);

        final String[] port1D = getResources().getStringArray(R.array.portArray);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                port = port1D[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        wifiImage = binding.getRoot().findViewById(R.id.complete_right);
        this.view = binding.getRoot();
        //初始化数据
        initData();
        status.wifiStatus(view);
        //版本号
        String version = Util. getVersionName(view.getContext());
        tvVersion.setText("版本号：V"+version);
        //注册事件
        EventBus.getDefault().register(this);
        //检查socket连接
        mHandler.postDelayed(r, 5000);
        return binding.getRoot();
    }

    /***
     *
     * 初始化数据
     *
     * ***/
    private void initData() {
        String ipaddr = (String) Util.getSP(view.getContext(), GlobalDefs.SENSOR_CONTROL_SHARED_PREFS_NAME, GlobalDefs.KEY_IP_ADDRESS, String.class);
        String port = (String) Util.getSP(view.getContext(), GlobalDefs.SENSOR_CONTROL_SHARED_PREFS_NAME, GlobalDefs.KEY_PORT, String.class);

        //地址
        if (TextUtils.isEmpty(ipaddr)) {
            ipaddr = GlobalDefs.DEFAULT_IP;
            Util.setSP(view.getContext(), GlobalDefs.SENSOR_CONTROL_SHARED_PREFS_NAME, GlobalDefs.KEY_IP_ADDRESS, GlobalDefs.DEFAULT_IP);
        }
        status.setIpaddr(ipaddr);

        //端口号码
        if (TextUtils.isEmpty(port)) {
            port = GlobalDefs.DEFAULT_PORT;
            Util.setSP(view.getContext(), GlobalDefs.SENSOR_CONTROL_SHARED_PREFS_NAME, GlobalDefs.KEY_PORT, GlobalDefs.DEFAULT_PORT);
        }
        status.setPort(port);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSensorEvent(Command command){
        status.writeCmd(command.execute());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(this);
        status.stopLink();
    }

    @Override
    public void onResume() {
        super.onResume();
        status.resumeState(this,wifiImage);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //隐藏键盘
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            if (getActivity().getCurrentFocus() == null) {
            } else {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }

            //IP
            String ip = ipaddrr.getText().toString().trim();
            if (!status.checkIP(ip)) {
                ToastUtils.showToast(view.getContext(), "请输入正确的服务器IP");
                return;
            }
            //保存在本地
            saveOrNot(ip, port, true);
            ToastUtils.showToast(view.getContext(), "设置成功");

            //开启线程连接
            try {
                //连接socket
                status.startConnSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //保存记忆 输入服务器IP地址
    private void saveOrNot(String serverIP, String serverPort, boolean or) {
        //保存
        if (or) {
            Util.setSP(this.getContext(), GlobalDefs.SENSOR_CONTROL_SHARED_PREFS_NAME, GlobalDefs.KEY_IP_ADDRESS, serverIP);
            Util.setSP(this.getContext(), GlobalDefs.SENSOR_CONTROL_SHARED_PREFS_NAME, GlobalDefs.KEY_PORT, serverPort);
        } else {
            Util.clearSP(this.getContext(), GlobalDefs.SENSOR_CONTROL_SHARED_PREFS_NAME, GlobalDefs.KEY_IP_ADDRESS);
            Util.clearSP(this.getContext(), GlobalDefs.SENSOR_CONTROL_SHARED_PREFS_NAME, GlobalDefs.KEY_PORT);
        }
    }
}
