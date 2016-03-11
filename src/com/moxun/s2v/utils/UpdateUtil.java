package com.moxun.s2v.utils;


import com.google.gson.Gson;

import javax.swing.*;

/**
 * Created by moxun on 16/3/10.
 */
public class UpdateUtil {
    private static final int VERSION = 4;
    private static final String URL = "https://raw.githubusercontent.com/misakuo/svgtoandroid/master/version.js";

    public static void checkUpdate(final JLabel status) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = HttpUtil.doGet(URL);
                if (s != null) {
                    Gson gson = new Gson();
                    final UpdateData data = gson.fromJson(s, UpdateData.class);
                    if (data != null) {
                        if (data.versionCode > VERSION) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    status.setVisible(true);
                                    status.setText("Update is valid:" + data.desc);
                                }
                            });
                        }
                    }
                }
            }
        }).start();
    }
}
