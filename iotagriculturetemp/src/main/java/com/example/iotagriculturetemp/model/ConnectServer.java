package com.example.iotagriculturetemp.model;

import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.iotagriculturetemp.R;

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

public class ConnectServer {
    private String ipaddr;
    private String port;
    private ExecutorService mThreadPool;
    private Socket socket = null;
    InputStream isStream;
    OutputStream outputStream;
    private boolean isRun = false;  //是否正在运行
    //关闭socket
    public void stopLink() {
        closeSocket();
    }
    public void initMineStatus(){
        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
    }
    public void startConnSocket(){
        closeSocket(); //先关闭
        connectSocket();//连接socket
        recvSocketData();//接收socket数据
    }

//    链接socket服务器
    private void connectSocket() {
        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int portInt = Integer.parseInt(port);
                    // 创建Socket对象 & 指定服务端的IP 及 端口号
                    Log.i("ip port ", "run: ip: "+ipaddr+" port:"+port);
                    socket = new Socket(ipaddr, portInt);
                    // 判断客户端和服务器是否连接成功
                    Log.i("socket:","socket.isConnected()=" + socket.isConnected());

                    //如果连接
                    if (socket.isConnected()) {
                        //已连接
                        isRun = true;
                    } else {
                        //未连接
                        isRun = false;
                    }
                } catch (IOException e) {
                    isRun = false;
                    e.printStackTrace();
                }
            }
        });
    }
//    接受服务器消息
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
                            Log.i("recvData:","recvData="+recvData);
                            //&&recvData.endsWith("T")
//                            if (recvData.startsWith("H")&&recvData.endsWith("T")) {
//                                String[] tempStrArray = recvData.split("T");
//                                // LogUtils.i("=========tempStrArray.length="+tempStrArray.length);
//                                int tempLength = tempStrArray.length;
//                                for (int i = 0; i < tempLength; i++) {
//                                    String tempData = tempStrArray[i];
//                                    if (!tempData.endsWith("T")) {
//                                        tempData = tempData + "T";
//                                    }
//                                    String content = tempData;
//                                    //LogUtils.i("content="+content);
//                                    EventBus.getDefault().post(content);//发送事件
//                                }
//                            }
                            EventBus.getDefault().post(recvData);//发送事件
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
//  断开服务器连接
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
    private void sendMsg(final String msg) {
        Log.i("sendMsg","sendMsg 发送消息 msg =" + msg);

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
    public void writeCmd(String cmd){
        sendMsg(cmd);
    }
//  检查ip地址正确
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
//  传入IP，端口
    public void setData(String ip,String port) {
        this.ipaddr = ip;
        this.port = port;
    }
    //判断是否链接成功
    public boolean isConnect()
    {
        return isRun;
    }

    public void resumeState(Fragment fragment, ImageView view){
        if (isRun) {
            view.setImageResource(R.drawable.wifi_true);
        } else {
            view.setImageResource(R.drawable.wifi_false);
        }
    }
}
