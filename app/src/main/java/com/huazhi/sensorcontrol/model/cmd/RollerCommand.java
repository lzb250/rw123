package com.huazhi.sensorcontrol.model.cmd;

import com.huazhi.sensorcontrol.model.Command;

//卷帘电机
public class RollerCommand implements Command {
    private int type;
    private String num;

    public RollerCommand(int type, String num) {
        this.type = type;
        this.num = num;
    }

    @Override
    public String execute() {
        String order = "";
        switch (type) {
            //正转
            case 1:
                order = "Hwcrs" + num + "04on05T";//正转  如：Hwcrs0104on00T
                break;
            //反转
            case 2:
                order = "Hwcrs" + num + "05off05T";//反转  如：Hwcrs0105off00T
                break;
            //空转
            case 3:
                order = "Hwcrs" + num + "040000T";//停止
                break;
        }
        return order;
    }
}
