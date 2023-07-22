package com.huazhi.sensorcontrol.model.cmd;

import com.huazhi.sensorcontrol.model.Command;

/**
 *
 * 可调灯
 *
 * ***/
public class LightCommand implements Command {

    private String cmd;

    public LightCommand(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String execute() {
        return cmd;
    }
}
