package com.moxun.s2v.utils;

import com.intellij.psi.xml.XmlTag;

import java.util.Map;

/**
 * Created by moxun on 16/3/10.
 */
public class AttrMergeUtil {
    public static XmlTag mergeAttrs(XmlTag src, Map<String, String> attrs) {
        if (src.getAttribute("id") == null && attrs.get("id") != null) {
            src.setAttribute("id", attrs.get("id"));
        }

        if (src.getAttribute("fill") == null && attrs.get("fill") != null) {
            src.setAttribute("fill", attrs.get("fill"));
        }

        if (src.getAttribute("transform") != null) {
            if (attrs.get("transform") != null) {
                String translate = attrs.get("transform");
                src.setAttribute("transform", merge(translate, src.getAttribute("transform").getValue()));
            }
        } else {
            if (attrs.get("transform") != null) {
                src.setAttribute("transform", attrs.get("transform"));
            }
        }
        if (attrs.get("transform") != null) {
            Logger.debug("Transform attrs merged:" + src.getAttribute("transform").getValue());
        }
        return src;
    }

    private static String merge(String translate, String transform) {
        transform = transform.replaceAll("[\\s]", "").replaceAll("\\)", ")#");
        String[] tmp = transform.split("#");
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i].startsWith("translate")) {
                tmp[i] = translate;
                break;
            }
        }
        String result = "";
        for (String s : tmp) {
            result += s;
        }
        return result;
    }

    //test case
    public static void main(String args[]) {
        System.out.println(merge("translate(123,456)", "translate(1,2),scale(1,3)"));
    }
}
