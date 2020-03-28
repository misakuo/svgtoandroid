package com.moxun.s2v.utils;

public class SVGPathBuilder {

    private final StringBuilder sb = new StringBuilder();

    public SVGPathBuilder moveto(double x, double y) {
        return cmd("M").xy(x, y);
    }

    public SVGPathBuilder lineto(double x, double y) {
        return cmd("L").xy(x, y);
    }

    public SVGPathBuilder quadto(double x1, double y1, double x, double y) {
        return cmd("Q").xy(x1, y1).xy(x, y);
    }

    public SVGPathBuilder cubicto(double x1, double y1, double x2, double y2, double x, double y) {
        return cmd("C").xy(x1, y1).xy(x2, y2).xy(x, y);
    }

    public SVGPathBuilder arcrel(double rx, double ry, double xAxisRotation, boolean largeArc, boolean sweep, double x, double y) {
        return cmd("a").xy(rx, ry).num(xAxisRotation).flag(largeArc).flag(sweep).xy(x, y);
    }

    public SVGPathBuilder closepath() {
        return cmd("z");
    }

    public String build() {
        return sb.toString().trim();
    }

    private SVGPathBuilder cmd(String cmd) {
        return rawStr(cmd).rawStr(" ");
    }

    private SVGPathBuilder xy(double x, double y) {
        return rawNum(x).rawStr(",").num(y);
    }

    private SVGPathBuilder num(double num) {
        return rawNum(num).rawStr(" ");
    }

    private SVGPathBuilder flag(boolean flag) {
        return rawStr(flag ? "1" : "0").rawStr(" ");
    }

    private SVGPathBuilder rawNum(double num) {
        double twoPlaces = Math.round(num * 100.0) / 100.0;
        sb.append(Double.toString(twoPlaces).replaceAll("\\.0$", ""));
        return this;
    }

    private SVGPathBuilder rawStr(String str) {
        sb.append(str);
        return this;
    }
}
