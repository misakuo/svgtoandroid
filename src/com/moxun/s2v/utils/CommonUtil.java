package com.moxun.s2v.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlAttribute;
import com.moxun.s2v.Configuration;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by moxun on 16/10/9.
 */
public class CommonUtil {

    public static void dumpAttrs(String tag, List<XmlAttribute> attrs) {

        if (!Logger.loggable(Logger.DEBUG)) {
            return;
        }

        if (attrs == null) {
            return;
        }

        String ret = "[";
        for (XmlAttribute attr : attrs) {
            ret += attr.getName() + ":" + attr.getValue() + ",";
        }
        if (ret.lastIndexOf(",") > 0) {
            ret = ret.substring(0, ret.lastIndexOf(","));
        }
        ret = ret + "]";
        Logger.debug(tag + ": " + ret);
    }

    public static String formatFloat(float f) {
        return isInteger(f) ? (int) f + "" : f + "";
    }

    public static String formatString(String s) {
        float f = Float.valueOf(s);
        return formatFloat(f);
    }

    public static String loadMetaInf(String key, String defValue) {
        try {
            InputStream in = CommonUtil.class.getClassLoader().getResourceAsStream("/META-INF/plugin.xml");
            SAXBuilder builder = new SAXBuilder();
            org.jdom.Document document = builder.build(in);
            return document.getRootElement().getChild(key).getContent().get(0).getValue();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public static String getValidName(String s) {
        char[] chars = s.toLowerCase().replaceAll("\\s*", "").toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!Character.isLetter(chars[i]) && !Character.isDigit(chars[i])) {
                chars[i] = '_';
            }
        }
        return Configuration.getPrefix() + String.valueOf(chars);
    }

    public static void showTopic(Project project, String title, String content, NotificationType type) {
        project.getMessageBus().syncPublisher(Notifications.TOPIC).notify(
                new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID,
                        title,
                        content,
                        type));
    }

    public static int parseColor(String colorString) {
        // Use a long to avoid rollovers on #ffXXXXXX
        long color = Long.parseLong(colorString.substring(1), 16);
        if (colorString.length() == 7) {
            // Set the alpha value
            color |= 0x00000000ff000000;
        } else if (colorString.length() != 9) {
            throw new IllegalArgumentException("Unknown color");
        }
        return (int) color;
    }

    private static boolean isInteger(float f) {
        return f - (int) f == 0;
    }
}
