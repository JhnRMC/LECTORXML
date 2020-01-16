package com.readerxml;


import java.util.Timer;
import com.Log;
public class DBScheduler {

    public void callScheduler() throws Exception {
        System.out.println("Iniciando Servicio...");
        Log.registrar();
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new Execute(), getTimePrecision(Constants.delay), getTimePrecision(Constants.timetoquery));
    }

    public long getTimePrecision(String value) {
        long l = 0;
        String val = "";
        try {
            if (value.endsWith("d") || value.endsWith("D")) {
                val = value.substring(0, value.length() - 1);
                l = Long.parseLong(val) * 24 * 60 * 60 * 1000;
            } else if (value.endsWith("h") || value.endsWith("H")) {
                val = value.substring(0, value.length() - 1);
                l = Long.parseLong(val) * 60 * 60 * 1000;
            } else if (value.endsWith("m") || value.endsWith("M")) {
                val = value.substring(0, value.length() - 1);
                l = Long.parseLong(val) * 60 * 1000;
            } else if (value.endsWith("s") || value.endsWith("S")) {
                val = value.substring(0, value.length() - 1);
                l = Long.parseLong(val) * 1000;
            } else {
                l = Long.parseLong(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public static void main(String[] args) {
        Log.registrar();
        try {
            while (true) {
                LectorEmail lector = new LectorEmail();
                lector.configuracionEmail();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
