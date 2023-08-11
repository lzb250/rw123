package com.example.iotagriculturetemp.model.Cmd;

import com.example.iotagriculturetemp.model.Command;

public class StandardCommend implements Command {
    private int types;
    private String CommendFor;

    @Override
    public String execute() {
        return CommendFor;
    }
    public void writeCommend(int num,String CommendF)
    {
        this.types = num;
        this.CommendFor = CommendF;
    }

    public int getTypes() {
        return types;
    }
    public String getCommendFor(){
        return CommendFor;
    }
}
