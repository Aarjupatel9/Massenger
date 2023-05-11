package com.example.mank.ThreadPackages;

public class StatusForThread {
    private int value;
    public  StatusForThread(int InitialValue){
        this.value =InitialValue;
    }
    public int getValue(){
        return this.value;
    }
    public void setValue(int value){
        this.value = value;
    }
}
