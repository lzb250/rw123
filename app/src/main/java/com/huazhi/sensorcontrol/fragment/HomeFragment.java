package com.huazhi.sensorcontrol.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.huazhi.sensorcontrol.R;
import com.huazhi.sensorcontrol.device.SensorType;
import com.huazhi.sensorcontrol.model.SensorDevice;
import com.huazhi.sensorcontrol.model.bind.HomeCtr;
import com.huazhi.sensorcontrol.utils.GlobalDefs;
import com.huazhi.sensorcontrol.utils.LogUtils;
import com.huazhi.sensorcontrol.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private String TAG = "HomeFragment";
    private TextView tvFan;
    private TextView tvFan1;
    private TextView tvLight;
    private Button btnLight;
    private RadioButton btnFan;
    private RadioButton btnFan1;
    private Button btnLight1;
    private TextView tvMotor;
    private TextView tvMotorStatus;
    private RadioGroup rgMotor;
    private RadioButton rbMotorClose;
    private RadioButton rbMotorOpen;
    private RadioButton rbMotorStop;
    private String num1="";
    private String m="";
    private String m1="";
    private Switch m3;

    private View view;
    private SensorDevice sensorDevice = null;//传感器设备
    private String fanDeviceNo = "08";//风扇
    private String fanDeviceNo1 = "";//风扇
    private String lightDeviceNo = "01";//可调灯
    private String lightDeviceNo1 = "05";//可调灯
    private String motorDeviceNo = "";//步进电机
    private boolean isClickStop = false;//是否点击停止

    private PopupWindow popupWindow = null;
    private String now_angle = "000";
    private String now_angle1 = "000";
    private String perial = "";//定时器时间间隔
    private Timer timerlight;
    // 用于将从服务器获取的消息显示出来
    private Handler mMainHandler;

    public HomeFragment() {
    }

    private HomeCtr homeCtr;

    Handler mHandler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            //可调灯
            if (!TextUtils.isEmpty(lightDeviceNo)) {
                String light = view.getContext().getString(R.string.hand_light_01_title) + "(编号" + lightDeviceNo + ")：";
                tvLight.setText(light);
            }

            //步进电机
            if (!TextUtils.isEmpty(motorDeviceNo)) {
                String motor = view.getContext().getString(R.string.hand_motor_01_title) + "(编号" + motorDeviceNo + ")：";
                tvMotor.setText(motor);
            }
            mHandler.postDelayed(r, 3000);
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载视图文件
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, null);
        }

        homeCtr = new HomeCtr();
        //初始化视图
        initView();
        initListener();

        // 实例化主线程,用于更新接收过来的消息
        mMainHandler = new Handler() {
            @SuppressLint("HandlerLeak")
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
        mHandler.postDelayed(r, 3000);
        return view;
    }


    /**
     * 初始化视图
     **/
    private void initView() {
        tvFan = view.findViewById(R.id.tv_fan);
        tvFan1 = view.findViewById(R.id.tv_fan1);
        tvLight = view.findViewById(R.id.tv_light);
        btnLight = view.findViewById(R.id.btn_light);
        btnLight1 = view.findViewById(R.id.btn_light1);
        tvMotor = view.findViewById(R.id.tv_motor);
        tvMotorStatus = view.findViewById(R.id.tv_motor_status);
        rgMotor = view.findViewById(R.id.motor_group);
        rbMotorClose = view.findViewById(R.id.motor_rb_close);
        rbMotorOpen = view.findViewById(R.id.motor_rb_open);
        rbMotorStop = view.findViewById(R.id.motor_rb_stop);
        m3=view.findViewById(R.id.k);
        btnFan=view.findViewById(R.id.motor_rb_close1);
        btnFan1=view.findViewById(R.id.motor_rb_open1);
    }

    /***
     *
     * 事件
     *
     * **/
    private void initListener() {
        //可调灯
        btnLight1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.i("---------直流电机--------");
                lightUserInface1("Hw");//wifi控制
            }
        });
        //可调灯
        btnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.i("---------可调灯--------");
                lightUserInface("Hw");//wifi控制
            }
        });
        btnFan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeCtr.sendID(GlobalDefs.SENSOR_TYPE_FAN,true,fanDeviceNo);
            }
        });
        btnFan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeCtr.sendID(GlobalDefs.SENSOR_TYPE_FAN,false,fanDeviceNo);
            }
        });
        //步进电机
        rbMotorOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("正转");
                isClickStop = false;
                motorCtrl(GlobalDefs.MOTOR_TYPE_1, motorDeviceNo);
            }
        });
        rbMotorClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("反转");
                isClickStop = false;
                motorCtrl(GlobalDefs.MOTOR_TYPE_2, motorDeviceNo);
            }
        });
        rbMotorStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("停止");
                isClickStop = true;
                motorCtrl(GlobalDefs.MOTOR_TYPE_3, motorDeviceNo);
            }
        });
    }
    private void lightUserInface1(final String orderHeader) {
        View view = LayoutInflater.from(this.view.getContext()).inflate(R.layout.dialog_light1, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(this.view, Gravity.CENTER, 5, 5);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_close_light1);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderHeader == "Hw") { //wifi控制才发送，zigbee控制不发送
                    if (!TextUtils.isEmpty(lightDeviceNo)) {
                        String orderlight = orderHeader + "czl" + lightDeviceNo + "08stopctrlT";
                        //发送数据
                    }
                }
                popupWindow.dismiss();
                popupWindow = null;
                //isOpenWindow = false;
            }
        });

        SeekBar light_control = (SeekBar) view.findViewById(R.id.light_control1);
        int parseInt = Integer.parseInt(now_angle1);

        LogUtils.i("lightUserInface  parseInt=" + parseInt);

        light_control.setProgress(parseInt);
        final TextView light_angle = (TextView) view.findViewById(R.id.light_angle1);
        light_angle.setText("转速:" + parseInt + "%");
        EditText control_perial = (EditText) view.findViewById(R.id.control_perial1);
        control_perial.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                perial = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        light_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                light_angle.setText("转速:" + progress + "%");
                String angle = "100";
                if ((progress + "").length() == 1) {
                    angle = "00" + progress;
                } else if ((progress + "").length() == 2) {
                    angle = "0" + progress;
                }
                now_angle1 = angle;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (orderHeader.contains("Hw")) {
                    TimerTask tasklight = new TimerTask() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(lightDeviceNo1)) {
                                String orderlight = orderHeader + "czl" + lightDeviceNo1 + "03" + now_angle1 + "T";
                                //发送数据

                            }
                        }
                    };
                    timerlight = new Timer();
                    try {
                        if (!perial.equals("")) {
                            timerlight.schedule(tasklight, 0, Integer.parseInt(perial));
                        } else {
                            timerlight.schedule(tasklight, 0, 500);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (orderHeader.contains("Hw")) {
                    if (!TextUtils.isEmpty(lightDeviceNo1)) {
                        String orderlight = orderHeader + "czl" + lightDeviceNo1 + "06" + now_angle1 + "endT";
                        //发送数据
                        homeCtr.sendLight(orderlight);
                    }
                    if (timerlight != null) {
                        timerlight.cancel();
                        timerlight = null;
                    }
                    seekBar.setEnabled(false);
                    Message msgRelift = new Message();
                    msgRelift.what = 0x117;
                    msgRelift.obj = seekBar;
                    mMainHandler.sendMessageDelayed(msgRelift, 1000);
                }
            }
        });
        //打开界面时，发送start控制命令
        if (orderHeader == "Hw") { //wifi控制才发送，zigbee控制不发送
            if (!TextUtils.isEmpty(lightDeviceNo)) {
                String orderlight = orderHeader + "cal" + lightDeviceNo + "09startctrlT";
                //发送数据
            }
        }
    }
    /**
     * 可调灯界面
     *
     * @param orderHeader 命令头
     */
    private void lightUserInface(final String orderHeader) {
        View view = LayoutInflater.from(this.view.getContext()).inflate(R.layout.dialog_light, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(this.view, Gravity.CENTER, 5, 5);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_close_light);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderHeader == "Hw") { //wifi控制才发送，zigbee控制不发送
                    if (!TextUtils.isEmpty(lightDeviceNo)) {
                        String orderlight = orderHeader + "cal" + lightDeviceNo + "08stopctrlT";
                        //发送数据
                        homeCtr.sendLight(orderlight);
                    }
                }
                popupWindow.dismiss();
                popupWindow = null;
                //isOpenWindow = false;
            }
        });

        SeekBar light_control = (SeekBar) view.findViewById(R.id.light_control);
        int parseInt = Integer.parseInt(now_angle);

        LogUtils.i("lightUserInface  parseInt=" + parseInt);

        light_control.setProgress(parseInt);
        final TextView light_angle = (TextView) view.findViewById(R.id.light_angle);
        light_angle.setText("亮度:" + parseInt + "%");
        EditText control_perial = (EditText) view.findViewById(R.id.control_perial);
        control_perial.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                perial = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        light_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                light_angle.setText("亮度:" + progress + "%");
                String angle = "100";
                if ((progress + "").length() == 1) {
                    angle = "00" + progress;
                } else if ((progress + "").length() == 2) {
                    angle = "0" + progress;
                }
                now_angle = angle;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (orderHeader.contains("Hw")) {
                    TimerTask tasklight = new TimerTask() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(lightDeviceNo)) {
                                String orderlight = orderHeader + "cal" + lightDeviceNo + "03" + now_angle + "T";
                                //发送数据
                            }
                        }
                    };
                    timerlight = new Timer();
                    try {
                        if (!perial.equals("")) {
                            timerlight.schedule(tasklight, 0, Integer.parseInt(perial));
                        } else {
                            timerlight.schedule(tasklight, 0, 500);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (orderHeader.contains("Hw")) {
                    if (!TextUtils.isEmpty(lightDeviceNo)) {
                        String orderlight = orderHeader + "cal" + lightDeviceNo + "06" + now_angle + "endT";
                        //发送数据
                        homeCtr.sendLight(orderlight);
                    }

                    if (timerlight != null) {
                        timerlight.cancel();
                        timerlight = null;
                    }
                    seekBar.setEnabled(false);
                    Message msgRelift = new Message();
                    msgRelift.what = 0x117;
                    msgRelift.obj = seekBar;
                    mMainHandler.sendMessageDelayed(msgRelift, 1000);
                }
            }
        });

        //打开界面时，发送start控制命令
        if (orderHeader == "Hw") { //wifi控制才发送，zigbee控制不发送
            if (!TextUtils.isEmpty(lightDeviceNo)) {
                String orderlight = orderHeader + "cal" + lightDeviceNo + "09startctrlT";
                //发送数据
                homeCtr.sendLight(orderlight);
            }
        }

    }

    /**
     * 控制设置
     */
    private void sensorCtrl(Switch swView, int type, String deviceNo) {
        if (TextUtils.isEmpty(deviceNo)) {
            ToastUtils.showToast(view.getContext(), view.getContext().getString(R.string.try_again));
            return;
        }

        boolean isCheck = false;
        if (swView.isChecked()) {
            isCheck = true;
        }
        homeCtr.sendID(type, isCheck, deviceNo);
    }

    /**
     * 卷帘电机控制
     ***/
    private void motorCtrl(int type, String deviceNo) {
        if (TextUtils.isEmpty(deviceNo)) {
            ToastUtils.showToast(view.getContext(), view.getContext().getString(R.string.try_again));
            return;
        }
        homeCtr.sendMotorID(type, deviceNo);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHomeCtrEvent(String recvData) {
        LogUtils.i("recvData=" + recvData);
        handleSocketReciveData(recvData);
    }

    /***
     *
     * 处理从socket接收到的数据
     *
     * **/
    private void handleSocketReciveData(String tempData) {
        //LogUtils.i("------------tempData=" + tempData);
        sensorDevice = new SensorDevice();//传感设备
        sensorDevice.setType(tempData.substring(3, 5));//设备类型
        sensorDevice.setNumber(tempData.substring(5, 7));//解析上报数据读取设备编号
        sensorDevice.setSrcData(tempData);
        //风扇
        if (sensorDevice.getType().endsWith(SensorType.SENSOR_FAN)) {
            if (TextUtils.isEmpty(fanDeviceNo)) {
                LogUtils.i("风扇1编号:" + sensorDevice.getNumber());
                fanDeviceNo = sensorDevice.getNumber();
                num1=sensorDevice.getNumber();
            }
            //状态设置
        }
        //可调灯
        if (sensorDevice.getType().endsWith(SensorType.SENSOR_AL)) {
            if (TextUtils.isEmpty(lightDeviceNo)) {
                LogUtils.i("可调灯编号:" + sensorDevice.getNumber());
                lightDeviceNo = sensorDevice.getNumber();
            }
        }
        //卷帘电机
        else if (sensorDevice.getType().endsWith(SensorType.SENSOR_RS)) {
            if (TextUtils.isEmpty(motorDeviceNo)) {
                LogUtils.i("卷帘电机编号:" + sensorDevice.getNumber());
                motorDeviceNo = sensorDevice.getNumber();
            }
            //状态设置
            sensorMotorStatusHandleData(tempData);
        }
    }

    /***
     *
     * 传感器状态_接收数据处理
     *
     * **/
    private void sensorStatusHandleData(Switch swView, String recvData) {
        if (recvData.contains("on")) {
            //LogUtils.i("返回状态  开");
            swView.setChecked(true);
        } else if (recvData.contains("off")) {
            //LogUtils.i("返回状态  关");
            swView.setChecked(false);
        }
    }

    /**
     * 传感器(卷帘电机)状态_接收数据处理
     **/
    private void sensorMotorStatusHandleData(String recvData) {
        //重置
        if (recvData.contains("on")) {
            //如果没有点击停止
            if (!isClickStop) {
                LogUtils.i("返回状态  正转");
                tvMotorStatus.setText((String) getResources().getText(R.string.forward));
                rbMotorOpen.setChecked(true);//正转不可点击
                rbMotorClose.setChecked(false);//反转可点击
                rbMotorStop.setChecked(false);//停止可点击
            }
        } else if (recvData.contains("off")) {
            //如果没有点击停止
            if (!isClickStop) {
                LogUtils.i("返回状态  反转");
                tvMotorStatus.setText((String) getResources().getText(R.string.reverse));
                rbMotorClose.setChecked(true);//反转不可点击
                rbMotorOpen.setChecked(false);//正转可点击
                rbMotorStop.setChecked(false);//停止可点击
            }
        } else {
            //停止
            LogUtils.i("返回状态  停止");
            tvMotorStatus.setText((String) getResources().getText(R.string.stop));
            rbMotorStop.setChecked(true);//停止不可点击
            rbMotorClose.setChecked(false);//反转可点击
            rbMotorOpen.setChecked(false);//正转可点击
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(this);
    }
}








