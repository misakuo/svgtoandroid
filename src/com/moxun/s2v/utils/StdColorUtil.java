package com.moxun.s2v.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by moxun on 15/12/15.
 */
public class StdColorUtil {
    private static Map<String,String> colors = new HashMap<String, String>();
    static {
        colors.put("green","#008000");
        colors.put("greenyellow","#ADFF2F");
        colors.put("grey","#808080");
        colors.put("honeydew","#F0FFF0");
        colors.put("hotpink","#FF69B4");
        colors.put("indianred","#CD5C5C");
        colors.put("indigo","#4B0082");
        colors.put("ivory","#FFFFF0");
        colors.put("khaki","#F0E68C");
        colors.put("lavender","#E6E6FA");
        colors.put("lavenderblush","#FFF0F5");
        colors.put("lawngreen","#7CFC00");
        colors.put("lemonchiffon","#FFFACD");
        colors.put("lightblue","#ADD8E6");
        colors.put("lightcoral","#F08080");
        colors.put("lightcyan","#E0FFFF");
        colors.put("lightgoldenrodyellow","#FAFAD2");
        colors.put("lightgray","#D3D3D3");
        colors.put("lightgreen","#90EE90");
        colors.put("lightgrey","#D3D3D3");
        colors.put("lightpink","#FFB6C1");
        colors.put("lightsalmon","#FFA07A");
        colors.put("lightseagreen","#20B2AA");
        colors.put("lightskyblue","#87CEFA");
        colors.put("lightslategray","#778899");
        colors.put("lightslategrey","#778899");
        colors.put("lightsteelblue","#B0C4DE");
        colors.put("lightyellow","#FFFFE0");
        colors.put("lime","#00FF00");
        colors.put("limegreen","#32CD32");
        colors.put("linen","#FAF0E6");
        colors.put("magenta","#FF00FF");
        colors.put("maroon","#800000");
        colors.put("mediumaquamarine","#66CDAA");
        colors.put("mediumblue","#0000CD");
        colors.put("mediumorchid","#BA55D3");
        colors.put("mediumpurple","#9370DB");
        colors.put("mediumseagreen","#3CB371");
        colors.put("mediumslateblue","#7B68EE");
        colors.put("mediumspringgreen","#00FA9A");
        colors.put("mediumturquoise","#48D1CC");
        colors.put("mediumvioletred","#C71585");
        colors.put("midnightblue","#191970");
        colors.put("mintcream","#F5FFFA");
        colors.put("mistyrose","#FFE4E1");
        colors.put("moccasin","#FFE4B5");
        colors.put("navajowhite","#FFDEAD");
        colors.put("navy","#000080");
        colors.put("oldlace","#FDF5E6");
        colors.put("olive","#808000");
        colors.put("olivedrab","#6B8E23");
        colors.put("orange","#FFA500");
        colors.put("orangered","#FF4500");
        colors.put("orchid","#DA70D6");
        colors.put("palegoldenrod","#EEE8AA");
        colors.put("palegreen","#98FB98");
        colors.put("paleturquoise","#AFEEEE");
        colors.put("palevioletred","#DB7093");
        colors.put("papayawhip","#FFEFD5");
        colors.put("peachpuff","#FFDAB9");
        colors.put("peru","#CD853F");
        colors.put("pink","#FFC0CB");
        colors.put("plum","#DDA0DD");
        colors.put("powderblue","#B0E0E6");
        colors.put("purple","#800080");
        colors.put("red","#FF0000");
        colors.put("rosybrown","#BC8F8F");
        colors.put("royalblue","#4169E1");
        colors.put("saddlebrown","#8B4513");
        colors.put("salmon","#FA8072");
        colors.put("sandybrown","#F4A460");
        colors.put("seagreen","#2E8B57");
        colors.put("seashell","#FFF5EE");
        colors.put("sienna","#A0522D");
        colors.put("silver","#C0C0C0");
        colors.put("skyblue","#87CEEB");
        colors.put("slateblue","#6A5ACD");
        colors.put("slategray","#708090");
        colors.put("slategrey","#708090");
        colors.put("snow","#FFFAFA");
        colors.put("springgreen","#00FF7F");
        colors.put("steelblue","#4682B4");
        colors.put("tan","#D2B48C");
        colors.put("teal","#008080");
        colors.put("thistle","#D8BFD8");
        colors.put("tomato","#FF6347");
        colors.put("turquoise","#40E0D0");
        colors.put("violet","#EE82EE");
        colors.put("wheat","#F5DEB3");
        colors.put("white","#FFFFFF");
        colors.put("whitesmoke","#F5F5F5");
        colors.put("yellow","#FFFF00");
        colors.put("yellowgreen","#9ACD32");
        colors.put("none","#00000000");
    }

    private static String getColorRGB(String colorName) {
        String result = colors.get(colorName);
        if (result == null) {
            result = "#000000";
        }
        return result;
    }

    public static String formatColor(String color) {
        Pattern RRGGBB = Pattern.compile("#[0-9A-Fa-f]{6}");
        Pattern RGB = Pattern.compile("#[0-9A-Fa-f]{3}");
        Pattern rgb = Pattern.compile("[rgb(d+,d+,d+)]");
        Pattern rgb2 = Pattern.compile("[rgb(d+%,d+%,d+%)]");

        if (RRGGBB.matcher(color).find()) {
            return color.toUpperCase();
        } else if (RGB.matcher(color).find()) {
            String r = "#" + color.charAt(1) + color.charAt(1) + color.charAt(2) + color.charAt(2) + color.charAt(3) + color.charAt(3);
            return r.toUpperCase();
        } else if (rgb2.matcher(color).find() && color.contains("%")) {
            String[] rgbCls = color.trim().replace("rgb(", "").replace(")","").replace("%","").split(",");
            String r = "#" + percent2HEX(rgbCls[0]) +  percent2HEX(rgbCls[1]) + percent2HEX(rgbCls[2]);
            return r.toUpperCase();
        } else if (rgb.matcher(color).find() && color.contains("rgb")) {
            String[] rgbCls = color.trim().replace("rgb(","").replace(")","").split(",");
            String r = "#" + DEC2HEX(rgbCls[0]) +  DEC2HEX(rgbCls[1]) + DEC2HEX(rgbCls[2]);
            return r.toUpperCase();
        } else {
            return getColorRGB(color);
        }
    }

    private static String DEC2HEX(String dec) {
        String r = Integer.toHexString(Integer.valueOf(dec));
        if (r.length() == 1) {
            r = "0" + r;
        }
        return r;
    }

    private static String percent2HEX(String percent) {
        String r =  Integer.toHexString(Integer.valueOf(percent) * 255 / 100);
        if (r.length() == 1) {
            r = "0" + r;
        }
        return r;
    }

    public static void main(String[] args) {
        System.out.println(formatColor("#abcdef"));
        System.out.println(formatColor("#abc"));
        System.out.println(formatColor("rgb(255,15,5)"));
        System.out.println(formatColor("rgb(100%,0%,0%)"));
        System.out.println(formatColor("blue"));
    }
}
