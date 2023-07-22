package com.huazhi.sensorcontrol.model.cmd;

import com.huazhi.sensorcontrol.model.Command;

//风扇
public class AirCommand implements Command {
    private boolean isCheck;
    private String num;
    private String m;

    public AirCommand(boolean isCheck, String num) {
        this.isCheck = isCheck;
        this.num = num;
        this.m=m;
    }

    @Override
    public String execute() {
        if (isCheck){
            return "Hwcaf" +num+ "02onT\n";//打开
        }else{
            return "Hwcaf" +num+ "03offT\n";//关闭
        }
    }
}
