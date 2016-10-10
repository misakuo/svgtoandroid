package com.moxun.s2v.utils;


import com.google.gson.Gson;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.moxun.s2v.Configuration;

import javax.swing.*;
import java.awt.*;

/**
 * Created by moxun on 16/3/10.
 */
public class UpdateUtil {
    private static int VERSION = Integer.MAX_VALUE;
    private static final String URL = "https://raw.githubusercontent.com/misakuo/svgtoandroid/master/version.json";

    public static void checkUpdate(final Project project) {
        if (!Configuration.isAutoCheckUpdate()) {
            Logger.info("Ignore check update");
            return;
        }
        VERSION = Integer.valueOf(CommonUtil.loadMetaInf("vcode", VERSION + ""));
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
                                    CommonUtil.showTopic(project,
                                            "Plugin SVG to VectorDrawable Update",
                                            "version: " + data.version + "<br>" + data.desc,
                                            NotificationType.INFORMATION);
                                }
                            });
                        }
                    }
                }
            }
        }).start();
    }
}
