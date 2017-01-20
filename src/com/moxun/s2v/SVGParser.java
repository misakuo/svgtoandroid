package com.moxun.s2v;

import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.moxun.s2v.utils.*;

import java.util.*;

/**
 * Created by moxun on 15/12/15.
 */
public class SVGParser {
    private XmlFile svg;
    private String dpi;
    private String opacity;
    private int width, height, viewportWidth, viewportHeight;
    private Map<String, String> styles;

    public SVGParser(XmlFile svg, String dpi) {
        styles = new HashMap<String, String>();
        this.svg = svg;
        this.dpi = dpi;
        parseDimensions();

        XmlDocument document = svg.getDocument();
        if (document != null) {
            XmlTag rootTag = document.getRootTag();
            if (rootTag != null) {
                XmlTag[] subTags = rootTag.getSubTags();
                for (XmlTag tag : subTags) {
                    getChildAttrs(tag);
                }
            }
        }
    }

    private void parseDimensions() {
        try {
            if (getValueFromRootTag("width") != null) {
                width = getNumber(getValueFromRootTag("width"));
            } else if (getValueFromRootTag("viewBox") != null) {
                width = getNumber(getValueFromRootTag("viewBox").split(" ")[2]);
            }

            if (getValueFromRootTag("height") != null) {
                height = getNumber(getValueFromRootTag("height"));
            } else if (getValueFromRootTag("viewBox") != null) {
                height = getNumber(getValueFromRootTag("viewBox").split(" ")[3]);
            }

            if (getValueFromRootTag("viewBox") != null) {
                viewportWidth = getNumber(getValueFromRootTag("viewBox").split(" ")[2]);
                viewportHeight = getNumber(getValueFromRootTag("viewBox").split(" ")[3]);
            } else {
                viewportWidth = width;
                viewportHeight = height;
            }

            if (getValueFromRootTag("opacity") != null) {
                opacity = getValueFromRootTag("opacity");
            }
        } catch (Exception e) {
            //do nothing, maybe null pointer exception, don't care
        }
    }

    private String getValueFromRootTag(String key) {
        if (svg.getDocument() != null) {
            XmlTag rootTag = svg.getDocument().getRootTag();
            if (rootTag != null) {
                XmlAttribute attribute = rootTag.getAttribute(key);
                if (attribute != null) {
                    return rootTag.getAttributeValue(key);
                }
            }
        }
        return null;
    }

    public List<XmlTag> getGroups() {
        List<XmlTag> groups = new ArrayList<XmlTag>();
        if (svg.getDocument() != null) {
            XmlTag rootTag = svg.getDocument().getRootTag();
            if (rootTag != null) {
                for (XmlTag tag : rootTag.getSubTags()) {
                    if (tag.getName().equals("g")) {
                        groups.add(trim(tag, null));
                    }
                }
            }
        }
        return groups;
    }

    public List<XmlTag> getGroupChildes(String groupName) {
        List<XmlTag> childes = new ArrayList<XmlTag>();
        if (svg.getDocument() != null) {
            XmlTag rootTag = svg.getDocument().getRootTag();
            if (rootTag != null) {
                for (XmlTag tag : rootTag.getSubTags()) {
                    if (tag.getName().equals(groupName)) {
                        Collections.addAll(childes, tag.getSubTags());
                    }
                }
            }
        }
        return childes;
    }

    public List<XmlTag> getSubGroups(XmlTag parent) {
        List<XmlTag> list = new ArrayList<XmlTag>();
        for (XmlTag tag : parent.getSubTags()) {
            if (tag.getName().equals("g")) {
                list.add(tag);
            }
        }
        return list;
    }

    public List<XmlTag> getSVGChildes() {
        List<XmlTag> childes = new ArrayList<XmlTag>();
        if (svg.getDocument() != null) {
            XmlTag rootTag = svg.getDocument().getRootTag();
            if (rootTag != null) {
                Collections.addAll(childes, rootTag.getSubTags());
            }
        }
        return childes;
    }

    public XmlTag getStyles() {
        List<XmlTag> childes = getSVGChildes();
        for (XmlTag tag : childes) {
            if ("defs".equals(tag.getName()) && tag.getSubTags() != null) {
                for (XmlTag subTag : tag.getSubTags()) {
                    if ("style".equals(subTag.getName())) {
                        return subTag;
                    }
                }
            }
        }
        return null;
    }

