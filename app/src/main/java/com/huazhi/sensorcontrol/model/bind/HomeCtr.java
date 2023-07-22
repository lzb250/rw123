package com.huazhi.sensorcontrol.model.bind;

import androidx.databinding.BaseObservable;

import com.huazhi.sensorcontrol.model.Command;
import com.huazhi.sensorcontrol.model.cmd.AirCommand;
import com.huazhi.sensorcontrol.model.cmd.LightCommand;
import com.huazhi.sensorcontrol.model.cmd.RollerCommand;
import com.huazhi.sensorcontrol.model.cmd.yuzhi;
import com.huazhi.sensorcontrol.model.cmd.yuzhi1;
import com.huazhi.sensorcontrol.utils.GlobalDefs;

import org.greenrobot.eventbus.EventBus;

public class HomeCtr extends BaseObservable {

    private Command command;

    /**
     * 发送命令（风扇等等）
     */
    public void sendID(int type, boolean isChecked, String num) {
        try {
            boolean can = false;
            switch (type) {
                //风扇
                case GlobalDefs.SENSOR_TYPE_FAN:
                    setCommand(new AirCommand(isChecked, num));
                    can = true;
                    break;
            }

            if (can) {
                //LogUtils.i("sendID type="+type);
                EventBus.getDefault().post(this.command);//发送事件
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void gzyuzhi(String num){
        try {
            setCommand(new yuzhi(num));
            EventBus.getDefault().post(this.command);//发送事件
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void wuduyuzhi(String num,String num1){
        try {
            setCommand(new yuzhi1(num,num1));
            EventBus.getDefault().post(this.command);//发送事件
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    /***
     *
     * 可调灯
     *
     * */
    public void sendLight(String cmd) {
        try {
            setCommand(new LightCommand(cmd));
            //LogUtils.i("sendID type="+type);
            EventBus.getDefault().post(this.command);//发送事件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送命令（卷帘电机）
     */
    public void sendMotorID(int type, String num) {
        try {
            boolean can = false;
            switch (type) {
                //正转
                case GlobalDefs.MOTOR_TYPE_1:
                    setCommand(new RollerCommand(type, num));
                    can = true;
                    break;
                //反转
                case GlobalDefs.MOTOR_TYPE_2:
                    setCommand(new RollerCommand(type, num));
                    can = true;
                    break;
                //空转
                case GlobalDefs.MOTOR_TYPE_3:
                    setCommand(new RollerCommand(type, num));
                    can = true;
                    break;

            }
            if (can) EventBus.getDefault().post(this.command);//发送事件

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

}
