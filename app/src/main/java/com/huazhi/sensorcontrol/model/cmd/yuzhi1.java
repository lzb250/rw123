package com.huazhi.sensorcontrol.model.cmd;

import com.huazhi.sensorcontrol.model.Command;

public class yuzhi1 implements Command {
    private String num;
    private String num1;
    public yuzhi1(String num,String num1){
        this.num1 = num1;
        this.num = num;
    }
    @Override
    public String execute() {
        return "Hwdhe0109tem+"+num+"+"+num1+"T";//打开
    }
}
