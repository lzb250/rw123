package com.huazhi.sensorcontrol.model.cmd;

import com.huazhi.sensorcontrol.model.Command;
//风扇
public class yuzhi implements Command {
    private String num;
    private String num1;


    public yuzhi(String num1) {
        this.num1 = num1;
    }

    @Override
    public String execute() {
            return "Hwdie0210illk"+num1+"T";//打开
    }
}
