package com.moxun.s2v.utils;

/**
 * Created by moxun on 15/12/15.
 */
public class DensityUtil {
    public static String px2dp(int px,String dpi) {
        float standardDPI = 160.0f;
        int result = -1;

        //fucking java 1.6
        if (dpi.equals("nodpi")) {
            result = px;
        } else if (dpi.equals("ldpi")) {
            result = (int) ((float)px / 120.0 * standardDPI);
        } else if (dpi.equals("mdpi")) {
            result = (int) ((float)px / 160.0 * standardDPI);
        } else if (dpi.equals("hdpi")) {
            result = (int) ((float)px / 240.0 * standardDPI);
        } else if (dpi.equals("xhdpi")) {
            result = (int) ((float)px / 320.0 * standardDPI);
        } else if (dpi.equals("xxhdpi")) {
            result = (int) ((float)px / 480.0 * standardDPI);
        } else if (dpi.equals("xxxhdpi")) {
            result = (int) ((float)px / 640.0 * standardDPI);
        } else {
            result = px;
        }
        return String.valueOf(result);
    }
}
