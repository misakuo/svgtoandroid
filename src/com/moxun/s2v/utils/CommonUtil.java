package com.moxun.s2v.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.moxun.s2v.Configuration;
import com.sun.org.apache.xerces.internal.jaxp.SAXParserImpl;
import com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogReader;
import com.sun.xml.internal.txw2.Document;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.rng.SAXSchemaReader;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * Created by moxun on 16/10/9.
 */
public class CommonUtil {

    public static void dumpAttrs(String tag, List<XmlAttribute> attrs) {
        if (attrs == null) {
            return;
        }

        String ret = "[";
        for (XmlAttribute attr : attrs) {
            ret += attr.getName() + ":" + attr.getValue() + ",";
        }
        ret = ret.substring(0, ret.length() - 2) + "]";
        Logger.debug(tag + ": " + ret);
    }

    public static String formatFloat(float f) {
        return isInteger(f) ? (int)f + "" : f + "";
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
            if (!Character.isLetter(chars[i])) {
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

    private static boolean isInteger(float f) {
        return f - (int )f == 0;
    }
}
