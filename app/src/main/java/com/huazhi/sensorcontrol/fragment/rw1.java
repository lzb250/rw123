package com.huazhi.sensorcontrol.fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.huazhi.sensorcontrol.SQL.DbContect;
import com.huazhi.sensorcontrol.SQL.UserDao;
import com.huazhi.sensorcontrol.model.bind.HomeCtr;

import com.huazhi.sensorcontrol.R;
import com.huazhi.sensorcontrol.device.SensorType;
import com.huazhi.sensorcontrol.model.Command;
import com.huazhi.sensorcontrol.model.SensorDevice;
import com.huazhi.sensorcontrol.model.bind.HomeCtr;
import com.huazhi.sensorcontrol.model.cmd.RollerCommand;
import com.huazhi.sensorcontrol.utils.GlobalDefs;
import com.huazhi.sensorcontrol.utils.LogUtils;
import com.huazhi.sensorcontrol.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class rw1 extends Fragment {
        private EditText etTemp1;
        private EditText etTemp2;
        private EditText et;
        private EditText yu;
        private TextView tvIe;
        private TextView wendu;
        private TextView wendu1;
        private TextView tvTemp;
        private TextView tvLight;
        private TextView tvText;
        private TextView dcid;
        private TextView tvFan1;
        private TextView tvMotor;
        private TextView tvStatus;
        private TextView zsu;
        private TextView kt;
        private EditText tg;
        private Button wenduyuzhi;
        private Button gzyuzhi;
        private Switch k;
        private String  num="";
        public String Tin;
        public String Tout;
        public String SD;
        private String gz;
        private String T1;
        private String T2;
        private Float T0;
        private String m="1";
        private String m1="2";
        private int i=0;
        private int i1=0;
        private int i2=0;
        private TextView tval;
        private String name="0";

        private int IE_MAX_VALUE = 1000;//光照度的最值
        private final int IE_MSG_CODE = 1;//光照度传感器
        private View view;
        private SensorDevice sensorDevice = null;//传感器设备
        private String fanDeviceNo = "";//风扇1
        private String fanDeviceNo1 = "";//风扇2
        private String lightDeviceNo = "";//可调灯
        private String motorDeviceNo = "";//步进电机
        private String tempDeviceNo="03";
        private String tempDeviceNo1="01";
        private boolean isClickStop = false;//是否点击停止
        private PopupWindow popupWindow = null;
        private String now_angle = "000";
        private String perial = "";//定时器时间间隔
        private Timer timerlight;
        // 用于将从服务器获取的消息显示出来
        private Handler mMainHandler;
        public rw1(){

        }
        private HomeCtr homeCtr;
        Handler mHandler = new Handler();
        Runnable r = new Runnable() {
                @Override
                public void run() {
                        if (!TextUtils.isEmpty(tempDeviceNo)){
                                String temp=view.getContext().getString(R.string.hand_temp_01_title)+"(编号" + tempDeviceNo + ")";
                                tvTemp.setText(temp);
                        }
                        if (!TextUtils.isEmpty(lightDeviceNo)) {
                                String light = view.getContext().getString(R.string.home_al_title) + "(编号" + lightDeviceNo + "):";
                                tvLight.setText(light);
                        }
                        //风扇
                        if (!TextUtils.isEmpty(fanDeviceNo)) {
                                String fan = view.getContext().getString(R.string.hand_fan_01_title) + "(编号" + fanDeviceNo + ")：";
                                tvFan1.setText(fan);
                        }
                        if (!TextUtils.isEmpty(fanDeviceNo1)) {
                                String fan1 = view.getContext().getString(R.string.hand_fan_01_title1) + "(编号" + fanDeviceNo1 + ")：";
                        }
                        //步进电机
                        if (!TextUtils.isEmpty(motorDeviceNo)) {
                                String motor = view.getContext().getString(R.string.hand_motor_01_title) + "(编号" + motorDeviceNo + ")：";
                                tvMotor.setText(motor);
                        }
                        mHandler.postDelayed(r, 3000);
                }
        };

        @SuppressLint("HandlerLeak")
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                // 加载视图文件
                if (view == null) {
                        view = inflater.inflate(R.layout.rw1, null);
                }
                homeCtr = new HomeCtr();
                //初始化视图
                initView();
                initListener();

                // 实例化主线程,用于更新接收过来的消息
                mMainHandler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                                switch (msg.what) {
                                        //光照度消息
                                        case IE_MSG_CODE:
                                                updateIeValue((String) msg.obj);
                                                //每次收到光照传感器的数据，如果空间上有人则根据光照的实时数据控制调节灯的亮度；如果无人，则关闭调节灯光（处理控制人体红外传感器
                                                break;
                                        //可调节灯消息
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
                mHandler.postDelayed(r, 4000);
                return view;
        }
        private void initView() {
                etTemp1=view.findViewById(R.id.conn_et_temp);
                etTemp2=view.findViewById(R.id.conn_et_temp1);
                et=view.findViewById(R.id.conn_et_temp3);
                tvIe=view.findViewById(R.id.home_tv_ie);
                tvLight=view.findViewById(R.id.tv_light);
                tvTemp=view.findViewById(R.id.tv_temp);
                wendu=view.findViewById(R.id.wudu);
                wendu1=view.findViewById(R.id.wudu1);
                tvText=view.findViewById(R.id.textView14_1);
                tvFan1=view.findViewById(R.id.tv_fan1);
                zsu=view.findViewById(R.id.conn_tv_fan_status1);
                tvMotor=view.findViewById(R.id.tv_);
                kt=view.findViewById(R.id.conn_tv_status);
                k=view.findViewById(R.id.k);
                tval=view.findViewById(R.id.home_tv_al);
                tvStatus=view.findViewById(R.id.conn_tv_status1);
                dcid=view.findViewById(R.id.PID);
                wenduyuzhi=view.findViewById(R.id.wuduyuzhi);
                gzyuzhi=view.findViewById(R.id.gzyuzhi);
                tg=view.findViewById(R.id.yu);
                yu=view.findViewById(R.id.yu1);
        }
        private void initListener() {
                wenduyuzhi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                String TG=tg.getText().toString().trim();
                                String setTemp1 = etTemp1.getText().toString().trim();
                                String setTemp2 = etTemp2.getText().toString().trim();
                                homeCtr.wuduyuzhi(setTemp1,setTemp2);
                                homeCtr.sendLight("Hwctg0103"+TG+"T");
                                T2=TG;
                        }
                });
                gzyuzhi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                String m=et.getText().toString().trim();
                                if (m.length()==3){
                                        StringBuilder sb = new StringBuilder(m);
                                        sb.insert(0,"000");
                                        homeCtr.sendLight("Hwcie0210illk"+sb+"T");
                                }else {
                                        StringBuilder sb = new StringBuilder(m);
                                        homeCtr.sendLight("Hwcie0210illk"+sb+"T");
                                }
                        }
                });
        }
        /**
         * 控制设置
         */
        private void sensorCtrl(Switch swView, int type, String deviceNo,String m) {
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
                UserDao userDao = UserDao.getInstance(getContext());
                if(sensorDevice.getType().endsWith(SensorType.SENSOR_HE)){
                        int len = Integer.parseInt(tempData.substring(7, 9));
                        String type = tempData.substring(3, 5);
                        String data = tempData.substring(9, 9 + len);
                        int h = data.indexOf('h');//h字符在字符串中的位置
                        String tem = data.substring(2, h);//温度数据
                        String hum = data.substring(h + 1);//湿度数据
                        wendu1.setText("厢外温度："+tem + "℃\t\t");//设置温度
                        Tout=tem;
                        userDao.savedb(Tout,"厢外温度",name);
                        }
                if(sensorDevice.getType().endsWith(SensorType.SENSOR_WD)){
                        int len = Integer.parseInt(tempData.substring(7, 9));
                        String type = tempData.substring(3, 5);
                        String data = tempData.substring(9, 9 + len);
                        int h = data.indexOf('h');//h字符在字符串中的位置
                        String tem = data.substring(2, h);//温度数据
                        String hum = data.substring(h + 1);//湿度数据
                        wendu.setText("厢内温度："+tem + "℃\t\t");//设置温度
                        tvText.setText("湿度："+hum + "%" + "\t\t");//设置湿度
                        Tin=tem;SD=hum;
                        userDao.savedb(Tin,"厢内温度",name);
                        userDao.savedb(SD,"湿度",name);
                        String TimDH = etTemp1.getText().toString().trim();
                        String TimDL = etTemp2.getText().toString().trim();
                        String ethum=yu.getText().toString().trim();
                                if(Float.parseFloat(ethum)<Float.parseFloat(hum)){
                                        homeCtr.sendLight("Hwclb0402onT");
                                }else {
                                        homeCtr.sendLight("Hwclb0403offT");
                                }
                        if(k.isChecked()){
                        if (Float.parseFloat(Tin)>=Float.parseFloat(TimDH)){
                                if (Float.parseFloat(Tin)>Float.parseFloat(Tout)){
                                        T0= Float.parseFloat(Tin)-Float.parseFloat(Tout);
                                        if(Float.parseFloat(T2)>T0){
                                                if(T0>1) {
                                                        homeCtr.sendLight("Hwcaf0802onT");
                                                        homeCtr.sendLight("Hwdsr0906rson00T");
                                                }
                                                else if (T0<1){
                                                        homeCtr.sendLight("Hwcaf08003offT");
                                                        homeCtr.sendLight("Hwdsr0906rson00T");
                                                }
                                        }else {
                                                homeCtr.sendLight("Hwczl0503100T");
                                        }
                                }else {
                                        homeCtr.sendLight("Hwcaf0802onT");
                                        homeCtr.sendLight("Hwdsr0906rson00T");
                                }
                        }
                        if (Float.parseFloat(Tin)<Float.parseFloat(TimDL)){
                                if (Float.parseFloat(Tin)<Float.parseFloat(Tout)){
                                        T0= Float.parseFloat(Tout)-Float.parseFloat(Tin);
                                        if(Float.parseFloat(T2)<T0){
                                                if (T0<1){
                                                homeCtr.sendLight("Hwcaf0803offT");
                                                homeCtr.sendLight("Hwdsr0906rsoff00T");}
                                                else if (T0>1){
                                                        homeCtr.sendLight("Hwcaf0802onT");
                                                        homeCtr.sendLight("Hwdsr0906rsoff00T");
                                                }
                                        }else {
                                                homeCtr.sendLight("Hwczl0503100T");
                                        }

                                }
                                else {
                                        homeCtr.sendLight("Hwdsr0804afonT");
                                        homeCtr.sendLight("Hwdsr0906rsoff00T");
                                }
                        }
                        }
                }
                if (sensorDevice.getType().endsWith(SensorType.SENSOR_BS)){
                        String s=tempData.substring(9,17);
                        dcid.setText("PID:"+s);
                        String n=tempData.substring(16,17);
                        name=n;
                }
                if (sensorDevice.getType().endsWith(SensorType.SENSOR_ZL)){
                        String sudu=tempData.substring(9,12);
                        zsu.setText(sudu);
                }
                if (sensorDevice.getType().endsWith(SensorType.SENSOR_IE)) {
                        if (tempData.contains("illk")){
                                String gzyz=tempData.substring(13,19);
                                et.setText(gzyz);
                        }
                        //光照度消息
                        String sendIeValue = "0";
                        Message message = new Message();
                        message.what = IE_MSG_CODE;
                        message.obj = tempData;
                        mMainHandler.sendMessage(message);
                        //LogUtils.i("光照度传感器 type=" + sensorDevice.getType());
                        //LogUtils.i("光照度传感器 number=" + sensorDevice.getNumber());
                        String ieData = tempData.substring(9, 9 + Integer.parseInt(tempData.substring(7, 9)));
                        Float ieValue = Float.parseFloat(ieData);
                        gz=ieData;
                        userDao.savedb(gz,"光照",name);
                        String maxValue=et.getText().toString().trim();
                        IE_MAX_VALUE = Integer.valueOf(maxValue);
                        //如果当前的光照度的值大于设置的最大值
                        if(k.isChecked()){
                        if(ieValue<Float.parseFloat(maxValue)){
                               homeCtr.sendLight("Hwcal0603100T");
                        }else  {
                                homeCtr.sendLight("Hwcal0603000T");
                        }
                        }
                }
                //风扇
                        if (sensorDevice.getType().endsWith(SensorType.SENSOR_FAN)) {
                                if (TextUtils.isEmpty(fanDeviceNo)) {
                                        LogUtils.i("风扇编号:" + sensorDevice.getNumber());
                                        fanDeviceNo = sensorDevice.getNumber();
                                        num = sensorDevice.getNumber();
                                }
                                //状态设置
                                sensorStatusHandleData(tvStatus, tempData);}
                //可调灯
                if (sensorDevice.getType().endsWith(SensorType.SENSOR_AL)) {
                        String light=tempData.substring(9,12);
                        tval.setText(light+"%");
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
                        if(tempData.contains("on")){
                              kt.setText("高功率");
                        }
                        if(tempData.contains("off")){
                              kt.setText("低功率");
                        }else kt.setText("关闭");

                }
        }

        /***
         *
         * 传感器状态_接收数据处理
         *
         * **/
        private void sensorStatusHandleData(TextView swView, String recvData) {
                if (recvData.contains("on")) {
                        //LogUtils.i("返回状态  开");
                        swView.setText("高功率");
                } else if (recvData.contains("off")) {
                        //LogUtils.i("返回状态  关");
                        swView.setText("低功率");
                }
        }
        private void updateIeValue(String data) {
                String ieData = data.substring(9, 9 + Integer.parseInt(data.substring(7, 9)));
                Float ieRecv = Float.parseFloat(ieData);
                String ieValue = (int) Math.floor(ieRecv) + "LUX"; //Hwdie0106002000T
                tvIe.setText(ieValue);
        }

        @Override
        public void onDestroy() {
                super.onDestroy();
                //取消注册事件
                EventBus.getDefault().unregister(this);
        }
}


