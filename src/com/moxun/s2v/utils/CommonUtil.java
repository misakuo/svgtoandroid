package com.moxun.s2v.utils;

import com.intellij.psi.xml.XmlAttribute;

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

    private static boolean isInteger(float f) {
        return f - (int )f == 0;
    }
}
