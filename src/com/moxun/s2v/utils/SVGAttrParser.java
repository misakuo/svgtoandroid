package com.moxun.s2v.utils;

import com.intellij.psi.xml.XmlTag;

import java.util.regex.Pattern;

/**
 * Created by moxun on 15/12/16.
 */
public class SVGAttrParser {
    public static String rectToPath(int x, int y, int width, int height, double rx, double ry) {
        StringBuilder sb = new StringBuilder();
        if (rx == 0 && ry == 0) {
            sb.append("M").append(x).append(",").append(y).append("L").append(x + width)
                    .append(",").append(y).append("L").append(x + width).append(",")
                    .append(y + height).append("L").append(x).append(",").append(y + height)
                    .append("z");
        } else {
            sb.append("M").append(x + rx).append(",").append(y).append(",")
                    .append("L").append(x + width - rx).append(",").append(y).append(",")
                    .append("Q").append(x + width).append(",").append(y).append(",").append(x + width).append(",").append(y + ry).append(",")
                    .append("L").append(x + width).append(",").append(y + height - ry).append(",")
                    .append("Q").append(x + width).append(",").append(y + height).append(",").append(x + width - rx).append(",").append(y + height).append(",")
                    .append("L").append(x + rx).append(",").append(y + height).append(",")
                    .append("Q").append(x).append(",").append(y + height).append(",").append(x).append(",").append(y + height - ry).append(",")
                    .append("L").append(x).append(",").append(y + ry).append(",")
                    .append("Q").append(x).append(",").append(y).append(",").append(x + rx).append(",").append(y).append("z");
        }
        return sb.toString().replaceAll("\\.0,",",");
    }

    public static String circleToPath(double cx, double cy, double r) {
        StringBuilder sb = new StringBuilder();
        sb.append("M").append(cx - r).append(",").append(cy).append("a").append(r).append(",").append(r)
                .append(" 0 0,1 ").append(r * 2).append(",0").append("a").append(r).append(",").append(r)
                .append(" 0 0,1 -").append(r * 2).append(",0z");
        return sb.toString().replaceAll("\\.0,",",");
    }

    public static String polygonToPath(String points) {
        String r = "M" + points.trim().replaceAll("\\s+", "L");
        if (Pattern.compile("[L$]").matcher(r).find()) {
            r = r.substring(0, r.length() - 1);
        }
        r += "z";
        return r;
    }

    public static String ellipseToPath(double cx, double cy, double rx, double ry) {
        double ctlX = rx * 0.5522847498307935;
        double ctlY = ry * 0.5522847498307935;

        StringBuilder sb = new StringBuilder();
        sb.append("M").append(cx).append(",").append(cy - ry).append(",")
                .append("C").append(toFixed(cx + ctlX)).append(",").append(cy - ry).append(",").append(cx+rx).append(",").append(toFixed(cy - ctlY)).append(",").append(cx + rx).append(",").append(cy).append(",")
                .append("C").append(cx+rx).append(",").append(toFixed(cy + ctlY)).append(",").append(toFixed(cx + ctlX)).append(",").append(cy+ry).append(",").append(cx).append(",").append(cy + ry).append(",")
                .append("C").append(toFixed(cx - ctlX)).append(",").append(cy+ry).append(",").append(cx - rx).append(",").append(toFixed(cy + ctlY)).append(",").append(cx - rx).append(",").append(cy).append(",")
                .append("C").append(cx - rx).append(",").append(toFixed(cy - ctlY)).append(",").append(toFixed(cx - ctlX)).append(",").append(cy - ry).append(",").append(cx).append(",").append(cy - ry).append("z");
        return sb.toString().replaceAll("\\.0,",",");
    }

    private static String toFixed(double d) {
        return String.format("%.2f", d);
    }

    public static String getPathData(XmlTag tag) {
        String type = tag.getName();
        if (type.equals("path")) {
            return tag.getAttributeValue("d");
        } else if (type.equals("rect")) {
            int x = getValue(tag,"x");
            int y = getValue(tag,"y");
            int width = getValue(tag,"width");
            int height = getValue(tag,"height");
            double rx = getValueF(tag,"rx");
            double ry = getValueF(tag,"ry");
            if (ry == 0) {
                ry = rx;
            } else if (rx == 0){
                rx = ry;
            }
            return rectToPath(x,y,width,height,rx,ry);
        } else if (type.equals("circle")) {
            double cx = getValueF(tag,"cx");
            double cy = getValueF(tag,"cy");
            double r = getValueF(tag,"r");
            return circleToPath(cx,cy,r);
        } else if (type.equals("ellipse")) {
            double cx = getValueF(tag,"cx");
            double cy = getValueF(tag,"cy");
            double rx = getValueF(tag,"rx");
            double ry = getValueF(tag,"ry");
            return ellipseToPath(cx,cy,rx,ry);
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
        System.out.println(polygonToPath(" 60,20  100,40 100,80 60,100 20,80 20,40 "));
        System.out.println(ellipseToPath(20, 16, 20, 16));
        System.out.println(circleToPath(16.852, 7.376, 5));
        System.out.println(rectToPath(10,10,100,100,15,15));

        System.out.println(safeGetValue(null));
        System.out.println(safeGetValue(""));
        System.out.println(safeGetValue("1"));
        System.out.println(Math.round(safeGetValue("1.1")));
        System.out.println(Math.round(safeGetValue("1.51")));
    }
}
