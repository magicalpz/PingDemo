package com.jyn.pingtest;

import android.os.Debug;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PingUtil {
    public static String ping(String domain) {
        String command = "/system/bin/ping -c 1 -w 1 "+domain;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            String delayString = "";
            StringBuilder sb = new StringBuilder();
            boolean success = false;
            while (null != (line = reader.readLine())) {
                sb.append(line);
               if (line.contains("time=")){
                   int startIndex = line.indexOf("time=");
                   int endIndex = line.indexOf("ms");
                   delayString = line.substring(startIndex+5,endIndex);
                   success = true;
                   break;
               }
            }
            reader.close();
            is.close();
            return success?delayString: sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != process) {
                process.destroy();
            }
        }
        return null;
    }

}
