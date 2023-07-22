package com.huazhi.sensorcontrol.model.bind;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.Fragment;

import com.huazhi.sensorcontrol.BR;
import com.huazhi.sensorcontrol.R;
import com.huazhi.sensorcontrol.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MineStatus extends BaseObservable{
    private String ipaddr;
    private String port;
    private String descri;

    private ImageView wifiview;
    public static String localIP = "";
    public static String wifissid = "";

    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;
    // Socket变量
    private Socket socket = null;
    // 输入流对象
    InputStream isStream;

    /**
     * 发送消息到服务器 变量
     */
    // 输出流对象
    OutputStream outputStream;
    private boolean isRun = false;  //是否正在运行

    public void resumeState(Fragment fragment, ImageView view){
        //设置WIFI名称等
        setDescri("WIFI名称: " + wifissid + "\n手机IP: " + localIP);

        if (isRun) {
            view.setImageResource(R.drawable.wifi_true);
        } else {
            view.setImageResource(R.drawable.wifi_false);
        }
        wifiview = view;
    }

    //本地WiFi网络状态
    public void wifiStatus(View view) {
        boolean isWifi = false;
        ConnectivityManager mConnectivity = (ConnectivityManager) view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info != null)//有网络
        {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d("hello", "wifi在线");
                isWifi = true;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d("hello", "移动网络");
                isWifi = true;
                return;
            }
        } else {
            localIP = "...";
            wifissid = "...";
            Log.d("hello", "没有网络");
        }

        if (!isWifi) return;                  //如果不是wifi，直接返回

        WifiManager wifiMan = (WifiManager) view.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo infowifi = wifiMan.getConnectionInfo();
        int ipAddress = infowifi.getIpAddress();
        String ssid = infowifi.getSSID();   // 获得本机所链接的WIFI名称
        String ipString = "";               // 本机在WIFI状态下路由分配给的IP地址.

        if (ipAddress != 0) {
            ipString = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                    + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
            localIP = ipString;

            if (!TextUtils.isEmpty(ssid)) {
                String newSsid = ssid.replace("\"", "");
                wifissid = newSsid;
            } else {
                wifissid = ssid;
            }

            //如何获取失败重新获取
            if (wifissid.contains("unknown")) {
                wifissid = getWIFISSID(view);
            }
        }
    }

    public static String getWIFISSID(View view) {
        String ssid = "unknown id";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WifiManager mWifiManager = (WifiManager) view.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            assert mWifiManager != null;
            WifiInfo info = mWifiManager.getConnectionInfo();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return info.getSSID();
            } else {
                return info.getSSID().replace("\"", "");
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {
            ConnectivityManager connManager = (ConnectivityManager) view.getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connManager != null;
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo() != null) {
                    return networkInfo.getExtraInfo().replace("\"", "");
                }
            }
        }
        return ssid;
    }

    /***
     *
     * 关闭socket
     *
     * **/
    public void stopLink() {
        closeSocket();//关闭socket
    }

    /***
     *
     * 初始化
     *
     * **/
    public void initMineStatus(){
        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
    }

    /***
     *
     * 开始连接socket
     *
     * ***/
    public void startConnSocket(){
        closeSocket(); //先关闭
        connectSocket();//连接socket
        recvSocketData();//接收socket数据
    }

    /**
     * 连接socket
     ***/
    private void connectSocket() {
        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int portInt = Integer.parseInt(port);
                    // 创建Socket对象 & 指定服务端的IP 及 端口号
                    socket = new Socket(ipaddr, portInt);
                    // 判断客户端和服务器是否连接成功
                    LogUtils.i("socket.isConnected()=" + socket.isConnected());

                    //如果连接
                    if (socket.isConnected()) {
                        //已连接
                        isRun = true;
                    } else {
                        //连接
                        isRun = false;
                    }
                } catch (IOException e) {
                    isRun = false;
                    e.printStackTrace();
                }
            }
        });
    }

    /***
     *
     * 接受socket数据
     *
     * **/
    private void recvSocketData() {
        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    // 步骤1：创建输入流对象InputStream
                    while (true) {
                        if (socket != null) {
                            // 步骤1：创建输入流对象InputStream
                            isStream = socket.getInputStream();

                            // 步骤2：创建输入流读取器对象 并传入输入流对象
                            InputStream inputStream = socket.getInputStream();
                            DataInputStream input = new DataInputStream(inputStream);

                            // 步骤3：接收服务器发送过来的数据
                            byte[] b = new byte[150];
                            int length = input.read(b);
                            String recvData = "";
                            try {
                                recvData = new String(b, 0, length, "utf-8");
                            } catch (Exception e) {

                            }
                            LogUtils.i("recvData="+recvData);
                            //&&recvData.endsWith("T")
                            if (recvData.startsWith("H")&&recvData.endsWith("T")) {
                                String[] tempStrArray = recvData.split("T");
                                // LogUtils.i("=========tempStrArray.length="+tempStrArray.length);
                                int tempLength = tempStrArray.length;
                                for (int i = 0; i < tempLength; i++) {
                                    String tempData = tempStrArray[i];
                                    if (!tempData.endsWith("T")) {
                                        tempData = tempData + "T";
                                    }
                                    String content = tempData;
                                    //LogUtils.i("content="+content);
                                    EventBus.getDefault().post(content);//发送事件
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    //未连接
                    isRun = false;
                    e.printStackTrace();
                }
            }
        });
    }

    /***
     *
     * 关闭socket
     *
     * **/
    private void closeSocket() {
        try {
            // 断开 客户端发送到服务器 的连接，即关闭输出流对象OutputStream
            if (outputStream != null) {
                outputStream.close();
            }

            if (isStream != null) {
                isStream.close();
            }

            // 断开 服务器发送到客户端 的连接，即关闭输入流读取器对象BufferedReader
            //br.close();

            // 最终关闭整个Socket连接
            if (socket != null) {
                socket.close();
            }

            // 判断客户端和服务器是否已经断开连接
            //LogUtils.i("socket.isConnected()=" + socket.isConnected());
        } catch (IOException e) {
            isRun = false;
            e.printStackTrace();
        }
    }

    /***
     *
     *  发送消息
     *
     * ***/
    private void sendMsg(final String msg) {
        LogUtils.i("sendMsg 发送消息 msg =" + msg);

        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (socket != null) {
                    try {
                        // 步骤1：从Socket 获得输出流对象OutputStream
                        // 该对象作用：发送数据
                        outputStream = socket.getOutputStream();
                        // 步骤2：写入需要发送的数据到输出流对象中
                        outputStream.write(msg.getBytes("utf-8"));
                        // 步骤3：发送数据到服务端
                        outputStream.flush();
                    } catch (IOException e) {
                        isRun = false;
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //往服务器写数据
    public void writeCmd(final String cmd){
        sendMsg(cmd);
    }

    /**
     * 检验IP是否正规
     **/
    public boolean checkIP(String ip) {
        boolean isIP = false;
        if (!TextUtils.isEmpty(ip)) {
            //IP地址验证规则
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(ip);
            boolean rs = matcher.matches();// 字符串是否与正则表达式相匹配
            isIP = rs;
        }
        return isIP;
    }

    @Bindable
    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
        //notifyPropertyChanged(BR.ipaddr);
    }

    @Bindable
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
        //notifyPropertyChanged(BR.port);
    }
    @Bindable
    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
        notifyPropertyChanged(BR.descri);
    }
}
