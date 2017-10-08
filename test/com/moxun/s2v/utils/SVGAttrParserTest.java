package com.moxun.s2v.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SVGAttrParserTest {

    @Test
    public void rectToPath() {
        assertThat(SVGAttrParser.rectToPath(10, 10.5, 100, 100, 0, 0),
                is("M10,10.5L110,10.50L110,110.50L10,110.50z"));

        assertThat(SVGAttrParser.rectToPath(10, 10.5, 100, 100, 15, 15), is(""
                + "M25,10.50,"
                + "L95,10.50,Q110,10.50,110,25.50,"
                + "L110,95.50,Q110,110.50,95,110.50,"
                + "L25,110.50,Q10,110.50,10,95.50,"
                + "L10,25.50,Q10,10.50,25,10.50"
                + "z"));
    }

    @Test
    public void circleToPath() {
        assertThat(SVGAttrParser.circleToPath(16.852, 7.376, 5), is(""
                + "M11.85,7.38"
                + "a5,5 0 0,1 10,0"
                + "a5,5 0 0,1 -10,0"
                + "z"));
    }

    @Test
    public void polygonToPath() {
        assertThat(SVGAttrParser.polygonToPath("0 0 0 100 100 100 100 0 0 0"),
                is("M0,0 L0,100 L100,100 L100,0 L0,0z"));

        assertThat(SVGAttrParser.polygonToPath(" 60,20  100,40 100,80 60,100 20,80 20,40 "),
                is("M60,20 L100,40 L100,80 L60,100 L20,80 L20,40z"));
    }

    @Test
    public void ellipseToPath() {
        assertThat(SVGAttrParser.ellipseToPath(20, 16, 20, 16), is(""
                + "M20,0,"
                + "C31.05,0,40,7.16,40,16,"
                + "C40,24.84,31.05,32,20,32,"
                + "C8.95,32,0,24.84,0,16,"
                + "C0,7.16,8.95,0,20,0"
                + "z"));
    }
}
