package com.huazhi.sensorcontrol.utils;

public class GlobalDefs {

    // 共享文件数据库名
    public static final String SENSOR_CONTROL_SHARED_PREFS_NAME = "sensor_control_setting";

    public static final String DEFAULT_IP = "192.168.1.200"; //默认ip
    public static final String DEFAULT_PORT = "6004"; //默认端口

    public static final String KEY_IP_ADDRESS = "IPaddress"; // IP
    public static final String KEY_PORT = "port"; // 端口号码

    public static final int SENSOR_TYPE_ALARM = 1;//声光报警器
    public static final int SENSOR_TYPE_LOCK = 2;//电磁锁
    public static final int SENSOR_TYPE_FAN = 3;//风扇
    public static final int SENSOR_TYPE_LIGHT = 4;//可调灯

    public static final int MOTOR_TYPE_1 = 1;//正转
    public static final int MOTOR_TYPE_2 = 2;//反转
    public static final int MOTOR_TYPE_3 = 3;//停止
}
