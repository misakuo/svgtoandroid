package com.moxun.s2v.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by moxun on 16/3/10.
 */
public class HttpUtil {
    public static String doGet(String u) {
        try {
            String reply = "";
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = rd.readLine()) != null) {
                reply += line;
            }
            conn.disconnect();
            return reply;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String args[]) {
        System.out.println(doGet("https://raw.githubusercontent.com/misakuo/svgtoandroid/master/version.js"));
    }
}
