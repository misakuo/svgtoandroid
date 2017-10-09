package com.moxun.s2v.utils;

import com.intellij.psi.xml.XmlTag;
import java.util.regex.Pattern;

/**
 * Created by moxun on 15/12/16.
 */
public class SVGAttrParser {
    public static String rectToPath(double x, double y, double width, double height, double rx, double ry) {
        if (rx == 0 && ry == 0) {
            return new SVGPathBuilder()
                    .moveto(x, y)
                    .lineto(x + width, y)
                    .lineto(x + width, y + height)
                    .lineto(x, y + height)
                    .closepath()
                    .build();
        } else {
            double x0 = x, x1 = x + rx, x2 = x + width - rx, x3 = x + width;
            double y0 = y, y1 = y + ry, y2 = y + height - ry, y3 = y + height;

            return new SVGPathBuilder()
                    .moveto(x2, y0).quadto(x3, y0, x3, y1)
                    .lineto(x3, y2).quadto(x3, y3, x2, y3)
                    .lineto(x1, y3).quadto(x0, y3, x0, y2)
                    .lineto(x0, y1).quadto(x0, y0, x1, y0)
                    .closepath()
                    .build();
        }
    }

    public static String circleToPath(double cx, double cy, double r) {
        return new SVGPathBuilder()
                .moveto(cx - r, cy)
                .arcrel(r, r, 0, false, true, 2 * r, 0)
                .arcrel(r, r, 0, false, true, -2 * r, 0)
                .closepath()
                .build();
    }

    public static String polygonToPath(String points) {
        String[] temp = points.split("\\s+");
        if (temp.length > 0 && temp.length % 2 == 0) {
            String result = "";
            for (int i = 0; i < temp.length; i++) {
                if (i % 2 == 0) {
                    result = result + " " + temp[i];
                } else {
                    result = result + "," + temp[i];
                }
            }
            points = result;
        }
        String r = "M" + points.trim().replaceAll("\\s+", " L");
        if (Pattern.compile("[L$]").matcher(r).find()) {
            r = r.substring(0, r.length());
        }
        r += "z";
        return r;
    }

    public static String ellipseToPath(double cx, double cy, double rx, double ry) {
        double ctlX = rx * 0.5522847498307935;
        double ctlY = ry * 0.5522847498307935;

        return new SVGPathBuilder()
                .moveto(cx, cy - ry)
                .cubicto(cx + ctlX, cy - ry, cx + rx, cy - ctlY, cx + rx, cy)
                .cubicto(cx + rx, cy + ctlY, cx + ctlX, cy + ry, cx, cy + ry)
                .cubicto(cx - ctlX, cy + ry, cx - rx, cy + ctlY, cx - rx, cy)
                .cubicto(cx - rx, cy - ctlY, cx - ctlX, cy - ry, cx, cy - ry)
                .closepath()
                .build();
    }

    public static String getPathData(XmlTag tag) {
        String type = tag.getName();
        if (type.equals("path")) {
            return tag.getAttributeValue("d");
        } else if (type.equals("rect")) {
            double x = getValueF(tag, "x");
            double y = getValueF(tag, "y");
            double width = getValue(tag, "width");
            double height = getValue(tag, "height");
            double rx = getValueF(tag, "rx");
            double ry = getValueF(tag, "ry");
            if (ry == 0) {
                ry = rx;
            } else if (rx == 0) {
                rx = ry;
            }
            return rectToPath(x, y, width, height, rx, ry);
        } else if (type.equals("circle")) {
            double cx = getValueF(tag, "cx");
            double cy = getValueF(tag, "cy");
            double r = getValueF(tag, "r");
            return circleToPath(cx, cy, r);
        } else if (type.equals("ellipse")) {
            double cx = getValueF(tag, "cx");
            double cy = getValueF(tag, "cy");
            double rx = getValueF(tag, "rx");
            double ry = getValueF(tag, "ry");
            return ellipseToPath(cx, cy, rx, ry);
        } else {
            String points = tag.getAttributeValue("points");
            return polygonToPath(points);
        }
    }

    private static int getValue(XmlTag tag, String key) {
        String attrValue = tag.getAttributeValue(key);
        double value = safeGetValue(attrValue);
        return (int) Math.round(value);
    }

    private static double getValueF(XmlTag tag, String key) {
        String attrValue = tag.getAttributeValue(key);
        return safeGetValue(attrValue);
    }

    private static double safeGetValue(String valueString) {
        if (valueString == null || valueString.isEmpty()) {
            return 0;
        } else {
            return Double.valueOf(valueString);
        }
    }

    public static void main(String[] args) {
        //test case
        System.out.println(safeGetValue(null));
        System.out.println(safeGetValue(""));
        System.out.println(safeGetValue("1"));
        System.out.println(Math.round(safeGetValue("1.1")));
        System.out.println(Math.round(safeGetValue("1.51")));
    }
}
