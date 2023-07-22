package com.huazhi.sensorcontrol.model.cmd;

import com.huazhi.sensorcontrol.model.Command;

//加热器
public class JiareCommand implements Command {
    private boolean isCheck;
    private String num;

    public JiareCommand(boolean isCheck, String num) {
        this.isCheck = isCheck;
        this.num = num;
    }

    @Override
    public String execute() {
        if (isCheck){
            return "Hwccr" +num+ "02onT0";
        }else{
            return "Hwccr" +num+ "03offT";
        }
    }
}
