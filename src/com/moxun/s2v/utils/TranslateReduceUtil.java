package com.moxun.s2v.utils;

import org.apache.commons.lang.StringUtils;

/**
 * Created by moxun on 16/3/10.
 */
public class TranslateReduceUtil {
    //only reduce translate attr now.
    public static String reduce(String t1, String t2) {
        float t1tx = 0f, t1ty = 0f, t2tx = 0f, t2ty = 0f;
        t1 = t1.replaceAll("[\\s]", "").replaceAll("\\)", ")#");
        t2 = t2.replaceAll("[\\s]", "").replaceAll("\\)", ")#");
        if (t1.endsWith("#")) {
            t1 = t1.substring(0, t1.length() - 1);
        }

        if (t2.endsWith("#")) {
            t2 = t2.substring(0, t2.length() - 1);
        }
        String[] t1s = t1.split("#");
        String[] t2s = t2.split("#");

        for (String s : t1s) {
            String translate = StringUtils.substringBetween(s, "translate(", ")");
            if (translate != null) {
                float x = Float.valueOf(translate.split(",")[0]);
                float y = Float.valueOf(translate.split(",")[1]);
                t1tx += x;
                t1ty += y;
            }
        }

        for (String s : t2s) {
            String translate = StringUtils.substringBetween(s, "translate(", ")");
            if (translate != null) {
                float x = Float.valueOf(translate.split(",")[0]);
                float y = Float.valueOf(translate.split(",")[1]);
                t2tx += x;
                t2ty += y;
            }
        }

        float rx = t1tx + t2tx;
        float ry = t1ty + t2ty;
        String r = "translate(" + rx + "," + ry + ")";
        return r;
    }

    public static void main(String[] args) {
        System.out.println(reduce("translate(138.000000, 15.000000) scale(-1, 1) translate(-138.000000, -15.000000) translate(123.000000, 0.000000)", "translate(49.000000, 88.000000)"));
    }
}
