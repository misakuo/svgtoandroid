package com.moxun.s2v.utils;

import com.intellij.psi.xml.XmlTag;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.format.CSSFormat;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moxun on 17/1/19.
 */
public class StyleParser {
    private XmlTag style;
    private CSSStyleSheet styleSheet;
    private CSSFormat cssFormat;
    private Map<String, CSSStyleRuleImpl> styleRuleMap = new HashMap<String, CSSStyleRuleImpl>();

    public StyleParser(XmlTag style) {
        this.style = style;
        init();
    }

    private void init() {
        if (style != null) {
            String styleContent = style.getValue().getText();
            if (styleContent != null && !styleContent.isEmpty()) {
                InputSource source = new InputSource(new StringReader(styleContent));
                CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
                parser.setErrorHandler(new ParserErrorHandler());
                try {
                    styleSheet = parser.parseStyleSheet(source, null, null);
                    cssFormat = new CSSFormat().setRgbAsHex(true);

                    CSSRuleList rules = styleSheet.getCssRules();
                    for (int i = 0; i < rules.getLength(); i++) {
                        final CSSRule rule = rules.item(i);
                        if (rule instanceof CSSStyleRuleImpl) {
                            styleRuleMap.put(((CSSStyleRuleImpl) rule).getSelectorText(), (CSSStyleRuleImpl) rule);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getFillColor(String cssClass) {
        //class selector
        String id = "." + cssClass;
        CSSStyleRuleImpl rule = styleRuleMap.get(id);
        if (rule != null) {
            CSSValueImpl cssValue = (CSSValueImpl) rule.getStyle().getPropertyCSSValue("fill");
            return cssValue.getCssText(cssFormat).toUpperCase();
        }
        return null;
    }

    private class ParserErrorHandler implements ErrorHandler {

        @Override
        public void warning(CSSParseException e) throws CSSException {
            Logger.warn(e.toString());
        }

        @Override
        public void error(CSSParseException e) throws CSSException {
            Logger.error(e.toString());
        }

        @Override
        public void fatalError(CSSParseException e) throws CSSException {
            Logger.error("Fatal Error: " + e.toString());
        }
    }
}
