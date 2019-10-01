package com.readerxml;

import java.util.TimerTask;


public class Execute extends TimerTask{
    @Override
    public void run(){
        try {
            LectorEmail lector = new LectorEmail();
            lector.configuracionEmail();
        }catch (Exception e) {
            e.printStackTrace();
        }  
        System.out.println("#############Email Recibido OK...");
    }
}
