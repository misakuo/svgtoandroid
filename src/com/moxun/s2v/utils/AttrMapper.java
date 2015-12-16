package com.moxun.s2v.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * http://developer.android.com/reference/android/graphics/drawable/VectorDrawable.html
 * Created by moxun on 15/12/16.
 */
public class AttrMapper {
    private static Map<String,String> mapper = new HashMap<String, String>();

    static {
        mapper.clear();
        mapper.put("id", "android:name");
        mapper.put("fill", "android:fillColor");
        mapper.put("fill-opacity", "android:fillAlpha");
        mapper.put("stroke","android:strokeColor");
        mapper.put("stroke-opacity","android:strokeAlpha");
        mapper.put("stroke-width","android:strokeWidth");
        mapper.put("stroke-linejoin","android:strokeLIneJoin");
        mapper.put("stroke-miterlimit","android:strokeMiterLimit");
        mapper.put("stroke-linecap","android:strokeLineCap");
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
        if (!mapper.containsKey(svgAttrName)) {
            Logger.warn("Skipping attr [" + svgAttrName + "], because it not supported by Android.");
        }
        return mapper.get(svgAttrName);
    }
}
