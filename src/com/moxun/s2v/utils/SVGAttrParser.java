package com.moxun.s2v.utils;

import com.intellij.psi.xml.XmlTag;

import java.util.regex.Pattern;
import java.util.Locale;

/**
 * Created by moxun on 15/12/16.
 */
public class SVGAttrParser {
    public static String rectToPath(double x, double y, double width, double height, double rx, double ry) {
        StringBuilder sb = new StringBuilder();
        if (rx == 0 && ry == 0) {
            sb.append("M").append(x).append(",").append(y).append("L").append(toFixed(x + width))
                    .append(",").append(toFixed(y)).append("L").append(toFixed(x + width)).append(",")
                    .append(toFixed(y + height)).append("L").append(toFixed(x)).append(",").append(toFixed(y + height))
                    .append("z");
        } else {
            sb.append("M").append(toFixed(x + rx)).append(",").append(toFixed(y)).append(",")
                    .append("L").append(toFixed(x + width - rx)).append(",").append(toFixed(y)).append(",")
                    .append("Q").append(toFixed(x + width)).append(",").append(toFixed(y)).append(",").append(toFixed(x + width)).append(",").append(toFixed(y + ry)).append(",")
                    .append("L").append(toFixed(x + width)).append(",").append(toFixed(y + height - ry)).append(",")
                    .append("Q").append(toFixed(x + width)).append(",").append(toFixed(y + height)).append(",").append(toFixed(x + width - rx)).append(",").append(toFixed(y + height)).append(",")
                    .append("L").append(toFixed(x + rx)).append(",").append(toFixed(y + height)).append(",")
                    .append("Q").append(toFixed(x)).append(",").append(toFixed(y + height)).append(",").append(toFixed(x)).append(",").append(toFixed(y + height - ry)).append(",")
                    .append("L").append(toFixed(x)).append(",").append(toFixed(y + ry)).append(",")
                    .append("Q").append(toFixed(x)).append(",").append(toFixed(y)).append(",").append(toFixed(x + rx)).append(",").append(toFixed(y)).append("z");
        }
        return sb.toString().replaceAll("\\.0,", ",");
    }

    public static String circleToPath(double cx, double cy, double r) {
        StringBuilder sb = new StringBuilder();
        sb.append("M").append(toFixed(cx - r)).append(",").append(toFixed(cy)).append("a").append(toFixed(r)).append(",").append(toFixed(r))
                .append(" 0 0,1 ").append(toFixed(r * 2)).append(",0").append("a").append(toFixed(r)).append(",").append(toFixed(r))
                .append(" 0 0,1 -").append(toFixed(r * 2)).append(",0z");
        return sb.toString().replaceAll("\\.0,", ",");
    }

    public static String polygonToPath(String points) {
        String[] temp = points.split("\\s+");
        if (temp != null && temp.length > 0 && temp.length % 2 == 0) {
            String result = "";
            for (int i = 0; i < temp.length; i++) {
                if (i % 2 == 0){
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

        StringBuilder sb = new StringBuilder();
        sb.append("M").append(toFixed(cx)).append(",").append(toFixed(cy - ry)).append(",")
                .append("C").append(toFixed(cx + ctlX)).append(",").append(toFixed(cy - ry)).append(",").append(toFixed(cx + rx)).append(",").append(toFixed(cy - ctlY)).append(",").append(toFixed(cx + rx)).append(",").append(toFixed(cy)).append(",")
                .append("C").append(toFixed(cx + rx)).append(",").append(toFixed(cy + ctlY)).append(",").append(toFixed(cx + ctlX)).append(",").append(toFixed(cy + ry)).append(",").append(toFixed(cx)).append(",").append(toFixed(cy + ry)).append(",")
                .append("C").append(toFixed(cx - ctlX)).append(",").append(toFixed(cy + ry)).append(",").append(toFixed(cx - rx)).append(",").append(toFixed(cy + ctlY)).append(",").append(toFixed(cx - rx)).append(",").append(toFixed(cy)).append(",")
                .append("C").append(toFixed(cx - rx)).append(",").append(toFixed(cy - ctlY)).append(",").append(toFixed(cx - ctlX)).append(",").append(toFixed(cy - ry)).append(",").append(toFixed(cx)).append(",").append(toFixed(cy - ry)).append("z");
        return sb.toString().replaceAll("\\.0,", ",");
    }

    private static String toFixed(double d) {
        return String.format(Locale.ROOT, "%.2f", d).replaceAll("\\.00$", "");
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
        System.out.println(polygonToPath("0 0 0 100 100 100 100 0 0 0"));
        System.out.println(polygonToPath(" 60,20  100,40 100,80 60,100 20,80 20,40 "));
        System.out.println(ellipseToPath(20, 16, 20, 16));
        System.out.println(circleToPath(16.852, 7.376, 5));
        System.out.println(rectToPath(10, 10.5, 100, 100, 15, 15));

        System.out.println(safeGetValue(null));
        System.out.println(safeGetValue(""));
        System.out.println(safeGetValue("1"));
        System.out.println(Math.round(safeGetValue("1.1")));
        System.out.println(Math.round(safeGetValue("1.51")));
    }
}
