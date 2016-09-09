package com.moxun.s2v.utils;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * http://developer.android.com/reference/android/graphics/drawable/VectorDrawable.html
 * Created by moxun on 15/12/16.
 */
public class AttrMapper {
    private static Map<String, String> mapper = new HashMap<String, String>();

    static {
        mapper.clear();
        mapper.put("id", "android:name");
        mapper.put("fill", "android:fillColor");
        mapper.put("fill-opacity", "android:fillAlpha");
        mapper.put("stroke", "android:strokeColor");
        mapper.put("stroke-opacity", "android:strokeAlpha");
        mapper.put("stroke-width", "android:strokeWidth");
        mapper.put("stroke-linejoin", "android:strokeLineJoin");
        mapper.put("stroke-miterlimit", "android:strokeMiterLimit");
        mapper.put("stroke-linecap", "android:strokeLineCap");
    }

    public static boolean isShapeName(String name) {
        HashSet<String> keys = new HashSet<String>();
        keys.add("path");
        keys.add("rect");
        keys.add("circle");
        keys.add("polygon");
        keys.add("polyline");
        keys.add("line");
        keys.add("ellipse");
        if (keys.contains(name)) {
            Logger.debug("attr [" + name + "] is a shape node");
        }
        return keys.contains(name);
    }

    public static String getAttrName(String svgAttrName) {
        if (!mapper.containsKey(svgAttrName) && !svgAttrName.equals("transform")) {
            Logger.warn("Skipping attr [" + svgAttrName + "], because it not supported by Android.");
        }
        return mapper.get(svgAttrName);
    }

    public static Map<String, String> getTranslateAttrs(String transAttr) {
        Map<String, String> result = new HashMap<String, String>();
        if (transAttr == null) {
            return result;
        }
        String tmp = transAttr.replaceAll(" ", ",");
        tmp = tmp.replaceAll(",,", ",");
        String translate = StringUtils.substringBetween(tmp, "translate(", ")");
        String scale = StringUtils.substringBetween(tmp, "scale(", ")");
        String rotate = StringUtils.substringBetween(tmp, "rotate(", ")");
        if (translate != null) {
            String[] txy = translate.split(",");
            if (txy.length == 1) {
                result.put("android:translateX", txy[0]);
            } else if (txy.length > 1) {
                result.put("android:translateX", txy[0]);
                result.put("android:translateY", txy[1]);
            }
        }

        if (scale != null) {
            String[] sxy = scale.split(",");
            if (sxy.length == 1) {
                result.put("android:scaleX", sxy[0]);
                result.put("android:scaleY", sxy[0]);
            } else if (sxy.length > 1) {
                result.put("android:scaleX", sxy[0]);
                result.put("android:scaleY", sxy[1]);
            }
        }

        if (rotate != null) {
            String[] rxy = rotate.split(",");
            if (rxy.length == 1) {
                result.put("android:rotation", rxy[0]);
            } else if (rxy.length == 2) {
                result.put("android:rotation", rxy[0]);
                result.put("android:pivotX", rxy[1]);
            } else if (rxy.length > 2) {
                result.put("android:rotation", rxy[0]);
                result.put("android:pivotX", rxy[1]);
                result.put("android:pivotY", rxy[2]);
            }
        }
        Logger.debug("Attrs Transformer: " + transAttr + " ===> " + result.toString());
        return result;
    }

    //test case
    public static void main(String args[]) {
        String s = "translate(100 50),scale(0.5)rotate(30,100,100)";
        System.out.println(getTranslateAttrs(s));
        String s1 = "translate(14.000000, 14.000000)";
        System.out.println(getTranslateAttrs(s1));
    }
}