    public List<XmlTag> getShapeTags(XmlTag parentTag) {
        List<XmlTag> tags = new ArrayList<XmlTag>();
        XmlTag[] subTags = parentTag.getSubTags();
        for (XmlTag tag : subTags) {
            if (AttrMapper.isShapeName(tag.getName())) {
                tags.add(tag);
            }
        }
        Logger.debug("shapeTag of " + parentTag.getName() + " :" + tags.toString());
        return tags;
    }

    public boolean hasGroupTag() {
        return getGroups().size() > 0;
    }

    public Map<String, String> getChildAttrs(XmlTag tag) {
        Map<String, String> styles = new HashMap<String, String>();
        XmlAttribute attrs[] = tag.getAttributes();
        for (XmlAttribute attr : attrs) {
            if (attr.getName().equals("style")) {
                String stylesValue = tag.getAttributeValue("style").replaceAll("\\s*", "");
                String[] list = stylesValue.split(";");
                for (String s : list) {
                    String[] item = s.split(":");
                    if (item[0].equals("fill")) {
                        item[1] = StdColorUtil.formatColor(item[1]);
                    }
                    styles.put(item[0], item[1]);
                }
            } else {
                if (attr.getName().equals("fill")) {
                    styles.put(attr.getName(), StdColorUtil.formatColor(tag.getAttributeValue(attr.getName())));
                } else {
                    styles.put(attr.getName(), tag.getAttributeValue(attr.getName()));
                }
            }
        }
        Logger.debug(tag.getName() + styles.toString());
        return styles;
    }

    public XmlTag trim(XmlTag rootTag, List<XmlAttribute> attr) {
        Logger.debug("Current tag: " + rootTag.getName());
        CommonUtil.dumpAttrs("current attr", Arrays.asList(rootTag.getAttributes()));
        CommonUtil.dumpAttrs("parent attr", attr);
        if (attr == null) {
            attr = new ArrayList<XmlAttribute>();
            attr.addAll(Arrays.asList(rootTag.getAttributes()));
        }
        XmlTag[] subTags = rootTag.getSubTags();
        List<XmlAttribute> attrs = new ArrayList<XmlAttribute>();
        if (subTags.length == 1 && subTags[0].getName().equals("g")) {
            Logger.debug("Tag" + rootTag + " has only a subTag and the tag is 'g'");
            Collections.addAll(attrs, subTags[0].getAttributes());
            attrs.addAll(attr);
            rootTag = trim(subTags[0], attrs);
        } else if (subTags.length > 0 && AttrMapper.isShapeName(subTags[0].getName())) {
            Logger.debug(rootTag.getSubTags()[0].getName());
            Logger.debug("Tag" + rootTag + " is correct tag.");
            for (XmlAttribute attribute : attr) {
                Logger.debug(attribute.getName() + ":" + attribute.getValue());
            }
            return AttrMergeUtil.mergeAttrs((XmlTag) rootTag.copy(), reduceAttrs(attr));
        }
        return rootTag;
    }

    //only focusing attr id & transform & fill
    private Map<String, String> reduceAttrs(List<XmlAttribute> attributes) {
        CommonUtil.dumpAttrs("Will reduce", attributes);
        String reducedTranslate = "";
        Map<String, String> result = new HashMap<String, String>();
        for (XmlAttribute attr : attributes) {
            if (attr.getName().equals("transform")) {
                reducedTranslate = TranslateReduceUtil.reduce(reducedTranslate, attr.getValue());
            } else {
                if (!result.containsKey(attr.getName())) {
                    result.put(attr.getName(), attr.getValue());
                }
            }
        }

        result.put("transform", reducedTranslate);
        Logger.debug("Reduced Attrs:" + result.toString());
        return result;
    }

    private int getNumber(String src) {
        String result = src.split("\\D+")[0];
        return Integer.valueOf(result);
    }

    public String getWidth() {
        return DensityUtil.px2dp(width, dpi) + "dp";
    }

    public String getHeight() {
        return DensityUtil.px2dp(height, dpi) + "dp";
    }

    public String getViewportWidth() {
        return String.valueOf(viewportWidth);
    }

    public String getViewportHeight() {
        return String.valueOf(viewportHeight);
    }

    public String getAlpha() {
        return opacity;
    }


    @Override
    public String toString() {
        return "SVGParser{" +
                "svg=" + svg +
                ", dpi='" + dpi + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", viewportWidth=" + viewportWidth +
                ", viewportHeight=" + viewportHeight +
                ", styles=" + styles +
                '}';
    }
}
